SELECT 
  (SELECT COUNT(*) FROM parking_data    ) AS parking_data_count,	
  (SELECT COUNT(*) FROM predictions     ) AS predictions_count,
  (SELECT COUNT(*) FROM play_evolutions ) AS play_evolutions_count