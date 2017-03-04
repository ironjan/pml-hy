#!/bin/bash
if [ "$#" -ne 1 ]; then
  echo "Usage: <script> \"msg\""
  exit 1
fi

./bin/activator compile && git commit app -m "$1" && git push && heroku logs --app pppb -t
