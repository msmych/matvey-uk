CREATE TABLE IF NOT EXISTS ingredients
(
    id         UUID         NOT NULL PRIMARY KEY,
    account_id UUID         NULL,
    type       VARCHAR(63)  NULL,
    name       VARCHAR(255) NOT NULL,
    visibility VARCHAR(63)  NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS ingredients_visibility_idx ON ingredients (visibility);