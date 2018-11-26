#!/bin/bash

SOLC=$(which fisco-solc)
WEB3J="../bin/web3sdk.sh"
java_source_code_dir=$2
temp_file=$(date +%s)".temp"
config_file=${java_source_code_dir}dist/bin/run.config
app_xml_config=${java_source_code_dir}dist/conf/applicationContext.xml

cd $2
APP_HOME=$(pwd)
cd -
CLASSPATH=$APP_HOME/dist/conf

for f in $APP_HOME/dist/lib/*.jar
do
CLASSPATH=$CLASSPATH:$f
done

for f in $APP_HOME/dist/app/*.jar
do
CLASSPATH=$CLASSPATH:$f
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
        which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

    Please set the JAVA_HOME variable in your environment to match the
    location of your Java installation."
    fi
}


function compile_contract() 
{ 
    cd ../contracts/
    
    package="com.webank.weid.contract"
    output_dir="${java_source_code_dir}dist/output"
    echo "output_dir is $output_dir"
    local files=$(ls ./*.sol)
    for itemfile in ${files}
    do
        local item=$(basename ${itemfile} ".sol")
        ${SOLC} --abi --bin -o ${output_dir} ${itemfile}
        echo "${output_dir}/${item}.bin, ${output_dir}, ${package} "
        ${WEB3J} solidity generate  ${output_dir}"/"${item}".bin" ${output_dir}"/"${item}".abi" -o ${output_dir} -p ${package} 
    done
}


function replace_java_contract()
{
    #override new java contract code
    cd ${java_source_code_dir}
    cp -r dist/output/com src/main/java/

}

function modify_config()
{
    echo "begin to modify sdk config..."
    cd ${java_source_code_dir}dist
    weid_address=$(grep "WeIDContract" $temp_file |awk -F"=" '{print $2}')
    sed -i "s/WEID_ADDRESS/$weid_address/g" $app_xml_config
    cpt_address=$(grep "CptController" $temp_file |awk -F"=" '{print $2}')
    sed -i "s/CPT_ADDRESS/$cpt_address/g" $app_xml_config
    issuer_address=$(grep "authorityIssuerController" $temp_file |awk -F"=" '{print $2}')
    sed -i "s/ISSUER_ADDRESS/$issuer_address/g" $app_xml_config
    
    rm -f $temp_file
    echo "modify sdk config finished..."
}

function clean_config()
{
    echo "begin to clean config..."
    cd ${java_source_code_dir}dist
    if [ -d bin/ ];then
    	rm -rf bin/
    fi
    if [ -d contracts/ ];then
    	rm -rf contracts/
    fi
    if [ -d output/ ];then
    	rm -rf output/
    fi
    echo "clean finished..."
}

function gradle_build_sdk()
{
    #run gradle build
    echo "Begin to compile java code......"
    cd ${java_source_code_dir}
    gradle clean build -x test
    echo "compile java code done."
}

function deploy_contract()
{
    echo "begin to deploy contract..."
    cd ${java_source_code_dir}dist
    
    node_addr=$(grep "blockchain.node.address" $config_file |awk -F"=" '{print $2}')
    OLD_IFS="$IFS"
    IFS=","
    array=($node_addr)
    IFS="$OLD_IFS"
 
    for var in ${array[@]}
    do
        content="<value>WeIdentity@"$var"</value>"
        sed -i "/BLOCKCHIAN_NODE_INFO/a${content}" $app_xml_config 
    done
    sed -i '/BLOCKCHIAN_NODE_INFO/d' $app_xml_config
    #java deploy interface
    #temp_file=`date +%s`".temp"
    java -cp "$CLASSPATH" com.webank.weid.contract.deploy.DeployContract ${temp_file}
    dos2unix ${temp_file}
    echo "contract deployment done."
        
}

function main()
{
    compile_contract ${1} ${2} ../output/
    replace_java_contract
    gradle_build_sdk
    deploy_contract
    modify_config	
    clean_config
}

main
