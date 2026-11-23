package br.com.corpoario.catalogue.repository;

import java.util.UUID;

import br.com.corpoario.catalogue.entity.ArtistProfileHistory;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

@ApplicationScoped
public class ArtistProfileHistoryRepository implements PanacheRepositoryBase<ArtistProfileHistory, UUID> {
}
