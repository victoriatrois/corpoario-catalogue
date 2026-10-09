# Catálogo

Microsserviço de catálogo da plataforma Corpoário, estruturado com Java 25 e
Quarkus.

## Requisitos

- JDK 25
- Maven 3.9 ou superior
- PostgreSQL disponível em `localhost:5432`, com banco `catalogue`

## Executar em desenvolvimento

Configure `DB_URL`, `DB_USERNAME` e `QUARKUS_DATASOURCE_PASSWORD` para o seu
PostgreSQL e execute:

```bash
mvn quarkus:dev
```

O modo de desenvolvimento disponibiliza a interface Swagger UI em
`http://localhost:8080/q/swagger-ui`. Os endpoints de health check e métricas
Prometheus ficam em `/q/health` e `/q/metrics`.
