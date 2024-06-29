create table if not exists athletes
(
    id         uuid         not null primary key,
    name       varchar(255) null,
    refs       jsonb        not null default '{}',
    created_at timestamp    not null default now(),
    updated_at timestamp    not null
);

create unique index if not exists athletes_tg_chat_id_idx on athletes ((refs ->> 'tgChatId'));

create table if not exists clubs
(
    id          uuid         not null primary key,
    name        varchar(255) not null,
    description text         null,
    refs        jsonb        not null default '{}',
    created_at  timestamp    not null default now(),
    updated_at  timestamp    not null
);

create unique index if not exists clubs_tg_chat_id_idx on clubs ((refs ->> 'tgChatId'));

create table if not exists club_members
(
    club_id    uuid      not null,
    athlete_id uuid      not null,
    refs       jsonb     not null default '{}',
    created_at timestamp not null default now(),
    constraint club_members_pk primary key (club_id, athlete_id)
);

create index if not exists club_members_club_id_idx on club_members (club_id);
create index if not exists club_members_athlete_id_idx on club_members (athlete_id);

create table if not exists events
(
    id           uuid         not null primary key,
    club_id      uuid         not null,
    organized_by uuid         not null,
    title        varchar(255) not null,
    description  text         null,
    type         varchar(63)  not null,
    date         date         null,
    date_time    timestamp    null,
    refs         jsonb        not null default '{}',
    created_at   timestamp    not null default now(),
    updated_at   timestamp    not null
);

create table if not exists event_participants
(
    event_id   uuid      not null,
    athlete_id uuid      not null,
    created_at timestamp not null default now(),
    constraint event_participants_pk primary key (event_id, athlete_id)
);

create index if not exists event_participants_event_id_idx on event_participants (event_id);
