# PostgreSQL with Docker

The service uses PostgreSQL at `localhost:5432` with a database named `catalogue`. With Docker and Docker Compose installed and running, create a `.env` file in the project root:

```dotenv
DB_PASSWORD=local-password
```

Docker Compose reads `.env` for variable substitution. The Quarkus process does not automatically load that file, so export its values in the current shell before starting either service:

```sh
set -a
. ./.env
set +a
```

Start the database and application from that same shell:

```sh
docker compose up -d
mvn quarkus:dev
```

Compose uses the database and user `catalogue` and the Docker volume `catalogue-data`. The `DB_PASSWORD` value initializes the PostgreSQL password only when the volume is first created; changing it does not change the password of an existing database. Compose reuses the volume and preserves its data.

Check the database status:

```bash
docker compose ps
```

If an older `catalogue-db` container is still running and occupies port 5432, stop it first:

```bash
docker stop catalogue-db
```

To stop the database without deleting its data:

```bash
docker compose down
```

Do not run `docker compose down -v` unless you intend to delete the database volume and all its data.
