#!/bin/bash

case $1 in
  --help)
    echo "$0 [--help]"
    echo
    echo "Build and start Docker containers for running the packaged WAR file in a containerised environment"
    echo
    echo "  --help : show this help text"
    echo
    echo "When started, the web application is available at http://localhost:8080"
    echo
    exit 0
    ;;
  *)
esac

if [[ -z $(which docker-compose) ]]; then
  echo "You need Docker to run this script"
  exit 1
fi

docker_cmd='docker-compose up --build -d'
cmd="sudo $docker_cmd"
wget=$(which wget)

for g in $(groups); do
  if [[ $g == 'docker' ]]; then
    cmd=$docker_cmd
  fi
done

cd docker
$cmd
docker_return=$?

if [[ ! $docker_return -eq 0 ]]; then
  exit $docker_return
fi

exec $wget -qO /dev/null 'http://localhost:8080'
