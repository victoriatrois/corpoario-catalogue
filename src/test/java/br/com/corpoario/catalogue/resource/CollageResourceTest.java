package br.com.corpoario.catalogue.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import br.com.corpoario.catalogue.entity.Collage;
import br.com.corpoario.catalogue.repository.CollageRepository;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;

import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig.NumberReturnType;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.matchesRegex;
import static io.restassured.http.ContentType.JSON;

// format of a collage
// {
//     "id": "28ba356c-5745-4a88-b70d-f4148a6f8f83",
//     "title": "Modern Art",
//     "price": 100.00, // * Default RestAssured JsonPath parses decimals as Float/Double
//     "createdAt": "2026-11-19T21:10:35.080614",
//     "updatedAt": "2026-11-19T21:10:35.080632"
// }

@QuarkusTest
public class CollageResourceTest {
    @Inject
    CollageRepository repository;

    @BeforeAll
    static void configureJsonNumberParsing() {
        // *
        // BigDecimal is needed to match price precisely against BigDecimal assertions.
        RestAssured.config = RestAssured.config()
            .jsonConfig(JsonConfig.jsonConfig().numberReturnType(NumberReturnType.BIG_DECIMAL));
    }

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoCollagesExist() {
        given()
            .when().get("/collages")
            .then()
            .statusCode(200)
            .body("size()", equalTo(0));
    }

    @Test
    void shouldReturnSingleCollageWhenOnlyOneExists() {
        Collage collage = new Collage("Modern Art", new BigDecimal("100.00"), "https://imageurl.com/art.png");

        QuarkusTransaction.requiringNew().run(() -> repository.persist(collage));

        given()
            .when().get("/collages")
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].title", equalTo("Modern Art"));
    }

    @Test
    void shouldReturnAllCollagesWhenMultipleExist() {
        Collage collageOne = new Collage("Collage 1", new BigDecimal("150.00"), "https://imageurl.com/collage1.png");
        Collage collageTwo = new Collage("Collage 2", new BigDecimal("130.00"), "https://imageurl.com/collage2.png");

        QuarkusTransaction.requiringNew().run(() -> repository.persist(collageOne, collageTwo));

        given()
            .when().get("/collages")
            .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("title", containsInAnyOrder("Collage 1", "Collage 2"));
    }

    @Test
    void shouldReturnCompleteCollageDetails() {
        Collage collage = new Collage("Modern Art", new BigDecimal("100.00"), "https://imageurl.com/art.png");

        QuarkusTransaction.requiringNew().run(() -> repository.persist(collage));

        given()
            .when().get("/collages")
            .then()
            .statusCode(200)
            .body("[0].id", notNullValue())
            .body("[0].id", matchesRegex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"))
            .body("[0].title", equalTo("Modern Art"))
            .body("[0].price", comparesEqualTo(new BigDecimal("100.00")))
            .body("[0].imageUrl", equalTo("https://imageurl.com/art.png"));
    }

    @Test
    void shouldReturnTheCollageForTheGivenId() {
        Collage requestedCollage = new Collage("Modern Art", new BigDecimal("100.00"), "https://imageurl.com/art.png");
        Collage anotherCollage = new Collage("Other Art", new BigDecimal("50.00"), "https://imageurl.com/other.png");

        QuarkusTransaction.requiringNew().run(() -> repository.persist(requestedCollage, anotherCollage));

        given()
        .when().get("/collages/{id}", requestedCollage.getId())
        .then()
        .statusCode(200)
        .body("id", equalTo(requestedCollage.getId().toString()))
        .body("title", equalTo("Modern Art"))
        .body("size()", equalTo(6));
    }

    @Test
    void shouldReturnAllTheCollagesDetails() {
        Collage collage = new Collage(
            "Modern Art",
            new BigDecimal("100.00"),
            "https://imageurl.com/art.png");

        QuarkusTransaction.requiringNew().run(() -> repository.persist(collage));

        given()
        .when().get("/collages/{id}", collage.getId())
        .then()
        .statusCode(200)
        .body("price", comparesEqualTo(new BigDecimal("100.00")))
        .body("imageUrl", equalTo("https://imageurl.com/art.png"))
        .body("createdAt", notNullValue())
        .body("updatedAt", notNullValue())
        .body("keySet()", containsInAnyOrder(
            "id",
            "title",
            "price",
            "createdAt",
            "updatedAt",
            "imageUrl"));
    }

    @Test
    void shouldReturnNotFoundWhenCollageDoesNotExist() {
        Collage collage = new Collage("Modern Art", new BigDecimal("100.00"), "https://imageurl.com/art.png");

        QuarkusTransaction.requiringNew().run(() -> repository.persist(collage));

        UUID nonExistentId = new UUID(collage.getId().getMostSignificantBits() ^ 1L, collage.getId().getLeastSignificantBits());

        given()
        .when().get("/collages/{id}", nonExistentId)
        .then()
        .statusCode(404)
        .contentType("application/json")
        .body("status", equalTo(404))
        .body("error", equalTo("Collage not found"))
        .body("message", equalTo("No collage was found with the id " + nonExistentId));
    }

    @Test
    void shouldReturnBadRequestWhenCollageIdIsNotAUuid() {
        String invalidId = "I'm not a UUID";
        given()
        .when().get("/collages/{id}", invalidId)
        .then()
        .statusCode(400)
        .contentType("application/json")
        .body("status", equalTo(400))
        .body("error", equalTo("Invalid collage id"))
        .body("message", equalTo("The collage id value must be a valid UUID"));
    }

    @Test
    void shouldReturnBadRequestWhenCollageIdIsNotInCanonicalUuidFormat() {
        given()
        .when().get("/collages/{id}", "1-1-1-1-1")
        .then()
        .statusCode(400)
        .contentType("application/json")
        .body("status", equalTo(400))
        .body("error", equalTo("Invalid collage id"))
        .body("message", equalTo("The collage id value must be a valid UUID"));
    }

    @Test
    void shouldAcceptUppercaseUuidWhenLookingUpCollage() {
        Collage collage = new Collage("Modern Art", new BigDecimal("100.00"), "https://imageurl.com/art.png");

        QuarkusTransaction.requiringNew().run(() -> repository.persist(collage));

        given()
        .when().get("/collages/{id}", collage.getId().toString().toUpperCase())
        .then()
        .statusCode(200)
        .body("id", equalTo(collage.getId().toString()));
    }

    @Test
    void shouldCreateCollage() {
        given()
            .contentType(JSON)
            .body("""
                {
                    "title": "Modern Art",
                    "price": 100.00,
                    "imageUrl": "https://imageurl.com/art.png"
                }
                """)
        .when().post("/collages")
        .then()
            .statusCode(201)
            .header("Location", matchesRegex(".*/collages/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"))
            .body("id", matchesRegex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"))
            .body("title", equalTo("Modern Art"))
            .body("price", comparesEqualTo(new BigDecimal("100.00")))
            .body("imageUrl", equalTo("https://imageurl.com/art.png"))
            .body("createdAt", notNullValue())
            .body("updatedAt", notNullValue());
    }

    @Test
    void shouldRejectCollageWithBlankTitle() {
        given()
            .contentType(JSON)
            .body("""
                {
                    "title": " ",
                    "price": 100.00,
                    "imageUrl": "https://imageurl.com/art.png"
                }
                """)
        .when().post("/collages")
        .then()
            .statusCode(400)
            .contentType(JSON)
            .body("status", equalTo(400))
            .body("error", equalTo("Validation error"))
            .body("message", equalTo("Title cannot be blank"))
            .body("keySet()", containsInAnyOrder("status", "error", "message"));
    }

    @Test
    void shouldRejectCollageWithNegativePrice() {
        given()
            .contentType(JSON)
            .body("""
                {
                    "title": "Modern Art",
                    "price": -1.00,
                    "imageUrl": "https://imageurl.com/art.png"
                }
                """)
        .when().post("/collages")
        .then()
            .statusCode(400)
            .contentType(JSON)
            .body("status", equalTo(400))
            .body("error", equalTo("Validation error"))
            .body("message", equalTo("Price cannot be negative"));
    }

    @Test
    void shouldReportAllInvalidFieldsWithoutPersisting() {
        given()
            .contentType(JSON)
            .body("""
                {
                    "title": " ",
                    "price": null,
                    "imageUrl": ""
                }
                """)
        .when().post("/collages")
        .then()
            .statusCode(400)
            .contentType(JSON)
            .body("status", equalTo(400))
            .body("error", equalTo("Validation error"))
            .body("message", equalTo("Image URL cannot be blank; Price cannot be null; Title cannot be blank"));

        given()
        .when().get("/collages")
        .then()
            .statusCode(200)
            .body("size()", equalTo(0));
    }

    @Test
    void shouldRejectNullRequestBody() {
        given()
            .contentType(JSON)
            .body("null")
        .when().post("/collages")
        .then()
            .statusCode(400)
            .contentType(JSON)
            .body("status", equalTo(400))
            .body("error", equalTo("Validation error"))
            .body("message", equalTo("Request body cannot be null"));
    }
}
