package br.com.corpoario.catalogue.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.corpoario.catalogue.entity.ArtistProfile;
import br.com.corpoario.catalogue.entity.ArtistProfileHistory;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class ArtistProfilePersistenceTest {
    @Inject
    ArtistProfileRepository profiles;

    @Inject
    ArtistProfileHistoryRepository history;

    @Inject
    DataSource dataSource;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        history.deleteAll();
        profiles.deleteAll();
    }

    @Test
    void shouldPreserveSnapshotsAfterReplacingCurrentProfile() {
        ArtistProfile original = new ArtistProfile("Original bio", "https://example.com/original.jpg", "original@example.com");
        QuarkusTransaction.requiringNew().run(() -> profiles.persist(original));
        ArtistProfile persisted = QuarkusTransaction.requiringNew().call(() -> profiles.findById(original.getId()));

        UUID[] historyIds = QuarkusTransaction.requiringNew().call(() -> {
            ArtistProfile current = profiles.findById(original.getId());
            ArtistProfileHistory first = new ArtistProfileHistory(current);
            ArtistProfileHistory second = new ArtistProfileHistory(current);
            history.persist(first, second);
            profiles.delete(current);
            profiles.flush();
            profiles.persist(new ArtistProfile("New bio", "https://example.com/new.jpg", "new@example.com"));
            return new UUID[] { first.getId(), second.getId() };
        });

        QuarkusTransaction.requiringNew().run(() -> {
            ArtistProfileHistory saved = history.findById(historyIds[0]);
            assertNotNull(saved);
            assertNotEquals(historyIds[0], historyIds[1]);
            assertEquals(2, history.count("artistProfileId", original.getId()));
            assertEquals(original.getId(), saved.getArtistProfileId());
            assertEquals(original.getBio(), saved.getBio());
            assertEquals(original.getPhotoUrl(), saved.getPhotoUrl());
            assertEquals(original.getEmail(), saved.getEmail());
            ArtistProfileHistory another = history.findById(historyIds[1]);
            assertEquals(saved.getArtistProfileId(), another.getArtistProfileId());
            assertEquals(saved.getCreatedAt(), another.getCreatedAt());
            assertEquals(saved.getUpdatedAt(), another.getUpdatedAt());
            assertEquals(persisted.getCreatedAt(), saved.getCreatedAt());
            assertEquals(persisted.getUpdatedAt(), saved.getUpdatedAt());
            assertNotNull(saved.getArchivedAt());
            assertEquals("New bio", profiles.findAll().firstResult().getBio());
        });
    }

    @Test
    void shouldRollBackSnapshotAndProfileChangeTogether() {
        ArtistProfile original = new ArtistProfile("Original bio", "https://example.com/original.jpg", "original@example.com");
        QuarkusTransaction.requiringNew().run(() -> profiles.persist(original));

        assertThrows(IllegalStateException.class, () -> QuarkusTransaction.requiringNew().run(() -> {
            ArtistProfile current = profiles.findById(original.getId());
            history.persist(new ArtistProfileHistory(current));
            profiles.delete(current);
            profiles.flush();
            throw new IllegalStateException("Simulated failure");
        }));

        QuarkusTransaction.requiringNew().run(() -> {
            assertEquals(0, history.count("artistProfileId", original.getId()));
            assertEquals(1, profiles.count());
            assertEquals("Original bio", profiles.findById(original.getId()).getBio());
        });
    }

    @Test
    void shouldAllowAtMostOneConcurrentCurrentProfileInsert() throws Exception {
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<String> first = executor.submit(() -> insertProfile(ready, start));
            Future<String> second = executor.submit(() -> insertProfile(ready, start));
            try {
                assertTrue(ready.await(10, TimeUnit.SECONDS));
            } finally {
                start.countDown();
            }

            String firstResult = first.get(20, TimeUnit.SECONDS);
            String secondResult = second.get(20, TimeUnit.SECONDS);
            assertTrue(
                (firstResult.equals("committed") && secondResult.equals("23505"))
                || (secondResult.equals("committed") && firstResult.equals("23505")),
                () -> "Unexpected outcomes: " + firstResult + ", " + secondResult);
        }

        QuarkusTransaction.requiringNew().run(() -> assertEquals(1, profiles.count()));
    }

    private String insertProfile(CountDownLatch ready, CountDownLatch start) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            ready.countDown();
            if (!start.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Concurrent inserts did not start");
            }

            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO artist_profile (id, bio, photo_url, email, created_at, updated_at)
                    VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                    """)) {
                statement.setQueryTimeout(10);
                statement.setObject(1, UUID.randomUUID());
                statement.setString(2, "Bio");
                statement.setString(3, "https://example.com/photo.jpg");
                statement.setString(4, "artist@example.com");
                statement.executeUpdate();
                connection.commit();
                return "committed";
            } catch (SQLException exception) {
                connection.rollback();
                return exception.getSQLState();
            }
        }
    }
}
