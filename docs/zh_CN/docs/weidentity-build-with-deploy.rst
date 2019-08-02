.. role:: raw-html-m2r(raw)
   :format: html

.. _weidentity-build-with-deploy:

WeIdentity JAVA SDK 安装部署工具（部署智能合约）
=============================================================

整体介绍
--------

一条区块链里，有多家机构，只需要一家机构部署 WeIdentity 智能合约，部署完成后，将智能合约地址给到其他机构即可。

* 部署 WeIdentity 智能合约的机构，参考本文档完成安装部署和集成。
* 不部署 WeIdentity 智能合约的机构，可以参考\ `WeIdentity Java SDK 安装部署工具（不部署智能合约 <./weidentity-build-without-deploy.html>`__\ 。


部署步骤
--------


1. 部署 WeIdentity 智能合约
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


1.1 下载安装部署工具
''''''''''''''''''''''''''''''
::

    git clone https://github.com/WeBankFinTech/weid-build-tools.git
 

该工具默认会使用最新版本的
`WeIdentity智能合约 <https://github.com/WeBankFinTech/weidentity-contract>`__\，该工具可以帮您发布智能合约和自动配置。


1.2  配置基本信息
'''''''''''''''''''''''''''''
weid-java-sdk 可以同时支持 FISCO BCOS 1.3 版本和 FISCO BCOS 2.0 版本。


1.2.1  基本配置
>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

::

    cd weid-build-tools   
    vim run.config   

- 配置区块链节点信息，填入区块链节点 IP 和 Channel端口，示例如下：

.. note::
     区块链节点的Channel端口说明见\ `FISCO BCOS 2.0配置文件说明 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/configuration.html#rpc>`__\ 或 `FISCO BCOS 1.3 配置文件说明 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/release-1.3/docs/web3sdk/config_web3sdk.html#java>`__\ 。

.. code:: shell

    blockchain_address=10.10.10.10:20200

- 如果需要配置多个区块链节点，用逗号分隔，示例如下：

.. code:: shell

    blockchain_address=10.10.10.10:20200,10.10.10.11:20200


- 配置 FISCO BCOS 版本信息，如果您使用 FISCO BCOS 2.0 版本则配置为2，FISCO BCOS 1.3 则配置为 1：

.. code:: shell

    blockchain_fiscobcos_version=2


- 配置机构名称，该名称也被用作后续机构间的 \ `AMOP <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/amop_protocol.html>`__ 通信标识。假设您的机构名为 test，您可以配置为：

.. code:: shell

    org_id=test

- 配置 chain-id，该配置项用于路由到不同的网络，假设您的 chain-id 定义为1，则您可以配置为：

.. code:: shell

    chain_id=1

保存退出，即完成基本配置。

1.2.2 配置节点证书和秘钥文件
>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

::

    cd resources/

FISCO BCOS 2.0请参考\ `2.0 web3sdk客户端配置 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/sdk.html#sdk>`__\，将证书文件 ``ca.crt``， ``node.crt`` 和 ``node.key`` 复制出来，拷贝至当前目录下。

FISCO BCOS 1.3请参考\ `1.3 web3sdk客户端配置 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/release-1.3/docs/tools/web3sdk.html>`__\，将证书文件 ``ca.crt`` 和 ``client.keystore`` 复制出来，拷贝至当前目录下 。


1.3 部署智能合约并自动生成配置文件
''''''''''''''''''''''''''''''

.. raw:: html

   </div>


- 如果您是第一次使用本工具，您需要先进行编译：

::

    cd ..
    chmod +x compile.sh   
    ./compile.sh

如果执行过程没报错，大约半分钟左右可以编译完成。

- 执行脚本 deploy.sh 进行 WeIdentity 智能合约的发布。

::

    chmod +x deploy.sh   
    ./deploy.sh


运行成功后，会打印以下信息：

::

    contract is deployed with success.
    ===========================================.
    weid contract address is 0x4ba81103afbd5fc203db14322c3a48cd1abb7770
    cpt contract address is 0xb1f3f13f772f3fc04b27ad8c377def5bc0c94200
    authority issuer contract address is 0xabb97b3042d0f50b87eef3c49ffc8447560faf76
    evidence contract address is 0x8cc0de880394cbde18ca17f6ce2cf7af5c51891e
    specificIssuer contract address is 0xca5fe4a67da7e25a24d76d24efbf955c475ab9ca
    ===========================================.


.. note::
  | 发布 WeIdentity 智能合约的机构将会自动注册为委员会机构成员（ Committee Member ）。
  | 发布 WeIdentity 智能合约会同时会在 ``weid-build-tools/output/admin`` 目录下动态生成私钥文件 ``ecdsa_key``，以及对应的公钥文件 ``ecdsa_key.pub``，此私钥后续用于注册权威机构，您可以将起保存到您的其他存储库里。


至此，您已经完成 weid-java-sdk 的安装部署，您可以开始您的 Java 应用集成以及便捷工具体验。

.. note::
     一条区块链里，有一家机构负责部署 WeIdentity 智能合约，部署成功后，会将上述智能合约地址给到其他机构。

2 weid-java-sdk 的集成
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

2.1 在自己的 Java 应用中引入 weid-java-sdk
'''''''''''''''''''''''''''''''''''''''''''''

在自己的 Java 应用通过 ``build.gradle`` 引入，目前weid-java-sdk暂时只支持gradle方式引入。

::

    compile("com.webank:weid-java-sdk:1.3.1")


2.2 配置您的 Java 应用
''''''''''''''''''''''''''''''''''''
将 weid-build-tools 里配置好的配置文件拷贝至您的 Java 应用中：
::

    cd resources/
    ls


您可以将 ``resources`` 目录下的所有文件拷贝至您的Java应用的 ``resources`` 目录下，weid-java-sdk 会自动加载相应的资源文件。

现在您可以使用 WeIdentity 开发您的区块链身份应用。weid-java-sdk 相关接口请见：\ `WeIdentity JAVA SDK文档 <https://weidentity.readthedocs.io/projects/javasdk/zh_CN/latest/docs/weidentity-java-sdk-doc.html>`__ 。

我们提供了一个基于WeID的 \ `开发样例 <https://github.com/WeBankFinTech/weid-sample>`__\， 您可以参考。


3. WeIdentity JAVA SDK 便捷工具使用
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

我们提供了一些快捷工具，可以帮您快速体验 weid-java-sdk，请参考\ `WeIdentity JAVA SDK 便捷工具使用 <./weidentity-quick-tools.html>`__\。
