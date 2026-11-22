# Database tables and useful queries

## `collage`

The `collage` table is created by
[`V1__Create_collage_table.sql`](../src/main/resources/database/migration/V1__Create_collage_table.sql).
It stores a collage's UUID, title, price, image URL, and creation/update
timestamps.

### List collages (`GET /collages`)

The endpoint returns all rows from `collage`. No ordering is currently
guaranteed.

```sql
SELECT id, title, price, image_url, created_at, updated_at
FROM collage;
```

#### Explore with Postman

Start the database and application as described in the
[README](../README.md#run-in-development-mode). In the Postman workspace
connected to this repository, select **Local View** and the **Local**
environment, then open **Catalogue → Get collages** and send the request.
The environment's `baseUrl` defaults to `http://localhost:8080`; change it if
the application uses another address.

A successful response has status `200` and a JSON array. It is empty when no
collages exist; otherwise, inspect the returned fields and values. The
collection is intended for manual checks and does not create test data or
replace the automated JUnit tests.

### Find a collage by ID (`GET /collages/{id}`)

The `id` path parameter is a UUID. When a matching collage exists, the endpoint
returns status `200` and a JSON object with `id`, `title`, `price`, `imageUrl`,
`createdAt`, and `updatedAt`. An ID that is not a UUID in the canonical
`8-4-4-4-12` hexadecimal format (uppercase or lowercase) returns
`400 Bad Request`. A valid UUID with no matching collage returns `404 Not Found`.

Both error responses use `application/json` and contain `status`, `error`, and
`message` fields. For example, an invalid ID returns:

```json
{
  "status": 400,
  "error": "Invalid collage id",
  "message": "The collage id value must be a valid UUID"
}
```

For a valid ID with no matching collage:

```json
{
  "status": 404,
  "error": "Collage not found",
  "message": "No collage was found with the id 28ba356c-5745-4a88-b70d-f4148a6f8f83"
}
```

The equivalent lookup uses the supplied UUID as a parameter:

```sql
SELECT id, title, price, image_url, created_at, updated_at
FROM collage
WHERE id = $1;
```

In Postman, open **Catalogue → Get collage by ID** and replace the example
`id` path variable with a UUID returned by **Catalogue → Get collages**.
Send the request to inspect the response.

### Count collages

Useful for checking the number of records before or after a cleanup:

```sql
SELECT COUNT(*) FROM collage;
```

### Delete all collages

**Destructive:** this removes every row from `collage`. Use a transaction to
review the result before committing:

```sql
BEGIN;

SELECT COUNT(*) FROM collage;
-- DELETE FROM collage;
SELECT COUNT(*) FROM collage;

ROLLBACK;
-- COMMIT;
```

If the result is unexpected, run `ROLLBACK` instead of `COMMIT`.
