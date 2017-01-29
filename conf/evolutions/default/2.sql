# --- !Ups

ALTER TABLE raw_parking_data ADD COLUMN city varchar(255);
UPDATE raw_parking_data  SET city='Paderborn' WHERE city IS NULL;
ALTER TABLE raw_parking_data ALTER COLUMN city SET NOT NULL;

# --- !Downs

ALTER TABLE raw_parking_data DROP COLUMN city;
