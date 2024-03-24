create table if not exists frobot
(
    id         UUID        NOT NULL PRIMARY KEY,
    state      VARCHAR(20) NOT NULL,
    tg         JSONB       NOT NULL DEFAULT '{}',
    created_at timestamp   not null,
    updated_at timestamp   not null
);
