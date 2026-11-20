# Catalogue

The Catalogue microservice for the Corpoário digital atelier platform, built
with Java 25 and Quarkus.

## Requirements

- JDK 25
- Maven 3.9 or later
- Docker with Docker Compose

## Run in development mode

Create a `.env` file in the project root with a local PostgreSQL password:

```dotenv
DB_PASSWORD=local-password
```

Load the variables into your shell, then start the database and application:

```bash
set -a
. ./.env
set +a

docker compose up -d
mvn quarkus:dev
```

To stop the database without deleting its data, run `docker compose down`. See
[the Docker documentation](documentation/docker.md) and
[database documentation](documentation/database.md) for details.

In development mode, Swagger UI is available at
`http://localhost:8080/q/swagger-ui`. Health checks and Prometheus metrics are
available at `/q/health` and `/q/metrics`.
