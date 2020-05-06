.. role:: raw-html-m2r(raw)
   :format: html


1. registerCacheNode
-----------------------------

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.cache.CacheManager.registerCacheNode
   接口定义: <T> CacheNode<T> registerCacheNode(String cacheName, Long timeout)
   接口描述: 根据缓存名和超时时间来注册缓存节点，用于按模块来缓存数据。

.. note::
     注意：此接口生成的缓存节点，存储的数据最大个数为系统默认，默认值为1000, 同时可以通过weidentity.properties文件来修改某一个缓存模块的最大数据个数，例：caffeineCache.maximumSize.XXXX=100。XXX为缓存名。


**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - cacheName
     - String
     - Y
     - 缓存模块名
     -
   * - timeout
     - Long
     - Y
     - 缓存超时时间,单位:毫秒
     -


**接口返回**\ :   com.webank.weid.suite.cache.CacheNode<T>;

**调用示例**

.. code-block:: java
   CacheNode<String> cptCahceNode =
        CacheManager.registerCacheNode("USER_CPT", 1000 * 3600 * 24L);
   //存入缓存数据
   cptCahceNode.put("cptKey", "cptValue");
   //获取缓存数据
   String cptValue = cptCahceNode.get("cptKey");
   //移除缓存数据
   cptCahceNode.remove("cptKey")


2. registerCacheNode
-----------------------------

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.cache.CacheManager.registerCacheNode
   接口定义: <T> CacheNode<T> registerCacheNode(String cacheName, Long timeout, Integer maximumSize)
   接口描述: 根据缓存名和超时时间来注册缓存节点，用于按模块来缓存数据。

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - cacheName
     - String
     - Y
     - 缓存模块名
     -
   * - timeout
     - Long
     - Y
     - 缓存超时时间,单位:毫秒
     -
   * - maximumSize
     - Integer
     - Y
     - 缓存数据最大个数
     -

**接口返回**\ :   com.webank.weid.suite.cache.CacheNode<T>;

**调用示例**

.. code-block:: java
   CacheNode<String> cptCahceNode =
        CacheManager.registerCacheNode("USER_CPT", 1000 * 3600 * 24L, 1000);
   //存入缓存数据
   cptCahceNode.put("cptKey", "cptValue");
   //获取缓存数据
   String cptValue = cptCahceNode.get("cptKey");
   //移除缓存数据
   cptCahceNode.remove("cptKey")
----