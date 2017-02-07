# --- !Ups

ALTER TABLE raw_parking_data RENAME TO parking_data;

# --- !Downs

ALTER TABLE parking_data RENAME TO raw_parking_data;
