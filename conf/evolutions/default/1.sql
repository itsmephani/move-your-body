# --- !Ups

create table "users" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "name" varchar not null,
  "age" int not null,
  "weight" int not null,
  "gender" varchar(10) not null,
  "email" varchar NOT NULL,
  "password" varchar NOT NULL,
  "apiKey" varchar NOT NULL,
  "createdAt" timestamp default current_timestamp,
  UNIQUE ("email")
);

# --- !Downs

drop table "users" if exists;