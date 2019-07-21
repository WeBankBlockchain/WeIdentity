#!/bin/bash
java_source_code_dir=$(pwd)

app_xml_config_tpl=${java_source_code_dir}/src/main/resources/fisco.properties.tpl
app_xml_config=${java_source_code_dir}/src/main/resources/fisco.properties
weid_config_tpl=${java_source_code_dir}/src/main/resources/weidentity.properties.tpl
weid_config=${java_source_code_dir}/src/main/resources/weidentity.properties

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
    MYVARS='${WEID_ADDRESS}:${CPT_ADDRESS}:${ISSUER_ADDRESS}:${EVIDENCE_ADDRESS}:${SPECIFICISSUER_ADDRESS}'
    envsubst ${MYVARS} < ${app_xml_config_tpl} >${app_xml_config}
    cp ${app_xml_config} ${java_source_code_dir}/src/test/resources/
    NODEVAR='${BLOCKCHIAN_NODE_INFO}'
    envsubst ${NODEVAR} < ${weid_config_tpl} >${weid_config}
    cp ${weid_config} ${java_source_code_dir}/src/test/resources/
    if [ -e ${java_source_code_dir}/ecdsa_key ];then
        cp ${java_source_code_dir}/ecdsa_key ${java_source_code_dir}/src/test/resources/
    fi
    #cat $app_xml_config
    echo "modify sdk config finished..."
}


function gradle_build_sdk()
{
    #run gradle build
 	cp ${java_source_code_dir}/ci/ca.crt ${java_source_code_dir}/src/main/resources
    cp ${java_source_code_dir}/ci/client.keystore ${java_source_code_dir}/src/main/resources
    content="WeIdentity@$NODE_IP"
    export BLOCKCHIAN_NODE_INFO=${content}
    export WEID_ADDRESS="0x0"
    export CPT_ADDRESS="0x0"
    export ISSUER_ADDRESS="0x0"
    export EVIDENCE_ADDRESS="0x0"
    export SPECIFICISSUER_ADDRESS="0x0"
    export FISCO_BCOS_VERSION="1"
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

function main()
{
    gradle_build_sdk
    deploy_contract
    modify_config
}

main
