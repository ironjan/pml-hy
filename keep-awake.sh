#!/bin/bash

# times are in seconds
SLEEP_TIME=60
PROD_START_TIME=050000 # UTC. Opening 6am MEZ/UTC+1 MESZ/UTC+2 (switch 2017-03-27)
PROD_END___TIME=230000 # UTC. Closing 2am MEZ/MESZ. App needs 6h sleep a day.
HEROKU_APP=https://pppb.herokuapp.com/

function wake_up_app {
  curl -I -X HEAD https://enigmatic-citadel-24582.herokuapp.com/
}

while true
do
  currentTime=`date --utc +"%H%M%S"`
  currentTimeFull=`date --utc +"%Y-%m-%d %H%M%S"`
  echo "Running script @$currentTimeFull UTC. PID=$$" >> ./logs/keep-awake.log
  if [[ ! "$currentTime" < "$PROD_START_TIME" && ! "$currentTime" > "$PROD_END___TIME" ]]; then
    wake_up_app
  fi

  sleep $SLEEP_TIME
done
