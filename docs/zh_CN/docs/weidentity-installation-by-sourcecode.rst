.. role:: raw-html-m2r(raw)
   :format: html

.. _weidentity-installation-by-sourcecode:

WeIdentity JAVA SDK安装部署文档（源码方式）
=================================================

1.下载源码
""""""""""


* `WeIdentity JAVA SDK <https://github.com/WeBankFinTech/weid-java-sdk.git>`_\ :raw-html-m2r:`<br>`
  建议下载最新版本的release
  
.. code-block:: shell

      git clone https://github.com/WeBankFinTech/weid-java-sdk.git
      cd weid-java-sdk
      
2.配置客户端证书
""""""""""""""""

* 将安装好的FISCO-BCOS节点证书复制到 ``src/main/resources``。
  
  如果您使用的FISCO-BCOS版本为1.x，证书在节点目录里面的路径：``build/web3sdk/conf``
  
  证书：``ca.crt``，``client.keystore``
 
 
  如果您使用的FISCO-BCOS版本为2.x，证书在节点目录里面的路径：``nodes/xxx/sdk``
  
  证书：``ca.crt``，``node.crt``，``node.key``
  
* 进入WeIdentity JAVA SDK的resources目录:

  .. code-block:: shell

     cd src/main/resources

  然后将FISCO-BCOS节点的证书文件拷贝至该目录，替换已有的证书文件:

3.配置SDK连接的区块链节点
"""""""""""""""""""""""""

.. code-block:: shell

   cd ../../../build-tools/bin/
   chmod +x *.sh
   vim run.config

* bin目录下是运行部署打包的脚本和配置文件，您需要将FISCO-BCOS的节点地址配置到run.config文件中。如果SDK只需要连接一个区块链节点，以IP：PORT的形式赋值给配置项blockchain.node.address，例子：

.. code-block:: shell

   blockchain.node.address=10.10.10.10:9000

如果有SDK需要连接多个区块链节点，用逗号","分割：

.. code-block:: shell

   blockchain.node.address=10.10.10.10:9000,10.11.11.11:9000

* 根据您的节点版本配置bcos.version

如果您使用的FISCO-BCOS版本为 1.x

.. code-block:: shell

   bcos.version=1.x

如果您使用的FISCO-BCOS版本为 2.x

.. code-block:: shell

   bcos.version=2.x  

4.安装部署
""""""""""

运行下面的命令，自动完成代码编译，智能合约编译，智能合约部署和所有配置文件的配置：

.. code-block:: shell

   ./run.sh

出现下列输出，则表示安装部署成功，源码目录下的dist中已生成可运行的SDK包和配置文件。

.. code-block:: shell

	contract deployment done.
	begin to modify sdk config...
	modify sdk config finished...
	begin to clean config...
	clean finished...

到这里，您已经完成了SDK的安装和部署的全部步骤，您可以开始使用WeIdentity来构建您的分布式身份管理的应用了。

Have fun!!!

备注
----

查看WeIdentity JAVA SDK部署结果
""""""""""""""""""""

* 进入dist目录

.. code-block:: shell

   cd ../../dist/
   ls

dist目录包含以下目录： ``app  conf  lib``

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

* 进入源码根目录

.. code-block:: shell

   cd ../
   ls

根目录下生成的文件 ``ecdsa_key`` 为SDK部署合约动态生成的秘钥文件，您的项目集成SDK的时候可能需要使用此文件，请妥善保管。