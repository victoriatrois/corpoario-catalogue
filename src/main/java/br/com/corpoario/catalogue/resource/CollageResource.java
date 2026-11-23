package br.com.corpoario.catalogue.resource;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import br.com.corpoario.catalogue.dto.CollageRequest;
import br.com.corpoario.catalogue.dto.CollageResponse;
import br.com.corpoario.catalogue.entity.Collage;
import br.com.corpoario.catalogue.exception.CollageNotFoundException;
import br.com.corpoario.catalogue.exception.InvalidCollageIdException;
import br.com.corpoario.catalogue.service.CollageService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/collages")
@Produces(MediaType.APPLICATION_JSON)
public class CollageResource {

    @Inject
    CollageService service;

    @GET
    public List<CollageResponse> getAll() {
        return service.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @Path("/{id}")
    @GET
    public CollageResponse findById(@PathParam("id") String id) {
        UUID collageId = parseCollageId(id);

        Collage collage = service.findById(collageId);

        if (collage == null) {
            throw new CollageNotFoundException(collageId);
        }

        return toResponse(collage);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@NotNull(message = "Request body cannot be null") @Valid CollageRequest request) {
        Collage collage = new Collage(
            request.title(),
            request.price(),
            request.imageUrl());

        Collage created = service.create(collage);
        CollageResponse response = toResponse(created);

        return Response.created(URI.create("/collages/" + created.getId()))
            .entity(response)
            .build();
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

    private CollageResponse toResponse(Collage collage) {
        return new CollageResponse(
            collage.getId(),
            collage.getTitle(),
            collage.getPrice(),
            collage.getImageUrl(),
            collage.getCreatedAt(),
            collage.getUpdatedAt());
    }
}
