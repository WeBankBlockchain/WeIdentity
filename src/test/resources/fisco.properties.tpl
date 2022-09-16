# Fisco-bcos blockchain node related properties

#######################################################################################################
#                                                                                                     #
#         fisco bcos version config                                                                   #
#                                                                                                     #
#######################################################################################################
# Version
bcos.version=${FISCO_BCOS_VERSION}


#######################################################################################################
#                                                                                                     #
#         contract address config                                                                     #
#                                                                                                     #
#######################################################################################################
# contract address
weId.contractaddress=
cpt.contractaddress=
issuer.contractaddress=
evidence.contractaddress=
specificissuer.contractaddress=

# This variable is used to distinguish the environment. You can use "dev" to set the development environment, 
# "stg" to set the test environment, "prd" to set the production environment,
# If you do not set it, the system will use allOrg as the environment by default. 
# It is not recommended. Production use default configuration
cns.profile.active=${CNS_PROFILE_ACTIVE}

#######################################################################################################
#                                                                                                     #
#         web3sdk connection config                                                                   #
#                                                                                                     #
#######################################################################################################
# blockchain connection params
web3sdk.timeout=30
web3sdk.core-pool-size=100
web3sdk.max-pool-size=200
web3sdk.queue-capacity=1000
web3sdk.keep-alive-seconds=60


#######################################################################################################
#                                                                                                     #
#         fisco bcos 2.0 related config                                                               #
#                                                                                                     #
#######################################################################################################
# Fisco-Bcos 2.x params, including Group ID and Encrypt Type
group.id=1

#######################################################################################################
#                                                                                                     #
#         fisco bcos node cert related config                                                         #
#                                                                                                     #
#######################################################################################################
# Fisco-Bcos sdk SSL encrypt type, false:ECDSA, true:SM2
sdk.sm-crypto=false
# fisco-bcos sdk cert path contains[ca.crt,sdk.crt,sdk.key]
# if sdk.sm-crypto is true, contains [gm] directory, and gm dir contains [gmca.crt,gmsdk.crt,gmsdk.key,gmensdk.crt,gmensdk.key]
sdk.cert-path=conf

# amop public key of pem and private key of p12 configuration
amop.pub-path=conf/amop/consumer_public_key.pem
amop.pri-path=conf/amop/consumer_private_key.p12
amop.p12-password=123456