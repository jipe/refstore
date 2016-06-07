#!/bin/bash
cd docker
docker-compose build
exec docker-compose start 2>&1
