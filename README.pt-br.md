# Catálogo

Microsserviço de catálogo da plataforma Corpoário, estruturado com Java 25 e
Quarkus.

## Requisitos

- JDK 25
- Maven 3.9 ou superior
- Docker com Docker Compose

## Executar em desenvolvimento

Crie um arquivo `.env` na raiz do projeto com uma senha local do PostgreSQL:

```dotenv
DB_PASSWORD=uma-senha-local
```

Carregue as variáveis no terminal e inicie o banco e a aplicação:

```bash
set -a
. ./.env
set +a

docker compose up -d
mvn quarkus:dev
```

Para parar o banco sem apagar os dados, execute `docker compose down`. Consulte
[a documentação do Docker](documentation/docker.pt-br.md) e
[a documentação do banco de dados](documentation/database.pt-br.md) para mais detalhes.

O modo de desenvolvimento disponibiliza a interface Swagger UI em
`http://localhost:8080/q/swagger-ui`. Os endpoints de health check e métricas
Prometheus ficam em `/q/health` e `/q/metrics`.
