CREATE TABLE artist_profile (
    id UUID PRIMARY KEY,
    bio TEXT NOT NULL,
    photo_url VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
