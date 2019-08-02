命令行方式使用
-------------

整体介绍
~~~~~~~~

命令行方式比较完整的模拟了各个 \ `WeIdentity 角色 <https://weidentity.readthedocs.io/zh_CN/latest/docs/weidentity-spec.html#id9>`__\ 的工作流程，可以帮您快速体验 WeIdentity 也业务流程和运行机制。
各个角色的基本流程如下：

- Issuer
 | 创建 WeID
 | 注册成为 Authority Issuer
 | 注册 CPT
 | 创建 Credential

- User Agent
 | 创建 WeID
 | 通过 AMOP 获取 Verifier 发布的 Presentation Policy
 | 创建 Presentation
 | 打包 Presentation 成 QRcode 或者 Json 串，发送给 Verifier

- Verifier
 | 获取 User Agent 的 Presentation
 | 验证 Presentation


1. 配置与部署
^^^^^^^^^^^^^^^^^^^^^^^^^^

1.1 下载 weid-sample 源码：
''''''''''''''''''''''''''''''''''''

.. code:: shell

    git clone https://github.com/WeBankFinTech/weid-sample.git
    

1.2 部署 weid-java-sdk 与配置基本信息
''''''''''''''''''''''''''''''''''''''

-  安装部署 weid-java-sdk

   weid-sample 需要依赖 weid-java-sdk，您需要参考\ `WeIdentity JAVA
   SDK安装部署 <https://weidentity.readthedocs.io/zh_CN/latest/docs/weidentity-installation.html>`__\ 完成
   weid-java-sdk 的安装部署，并参照\ `Java应用集成章节 <https://weidentity.readthedocs.io/zh_CN/latest/docs/weidentity-build-with-deploy.html#weid-java-sdk>`__\ 完成
   weid-sample 的配置。



-  配置 Committee Member 私钥

.. note::
  此项配置并非必要。由于注册 Authority Issuer 需要委员会机构成员（ Committee Member ）权限，若您不是发布智能合约的机构，您无需关注此配置项。
  若您是智能合约发布的机构，您可以参考以下进行配置：


将您在\ `部署WeIdentity智能合约阶段 <https://weidentity.readthedocs.io/zh_CN/latest/docs/weidentity-build-with-deploy.html#id7>`__\ 生成的私钥文件拷贝至
``weid-sample/keys/priv/`` 目录中，此私钥后续将用于注册 Authority Issuer，weid-sample 会自动加载。


- 修改节点和机构配置

多个角色之间会使用 \ `AMOP <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/amop_protocol.html>`__ 进行通信，根据 AMOP 协议，每个机构需要配置为连接不同的区块链节点。

.. code:: shell

   cd weid-sample
   vim src/main/resources/weidentity.properties

关键配置如下：

 | ``blockchain.orgid`` ：机构名称。样例以 organizationA 为例，请修改为 organizationA。
 | ``nodes`` ：区块链节点信息。你可以修改为您区块链网络中的任一节点即可。

配置样例：

.. code:: properties

  blockchain.orgid=organizationA
  nodes=10.10.10.10:20200 


- 编译 weid-sample

如果您是第一次运行 weid-sample，您需要先进行编译：

.. code:: shell

    chmod +x *.sh
    ./build.sh

- 启动 AMOP 服务

weid-sample 里的 AMOP 服务是模拟 Verifier 向 User Agent 发送获取秘钥的请求，因此 Verifier 和 User Agent 需要连接同一条链中的不同的区块链节点。
先启动 Verifier 进程：

.. code:: shell

    ./command.sh daemon

运行成功，会启动 Verifier 的 AMOP 服务，输出如下日志：

.. code:: text

    the AMOP server start success.

- 修改 User Agent 配置

在启动完 Verifier 进程之后，还需要修改 User Agent 的配置，确保 User Agent 连接的区块链节点和 Verifier 连接的区块链节点在同一条链上，且连接的是不同的区块链节点：

.. code:: shell

    vim dist/conf/weidentity.properties

此处主要是修改机构名称和区块链节点配置，要确保和 Verifier 连接的不是同一个区块链节点。

配置样例：

.. code:: properties

    blockchain.orgid=organizationB
    nodes=10.10.10.11:20200  


2. 基本流程的演示
^^^^^^^^^^^^^^^^^^^^^^^^^^

- Issuer 操作流程演示

.. code:: shell

    ./command.sh issuer

若运行成功，则会打印包括创建 WeID、注册成为 Authority Issuer、注册 CPT 和创建 Credential 等运行流程。

以下为截取的部分流程日志：
::

    
    --------- start issuer ----------
    issuer() init...

    begin to createWeId...

    createWeId result:

    result:(com.webank.weid.protocol.response.CreateWeIdDataResult)
    weId: did:weid:1:0x7a276b294ecf0eb7b917765f308f024af2c99a38
    userWeIdPublicKey:(com.webank.weid.protocol.base.WeIdPublicKey)
        publicKey: 1443108387689714733821851716463554592846955595194902087319775398382966796515741745
        951182105547115313067791999154982272567881519406873966935891855085705784
    userWeIdPrivateKey:(com.webank.weid.protocol.base.WeIdPrivateKey)
        privateKey: 46686865859949148045125507514815998920467147178097685958028816903332430030079
    errorCode: 0
    errorMessage: success
    transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
    blockNumber: 2098
    transactionHash: 0x20fc5c2730e4636248b121d31ffdbf7fa12e95185068fc1dea060d1afa9d554e
    transactionIndex: 0

    begin to setPublicKey...

    setPublicKey result:

    result: true
    errorCode: 0
    errorMessage: success
    transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
    blockNumber: 2099
    transactionHash: 0x498d2bfd2d8ffa297af699c788e80de1bd51c255a7365307624637ae5a42f3a1
    transactionIndex: 0


- User Agent 操作流程演示

.. code:: shell

    ./command.sh user_agent

运行成功，则会打印包括创建 WeID、 通过 AMOP 获取 Verifier 发布的 Presentation Policy、创建 Presentation 以及打包 Presentation 成 QRcode 或者 Json 串的流程。
以下为截取的部分日志： 

::

    
    --------- start User Agent ----------
    userAgent() init...

    begin to create weId for useragent...

    createWeId result:

    result:(com.webank.weid.protocol.response.CreateWeIdDataResult)
    weId: did:weid:1:0x38198689923961e8ecd6d57d88d027b1a6d1daf2
    userWeIdPublicKey:(com.webank.weid.protocol.base.WeIdPublicKey)
        publicKey: 12409513077193959265896252693672990701614851618753940603742819290794422690048786166
        777486244492302423653282585338774488347536362368216536452956852123869456
    userWeIdPrivateKey:(com.webank.weid.protocol.base.WeIdPrivateKey)
        privateKey: 11700070604387246310492373601720779844791990854359896181912833510050901695117
    errorCode: 0
    errorMessage: success
    transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
    blockNumber: 2107
    transactionHash: 0x2474141b82c367d8d5770a7f4d124aeaf985e7fa3e3e2f7f98eeed3d38d862f5
    transactionIndex: 0



- Verifier 操作流程演示

.. code:: shell

    ./command.sh verifier

运行成功，则会打印 Verifier 反序列化 Presentation 以及验证 Presentation 的过程。
以下为截取的部分日志，详细流程可以参考代码实现：

::

    --------- start verifier ----------
    verifier() init...

    begin get the presentation json...


至此，您已经体验了 weid-sample 实现的各个角色的运行流程，实现的入口类在weid-sample工程的 ``com.webank.weid.demo.server.SampleApp``，您可以参考进行您的 Java 应用开发。