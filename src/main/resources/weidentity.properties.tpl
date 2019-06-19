# The organization ID for AMOP communication.
blockchain.orgid=organizationA

# Persistence Layer configurations. Do NOT change this if you are not using Persistence Layer features!
# MySQL connection config
jdbc.url=jdbc:mysql://0.0.0.0:3306/mysql?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
jdbc.username=user
jdbc.password=password
jdbc.maxActive=50
jdbc.minIdle=5
jdbc.maxIdle=5
jdbc.maxWait=10000
jdbc.timeBetweenEvictionRunsMillis=600000
jdbc.numTestsPerEvictionRun=5
jdbc.minEvictableIdleTimeMillis=1800000

# Salt length for Proof creation.
salt.length=5

# AMOP Config
# Timeout for amop request, default: 5000ms
amop.request.timeout=5000

# Blockchain node info.
nodes=${BLOCKCHIAN_NODE_INFO}