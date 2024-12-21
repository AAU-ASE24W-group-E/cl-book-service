-- create database schema

create table book
(
    id                  uuid         not null
        primary key,
    publish_year        integer,
    isbn_number         bigint,
    cover_id            varchar(255),
    edition             varchar(255),
    format              varchar(255),
    isbn_display_number varchar(255),
    languages           varchar(255),
    publisher           varchar(255),
    title               varchar(255) not null
);

create table book_author
(
    id   uuid         not null
        primary key,
    key  varchar(255) not null
        constraint idx_book_author_key
            unique,
    name varchar(255) not null
);

create table book_authoring
(
    author_id uuid not null
        constraint fk_book_authoring_author_id
            references book_author,
    book_id   uuid not null
        constraint fk_book_authoring_book_id
            references book
);
