# --- !Ups

CREATE TABLE predictions(
  id SERIAL PRIMARY KEY,
  is_deleted boolean NOT NULL,
  regression_class varchar(255) NOT NULL,
  prediction double precision NOT NULL,
  avg_abs_error double precision NOT NULL,
  predicted_time TIMESTAMP NOT NULL
  );

# --- !Downs

DROP TABLE predictions;