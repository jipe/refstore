#!/bin/bash
mvn -B -f /refstore/pom.xml "$@"
exit_code=$?
chown -R ${UID}.${GID} /refstore/target
exit $exit_code
