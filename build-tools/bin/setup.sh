#!/bin/bash

java_source_code_dir=$2
config_file=${java_source_code_dir}/dist/bin/run.config
app_xml_config_dir=${java_source_code_dir}/dist/conf/
app_xml_config_tpl=${java_source_code_dir}/src/main/resources/fisco.properties.tpl
app_xml_config=${java_source_code_dir}/src/main/resources/fisco.properties
weid_config_tpl=${java_source_code_dir}/src/main/resources/weidentity.properties.tpl
weid_config=${java_source_code_dir}/src/main/resources/weidentity.properties

JAVA_OPTS='-Djdk.tls.namedGroups="secp256k1"'

CLASSPATH=${java_source_code_dir}/dist/conf

for jar_file in ${java_source_code_dir}/dist/lib/*.jar
do
CLASSPATH=${CLASSPATH}:${jar_file}
done


function check_jdk()
{
    # Determine the Java command to use to start the JVM.
    if [ -n "$JAVA_HOME" ] ; then
        if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
            # IBM's JDK on AIX uses strange locations for the executables
            JAVACMD="$JAVA_HOME/jre/sh/java"
        else
            JAVACMD="$JAVA_HOME/bin/java"
		fi
    if [ ! -x "$JAVACMD" ] ; then
        echo "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME
             Please set the JAVA_HOME variable in your environment to match the
             location of your Java installation."
    fi
    else
        JAVACMD="java"
        which java >/dev/null 2>&1 || echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

    Please set the JAVA_HOME variable in your environment to match the
    location of your Java installation."
    fi
}



function replace_java_contract()
{
    #override new java contract code
    cd ${java_source_code_dir}/
    cp -r dist/output/com src/main/java/

}

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
    export FISCO_BCOS_VERSION=${bcos_version}
    MYVARS='${WEID_ADDRESS}:${CPT_ADDRESS}:${ISSUER_ADDRESS}:${EVIDENCE_ADDRESS}:${SPECIFICISSUER_ADDRESS}:${FISCO_BCOS_VERSION}'
    envsubst ${MYVARS} < ${app_xml_config_tpl} >${app_xml_config}
    cp ${app_xml_config} ${app_xml_config_dir}
    NODEVAR='${BLOCKCHIAN_NODE_INFO}'
    envsubst ${NODEVAR} < ${weid_config_tpl} >${weid_config}
    cp ${weid_config} ${app_xml_config_dir}
    echo "modify sdk config finished..."
}

function clean_config()
{
    echo "begin to clean config..."
    cd ${java_source_code_dir}/dist
    if [ -d bin/ ];then
    	rm -rf bin/
    fi
    if [ -d contracts/ ];then
    	rm -rf contracts/
    fi
    if [ -d output/ ];then
    	rm -rf output/
    fi
    if [ -f ${app_xml_config} ];then
	rm -f ${app_xml_config}
    fi
    
    cd ${java_source_code_dir}
    if [ -f weIdContract.address ];then
        rm -f weIdContract.address
    fi
    if [ -f cptController.address ];then
        rm -f cptController.address
    fi
    if [ -f authorityIssuer.address ];then
        rm -f authorityIssuer.address
    fi
    if [ -f evidenceController.address ];then
        rm -f evidenceController.address
    fi
    if [ -f specificIssuer.address ];then
        rm -f specificIssuer.address
    fi
    echo "clean finished..."
}

function gradle_build_sdk()
{
    #run gradle build
    echo "Begin to compile java code......"
	
	node_addr=$(grep "blockchain_address" $config_file |awk -F"=" '{print $2}')
	bcos_version=$(grep "blockchain_fiscobcos_version" $config_file |awk -F"=" '{print $2}')
    OLD_IFS="$IFS"
    IFS=","
    array=($node_addr)
    IFS="$OLD_IFS"
	content=
    for var in ${array[@]}
    do
	    if [[ $bcos_version == 1 ]];
	    then
	    	content="${content}WeIdentity@$var,"
	    else
	    	content="${content}$var,"
	    fi
      
    done
    
    if [[ $bcos_version == 1 ]];
    then
        JAVA_OPTS=''
    fi
    
	export BLOCKCHIAN_NODE_INFO=$(echo -e ${content})
	export WEID_ADDRESS="0x0"
    export CPT_ADDRESS="0x0"
    export ISSUER_ADDRESS="0x0"
    export EVIDENCE_ADDRESS="0x0"
    export SPECIFICISSUER_ADDRESS="0x0"
    export FISCO_BCOS_VERSION=${bcos_version}
    MYVARS='${WEID_ADDRESS}:${CPT_ADDRESS}:${ISSUER_ADDRESS}:${EVIDENCE_ADDRESS}:${SPECIFICISSUER_ADDRESS}:${FISCO_BCOS_VERSION}'
    envsubst ${MYVARS} < ${app_xml_config_tpl} >${app_xml_config}
    NODEVAR='${BLOCKCHIAN_NODE_INFO}'
    envsubst ${NODEVAR} < ${weid_config_tpl} >${weid_config}
	
    cd ${java_source_code_dir}/
    if [ -d dist/ ];then
        rm -rf dist/app
        rm -rf dist/conf
        rm -rf dist/*.jar
    fi
    gradle clean build -x checkMain -x checkTest -x spotbugsMain -x spotbugsTest -x test
    echo "compile java code done."
}

function deploy_contract()
{
    echo "begin to deploy contract..."

	for jar_file in ${java_source_code_dir}/dist/app/*.jar
	do
	CLASSPATH=${CLASSPATH}:${jar_file}
	done

    java ${JAVA_OPTS} -cp "$CLASSPATH" com.webank.weid.contract.deploy.DeployContract
    echo "contract deployment done."
}

function main()
{
    # compile_contract ${1} ${2} ../output/
    # replace_java_contract
    gradle_build_sdk
    deploy_contract
    modify_config	
    clean_config
}

main
