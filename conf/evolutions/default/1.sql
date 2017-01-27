# --- !Ups

CREATE TABLE raw_parking_data(
  id SERIAL,
  is_deleted boolean NOT NULL,
  name varchar(255) NOT NULL,
  used int NOT NULL,
  capacity int NOT NULL,
  crawling_time TIMESTAMP NOT NULL
  );

# --- !Downs

DROP TABLE raw_parking_data;