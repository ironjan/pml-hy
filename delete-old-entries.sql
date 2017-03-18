DELETE FROM predictions
WHERE predicted_time <  NOW() - INTERVAL '1 days';

DELETE FROM parking_data 
WHERE crawling_time < NOW() - INTERVAL '7 days';
