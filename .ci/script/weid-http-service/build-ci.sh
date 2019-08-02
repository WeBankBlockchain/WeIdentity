#!/usr/bin/env sh

if [ "$TRAVIS_BRANCH" = "master" ];then
    echo "This is a master branch PR, starting Http Service CI pipeline.."
    chmod u+x .ci/script/build-ci.sh
    .ci/script/build-ci.sh

    # clone repo
    rm -rf weid-http-service/
    git clone https://github.com/WeBankFinTech/weid-http-service.git

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

    # copy SDK jar and libs to repo dependencies path and rename
    cp dist/app/$FILENAME weid-http-service/dependencies/weid-java-sdk-pipeline.jar
    mkdir -p weid-http-service/dist/lib
    cp dist/lib/* weid-http-service/dist/lib/

    # copy config files
    cp ecdsa_key weid-http-service/src/main/resources/
    cp ecdsa_key weid-http-service/src/test/resources/
    cp ecdsa_key weid-http-service/keys/priv/
    cp src/main/resources/* weid-http-service/src/main/resources/
    cp src/main/resources/* weid-http-service/src/test/resources/
    cp .ci/ca.crt weid-http-service/src/test/resources/
    cp .ci/ca.crt weid-http-service/src/main/resources/
    cp .ci/client.keystore weid-http-service/src/test/resources/
    cp .ci/client.keystore weid-http-service/src/main/resources/

    # run repo ci scripts
    cd weid-http-service/
    gradle build
else
    echo "This is not a master branch PR (commit omitted). Http Service CI skipped."
fi