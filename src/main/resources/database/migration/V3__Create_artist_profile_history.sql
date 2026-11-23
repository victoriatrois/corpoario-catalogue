CREATE UNIQUE INDEX artist_profile_single_current_idx ON artist_profile ((true));

CREATE TABLE artist_profile_history (
    id UUID PRIMARY KEY,
    artist_profile_id UUID NOT NULL,
    bio TEXT NOT NULL,
    photo_url VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    archived_at TIMESTAMP NOT NULL
);
