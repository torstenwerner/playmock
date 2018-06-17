create table author (
  author_id int primary key,
  name      text not null
);

create table book (
  book_id   int primary key,
  title     text not null,
  author_id int  not null references author (author_id)
);

create table customer (
  customer_id int primary key,
  name        text not null
);

CREATE TABLE revinfo
(
  rev      integer NOT NULL,
  revtstmp bigint,
  CONSTRAINT revinfo_pkey PRIMARY KEY (rev)
);

CREATE TABLE customer_aud
(
  rev         integer NOT NULL,
  revtype     smallint,
  customer_id int,
  name        text    not null,
  CONSTRAINT customer_aud_pkey PRIMARY KEY (customer_id, rev),
  CONSTRAINT customer_aud_revinfo FOREIGN KEY (rev)
  REFERENCES revinfo (rev) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

create sequence hibernate_sequence;
