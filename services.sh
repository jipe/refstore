#!/bin/bash
application="refstore"
services="mq db"

function usage {
  echo "$0 <build|start|stop|remove>"
  echo
  echo "  Actions:"
  echo
  echo "    build: Build Docker images for all services"
  echo "    start: Run the service containers"
  echo "     stop: Stop the service containers"
  echo "   remove: Remove the service containers"
  echo
}

function build_images {
  for service in ${services}; do
    $docker build -t ${application}/${service} -f ${PWD}/docker/${service}/Dockerfile $PWD
  done
}

function start_containers {
  $docker network create ${application}
  for service in ${services}; do
    $docker run -d --name ${application}_${service} --network $application --network-alias $service ${application}/${service}
  done
}

function stop_containers {
  for service in ${services}; do
    $docker stop ${application}_${service}
  done
}

function remove_containers {
  for service in ${services}; do
    $docker rm refstore_${service}
  done
  $docker network rm $application
}

docker=$(which docker)

if [[ -z $docker ]]; then
  echo "You need Docker to run this script."
  exit 1
fi

if [[ -z $@ ]]; then
  usage
  exit 1
fi

for arg; do
  case $arg in
    build)
      build_images
      ;;
    start)
      start_containers
      ;;
    stop)
      stop_containers
      ;;
    remove)
      remove_containers
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      ;;
  esac
done
