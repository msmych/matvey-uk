create table if not exists frobot
(
    id    UUID        NOT NULL PRIMARY KEY,
    state VARCHAR(20) NOT NULL,
    tg    JSONB       NOT NULL DEFAULT '{}'
);
