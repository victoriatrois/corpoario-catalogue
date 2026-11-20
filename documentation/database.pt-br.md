# Tabelas do banco e consultas úteis

## `collage`

A tabela `collage` é criada pela migration
[`V1__Create_collage_table.sql`](../src/main/resources/database/migration/V1__Create_collage_table.sql).
Ela armazena o UUID, título, preço, URL da imagem e os horários de criação e
atualização da colagem.

### Listar colagens (`GET /collages`)

O endpoint retorna todas as linhas de `collage`. Atualmente, não há garantia
de ordenação.

```sql
SELECT id, title, price, image_url, created_at, updated_at
FROM collage;
```

#### Explorar com o Postman

Inicie o banco e a aplicação conforme o
[README](../README.pt-br.md#executar-em-desenvolvimento). No workspace do
Postman conectado a este repositório, selecione **Local View** e o ambiente
**Local**; depois, abra **Catalogue → Get collages** e envie a requisição.
O `baseUrl` do ambiente usa `http://localhost:8080` por padrão; altere-o se a
aplicação estiver em outro endereço.

Uma resposta bem-sucedida tem status `200` e contém uma lista JSON. Ela será
vazia se não houver colagens; caso contrário, confira os campos e valores
retornados. A collection serve para verificações manuais: ela não cria dados
de teste nem substitui os testes automatizados com JUnit.

### Contar colagens

Útil para conferir a quantidade de registros antes ou depois de uma limpeza:

```sql
SELECT COUNT(*) FROM collage;
```

### Excluir todas as colagens

**Atenção: operação destrutiva.** A consulta remove todas as linhas de
`collage`. Use uma transação para conferir o resultado antes de confirmar:

```sql
BEGIN;

SELECT COUNT(*) FROM collage;
-- DELETE FROM collage;
SELECT COUNT(*) FROM collage;

ROLLBACK;
-- COMMIT;
```

Se o resultado não for o esperado, execute `ROLLBACK` em vez de `COMMIT`.
