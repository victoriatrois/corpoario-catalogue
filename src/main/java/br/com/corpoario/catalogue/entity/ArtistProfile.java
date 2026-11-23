package br.com.corpoario.catalogue.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "artist_profile")
public class ArtistProfile {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "The bio cannot be blank")
    @Column(nullable = false)
    private String bio;

    @NotBlank(message = "The photo URL cannot be blank")
    @Column(name = "photo_url", nullable = false, updatable = true)
    private String photoUrl;

    @NotBlank(message = "The email address cannot be blank")
    @Column(nullable = false)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ArtistProfile() {

    }

    public ArtistProfile(
        @NotBlank(message = "The bio cannot be blank") String bio,
        @NotBlank(message = "The photo URL cannot be blank") String photoUrl,
        @NotBlank(message = "The email address cannot be blank") String email) {
            this.bio = bio;
            this.photoUrl = photoUrl;
            this.email = email;
    }

    public UUID getId() {
        return id;
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

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
