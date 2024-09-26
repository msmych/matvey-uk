create table if not exists accounts
(
    id         uuid          not null primary key default gen_random_uuid(),
    state      varchar(63)   not null,
    name       varchar(255)  not null,
    tags       varchar(31)[] not null             default '{}',
    refs       jsonb         not null             default '{}',
    created_at timestamp     not null             default now(),
    updated_at timestamp     not null
);

create unique index if not exists accounts_refs_tg_idx on accounts ((refs ->> 'tg')) where (refs ->> 'tg') is not null;

insert into accounts (state, name, tags, refs, updated_at)
values ('ACTIVE', 'Matvey', '{ADMIN}', ('{"tg":' || ${tgAdminId} || '}')::jsonb, now());
