#!/bin/bash
docker=$(which docker)

if [[ -z $docker ]]; then
  echo "You need Docker to run this script."
  exit 1
fi

application=refstore

scripts/build_runner.sh mvn -f

exec scripts/mvn.sh clean verify
