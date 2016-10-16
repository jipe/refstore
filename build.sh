#!/bin/bash
mvn=$(which mvn)

if [[ -z $mvn ]]; then
  echo "You need to have Maven installed"
  exit 1
fi

exec $mvn clean package

