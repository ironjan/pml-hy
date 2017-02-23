DELETE FROM parking_data WHERE is_deleted  = true;
DELETE FROM parking_data WHERE model_version IS NULL OR model_version < 1;
DELETE FROM parking_data WHERE name NOT LIKE 'P6%';