create table author (
  author_id int primary key,
  name text not null
);

create table book (
  book_id int primary key,
  title text not null,
  author_id int not null references author(author_id)
);

create sequence hibernate_sequence;
