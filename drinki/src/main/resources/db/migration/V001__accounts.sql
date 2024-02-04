CREATE TABLE IF NOT EXISTS accounts
(
    id         UUID          NOT NULL PRIMARY KEY,
    tg_session JSONB         NOT NULL DEFAULT '{}'::JSONB,
    created_at TIMESTAMP     NOT NULL,
    updated_at TIMESTAMP     NOT NULL
);

CREATE UNIQUE INDEX accounts_tg_user_id_idx ON accounts ((tg_session ->> 'userId'))
    WHERE (tg_session ->> 'userId') IS NOT NULL;