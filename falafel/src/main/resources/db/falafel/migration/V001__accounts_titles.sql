create table if not exists falafel.accounts
(
    id         uuid      not null default gen_random_uuid() primary key,
    balance    bigint    not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null
);

create unique index if not exists accounts_id_idx on accounts (id);

create table if not exists titles
(
    id            uuid          not null default gen_random_uuid() primary key,
    state         varchar(63)   not null,
    title         varchar(1023) not null,
    director_name varchar(255)  null,
    release_year  int           null,
    refs          jsonb         not null default '{}',
    created_at    timestamp     not null default now(),
    updated_at    timestamp     not null
);

create unique index if not exists titles_tmdb_idx on titles ((refs -> 'tmdb')) where (refs -> 'tmdb') is not null;
