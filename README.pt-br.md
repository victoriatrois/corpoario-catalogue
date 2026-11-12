# Catálogo

Microsserviço de catálogo da plataforma Corpoário, estruturado com Java 25 e
Quarkus.

## Requisitos

- JDK 25
- Maven 3.9 ou superior
- Docker com Docker Compose

## Executar em desenvolvimento

Configure a senha local do PostgreSQL e inicie o banco na raiz do projeto:

```bash
export POSTGRES_PASSWORD=uma-senha-local
docker compose up -d
```

Use a mesma senha para iniciar a aplicação:

```bash
export QUARKUS_DATASOURCE_PASSWORD="$POSTGRES_PASSWORD"
mvn quarkus:dev
```

Para parar o banco sem apagar os dados, execute `docker compose down`. Consulte
[a documentação do Docker](src/main/documentation/docker.pt-br.md) para detalhes e
opções de configuração.

O modo de desenvolvimento disponibiliza a interface Swagger UI em
`http://localhost:8080/q/swagger-ui`. Os endpoints de health check e métricas
Prometheus ficam em `/q/health` e `/q/metrics`.
