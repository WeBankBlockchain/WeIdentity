# Fisco-bcos blockchain node related properties

# Version
bcos.version=2

# contract address
weId.contractaddress=${WEID_ADDRESS}
cpt.contractaddress=${CPT_ADDRESS}
issuer.contractaddress=${ISSUER_ADDRESS}
evidence.contractaddress=${EVIDENCE_ADDRESS}
specificissuer.contractaddress=${SPECIFICISSUER_ADDRESS}

# blockchain connection params
chain.id=101
web3sdk.timeout=10000
web3sdk.core-pool-size=100
web3sdk.max-pool-size=200
web3sdk.queue-capacity=1000
web3sdk.keep-alive-seconds=60

# Fisco-Bcos 2.x params, including Group ID and Encrypt Type
group.id=1
encrypt.type=0

# config files params
v1.ca-crt-path=ca.crt
v1.client-crt-password=123456
v1.client-key-store-path=client.keystore
v1.key-store-password=123456
v2.ca-crt-path=./v2/ca.crt
v2.node-crt-path=./v2/node.crt
v2.node-key-path=./v2/node.key