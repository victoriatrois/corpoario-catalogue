package br.com.corpoario.catalogue.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CollageResponse(
    UUID id,
    String title,
    BigDecimal price,
    String imageUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
