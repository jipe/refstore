#!/bin/bash
application="refstore"

function usage {
  echo "$0 options : Run $application tests"
  echo
  echo "  Options:"
  echo
  echo "   -d|--docker : Run tests in Docker"
  echo "   -h|--help   : Show this help text"
  echo
}

function test_locally {
  mvn=$(which mvn)
  
  if [[ -z $mvn ]]; then
    echo "You need Maven to run this script."
    exit 1
  fi

  exec $mvn -B cobertura:cobertura-integration-test
}

function test_in_docker {
  docker=$(which docker)

  if [[ -z $docker ]]; then
    echo "You need Docker to run this script."
    exit 1
  fi

  $docker build -t ${application}/tester -f ${PWD}/docker/tester/Dockerfile $PWD
  exec $docker run --rm -e "UID=$(id -u $USER)" -e "GID=$(id -g $USER)" -v ${PWD}:/${application} -v maven-repo:/root/.m2/repository ${application}/tester
}

for arg; do
  case $arg in
    -d|--docker)
      docker=1
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      usage
      exit 1
      ;;
  esac
done

if [[ -z $docker ]]; then
  test_locally
else
  test_in_docker
fi
