.. role:: raw-html-m2r(raw)
   :format: html

.. _weidentity-installation-by-sourcecode:

WeIdentity JAVA SDK安装部署文档（源码方式）
=================================================

1.下载源码
""""""""""


* `WeIdentity JAVA SDK <https://github.com/WeBankFinTech/weidentity-java-sdk.git>`_\ :raw-html-m2r:`<br>`
  建议下载最新版本的release
* `WeIdentity 智能合约 <https://github.com/WeBankFinTech/weidentity-contract.git>`_\ :raw-html-m2r:`<br>`
  建议下载最新版本的release

2.拷贝「WeIdentity智能合约」源码到特定目录
""""""""""""""""""""""""""""""""""""""""""""""""

进入build_tools目录：

.. code-block:: shell

      cd weidentity-java-sdk/build-tools/contracts/

将您刚下载的「WeIdentity智能合约」源码文件放至该目录即可。

..

    如果不需要特定版本的「WeIdentity智能合约」，可以跳过第2步，而默认使用该目录下的智能合约（当前版本WeIdentity JAVA SDK在发布时最新版本的智能合约）。


3.配置客户端证书
""""""""""""""""


*
  将安装的FISCO-BCOS节点build/web3sdk里的客户端证书ca.crt和client.keystore复制出来。

*
  进入WeIdentity JAVA SDK的resources目录:

  .. code-block:: shell

     cd ../../src/main/resources

  然后将FISCO-BCOS节点的证书ca.crt和keystore文件拷贝至该目录，替换已有的证书文件:

4.配置SDK连接的区块链节点
"""""""""""""""""""""""""

.. code-block:: shell

   cd ../../../build-tools/bin/
   chmod +x *.sh
   vim run.config

bin目录下是运行部署打包的脚本和配置文件，您需要将FISCO-BCOS的节点地址配置到run.config文件中。如果SDK只需要连接一个区块链节点，以IP：PORT的形式赋值给配置项blockchain.node.address，例子：

.. code-block:: shell

   blockchain.node.address=10.10.10.10:9000

如果有SDK需要连接多个区块链节点，用逗号","分割：

.. code-block:: shell

   blockchain.node.address=10.10.10.10:9000,10.11.11.11:9000

5.安装部署
""""""""""

运行下面的命令，自动完成代码编译，智能合约编译，智能合约部署和所有配置文件的配置：

.. code-block:: shell

   ./run.sh

如果部署过程中没有报错，那么源码目录下的dist里即为已经编译好并部署好智能合约的可运行的SDK包和配置文件。

6. 完成
"""""""

到这里，您已经完成了SDK的安装和部署的全部步骤，您可以开始使用WeIdentity来构建您的分布式身份管理的应用了。

Have fun!!!

备注
----

查看智能合约部署结果
""""""""""""""""""""

进入dist目录

.. code-block:: shell

   cd ../../dist/
   ls

正常情况下，dist目录包含以下目录： ``app  conf  lib``

.. list-table::
   :header-rows: 1

   * - 目录名
     - 说明
   * - app
     - 打包好的SDK jar包。
   * - conf
     - SDK运行时的一些配置，应用集成SDK的时候，需要将次目录下的文件放到classpath下。
   * - lib
     - 依赖的jar包。


客户端证书ca.crt,以及client.keystore的作用：
""""""""""""""""""""""""""""""""""""""""""""


* 证书ca.crt：用来验证sdk连接节点的节点证书的合法性。
* client.keystore有三种用途：(1) 用作和节点连接是sdk的身份证书，由节点的ca.crt和agency.crt来验证合法性。(2)用作和其他sdk（前置）连接的身份证书，由其他sdk的ca.crt来验证合法性。(3)用作sdk发交易的私钥证书。
