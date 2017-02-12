DELETE FROM parking_data 
WHERE id IN (SELECT id
              FROM (SELECT id,
                             ROW_NUMBER() OVER (partition BY crawling_time ORDER BY id) AS rnum
                     FROM parking_data) t
              WHERE t.rnum > 1)