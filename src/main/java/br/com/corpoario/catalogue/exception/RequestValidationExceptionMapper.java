package br.com.corpoario.catalogue.exception;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.jboss.logging.Logger;

import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RequestValidationExceptionMapper implements ExceptionMapper<ResteasyReactiveViolationException> {

    private static final Logger LOG = Logger.getLogger(RequestValidationExceptionMapper.class);

    @Context
    ResourceInfo resourceInfo;

    @Override
    public Response toResponse(ResteasyReactiveViolationException exception) {
        boolean requestValidation = resourceInfo.getResourceClass() != null
            && !exception.getConstraintViolations().isEmpty()
            && exception.getConstraintViolations().stream().allMatch(this::isRequestParameterViolation);

        if (!requestValidation) {
            LOG.error("Validation failed outside the resource request parameters");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse(500, "Internal server error", "An internal validation error occurred"))
                .type(MediaType.APPLICATION_JSON)
                .build();
        }

        String message = exception.getConstraintViolations().stream()
            .map(violation -> Objects.requireNonNull(violation).getMessage())
            .distinct()
            .sorted()
            .collect(Collectors.joining("; "));

        return Response.status(Response.Status.BAD_REQUEST)
            .entity(new ErrorResponse(400, "Validation error", message))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    private boolean isRequestParameterViolation(ConstraintViolation<?> violation) {
        if (!resourceInfo.getResourceClass().isAssignableFrom(violation.getRootBeanClass())) {
            return false;
        }

        return StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
            .anyMatch(node -> node.getKind() == ElementKind.PARAMETER);
    }
}
