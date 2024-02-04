CREATE TABLE IF NOT EXISTS drinks
(
    id          UUID         NOT NULL PRIMARY KEY,
    account_id  UUID         NULL,
    name        VARCHAR(255) NOT NULL,
    ingredients JSONB        NOT NULL DEFAULT '[]'::JSONB,
    recipe      TEXT         NULL,
    visibility  VARCHAR(63)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS drinks_visibility_idx ON drinks (visibility);
