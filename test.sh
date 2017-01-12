#!/bin/bash
exec scripts/mvn.sh -P code-coverage clean cobertura:cobertura-integration-test
