package br.com.corpoario.catalogue.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "artist_profile_history")
public class ArtistProfileHistory {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "artist_profile_id", nullable = false, updatable = false)
    private UUID artistProfileId;

    @Column(nullable = false, updatable = false, columnDefinition = "TEXT")
    private String bio;

    @Column(name = "photo_url", nullable = false, updatable = false)
    private String photoUrl;

    @Column(nullable = false, updatable = false)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "archived_at", nullable = false, updatable = false)
    private LocalDateTime archivedAt;

    protected ArtistProfileHistory() {
    }

    public ArtistProfileHistory(ArtistProfile profile) {
        this.artistProfileId = Objects.requireNonNull(profile.getId(), "Profile must be persisted");
        this.bio = profile.getBio();
        this.photoUrl = profile.getPhotoUrl();
        this.email = profile.getEmail();
        this.createdAt = Objects.requireNonNull(profile.getCreatedAt(), "Profile must be persisted");
        this.updatedAt = Objects.requireNonNull(profile.getUpdatedAt(), "Profile must be persisted");
    }

    public UUID getId() {
        return id;
    }

    public UUID getArtistProfileId() {
        return artistProfileId;
    }

    public String getBio() {
        return bio;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.archivedAt = LocalDateTime.now();
    }
}
