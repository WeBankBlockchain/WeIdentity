# FISCO-BCOS blockchain node related properties

# Blockchain version. 1.x and 2.x are both allowed.
bcos.version=1.3

# WeIdentity Contract addresses, formatted as "0xab0f7a80152ba6d65cb28a164be6094bd1de3fa2".
weId.contractaddress=${WEID_ADDRESS}
cpt.contractaddress=${CPT_ADDRESS}
issuer.contractaddress=${ISSUER_ADDRESS}
evidence.contractaddress=${EVIDENCE_ADDRESS}
specificissuer.contractaddress=${SPECIFICISSUER_ADDRESS}

# Specified blockchain ID you are targeting to.
chain.id=101

# Blockchain connection params. Do NOT change these unless you are troubleshooting!
web3sdk.timeout=10000
web3sdk.core-pool-size=100
web3sdk.max-pool-size=200
web3sdk.queue-capacity=1000
web3sdk.keep-alive-seconds=60

# Fisco-Bcos 2.x params, including Group ID and Encrypt Type.
group.id=1
encrypt.type=0

# Config files locations and params. These should be originated from blockchain nodes.
v1.ca-crt-path=ca.crt
v1.client-crt-password=123456
v1.client-key-store-path=client.keystore
v1.key-store-password=123456
v2.ca-crt-path=./v2/ca.crt
v2.node-crt-path=./v2/node.crt
v2.node-key-path=./v2/node.key