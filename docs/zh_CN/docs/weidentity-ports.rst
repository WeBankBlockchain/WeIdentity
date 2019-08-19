# WeIdentity 网络端口

.. list-table::
   :header-rows: 1

   * - 默认端口
     - 协议说明
     - 源服务
     - 目的地服务
     - 端口描述
   * - 6080
     - HTTPS
     - 任意
     - WeIdentity RestService服务端
     - RestService监听外部RESTful API请求
   * - 6081
     - HTTP
     - 任意
     - WeIdentity RestService服务端
     - RestService监听外部RESTful API请求
   * - 6090
     - TCP
     - WeIdentity RestService服务端
     - Endpoint Service服务端
     - Endpoint Service监听来自RestService的RPC请求
   * - 6190
     - HTTPS
     - 任意
     - WeIdentity Sample服务端
     - Sample监听外部请求
   * - 6191
     - HTTP
     - 任意
     - WeIdentity Sample服务端
     - Sample监听外部请求
