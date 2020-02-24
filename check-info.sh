#!/bin/bash

JAVA_OPTS='-Djdk.tls.namedGroups="secp256r1,secp256k1"'
classpathDir="./dist/conf"
libDir="./dist/lib"
set -- `getopt c:l: "$@"`
while [ -n "$1" ]
do
    case "$1" in 
     -c) 
     	 classpathDir=$2 
         shift ;;
     -l) 
		 libDir=$2
         shift ;;
    esac
    shift
done

# check the command is exists
function checkCommand(){
    if type $1 >/dev/null 2>&1
    then
       return 1
    else
       return 0
    fi
}

# check the os version
function check_os_version() {
	echo "----------------------------"
	echo "1. OS Version"
	echo "----------------------------"
	checkCommand uname
	if [ $? == 1 ] ; then
		uname -a
	else
		echo "ERROR: no 'uname' command could be found in your OS."
	fi
}

# check the jdk version
function check_jdk_version() {
	echo "----------------------------"
	echo "2. JDK Version"
	echo "----------------------------"
	checkCommand java
	if [ $? == 1 ] ; then
		java -version
	else
		echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
	fi
}

# check the gradle version
function check_gradle_version() {
	echo "----------------------------"
	echo "3. Gradle Version"
	echo "----------------------------"
	checkCommand gradle
	if [ $? == 1 ] ; then
		gradle -version
	else
		echo "WARN: GRADLE_HOME is not set and no 'gradle' command could be found in your PATH.
Please set the GRADLE_HOME variable in your environment to match the
location of your Gradle installation."
	fi
}

# check the fisco-solc version
function check_fisco_solc_version() {
	echo "----------------------------"
	echo "4. FISCO-SOLC Version"
	echo "----------------------------"
	checkCommand fisco-solc
	if [ $? == 1 ] ; then
		fisco-solc --version
	else
		echo "WARN: no 'fisco-solc' command could be found in your PATH."
	fi
}

# check the os version
check_os_version
	
# check the jdk version
check_jdk_version
	
# check the gradle version
check_gradle_version

# check the fisco-solc version
check_fisco_solc_version

if [ ! -d "$classpathDir" ];
then
	echo "ERROR: you need use -c to specify your right classpath."
	exit 0
fi
if [ ! -d "$libDir" ];
then
	echo "ERROR: you need use -l to specify your libDir."
	exit 0
fi


fisco_properties="$classpathDir/fisco.properties"
weidentity_properties="$classpathDir/weidentity.properties"
ca_crt="$classpathDir/ca.crt"
client_keystore="$classpathDir/client.keystore"
node_crt="$classpathDir/node.crt"
node_key="$classpathDir/node.key"
sdk_version=

# check the user configure
function check_user_config() {
	echo "----------------------------"
	echo "5. User Config"
	echo "----------------------------"
	if [ ! -f "$fisco_properties" ];
	then
		echo "ERROR: the fisco.properties does not exists."
	else
		echo "the fisco.properties is exists."
	fi
	if [ ! -f "$weidentity_properties" ];
	then
		echo "ERROR: the weidentity.properties does not exists."
	else
		echo "the weidentity.properties is exists."
	fi
	if [ ! -f "$ca_crt" ];
	then
		echo "ERROR: the ca.crt does not exists."
	else
		echo "the ca.crt is exists and the MD5 is `md5sum $ca_crt  | cut -d " " -f1`"		
	fi
	
	bcos_version=$(grep "bcos\.version" $fisco_properties |awk -F"=" '{print $2}')
	if [[ $bcos_version == 1* ]];
	then
		if [ ! -f "$client_keystore" ];
		then
			echo "ERROR: the client.keystore does not exists."
		else
			echo "the client.keystore is exists and the MD5 is `md5sum $client_keystore  | cut -d " " -f1`"		
		fi
	elif [[ $bcos_version == 2* ]];
	then
		if [ ! -f "$node_crt" ];
		then
			echo "ERROR: the node.crt does not exists."
		else
			echo "the node.crt is exists and the MD5 is `md5sum $node_crt  | cut -d " " -f1`"			
		fi
		if [ ! -f "$node_key" ];
		then
			echo "ERROR: the node.key does not exists."
		else
			echo "the node.key is exists and the MD5 is `md5sum $node_key  | cut -d " " -f1`"		
		fi
	else
		echo "ERROR: the bcos.version value is invalid."
	fi
	
	blockchain_orgid=$(grep "blockchain\.orgid=" $weidentity_properties |awk -F"=" '{print $2}')
	echo "the current orgid: $blockchain_orgid"
	nodes=$(grep "nodes=" $weidentity_properties |awk -F"=" '{print $2}')
	echo "the current nodes: $nodes"
	
	OLD_IFS="$IFS"
    IFS=","
    array=($nodes)
    IFS="$OLD_IFS"
    for var in ${array[@]}
    do
		var=${var##*@}
	    echo begin test $var
		ip=`echo $var | cut -d : -f 1`
		port=`echo $var | cut -d : -f 2`
		result=`echo -e "\n" | timeout 5 telnet $ip $port 2>/dev/null | grep Connected | wc -l`
		if [ $result -eq 1 ]; then
			echo "Network is Open."
		else
			echo "Network is Closed."
		fi
    done
}

# check the jar version
function  check_jar_version() {
	echo "----------------------------"
	echo "6. Dependencies Jar Version"
	echo "----------------------------" 
	bcos_version=$(grep "bcos\.version" $fisco_properties |awk -F"=" '{print $2}')
	echo "the bcos version: $bcos_version"
	isSdk=1
	for file in $libDir/*
	do
		file=${file##*/}
		if [[ $file == weid-contract-java* ]];
		then
			echo "the weid contract jar: "$file
		fi
		if [[ $file == weid-java-sdk* ]];
		then
			isSdk=0
			echo "the weid java sdk jar: "$file
		fi
		if [[ $file == web3sdk*-1.* ]];
		then
			if [[ $bcos_version == 1* ]];
			then
				echo "the web3sdk jar： "$file
			fi
		fi
		if [[ $file == web3sdk*-2.* ]];
		then
			if [[ $bcos_version == 2* ]];
			then
				echo "the web3sdk jar： "$file
			fi
		fi
	done
	
	if [[ $isSdk == 1 ]];
	then
		if [ -d "./dist/app/" ];
		then
			for file in ./dist/app/*
			do
				file=${file##*/}
				if [[ $file == weid-java-sdk-*.jar ]];
				then
					echo "the weid java sdk jar: $file"
				fi
			done
		fi
	fi
}

# check the node version
function check_node_version() {
	echo "----------------------------"
	echo "7. FISCO BCOS Version"
	echo "----------------------------"
	isSdk=1
	version_default=1.4
	for file in $libDir/*
	do
		file=${file##*/}
		if [[ $file == weid-java-sdk* ]];
		then
			isSdk=0
			echo "your project includes the weid-java-sdk, this is: "$file
			get_the_version $file
			if [[ $(echo "${sdk_version} < ${version_default}" | bc) -eq 1 ]]
			then
				echo "WARN: the current version of SDK does not support to check the node version, minimum version 1.4.0"
			else
				java ${JAVA_OPTS} -cp $libDir/*:$classpathDir/ com.webank.weid.app.AppCommand --checkversion test
			fi
		fi
	done
	if [[ $isSdk == 1 ]];
	then
		if [ -d "./dist/app/" ];
		then
			for file in ./dist/app/*
			do
				file=${file##*/}
				if [[ $file == weid-java-sdk-*.jar ]];
				then
					get_the_version $file
					if [[ $(echo "${sdk_version} < ${version_default}" | bc) -eq 1 ]]
					then
						echo "WARN: the current version of SDK does not support to check the node version, minimum version 1.4.0"
					else
						currentDir=`pwd`
						java ${JAVA_OPTS} -cp $currentDir/dist/lib/*:$currentDir/dist/app/*:$currentDir/dist/conf/ com.webank.weid.app.AppCommand --checkversion test
					fi
				fi
			done
		fi
	fi
}

function get_the_version() {
	sdk_version=$1
	sdk_version=${sdk_version/.rc-/}
	sdk_version=${sdk_version##*-}
	sdk_version=${sdk_version%.jar}
	sdk_version=${sdk_version%.*}
}

function main() {
	# check the user config
	check_user_config
	
	# check the jar version
	check_jar_version
	
	# check the node version
	check_node_version
}

main