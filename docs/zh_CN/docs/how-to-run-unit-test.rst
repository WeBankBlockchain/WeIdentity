如何运行单元测试
================

LINUX 系统上通过 gradle 执行单元测试
------------------------------------

前提条件
~~~~~~~~

单元测试的运行需要提前安装部署好WeIdentity JAVA SDK，请参考 `WeIdentity
JAVA SDK 安装部署文档`_\ 中的源码方式安装部署WeIdentity。

流程
~~~~

下载源代码进行安装部署后，以 ``weid-java-sdk`` 根目录为起点：

1, 进入dist/conf目录。

.. code:: shell

   cd dist/conf

2, 将生成好的 ``weidentity.properties`` 和 ``fisco.properties`` 复制到 ``src/test/resources``
目录，WeIdentity 安装部署完会自动生成并配置好
`weidentity.properties`` 和 ``fisco.properties``\ ，所需的节点配置和合约地址配置已完成,可以直接使用。

.. code:: shell

   cp fisco.properties weidentity.properties  ../../src/test/resources/

3, 将生成好的 ``ca.crt`` 和 ``client.keystore`` 复制到
``src/test/resources`` 目录, 这两个证书是 WeIdentity 运行所需要的 SDK
证书。

.. code:: shell

   cp ca.crt client.keystore  ../../src/test/resources/

4, 回到项目根目录，执行测试命令。

.. code:: shell

   cd ../../
   gradle test

.. _WeIdentity JAVA SDK 安装部署文档: ./weidentity-installation.html#