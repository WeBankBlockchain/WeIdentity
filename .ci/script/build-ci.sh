#!/bin/bash
java_source_code_dir=$(pwd)

app_xml_config_tpl=${java_source_code_dir}/src/main/resources/fisco.properties.tpl
app_xml_config=${java_source_code_dir}/src/main/resources/fisco.properties
weid_config_tpl=${java_source_code_dir}/src/main/resources/weidentity.properties.tpl
weid_config=${java_source_code_dir}/src/main/resources/weidentity.properties
font=${java_source_code_dir}/src/test/resources/NotoSansCJKtc-Regular.ttf

function modify_config()
{
    echo "begin to modify sdk config..."
    weid_address=$(cat weIdContract.address)
    cpt_address=$(cat cptController.address)
    issuer_address=$(cat authorityIssuer.address)
    evidence_address=$(cat evidenceController.address)
    specificissuer_address=$(cat specificIssuer.address)
    export WEID_ADDRESS=${weid_address}
    export CPT_ADDRESS=${cpt_address}
    export ISSUER_ADDRESS=${issuer_address}
    export EVIDENCE_ADDRESS=${evidence_address}
    export SPECIFICISSUER_ADDRESS=${specificissuer_address}
    export FISCO_BCOS_VERSION="2"
    MYVARS='${WEID_ADDRESS}:${CPT_ADDRESS}:${ISSUER_ADDRESS}:${EVIDENCE_ADDRESS}:${SPECIFICISSUER_ADDRESS}:${FISCO_BCOS_VERSION}'
    envsubst ${MYVARS} < ${app_xml_config_tpl} >${app_xml_config}
    cp ${app_xml_config} ${java_source_code_dir}/src/test/resources/
    NODEVAR='${BLOCKCHIAN_NODE_INFO}'
    envsubst ${NODEVAR} < ${weid_config_tpl} >${weid_config}
    cp ${weid_config} ${java_source_code_dir}/src/test/resources/
    if [ -e ${java_source_code_dir}/ecdsa_key ];then
        cp ${java_source_code_dir}/ecdsa_key ${java_source_code_dir}/src/test/resources/
    fi
    #cat $app_xml_config
    cp ${java_source_code_dir}/.ci/ca.crt ${java_source_code_dir}/src/test/resources
    cp ${java_source_code_dir}/.ci/node.crt ${java_source_code_dir}/src/test/resources
    cp ${java_source_code_dir}/.ci/node.key ${java_source_code_dir}/src/test/resources
    echo "modify sdk config finished..."
}


function gradle_build_sdk()
{
    #run gradle build
    cp ${java_source_code_dir}/.ci/ca.crt ${java_source_code_dir}/src/main/resources
    cp ${java_source_code_dir}/.ci/node.crt ${java_source_code_dir}/src/main/resources
    cp ${java_source_code_dir}/.ci/node.key ${java_source_code_dir}/src/main/resources
    content="$NODE_IP"
    export BLOCKCHIAN_NODE_INFO=${content}
    export WEID_ADDRESS="0x0"
    export CPT_ADDRESS="0x0"
    export ISSUER_ADDRESS="0x0"
    export EVIDENCE_ADDRESS="0x0"
    export SPECIFICISSUER_ADDRESS="0x0"
    export FISCO_BCOS_VERSION="2"
    MYVARS='${WEID_ADDRESS}:${CPT_ADDRESS}:${ISSUER_ADDRESS}:${EVIDENCE_ADDRESS}:${SPECIFICISSUER_ADDRESS}:${FISCO_BCOS_VERSION}'
    envsubst ${MYVARS} < ${app_xml_config_tpl} >${app_xml_config}
    NODEVAR='${BLOCKCHIAN_NODE_INFO}'
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

    java -cp "$CLASSPATH" com.webank.weid.contract.deploy.DeployContract
    echo "contract deployment done."
}

function  install_font()
{
    sudo mkdir -p /usr/share/fonts/custom&&
    sudo cp ${font}  /usr/share/fonts/custom/&&
    sudo apt install xfonts-utils -y&&
    sudo mkfontscale&&
    sudo mkfontdir&&
    sudo fc-cache -fv
    echo "font install done."
}

function main()
{
    install_font
    gradle_build_sdk
    check
    deploy_contract
    modify_config
}

main
