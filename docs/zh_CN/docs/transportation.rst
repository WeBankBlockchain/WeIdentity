.. role:: raw-html-m2r(raw)
   :format: html

JsonTransportation
-----------------------------

1. specify
^^^^^^^^^^^^^^

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.JsonTransportation.specify
   接口定义: JsonTransportation specify(List<String> verifierWeIdList)
   接口描述: 指定transportation的认证者,用于权限控制。

**接口入参**\ :

java.util.List<java.lang.String>

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - verifierWeIdList
     - List<String>
     - N
     - verifierWeId列表
     -


**接口返回**\ :   com.webank.weid.suite.api.transportation.inf.JsonTransportation;

**调用示例**

.. code-block:: java

   JsonTransportation jsonTransportation =TransportationFactory.build(TransportationType.JSON);

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);
   jsonTransportation = jsonTransportation.specify(verifierWeIdList);


**时序图**

.. mermaid::


   sequenceDiagram
   participant 调用者
   participant JsonTransportation
   participant WeIdService
   participant 区块链
   调用者->>JsonTransportation: 调用specify()
   JsonTransportation->>JsonTransportation: 入参非空、格式及合法性检查
   opt 入参校验失败
   JsonTransportation-->>调用者: 报错，提示参数不合法并退出
   end
   loop 遍历每个WeID
   JsonTransportation->>WeIdService: 判断WeID的合法性，以及存在性，调用isWeIdExist()方法
   WeIdService->>区块链: 查询该WeID是否存在
   区块链-->>WeIdService: 返回查询结果
   WeIdService-->>JsonTransportation: 返回查询结果
   opt WeID不存在
   JsonTransportation-->>调用者: 报错，提示WeID不存在
   end
   JsonTransportation->>JsonTransportation: 放入verifier list里
   end
   JsonTransportation-->>调用者: 返回成功


2. serialize
^^^^^^^^^^^^^^

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.JsonTransportation.serialize
   接口定义: <T extends JsonSerializer> ResponseData<String> serialize(T object,ProtocolProperty property)
   接口描述: 用于序列化对象,要求对象实现JsonSerializer接口。

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - object
     - <T extends JsonSerializer>
     - Y
     - 待序列化对象
     -
   * - property
     - ProtocolProperty
     - Y
     - 协议配置
     -

**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<String>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     -
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - String
     - 序列化后的字符串数据
     - 业务数据


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - TRANSPORTATION_BASE_ERROR
     - 100800
     - transportation基本未知异常
   * - TRANSPORTATION_PROTOCOL_PROPERTY_ERROR
     - 100801
     - 协议配置异常
   * - TRANSPORTATION_PROTOCOL_ENCODE_ERROR
     - 100803
     - 协议配置Encode异常
   * - TRANSPORTATION_PROTOCOL_DATA_INVALID
     - 100805
     - 协议数据无效
   * - TRANSPORTATION_ENCODE_BASE_ERROR
     - 100807
     - Encode基本未知异常
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - dataKey无效
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - BASE_ERROR
     - 160007
     - weId基础未知异常
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常


**调用示例**

.. code-block:: java

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);

   PresentationE presentation;

   //原文方式调用
   ResponseData<String> result1 =
       TransportationFactory
           .build(TransportationType.JSON)
           .specify(verifierWeIdList)
           .serialize(presentation,new ProtocolProperty(EncodeType.ORIGINAL));

   //密文方式调用
   ResponseData<String> result2 =
      TransportationFactory
           .build(TransportationType.JSON)
           .specify(verifierWeIdList)
           .serialize(presentation,new ProtocolProperty(EncodeType.CIPHER));



**时序图**

.. mermaid::


  sequenceDiagram
  participant 调用者
  participant JsonTransportation
  调用者->>JsonTransportation: 调用serialize()
  JsonTransportation->>JsonTransportation: 入参非空、格式及合法性检查
  opt 入参校验失败
  JsonTransportation-->>调用者: 报错，提示参数不合法并退出
  end
  JsonTransportation->>JsonTransportation: 拼装Json格式的协议头数据
  JsonTransportation->>JsonTransportation: 判断是采用加密方式还是非加密方式
  opt 非加密方式
  JsonTransportation->>JsonTransportation: 将presentation原文放入协议里
  end
  opt 加密方式
  JsonTransportation->>EncodeProcessor: 调用encode方法
  EncodeProcessor->>EncodeProcessor: 采用AES算法，生成对称加密秘钥
  EncodeProcessor->>persistence: 保存至存储库里
  persistence-->>EncodeProcessor: 返回
  EncodeProcessor-->>JsonTransportation: 返回加密之后的presentation数据
  JsonTransportation->>JsonTransportation: 将presentation密文放入协议里
  end
  JsonTransportation->>DataToolUtils: 调用objToJsonStrWithNoPretty()将协议序列化成Json数据
  DataToolUtils-->>JsonTransportation:返回包含presentation的Json数据
  JsonTransportation-->>调用者: 返回成功


3. deserialize
^^^^^^^^^^^^^^

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.JsonTransportation.deserialize
   接口定义: <T extends JsonSerializer> ResponseData<T> deserialize(WeIdAuthentication weIdAuthentication, String transString,Class<T> clazz)
   接口描述: 用于反序列化对象,要求目标对象实现JsonSerializer接口。

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - weIdAuthentication
     - WeIdAuthentication
     - Y
     - 调用者身份信息
     -
  * - transString
     - String
     - Y
     - 待序列化对象
     -
   * - clazz
     - Class<T>
     - Y
     - 目标类型
     -

**接口返回**\ :  <T extends JsonSerializer> com.webank.weid.protocol.response.ResponseData\<T>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     -
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - <T extends JsonSerializer>
     - 反序列化后的对象
     - 业务数据

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - ENCRYPT_KEY_NOT_EXISTS
     - 100700
     -  无法获取秘钥
   * - TRANSPORTATION_BASE_ERROR
     - 100800
     - transportation基本未知异常
   * - TRANSPORTATION_PROTOCOL_VERSION_ERROR
     - 100802
     - 协议版本错误
   * - TRANSPORTATION_PROTOCOL_ENCODE_ERROR
     - 100803
     - 协议配置Encode异常
   * - TRANSPORTATION_PROTOCOL_DATA_INVALID
     - 100805
     - 协议数据无效
   * - TRANSPORTATION_ENCODE_BASE_ERROR
     - 100807
     - Encode基本未知异常
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - dataKey无效
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - BASE_ERROR
     - 160007
     - weId基础未知异常
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常
   * - DIRECT_ROUTE_REQUEST_TIMEOUT
     - 160009
     - AMOP超时
   * - DIRECT_ROUTE_MSG_BASE_ERROR
     - 160010
     - AMOP异常
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常


**调用示例**

.. code-block:: java

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);

   String transString="";
   WeIdAuthentication weIdAuthentication;
   //原文方式调用反序列化
   ResponseData<PresentationE> result1 =
       TransportationFactory
           .build(TransportationType.JSON)
           .deserialize(weIdAuthentication,transString,PresentationE.class);


**时序图**

.. mermaid::


   sequenceDiagram
   participant 调用者
   participant JsonTransportation
   调用者->>JsonTransportation: 调用deserialize()
   JsonTransportation->>JsonTransportation: 入参非空、格式及合法性检查
   opt 入参校验失败
   JsonTransportation-->>调用者: 报错，提示参数不合法并退出
   end
   JsonTransportation->>DataToolUtils: 调用deserialize()方法，反序列化协议数据
   DataToolUtils-->>JsonTransportation:返回Json格式的协议数据
   JsonTransportation->>JsonTransportation: 解析协议，判断是采用加密方式还是非加密方式
   opt 非加密方式
   JsonTransportation->>DataToolUtils: 调用deserialize方法将协议里的presentation反序列化为对象
   DataToolUtils-->>JsonTransportation: 返回PresentationE对象
   end
   opt 加密方式
   JsonTransportation->>EncodeProcessor: 调用decode方法
   EncodeProcessor->>User Agent: 发送AMOP请求，获取对称加密秘钥
   User Agent-->>EncodeProcessor: 返回加密秘钥
   EncodeProcessor->>EncodeProcessor: 解密协议数据
   EncodeProcessor-->>JsonTransportation: 返回解密后的presentation数据
   JsonTransportation->>DataToolUtils: 调用deserialize方法将协议里的presentation反序列化
   DataToolUtils-->>JsonTransportation: 返回PresentationE对象presentation反序列化为对象
   end

 JsonTransportation-->>调用者: 返回成功


QrCodeTransportation
------------------------

1. specify
^^^^^^^^^^^^^^

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.QrCodeTransportation.specify
   接口定义: JsonTransportation specify(List<String> verifierWeIdList)
   接口描述: 指定transportation的认证者,用于权限控制。

**接口入参**\ :

java.util.List<java.lang.String>

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - verifierWeIdList
     - List<String>
     - N
     - verifierWeId列表
     -


**接口返回**\ :   com.webank.weid.suite.api.transportation.inf.JsonTransportation;

**调用示例**

.. code-block:: java

   Transportation transportation =TransportationFactory.build(TransportationType.QR_CODE);

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);
   transportation = transportation.specify(verifierWeIdList);


**时序图**

.. mermaid::


   sequenceDiagram
   participant 调用者
   participant QrCodeTransportation
   participant WeIdService
   participant 区块链
   调用者->>QrCodeTransportation: 调用specify()
   QrCodeTransportation->>QrCodeTransportation: 入参非空、格式及合法性检查
   opt 入参校验失败
   QrCodeTransportation-->>调用者: 报错，提示参数不合法并退出
   end
   loop 遍历每个WeID
   QrCodeTransportation->>WeIdService: 判断WeID的合法性，以及存在性，调用isWeIdExist()方法
   WeIdService->>区块链: 查询该WeID是否存在
   区块链-->>WeIdService: 返回查询结果
   WeIdService-->>QrCodeTransportation: 返回查询结果
   opt WeID不存在
   QrCodeTransportation-->>调用者: 报错，提示WeID不存在
   end
   QrCodeTransportation->>QrCodeTransportation: 放入verifier list里
   end
   QrCodeTransportation-->>调用者: 返回成功


2. serialize
^^^^^^^^^^^^^^

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.QrCodeTransportation.serialize
   接口定义: <T extends JsonSerializer> ResponseData<String> serialize(T object,ProtocolProperty property)
   接口描述: 用于序列化对象,要求对象实现JsonSerializer接口，此接口仅支持数据模式

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - object
     - <T extends JsonSerializer>
     - Y
     - 待序列化对象
     -
   * - property
     - ProtocolProperty
     - Y
     - 协议配置
     -

**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<String>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     -
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - String
     - 序列化后的字符串数据
     - 业务数据


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - TRANSPORTATION_PROTOCOL_PROPERTY_ERROR
     - 100801
     - 协议配置异常
   * - TRANSPORTATION_PROTOCOL_ENCODE_ERROR
     - 100803
     - 协议配置Encode异常
   * - TRANSPORTATION_PROTOCOL_DATA_INVALID
     - 100805
     - 协议数据无效
   * - TRANSPORTATION_ENCODE_BASE_ERROR
     - 100807
     - Encode基本未知异常
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - id无效
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - BASE_ERROR
     - 160007
     - weId基础未知异常
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常


**调用示例**

.. code-block:: java

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);

   PresentationE presentation;

   //数据模式
   //原文方式调用
   ResponseData<String> result1 =
       TransportationFactory
           .build(TransportationType.QR_CODE)
           .specify(verifierWeIdList)
           .serialize(presentation,new ProtocolProperty(EncodeType.ORIGINAL));

   //密文方式调用
   ResponseData<String> result2 =
      TransportationFactory
           .build(TransportationType.QR_CODE)
           .specify(verifierWeIdList)
           .serialize(presentation,new ProtocolProperty(EncodeType.CIPHER));


**时序图**

.. mermaid::


  sequenceDiagram
  participant 调用者
  participant QrCodeTransportation
  调用者->>QrCodeTransportation: 调用serialize()
  QrCodeTransportation->>QrCodeTransportation: 入参非空、格式及合法性检查
  opt 入参校验失败
  QrCodeTransportation-->>调用者: 报错，提示参数不合法并退出
  end
  QrCodeTransportation->>QrCodeTransportation: 拼装协议头数据
  QrCodeTransportation->>QrCodeTransportation: 判断是采用加密方式还是非加密方式
  opt 非加密方式
  QrCodeTransportation->>QrCodeTransportation: 将presentation原文放入协议里
  end
  opt 加密方式
  QrCodeTransportation->>EncodeProcessor: 调用encode方法
  EncodeProcessor->>EncodeProcessor: 采用AES算法，生成对称加密秘钥
  EncodeProcessor->>persistence: 保存至存储库里
  persistence-->>EncodeProcessor: 返回
  EncodeProcessor-->>QrCodeTransportation: 返回加密之后的presentation数据
  QrCodeTransportation->>QrCodeTransportation: 将presentation密文放入协议里
  end
  QrCodeTransportation-->>调用者: 返回QRCode协议数据


3. serialize
^^^^^^^^^^^^^^^


**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.QrCodeTransportation.serialize
   接口定义: <T extends JsonSerializer> ResponseData<String> serialize(WeIdAuthentication weIdAuthentication, T object,ProtocolProperty property)
   接口描述: 用于序列化对象,要求对象实现JsonSerializer接口，此接口支持将纯数据编入二维码协议，也支持将资源Id编入二维码协议进行远程下载

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - weIdAuthentication
     - WeIdAuthentication
     - Y
     - 调用者身份信息
     -
   * - object
     - <T extends JsonSerializer>
     - Y
     - 待序列化对象
     -
   * - property
     - ProtocolProperty
     - Y
     - 协议配置
     -

**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<String>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     -
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - String
     - 序列化后的字符串数据
     - 业务数据


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - TRANSPORTATION_PROTOCOL_PROPERTY_ERROR
     - 100801
     - 协议配置异常
   * - TRANSPORTATION_PROTOCOL_ENCODE_ERROR
     - 100803
     - 协议配置Encode异常
   * - TRANSPORTATION_PROTOCOL_DATA_INVALID
     - 100805
     - 协议数据无效
   * - TRANSPORTATION_ENCODE_BASE_ERROR
     - 100807
     - Encode基本未知异常
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - id无效
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - BASE_ERROR
     - 160007
     - weId基础未知异常
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常


**调用示例**

.. code-block:: java

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);

   PresentationE presentation;
   WeIdAuthentication weIdAuthentication;
   //数据模式
   //原文方式调用
   ResponseData<String> result1 =
       TransportationFactory
           .build(TransportationType.QR_CODE)
           .specify(verifierWeIdList)
           .serialize(weIdAuthentication, presentation,new ProtocolProperty(EncodeType.ORIGINAL));

   //密文方式调用
   ResponseData<String> result2 =
      TransportationFactory
           .build(TransportationType.QR_CODE)
           .specify(verifierWeIdList)
           .serialize(weIdAuthentication, presentation,new ProtocolProperty(EncodeType.CIPHER));

   //下载模式
   //原文方式调用
   ResponseData<String> result3 =
       TransportationFactory
           .build(TransportationType.QR_CODE)
           .specify(verifierWeIdList)
           .serialize(weIdAuthentication, presentation,new ProtocolProperty(EncodeType.ORIGINAL, TransMode.DOWNLOAD_MODE));

   //密文方式调用
   ResponseData<String> result4 =
      TransportationFactory
           .build(TransportationType.QR_CODE)
           .specify(verifierWeIdList)
           .serialize(weIdAuthentication, presentation,new ProtocolProperty(EncodeType.CIPHER, TransMode.DOWNLOAD_MODE));


**时序图**

.. mermaid::


  sequenceDiagram
  participant 调用者
  participant QrCodeTransportation
  调用者->>QrCodeTransportation: 调用serialize()
  QrCodeTransportation->>QrCodeTransportation: 入参非空、格式及合法性检查
  opt 入参校验失败
  QrCodeTransportation-->>调用者: 报错，提示参数不合法并退出
  end
  QrCodeTransportation->>QrCodeTransportation: 拼装协议头数据
  QrCodeTransportation->>QrCodeTransportation: 判断是采用加密方式还是非加密方式
  opt 非加密方式
  QrCodeTransportation->>QrCodeTransportation: 将presentation原文放入协议里
  end
  opt 加密方式
  QrCodeTransportation->>EncodeProcessor: 调用encode方法
  EncodeProcessor->>EncodeProcessor: 采用AES算法，生成对称加密秘钥
  EncodeProcessor->>persistence: 保存至存储库里
  persistence-->>EncodeProcessor: 返回
  EncodeProcessor-->>QrCodeTransportation: 返回加密之后的presentation数据
  QrCodeTransportation->>QrCodeTransportation: 将presentation密文放入协议里
  end
  QrCodeTransportation-->>调用者: 返回QRCode协议数据


4. deserialize
^^^^^^^^^^^^^^^^^

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.QrCodeTransportation.deserialize
   接口定义: <T extends JsonSerializer> ResponseData<T> deserialize(WeIdAuthentication weIdAuthentication, String transString,Class<T> clazz)
   接口描述: 用于反序列化对象,要求目标对象实现JsonSerializer接口。

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - weIdAuthentication
     - WeIdAuthentication
     - Y
     - 调用者身份信息
     -
   * - transString
     - String
     - Y
     - 待序列化对象
     -
   * - clazz
     - Class<T>
     - Y
     - 目标类型
     -

**接口返回**\ :  <T extends JsonSerializer> com.webank.weid.protocol.response.ResponseData\<T>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     -
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - <T extends JsonSerializer>
     - 反序列化后的对象
     - 业务数据

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - ENCRYPT_KEY_NOT_EXISTS
     - 100700
     - 无法获取秘钥
   * - TRANSPORTATION_PROTOCOL_VERSION_ERROR
     - 100802
     - 协议版本错误
   * - TRANSPORTATION_PROTOCOL_STRING_INVALID
     - 100804
     - 协议字符串无效
   * - TRANSPORTATION_ENCODE_BASE_ERROR
     - 100807
     - Encode基本未知异常
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - id无效
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - BASE_ERROR
     - 160007
     - weId基础未知异常
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常
   * - DIRECT_ROUTE_REQUEST_TIMEOUT
     - 160009
     - AMOP超时
   * - DIRECT_ROUTE_MSG_BASE_ERROR
     - 160010
     - AMOP异常
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常


**调用示例**

.. code-block:: java

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);

   String transString="";
   WeIdAuthentication weIdAuthentication;
   //调用反序列化
   ResponseData<PresentationE> result1 =
       TransportationFactory
           .build(TransportationType.QR_CODE)
           .deserialize(weIdAuthentication, transString, PresentationE.class);

**时序图**

.. mermaid::


   sequenceDiagram
   participant 调用者
   participant QrCodeTransportation
   调用者->>QrCodeTransportation: 调用deserialize()
   QrCodeTransportation->>QrCodeTransportation: 入参非空、格式及合法性检查
   opt 入参校验失败
   QrCodeTransportation-->>调用者: 报错，提示参数不合法并退出
   end
   QrCodeTransportation->>QrCodeTransportation: 解析协议，判断是采用加密方式还是非加密方式
   opt 非加密方式
   QrCodeTransportation->>DataToolUtils: 调用deserialize方法将协议里的presentation反序列化为对象
   DataToolUtils-->>QrCodeTransportation: 返回PresentationE对象
   end
   opt 加密方式
   QrCodeTransportation->>EncodeProcessor: 调用decode方法
   EncodeProcessor->>User Agent: 发送AMOP请求，获取对称加密秘钥
   User Agent-->>EncodeProcessor: 返回加密秘钥
   EncodeProcessor->>EncodeProcessor: 解密协议数据
   EncodeProcessor-->>QrCodeTransportation: 返回解密后的presentation数据
   QrCodeTransportation->>DataToolUtils: 调用deserialize方法将协议里的presentation反序列化
   DataToolUtils-->>QrCodeTransportation: 返回PresentationE对象presentation反序列化为对象
   end

 QrCodeTransportation-->>调用者: 返回成功



BarCodeTransportation
--------------------------

1. specify
^^^^^^^^^^^^^^^^^

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.Transportation.specify
   接口定义: Transportation specify(List<String> verifierWeIdList)
   接口描述: 指定transportation的认证者,用于权限控制。

**接口入参**\ :

java.util.List<java.lang.String>

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - verifierWeIdList
     - List<String>
     - N
     - verifierWeId列表
     -


**接口返回**\ :   com.webank.weid.suite.api.transportation.inf.JsonTransportation;

**调用示例**

.. code-block:: java

   Transportation transportation =TransportationFactory.build(TransportationType.QR_CODE);

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);
   transportation = transportation.specify(verifierWeIdList);


**时序图**

.. mermaid::


   sequenceDiagram
   participant 调用者
   participant Transportation
   participant WeIdService
   participant 区块链
   调用者->>Transportation: 调用specify()
   Transportation->>Transportation: 入参非空、格式及合法性检查
   opt 入参校验失败
   Transportation-->>调用者: 报错，提示参数不合法并退出
   end
   loop 遍历每个WeID
   Transportation->>WeIdService: 判断WeID的合法性，以及存在性，调用isWeIdExist()方法
   WeIdService->>区块链: 查询该WeID是否存在
   区块链-->>WeIdService: 返回查询结果
   WeIdService-->>Transportation: 返回查询结果
   opt WeID不存在
   Transportation-->>调用者: 报错，提示WeID不存在
   end
   Transportation->>Transportation: 放入verifier list里
   end
   Transportation-->>调用者: 返回成功


2. serialize
^^^^^^^^^^^^^^^^^

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.Transportation.serialize
   接口定义: <T extends JsonSerializer> ResponseData<String> serialize(WeIdAuthentication weIdAuthentication, T object,ProtocolProperty property)
   接口描述: 用于序列化对象,要求对象实现JsonSerializer接口，接口将资源数据存入数据库，然后通过资源Id来进行关联，并将资源Id编入协议字符串中

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - weIdAuthentication
     - WeIdAuthentication
     - Y
     - 调用者身份信息
     -
   * - object
     - <T extends JsonSerializer>
     - Y
     - 待序列化对象
     -
   * - property
     - ProtocolProperty
     - Y
     - 协议配置
     -

**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<String>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     -
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - String
     - 序列化后的字符串数据
     - 业务数据


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - TRANSPORTATION_PROTOCOL_PROPERTY_ERROR
     - 100801
     - 协议配置异常
   * - TRANSPORTATION_PROTOCOL_ENCODE_ERROR
     - 100803
     - 协议配置Encode异常
   * - TRANSPORTATION_PROTOCOL_DATA_INVALID
     - 100805
     - 协议数据无效
   * - TRANSPORTATION_ENCODE_BASE_ERROR
     - 100807
     - Encode基本未知异常
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - id无效
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - BASE_ERROR
     - 160007
     - weId基础未知异常
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常


**调用示例**

.. code-block:: java

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);

   PresentationE presentation;

   //下载模式
   //原文方式调用
   ResponseData<String> result3 =
       TransportationFactory
           .build(TransportationType.QR_CODE)
           .specify(verifierWeIdList)
           .serialize(presentation,new ProtocolProperty(EncodeType.ORIGINA));

   //密文方式调用
   ResponseData<String> result4 =
      TransportationFactory
           .build(TransportationType.QR_CODE)
           .specify(verifierWeIdList)
           .serialize(presentation,new ProtocolProperty(EncodeType.CIPHER));


**时序图**

.. mermaid::


  sequenceDiagram
  participant 调用者
  participant Transportation
  调用者->>Transportation: 调用serialize()
  Transportation->>Transportation: 入参非空、格式及合法性检查
  opt 入参校验失败
  Transportation-->>调用者: 报错，提示参数不合法并退出
  end
  Transportation->>Transportation: 拼装协议头数据
  Transportation->>Transportation: 判断是采用加密方式还是非加密方式
  opt 非加密方式
  Transportation->>Transportation: 将presentation原文放入协议里
  end
  opt 加密方式
  Transportation->>EncodeProcessor: 调用encode方法
  EncodeProcessor->>EncodeProcessor: 采用AES算法，生成对称加密秘钥
  EncodeProcessor->>persistence: 保存至存储库里
  persistence-->>EncodeProcessor: 返回
  EncodeProcessor-->>Transportation: 返回加密之后的presentation数据
  Transportation->>Transportation: 将presentation密文放入协议里
  end
  Transportation-->>调用者: 返回QRCode协议数据


3. deserialize
^^^^^^^^^^^^^^^^^

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.Transportation.deserialize
   接口定义: <T extends JsonSerializer> ResponseData<T> deserialize(WeIdAuthentication weIdAuthentication, String transString,Class<T> clazz)
   接口描述: 用于反序列化对象,要求目标对象实现JsonSerializer接口。

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - weIdAuthentication
     - WeIdAuthentication
     - Y
     - 调用者身份信息
     -
   * - transString
     - String
     - Y
     - 待序列化对象
     -
   * - clazz
     - Class<T>
     - Y
     - 目标类型
     -

**接口返回**\ :  <T extends JsonSerializer> com.webank.weid.protocol.response.ResponseData\<T>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     -
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - <T extends JsonSerializer>
     - 反序列化后的对象
     - 业务数据

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - ENCRYPT_KEY_NOT_EXISTS
     - 100700
     - 无法获取秘钥
   * - TRANSPORTATION_PROTOCOL_VERSION_ERROR
     - 100802
     - 协议版本错误
   * - TRANSPORTATION_PROTOCOL_STRING_INVALID
     - 100804
     - 协议字符串无效
   * - TRANSPORTATION_ENCODE_BASE_ERROR
     - 100807
     - Encode基本未知异常
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - id无效
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - BASE_ERROR
     - 160007
     - weId基础未知异常
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常
   * - DIRECT_ROUTE_REQUEST_TIMEOUT
     - 160009
     - AMOP超时
   * - DIRECT_ROUTE_MSG_BASE_ERROR
     - 160010
     - AMOP异常
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常


**调用示例**

.. code-block:: java

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);

   String transString="";
   WeIdAuthentication weIdAuthentication;
   //调用反序列化
   ResponseData<PresentationE> result1 =
       TransportationFactory
           .build(TransportationType.BAR_CODE)
           .deserialize(weIdAuthentication, transString, PresentationE.class);


PdfTransportation
------------------------

1. specify
^^^^^^^^^^^^^^^^^

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.PdfTransportation.specify
   接口定义: PdfTransportation specify(List<String> verifierWeIdList)
   接口描述: 指定transportation的认证者,用于权限控制。

**接口入参**\ :

java.util.List<java.lang.String>

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - verifierWeIdList
     - List<String>
     - N
     - verifierWeId列表
     -


**接口返回**\ :   com.webank.weid.suite.api.transportation.inf.PdfTransportation;

**调用示例**

.. code-block:: java

   PPdfTransportation pdfTransportation = TransportationFactory.newPdfTransportation();

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);
   pdfTransportation = PdfTransportation.specify(verifierWeIdList);


2. serialize
^^^^^^^^^^^^^^^^^

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.PdfTransportation.serialize
   接口定义: <T extends JsonSerializer> ResponseData<byte[]> serialize(T object, ProtocolProperty property, WeIdAuthentication weIdAuthentication);
   接口描述: 用于序列化对象,要求对象实现JsonSerializer接口

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - object
     - <T extends JsonSerializer>
     - Y
     - 待序列化对象
     -
   * - property
     - ProtocolProperty
     - Y
     - 协议配置
     -
   * - weIdAuthentication
     - WeIdAuthentication
     - Y
     - WeID公私钥信息
     -

**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<byte[]>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     -
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - byte[]
     - 序列化后PDF文件的byte数组
     - 业务数据


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CREDENTIAL_ISSUER_MISMATCH
     - 100403
     - issuerWeId跟Credential中的issuer不匹配
   * - CREDENTIAL_EVIDENCE_SIGNATURE_BROKEN
     - 100431
     - 存证签名异常
   * - CREDENTIAL_EVIDENCE_BASE_ERROR
     - 100500
     - Evidence标准错误
   * - CREDENTIAL_EVIDENCE_HASH_MISMATCH
     - 100501
     - Evidence Hash不匹配
   * - TRANSPORTATION_BASE_ERROR
     - 100800
     - transportation基本未知异常
   * - TRANSPORTATION_PROTOCOL_PROPERTY_ERROR
     - 100801
     - 协议配置异常
   * - TRANSPORTATION_PROTOCOL_ENCODE_ERROR
     - 100803
     - 协议配置Encode异常
   * - TRANSPORTATION_ENCODE_BASE_ERROR
     - 100807
     - Encode基本未知异常
   * - TRANSPORTATION_PDF_TRANSFER_ERROR
     - 100808
     - Pdf转换异常
   * - WEID_AUTHORITY_INVALID
     - 100109
     - 授权信息无效
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥与WeIdentity DID不匹配
   * - WEID_DOES_NOT_EXIST
     - 100104
     - WeIdentity DID不存在
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - dataKey无效
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常






**调用示例**

.. code-block:: java

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);

   PresentationE presentation;
   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();;

   //原文方式调用
   ResponseData<byte[]> result1 =
       TransportationFactory
           .newPdfTransportation()
           .specify(verifierWeIdList)
           .serialize(presentation,new ProtocolProperty(EncodeType.ORIGINAL),weIdAuthentication);

   //密文方式调用
   ResponseData<byte[]> result2 =
      TransportationFactory
           .newPdfTransportation()
           .specify(verifierWeIdList)
           .serialize(presentation,new ProtocolProperty(EncodeType.CIPHER),weIdAuthentication);


3. serialize
^^^^^^^^^^^^^^^^^

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.PdfTransportation.serialize
   接口定义: <T extends JsonSerializer> ResponseData<Boolean> serialize(T object, ProtocolProperty property, WeIdAuthentication weIdAuthentication,String outputPdfFilePath);
   接口描述: 用于序列化对象并输出PDF文件,要求对象实现JsonSerializer接口

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - object
     - <T extends JsonSerializer>
     - Y
     - 待序列化对象
     -
   * - property
     - ProtocolProperty
     - Y
     - 协议配置
     -
   * - weIdAuthentication
     - WeIdAuthentication
     - Y
     - WeID公私钥信息
     -
   * - outputPdfFilePath
     - String
     - Y
     - 输出文件的路径
     -

**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<Boolean>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     -
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - Boolean
     - 序列化生成文件的结果
     -


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CREDENTIAL_ISSUER_MISMATCH
     - 100403
     - issuerWeId跟Credential中的issuer不匹配
   * - CREDENTIAL_EVIDENCE_SIGNATURE_BROKEN
     - 100431
     - 存证签名异常
   * - CREDENTIAL_EVIDENCE_BASE_ERROR
     - 100500
     - Evidence标准错误
   * - CREDENTIAL_EVIDENCE_HASH_MISMATCH
     - 100501
     - Evidence Hash不匹配
   * - TRANSPORTATION_BASE_ERROR
     - 100800
     - transportation基本未知异常
   * - TRANSPORTATION_PROTOCOL_PROPERTY_ERROR
     - 100801
     - 协议配置异常
   * - TRANSPORTATION_PROTOCOL_ENCODE_ERROR
     - 100803
     - 协议配置Encode异常
   * - TRANSPORTATION_ENCODE_BASE_ERROR
     - 100807
     - Encode基本未知异常
   * - TRANSPORTATION_PDF_TRANSFER_ERROR
     - 100808
     - Pdf转换异常
   * - WEID_AUTHORITY_INVALID
     - 100109
     - 授权信息无效
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥与WeIdentity DID不匹配
   * - WEID_DOES_NOT_EXIST
     - 100104
     - WeIdentity DID不存在
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - dataKey无效
   * - ILLEGAL_INPUT
     - 160004
     - 参数非法
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常






**调用示例**

.. code-block:: java

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);

   PresentationE presentation;
   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();;

   //原文方式调用
   ResponseData<byte[]> result1 =
       TransportationFactory
           .newPdfTransportation()
           .specify(verifierWeIdList)
           .serialize(presentation,new ProtocolProperty(EncodeType.ORIGINAL),weIdAuthentication,"./");

   //密文方式调用
   ResponseData<byte[]> result2 =
      TransportationFactory
           .newPdfTransportation()
           .specify(verifierWeIdList)
           .serialize(presentation,new ProtocolProperty(EncodeType.CIPHER),weIdAuthentication,"./");


4. serializeWithTemplate
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.PdfTransportation.serializeWithTemplate
   接口定义: <T extends JsonSerializer> ResponseData<byte[]> serializeWithTemplate(T object, ProtocolProperty property, WeIdAuthentication weIdAuthentication,String inputPdfTemplatePath);
   接口描述: 用于序列化对象,要求对象实现JsonSerializer接口

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - object
     - <T extends JsonSerializer>
     - Y
     - 待序列化对象
     -
   * - property
     - ProtocolProperty
     - Y
     - 协议配置
     -
   * - weIdAuthentication
     - WeIdAuthentication
     - Y
     - WeID公私钥信息
     -
   * - inputPdfTemplatePath
     - String
     - Y
     - 指定模板位置
     -

**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<byte[]>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     -
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - byte[]
     - 序列化后PDF文件的byte数组
     - 业务数据


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CREDENTIAL_ISSUER_MISMATCH
     - 100403
     - issuerWeId跟Credential中的issuer不匹配
   * - CREDENTIAL_EVIDENCE_SIGNATURE_BROKEN
     - 100431
     - 存证签名异常
   * - CREDENTIAL_EVIDENCE_BASE_ERROR
     - 100500
     - Evidence标准错误
   * - CREDENTIAL_EVIDENCE_HASH_MISMATCH
     - 100501
     - Evidence Hash不匹配
   * - TRANSPORTATION_BASE_ERROR
     - 100800
     - transportation基本未知异常
   * - TRANSPORTATION_PROTOCOL_PROPERTY_ERROR
     - 100801
     - 协议配置异常
   * - TRANSPORTATION_PROTOCOL_ENCODE_ERROR
     - 100803
     - 协议配置Encode异常
   * - TRANSPORTATION_ENCODE_BASE_ERROR
     - 100807
     - Encode基本未知异常
   * - TRANSPORTATION_PDF_TRANSFER_ERROR
     - 100808
     - Pdf转换异常
   * - WEID_AUTHORITY_INVALID
     - 100109
     - 授权信息无效
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥与WeIdentity DID不匹配
   * - WEID_DOES_NOT_EXIST
     - 100104
     - WeIdentity DID不存在
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - dataKey无效
   * - ILLEGAL_INPUT
     - 160004
     - 参数非法
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常






**调用示例**

.. code-block:: java

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);

   PresentationE presentation;
   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();

   //原文方式调用
   ResponseData<byte[]> result1 =
       TransportationFactory
           .newPdfTransportation()
           .specify(verifierWeIdList)
           .serializeWithTemplate(
               presentation,
               new ProtocolProperty(EncodeType.ORIGINAL),
               weIdAuthentication,
               "./test-template.pdf");

   //密文方式调用
   ResponseData<byte[]> result2 =
      TransportationFactory
           .newPdfTransportation()
           .specify(verifierWeIdList)
           .serializeWithTemplate(
               presentation,
               new ProtocolProperty(EncodeType.CIPHER),
               weIdAuthentication,
               "./test-template.pdf");


5. serializeWithTemplate
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.PdfTransportation.serializeWithTemplate
   接口定义: <T extends JsonSerializer> ResponseData<Boolean> serializeWithTemplate(T object, ProtocolProperty property, WeIdAuthentication weIdAuthentication,String inputPdfTemplatePath,String outputPdfFilePath);
   接口描述: 用于序列化对象,要求对象实现JsonSerializer接口

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - object
     - <T extends JsonSerializer>
     - Y
     - 待序列化对象
     -
   * - property
     - ProtocolProperty
     - Y
     - 协议配置
     -
   * - weIdAuthentication
     - WeIdAuthentication
     - Y
     - WeID公私钥信息
     -
   * - inputPdfTemplatePath
     - String
     - Y
     - 指定模板位置
     -
   * - outputPdfFilePath
     - String
     - Y
     - 输出PDF文件位置
     -


**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<Boolean>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     -
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - Boolean
     - 序列化生成文件的结果
     -


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CREDENTIAL_ISSUER_MISMATCH
     - 100403
     - issuerWeId跟Credential中的issuer不匹配
   * - CREDENTIAL_EVIDENCE_SIGNATURE_BROKEN
     - 100431
     - 存证签名异常
   * - CREDENTIAL_EVIDENCE_BASE_ERROR
     - 100500
     - Evidence标准错误
   * - CREDENTIAL_EVIDENCE_HASH_MISMATCH
     - 100501
     - Evidence Hash不匹配
   * - TRANSPORTATION_BASE_ERROR
     - 100800
     - transportation基本未知异常
   * - TRANSPORTATION_PROTOCOL_PROPERTY_ERROR
     - 100801
     - 协议配置异常
   * - TRANSPORTATION_PROTOCOL_ENCODE_ERROR
     - 100803
     - 协议配置Encode异常
   * - TRANSPORTATION_ENCODE_BASE_ERROR
     - 100807
     - Encode基本未知异常
   * - TRANSPORTATION_PDF_TRANSFER_ERROR
     - 100808
     - Pdf转换异常
   * - WEID_AUTHORITY_INVALID
     - 100109
     - 授权信息无效
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥与WeIdentity DID不匹配
   * - WEID_DOES_NOT_EXIST
     - 100104
     - WeIdentity DID不存在
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - dataKey无效
   * - ILLEGAL_INPUT
     - 160004
     - 参数非法
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常






**调用示例**

.. code-block:: java

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);

   PresentationE presentation;
   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();

   //原文方式调用
   ResponseData<byte[]> result1 =
       TransportationFactory
           .newPdfTransportation()
           .specify(verifierWeIdList)
           .serializeWithTemplate(
               presentation,
               new ProtocolProperty(EncodeType.ORIGINAL),
               weIdAuthentication,
               "./test-template.pdf",
               "./");

   //密文方式调用
   ResponseData<byte[]> result2 =
      TransportationFactory
           .newPdfTransportation()
           .specify(verifierWeIdList)
           .serializeWithTemplate(
               presentation,
               new ProtocolProperty(EncodeType.CIPHER),
               weIdAuthentication,
               "./test-template.pdf",
               "./");

6. deserialize
^^^^^^^^^^^^^^^^^

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.PdfTransportation.deserialize
   接口定义: <T extends JsonSerializer> ResponseData<T> deserialize(byte[] pdfTransportation, Class clazz, WeIdAuthentication weIdAuthentication);
   接口描述: 用于反序列化对象,要求目标对象实现JsonSerializer接口。

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - pdfTransportation
     - byte[ ]
     - Y
     - 待反序列化的包含PDF信息的byte数组
     -
   * - clazz
     - Class<T>
     - Y
     - 目标类型
     -
   * - weIdAuthentication
     - WeIdAuthentication
     - Y
     - WeID公私钥信息
     -

**接口返回**\ :  <T extends JsonSerializer> com.webank.weid.protocol.response.ResponseData\<T>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     -
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - <T extends JsonSerializer>
     - 反序列化后的对象
     - 业务数据

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - ENCRYPT_KEY_NOT_EXISTS
     - 100700
     -  无法获取秘钥
   * - TRANSPORTATION_BASE_ERROR
     - 100800
     - transportation基本未知异常
   * - TRANSPORTATION_PROTOCOL_VERSION_ERROR
     - 100802
     - 协议版本错误
   * - TRANSPORTATION_PROTOCOL_ENCODE_ERROR
     - 100803
     - 协议配置Encode异常
   * - TRANSPORTATION_PROTOCOL_DATA_INVALID
     - 100805
     - 协议数据无效
   * - TRANSPORTATION_ENCODE_BASE_ERROR
     - 100807
     - Encode基本未知异常
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - dataKey无效
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - BASE_ERROR
     - 160007
     - weId基础未知异常
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常
   * - DIRECT_ROUTE_REQUEST_TIMEOUT
     - 160009
     - AMOP超时
   * - DIRECT_ROUTE_MSG_BASE_ERROR
     - 160010
     - AMOP异常
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常


**调用示例**

.. code-block:: java

   String weId = "did:weid:1000:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);

   PresentationE presentation;
   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();

   //序列化
   ResponseData<byte[]> result =
       TransportationFactory
           .newPdfTransportation()
           .specify(verifierWeIdList)
           .serialize(presentation,new ProtocolProperty(EncodeType.ORIGINAL),weIdAuthentication);

   //序列化
   ResponseData<byte[]> result1 =
       TransportationFactory
           .newPdfTransportation()
           .specify(verifierWeIdList)
           .serialize(presentation,new ProtocolProperty(EncodeType.CIPHER),weIdAuthentication);

   //原文方式调用反序列化
   ResponseData<PresentationE> resDeserialize =
       TransportationFactory
           .newPdfTransportation()
           .deserialize(response.getResult(),PresentationE.class,weIdAuthentication);

   //密文方式调用反序列化
   ResponseData<PresentationE> resDeserialize1 =
      TransportationFactory
           .newJsonTransportation()
           .deserialize(response1.getResult(),PresentationE.class,weIdAuthentication);
