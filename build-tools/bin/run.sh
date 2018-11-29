#!/bin/bash

current_path=$(pwd)
source_code_dir=$current_path"/../.."
echo $source_code_dir
chmod 755 web3sdk.sh
cd $source_code_dir

if [ -d dist/ ];then
    rm -rf dist/
fi

gradle clean build -x test

cp -r ./build-tools/* dist/

cd dist/bin
chmod +x ./*.sh
./setup.sh "com.webank.weid.contract" "$source_code_dir"


