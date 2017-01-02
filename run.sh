#!/bin/bash
function run_locally {
}

function run_in_docker {
}

for arg; do
  case arg in
    -d|--docker)
      docker=1
      ;;
    *)
      ;;
  esac
done

if [[ -z $docker ]]; then
  run_locally
else
  run_in_docker
fi
