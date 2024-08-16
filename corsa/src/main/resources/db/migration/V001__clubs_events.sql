create table if not exists clubs
(
    id         uuid         not null default gen_random_uuid() primary key,
    name       varchar(255) not null,
    created_at timestamp    not null default now(),
    updated_at timestamp    not null
);

create table if not exists events
(
    id         uuid         not null default gen_random_uuid() primary key,
    name       varchar(255) not null,
    club_id    uuid         not null,
    date       date         not null,
    created_at timestamp    not null default now(),
    updated_at timestamp    not null
);