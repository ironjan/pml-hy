# --- !Ups
--
--CREATE TABLE User (
--    id bigint(20) NOT NULL AUTO_INCREMENT,
--    email varchar(255) NOT NULL,
--    password varchar(255) NOT NULL,
--    fullname varchar(255) NOT NULL,
--    isAdmin boolean NOT NULL,
--    PRIMARY KEY (id)
--);
CREATE TABLE raw_parking_data(
  id bigint(20) NOT NULL AUTO_INCREMENT,
  is_deleted boolean NOT NULL,
  name varchar(255) NOT NULL,
  used int NOT NULL,
  capacity int NOT NULL,
  crawling_time TIMESTAMP NOT NULL
  );

# --- !Downs

DROP TABLE raw_parking_data;