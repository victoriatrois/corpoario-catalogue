package br.com.corpoario;

import org.junit.jupiter.api.Test;

import br.com.corpoario.catalogue.entity.ArtistProfile;
import br.com.corpoario.catalogue.repository.ArtistProfileRepository;

import org.junit.jupiter.api.BeforeEach;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class ArtistProfileResourceTest {
    @Inject
    ArtistProfileRepository repository;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnNotFoundWhenNoArtistDataExist() {
        given()
            .when().get("/artist-profile")
            .then()
            .statusCode(404)
            .contentType("application/json")
            .body("status", equalTo(404))
            .body("error", equalTo("Artist profile not found"))
            .body("message", equalTo("No artist profile was found"));
    }

    @Test
    void shouldReturnArtistProfileWhenProfileExists() {
        ArtistProfile profile = new ArtistProfile(
            "Artist biography",
            "https://example.com/artist.jpg",
            "artist@example.com");

        QuarkusTransaction.requiringNew().run(() -> repository.persist(profile));

        given()
            .when().get("/artist-profile")
            .then()
            .statusCode(200)
            .contentType("application/json")
            .body("keySet()", containsInAnyOrder("bio", "photoUrl", "email"))
            .body("bio", equalTo("Artist biography"))
            .body("photoUrl", equalTo("https://example.com/artist.jpg"))
            .body("email", equalTo("artist@example.com"));
    }

}
