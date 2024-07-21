create table if not exists cards
(
    id          uuid primary key not null,
    type        varchar(63)      not null,
    title       varchar(255)     not null,
    description text             null,
    url         text             null,
    created_at  timestamp        not null,
    updated_at  timestamp        not null
)