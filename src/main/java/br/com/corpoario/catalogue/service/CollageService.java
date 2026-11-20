package br.com.corpoario.catalogue.service;

import java.util.List;

import br.com.corpoario.catalogue.entity.Collage;
import br.com.corpoario.catalogue.repository.CollageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CollageService {

    @Inject
    CollageRepository repository;

    @Transactional
    public List<Collage> findAll() {
        return repository.findAll().list();
    }
}
