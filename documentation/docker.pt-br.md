# PostgreSQL com Docker

O serviço usa PostgreSQL no endereço `localhost:5432` e banco `catalogue`. Com o Docker e o Docker Compose instalados e em execução, crie um arquivo `.env` na raiz do projeto:

```dotenv
DB_PASSWORD=uma-senha-local
```

O Docker Compose lê o `.env` para substituir variáveis. O processo do Quarkus não carrega esse arquivo automaticamente; exporte as variáveis no terminal antes de iniciar os serviços:

```sh
set -a
. ./.env
set +a
```

Inicie o banco e a aplicação nesse mesmo terminal:

```sh
docker compose up -d
mvn quarkus:dev
```

O Compose usa o banco e o usuário `catalogue`, e o volume Docker `catalogue-data`. O valor de `DB_PASSWORD` inicializa a senha do PostgreSQL somente quando o volume é criado pela primeira vez; alterá-lo não muda a senha de um banco existente. O Compose reutiliza o volume e preserva os dados.

Confira o estado e a disponibilidade do banco:

```bash
docker compose ps
```

Se um contêiner antigo chamado `catalogue-db` estiver usando a porta 5432, pare-o antes:

```bash
docker stop catalogue-db
```

Para parar o banco sem apagar os dados:

```bash
docker compose down
```

Não execute `docker compose down -v` a menos que queira apagar o volume do banco e todos os dados nele.
