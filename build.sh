#!/bin/bash
application=refstore

function usage {
  echo "$0 [options] : Build $application application"
  echo
  echo "  Options:"
  echo
  echo "  -d|--docker : Build application using Docker"
  echo "    -h|--help : Show this help text"
  echo
}

function build_locally {
  mvn=$(which mvn)

  if [[ -z $mvn ]]; then
    echo "You need Maven to run this script."
    exit 1
  fi

  $mvn -B clean package
}

function build_in_docker {
  docker=$(which docker)

  if [[ -z $docker ]]; then
    echo "You need Docker to run this script."
    exit 1
  fi

  $docker build -f docker/builder/Dockerfile -t ${application}/builder $PWD
  $docker run --rm -e "UID=$(id -u $USER)" \
                   -e "GID=$(id -g $USER)" \
                   -e "APPLICATION=${application}" \
                   -v ${PWD}:/${application} \
                   -v maven-repo:/root/.m2/repository \
                   ${application}/builder clean package
}

for arg; do
  case $arg in
    -d|--docker)
      docker=1
      ;;
    -h|--help)
      help=1
      ;;
    *)
      ;;
  esac
done

if [[ ! -z $help ]]; then
  usage
  exit 0
fi

if [[ -z $docker ]]; then
  build_locally
else
  build_in_docker
fi

