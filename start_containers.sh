#!/bin/bash
docker_cmd='docker-compose up --build -d'
cmd="sudo $docker_cmd"
wget=$(which wget)

for g in $(groups); do
  if [ $g == 'docker' ]; then
    cmd=$docker_cmd
  fi
done

cd docker
$cmd
docker_return=$?

if [ ! $docker_return -eq 0 ]; then
  exit $docker_return
fi

# Wait for controller availability
retries=1
until $($wget -qO /dev/null 'http://localhost:8080'); do
  sleep 0.1
  ((retries -= 1 ))
  if ((retries == 0)); then
    echo "Controller did not become available withing 10 seconds"
    exit 1
  fi
done
