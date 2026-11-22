package br.com.corpoario.catalogue.resource;

import br.com.corpoario.catalogue.dto.ArtistProfileResponse;
import br.com.corpoario.catalogue.exception.ErrorResponse;
import br.com.corpoario.catalogue.service.ArtistProfileService;

import jakarta.inject.Inject;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/artist-profile")
@Produces(MediaType.APPLICATION_JSON)
public class ArtistProfileResource {

    @Inject
    ArtistProfileService service;

    @GET
    public Response getCurrent() {
        return service.findCurrent()
            .map(profile -> Response.ok(new ArtistProfileResponse(
                profile.getBio(), profile.getPhotoUrl(), profile.getEmail())).build())
            .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(404, "Artist profile not found", "No artist profile was found"))
                .build());
    }
}
