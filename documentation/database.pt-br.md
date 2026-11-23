# Tabelas do banco e consultas úteis

## `artist_profile`

### Consultar o perfil publicado (`GET /artist-profile`)

Quando existe um perfil, a rota retorna `200` e um objeto JSON com `bio`,
`photoUrl` e `email`. Se não houver perfil, retorna `404` com uma resposta JSON
contendo `status`, `error` e `message`.

O sistema espera no máximo um perfil atual. Enquanto essa regra não é
garantida pelo banco, a existência de mais de um registro é tratada como erro
interno (`500`), sem escolher um perfil arbitrariamente.

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

### Criar uma colagem (`POST /collages`)

Envie um objeto JSON com `title`, `price` e `imageUrl`. `title` e `imageUrl`
não podem estar vazios, e `price` não pode ser negativo. Uma requisição
bem-sucedida retorna `201 Created`, a colagem criada no corpo da resposta e um
cabeçalho `Location` apontando para o novo recurso.

```json
{
  "title": "Modern Art",
  "price": 100.00,
  "imageUrl": "https://imageurl.com/art.png"
}
```

Dados inválidos retornam `400 Bad Request` com `status`, `error` e `message`.
Se mais de um campo for inválido, as mensagens são ordenadas e separadas por
`; `. Por exemplo:

```json
{
  "status": 400,
  "error": "Validation error",
  "message": "Price cannot be negative"
}
```

### Buscar uma colagem por ID (`GET /collages/{id}`)

O parâmetro `id` no caminho é um UUID. Quando a colagem existe, o endpoint
retorna status `200` e um objeto JSON com `id`, `title`, `price`, `imageUrl`,
`createdAt` e `updatedAt`. Um ID que não seja um UUID no formato canônico
hexadecimal `8-4-4-4-12` (maiúsculas ou minúsculas) retorna `400 Bad Request`.
Um UUID válido sem uma colagem correspondente retorna `404 Not Found`.

As duas respostas de erro usam `application/json` e contêm os campos `status`,
`error` e `message`. Por exemplo, para um ID inválido:

```json
{
  "status": 400,
  "error": "Invalid collage id",
  "message": "The collage id value must be a valid UUID"
}
```

Para um ID válido sem uma colagem correspondente:

```json
{
  "status": 404,
  "error": "Collage not found",
  "message": "No collage was found with the id 28ba356c-5745-4a88-b70d-f4148a6f8f83"
}
```

A consulta equivalente usa o UUID informado como parâmetro:

```sql
SELECT id, title, price, image_url, created_at, updated_at
FROM collage
WHERE id = $1;
```

No Postman, abra **Catalogue → Get collage by ID** e substitua o exemplo da
variável de caminho `id` por um UUID retornado por **Catalogue → Get collages**.
Envie a requisição para conferir a resposta.

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
