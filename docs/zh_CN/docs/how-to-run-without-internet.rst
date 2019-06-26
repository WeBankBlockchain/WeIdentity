网络访问受限时的部署方法
========================

WeIdentity-Java-SDK及其部署工具默认需要连接到Internet，以访问Maven中央仓库去下载所需的依赖库。
当您访问Internet受限时，根据您是否可访问自建的仓库服务（如企业内部搭建的仓库），本文提供了两种部署解决方案：

有自建仓库服务
--------------

如果您需要自建Maven仓库，请查阅\ `此链接 <https://www.theserverside.com/news/1364121/Setting-Up-a-Maven-Repository>`__\。
文中详尽地介绍了基于Artifactory或Nexus OSS的多种自建Maven仓库，以及从中央仓库拉取jar包的方法。

当您的自建仓库可以使用时，您需要更改默认的maven仓库地址及build.gradle里的仓库地址，使得它能够被正确指向。您可参阅
\ `此链接 <https://discuss.gradle.org/t/mavenlocal-how-does-gradle-resolve-the-directory-of-the-local-maven-repository/4407>`__
了解如何修改默认的maven仓库地址配置。


无自建仓库服务
--------------

如果您没有自建仓库的打算，或者您的部署环境无法连接到除了区块链节点以外的Internet环境，那么下面将介绍从Java-SDK 1.3.0
开始，新引入的离线编译功能。此功能可以支持您在无Internet环境下WeIdentity-Java-SDK及部署工具build-tools的编译与部署。

前提条件
~~~~~~~~

您仍需要在部署的机器上预先安装好Gradle、JDK环境及fisco-solc编译器，详见\ `WeIdentity JAVA SDK安装部署文档 <./weidentity-installation.html>`__\ 中的“准备工作”一节。

流程
~~~~

1, 拉取部署工具或Java-SDK的代码。

2, 您需要在有网络的环境下运行编译命令，拉取所有的依赖库。

如果您使用的是部署工具build-tools，请运行：

.. code:: shell

   ./compile.sh

如果您准备直接编译Java-SDK，请运行：

.. code:: shell

   gradle build

当执行完成后，您应该在dist/lib/目录里看到所有的依赖库已经下载完毕。

3, 将依赖库拷贝到无网环境下的部署机器中。

具体地，不论是部署工具还是Java-SDK，您只需将所有的依赖库拷贝到代码目录下dist/lib/中即可。如果此目录不存在，请新建一个。

4, 执行离线编译命令。

如果您使用的是部署工具build-tools，请运行下面的命令进行离线编译及部署：

.. code:: shell

   ./compile.sh --offline
   ./deploy.sh

如果您准备直接编译Java-SDK，请运行下面的命令进行离线编译及组装：

.. code:: shell

   gradle build --offline