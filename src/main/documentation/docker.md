# PostgreSQL with Docker

The service uses PostgreSQL at `localhost:5432` with a database named `catalogue`. With Docker and Docker Compose installed and running, set a local password and start the database from the project root:

```bash
export POSTGRES_PASSWORD='local-password'
docker-compose up -d
```

Compose uses the database and user `catalogue` and the Docker volume `catalogue-data`. If you previously used the documented `docker run` command, set the original database password. If the old `catalogue-db` container is still running, stop it first to free port 5432:

```bash
docker stop catalogue-db
```

Compose reuses the `catalogue-data` volume, preserving existing data. `POSTGRES_PASSWORD` sets the password only when the volume is initialized; changing the variable does not change an existing database password. Do not remove the volume if you want to keep the data.

Check the database status:

```bash
docker-compose ps
```

Start Quarkus in the same terminal, using the same password:

```bash
export QUARKUS_DATASOURCE_PASSWORD="$POSTGRES_PASSWORD"
mvn quarkus:dev
```

To stop the database without deleting its data:

```bash
docker-compose down
```

The commands above use the standalone `docker-compose` executable. If you use
the Docker Compose plugin, replace `docker-compose` with `docker compose`.
