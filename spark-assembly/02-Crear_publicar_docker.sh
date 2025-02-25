#!/usr/bin/env bash

# Remove previous docker image
docker rm -f eoi.de/sparkeoi:latest

cd ..

# Build the docker image
sbt docker:publishLocal

docker images | grep spark-eoi

# La ejecutamos para comprobar si esta todo bien
docker run -d --name spark-eoi eoi.de/sparkeoi:latest

cd -