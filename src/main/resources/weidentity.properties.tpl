# The organization ID for AMOP communication.
blockchain.orgid=organizationA

# Persistence Layer configurations. Do NOT change this if you are not using Persistence Layer features!
# MySQL connection config
# Support multiple data source configurations with comma-separated multiple data sources.
datasource.name=datasource1

# The configuration of each data source is prefixed by the name of the data source.
datasource1.jdbc.url=jdbc:mysql://0.0.0.0:3306/mysql?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
datasource1.jdbc.username=user
datasource1.jdbc.password=password
datasource1.jdbc.maxActive=50
datasource1.jdbc.minIdle=5
datasource1.jdbc.maxIdle=5
datasource1.jdbc.maxWait=10000
datasource1.jdbc.timeBetweenEvictionRunsMillis=600000
datasource1.jdbc.numTestsPerEvictionRun=5
datasource1.jdbc.minEvictableIdleTimeMillis=1800000

# Domain configuration, which divides colons into two segments, the first segment is the name of the data source, 
# the second segment is the name of the table, and if not, the default is the first data source and the default table `sdk_all_data`,
# Multiple domains can be configured at the same time.
# example:
# credential.domain=datasource1:credential_data
# weidDocument.domain=datasource1:weid_document_data

default.domain=datasource1:sdk_all_data

# Salt length for Proof creation.
salt.length=5

# AMOP Config
# Timeout for amop request, default: 5000ms
amop.request.timeout=5000

# Blockchain node info.
nodes=${BLOCKCHIAN_NODE_INFO}