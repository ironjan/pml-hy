DELETE FROM predictions
WHERE predicted_time <  NOW() - INTERVAL '2 days';

DELETE FROM parking_data 
WHERE crawling_time < NOW() - INTERVAL '7 days';
