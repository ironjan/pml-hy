# --- !Ups

ALTER TABLE parking_data RENAME COLUMN free     TO free_raw;
ALTER TABLE parking_data RENAME COLUMN capacity TO capacity_raw;

# --- !Downs

ALTER TABLE parking_data RENAME COLUMN free_raw     TO free;
ALTER TABLE parking_data RENAME COLUMN capacity_raw TO capacity;
