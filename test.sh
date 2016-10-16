#!/bin/bash
mvn=$(which mvn)

if [[ -z $mvn ]]; then
  echo "You need Maven to run this script"
  exit 1
fi

# TODO: Use getopts to mix flags
case "$1" in
  --no-sut)
    mvn_params="verify"
    ;;
  --coverage)
    mvn_params="-P setup-SUT,code-coverage cobertura:cobertura-integration-test"
    ;;
  --help)
    echo "Usage:"
    echo
    echo "$0 [--no-sut | --travis | --help]"
    echo
    echo "  --no-sut  : assume the system under test is already running"
    echo "  --travis  : as --setup but also runs code coverage analysis and reporting"
    echo "  --help    : shows this help text"
    echo
    echo "If no arguments are given, the tests are wrapped with starting and stopping of the system under test."
    echo
    exit 0
    ;;
  *)
    mvn_params="-P setup-SUT verify"
esac

exec $mvn $mvn_params
