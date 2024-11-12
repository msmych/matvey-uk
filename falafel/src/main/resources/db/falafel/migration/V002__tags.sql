create table if not exists tags
(
    id         uuid         not null default gen_random_uuid() primary key,
    name       varchar(255) not null,
    title_id   uuid         not null,
    account_id uuid         not null,
    created_at timestamp    not null default now()
);

create index if not exists tags_title_id_idx on tags (title_id);
