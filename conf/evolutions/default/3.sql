# --- !Ups

ALTER TABLE raw_parking_data RENAME COLUMN used TO free;

# --- !Downs

ALTER TABLE raw_parking_data RENAME COLUMN free TO used;
