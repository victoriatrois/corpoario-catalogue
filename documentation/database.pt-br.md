# Tabelas do banco e consultas úteis

## `artist_profile`

### Consultar o perfil publicado (`GET /artist-profile`)

Quando existe um perfil, a rota retorna `200` e um objeto JSON com `bio`,
`photoUrl` e `email`. Se não houver perfil, retorna `404` com uma resposta JSON
contendo `status`, `error` e `message`.

A migration [`V3`](../src/main/resources/database/migration/V3__Create_artist_profile_history.sql)
garante no máximo um perfil atual por meio de um índice único, inclusive em
inserções concorrentes. Se a tabela já contiver várias linhas, a
migration falha sem excluir dados nem escolher um perfil; resolva as
duplicidades antes de executá-la novamente.

A tabela `artist_profile_history` armazena snapshots de perfis publicados
anteriormente. Cada snapshot possui UUID próprio, UUID do perfil original,
biografia, URL da foto, email, timestamps originais de criação/atualização e
timestamp de arquivamento. Não há chave estrangeira para `artist_profile`,
permitindo preservar snapshots após a substituição do perfil atual e registrar
várias versões do mesmo perfil. Persista o snapshot e a alteração correspondente
do perfil na mesma transação. Não há rota de histórico nem armazenamento de
rascunhos.

#### Explorar com o Postman

Inicie o banco e a aplicação conforme o
[README](../README.pt-br.md#executar-em-desenvolvimento). No workspace do
Postman conectado a este repositório, selecione **Local View** e o ambiente
**Local**; depois, abra **Catalogue → Get artist profile** e envie a
requisição. O `baseUrl` do ambiente usa `http://localhost:8080` por padrão;
altere-o se a aplicação estiver em outro endereço.

Uma resposta bem-sucedida tem status `200` e um objeto JSON com `bio`,
`photoUrl` e `email`; sem perfil atual, a resposta é `404`. As versões
arquivadas não são expostas pela API, portanto consulte-as com as consultas
abaixo. A collection serve para verificações manuais: ela não cria dados de
teste nem substitui os testes automatizados com JUnit.

### Consultar o perfil atual

```sql
SELECT id, bio, photo_url, email, created_at, updated_at
FROM artist_profile;
```

Para verificar a quantidade de registros atuais, inclusive antes de aplicar a
`V3`:

```sql
SELECT COUNT(*) FROM artist_profile;
```

## `artist_profile_history`

### Listar versões arquivadas

```sql
SELECT id, artist_profile_id, bio, photo_url, email, created_at, updated_at, archived_at
FROM artist_profile_history
ORDER BY archived_at DESC, id;
```

### Buscar versões de um perfil

Informe o UUID do perfil original como parâmetro `$1`. A consulta encontra
versões mesmo após a substituição do perfil atual.

```sql
SELECT id, artist_profile_id, bio, photo_url, email, created_at, updated_at, archived_at
FROM artist_profile_history
WHERE artist_profile_id = $1
ORDER BY archived_at DESC, id;
```

Para consultar uma versão arquivada, informe o UUID do snapshot como `$1`:

```sql
SELECT id, artist_profile_id, bio, photo_url, email, created_at, updated_at, archived_at
FROM artist_profile_history
WHERE id = $1;
```

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
