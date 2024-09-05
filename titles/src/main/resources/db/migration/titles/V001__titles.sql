create table if not exists titles
(
    id         uuid          not null primary key,
    state      varchar(63)   not null,
    title      varchar(1023) not null,
    refs       jsonb         not null default '{}',
    created_by uuid          null,
    created_at timestamp     not null default now(),
    updated_at timestamp     not null
);
