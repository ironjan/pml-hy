# --- !Ups

CREATE TABLE raw_parking_data(
  id SERIAL PRIMARY KEY,
  is_deleted boolean NOT NULL,
  name varchar(255) NOT NULL,
  used varchar(255) NOT NULL,
  capacity varchar(255) NOT NULL,
  crawling_time TIMESTAMP NOT NULL
  );

# --- !Downs

DROP TABLE raw_parking_data;