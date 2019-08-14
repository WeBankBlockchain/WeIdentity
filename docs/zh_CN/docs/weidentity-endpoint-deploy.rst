
WeIdentity Endpoint Service部署指引
-------------------------------------

为了实现基于RPC的远程调用和转发机制，WeIdentity Endpoint Service分成两部分：代理端（也即RestService这一侧）和服务端（也即Java SDK这一侧）。其中，代理端负责直接接收HTTP/HTTPS请求，解析、进行Endpoint查询，并转发给对应的服务端。服务端负责接收请求，并直接转发给预先注册的Endpoint实现。因此，您需要按照以下顺序部署Endpoint Service。

1. 代理端部署
^^^^^^^^^^^^^^^^^^^

Endpoint Service在代理端依托于RestService，环境要求也与其一致，请见 \ `Endpoint Service 部署文档 <./weidentity-endpoint-deploy.html>`_\。

随后，在 ``dist/conf`` 目录下，修改 ``application.properties`` 文件中的主机端口列表这一项。您需要在此处以逗号分隔所有需要连接的远程服务端，指明其主机IP及端口。这样，Endpoint Service就会在后台以您配置的时间间隔（此处的 ``fetch.period.seconds`` ）去远程拉取注册在服务端的Endpoint。

.. code-block:: bash

    # 默认的服务端开启端口，用于在访问未指定服务端主机端口的主机时使用
    default.listener.port=10090
    # 向服务端周期拉取Endpoint配置的时间间隔，单位为秒
    fetch.period.seconds=60
    # 服务端所有主机端口列表
    server.hostport.list=127.0.0.1:10090,10.105.107.225:10090


2. 服务端部署
^^^^^^^^^^^^^^^^^^^

Endpoint Service在服务端依托于WeIdentity-Java-SDK，环境要求也与其一致，请见 `Java-SDK 部署环境要求 <https://weidentity.readthedocs.io/projects/javasdk/zh_CN/latest/docs/weidentity-installation.html>`_。

在 ``src/main/resources`` 目录下，修改 ``weidentity.properties`` 文件中的 ``rpc.listener.port`` 这一项内容，以具体确定在哪个端口上进行监听。

.. code-block:: bash

    # RPC服务端的监听端口
    rpc.listener.port=10090

随后，您可以通过以下命令操作RPC服务端：

.. code-block:: bash

    # 为脚本文件增加权限
    $ chmod +x *.sh
    # 启动应用
    $ ./rpc_start.sh
    # 观察应用状态
    $ ./rpc_health.sh
    # 停止应用
    $ ./rpc_stop.sh

执行 ``./rpc_start.sh`` 之后会输出以下提示，表示 RestServer 已经顺利启动：

.. code-block:: text

    ================================================================
    Starting com.webank.weid.suite.endpoint.RpcServer ... [SUCCESS]
    ================================================================

请您通过执行 ``./rpc_health.sh`` 确认 RestServer 已经成功启动：

.. code-block:: text

    ================================================================
    com.webank.weid.suite.endpoint.RpcServer is running(PID=100891)
    ================================================================

如果需要停止服务，请执行 ``./rpc_stop`` ，之后会输出以下提示，表示 RestServer 已经顺利停止：

.. code-block:: text

    ================================================================
    Stopping com.webank.weid.suite.endpoint.RpcServer ... [SUCCESS]
    ================================================================


3. 在服务端注册或注销您的Endpoint
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

为了注册您的Endpoint以调用所需的Java方法，您需要在服务端集成时，为您需要注册的每个Endpoint，实现一个对应的 ``EndpointFunctor`` 接口。此接口包括两个方法： ``execute()`` 和 ``getDescription()`` ，其中前者界定了具体当代理端的RPC请求发送过来时需要进行的操作，后者则需要提供一段对此接口功能的描述。在实现完成之后，还需要调用 ``EndpointHandler`` 类的 ``registerEndpoint()`` 方法进行注册。

您可以参考SampleEndpointFunctor.java的相关实现。

4. 在代理端调用您的Endpoint
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

当您注册Endpoint完毕之后，等待您之前所设定的拉取时间间隔，以保证此Endpoint被代理端正确地拉取到。随后，您就可以在代理端调用此接口了。

您可以参见\ `REST API文档 <./weidentity-endpoint-deploy.html>`_\ 中的“WeIdentity Endpoint Service API”一节，了解如何获取和调用注册的Endpoint。