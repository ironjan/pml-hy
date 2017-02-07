# --- !Ups

ALTER TABLE parking_data ADD COLUMN model_version  INTEGER;
ALTER TABLE parking_data ADD COLUMN hour_of_day    INTEGER;
ALTER TABLE parking_data ADD COLUMN minute_of_hour INTEGER;
ALTER TABLE parking_data ADD COLUMN day_of_week    INTEGER;
ALTER TABLE parking_data ADD COLUMN day_of_month   INTEGER;
ALTER TABLE parking_data ADD COLUMN week_of_month  INTEGER;
ALTER TABLE parking_data ADD COLUMN week_of_year   INTEGER;

# --- !Downs

ALTER TABLE parking_data DROP COLUMN week_of_year;
ALTER TABLE parking_data DROP COLUMN week_of_month;
ALTER TABLE parking_data DROP COLUMN day_of_month;
ALTER TABLE parking_data DROP COLUMN day_of_week;
ALTER TABLE parking_data DROP COLUMN minute_of_hour;
ALTER TABLE parking_data DROP COLUMN hour_of_day;
ALTER TABLE parking_data DROP COLUMN model_version;
