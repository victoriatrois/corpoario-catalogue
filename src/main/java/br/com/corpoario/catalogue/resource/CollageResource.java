package br.com.corpoario.catalogue.resource;

import java.util.List;
import java.util.UUID;

import br.com.corpoario.catalogue.entity.Collage;
import br.com.corpoario.catalogue.exception.CollageNotFoundException;
import br.com.corpoario.catalogue.exception.InvalidCollageIdException;
import br.com.corpoario.catalogue.service.CollageService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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

    @Path("/{id}")
    @GET
    public Collage findById(@PathParam("id") String id) {
        UUID collageId = parseCollageId(id);

        Collage collage = service.findById(collageId);

        if (collage == null) {
            throw new CollageNotFoundException(collageId);
        }

        return collage;
    }

    private UUID parseCollageId(String id) {
        try {
            UUID collageId = UUID.fromString(id);

            if (!collageId.toString().equalsIgnoreCase(id)) {
                throw new InvalidCollageIdException();
            }

            return collageId;
        } catch (IllegalArgumentException exception) {
            throw new InvalidCollageIdException();
        }
    }
}
