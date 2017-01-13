#!/bin/bash
docker=$(which docker)

if [[ -z $docker ]]; then
  echo "You need Docker to run this script."
  exit 1
fi

application=refstore
services=$(ls docker/services)
network_name=$(cat docker/network_name)

function usage {
  echo "$0 <build|run|stop|rm>"
  echo
  echo "  Actions:"
  echo
  echo "    build: Build Docker images for all buildable services"
  echo "      run: Run the service containers"
  echo "     stop: Stop the service containers"
  echo "       rm: Remove the service containers"
  echo
}

function is_buildable {
  echo $(ls $PWD/docker/services/$1/Dockerfile)
}

function build_images {
  for service in $services; do
    if [[ $(is_buildable $service) ]]; then
      echo "Building service '$service'"
      $docker build -t $application/$service \
                    -f $PWD/docker/services/$service/Dockerfile \
                    $PWD/docker/services/$service
    fi
  done
}

function run_containers {
  scripts/network.sh create

  for service in $services; do
    if [[ $(is_buildable $service) ]]; then
      image_name=${application}/${service}
    else
      image_name=$(cat $PWD/docker/services/$service/image_name)
    fi
    container_name=${application}_${service}

    if [[ -f "$PWD/docker/services/$service/docker_args" ]]; then
      docker_args=$(cat $PWD/docker/services/$service/docker_args)
    fi

    echo "Starting service '$service' (container name: $container_name)"
    $docker run -d -t --name $container_name \
                      --network $network_name \
                      --network-alias $service \
                      $docker_args \
                      $image_name > /dev/null
  done
}

function start_containers {
  for service in $services; do
    $container_name=${application}_${service}
    echo "Starting stopped service '$service' (container name: $container_name)"
    $docker start $container_name > /dev/null
  done
}

function stop_containers {
  for service in $services; do
    container_name=${application}_${service}
    if [[ $($docker ps | grep -e " $container_name$") ]]; then
      echo "Stopping service '$service' (container name: $container_name)"
      $docker stop $container_name >/dev/null
    fi
  done
}

function rm_containers {
  stop_containers
  for service in $services; do
    container_name=${application}_${service}
    if [[ $($docker ps -a | grep -e " $container_name$") ]]; then
      echo "Removing service '$service' (container name: $container_name)"
      $docker rm $container_name > /dev/null
    fi
  done
}

if [[ -z $@ ]]; then
  usage
  exit 1
fi

for arg; do
  case $arg in
    build)
      build_images
      ;;
    run)
      run_containers
      ;;
    stop)
      stop_containers
      ;;
    rm)
      rm_containers
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      ;;
  esac
done
