# Catalogue

The Catalogue microservice for the Corpoário digital atelier platform, built
with Java 25 and Quarkus.

## Requirements

- JDK 25
- Maven 3.9 or later
- Docker with Docker Compose

## Run in development mode

Set a local PostgreSQL password and start the database from the project root:

```bash
export POSTGRES_PASSWORD=local-password
docker compose up -d
```

Use the same password to start the application:

```bash
export QUARKUS_DATASOURCE_PASSWORD="$POSTGRES_PASSWORD"
mvn quarkus:dev
```

To stop the database without deleting its data, run `docker compose down`. See
[the Docker documentation](src/main/documentation/docker.md) for details and
configuration options.

In development mode, Swagger UI is available at
`http://localhost:8080/q/swagger-ui`. Health checks and Prometheus metrics are
available at `/q/health` and `/q/metrics`.
