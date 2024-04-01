CREATE TABLE IF NOT EXISTS wishlist
(
    id          UUID          NOT NULL PRIMARY KEY,
    name        VARCHAR(255)  NOT NULL,
    state       VARCHAR(20)   NOT NULL,
    tags        VARCHAR(20)[] NOT NULL DEFAULT '{}'::VARCHAR(20)[],
    description TEXT          NULL,
    url         TEXT          NULL,
    tg          JSONB         NOT NULL DEFAULT '{}'::JSONB,
    created_at  TIMESTAMP     NOT NULL,
    updated_at  TIMESTAMP     NOT NULL
);
