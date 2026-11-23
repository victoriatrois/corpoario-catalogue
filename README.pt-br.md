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

## Executar os testes

```bash
mvn test
```

Os testes usam um container PostgreSQL descartável, iniciado automaticamente
pelo Quarkus Dev Services; por isso, o Docker precisa estar em execução. Eles
nunca usam o banco de desenvolvimento nem `DB_URL`. Se o socket do Docker não
estiver no local padrão (por exemplo, no Colima), defina antes `DOCKER_HOST` e
`TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock`.
