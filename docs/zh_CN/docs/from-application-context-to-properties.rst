从ApplicationContext.xml到properties的配置方式转变
=================================================

自从WeIdentity-Java-SDK 1.2.0版本开始，您可能已经关注到，我们的配置文件已经弃用了ApplicationContext.xml。这种做法基于以下考虑：

* 使用ApplicationContext.xml，与使用Spring Boot的调用方式是互斥的。
* ApplicationContext.xml不必要地引入了大量Spring的依赖，同时包括了大量不必要的配置项，较为臃肿。

当前，我们使用fisco.properties及weidentity.properties两者组合。其中，fisco.properties记录了与FISCO-BCOS区块链节点相关的配置项，而weidentity.properties包括了WeIdentity SDK特有的功能配置项。实现上，其加载方式也对应由Spring Bean的注入方式转变成了显式读取。

如果您使用的是WeIdentity-Java-SDK 1.1.x，又需要升级到1.2.x及以上的版本，那么需要您对配置文件和调用方式进行一些处理：

配置文件
---------

如果您使用上文的 `安装部署工具方式 <https://weidentity.readthedocs.io/projects/buildtools/zh_CN/latest/docs/weidentity-build-tools-doc.html>`_ ，重新部署了智能合约，那么这一部分可以跳过。

如果您不需要重新部署合约，或不准备使用部署工具，则需要执行以下步骤：

- 更新fisco.properties中的对应项
    - 将fisco.properties.tpl改名为fisco.properties
    - 更新合约地址
    - 设置chain.id，默认可设置为1
- 更新完毕之后，将fisco.properties放到/resources/目录中
- 更新weidentity.properties中的对应项
    - 将weidentity.properties.tpl改名为weidentity.properties
    - 更新节点信息nodes
        - 如果您的FISCO-BCOS链为1.3版，格式为：“WeIdentity@节点IP:端口号”
        - 如果您的FISCO-BCOS链为2.x版，格式为：“节点IP:端口号”
    - 更新blockchain.orgid，为您的机构名，默认可设置为“test”
- 更新完毕之后，将weidentity.properties放到/resources/目录中
- 最后，更新节点的配置文件放到/resources/目录中
    - 如果您的FISCO-BCOS链为1.3版，请拷贝ca.crt和client.keystore
    - 如果您的FISCO-BCOS链为2.x版，请拷贝ca.crt，node.crt和node.key

下面是两个可用的样例：

.. code-block:: bash

    # fisco.properties
    # FISCO-BCOS blockchain node related properties

    # Blockchain version. 1.x and 2.x are both allowed.
    bcos.version=1.3

    # WeIdentity Contract addresses, formatted as "0xab0f7a80152ba6d65cb28a164be6094bd1de3fa2".
    weId.contractaddress=0xab0f7a80152ba6d65cb28a164be6094bd1de3fab
    cpt.contractaddress=0xc594245558b7daf8c1e386771eaab8688fa06656
    issuer.contractaddress=0xa33143f3e7dd190f0ca63729a85b224b22729f81
    evidence.contractaddress=0x9bce2c5f1687b7d50a7a36290a467b923d8313ca
    specificissuer.contractaddress=0x329220c0353ed40dfe44d3755d456f767735042c

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
    v2.ca-crt-path=ca.crt
    v2.node-crt-path=node.crt
    v2.node-key-path=node.key

.. code-block:: bash

    # weidentity.properties
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

    # Proof salt length for Proof creation.
    salt.length=5

    # AMOP Config
    # Timeout for amop request, default: 5000ms
    amop.request.timeout=5000

    # Blockchain node info.
    nodes=127.0.0.1:8888


调用方式
---------

目前，不再支持使用@Autowire的方式去加载WeIdService等服务。您需要使用以下代码直接创建服务实例：

.. code-block:: java

    WeIdService weIdService = new WeIdServiceImpl();
    AuthorityIssuerService authorityIssuerService = new AuthorityIssuerServiceImpl();
    CptService cptService = new CptServiceImpl();
    CredentialService credentialService = new CredentialServiceImpl();
    EvidenceService evidenceService = new EvidenceServiceImpl();
