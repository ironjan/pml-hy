#!/bin/bash

# times are in seconds
SLEEP_TIME=1800
PROD_START_TIME=020000
PROD_END_TIME=080000
HEROKU_APP=https://enigmatic-citadel-24582.herokuapp.com/

function wake_up_app {
  curl -I -X HEAD https://enigmatic-citadel-24582.herokuapp.com/
}

while true
do
  currentTime=`date +"%H%M%S"`
  echo "Running script @$currentTime" >> ./logs/keep-awake.log
  if [[ ! "$currentTime" < "$PROD_START_TIME" && ! "$currentTime" > "$PROD_END_TIME" ]]; then
    wake_up_app
  fi

  sleep $SLEEP_TIME
done
