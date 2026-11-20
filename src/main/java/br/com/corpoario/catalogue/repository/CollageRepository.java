package br.com.corpoario.catalogue.repository;

import java.util.UUID;

import br.com.corpoario.catalogue.entity.Collage;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

@ApplicationScoped
public class CollageRepository implements PanacheRepositoryBase<Collage, UUID> {
}
