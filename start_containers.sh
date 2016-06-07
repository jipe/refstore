#!/bin/bash
cd docker
sudo docker-compose build
sudo docker-compose start 2>&1
