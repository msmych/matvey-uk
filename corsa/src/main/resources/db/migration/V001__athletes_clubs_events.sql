create table if not exists athletes
(
    id         uuid         not null default gen_random_uuid() primary key,
    name       varchar(255) not null,
    refs       jsonb        not null default '{}',
    created_at timestamp    not null default now(),
    updated_at timestamp    not null
);

create unique index if not exists athletes_refs_tg_idx on athletes ((refs -> 'tg'));

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
    date_time  timestamp    null,
    created_at timestamp    not null default now(),
    updated_at timestamp    not null
);

create table if not exists clubs_athletes
(
    club_id    uuid    not null,
    athlete_id uuid    not null,
    role       varchar(63) not null default 'MEMBER',
    primary key (club_id, athlete_id)
);

create index if not exists clubs_athletes_athlete_id_idx on clubs_athletes (athlete_id);

create table if not exists events_athletes
(
    event_id   uuid not null,
    athlete_id uuid not null,
    primary key (event_id, athlete_id)
);

create index if not exists events_athletes_athlete_id_idx on events_athletes (athlete_id);
