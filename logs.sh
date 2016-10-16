#!/bin/bash
if [[ -z $(which docker-compose) ]]; then
  echo "You need Docker Compose to run this script"
  exit 1
fi

docker_cmd='docker-compose logs -f'
cmd="sudo $docker_cmd"

for g in $(groups); do
  if [[ $g == 'docker' ]]; then
    cmd=$docker_cmd
  fi
done

cd docker
exec $cmd

