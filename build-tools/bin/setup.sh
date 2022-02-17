#!/bin/bash

source run.config

java_source_code_dir=$2
config_file=${java_source_code_dir}/dist/bin/run.config
app_xml_config_dir=${java_source_code_dir}/dist/conf/
app_xml_config_tpl=${java_source_code_dir}/src/main/resources/fisco.properties.tpl
app_xml_config=${java_source_code_dir}/src/main/resources/fisco.properties
weid_config_tpl=${java_source_code_dir}/src/main/resources/weidentity.properties.tpl
weid_config=${java_source_code_dir}/src/main/resources/weidentity.properties

JAVA_OPTS='-Djdk.tls.namedGroups="secp256r1,secp256k1"'

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
    hash=$(cat hash)
    export FISCO_BCOS_VERSION=${FISCO_BCOS_VERSION}
    export CNS_PROFILE_ACTIVE=${CNS_PROFILE_ACTIVE}
    
    MYVARS='${FISCO_BCOS_VERSION}:${CNS_PROFILE_ACTIVE}'
    envsubst ${MYVARS} < ${app_xml_config_tpl} >${app_xml_config}
    cp ${app_xml_config} ${app_xml_config_dir}
    
    export ORG_ID=${ORG_ID}
    export AMOP_ID=${AMOP_ID}
    export PERSISTENCE_TYPE=${persistence_type}
    export MYSQL_ADDRESS=${MYSQL_ADDRESS}
    export MYSQL_DATABASE=${MYSQL_DATABASE}
    export MYSQL_USERNAME=${MYSQL_USERNAME}
    export MYSQL_PASSWORD=${MYSQL_PASSWORD}
    export BLOCKCHIAN_NODE_INFO=${BLOCKCHIAN_NODE_INFO}
    
    NODEVAR='${ORG_ID}:${AMOP_ID}:${PERSISTENCE_TYPE}:${MYSQL_ADDRESS}:${MYSQL_DATABASE}:${MYSQL_USERNAME}:${MYSQL_PASSWORD}:${BLOCKCHIAN_NODE_INFO}'
    envsubst ${NODEVAR} < ${weid_config_tpl} >${weid_config}
    cp ${weid_config} ${app_xml_config_dir}
    echo "modify sdk config finished..."
}

function clean_config()
{
    echo "begin to clean config..."
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
    if [ -f hash ];then
        rm -f hash
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

	export BLOCKCHIAN_NODE_INFO=$(echo -e ${content})
    export FISCO_BCOS_VERSION=${bcos_version}
    export CNS_PROFILE_ACTIVE=${cns_profile_active}
    
    MYVARS='${FISCO_BCOS_VERSION}:${CNS_PROFILE_ACTIVE}:'
    envsubst ${MYVARS} < ${app_xml_config_tpl} >${app_xml_config}
    
    export ORG_ID=${org_id}
    export AMOP_ID=${amop_id}
    export PERSISTENCE_TYPE=${persistence_type}
    export MYSQL_ADDRESS=${mysql_address}
    export MYSQL_DATABASE=${mysql_database}
    export MYSQL_USERNAME=${mysql_username}
    export MYSQL_PASSWORD=${mysql_password}
    NODEVAR='${ORG_ID}:${AMOP_ID}:${PERSISTENCE_TYPE}:${MYSQL_ADDRESS}:${MYSQL_DATABASE}:${MYSQL_USERNAME}:${MYSQL_PASSWORD}:${BLOCKCHIAN_NODE_INFO}'
    envsubst ${NODEVAR} < ${weid_config_tpl} >${weid_config}
	
    cd ${java_source_code_dir}/
    if [ -d dist/ ];then
        rm -rf dist/app
        rm -rf dist/conf
        rm -rf dist/*.jar
    fi
    ./gradlew clean build -x checkMain -x checkTest -x spotbugsMain -x spotbugsTest -x test
    echo "compile java code done."
}

function deploy_contract()
{
    echo "begin to deploy contract..."

	for jar_file in ${java_source_code_dir}/dist/app/*.jar
	do
	CLASSPATH=${CLASSPATH}:${jar_file}
	done

    java ${JAVA_OPTS} -cp "$CLASSPATH" com.webank.weid.contract.deploy.DeployContract ${chain_id} $1
    echo "contract deployment done."
}

function main()
{
    # compile_contract ${1} ${2} ../output/
    # replace_java_contract
    gradle_build_sdk
    deploy_contract $3
    modify_config	
    clean_config
}

main $@
