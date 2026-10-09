# Catalogue

The Catalogue microservice for the Corpoário digital atelier platform, built
with Java 25 and Quarkus.

## Requirements

- JDK 25
- Maven 3.9 or later
- PostgreSQL available at `localhost:5432`, with a database named `catalogue`

## Run in development mode

Set `DB_URL`, `DB_USERNAME`, and `QUARKUS_DATASOURCE_PASSWORD` for your
PostgreSQL instance, then run:

```bash
mvn quarkus:dev
```

In development mode, Swagger UI is available at
`http://localhost:8080/q/swagger-ui`. Health checks and Prometheus metrics are
available at `/q/health` and `/q/metrics`.
