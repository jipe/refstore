#!/bin/bash
docker=$(which docker)

if [[ -z $docker ]]; then
  echo "You need Docker to run this script."
  exit 1
fi

network_name=$(cat docker/network_name)

function usage {
  echo "$0 <create|rm> : Create or remove Docker network used for the application."
  echo
  echo "  Actions"
  echo "  -------"
  echo "   create : Create the Docker network."
  echo "       rm : Remove the Docker network."
  echo
}

function network_exists {
  echo $($docker network ls | grep -e " $network_name ")
}

if [[ -z $@ ]]; then
  echo "Missing argument."
  echo
  usage
  exit 1
fi

for arg; do
  case $arg in
    create)
      if [[ ! $(network_exists) ]]; then
        $docker network create $network_name
      fi
      ;;
    rm)
      if [[ $(network_exists) ]]; then
        $docker network rm $network_name
      fi
      ;;
    -h|--help)
      usage
      ;;
    *)
      usage
      exit 1
      ;;
  esac
done
