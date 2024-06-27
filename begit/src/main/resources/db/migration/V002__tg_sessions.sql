create table if not exists tg_sessions
(
    chat_id    bigint    not null primary key,
    data       jsonb     not null default '{}',
    created_at timestamp not null default now(),
    updated_at timestamp not null
);
