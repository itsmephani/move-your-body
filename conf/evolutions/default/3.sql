# --- !Ups

create table "workouts" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "name" varchar not null,
  "description" varchar,
  "programID" bigint NOT NULL,
  FOREIGN KEY ("programID") REFERENCES programs("id")
);

# --- !Downs

drop table "workouts" if exists;