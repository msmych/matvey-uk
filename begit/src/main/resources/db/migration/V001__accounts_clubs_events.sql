create table if not exists members
(
    id         uuid         not null primary key,
    name       varchar(255) null,
    refs       jsonb        not null default '{}',
    created_at timestamp    not null default now(),
    updated_at timestamp    not null
);

create unique index if not exists members_tg_id_idx on members ((refs ->> 'tgId'));

create table if not exists clubs
(
    id          uuid         not null primary key,
    name        varchar(255) not null,
    description text         null,
    refs        jsonb        not null default '{}',
    created_at  timestamp    not null default now(),
    updated_at  timestamp    not null
);

create unique index if not exists clubs_tg_id_idx on clubs ((refs ->> 'tgId'));

create table if not exists club_members
(
    club_id   uuid not null,
    member_id uuid not null,
    refs      jsonb not null default '{}',
    constraint club_members_pk primary key (club_id, member_id)
);

create index if not exists club_members_club_id_idx on club_members (club_id);

create table if not exists events
(
    id          uuid         not null primary key,
    title       varchar(255) not null,
    description text         null,
    type        varchar(63)  not null,
    date        date         null,
    date_time   timestamp    null,
    refs        jsonb        not null default '{}',
    created_at  timestamp    not null default now(),
    updated_at  timestamp    not null
);