#!/bin/bash
cd docker
docker-compose build
docker-compose start 2>&1
