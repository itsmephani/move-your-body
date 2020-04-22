# --- !Ups

create table "enrollments" (
  "userId" bigint NOT NULL,
  "programId" bigint NOT NULL,
  "startDate" timestamp NOT NULL,
  FOREIGN KEY ("userId") REFERENCES users("id"),
  FOREIGN KEY ("programId") REFERENCES workouts("id"),
  UNIQUE ("userId", "programId")
);

# --- !Downs

drop table "enrollments" if exists;