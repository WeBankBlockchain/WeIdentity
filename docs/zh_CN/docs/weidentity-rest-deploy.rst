
.. _weidentity-rest-deploy:

WeIdentity RestService 部署文档
----------------------------------------

1. Server部署说明
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

RestService会提供一个Server组件包以供部署。未来，此Server组件包将会开源。

1.1 环境要求
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Server的环境要求与WeIdentity-Java-SDK的 `环境要求 <https://weidentity.readthedocs.io/projects/javasdk/zh_CN/latest/docs/weidentity-installation.html>`_ 类似，但它不需要fisco-solc编译环境：

.. list-table::
   :header-rows: 1
   :widths: 30 30 60

   * - 物料
     - 版本
     - 说明
   * - CentOS/Ubuntu
     - 7.2 / 16.04，64位
     - 部署RestServer用
   * - JDK
     - 1.8+
     - 推荐使用1.8u141及以上
   * - FISCO-BCOS
     - 1.3.6+
     - 确保它可以和部署Server机器互相telnet连通其channelPort端口
   * - gradle
     - 4.6+
     - 

1.2 物料准备
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

执行如下命令将dist.zip解压：

.. code-block:: bash

   $ unzip dist.zip

可以得到如下结构：

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

* 首先，确认WeIdentity合约已部署完毕。
* 修改 ``dist/conf/ApplicationContext.xml`` ，更新合约地址及区块链节点信息。

合约地址修改示例：更新下列属性中weId、cpt、issuer、evidence合约地址的值

.. code-block:: xml

    <bean class="org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer"
      id="appConfig">
    ...
      <property name="properties">
        <props>
          <prop key="weId.contractaddress">0xedfe29997c7783d618510f2da6510010ad5253f4</prop>
          <prop key="cpt.contractaddress">0x8984cab94b7c3add9c56e6c21d4329e0020d73ad</prop>
          <prop key="issuer.contractaddress">0xb5346fd29ac75e7bb682c548f2951b6f8bf7d754</prop>
          <prop key="evidence.contractaddress">0xddddd42da68a40784f5f63ada7ead9b36a38d2e3</prop>
        </props>
      </property>

区块链节点信息修改示例：更新value列表的值，注意每一条value记录都应包含区块链用户、节点IP、节点channel端口地址信息

.. code-block:: xml

    <bean class="org.bcos.channel.handler.ChannelConnections">
    ...
      <property name="connectionsStr">
        <list>
          <value>WeIdentity@10.107.105.203:8812</value>
          <value>WeIdentity@10.107.105.229:8900</value>
        </list>
      </property>
    </bean>

* 修改 ``dist/conf/application.properties`` ，确认Server监听端口地址（此即为RestServer的HTTP端口地址）及HTTP重定向地址已设置且未被其他程序占用，否则请修改之。示例：

.. code-block:: bash

    # Server监听端口地址
    server.port=20191
    # HTTP重定向地址
    server.http.port=20190

2. Server使用说明
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

2.1 Server启动/停止
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

进入dist目录执行以下应用以启动或停止RestServer：

.. code-block:: bash

    # 为脚本文件增加权限
    $ chmod +x *.sh
    # 启动应用
    $ ./start.sh
    # 观察应用状态
    $ ./server_status.sh
    # 停止应用
    $ ./stop.sh 停止应用

执行 ``./start.sh`` 之后会输出以下提示，表示RestServer已经顺利启动：

.. code-block:: text

    ========================================================
    Starting com.webank.weid.http.Application ... [SUCCESS]
    ========================================================

有时候会提示Failed，请通过执行 ``./server_status.sh`` 确认RestServer已经成功启动：

.. code-block:: text

    ========================================================
    com.webank.weid.http.Application is running(PID=100891)
    ========================================================

执行 ``./stop.sh`` 之后会输出以下提示，表示RestServer已经顺利停止：

.. code-block:: text

    ========================================================
    Stopping com.webank.weid.http.Application ... [SUCCESS]
    ========================================================

3. 使用Postman访问RestServer的API
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

RestServer支持任何使用标准HTTP/HTTPS协议的RESTful API客户端访问，详细接口说明可见API文档。我们提供了一套Postman的环境与请求集供快速集成。使用步骤如下：

* 点击Postman的Import按钮，导入环境文件 ``weidentity-restservice.postman_environment.json`` 和请求集 ``invoke.postman_collection.json``
* 首先确认weidentity-restservice这个Environment已导入成功，它包含两个变量host和httpport
    * 修改环境变量host属性的值为安装部署RestServer的服务器地址
    * 修改环境变量httpport属性的值为1.3节中的Server监听端口地址
* 接下来确认Invoke这个Collection已导入成功，可以从侧边栏中找到
* 现在，可以调用Invoke这个Collection中的各类API了。您可以从无参数请求CreateWeId开始——看看返回结果是不是和API文档中一致，成功创建一个WeIdentity DID