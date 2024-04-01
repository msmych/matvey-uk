CREATE TABLE IF NOT EXISTS wishlist
(
    id          UUID PRIMARY KEY NOT NULL,
    name        VARCHAR(255)     NOT NULL,
    state       VARCHAR(20)      NOT NULL,
    priority    VARCHAR(20)      NOT NULL,
    description TEXT             NULL,
    url         TEXT             NULL,
    created_at  TIMESTAMP        NOT NULL,
    updated_at  TIMESTAMP        NOT NULL
);
