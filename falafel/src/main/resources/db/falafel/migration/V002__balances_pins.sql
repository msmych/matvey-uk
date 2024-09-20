create table if not exists balances
(
    id         uuid      not null default gen_random_uuid() primary key,
    account_id uuid      not null,
    quantity   bigint    not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null
);

create unique index if not exists balances_account_id_idx on balances (account_id);

create table if not exists pins
(
    id         uuid         not null default gen_random_uuid() primary key,
    name       varchar(255) not null,
    title_id   uuid         not null,
    created_at timestamp    not null default now()
);
