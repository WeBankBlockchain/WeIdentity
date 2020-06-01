#!/bin/bash
java_source_code_dir=$(pwd)

app_xml_config_tpl=${java_source_code_dir}/src/main/resources/fisco.properties.tpl
app_xml_config=${java_source_code_dir}/src/main/resources/fisco.properties
weid_config_tpl=${java_source_code_dir}/src/main/resources/weidentity.properties.tpl
weid_config=${java_source_code_dir}/src/main/resources/weidentity.properties
font=${java_source_code_dir}/src/main/resources/NotoSansCJKtc-Regular.ttf

export MYSQL_ADDRESS=${1:-0.0.0.0:3306}
export MYSQL_DATABASE=${2:-database}
export MYSQL_USERNAME=${3:-username}
export MYSQL_PASSWORD=${4:-password}

JAVA_OPTS='-Djdk.tls.namedGroups="secp256r1,secp256k1"'

function modify_config()
{
    echo "begin to modify sdk config..."

    hash=$(cat hash)
    export FISCO_BCOS_VERSION=${FISCO_BCOS_VERSION}
    export CNS_PROFILE_ACTIVE=${CNS_PROFILE_ACTIVE}
    export CNS_CONTRACT_FOLLOW=${hash}
    export CHAIN_ID=${CHAIN_ID}
    
    MYVARS='${FISCO_BCOS_VERSION}:${CNS_PROFILE_ACTIVE}:${CNS_CONTRACT_FOLLOW}:${CHAIN_ID}'
    envsubst ${MYVARS} < ${app_xml_config_tpl} >${app_xml_config}
    cp ${app_xml_config} ${java_source_code_dir}/src/test/resources/
    # cat $app_xml_config
    
    export ORG_ID=${ORG_ID}
    export MYSQL_ADDRESS=${MYSQL_ADDRESS}
    export MYSQL_DATABASE=${MYSQL_DATABASE}
    export MYSQL_USERNAME=${MYSQL_USERNAME}
    export MYSQL_PASSWORD=${MYSQL_PASSWORD}
    export BLOCKCHIAN_NODE_INFO=${BLOCKCHIAN_NODE_INFO}
    
    NODEVAR='${ORG_ID}:${MYSQL_ADDRESS}:${MYSQL_DATABASE}:${MYSQL_USERNAME}:${MYSQL_PASSWORD}:${BLOCKCHIAN_NODE_INFO}'
    envsubst ${NODEVAR} < ${weid_config_tpl} >${weid_config}
    cp ${weid_config} ${java_source_code_dir}/src/test/resources/

    if [ -e ${java_source_code_dir}/ecdsa_key ];then
        cp ${java_source_code_dir}/ecdsa_key ${java_source_code_dir}/src/test/resources/
    fi
    cp ${java_source_code_dir}/.ci/ca.crt ${java_source_code_dir}/src/test/resources
    cp ${java_source_code_dir}/.ci/node.crt ${java_source_code_dir}/src/test/resources
    cp ${java_source_code_dir}/.ci/node.key ${java_source_code_dir}/src/test/resources
	cp -r ${java_source_code_dir}/src/main/resources/WeDPR_dynamic_lib ${java_source_code_dir}/src/test/resources
    echo "modify sdk config finished..."
}


function gradle_build_sdk()
{
    #run gradle build
    cp ${java_source_code_dir}/.ci/ca.crt ${java_source_code_dir}/src/main/resources
    cp ${java_source_code_dir}/.ci/node.crt ${java_source_code_dir}/src/main/resources
    cp ${java_source_code_dir}/.ci/node.key ${java_source_code_dir}/src/main/resources
    cp ${java_source_code_dir}/dist/lib/NotoSansCJKtc-Regular.ttf ${java_source_code_dir}/src/main/resources
    cp ${java_source_code_dir}/dist/lib/NotoSansCJKtc-Regular.ttf ${java_source_code_dir}/src/test/resources
    
    export FISCO_BCOS_VERSION="2"
    export CNS_CONTRACT_FOLLOW=
    export CNS_PROFILE_ACTIVE="ci"
    export CHAIN_ID=101
    
    MYVARS='${FISCO_BCOS_VERSION}:${CNS_PROFILE_ACTIVE}:${CNS_CONTRACT_FOLLOW}:${CHAIN_ID}'
    envsubst ${MYVARS} < ${app_xml_config_tpl} >${app_xml_config}
    
    
    content="$NODE_IP"
    #content="0.0.0.0:8900"
    export BLOCKCHIAN_NODE_INFO=${content}
    export ORG_ID="webank_ci"
    echo $MYSQL_ADDRESS
    echo $MYSQL_DATABASE
    echo $MYSQL_USERNAME
    echo $MYSQL_PASSWORD
    NODEVAR='${ORG_ID}:${MYSQL_ADDRESS}:${MYSQL_DATABASE}:${MYSQL_USERNAME}:${MYSQL_PASSWORD}:${BLOCKCHIAN_NODE_INFO}'
    envsubst ${NODEVAR} < ${weid_config_tpl} >${weid_config}

    echo "Begin to compile java code......"
    if [ -d ${java_source_code_dir}/dist ]; then
        rm -rf ${java_source_code_dir}/dist
    fi
    gradle clean build -x test
    echo "compile java code done."
}

function check()
{
    echo "begin check-info..."
    chmod u+x check-info.sh
    ./check-info.sh
    echo "check-info done."
}

function deploy_contract()
{

    CLASSPATH=${java_source_code_dir}/dist/conf
    echo "begin to deploy contract..."
    for jar_file in ${java_source_code_dir}/dist/lib/*.jar
    do
        CLASSPATH=${CLASSPATH}:${jar_file}
    done
    for jar_file in ${java_source_code_dir}/dist/app/*.jar
    do
        CLASSPATH=${CLASSPATH}:${jar_file}
    done

    java ${JAVA_OPTS} -cp "$CLASSPATH" com.webank.weid.contract.deploy.DeployContract
    echo "contract deployment done."
}

function  install_font()
{
    sudo mkdir -p /usr/share/fonts/custom&&
    sudo cp ${font} /usr/share/fonts/custom/&&
    sudo apt install xfonts-utils -y&&
    sudo mkfontscale&&
    sudo mkfontdir&&
    sudo fc-cache -fv
    echo "font install done."
}

function main()
{
    gradle_build_sdk
    install_font
    check
    deploy_contract
    modify_config
}

main
