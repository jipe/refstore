#!/bin/bash
mvn -B -f /${APPLICATION}/pom.xml "$@"
exit_code=$?
chown -R ${UID}.${GID} /${APPLICATION}/target
exit $exit_code
