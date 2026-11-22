package br.com.corpoario.catalogue.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidCollageIdExceptionMapper implements ExceptionMapper<InvalidCollageIdException> {

    @Override
    public Response toResponse(InvalidCollageIdException exception) {
        ErrorResponse body = new ErrorResponse(
            400,
            "Invalid collage id",
            exception.getMessage()
        );
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(body)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
