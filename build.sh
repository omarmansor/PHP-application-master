#!/bin/bash

sudo docker ps | xargs sudo docker rm -f
sudo docker system prune
sudo docker build -t omarmansor/phpwebsite:$BUILD_NUMBER .
sudo docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
sudo docker push omarmansor/phpwebsite:$BUILD_NUMBER
sudo docker run -itd -p 8080:80 omarmansor/phpwebsite:$BUILD_NUMBER