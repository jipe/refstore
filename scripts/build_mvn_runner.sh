#!/bin/bash
application=refstore

function usage {
  echo "$0 [options] : Build $application application"
  echo
  echo "  Options:"
  echo
  echo "    -h|--help : Show this help text"
  echo
}

function build {
  docker=$(which docker)

  if [[ -z $docker ]]; then
    echo "You need Docker to run this script."
    exit 1
  fi

  $docker build -f docker/mvn/Dockerfile -t $application/mvn-runner $PWD
}

for arg; do
  case $arg in
    -h|--help)
      usage
      ;;
    *)
      echo "Bad argument list: $@"
      echo
      usage
      exit 1
      ;;
  esac
done

build
