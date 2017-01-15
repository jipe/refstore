#!/bin/bash
docker=$(which docker)

if [[ -z $docker ]]; then
  echo "You need Docker to run this script."
  exit 1
fi

application=refstore
root_dir=.

if [[ ! -d $root_dir/docker ]]; then
  root_dir=..
fi

for arg; do
  case $arg in
    -f|--force)
      force=true
      ;;
    --no-cache)
      docker_args="$docker_args --no-cache"
      ;;
    *)
      runners="$runners $arg"
      ;;
  esac
done

for runner in $runners; do
  image_name="$application/${runner}-runner"
  image_grep=$($docker images | grep -e "^$image_name ")
  if [[ $force ]] || [[ -z $image_grep ]]; then
    echo "Building runner '$runner' (image name: $image_name)"
    $docker build $docker_args -f $root_dir/docker/runners/$runner/Dockerfile -t $image_name $root_dir
  fi
done
