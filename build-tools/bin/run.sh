#!/bin/bash

current_path=$(pwd)
source_code_dir=$current_path"/../.."
echo $source_code_dir
cd $source_code_dir

if [ -d dist/ ];then
    rm -rf dist/
fi

gradle clean build -x checkMain -x checkTest -x spotbugsMain -x spotbugsTest -x test

cp -r ./build-tools/* dist/

cd dist/bin
chmod u+x ./*.sh
./setup.sh "com.webank.weid.contract" "$source_code_dir"


