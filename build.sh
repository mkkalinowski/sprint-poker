#!/bin/sh

# Build the uberjar that will be included in the constructed image.
echo docker run --rm -v $(pwd):/root -w /root pandeiro/lein sh -c 'lein clean && lein uberjar'
docker run --rm -v $(pwd):/root -w /root pandeiro/lein sh -c 'lein clean && lein uberjar'

# Figure out the path to the uberjar.
UBERJAR="target/$(ls target/ | grep 'sprint-poker-.*-standalone.jar')"
echo Uberjar: $UBERJAR

# Copy to the path the Dockerfile expects.
echo cp $UBERJAR sprint-poker.jar
cp $UBERJAR sprint-poker.jar

# Build the image.
docker build -t tokenshift/sprint-poker ./
