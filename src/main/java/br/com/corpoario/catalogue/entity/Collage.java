package br.com.corpoario.catalogue.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table
public class Collage extends Artwork {

    @NotBlank(message = "Image URL cannot be blank")
    @Column(nullable = false, updatable = true)
    private String imageUrl;

    public Collage() {}

    public Collage(String title, BigDecimal price, String imageUrl) {
        setTitle(title);
        setPrice(price);
        this.imageUrl = imageUrl;
    }

    // Defining methods' contracts,
    // however implementation will be dealt with later
    @Override
    public void getDetails() {}

    public void update() {}

    public void delete() {}
}
