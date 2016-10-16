#!/bin/bash
docker_cmd='docker-compose down --remove-orphans'
cmd="sudo $docker_cmd"

for g in $(groups); do
  if [ $g == 'docker' ]; then
    cmd=$docker_cmd
  fi
done

cd docker
exec $cmd 2>&1