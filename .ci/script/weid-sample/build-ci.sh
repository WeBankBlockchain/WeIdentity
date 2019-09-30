#!/usr/bin/env sh

if [ "$TRAVIS_BRANCH" = "master" ];then
    echo "This is a master branch PR, starting Sample CI pipeline.."
    chmod u+x .ci/script/build-ci.sh
    .ci/script/build-ci.sh

    # clone repo
    rm -rf weid-sample/
    git clone https://github.com/WeBankFinTech/weid-sample.git
    cd weid-sample/
    git checkout develop
    cd ..

    # construct SDK jar version and file name
    cat build.gradle | grep "version =" > temp.ver
    sed -e "s/version = \"//g" -i temp.ver
    sed -e "s/\"//g" -i temp.ver
    SDKVER=$(cat temp.ver)
    rm temp.ver
    SDKNAME='weid-java-sdk-'
    JAR='.jar'
    FILENAME="$SDKNAME$SDKVER$JAR"
    echo sdk jar filename: $FILENAME

    # copy SDK jar to repo dependencies path and rename
    # requires repo to allow local dep first
    mkdir -p weid-sample/dependencies
    cp dist/app/$FILENAME weid-sample/dependencies/weid-java-sdk-pipeline.jar
    mkdir -p weid-sample/libs
    cp dist/lib/* weid-sample/libs

    # copy config files
    cp ecdsa_key weid-sample/keys/priv/
    cp src/main/resources/fisco.properties weid-sample/src/main/resources/
    cp src/main/resources/weidentity.properties weid-sample/src/main/resources/
    cp .ci/ca.crt weid-sample/src/main/resources/
    cp .ci/node.crt weid-sample/src/main/resources/
    cp .ci/node.key weid-sample/src/main/resources/

    # run repo ci scripts
    cd weid-sample/
    chmod u+x *.sh
    ./build.sh
    
    if ! ./sample-ci.sh
    then
        echo "execute sample-ci failed."
        exit 1
    fi
    
    echo "execute sample-ci success."
    #gradle build
else
    echo "This is not a master branch PR (commit omitted). CI skipped."
fi