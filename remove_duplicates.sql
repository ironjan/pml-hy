----
--
-- Based on https://wiki.postgresql.org/wiki/Deleting_duplicates
--
----
DELETE FROM parking_data 
WHERE id IN (SELECT id
              FROM (SELECT id,
                             ROW_NUMBER() OVER (partition BY name, city, hour_of_day, minute_of_hour, day_of_week, day_of_month, week_of_month, week_of_year ORDER BY id) AS rnum
                     FROM parking_data) t
              WHERE t.rnum > 1)