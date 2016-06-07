#!/bin/bash
cd docker
sudo docker-compose build
exec sudo docker-compose start 2>&1
