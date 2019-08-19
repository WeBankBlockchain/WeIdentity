WeIdentity 网络端口
^^^^^^^^^^^^^^^^^^^^

.. list-table::
   :header-rows: 1

   * - 默认端口
     - 协议说明
     - 对应服务
     - 端口描述
   * - 6000
     - HTTPS
     - WeIdentity RestService服务端
     - RestService及Endpoint Service监听来自外部RESTful API请求
   * - 6001
     - HTTP
     - WeIdentity RestService服务端
     - RestService及Endpoint Service监听来自外部RESTful API请求
   * - 6010
     - TCP
     - Endpoint Service服务端，集成于Java-SDK
     - Endpoint Service服务端监听来自RestService的RPC请求
   * - 6100
     - HTTPS
     - WeIdentity Sample服务端
     - Sample监听来自外部请求
   * - 6101
     - HTTP
     - WeIdentity Sample服务端
     - Sample监听来自外部请求
