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

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.matchesRegex;



@QuarkusTest
public class CollageResourceTest {
    @Inject
    CollageRepository repository;

    @BeforeAll
    static void configureJsonNumberParsing() {
        // Default RestAssured JsonPath parses decimals as Float/Double;
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

        // {
        //     "id": "28ba356c-5745-4a88-b70d-f4148a6f8f83",
        //     "title": "Modern Art",
        //     "price": 100.00,
        //     "createdAt": "2026-11-19T21:10:35.080614",
        //     "updatedAt": "2026-11-19T21:10:35.080632"
        // }

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
}
