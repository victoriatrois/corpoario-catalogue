# PostgreSQL com Docker

O serviço usa PostgreSQL no endereço `localhost:5432` e banco `catalogue`. Com o Docker e o Docker Compose instalados e em execução, configure uma senha local e inicie o banco na raiz do projeto:

```bash
export POSTGRES_PASSWORD='uma-senha-local'
docker-compose up -d
```

O Compose usa o banco e o usuário `catalogue`, e o volume Docker `catalogue-data`. Se você já usou o comando `docker run` documentado anteriormente, configure a senha original do banco. Se o contêiner `catalogue-db` antigo ainda estiver em execução, pare-o antes para liberar a porta 5432:

```bash
docker stop catalogue-db
```

O Compose reutiliza o volume `catalogue-data`, preservando os dados já gravados. `POSTGRES_PASSWORD` define a senha somente quando o volume é inicializado; alterar a variável não muda a senha de um banco existente. Não remova esse volume se quiser manter os dados.

Confira o estado e a disponibilidade do banco:

```bash
docker-compose ps
```

Inicie o Quarkus no mesmo terminal, usando a mesma senha:

```bash
export QUARKUS_DATASOURCE_PASSWORD="$POSTGRES_PASSWORD"
mvn quarkus:dev
```

Para parar o banco sem apagar os dados:

```bash
docker-compose down
```

Os comandos acima usam o executável independente `docker-compose`. Se você
usa o plugin do Docker Compose, substitua `docker-compose` por `docker compose`.
