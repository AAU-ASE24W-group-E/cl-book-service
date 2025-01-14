-- add book owners tables
drop index if exists idx_book_owner_location;
drop table if exists book_owner;
drop table if exists book_ownership;

create table book_owner
(
    id       uuid not null
        primary key,
    name     varchar(255),
    location geography(Point, 4326)
);

create table book_ownership
(
    exchangeable boolean not null,
    giftable     boolean not null,
    lendable     boolean not null,
    book_id      uuid    not null
        constraint fk_book_ownership_book_id
            references book,
    owner_id     uuid    not null
        constraint fk_book_ownership_owner_id
            references book_owner,
    status       varchar(16),
    primary key (book_id, owner_id)
);

-- see https://postgis.net/workshops/postgis-intro/indexing.html
create index idx_book_owner_location
    on book_owner using gist (location);