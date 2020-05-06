.. role:: raw-html-m2r(raw)
   :format: html


1. registerCpt
-----------------------------

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CptService.registerCpt
   接口定义:ResponseData<CptBaseInfo> registerCpt(CptMapArgs args)
   接口描述: 传入WeIdentity DID，JsonSchema(Map类型) 和其对应的私钥，链上注册CPT，返回CPT编号和版本。

**接口入参**\ :    com.webank.weid.protocol.request.CptMapArgs

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
     - 认证信息，包含WeIdentity DID和私钥
     - 用于WeIdentity DID的身份认证
   * - cptJsonSchema
     - Map<String, Object>
     - Y
     - Map类型的JsonSchema信息
     - 基本使用见调用示例


com.webank.weid.protocol.base.WeIdAuthentication

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - weId
     - String
     - Y
     - CPT发布者的WeIdentity DID
     - WeIdentity DID的格式传入
   * - weIdPublicKeyId
     - String
     - N
     - 公钥Id
     -
   * - weIdPrivateKey
     - WeIdPrivateKey
     - Y
     -
     - 交易私钥，见下


com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - privateKey
     - String
     - Y
     - 私钥值
     - 使用十进制数字表示


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<CptBaseInfo>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     - 此接口返回的code
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - CptBaseInfo
     -
     - CPT基础数据，见下
   * - transactionInfo
     - TransactionInfo
     - 交易信息
     -


com.webank.weid.protocol.response.TransactionInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - blockNumber
     - BigInteger
     - 交易块高
     -
   * - transactionHash
     - String
     - 交易hash
     -
   * - transactionIndex
     - BigInteger
     - 交易索引
     -


com.webank.weid.protocol.base.CptBaseInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - cptId
     - Integer
     - cpId编号
     -
   * - cptVersion
     - Integer
     - 版本号
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
   * - WEID_INVALID
     - 100101
     - WeIdentity DID无效
   * - WEID_PRIVATEKEY_INVALID
     - 100103
     - 私钥无效
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥与WeIdentity DID不匹配
   * - WEID_AUTHORITY_INVALID
     - 100109
     - 授权信息无效
   * - CPT_JSON_SCHEMA_INVALID
     - 100301
     - schema无效
   * - CPT_EVENT_LOG_NULL
     - 100304
     - 交易日志异常
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空
   * - CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX
     - 500302
     - 为权威机构生成的cptId超过上限
   * - CPT_PUBLISHER_NOT_EXIST
     - 500303
     - CPT发布者的WeIdentity DID不存在


**调用示例**

.. code-block:: java

   CptService cptService = new CptServiceImpl();

   HashMap<String, Object> cptJsonSchema = new HashMap<String, Object>(3);
   cptJsonSchema.put(JsonSchemaConstant.TITLE_KEY, "cpt template");
   cptJsonSchema.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is a cpt template");

   HashMap<String, Object> propertitesMap1 = new HashMap<String, Object>(2);
   propertitesMap1.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_STRING);
   propertitesMap1.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is name");

   String[] genderEnum = { "F", "M" };
   HashMap<String, Object> propertitesMap2 = new HashMap<String, Object>(2);
   propertitesMap2.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_STRING);
   propertitesMap2.put(JsonSchemaConstant.DATA_TYPE_ENUM, genderEnum);

   HashMap<String, Object> propertitesMap3 = new HashMap<String, Object>(2);
   propertitesMap3.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_NUMBER);
   propertitesMap3.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is age");

   HashMap<String, Object> cptJsonSchemaKeys = new HashMap<String, Object>(3);
   cptJsonSchemaKeys.put("name", propertitesMap1);
   cptJsonSchemaKeys.put("gender", propertitesMap2);
   cptJsonSchemaKeys.put("age", propertitesMap3);
   cptJsonSchema.put(JsonSchemaConstant.PROPERTIES_KEY, cptJsonSchemaKeys);

   String[] genderRequired = { "name", "gender" };
   cptJsonSchema.put(JsonSchemaConstant.REQUIRED_KEY, genderRequired);

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   CptMapArgs cptMapArgs = new CptMapArgs();
   cptMapArgs.setCptJsonSchema(cptJsonSchema);
   cptMapArgs.setWeIdAuthentication(weIdAuthentication);

   ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);


.. code-block:: text

   返回数据如下：
   result:(com.webank.weid.protocol.base.CptBaseInfo)
      cptId: 1016
      cptVersion: 1
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 29950
      transactionHash: 0xe3f48648beee61d17de609d32af36ac0bf4d68a9352890b04d53841c4949bd13
      transactionIndex: 0


**时序图**

（同时也包含重载updateCpt时序）

.. mermaid::

   sequenceDiagram
   调用者->>WeIdentity SDK : 传入自己已有的WeIdentity DID及对应的私钥，及其jsonSchema，调用registerCpt来注册CPT。
   opt 参数校验
   Note over WeIdentity SDK:如果WeIdentity DID或者私钥为空或不匹配
   WeIdentity SDK->>WeIdentity SDK:报错，提示参数不合法并退出
   end
   WeIdentity SDK->>区块链节点: 将java对象转换为合约所需的字段，调用智能合约，将CPT信息上链
   opt 身份校验
   Note over 区块链节点:如果传入WeIdentity DID在链上不存在
   区块链节点->>区块链节点:报错，提示WeIdentity DID不存在并退出
   end
   区块链节点->>区块链节点:写入CPT信息
   区块链节点-->>WeIdentity SDK:返回
   WeIdentity SDK-->>调用者:返回调用结果


2. registerCpt
-----------------------------

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.rpc.CptService.registerCpt
   接口定义: ResponseData<CptBaseInfo> registerCpt(CptMapArgs args, Integer cptId)
   接口描述: 传入WeIdentity DID，JsonSchema(Map类型), cptId 和其对应的私钥，链上注册指定cptId的CPT，返回CPT编号和版本。

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - args
     - CptMapArgs
     - Y
     - Map类型参数注册CPT
     -
   * - cptId
     - Integer
     - Y
     - 指定的cptId
     -


com.webank.weid.protocol.request.CptMapArgs

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
     - 认证信息，包含WeIdentity DID和私钥
     - 用于WeIdentity DID的身份认证
   * - cptJsonSchema
     - Map<String, Object>
     - Y
     - Map类型的JsonSchema信息
     - 基本使用见调用示例


com.webank.weid.protocol.base.WeIdAuthentication

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - weId
     - String
     - Y
     - CPT发布者的WeIdentity DID
     - WeIdentity DID的格式传入
   * - weIdPublicKeyId
     - String
     - N
     - 公钥Id
     -
   * - weIdPrivateKey
     - WeIdPrivateKey
     - Y
     -
     - 交易私钥，见下


com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - privateKey
     - String
     - Y
     - 私钥值
     - 使用十进制数字表示


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<CptBaseInfo>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     - 此接口返回的code
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - CptBaseInfo
     -
     - CPT基础数据，见下
   * - transactionInfo
     - TransactionInfo
     - 交易信息
     -


com.webank.weid.protocol.response.TransactionInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - blockNumber
     - BigInteger
     - 交易块高
     -
   * - transactionHash
     - String
     - 交易hash
     -
   * - transactionIndex
     - BigInteger
     - 交易索引
     -


com.webank.weid.protocol.base.CptBaseInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - cptId
     - Integer
     - cpId编号
     -
   * - cptVersion
     - Integer
     - 版本号
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
   * - WEID_INVALID
     - 100101
     - WeIdentity DID无效
   * - WEID_PRIVATEKEY_INVALID
     - 100103
     - 私钥无效
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥与WeIdentity DID不匹配
   * - WEID_AUTHORITY_INVALID
     - 100109
     - 授权信息无效
   * - CPT_JSON_SCHEMA_INVALID
     - 100301
     - schema无效
   * - CPT_EVENT_LOG_NULL
     - 100304
     - 交易日志异常
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空
   * - CPT_NOT_EXISTS
     - 500301
     - CPT不存在
   * - CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX
     - 500302
     - 为权威机构生成的cptId超过上限
   * - CPT_PUBLISHER_NOT_EXIST
     - 500303
     - CPT发布者的WeIdentity DID不存在
   * - CPT_ALREADY_EXIST
     - 500304
     - CPT已经存在
   * - CPT_NO_PERMISSION
     - 500305
     - CPT无权限


**调用示例**

.. code-block:: java

   CptService cptService = new CptServiceImpl();

   HashMap<String, Object> cptJsonSchema = new HashMap<String, Object>(3);
   cptJsonSchema.put(JsonSchemaConstant.TITLE_KEY, "cpt template");
   cptJsonSchema.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is a cpt template");

   HashMap<String, Object> propertitesMap1 = new HashMap<String, Object>(2);
   propertitesMap1.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_STRING);
   propertitesMap1.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is name");

   String[] genderEnum = { "F", "M" };
   HashMap<String, Object> propertitesMap2 = new HashMap<String, Object>(2);
   propertitesMap2.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_STRING);
   propertitesMap2.put(JsonSchemaConstant.DATA_TYPE_ENUM, genderEnum);

   HashMap<String, Object> propertitesMap3 = new HashMap<String, Object>(2);
   propertitesMap3.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_NUMBER);
   propertitesMap3.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is age");

   HashMap<String, Object> propertitesMap4 = new HashMap<String, Object>(2);
   propertitesMap4.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_STRING);
   propertitesMap4.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is id");

   HashMap<String, Object> cptJsonSchemaKeys = new HashMap<String, Object>(3);
   cptJsonSchemaKeys.put("name", propertitesMap1);
   cptJsonSchemaKeys.put("gender", propertitesMap2);
   cptJsonSchemaKeys.put("age", propertitesMap3);
   cptJsonSchemaKeys.put("id", propertitesMap4);
   cptJsonSchema.put(JsonSchemaConstant.PROPERTIES_KEY, cptJsonSchemaKeys);

   String[] genderRequired = { "id", "name", "gender" };
   cptJsonSchema.put(JsonSchemaConstant.REQUIRED_KEY, genderRequired);

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   CptMapArgs cptMapArgs = new CptMapArgs();
   cptMapArgs.setCptJsonSchema(cptJsonSchema);
   cptMapArgs.setWeIdAuthentication(weIdAuthentication);

   ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs, 101);


.. code-block:: text

   返回数据如下：
   result:(com.webank.weid.protocol.base.CptBaseInfo)
      cptId: 101
      cptVersion: 1
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 29950
      transactionHash: 0xe3f48648beee61d17de609d32af36ac0bf4d68a9352890b04d53841c4949bd13
      transactionIndex: 0


3. registerCpt
-----------------------------

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CptService.registerCpt
   接口定义:ResponseData<CptBaseInfo> registerCpt(CptStringArgs args)
   接口描述: 传入WeIdentity DID，JsonSchema(String类型) 和其对应的私钥，链上注册CPT，返回CPT编号和版本。

**接口入参**\ :    com.webank.weid.protocol.request.CptStringArgs

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
     - 认证信息，包含WeIdentity DID和私钥
     - 用于WeIdentity DID的身份认证
   * - cptJsonSchema
     - String
     - Y
     - 字符串类型的JsonSchema信息
     - 基本使用见调用示例


com.webank.weid.protocol.base.WeIdAuthentication

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - weId
     - String
     - Y
     - CPT发布者的WeIdentity DID
     - WeIdentity DID的格式传入
   * - weIdPublicKeyId
     - String
     - N
     - 公钥Id
     -
   * - weIdPrivateKey
     - WeIdPrivateKey
     - Y
     -
     - 交易私钥，见下


com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - privateKey
     - String
     - Y
     - 私钥值
     - 使用十进制数字表示


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<CptBaseInfo>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     - 此接口返回的code
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - CptBaseInfo
     -
     - CPT基础数据，见下
   * - transactionInfo
     - TransactionInfo
     - 交易信息
     -


com.webank.weid.protocol.response.TransactionInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - blockNumber
     - BigInteger
     - 交易块高
     -
   * - transactionHash
     - String
     - 交易hash
     -
   * - transactionIndex
     - BigInteger
     - 交易索引
     -


com.webank.weid.protocol.base.CptBaseInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - cptId
     - Integer
     - cpId编号
     -
   * - cptVersion
     - Integer
     - 版本号
     -


.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - WEID_INVALID
     - 100101
     - WeIdentity DID无效
   * - WEID_PRIVATEKEY_INVALID
     - 100103
     - 私钥无效
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥与WeIdentity DID不匹配
   * - WEID_AUTHORITY_INVALID
     - 100109
     - 授权信息无效
   * - CPT_JSON_SCHEMA_INVALID
     - 100301
     - schema无效
   * - CPT_EVENT_LOG_NULL
     - 100304
     - 交易日志异常
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空
   * - CPT_NOT_EXISTS
     - 500301
     - CPT不存在
   * - CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX
     - 500302
     - 为权威机构生成的cptId超过上限
   * - CPT_PUBLISHER_NOT_EXIST
     - 500303
     - CPT发布者的WeIdentity DID不存在
   * - CPT_ALREADY_EXIST
     - 500304
     - CPT已经存在
   * - CPT_NO_PERMISSION
     - 500305
     - CPT无权限


**调用示例**

.. code-block:: java

   CptService cptService = new CptServiceImpl();

   String jsonSchema = "{\"properties\" : {\"name\": {\"type\": \"string\",\"description\": \"the name of certificate owner\"},\"gender\": {\"enum\": [\"F\", \"M\"],\"type\": \"string\",\"description\": \"the gender of certificate owner\"}, \"age\": {\"type\": \"number\", \"description\": \"the age of certificate owner\"}},\"required\": [\"name\", \"age\"]}";

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   CptStringArgs cptStringArgs = new CptStringArgs();
   cptStringArgs.setCptJsonSchema(jsonSchema);
   cptStringArgs.setWeIdAuthentication(weIdAuthentication);

   ResponseData<CptBaseInfo> response = cptService.registerCpt(cptStringArgs);


.. code-block:: text

   返回数据如下：
   result:(com.webank.weid.protocol.base.CptBaseInfo)
      cptId: 1017
      cptVersion: 1
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 29964
      transactionHash: 0xf3b039557b2d1e575e9949b3a33d34ee5c8749b55940347d18a0f7e929eda799
      transactionIndex: 0



4. registerCpt
-----------------------------

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.rpc.CptService.registerCpt
   接口定义: ResponseData<CptBaseInfo> registerCpt(CptStringArgs args, Integer cptId)
   接口描述: 传入WeIdentity DID，JsonSchema(String类型) , cptId和其对应的私钥，链上注册指定cptId的CPT，返回CPT编号和版本。

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - args
     - CptStringArgs
     - Y
     - String类型参数注册CPT
     -
   * - cptId
     - Integer
     - Y
     - 指定的cptId
     -


com.webank.weid.protocol.request.CptStringArgs

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
     - 认证信息，包含WeIdentity DID和私钥
     - 用于WeIdentity DID的身份认证
   * - cptJsonSchema
     - String
     - Y
     - 字符串类型的JsonSchema信息
     - 基本使用见调用示例


com.webank.weid.protocol.base.WeIdAuthentication

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - weId
     - String
     - Y
     - CPT发布者的WeIdentity DID
     - WeIdentity DID的格式传入
   * - weIdPublicKeyId
     - String
     - N
     - 公钥Id
     -
   * - weIdPrivateKey
     - WeIdPrivateKey
     - Y
     -
     - 交易私钥，见下


com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - privateKey
     - String
     - Y
     - 私钥值
     - 使用十进制数字表示


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<CptBaseInfo>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     - 此接口返回的code
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - CptBaseInfo
     -
     - CPT基础数据，见下
   * - transactionInfo
     - TransactionInfo
     - 交易信息
     -


com.webank.weid.protocol.response.TransactionInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - blockNumber
     - BigInteger
     - 交易块高
     -
   * - transactionHash
     - String
     - 交易hash
     -
   * - transactionIndex
     - BigInteger
     - 交易索引
     -


com.webank.weid.protocol.base.CptBaseInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - cptId
     - Integer
     - cpId编号
     -
   * - cptVersion
     - Integer
     - 版本号
     -


.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - WEID_INVALID
     - 100101
     - WeIdentity DID无效
   * - WEID_PRIVATEKEY_INVALID
     - 100103
     - 私钥无效
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥与WeIdentity DID不匹配
   * - WEID_AUTHORITY_INVALID
     - 100109
     - 授权信息无效
   * - CPT_JSON_SCHEMA_INVALID
     - 100301
     - schema无效
   * - CPT_EVENT_LOG_NULL
     - 100304
     - 交易日志异常
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空
   * - CPT_NOT_EXISTS
     - 500301
     - CPT不存在
   * - CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX
     - 500302
     - 为权威机构生成的cptId超过上限
   * - CPT_PUBLISHER_NOT_EXIST
     - 500303
     - CPT发布者的WeIdentity DID不存在
   * - CPT_ALREADY_EXIST
     - 500304
     - CPT已经存在
   * - CPT_NO_PERMISSION
     - 500305
     - CPT无权限


**调用示例**

.. code-block:: java

   CptService cptService = new CptServiceImpl();

   String jsonSchema = "{\"properties\" : {\"id\": {\"type\": \"string\",\"description\": \"the id of certificate owner\"}, \"name\": {\"type\": \"string\",\"description\": \"the name of certificate owner\"},\"gender\": {\"enum\": [\"F\", \"M\"],\"type\": \"string\",\"description\": \"the gender of certificate owner\"}, \"age\": {\"type\": \"number\", \"description\": \"the age of certificate owner\"}},\"required\": [\"id\", \"name\", \"age\"]}";

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   CptStringArgs cptStringArgs = new CptStringArgs();
   cptStringArgs.setCptJsonSchema(jsonSchema);
   cptStringArgs.setWeIdAuthentication(weIdAuthentication);

   ResponseData<CptBaseInfo> response = cptService.registerCpt(cptStringArgs, 103);


.. code-block:: text

   返回数据如下：
   result:(com.webank.weid.protocol.base.CptBaseInfo)
      cptId: 103
      cptVersion: 1
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 29910
      transactionHash: 0xf3b039557b2d1e575e9949b3a33d34e35c8749b55940347d18a0f7e929eda799
      transactionIndex: 0


5. queryCpt
-----------------------------

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CptService.queryCpt
   接口定义:ResponseData<Cpt> queryCpt(Integer cptId)
   接口描述: 根据CPT编号查询CPT信息。

**接口入参**\ :    java.lang.Integer

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - cptId
     - Integer
     - Y
     - cptId编号
     -


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<Cpt>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     - 此接口返回的code
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - Cpt
     -
     - CPT内容，见下
   * - transactionInfo
     - TransactionInfo
     - 交易信息
     -


com.webank.weid.protocol.response.TransactionInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - blockNumber
     - BigInteger
     - 交易块高
     -
   * - transactionHash
     - String
     - 交易hash
     -
   * - transactionIndex
     - BigInteger
     - 交易索引
     -


com.webank.weid.protocol.base.Cpt

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - cptJsonSchema
     - Map<String, Object>
     - Map类型的cptJsonSchema信息
     -
   * - cptBaseInfo
     - CptBaseInfo
     -
     - CPT基础数据，见下
   * - cptMetaData
     - CptMetaData
     -
     - CPT元数据内部类，见下


com.webank.weid.protocol.base.CptBaseInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - cptId
     - Integer
     - cpId编号
     -
   * - cptVersion
     - Integer
     - 版本号
     -


com.webank.weid.protocol.base.Cpt.MetaData

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - cptPublisher
     - String
     - CPT发布者的WeIdentity DID
     - WeIdentity DID格式数据
   * - cptSignature
     - String
     - 签名数据
     - cptPublisher与cptJsonSchema拼接的签名数据
   * - updated
     - long
     - 更新时间
     -
   * - created
     - long
     - 创建日期
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
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数非法
   * - CPT_NOT_EXISTS
     - 500301
     - CPT不存在


**调用示例**

.. code-block:: java

   CptService cptService = new CptServiceImpl();
   Integer cptId = Integer.valueOf(1017);
   ResponseData<Cpt> response = cptService.queryCpt(cptId);


.. code-block:: text

   返回数据如下：
   result:(com.webank.weid.protocol.base.Cpt)
      cptBaseInfo:(com.webank.weid.protocol.base.CptBaseInfo)
         cptId: 1017
         cptVersion: 1
      cptJsonSchema:(java.util.HashMap)
         $schema: http://json-schema.org/draft-04/schema#
         type: object
         properties:(java.util.LinkedHashMap)
            age:(java.util.LinkedHashMap)
               description: the age of certificate owner
               type: number
            gender:(java.util.LinkedHashMap)
               description: the gender of certificate owner
               enum:(java.util.ArrayList)
                  [0]:F
                  [1]:M
               type: string
            name:(java.util.LinkedHashMap)
               description: the name of certificate owner
               type: string
         required:(java.util.ArrayList)
            [0]:name
            [1]:age
      metaData:(com.webank.weid.protocol.base.Cpt$MetaData)
         cptPublisher: did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7
         cptSignature: G/YGY8Ftj9jPRdtr4ym+19M4/K6x9RbmRiV9JkryXeQGFr8eukDCBAcbinnNpF2N3Eo72bvxNqJOKx4ohWIus0Y=
         created: 1560415607673
         updated: 0
   errorCode: 0
   errorMessage: success
   transactionInfo:null


**时序图**

.. mermaid::

   sequenceDiagram
   调用者->>WeIdentity SDK : 传入指定的cptId
   opt 参数校验
   Note over WeIdentity SDK:检查传入的cptId是否为空或负数
   WeIdentity SDK->>WeIdentity SDK:报错，提示weid不合法并退出
   end
   WeIdentity SDK->>区块链节点: 调用合约查询链上的指定cpt对应的信息
   区块链节点-->>WeIdentity SDK:返回
   WeIdentity SDK->>WeIdentity SDK:根据合约返回的值构建返回的java对象
   WeIdentity SDK-->>调用者:返回调用结果

6. updateCpt
-----------------------------

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CptService.updateCpt
   接口定义:ResponseData<CptBaseInfo> updateCpt(CptMapArgs args, Integer cptId)
   接口描述: 传入cptId，JsonSchema(Map类型)，WeIdentity DID，WeIdentity DID所属私钥，进行更新CPT信息，更新成功版本自动+1。

**接口入参**\ :    com.webank.weid.protocol.request.CptMapArgs，Integer

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - args
     - CptMapArgs
     - Y
     - CPT信息
     - 具体见下
   * - cptId
     - Integer
     - Y
     - 发布的CPT编号
     -


com.webank.weid.protocol.request.CptMapArgs

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
     - 认证信息，包含WeIdentity DID和私钥
     - 用于WeIdentity DID的身份认证
   * - cptJsonSchema
     - Map<String, Object>
     - Y
     - Map类型的JsonSchema信息
     - 基本使用见调用示例


com.webank.weid.protocol.base.WeIdAuthentication

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - weId
     - String
     - Y
     - CPT发布者的WeIdentity DID
     - WeIdentity DID的格式传入
   * - weIdPublicKeyId
     - String
     - N
     - 公钥Id
     -
   * - weIdPrivateKey
     - WeIdPrivateKey
     - Y
     -
     - 交易私钥，见下


com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - privateKey
     - String
     - Y
     - 私钥值
     - 使用十进制数字表示


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<CptBaseInfo>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     - 此接口返回的code
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - CptBaseInfo
     -
     - CPT基础数据，见下
   * - transactionInfo
     - TransactionInfo
     - 交易信息
     -


com.webank.weid.protocol.response.TransactionInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - blockNumber
     - BigInteger
     - 交易块高
     -
   * - transactionHash
     - String
     - 交易hash
     -
   * - transactionIndex
     - BigInteger
     - 交易索引
     -


com.webank.weid.protocol.base.CptBaseInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - cptId
     - Integer
     - cpId编号
     -
   * - cptVersion
     - Integer
     - 版本号
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
   * - WEID_INVALID
     - 100101
     - WeIdentity DID无效
   * - WEID_PRIVATEKEY_INVALID
     - 100103
     - 私钥无效
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥与WeIdentity DID不匹配
   * - WEID_AUTHORITY_INVALID
     - 100109
     - 授权信息无效
   * - CPT_JSON_SCHEMA_INVALID
     - 100301
     - schema无效
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CPT_EVENT_LOG_NULL
     - 100304
     - 交易日志异常
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空
   * - CPT_NOT_EXISTS
     - 500301
     - CPT不存在
   * - CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX
     - 500302
     - 为权威机构生成的cptId超过上限
   * - CPT_PUBLISHER_NOT_EXIST
     - 500303
     - CPT发布者的WeIdentity DID不存在
   * - CPT_ALREADY_EXIST
     - 500304
     - CPT已经存在
   * - CPT_NO_PERMISSION
     - 500305
     - CPT无权限


**调用示例**

.. code-block:: java

   CptService cptService = new CptServiceImpl();

   HashMap<String, Object> cptJsonSchema = new HashMap<String, Object>(3);
   cptJsonSchema.put(JsonSchemaConstant.TITLE_KEY, "cpt template");
   cptJsonSchema.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is a cpt template");

   HashMap<String, Object> propertitesMap1 = new HashMap<String, Object>(2);
   propertitesMap1.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_STRING);
   propertitesMap1.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is name");

   String[] genderEnum = { "F", "M" };
   HashMap<String, Object> propertitesMap2 = new HashMap<String, Object>(2);
   propertitesMap2.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_STRING);
   propertitesMap2.put(JsonSchemaConstant.DATA_TYPE_ENUM, genderEnum);

   HashMap<String, Object> propertitesMap3 = new HashMap<String, Object>(2);
   propertitesMap3.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_NUMBER);
   propertitesMap3.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is age");

   HashMap<String, Object> cptJsonSchemaKeys = new HashMap<String, Object>(3);
   cptJsonSchemaKeys.put("name", propertitesMap1);
   cptJsonSchemaKeys.put("gender", propertitesMap2);
   cptJsonSchemaKeys.put("age", propertitesMap3);
   cptJsonSchema.put(JsonSchemaConstant.PROPERTIES_KEY, cptJsonSchemaKeys);

   String[] genderRequired = { "name", "gender" };
   cptJsonSchema.put(JsonSchemaConstant.REQUIRED_KEY, genderRequired);

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   CptMapArgs cptMapArgs = new CptMapArgs();
   cptMapArgs.setCptJsonSchema(cptJsonSchema);
   cptMapArgs.setWeIdAuthentication(weIdAuthentication);

   Integer cptId = Integer.valueOf(1017);

   ResponseData<CptBaseInfo> response = cptService.updateCpt(cptMapArgs, cptId);


.. code-block:: text

   返回数据如下：
   result:(com.webank.weid.protocol.base.CptBaseInfo)
      cptId: 1017
      cptVersion: 2
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 29989
      transactionHash: 0x4435fa88f9f138f14671d8baa5e5f16c69c5efa3591c4912772b9b1233af398a
      transactionIndex: 0


**时序图**

（同时也包含重载updateCpt时序）

.. mermaid::

   sequenceDiagram
   调用者->>WeIdentity SDK : 传入自己已有的WeIdentity DID及对应的私钥，及其需新版本的jsonSchema，调用updateCpt来更新CPT。
   opt 参数校验
   Note over WeIdentity SDK:如果WeIdentity DID或者私钥为空或不匹配
   WeIdentity SDK->>WeIdentity SDK:报错，提示参数不合法并退出
   end
   WeIdentity SDK->>区块链节点: 将java对象转换为合约所需的字段，调用智能合约，将更新的CPT信息上链
   opt 身份校验
   Note over 区块链节点:如果传入WeIdentity DID在链上不存在
   区块链节点->>区块链节点:报错，提示WeIdentity DID不存在并退出
   end
   区块链节点->>区块链节点:写入CPT更新信息
   区块链节点-->>WeIdentity SDK:返回
   WeIdentity SDK-->>调用者:返回调用结果


7. updateCpt
-----------------------------

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CptService.updateCpt
   接口定义:ResponseData<CptBaseInfo> updateCpt(CptStringArgs args, Integer cptId)
   接口描述: 传入cptId，JsonSchema(String类型)，WeIdentity DID，WeIdentity DID所属私钥，进行更新CPT信息，更新成功版本自动+1。

**接口入参**\ :    com.webank.weid.protocol.request.CptStringArgs，Integer

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - args
     - CptStringArgs
     - Y
     - CPT信息
     - 具体见下
   * - cptId
     - Integer
     - Y
     - 发布的CPT编号
     -

com.webank.weid.protocol.request.CptStringArgs

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
     - 认证信息，包含WeIdentity DID和私钥
     - 用于WeIdentity DID的身份认证
   * - cptJsonSchema
     - String
     - Y
     - 字符串类型的JsonSchema信息
     - 基本使用见调用示例


com.webank.weid.protocol.base.WeIdAuthentication

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - weId
     - String
     - Y
     - CPT发布者的WeIdentity DID
     - WeIdentity DID的格式传入
   * - weIdPublicKeyId
     - String
     - N
     - 公钥Id
     -
   * - weIdPrivateKey
     - WeIdPrivateKey
     - Y
     -
     - 交易私钥，见下


com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - privateKey
     - String
     - Y
     - 私钥值
     - 使用十进制数字表示


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<CptBaseInfo>;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - errorCode
     - Integer
     - 返回结果码
     - 此接口返回的code
   * - errorMessage
     - String
     - 返回结果描述
     -
   * - result
     - CptBaseInfo
     -
     - CPT基础数据，见下
   * - transactionInfo
     - TransactionInfo
     - 交易信息
     -


com.webank.weid.protocol.response.TransactionInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - blockNumber
     - BigInteger
     - 交易块高
     -
   * - transactionHash
     - String
     - 交易hash
     -
   * - transactionIndex
     - BigInteger
     - 交易索引
     -

com.webank.weid.protocol.base.CptBaseInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - cptId
     - Integer
     - cpId编号
     -
   * - cptVersion
     - Integer
     - 版本号
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
   * - WEID_INVALID
     - 100101
     - WeIdentity DID无效
   * - WEID_PRIVATEKEY_INVALID
     - 100103
     - 私钥无效
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥与WeIdentity DID不匹配
   * - WEID_AUTHORITY_INVALID
     - 100109
     - 授权信息无效
   * - CPT_JSON_SCHEMA_INVALID
     - 100301
     - schema无效
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CPT_EVENT_LOG_NULL
     - 100304
     - 交易日志异常
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空
   * - CPT_NOT_EXISTS
     - 500301
     - CPT不存在
   * - CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX
     - 500302
     - 为权威机构生成的cptId超过上限
   * - CPT_PUBLISHER_NOT_EXIST
     - 500303
     - CPT发布者的WeIdentity DID不存在
   * - CPT_ALREADY_EXIST
     - 500304
     - CPT已经存在
   * - CPT_NO_PERMISSION
     - 500305
     - CPT无权限


**调用示例**

.. code-block:: java

   CptService cptService = new CptServiceImpl();

   String jsonSchema = "{\"properties\" : {\"name\": {\"type\": \"string\",\"description\": \"the name of certificate owner\"},\"gender\": {\"enum\": [\"F\", \"M\"],\"type\": \"string\",\"description\": \"the gender of certificate owner\"}, \"age\": {\"type\": \"number\", \"description\": \"the age of certificate owner\"}},\"required\": [\"name\", \"age\"]}";

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   CptStringArgs cptStringArgs = new CptStringArgs();
   cptStringArgs.setCptJsonSchema(jsonSchema);
   cptStringArgs.setWeIdAuthentication(weIdAuthentication);

   Integer cptId = Integer.valueOf(1017);

   ResponseData<CptBaseInfo> response = cptService.updateCpt(cptStringArgs, cptId);


.. code-block:: text

   返回数据如下：
   result:(com.webank.weid.protocol.base.CptBaseInfo)
      cptId: 1017
      cptVersion: 3
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 29991
      transactionHash: 0x8ed8113dd1772ae74e6f12de3d3716d76a410190c3d564d5d5842b85c7005aee
      transactionIndex: 0
