create table if not exists clubs
(
    id                 uuid         not null default gen_random_uuid() primary key,
    name               varchar(255) not null,
    refs               jsonb        not null default '{}',
    created_at         timestamp    not null default now(),
    updated_at         timestamp    not null
);

create table if not exists club_members
(
    club_id    uuid        not null,
    user_id    uuid        not null,
    role       varchar(63) not null default 'MEMBER',
    created_at timestamp   not null default now(),
    updated_at timestamp   not null,
    primary key (club_id, user_id)
);

create table if not exists titles
(
    id         uuid          not null default gen_random_uuid() primary key,
    state      varchar(63)   not null,
    title      varchar(1023) not null,
    club_id    uuid          null,
    refs       jsonb         not null default '{}',
    created_by uuid          null,
    created_at timestamp     not null default now(),
    updated_at timestamp     not null
);
