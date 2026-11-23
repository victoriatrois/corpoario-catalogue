package br.com.corpoario.catalogue.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CollageRequest(
    @NotBlank(message = "Title cannot be blank") String title,
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.00", inclusive = true, message = "Price cannot be negative") BigDecimal price,
    @NotBlank(message = "Image URL cannot be blank") String imageUrl) {}
