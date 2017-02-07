# --- !Ups

ALTER TABLE parking_data ADD COLUMN free     INTEGER;
ALTER TABLE parking_data ADD COLUMN capacity INTEGER;

# --- !Downs

ALTER TABLE parking_data DROP COLUMN capacity;
ALTER TABLE parking_data DROP COLUMN free;
