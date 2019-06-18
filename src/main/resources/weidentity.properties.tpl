#orgid, used for amop communication
blockchain.orgid=organizationA

#presistence configuration
#mysql connection config
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

salt.length=5

#amop config
#timeout for amop request, default:5000ms
amop.request.timeout=5000

# blockchain node info
nodes=${BLOCKCHIAN_NODE_INFO}