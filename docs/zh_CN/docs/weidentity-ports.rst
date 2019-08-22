WeIdentity 网络端口
^^^^^^^^^^^^^^^^^^^^

.. list-table::
   :header-rows: 1

   * - 默认端口
     - 预留范围
     - 协议说明
     - 对应服务
     - 端口描述
   * - 6000
     - 6000 ~ 6010
     - HTTPS
     - WeIdentity RestService服务端
     - RestService及Endpoint Service监听来自外部RESTful API请求
   * - 6001
     - 6000 ~ 6010
     - HTTP
     - WeIdentity RestService服务端
     - RestService及Endpoint Service监听来自外部RESTful API请求
   * - 6010
     - 6010 ~ 6020
     - TCP
     - Endpoint Service，集成于Java-SDK
     - Endpoint Service服务端监听来自RestService的RPC请求
   * - 6100
     - 6100 ~ 6110
     - HTTPS
     - WeIdentity Sample服务端
     - Sample监听来自外部请求
   * - 6101
     - 6101 ~ 6110
     - HTTP
     - WeIdentity Sample服务端
     - Sample监听来自外部请求

注：如果您需要修改对应服务的端口配置，请确认修改后的端口落入对应服务的“预留范围”中，以免和其他服务产生冲突。