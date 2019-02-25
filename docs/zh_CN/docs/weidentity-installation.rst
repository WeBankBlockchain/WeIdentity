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

操作系统
""""""""

  CentOS （7.2 64位）或Ubuntu（16.04 64位）
　
FISCO-BCOS区块链环境
""""""""""""""""""""

 您需要有一套可以运行的FISCO-BCOS区块链环境，如果没有，可以参考\ `「FISCO-BCOS节点安装方法」 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/tools/index.html>`_\ 来搭建一套区块链环境。

JDK
"""

  要求\ `JDK1.8+ <https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>`_\ 。推荐使用jdk8u141

fisco-solc
""""""""""

   fisco-solc是solidity的编译器。\ `下载地址 <https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/fisco-solc>`_


* fisco-solc安装方法：将fisco-solc拷贝到系统的/usr/bin目录下，执行命令\ ``chmod +x fisco-solc``\ 。

gradle
""""""

  WeIdentity JAVA SDK使用\ `gradle <https://gradle.org/>`_\ 进行构建，您需要提前安装好gradle，版本要求不低于4.3。

网络连通
""""""""

 检查WeIdentity JAVA SDK部署环境是否能telnet通FISCO BCOS节点的channelPort端口，若telnet不通，需要检查网络连通性和安全策略。

安装部署
--------

我们提供两种方式使用SDK：   

* `源码方式 <https://weidentity.readthedocs.io/projects/javasdk/zh_CN/latest/docs/weidentity-installation-by-sourcecode.html>`_   
* `weidentity-build-tools方式 <https://weidentity.readthedocs.io/projects/buildtools/zh_CN/latest/docs/weidentity-build-tools-doc.html>`_
