package br.com.corpoario.catalogue.resource;

import java.util.List;

import br.com.corpoario.catalogue.entity.Collage;
import br.com.corpoario.catalogue.service.CollageService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/collages")
@Produces(MediaType.APPLICATION_JSON)
public class CollageResource {

    @Inject
    CollageService service;

    @GET
    public List<Collage> getAll() {
        return service.findAll();
    }
}
