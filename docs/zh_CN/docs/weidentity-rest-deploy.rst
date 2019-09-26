
.. _weidentity-rest-deploy:

WeIdentity RestService 部署文档
----------------------------------------

1. Server 部署说明
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

1.1 环境要求
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Server 的环境要求与 WeIdentity-Java-SDK 的 `环境要求 <https://weidentity.readthedocs.io/projects/javasdk/zh_CN/latest/docs/weidentity-installation.html>`_ 类似，但它不需要 fisco-solc 编译环境：


.. list-table::
   :header-rows: 1
   :widths: 30 30 60

   * - 物料
     - 版本
     - 说明
   * - CentOS/Ubuntu
     - 7.2 / 16.04，64位
     - 部署 RestServer 用
   * - JDK
     - 1.8+
     - 推荐使用 1.8u141 及以上
   * - FISCO-BCOS 节点
     - 1.2.5（目前暂不支持 2.x）
     - 确保它可以和部署 Server 机器互相 telnet 连通其 channelPort 端口
   * - Gradle
     - 4.6+
     - 同时支持 4.x 和 5.x 版本的 Gradle


1.2 生成安装包
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

您可以从 \ `GitHub <https://github.com/WeBankFinTech/weid-http-service>`_\ 下载 RestService 的源代码，并进行编译以生成安装包（默认置于 ``/dist`` 目录下）：

.. code-block:: bash

   $ unzip dist.zip
   $ git clone https://github.com/WeBankFinTech/weid-http-service.git
   $ cd weid-http-service
   $ gradle build -x test
   $ cd dist

如果您没有外网连接，也可以从 \ `金链盟CDN <https://www.fisco.com.cn/cdn/weidentity/download/releases/weidentity.zip>`_\ 下载 WeIdentity RestService 的离线安装包并拷贝进 Server。下载完成后，执行如下命令将 ``http-service-dist.zip`` 解压：

.. code-block:: bash

   $ unzip http-service-dist.zip

两种方式均可以生成如下结构的安装包：

.. code-block:: text

   └─ dist：部署物料
      ├─ app: 启动jar包
      ├─ lib: 依赖库
      └─ conf: 配置文件
   ├─ server_status.sh：监控系统运行进度
   ├─ start.sh：启动RestServer
   └─ stop.sh：停止RestServer

1.3 修改配置文件
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

* 首先，确认 WeIdentity 合约已部署完毕，同时您所部署的 FISCO-BCOS 节点可以正常连通。
* 修改合约地址。如果您使用部署工具部署了 WeIdentity 合约，那么只需将部署工具生成的 ``fisco.properties`` 及 ``weidentity.properties`` 拷贝到 ``dist/conf`` 目录下即可。如果您使用源码部署，请手动修改 ``dist/conf/fisco.properties.tpl`` 及 ``dist/conf/weidentity.properties.tpl`` ，更新合约地址及区块链节点信息；修改完成后，将两个文件的子扩展名 ``.tpl`` 去掉。详情：

合约地址修改示例：更新 ``dist/conf/fisco.properties.tpl`` 下列属性中weId、cpt、issuer、evidence合约地址的值。

.. code-block:: xml

    weId.contractaddress=0xedfe29997c7783d618510f2da6510010ad5253f4
    cpt.contractaddress=0x8984cab94b7c3add9c56e6c21d4329e0020d73ad
    issuer.contractaddress=0xb5346fd29ac75e7bb682c548f2951b6f8bf7d754
    evidence.contractaddress=0xddddd42da68a40784f5f63ada7ead9b36a38d2e3
    specificissuer.contractaddress=0x215d5c4b8867ce9f52d1a599c9dfef190201c263

区块链节点信息修改示例：更新 ``dist/conf/weidentity.properties.tpl`` 中 ``nodes`` 项的值，注意每一条信息都应包含区块链用户、节点IP、节点channel端口地址；多于一个区块链节点，请用 “,” 半角逗号分隔。


.. code-block:: xml

    nodes=WeIdentity@127.0.0.1:8812,WeIdentity@127.0.0.1:8900

* 拷贝您 WeIdentity 合约部署者的私钥到 ``dist/conf`` 目录下，并重命名为``ecdsa_key``。如果您使用部署工具部署了 WeIdentity 合约，这个文件在 ``output/admin/`` 目录。如果您使用源码部署，这个文件在源代码根目录下。

* 修改 ``dist/conf/application.properties`` ，填入需要打开的监听端口地址（用于 RestServer 监听外来的 HTTP/HTTPS RESTful 请求，默认为 6000/6001，不可被其他程序占用）。同时，请确认用来调用默认合约部署者私钥的暗语；由于此暗语可直接调用 WeIdentity 合约部署者的私钥，权限较高（详见 \ `RestService API 说明文档 <./weidentity-rest-api.html>`_\ ），因此请您务必对其进行修改。

.. code-block:: bash

    # HTTPS请求端口
    server.port=6001
    # HTTP请求端口
    server.http.port=6000
    # 合约部署者私钥暗语
    default.passphrase=ecdsa_key

2. Server 使用说明
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

2.1 Server 启动/停止
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

进入 dist 目录执行以下应用以启动或停止 Rest Server：

.. code-block:: bash

    # 为脚本文件增加权限
    $ chmod +x *.sh
    # 启动应用
    $ ./start.sh
    # 观察应用状态
    $ ./server_status.sh
    # 停止应用
    $ ./stop.sh

执行 ``./start.sh`` 之后会输出以下提示，表示 RestServer 已经顺利启动：

.. code-block:: text

    ========================================================
    Starting com.webank.weid.http.Application ... [SUCCESS]
    ========================================================

请您通过执行 ``./server_status.sh`` 确认 RestServer 已经成功启动：

.. code-block:: text

    ========================================================
    com.webank.weid.http.Application is running(PID=100891)
    ========================================================

如果需要停止服务，请执行 ``./stop.sh`` ，之后会输出以下提示，表示 RestServer 已经顺利停止：

.. code-block:: text

    ========================================================
    Stopping com.webank.weid.http.Application ... [SUCCESS]
    ========================================================

3. 使用 Postman 访问 RestServer 的 API
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

RestServer 支持任何使用标准 HTTP/HTTPS 协议的 RESTful API 客户端访问，详细接口说明可见 API 文档。我们提供了一套 Postman 的环境与请求集供快速集成。使用步骤如下：

* 点击Postman的Import按钮，导入环境文件 ``weidentity-restservice.postman_environment.json`` 和请求集 ``invoke.postman_collection.json`` 。这两个文件可以在 GitHub代码仓库的 \ `对应目录 <https://github.com/WeBankFinTech/weid-http-service/tree/develop/PostmanConfig>`_\ 下找到
* 确认 ``weidentity-restservice`` 这个环境文件已导入成功，它包含两个环境变量 ``host`` 和 ``httpport``
    * 修改环境变量 ``host`` 属性的值为安装部署 ``RestServer`` 的服务器地址
    * 修改环境变量 ``httpport`` 属性的值配置文件中的 Server 监听端口地址
* 接下来确认 Invoke 这个命令集已导入成功。如果成功，可以从侧边栏中看到
* 现在，可以调用 Invoke 这个命令集中的各类API了。您可以从无参数请求 CreateWeId 开始，看看返回结果是不是和 API 文档中一致，成功创建了一个 WeIdentity DID。
