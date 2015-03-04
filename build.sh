#!/bin/sh

echo docker run --rm -v $(pwd):/root -w /root pandeiro/lein sh -c 'lein clean && lein uberjar'
docker run --rm -v $(pwd):/root -w /root pandeiro/lein sh -c 'lein clean && lein uberjar'

UBERJAR="target/$(ls target/ | grep 'sprint-poker-.*-standalone.jar')"
echo Uberjar: $UBERJAR

echo cp $UBERJAR sprint-poker.jar
cp $UBERJAR sprint-poker.jar
