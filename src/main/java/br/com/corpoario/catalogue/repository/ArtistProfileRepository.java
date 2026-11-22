package br.com.corpoario.catalogue.repository;

import java.util.UUID;

import br.com.corpoario.catalogue.entity.ArtistProfile;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

@ApplicationScoped
public class ArtistProfileRepository implements PanacheRepositoryBase<ArtistProfile, UUID> {
}
