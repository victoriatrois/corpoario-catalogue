# Database tables and useful queries

## `artist_profile`

### Retrieve the published profile (`GET /artist-profile`)

When a profile exists, the endpoint returns `200` and a JSON object with `bio`,
`photoUrl`, and `email`. If no profile exists, it returns `404` with a JSON
response containing `status`, `error`, and `message`.

Migration [`V3`](../src/main/resources/database/migration/V3__Create_artist_profile_history.sql)
enforces at most one current profile with a unique index, including concurrent
inserts. If the table already contains multiple rows, the migration
fails without deleting or choosing a profile; resolve the duplicates before
restarting the migration.

The `artist_profile_history` table stores snapshots of previously published
profiles. Each snapshot has its own UUID, the original profile UUID, biography,
photo URL, email, original creation/update timestamps, and an archive timestamp.
It has no foreign key to `artist_profile`, so snapshots survive replacement of
the current row and multiple versions can refer to the same profile. Persist a
snapshot and the corresponding profile change in the same transaction. No
history endpoint or draft storage is provided.

#### Explore with Postman

Start the database and application as described in the
[README](../README.md#run-in-development-mode). In the Postman workspace
connected to this repository, select **Local View** and the **Local**
environment, then open **Catalogue → Get artist profile** and send the request.
The environment's `baseUrl` defaults to `http://localhost:8080`; change it if
the application uses another address.

A successful response has status `200` and a JSON object with `bio`,
`photoUrl`, and `email`; without a current profile, the response is `404`.
Archived versions are not exposed by the API, so inspect them with the queries
below. The collection is intended for manual checks and does not create test
data or replace the automated JUnit tests.

### Inspect the current profile

```sql
SELECT id, bio, photo_url, email, created_at, updated_at
FROM artist_profile;
```

To check how many current records exist, including before applying `V3`:

```sql
SELECT COUNT(*) FROM artist_profile;
```

## `artist_profile_history`

### List archived versions

```sql
SELECT id, artist_profile_id, bio, photo_url, email, created_at, updated_at, archived_at
FROM artist_profile_history
ORDER BY archived_at DESC, id;
```

### Find versions of a profile

Supply the original profile UUID as the `$1` parameter. This can find versions
even after the current profile has been replaced.

```sql
SELECT id, artist_profile_id, bio, photo_url, email, created_at, updated_at, archived_at
FROM artist_profile_history
WHERE artist_profile_id = $1
ORDER BY archived_at DESC, id;
```

To retrieve one archived version, supply its snapshot UUID as `$1`:

```sql
SELECT id, artist_profile_id, bio, photo_url, email, created_at, updated_at, archived_at
FROM artist_profile_history
WHERE id = $1;
```

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

### Create a collage (`POST /collages`)

Send a JSON object with `title`, `price`, and `imageUrl`. `title` and
`imageUrl` must not be blank, and `price` cannot be negative. A successful
request returns `201 Created`, the created collage in the response body, and a
`Location` header pointing to the new resource.

```json
{
  "title": "Modern Art",
  "price": 100.00,
  "imageUrl": "https://imageurl.com/art.png"
}
```

Invalid input returns `400 Bad Request` with `status`, `error`, and `message`.
If more than one field is invalid, their messages are sorted and separated by
`; `. For example:

```json
{
  "status": 400,
  "error": "Validation error",
  "message": "Price cannot be negative"
}
```

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
