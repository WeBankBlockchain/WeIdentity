.. role:: raw-html-m2r(raw)
   :format: html

.. _weidentity-installation:

WeIdentity JAVA SDK安装部署文档
===============================


整体介绍
--------

  通过安装部署脚本，可以帮助您快速完成源码的编译打包以及智能合约的部署，您只需要进行一些简单的配置，即可快速的生成一套可执行并配置好的WeIdentity运行环境.

准备工作
--------

.. list-table::
   :header-rows: 1

   * - 配置
     - 说明
   * - 操作系统
     - CentOS （7.2 64位）或Ubuntu（16.04 64位）。
   * - FISCO-BCOS区块链环境
     - 您需要有一套可以运行的FISCO-BCOS区块链环境，如果没有，可以参考\ `「FISCO-BCOS节点安装方法」 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/installation.html>`_\ 来搭建一套区块链环境。
   * - JDK
     - 要求\ `JDK1.8+ <https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>`_\ ，推荐使用jdk8u141。
   * - gradle
     - WeIdentity JAVA SDK使用\ `gradle <https://gradle.org/>`_\ 进行构建，您需要提前安装好gradle，版本要求不低于4.3。
   * - 网络连通
     - 检查WeIdentity JAVA SDK部署环境是否能telnet通FISCO BCOS节点的channelPort端口，若telnet不通，需要检查网络连通性和安全策略。


安装部署
--------

我们提供两种方式安装部署SDK：   

* `安装部署工具方式 <https://weidentity.readthedocs.io/projects/buildtools/zh_CN/latest/docs/weidentity-build-tools-doc.html>`_ （推荐方式）   
* `源码方式 <https://weidentity.readthedocs.io/projects/javasdk/zh_CN/latest/docs/weidentity-installation-by-sourcecode.html>`_

从ApplicationContext.xml到properties的配置方式变化
--------------------------------------------------

自从WeIdentity-Java-SDK 1.2.0版本开始，您可能已经关注到，我们的配置文件已经弃用了ApplicationContext.xml。这种做法基于以下考虑：

* 使用ApplicationContext.xml，与使用Spring Boot的调用方式是互斥的。
* ApplicationContext.xml不必要地引入了大量Spring的依赖，同时包括了大量不必要的配置项，较为臃肿。

当前，我们使用fisco.properties及weidentity.properties两者组合。其中，fisco.properties记录了与FISCO-BCOS区块链节点相关的配置项，而weidentity.properties包括了WeIdentity SDK特有的功能配置项。实现上，其加载方式也对应由Spring Bean的注入方式转变成了显式读取。

这一改变，需要您对配置文件和调用方式进行一些处理：

配置文件
^^^^^^^^^

如果您使用上文的 `安装部署工具方式 <https://weidentity.readthedocs.io/projects/buildtools/zh_CN/latest/docs/weidentity-build-tools-doc.html>`_ ，重新部署了智能合约，那么这一部分可以跳过。

如果您不需要重新部署合约，或不准备使用部署工具，则需要执行以下步骤：

- 更新fisco.properties中的对应项
    - 将fisco.properties.tpl改名为fisco.properties
    - 合约地址
    - 更新chain.id，默认可设置为1
- 更新完毕之后，将fisco.properties放到/resources/目录中
- 更新weidentity.properties中的对应项
    - 将weidentity.properties.tpl改名为weidentity.properties
    - 更新nodes，格式为：“WeIdentity@节点IP:端口号”
    - 更新blockchain.orgid，为你的组织名
- 更新完毕之后，将weidentity.properties放到/resources/目录中

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
    v2.ca-crt-path=./v2/ca.crt
    v2.node-crt-path=./v2/node.crt
    v2.node-key-path=./v2/node.key

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
    nodes=10.107.106.107:20102


调用方式
^^^^^^^^^

目前，不再支持使用@Autowire的方式去加载WeIdService等服务。您需要使用以下代码直接创建服务实例：

.. code-block:: java

    WeIdService weIdService = new WeIdServiceImpl();
    AuthorityIssuerService authorityIssuerService = new AuthorityIssuerServiceImpl();
    CptService cptService = new CptServiceImpl();
    CredentialService credentialService = new CredentialServiceImpl();
    EvidenceService evidenceService = new EvidenceServiceImpl();
