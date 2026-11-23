package br.com.corpoario.catalogue.exception;

import java.lang.reflect.Method;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
class RequestValidationExceptionMapperTest {

    @Inject
    Validator validator;

    @Test
    void shouldTreatInvalidReturnValueAsServerError() throws NoSuchMethodException {
        TestResource resource = new TestResource();
        Method method = TestResource.class.getMethod("invalidResult");
        Set<ConstraintViolation<TestResource>> violations = validator.forExecutables()
            .validateReturnValue(resource, method, null);

        assertInternalError(violations);
    }

    @Test
    void shouldTreatInternalParameterViolationAsServerError() throws NoSuchMethodException {
        TestService service = new TestService();
        Method method = TestService.class.getMethod("process", String.class);
        Set<ConstraintViolation<TestService>> violations = validator.forExecutables()
            .validateParameters(service, method, new Object[] { null });

        assertInternalError(violations);
    }

    private void assertInternalError(Set<? extends ConstraintViolation<?>> violations) {
        assertFalse(violations.isEmpty());
        RequestValidationExceptionMapper mapper = new RequestValidationExceptionMapper();
        mapper.resourceInfo = new ResourceInfo() {
            @Override
            public Method getResourceMethod() {
                return null;
            }

            @Override
            public Class<?> getResourceClass() {
                return TestResource.class;
            }
        };

        try (Response response = mapper.toResponse(new ResteasyReactiveViolationException(violations))) {
            assertEquals(500, response.getStatus());
            assertEquals(new ErrorResponse(500, "Internal server error", "An internal validation error occurred"),
                response.getEntity());
        }
    }

    static class TestResource {
        @NotNull
        public String invalidResult() {
            return null;
        }
    }

    static class TestService {
        public void process(@NotNull String value) {}
    }
}
