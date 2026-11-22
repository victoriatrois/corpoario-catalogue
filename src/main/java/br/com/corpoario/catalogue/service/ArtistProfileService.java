package br.com.corpoario.catalogue.service;

import java.util.List;
import java.util.Optional;

import br.com.corpoario.catalogue.entity.ArtistProfile;
import br.com.corpoario.catalogue.repository.ArtistProfileRepository;
import io.quarkus.panache.common.Page;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;

import jakarta.transaction.Transactional;

@ApplicationScoped
public class ArtistProfileService {
    @Inject
    ArtistProfileRepository repository;

    @Transactional
    public Optional<ArtistProfile> findCurrent() {
        List<ArtistProfile> profiles = repository.findAll().page(Page.ofSize(2)).list();
        if (profiles.size() > 1) {
            throw new IllegalStateException("Multiple current artist profiles found");
        }
        return profiles.stream().findFirst();
    }
}
