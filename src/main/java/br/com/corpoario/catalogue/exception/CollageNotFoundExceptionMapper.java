package br.com.corpoario.catalogue.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CollageNotFoundExceptionMapper implements ExceptionMapper<CollageNotFoundException>{

    @Override
    public Response toResponse(CollageNotFoundException exception) {
        ErrorResponse body = new ErrorResponse(
            404,
            "Collage not found",
            exception.getMessage()
        );
        return Response.status(Response.Status.NOT_FOUND)
            .entity(body)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
