#!/bin/bash
docker=$(which docker)

if [[ -z $docker ]]; then
  echo "You need Docker to run this script."
  exit 1
fi

application=refstore
runner=mvn-runner
container_name=${application}-${runner}-$(uuidgen)
network_name=$(cat docker/network_name)
network_alias=${application}-${runner}

scripts/network.sh create

if [[ -z $($docker network ls | grep -e " $network_name ") ]]; then
  $docker network create $network_name
fi

exec $docker run --rm --name $container_name \
                      --network $network_name \
                      --network-alias $network_alias \
                      -e "APPLICATION=$application" \
                      -e "UID=$(id -u $USER)" \
                      -e "GID=$(id -g $USER)" \
                      -v $PWD:/$application \
                      -v maven-repo:/root/.m2/repository \
                      $application/$runner "$@"
