#!/bin/bash

if [ -n "$1" ] ;then
    sed -i "/^repoType/crepoType=$1" gradle.properties
fi

#build
chmod u+x gradlew

./gradlew clean build