.. role:: raw-html-m2r(raw)
   :format: html

.. _weidentity-java-sdk-doc:

WeIdentity JAVA SDK文档
=======================

总体介绍
--------

WeIdentity Java SDK提供了一整套对WeIdentity进行管理操作的Java库。目前，SDK支持本地密钥管理、数字身份标识（WeIdentity DID）管理、电子凭证（WeIdentity Credential）管理、授权机构（Authority Issuer）管理、CPT管理等功能，同时也提供基于FISCO-BCOS的区块链交互、智能合约的部署与调用。未来还将支持更丰富的功能和应用。

术语
----

* 请参阅：`术语表 <https://weidentity.readthedocs.io/zh_CN/latest/docs/terminologies.html>`_

部署SDK
-------

* 请参阅：`WeIdentity JAVA SDK 安装部署文档 <./weidentity-installation.html>`_

* 开始使用之前，再次确认启动FISCO-BCOS节点已启动，确保端口可以访问。

整体过程快速上手
----------------

按照以下流程可以完整地体验本SDK的核心功能：


#. 注册DID：通过WeIdService的createWeId()生成一个WeIdentity DID并注册到链上；
#. 设置DID属性：分别调用WeIdService的set方法组，为此DID设置公钥、认证方式、服务端点等属性；
#. 查询DID属性：调用WeIdService的getWeIdDocumentJson()查阅生成的WeIdentity DID数据；
#. 注册授权机构：通过AuthorityIssuerService的registerAuthorityIssuer()把生成的WeIdentity DID注册成一个授权机构；
#. 查询授权机构：调用AuthorityIssuerService的queryAuthorityIssuerInfo()查阅生成的授权机构数据；
#. 注册CPT：通过CptService的registerCpt()，通过之前生成的WeIdentity DID身份创建一个你喜欢的CPT模板；
#. 查询CPT：调用CptService的queryCpt()查阅生成的CPT模板；
#. 生成凭证：通过CredentialService的CreateCredential()，根据CPT模板，生成一份Credential；
#. 查询凭证：调用CredentialService的VerifyCredential()，验证此Credential是否合法；
#. 凭证存证上链：调用EvidenceService的CreateEvidence()，将之前生成的Credential生成一份Hash存证上链；
#. 验证链上凭证存证：调用EvidenceService的VerifyEvidence()，和链上对比，验证Credential是否被篡改。

代码结构说明
------------

.. code-block:: text

   ├─ config：FISCO-BCOS的合约配置
   ├─ constant：系统常量相关
   └─ contract：通过FISCO-BCOS Web3sdk生成的合约Java接口文件
      └─ deploy: 合约部署相关
   └─ protocol：接口参数相关定义
      ├─ base: 基础数据类型定义
      ├─ request: 接口入参定义
      └─ response: 接口出参定义
   ├─ rpc：接口定义
   ├─ service：接口相关实现
   └─ util：工具类实现

接口简介
--------

整体上，WeIdentity Java SDK包括五个主要的接口，它们分别是：AuthorityIssuerService、CptService、CredentialService、WeIdService、EvidenceService。


* AuthorityIssuerService

在WeIdentity的整体架构中，存在着可信的“授权机构”这一角色。一般来说，授权机构特指那些广为人知的、具有一定公信力的、并且有相对频繁签发Credential需求的实体。

本接口提供了对这类授权签发Credential的机构的注册、移除、查询信息等操作。


* CptService

任何凭证的签发，都需要将数据转换成已经注册的CPT (Claim Protocol Type)格式规范，也就是所谓的“标准化格式化数据”。相关机构事先需要注册好CPT，在此之后，签发机构会根据CPT提供符合格式的数据，进而进行凭证的签发。

本接口提供了对CPT的注册、更新、查询等操作。


* CredentialService

凭证签发相关功能的核心接口。

本接口提供凭证的签发和验证操作。


* WeIdService

WeIdentity DID相关功能的核心接口。

本接口提供WeIdentity DID的创建、获取信息、设置属性等相关操作。


* EvidenceService

凭证存证上链的相关接口。

本接口提供凭证的Hash存证的生成上链、链上查询及校验等操作。


接口列表
--------

AuthorityIssuerService
^^^^^^^^^^^^^^^^^^^^^^

1. registerAuthorityIssuer
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.AuthorityIssuerService.registerAuthorityIssuer
   接口定义:ResponseData<Boolean> registerAuthorityIssuer(RegisterAuthorityIssuerArgs args)
   接口详细描述: 注册新的授权发行机构。
   注意：这是一个需要权限的操作，目前只有合约的部署者（一般为SDK）才能正确执行。

**接口入参**\ : com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - authorityIssuer
     - AuthorityIssuer
     - Y
     - JavaBean
     - AuthorityIssuer信息，见下
   * - weIdPrivateKey
     - WeIdPrivateKey
     - Y
     - JavaBean
     - 交易私钥，见下


com.webank.weid.protocol.base.AuthorityIssuer

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
     - 授权机构WeIdentity DID
     - 
   * - name
     - String
     - Y
     - 授权机构名称
     - 
   * - createDate
     - Long
     - Y
     - 创建日期
     - 
   * - accValue
     - String
     - Y
     - 授权方累积判定值
     -


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


**接口返回**\ :     com.webank.weid.protocol.response.ResponseData\<Boolean>;

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
     - 返回结果值
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
     - 无效的WeIdentity DID
   * - WEID_DOES_NOT_EXIST
     - 100104
     - WeIdentity DID不存在
   * - AUTHORITY_ISSUER_ERROR
     - 100200
     - 授权标准异常
   * - AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL
     - 100202
     - 私钥格式非法
   * - AUTHORITY_ISSUER_ADDRESS_MISMATCH
     - 100204
     - 地址不匹配
   * - AUTHORITY_ISSUER_OPCODE_MISMATCH
     - 100205
     - 操作码不匹配
   * - AUTHORITY_ISSUER_NAME_ILLEGAL
     - 100206
     - 名称格式非法
   * - AUTHORITY_ISSUER_ACCVALUE_ILLEAGAL
     - 100207
     - 累计值格式非法
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空
   * - AUTHORITY_ISSUER_CONTRACT_ERROR_ALREADY_EXIST
     - 500201
     - 授权人已经存在
   * - AUTHORITY_ISSUER_CONTRACT_ERROR_NO_PERMISSION
     - 500203
     - 授权人没有权限


**调用示例**

.. code-block:: java

   @Autowired
   private AuthorityIssuerService authorityIssuerService;
   AuthorityIssuer authorityIssuer = new AuthorityIssuer();
   authorityIssuer.setWeId("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
   authorityIssuer.setCreateDate(new Date().getTime());
   authorityIssuer.setName("webank1");
   authorityIssuer.setAccValue("0");
   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey(new BigInteger(1231232142).toString());
   RegisterAuthorityIssuerArgs args = new RegisterAuthorityIssuerArgs();
   args.setAuthorityIssuer(authorityIssuer);
   args.setWeIdPrivateKey(weIdPrivateKey);
   ResponseData<Boolean> response = authorityIssuerService.registerAuthorityIssuer(args);

.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success


**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant AuthorityIssuerService
   participant WeIdService
   participant 区块链节点
   调用者->>AuthorityIssuerService: 调用RegisterAuthorityIssuer()
   AuthorityIssuerService->>AuthorityIssuerService: 入参非空、格式及合法性检查
   opt 入参校验失败
   AuthorityIssuerService-->>调用者: 报错，提示参数不合法并退出
   end
   AuthorityIssuerService->>WeIdService: 查询WeIdentity DID存在性
   WeIdService->>区块链节点: 链上查询WeIdentity DID属性
   区块链节点-->>WeIdService: 返回查询结果
   WeIdService-->>AuthorityIssuerService: 返回查询结果
   opt 在链上不存在
   AuthorityIssuerService-->>调用者: 报错并退出
   end
   AuthorityIssuerService->>区块链节点: 加载私钥，调用注册合约
   opt 身份校验
   Note over 区块链节点: 如果传入WeIdentity DID在链上不存在
   区块链节点->>区块链节点: 报错并退出
   end
   区块链节点->>区块链节点: 权限检查，执行合约写入AuthorityIssuer信息
   区块链节点-->>AuthorityIssuerService: 返回合约执行结果
   AuthorityIssuerService->>AuthorityIssuerService: 解析合约事件
   opt 失败，地址无效或无权限
   AuthorityIssuerService-->>调用者: 报错并退出
   end
   AuthorityIssuerService-->>调用者: 返回成功


----

2. removeAuthorityIssuer
~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.AuthorityIssuerService.removeAuthorityIssuer
   接口定义:ResponseData<Boolean> removeAuthorityIssuer(String weId)
   接口详细描述: 根据WeID注销授权机构。
   注意：这是一个需要权限的操作，目前只有合约的部署者（一般为SDK）才能正确执行。

**接口入参**\ :  com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs

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
     - WeIdentity DID
     - 授权机构WeIdentity DID
   * - weIdPrivateKey
     - WeIdPrivateKey
     - Y
     - JavaBean
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


**接口返回**\ :     com.webank.weid.protocol.response.ResponseData\<Boolean>;

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
     - 返回结果值
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
     - 无效的WeIdentity DID
   * - AUTHORITY_ISSUER_ERROR
     - 100200
     - 授权标准异常
   * - AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL
     - 100202
     - 私钥格式非法
   * - AUTHORITY_ISSUER_ADDRESS_MISMATCH
     - 100204
     - 地址不匹配
   * - AUTHORITY_ISSUER_OPCODE_MISMATCH
     - 100205
     - 操作码不匹配
   * - AUTHORITY_ISSUER_NAME_ILLEGAL
     - 100206
     - 名称格式非法
   * - AUTHORITY_ISSUER_ACCVALUE_ILLEAGAL
     - 100207
     - 累计值格式非法
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空
   * - AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS
     - 500202
     - 授权人信息不存在
   * - AUTHORITY_ISSUER_CONTRACT_ERROR_NO_PERMISSION
     - 500203
     - 授权人没有权限


**调用示例**

.. code-block:: java

   @Autowired
   private AuthorityIssuerService authorityIssuerService;
   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey(new BigInteger(1231232142).toString());
   RemoveAuthorityIssuerArgs args = new RemoveAuthorityIssuerArgs();
   args.setWeId("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
   args.setWeIdPrivateKey(weIdPrivateKey);
   ResponseData<Boolean> response = authorityIssuerService.removeAuthorityIssuer(args);

.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success


**时序图**


.. mermaid::

   sequenceDiagram
   participant 调用者
   participant AuthorityIssuerService
   participant 区块链节点
   调用者->>AuthorityIssuerService: 调用RemoverAuthorityIssuer()
   AuthorityIssuerService->>AuthorityIssuerService: 入参非空、格式及合法性检查
   opt 入参校验失败
   AuthorityIssuerService-->>调用者: 报错，提示参数不合法并退出
   end
   AuthorityIssuerService->>区块链节点: 加载交易私钥，调用移除合约
   区块链节点->>区块链节点: 权限检查，执行合约删除WeIdentity DID信息
   区块链节点-->>AuthorityIssuerService: 返回合约执行结果
   AuthorityIssuerService->>AuthorityIssuerService: 解析合约事件
   opt 失败，地址无效或无权限
   AuthorityIssuerService-->>调用者: 报错并退出
   end
   AuthorityIssuerService-->>调用者: 返回成功



----

3. isAuthorityIssuer
~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.AuthorityIssuerService.isAuthorityIssuer
   接口定义:ResponseData<Boolean> isAuthorityIssuer(String weId)
   接口详细描述: 根据WeIdentity DID查询是否注册成授权者

**接口入参**\ :    String

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
     - WeIdentity DID
     - 用于搜索权限发布者


**接口返回**\ :     com.webank.weid.protocol.response.ResponseData\<Boolean>;

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
     - 返回结果值
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
     - 无效的WeIdentity DID
   * - AUTHORITY_ISSUER_ERROR
     - 100200
     - 授权标准异常
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误


**调用示例**

.. code-block:: java

   @Autowired
   private AuthorityIssuerService authorityIssuerService;
   ResponseData<Boolean> response = authorityIssuerService.isAuthorityIssuer("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");

.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success


**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant AuthorityIssuerService
   participant 区块链节点
   调用者->>AuthorityIssuerService: 调用IsAuthorityIssuer()
   AuthorityIssuerService->>AuthorityIssuerService: 入参非空、格式及合法性检查
   opt 入参校验失败
   AuthorityIssuerService-->>调用者: 报错，提示参数不合法并退出
   end
   AuthorityIssuerService->>区块链节点: 调用查询是否为授权机构合约
   区块链节点->>区块链节点: 执行合约通过WeIdentity DID查询
   区块链节点-->>AuthorityIssuerService: 返回查询结果
   AuthorityIssuerService-->>调用者: 返回是/否


----

4. queryAuthorityIssuerInfo
~~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.AuthorityIssuerService.queryAuthorityIssuerInfo
   接口定义:ResponseData<AuthorityIssuer> queryAuthorityIssuerInfo(String weId)
   接口详细描述: 根据WeIdentity DID查询授权人信息

**接口入参**\ :    String

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
     - WeIdentity DID
     - 用于搜索权限发布者


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<AuthorityIssuer>;

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
     - AuthorityIssuer
     - JavaBean
     - 授权机构信息，见下


com.webank.weid.protocol.base.AuthorityIssuer

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
     - 授权机构WeIdentity DID
     - 
   * - name
     - String
     - Y
     - 授权机构名称
     - 
   * - createDate
     - Long
     - Y
     - 创建日期
     - 
   * - accValue
     - String
     - Y
     - 授权方累积判定值
     - 


**注意**\ ：因为Solidity 0.4.4的限制，无法正确的返回accValue，因此这里取得的accValue一定为空字符串。未来会进行修改。

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
     - 无效的WeIdentity DID
   * - AUTHORITY_ISSUER_ERROR
     - 100200
     - 授权标准异常
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS
     - 500202
     - 授权人信息不存在


**调用示例**

.. code-block:: java

   @Autowired
   private AuthorityIssuerService authorityIssuerService;
   ResponseData<AuthorityIssuer> response = authorityIssuerService.queryAuthorityIssuerInfo("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");

.. code-block:: text

   返回数据如：
   result:(com.webank.weid.protocol.base.AuthorityIssuer)
      weId: did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f
      name: webank1
      createDate: 1539239136000
      accValue:
   errorCode: 0
   errorMessage: success

**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant AuthorityIssuerService
   participant 区块链节点
   调用者->>AuthorityIssuerService: 调用queryAuthorityIssuerInfo()
   AuthorityIssuerService->>AuthorityIssuerService: 入参非空、格式及合法性检查
   opt 入参校验失败
   AuthorityIssuerService-->>调用者: 报错，提示参数不合法并退出
   end
   AuthorityIssuerService->>区块链节点: 调用查询详细信息合约
   区块链节点->>区块链节点: 执行合约通过WeIdentity DID查询
   区块链节点-->>AuthorityIssuerService: 返回查询结果
   AuthorityIssuerService-->>调用者: 返回查询结果（非授权机构则无）

----

CptService
^^^^^^^^^^

1. registerCpt
~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CptService.registerCpt
   接口定义:ResponseData<CptBaseInfo> registerCpt(RegisterCptArgs args)
   接口详细描述: 传入WeIdentity DID，jsonSchema 和其对应的私钥，链上注册cpt，返回cpt编号和版本
   有对cptPublisher和cptJsonSchema 组合签名

**接口入参**\ :    com.webank.weid.protocol.request.RegisterCptArgs

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - cptJsonSchema
     - String
     - Y
     - json验证器
     - 用于验证json 数据的合规性
   * - cptPublisher
     - String
     - Y
     - CPT发布者的WeIdentity DID
     - WeIdentity DID的格式传入
   * - cptPublisherPrivateKey
     - WeIdPrivateKey
     - Y
     - JavaBean
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
     - JavaBean
     - CPT基础数据，见下


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
   * - CPT_JSON_SCHEMA_INVALID
     - 100301
     - schema无效
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空
   * - CPT_NOT_EXISTS
     - 500301
     - cpt不存在
   * - CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX
     - 500302
     - 为权威机构生成的cptId超过上限
   * - CPT_PUBLISHER_NOT_EXIST
     - 500303
     - CPT发布者的WeIdentity DID不存在


**调用示例**

.. code-block:: java

   private CptServiceImpl cptService = new CptServiceImpl();
   String schema = "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"title\":\"/etc/fstab\",\"description\":\"JSON representation of /etc/fstab\",\"type\":\"object\",\"properties\":{\"swap\":{\"$ref\":\"#/definitions/mntent\"}},\"patternProperties\":{\"^/([^/]+(/[^/]+)*)?$\":{\"$ref\":\"#/definitions/mntent\"}},\"required\":[\"/\",\"swap\"],\"additionalProperties\":false,\"definitions\":{\"mntent\":{\"title\":\"mntent\",\"description\":\"An fstab entry\",\"type\":\"object\",\"properties\":{\"device\":{\"type\":\"string\"},\"fstype\":{\"type\":\"string\"},\"options\":{\"type\":\"array\",\"minItems\":1,\"items\":{\"type\":\"string\"}},\"dump\":{\"type\":\"integer\",\"minimum\":0},\"fsck\":{\"type\":\"integer\",\"minimum\":0}},\"required\":[\"device\",\"fstype\"],\"additionalItems\":false}}}";
   RegisterCptArgs args = new RegisterCptArgs();
   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   args.setCptPublisherPrivateKey(weIdPrivateKey);
   args.setCptJsonSchema(schema);
   args.setCptPublisher("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
   weIdPrivateKey.setPrivateKey(new String(Base64.encode(new BigInteger(
        "84259158061731800175730035500197147557630375762366333000754891654353899157503")
                       .toByteArray())));
   ResponseData<RegisterCptResult> response = cptService.registerCpt(args);
   
.. code-block:: text

   返回数据如下：
   result:(com.webank.weid.protocol.response.CptBaseInfo)
      cptId: 148
      cptVersion: 1
   errorCode: 0
   errorMessage: success

**时序图**

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

----

2. queryCpt
~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CptService.queryCpt
   接口定义:ResponseData<Cpt> queryCpt(Integer cptId)
   接口详细描述: 根据cpt编号查询cpt注册信息

**接口入参**\ :    java.lang.Integer

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - value
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
     - JavaBean
     - CPT内容，见下


com.webank.weid.protocol.base.Cpt

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - cptJsonSchema
     - String
     - json验证器
     - 
   * - cptBaseInfo
     - CptBaseInfo
     - JavaBean
     - CPT基础数据，见下
   * - cptMetaData
     - CptMetaData
     - JavaBean
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
     - Long
     - 更新时间
     - 
   * - created
     - Long
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
   * - CPT_NOT_EXISTS
     - 500301
     - cpt不存在


**调用示例**

.. code-block:: java

   private CptServiceImpl cptService = new CptServiceImpl();;
   ResponseData<QueryCptResult> response = cptService.queryCpt("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");

.. code-block:: text

   返回数据如下：
   result:(com.webank.weid.protocol.response.Cpt)
      cptBaseInfo:(com.webank.weid.protocol.response.CptBaseInfo)
         cptId: 148
         cptVersion: 1
      cptJsonSchema: {"$schema":"http://json-schema.org/draft-04/schema#","title":"/etc/fstab","description":"JSON representation of /etc/fstab","type":"object","properties":{"swap":{"$ref":"#/definitions/mntent"}},"patternProperties":{"^/([^/]+(/[^/]+)*)?$":{"$ref":"#/definitions/mntent"}},"required":["/","swap"],"additionalProperties":false,"definitions":{"mntent":{"title":"mntent","description":"An fstab entry","type":"object","properties":{"device":{"type":"string"},"fstype":{"type":"string"},"options":{"type":"array","minItems":1,"items":{"type":"string"}},"dump":{"type":"integer","minimum":0},"fsck":{"type":"integer","minimum":0}},"required":["device","fstype"],"additionalItems":false}}}
      cptMetaData:(com.webank.weid.protocol.response.Cpt$CptMetaData)
         cptPublisher: did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f
         cptSignature: HO+/ve+/vXfvv70hQO+/vQwMVO+/vSot77+977+9UGEhLO+/vW4277+977+977+9HO+/ve+/vSTvv70777+9f14=
         created: -1957647935
         updated: 0
   errorCode: 0
   errorMessage: success


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


----

3. updateCpt
~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CptService.updateCpt
   接口定义:ResponseData<CptBaseInfo> updateCpt(UpdateCptArgs args)
   接口详细描述: 传入cptId，json验证器，WeIdentity DID，WeIdentity DID所属私钥，进行更新cpt信息，更新成功版本自动+1
   有对cptPublisher和cptJsonSchema组合签名

**接口入参**\ :    com.webank.weid.protocol.request.UpdateCptArgs

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
     - cpt编号
     - 
   * - cptJsonSchema
     - String
     - Y
     - json验证器
     - json schema 数据
   * - cptPublisher
     - String
     - Y
     - CPT发布者的WeIdentity DID
     - WeIdentity DID格式
   * - cptPublisherPrivateKey
     - WeIdPrivateKey
     - Y
     - JavaBean
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
     - JavaBean
     - CPT基础数据，见下


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
   * - CPT_JSON_SCHEMA_INVALID
     - 100301
     - schema无效
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空
   * - CPT_ID_AUTHORITY_ISSUER_EXCEED_MAX
     - 500302
     - 为权威机构生成的cptId超过上限
   * - CPT_NOT_EXISTS
     - 500301
     - cpt不存在
   * - CPT_PUBLISHER_NOT_EXIST
     - 500303
     - cpt发布者的WeIdentity DID不存在


**调用示例**

.. code-block:: java

   private CptServiceImpl cptService = new CptServiceImpl();
   String schema = "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"title\":\"/etc/fstab\",\"description\":\"JSON representation of /etc/fstab\",\"type\":\"object\",\"properties\":{\"swap\":{\"$ref\":\"#/definitions/mamntent\"}},\"patternProperties\":{\"^/([^/]+(/[^/]+)*)?$\":{\"$ref\":\"#/definitions/mntent\"}},\"required\":[\"/\",\"swap\"],\"additionalProperties\":false,\"definitions\":{\"mntent\":{\"title\":\"mntent\",\"description\":\"An fstab entry\",\"type\":\"object\",\"properties\":{\"device\":{\"type\":\"string\"},\"fstype\":{\"type\":\"string\"},\"options\":{\"type\":\"array\",\"minItems\":1,\"items\":{\"type\":\"string\"}},\"dump\":{\"type\":\"integer\",\"minimum\":0},\"fsck\":{\"type\":\"integer\",\"minimum\":0}},\"required\":[\"device\",\"fstype\"],\"additionalItems\":false}}}";

   UpdateCptArgs args = new UpdateCptArgs();
   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   args.setCptPublisherPrivateKey(weIdPrivateKey);
   args.setCptJsonSchema(schema);
   args.setCptPublisher("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
   weIdPrivateKey.setPrivateKey(new String(Base64.encode(new BigInteger("84259158061731800175730035500197147557630375762366333000754891654353899157503")
                       .toByteArray())));
   args.setCptId(148);
   ResponseData<CptBaseInfo> response = cptService.updateCpt(args);

.. code-block:: text

   返回数据如下：
   result:(com.webank.weid.protocol.response.CptBaseInfo)
      cptId: 148
      cptVersion: 3
   errorCode: 0
   errorMessage: success

**时序图**

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

----

CredentialService
^^^^^^^^^^^^^^^^^

1. createCredential
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialService.createCredential
   接口定义:ResponseData<Credential> createCredential(CreateCredentialArgs args)
   接口详细描述: 生成证书信息，有判断要求数据有效，相关非空验证等
   注意：本接口并不进行链上操作检查CPT真实性等问题。创造出来的credential有可能是无效的！
   调用方有责任在调用之前通过其他API判断。

**接口入参**\ :   com.webank.weid.protocol.request.CreateCredentialArgs

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
     - cpt编号
     - 
   * - issuer
     - String
     - Y
     - 发行方WeIdentity DID
     - WeIdentity DID格式数据
   * - expirationDate
     - String
     - Y
     - 到期日
     - 
   * - claimData
     - String
     - Y
     - Claim的json格式数据
     - 通过cpt的jsonSchema验证的数据
   * - cptPublisherPrivateKey
     - WeIdPrivateKey
     - Y
     - JavaBean
     - 签名所用Issuer WeIdentity DID私钥，见下


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


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<Credential>;

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
     - Credential
     - JavaBean
     - 见下


com.webank.weid.protocol.base.Credential

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - context
     - String
     - Y
     - 版本
     - 默认为v1
   * - id
     - String
     - Y
     - 证书编号
     - 
   * - cptId
     - Integer
     - Y
     - cptId
     - 
   * - issuer
     - String
     - Y
     - WeIdentity DID
     - 
   * - issuranceDate
     - Long
     - Y
     - 创建日期
     - 
   * - expirationDate
     - Long
     - Y
     - 到期日期
     - 
   * - signature
     - String
     - Y
     - 签名数据
     - 
   * - claimData
     - String
     - Y
     - Claim数据
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
   * - CREDENTIAL_ERROR
     - 100400
     - Credential标准错误
   * - CREDENTIAL_ISSUER_NOT_EXISTS
     - 100407
     - WeIdentity DID不能为空
   * - CREDENTIAL_EXPIRE_DATE_ILLEGAL
     - 100409
     - 到期日期无效
   * - CREDENTIAL_CLAIM_NOT_EXISTS
     - 100410
     - Claim数据不能为空
   * - CREDENTIAL_CLAIM_DATA_ILLEGAL
     - 100411
     - Claim数据无效
   * - CREDENTIAL_PRIVATE_KEY_NOT_EXISTS
     - 100415
     - 私钥为空
   * - CREDENTIAL_CPT_NOT_EXISTS
     - 100416
     - cpt不存在
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   @Autowired
   private CredentialService credentialService;
   String schema1Data =  "{\"/\":{\"device\":\"/dev/sda1\",\"fstype\":\"btrfs\",\"options\":[\"ssd\"]},\"swap\":{\"device\":\"/dev/sda2\",\"fstype\":\"swap\"},\"/tmp\":{\"device\":\"tmpfs\",\"fstype\":\"tmpfs\",\"options\":[\"size=64M\"]},\"/var/lib/mysql\":{\"device\":\"/dev/data/mysql\",\"fstype\":\"btrfs\"}}";

   CreateCredentialArgs args = new CreateCredentialArgs();
   args.setClaimData(RequestUtil.schema1Data);
   args.setCptId(155);
   args.setExpirationDate(21313312312312312L);
   args.setIssuer("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey(new BigInteger(1231232142).toString());
   args.setWeIdPrivateKey(weIdPrivateKey);
   ResponseData<Credential> response = credentialService.createCredential(args);

.. code-block:: text

   返回结果如：
   result:(com.webank.weid.protocol.base.Credential)
      context: v1
      credentialId: 41e07e22-9022-476f-80d6-0dfbd6a328d1
      cptId: 155
      issuer: did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f
      issuranceDate: 1539239136000
      expirationDate: 1539259136000
      claimData: {"/":{"device":"/dev/sda1","fstype":"btrfs","options":["ssd"]},"swap":{"device":"/dev/sda2","fstype":"swap"},"/tmp":{"device":"tmpfs","fstype":"tmpfs","options":["size=64M"]},"/var/lib/mysql":{"device":"/dev/data/mysql","fstype":"btrfs"}}
      signature: HPzECgQJOWWhOfFOfZjOTwEv0b7DZXfji39jdaC1+TTHXXpJJBQ+rBHQ7tPlsBokBhUCmm5EG/CkQJCb7z03FuA=
   errorCode: 0
   errorMessage: success

**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant CredentialService
   调用者->>CredentialService: 调用CreateCredential()
   CredentialService->>CredentialService: 入参非空、格式及合法性检查
   opt 入参校验失败
   CredentialService-->>调用者: 报错，提示参数不合法并退出
   end
   CredentialService->>CredentialService: 生成签发日期、生成数字签名
   CredentialService-->>调用者: 返回凭证

----

2. verifyCredential
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialService.verifyCredential
   接口定义:ResponseData<Boolean> verifyCredential(Credential args)
   接口详细描述: 传入Credential信息进行验证，无需公钥

**接口入参**\ :   com.webank.weid.protocol.base.Credential

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - context
     - String
     - Y
     - 版本
     - 默认为v1
   * - id
     - String
     - Y
     - 证书编号
     - 
   * - cptId
     - Integer
     - Y
     - cptId
     - 
   * - issuer
     - String
     - Y
     - WeIdentity DID
     - 
   * - issuranceDate
     - Long
     - Y
     - 创建日期
     - 
   * - expirationDate
     - Long
     - Y
     - 到期日期
     - 
   * - signature
     - String
     - Y
     - 签名数据
     - 
   * - claimData
     - String
     - Y
     - Claim数据
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
     - 返回结果值
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
   * - CPT_JSON_SCHEMA_INVALID
     - 100301
     - Json Schema非法
   * - CREDENTIAL_ERROR
     - 100400
     - Credential标准错误
   * - CREDENTIAL_NOT_EXISTS
     - 100401
     - Credential入参为空
   * - CREDENTIAL_EXPIRED
     - 100402
     - 过期
   * - CREDENTIAL_ISSUER_MISMATCH
     - 100403
     - issuer与签名不匹配
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名破坏
   * - CREDENTIAL_REVOKED
     - 100406
     - 已被撤销
   * - CREDENTIAL_ISSUER_NOT_EXISTS
     - 100407
     - WeIdentity DID不能为空
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
   * - CREDENTIAL_EXPIRE_DATE_ILLEGAL
     - 100409
     - 到期日期格式非法
   * - CREDENTIAL_CLAIM_NOT_EXISTS
     - 100410
     - Claim数据不能为空
   * - CREDENTIAL_CLAIM_DATA_ILLEGAL
     - 100411
     - Claim数据无效
   * - CREDENTIAL_ID_NOT_EXISTS
     - 100412
     - ID为空
   * - CREDENTIAL_CONTEXT_NOT_EXISTS
     - 100413
     - context为空
   * - CREDENTIAL_CPT_NOT_EXISTS
     - 100416
     - cpt不存在
   * - CREDENTIAL_WEID_DOCUMENT_ILLEGAL
     - 100417
     - WeIdentity Document为空
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   @Autowired
   private CredentialService credentialService;
     String schemaData =  "{\"/\":{\"device\":\"/dev/sda2\",\"fstype\":\"btrfs\",\"options\":[\"ssd\"]},\"swap\":{\"device\":\"/dev/sda2\",\"fstype\":\"swap\"},\"/tmp\":{\"device\":\"tmpfs\",\"fstype\":\"tmpfs\",\"options\":[\"size=64M\"]},\"/var/lib/mysql\":{\"device\":\"/dev/data/mysql\",\"fstype\":\"btrfs\"}}";

   Credential args = new Credential();
   args.setClaimData(RequestUtil.schemaData);
   args.setContext("v1");
   args.setCptId(155);
   args.setIssuranceDate(11313312312312312L);
   args.setCredentialId("54bc3832-fce7-433a-80c7-ba284635c67a");// 系统生成
   args.setSignature("HLrW58iqkupFZAykaPTvU8RJ1paNUk3dou9h4LFR22y2NjQsINN2DkQk8otiKLuSUjrFFvupSxfpEvUrMOC5nWc=");
   args.setExpirationDate(21313312312312312L);
   args.setIssuer("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
   ResponseData<Boolean> response = credentialService.verifyCredential(args);

.. code-block:: text

   返回结果如：
   result: false
   errorCode: 0
   errorMessage: success


**时序图**

（同时也包含verifyCredentialWithSpecifiedPubKey时序）

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant CredentialService
   participant CptService
   participant WeIdService
   participant 区块链节点
   调用者->>CredentialService: 调用VerifyCredential()或verifyCredentialWithSpecifiedPubKey()
   CredentialService->>CredentialService: 入参非空、格式及合法性检查
   opt 入参校验失败
   CredentialService-->>调用者: 报错，提示参数不合法并退出
   end
   CredentialService->>WeIdService: 查询WeIdentity DID存在性
   WeIdService->>区块链节点: 调用智能合约，查询WeIdentity DID属性
   区块链节点-->>WeIdService: 返回查询结果
   WeIdService-->>CredentialService: 返回查询结果
   opt 查询不存在
   CredentialService-->>调用者: 报错并退出
   end
   CredentialService->>CptService: 查询CPT存在性及Claim关联语义
   CptService->>区块链节点: 调用智能合约，查询CPT
   区块链节点-->>CptService: 返回查询结果
   CptService-->>CredentialService: 返回查询结果
   opt 不符合CPT格式要求
   CredentialService-->>调用者: 报错并退出
   end
   CredentialService->>CredentialService: 验证过期、撤销与否
   opt 任一验证失败
   CredentialService-->>调用者: 报错并退出
   end
   opt 未提供验签公钥
   CredentialService->>WeIdService: 查询Issuer对应公钥
   WeIdService->>区块链节点: 调用智能合约，查询Issuer的WeIdentity DID Document
   区块链节点-->>WeIdService: 返回查询结果
   WeIdService-->>CredentialService: 返回查询结果
   end
   CredentialService->>CredentialService: 通过公钥与签名对比，验证Issuer是否签发此凭证
   opt 验证签名失败
   CredentialService-->>调用者: 报错并退出
   end
   CredentialService-->>调用者: 返回成功

----

3. verifyCredentialWithSpecifiedPubKey
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialService.verifyCredentialWithSpecifiedPubKey
   接口定义:ResponseData<Boolean> verifyCredentialWithSpecifiedPubKey(VerifyCredentialArgs args)
   接口详细描述: 传入Credential信息进行验证，需公钥一并传入

**接口入参**\ :   com.webank.weid.protocol.request.VerifyCredentialArgs

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - credential
     - Credential
     - Y
     - JavaBean，见下
     - 
   * - publicKey
     - String
     - Y
     - 公钥
     - 


com.webank.weid.protocol.base.Credential

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - context
     - String
     - Y
     - 版本
     - 默认为v1
   * - id
     - String
     - Y
     - 证书编号
     - 
   * - cptId
     - Integer
     - Y
     - cptId
     - 
   * - issuer
     - String
     - Y
     - WeIdentity DID
     - 
   * - issuranceDate
     - Long
     - Y
     - 创建日期
     - 
   * - expirationDate
     - Long
     - Y
     - 到期日期
     - 
   * - signature
     - String
     - Y
     - 签名数据
     - 
   * - claimData
     - String
     - Y
     - Claim数据
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
     - 返回结果值
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
   * - CPT_JSON_SCHEMA_INVALID
     - 100301
     - Json Schema非法
   * - CREDENTIAL_ERROR
     - 100400
     - Credential标准错误
   * - CREDENTIAL_NOT_EXISTS
     - 100401
     - Credential入参为空
   * - CREDENTIAL_EXPIRED
     - 100402
     - 过期
   * - CREDENTIAL_ISSUER_MISMATCH
     - 100403
     - issuer与签名不匹配
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名破坏
   * - CREDENTIAL_REVOKED
     - 100406
     - 已被撤销
   * - CREDENTIAL_ISSUER_NOT_EXISTS
     - 100407
     - WeIdentity DID不能为空
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
   * - CREDENTIAL_EXPIRE_DATE_ILLEGAL
     - 100409
     - 到期日期格式非法
   * - CREDENTIAL_CLAIM_NOT_EXISTS
     - 100410
     - Claim数据不能为空
   * - CREDENTIAL_CLAIM_DATA_ILLEGAL
     - 100411
     - Claim数据无效
   * - CREDENTIAL_ID_NOT_EXISTS
     - 100412
     - ID为空
   * - CREDENTIAL_CONTEXT_NOT_EXISTS
     - 100413
     - context为空
   * - CREDENTIAL_CPT_NOT_EXISTS
     - 100416
     - cpt不存在
   * - CREDENTIAL_WEID_DOCUMENT_ILLEGAL
     - 100417
     - WeIdentity Document为空
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   @Autowired
   private CredentialService credentialService;
     String schemaData =  "{\"/\":{\"device\":\"/dev/sda2\",\"fstype\":\"btrfs\",\"options\":[\"ssd\"]},\"swap\":{\"device\":\"/dev/sda2\",\"fstype\":\"swap\"},\"/tmp\":{\"device\":\"tmpfs\",\"fstype\":\"tmpfs\",\"options\":[\"size=64M\"]},\"/var/lib/mysql\":{\"device\":\"/dev/data/mysql\",\"fstype\":\"btrfs\"}}";

   Credential args = new Credential();
   args.setClaimData(RequestUtil.schemaData);
   args.setContext("v1");
   args.setCptId(155);
   args.setIssuranceDate(11313312312312312L);
   args.setCredentialId("54bc3832-fce7-433a-80c7-ba284635c67a");// 系统生成
   args.setSignature("HLrW58iqkupFZAykaPTvU8RJ1paNUk3dou9h4LFR22y2NjQsINN2DkQk8otiKLuSUjrFFvupSxfpEvUrMOC5nWc=");
   args.setExpirationDate(21313312312312312L);
   args.setIssuer("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
   VerifyCredentialArgs verify=new VerifyCredentialArgs();
   verify.setCredential(args);
   WeIdPublicKey weIdPublicKey = new WeIdPublicKey();
    weIdPublicKey.setPublicKey(new BigInteger("13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342"));
    verify.setWeIdPublicKey(weIdPublicKey);
   ResponseData<Boolean> response = credentialService.verifyCredentialWithSpecifiedPubKey(verify);

.. code-block:: text

   返回结果如：
   result: false
   errorCode: 0
   errorMessage: success

----

4. getCredentialHash
~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialService.getCredentialHash
   接口定义:ResponseData<String> getCredentialHash(Credential args)
   接口详细描述: 传入Credential信息生成Credential整体的Hash值。

**调用示例**

.. code-block:: java
   @Autowired
   private CredentialService credentialService;
     String schemaData =  "{\"/\":{\"device\":\"/dev/sda2\",\"fstype\":\"btrfs\",\"options\":[\"ssd\"]},\"swap\":{\"device\":\"/dev/sda2\",\"fstype\":\"swap\"},\"/tmp\":{\"device\":\"tmpfs\",\"fstype\":\"tmpfs\",\"options\":[\"size=64M\"]},\"/var/lib/mysql\":{\"device\":\"/dev/data/mysql\",\"fstype\":\"btrfs\"}}";
   Credential args = new Credential();
   args.setClaimData(RequestUtil.schemaData);
   args.setContext("v1");
   args.setCptId(155);
   args.setIssuranceDate(11313312312312312L);
   args.setCredentialId("54bc3832-fce7-433a-80c7-ba284635c67a");// 系统生成
   args.setSignature("HLrW58iqkupFZAykaPTvU8RJ1paNUk3dou9h4LFR22y2NjQsINN2DkQk8otiKLuSUjrFFvupSxfpEvUrMOC5nWc=");
   args.setExpirationDate(21313312312312312L);
   args.setIssuer("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
   String credHash = credentialService.getCredentialHash(args);
   return credHash;
.. code-block:: text

   返回结果如：
   result: d8d969faf6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923bea5459
   errorCode: 0
   errorMessage: success

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CREDENTIAL_ERROR
     - 100400
     - Credential标准错误
   * - CREDENTIAL_NOT_EXISTS
     - 100401
     - Credential入参为空
   * - CREDENTIAL_EXPIRED
     - 100402
     - 过期
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名破坏
   * - CREDENTIAL_ISSUER_NOT_EXISTS
     - 100407
     - WeIdentity DID不能为空
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
   * - CREDENTIAL_EXPIRE_DATE_ILLEGAL
     - 100409
     - 到期日期格式非法
   * - CREDENTIAL_CLAIM_NOT_EXISTS
     - 100410
     - Claim数据不能为空
   * - CREDENTIAL_CLAIM_DATA_ILLEGAL
     - 100411
     - Claim数据无效
   * - CREDENTIAL_ID_NOT_EXISTS
     - 100412
     - ID为空
   * - CREDENTIAL_CONTEXT_NOT_EXISTS
     - 100413
     - context为空
   * - CREDENTIAL_PRIVATE_KEY_NOT_EXISTS
     - 100415
     - 私钥为空
   * - CREDENTIAL_CPT_NOT_EXISTS
     - 100416
     - cpt不存在
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空

**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant CredentialService
   调用者->>CredentialService: 调用GetCredentialHash()
   CredentialService->>CredentialService: 入参非空、格式及合法性检查
   opt 入参校验失败
   CredentialService-->>调用者: 报错，提示参数不合法并退出
   end
   CredentialService->>CredentialService: 生成凭证Hash
   CredentialService-->>调用者: 返回凭证Hash

----


WeIDService
^^^^^^^^^^^

1. createWeId
~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.createWeId
   接口定义:ResponseData<CreateWeIdDataResult> createWeId()
   接口详细描述: 生成一对公私钥,将公钥转换成16进制，并生产weid:did:weid:1:0x………………….
   并返回公钥 私钥 以及WeIdentity DID

**接口入参**\ :   无

**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<CreateWeIdDataResult>;

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
     - CreateWeIdDataResult
     - JavaBean
     - 见下


com.webank.weid.protocol.response.CreateWeIdDataResult

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - weId
     - String
     - 公钥WeIdentity DID格式字符串
     - 格式:did:weid:1:0x685
   * - userWeIdPublicKey
     - WeIdPublicKey
     - JavaBean
     - 
   * - userWeIdPrivateKey
     - WeIdPrivateKey
     - JavaBean
     - 


com.webank.weid.protocol.base.WeIdPublicKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - publicKey
     - String
     - 数字公钥
     - 如下调用示例返回，使用十进制数字表示


com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - privateKey
     - String
     - 数字私钥
     - 如下调用示例返回，使用十进制数字表示


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - WEID_KEYPAIR_CREATE_FAILED
     - 10107
     - 创建密钥对失败
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误


**调用示例**

.. code-block:: java

   @Autowired
   private WeIdService weIdService;
   ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();

.. code-block:: text

   输出结果如下：
   result:(com.webank.weid.protocol.response.CreateWeIdDataResult)
      weId: did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f
      userWeIdPublicKey:(com.webank.weid.protocol.base.WeIdPublicKey)
         publicKey: 13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342
      userWeIdPrivateKey:(com.webank.weid.protocol.base.WeIdPrivateKey)
         privateKey: 84259158061731800175730035500197147557630375762366333000754891654353899157503
   errorCode: 0
   errorMessage: success


**时序图**

.. mermaid::

   sequenceDiagram
   调用者->>WeIdentity SDK: 调用CreateWeID()
   WeIdentity SDK->>WeIdentity SDK: 创建公私钥对
   WeIdentity SDK->>区块链节点: 调用智能合约
   区块链节点->>区块链节点: 以事件的方式记录created属性和public key属性
   区块链节点->>区块链节点: 记录当前的最新块高
   区块链节点-->>WeIdentity SDK: 创建成功
   WeIdentity SDK-->>调用者:新创建好的WeIdentity DID以及公私钥对

----

2. createWeId
~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.createWeId
   接口定义:ResponseData<String> createWeId(CreateWeIdArgs createWeIdArgs)
   接口详细描述: 传入数字公钥，如果数字公钥为null 则返回为空，如果数字公钥不为空，则将数字公钥转换成16进制，并组成成WeIdentity DID

**接口入参**\ :  com.webank.weid.protocol.request.CreateWeIdArgs

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - publicKey
     - String
     - Y
     - 数字公钥
     - 
   * - weIdPrivateKey
     - WeIdPrivateKey
     - N
     - JavaBean
     - 后期鉴权使用


com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - privateKey
     - String
     - 数字私钥
     - 使用十进制数字表示


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
     - 公钥WeIdentity DID格式字符串
     - 如：did:weid:1:0x....


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - WEID_PUBLICKEY_AND_PRIVATEKEY_NOT_MATCHED
     - 10108
     - 公私钥不成对
   * - WEID_PRIVATEKEY_INVALID
     - 100103
     - 私钥格式非法
   * - WEID_ALREADY_EXIST
     - 100105
     - WeIdentity DID已存在
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥不与WeIdentity DID所对应
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   @Autowired
   private WeIdService weIdService;
   CreateWeIdArgs args=new CreateWeIdArgs();
   args.setPublicKey("13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342");
   ResponseData<String> response = weIdService.createWeId(args);

.. code-block:: text

   输出结果如下：
   result: did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f
   errorCode: 0
   errorMessage: success

**时序图**

.. mermaid::

   sequenceDiagram
   Note over 调用者:传入自己的WeIdentity DID及用作authentication的私钥
   调用者->>WeIdentity SDK:调用CreateWeID()
   WeIdentity SDK->>区块链节点:调用智能合约
   区块链节点->>区块链节点: 检查调用者的身份是否和WeIdentity DID匹配　　　
   opt 身份校验不通过
   区块链节点-->>WeIdentity SDK:报错，提示私钥不匹配并退出
   WeIdentity SDK-->>调用者:报错退出
   end
   区块链节点->>区块链节点 : 以事件的方式记录created属性和public key属性
   区块链节点->>区块链节点 : 记录当前的最新块高
   区块链节点-->>WeIdentity SDK: 创建成功
   WeIdentity SDK-->>调用者:新创建好的WeIdentity DID

----

3. getWeIdDocumentJson
~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.getWeIdDocumentJson
   接口定义:ResponseData<String> getWeIdDocumentJson(String weId)
   接口详细描述: 传入WeIdentity DID，检查其有效性：不能为null,并且包含did:weid:
   解析WeIdentity DID的公钥地址（16进制的hash地址），根据公钥地址，去查找最后的区块数据，并且递归去查找全部数据并解析
   成WeIdentity docment,将WeIdentity docment格式化为json字符串

**接口入参**\ :   String

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
     - WeIdentity DID字符串
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
     - weidDocument Json
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
     - 无效的WeIdentity DID
   * - WEID_DOES_NOT_EXIST
     - 100104
     - WeIdentity DID不存在
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   @Autowired
   private WeIdService weIdService;
   ResponseData<String> response = weIdService.getWeIdDocumentJson("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");

.. code-block:: text

   返回结果如下：
   result: {
               "@context": "https://weidentity.webank.com/did/v1",
              "authentication": [{
                           "publicKey": "did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f#keys-0",
                           "type": "Secp256k1SignatureAuthentication2018"
              }],
             "created": "2018-10-19T11:11:10Z",
             "id": "did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f",
             "publicKey": [{
                       "id": "did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f#keys-0",
                       "owner": "did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f",
                      "publicKey": "13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342",
                     "type": "Secp256k1VerificationKey2018"
              }],
             "service": [{
                       "serviceEndpoint": "https://weidentity.webank.com/endpoint/8377464",
                       "type": "drivingCardService"
            }],
          "updated": "2018-10-19T12:13:38Z"
          }
   errorCode: 0
   errorMessage: success

**时序图**

（同时也包含getWeIDDocment时序）

.. mermaid::

   sequenceDiagram
   调用者->>WeIdentity SDK : 传入指定的WeIdentity DID
   WeIdentity SDK->>区块链节点: 调用智能合约
   区块链节点->>区块链节点: 查找记录该WeIdentity DID关联的属性事件最后一次更新时的块高
   区块链节点-->>WeIdentity SDK: 返回
   loop 解析事件
   WeIdentity SDK->>区块链节点: 根据块高，过滤该区块里的属性事件
   区块链节点-->>WeIdentity SDK: 返回
   WeIdentity SDK->>WeIdentity SDK: 根据块高，获取到对应区块所有交易
   WeIdentity SDK->>WeIdentity SDK: 根据交易获取交易回执
   WeIdentity SDK->>WeIdentity SDK: 根据交易回执过滤跟当前WeIdentity DID相关的属性事件
   WeIdentity SDK->>WeIdentity SDK: 根据不同的key，解析public key, authentication, service endpoint
   WeIdentity SDK->>WeIdentity SDK: 组装WeIdentity Document
   WeIdentity SDK->>WeIdentity SDK: 根据当前事件找到上一个事件对应的块高
   end
   WeIdentity SDK-->>调用者:返回WeIdentity Document

----

4. getWeIDDocment
~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.getWeIdDocument
   接口定义:ResponseData<WeIdDocument> getWeIdDocument(String weId)
   接口详细描述: 传入WeIdentity DID，检查其有效性：不能为null,并且包含did:weid:
   解析WeIdentity DID的公钥地址（16进制的hash地址），根据公钥地址，去查找最后的区块数据，并且递归去查找全部数据并解析
   成WeIdentity docment对象

**接口入参**\ :  String

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
     - WeIdentity DID字符串
     - 


**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<WeIdDocument>;

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
     - WeIdDocument
     - JavaBean
     - 见下


com.webank.weid.protocol.base.WeIdDocument

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - id
     - String
     - 
     - 
   * - created
     - String
     - 
     - 
   * - updated
     - String
     - 
     - 
   * - publicKey
     - List\ :raw-html-m2r:`<PublicKeyProperty>`
     - JavaBean
     - 列出公钥集合，见下
   * - authentication
     - List\ :raw-html-m2r:`<AuthenticationProperty>`
     - JavaBean
     - 认证方集合，见下
   * - service
     - List\ :raw-html-m2r:`<ServiceProperty>`
     - JavaBean
     - 服务端点集合，见下


com.webank.weid.protocol.base.PublicKeyProperty

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - id
     - String
     - 
     - 
   * - type
     - String
     - 类型
     - 默认为：Secp256k1VerificationKey2018
   * - owner
     - String
     - 拥有者WeIdentity DID
     - 
   * - publicKey
     - String
     - 数字公钥
     - 


com.webank.weid.protocol.base.AuthenticationProperty

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - type
     - String
     - 类型
     - 默认为：Secp256k1SignatureAuthentication2018
   * - publicKey
     - String
     - 
     - 


com.webank.weid.protocol.base.ServiceProperty

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - type
     - String
     - 类型
     - 
   * - serviceEndpoint
     - String
     - 
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
     - 无效的WeIdentity DID
   * - WEID_DOES_NOT_EXIST
     - 100104
     - WeIdentity DID不存在
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   @Autowired
   private WeIdService weIdService;
   ResponseData<String> response = weIdService.getWeIdDocument("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");

.. code-block:: text

   返回结果如下：
   result:(com.webank.weid.protocol.base.WeIdDocument)
      id: did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f
      created: 2018-10-19T11:11:10Z
      updated: 2018-10-19T12:13:38Z
      publicKey:(java.util.ArrayList)
         [0]: com.webank.weid.protocol.base.PublicKeyProperty
            id: did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f#keys-0
            type: Secp256k1VerificationKey2018
            owner: did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f
            publicKey: 13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342
      authentication:(java.util.ArrayList)
         [0]: com.webank.weid.protocol.base.AuthenticationProperty
            type: Secp256k1SignatureAuthentication2018
            publicKey: did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f#keys-0
      service:(java.util.ArrayList)
         [0]: com.webank.weid.protocol.base.ServiceProperty
            type: drivingCardService
            serviceEndpoint: https://weidentity.webank.com/endpoint/8377464
   errorCode: 0
   errorMessage: success

----

5. setPublicKey
~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.setPublicKey
   接口定义:ResponseData<Boolean> setPublicKey(SetPublicKeyArgs setPublicKeyArgs)
   接口详细描述: 传入WeIdentity DID格式字符串，hash类型，数字公钥，
   根据WeIdentity DID格式字符串提取16进制公钥hash值，数字公钥，组装公钥属性key，进行上链操作

**接口入参**\ :   com.webank.weid.protocol.request.SetPublicKeyArgs

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
     - WeIdentity DID格式字符串
     - 如：did:weid:1:0x....
   * - type
     - String
     - Y
     - hash套件
     - 默认：Secp256k1
   * - owner
     - String
     - N
     - 所有者
     - 
   * - publicKey
     - String
     - Y
     - 数字公钥
     - 
   * - userWeIdPrivateKey
     - WeIdPrivateKey
     - N
     - JavaBean
     - 交易私钥，后期鉴权使用，见下


com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - privateKey
     - String
     - 数字私钥
     - 使用十进制数字表示


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
     - 是否set成功
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
     - 无效的WeIdentity DID
   * - WEID_PRIVATEKEY_INVALID
     - 100103
     - 私钥格式非法
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥不与WeIdentity DID所对应
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   @Autowired
   private WeIdService weIdService;
   SetPublicKeyArgs args=new SetPublicKeyArgs();
   args.setWeId("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
   args.setType("Secp256k1");
   args.setPublicKey("13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342");
   ResponseData<Boolean> response = weIdService.setPublicKey(args)

.. code-block:: text

   返回结果如下：
   result: true
   errorCode: 0
   errorMessage: success

**时序图**


.. mermaid::

   sequenceDiagram
   Note over 调用者:传入自己的WeIdentity DID及用作authentication的公私钥
   调用者->>WeIdentity SDK : 调用setPublicKey来添加公钥。
   WeIdentity SDK->>WeIdentity SDK:拿私钥来重新加载合约对象
   WeIdentity SDK->>区块链节点: 调用智能合约
   区块链节点->>区块链节点: 检查调用者的身份是否和WeIdentity DID匹配　　　
   opt 身份校验不通过
   区块链节点-->>WeIdentity SDK:报错，提示私钥不匹配并退出
   WeIdentity SDK-->>调用者:报错退出
   end
   区块链节点->>区块链节点:将公钥和WeIdentity DID以及上次记录的块高写到属性事件中
   区块链节点->>区块链节点:记录最新块高
   区块链节点-->>WeIdentity SDK:返回
   WeIdentity SDK-->>调用者:返回调用结果

----

6. setService
~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.setService
   接口定义:ResponseData<Boolean> setService(SetServiceArgs setServiceArgs)
   接口详细描述: 传入WeIdentity DID，服务名称（type），服务端点
   根据WeIdentity DID格式字符串提取16进制公钥hash值，组装公钥属性key，进行上链操作

**接口入参**\ :   com.webank.weid.protocol.request.SetServiceArgs

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
     - WeIdentity DID格式字符串
     - 如：did:weid:1:0x.....
   * - serviceName
     - String
     - Y
     - 服务名称
     - 如：drivingCardService
   * - serviceEndpoint
     - String
     - Y
     - 服务端点
     - 如："https://weidentity.webank.com/endpoint/8377464"
   * - userWeIdPrivateKey
     - WeIdPrivateKey
     - N
     - JavaBean
     - 交易私钥，后期鉴权使用，见下


com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - privateKey
     - String
     - 数字私钥
     - 使用十进制数字表示


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
     - 是否set成功
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
     - 无效的WeIdentity DID
   * - WEID_PRIVATEKEY_INVALID
     - 100103
     - 私钥格式非法
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥不与WeIdentity DID所对应
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   @Autowired
   private WeIdService weIdService;
   SetServiceArgs args=new SetServiceArgs();
   args.setWeId("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
   args.setServiceName("drivingCardService");
   args.setServiceEndpoint("https://weidentity.webank.com/endpoint/8377464");
   ResponseData<Boolean> response = weIdService.setService(args);

.. code-block:: text

   返回结果如下：
   result: true
   errorCode: 0
   errorMessage: success

**时序图**

.. mermaid::

   sequenceDiagram
   Note over 调用者:传入自己的WeIdentity DID及要用作<br>authentication的私钥，<br>以及service endpoint
   调用者->>WeIdentity SDK : 调用setAuthentication来添加认证。
   WeIdentity SDK->>WeIdentity SDK:拿私钥来重新加载合约对象
   WeIdentity SDK->>区块链节点: 调用智能合约
   区块链节点->>区块链节点: 检查调用者的身份是否和WeIdentity DID匹配　　　
   opt 身份校验不通过
   区块链节点-->>WeIdentity SDK:报错，提示私钥不匹配并退出
   WeIdentity SDK-->>调用者:报错退出
   end
   区块链节点->>区块链节点:将service endpoint和WeIdentity DID以及上次记录的块高写到属性事件中
   区块链节点->>区块链节点:记录最新块高
   区块链节点-->>WeIdentity SDK:返回
   WeIdentity SDK-->>调用者:返回调用结果

----

7. setAuthentication
~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.setAuthentication
   接口定义:ResponseData<Boolean> setAuthentication(SetAuthenticationArgs setAuthenticationArgs)
   接口详细描述: 传入WeIdentity DID格式字符串，hash类型，数字公钥，
   根据WeIdentity DID格式字符串提取16进制公钥hash值，数字公钥，组装认证属性key，进行上链操作

**接口入参**\ :   com.webank.weid.protocol.request.SetAuthenticationArgs

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
     - WeIdentity DID格式字符串
     - 如：did:weid:1:0x....
   * - type
     - String
     - Y
     - hash类型
     - 
   * - owner
     - String
     - N
     - 所属
     - 
   * - publicKey
     - String
     - Y
     - 数字公钥
     - 
   * - userWeIdPrivateKey
     - WeIdPrivateKey
     - N
     - JavaBean
     - 交易私钥，后期鉴权使用，见下


com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - privateKey
     - String
     - 数字私钥
     - 使用十进制数字表示


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
     - 是否set成功
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
     - 无效的WeIdentity DID
   * - WEID_PRIVATEKEY_INVALID
     - 100103
     - 私钥格式非法
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥不与WeIdentity DID所对应
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   @Autowired
   private WeIdService weIdService;
   SetAuthenticationArgs args=new SetAuthenticationArgs();
   args.setWeId("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
   args.setPublicKey("13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342");
   args.setType("RsaSignatureAuthentication2018");
   ResponseData<Boolean> response = weIdService.setAuthentication(args);


.. code-block:: text

   返回结果如下：
   result: true
   errorCode: 0
   errorMessage: success

**时序图**

.. mermaid::

   sequenceDiagram
   Note over 调用者:传入自己的WeIdentity DID及用作authentication的公私钥
   调用者->>WeIdentity SDK : 调用setAuthentication来添加认证。
   WeIdentity SDK->>WeIdentity SDK:拿私钥来重新加载合约对象
   WeIdentity SDK->>区块链节点: 调用智能合约
   区块链节点->>区块链节点: 检查调用者的身份是否和WeIdentity DID匹配　　　
   opt 身份校验不通过
   区块链节点-->>WeIdentity SDK:报错，提示私钥不匹配并退出
   WeIdentity SDK-->>调用者:报错退出
   end
   区块链节点->>区块链节点:将authentication和WeIdentity DID以及上次记录的块高写到属性事件中
   区块链节点->>区块链节点:记录最新块高
   区块链节点-->>WeIdentity SDK:返回
   WeIdentity SDK-->>调用者:返回调用结果

----

EvidenceService
^^^^^^^^^^^^^^^^^

1. createEvidence
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.EvidenceService.createEvidence
   接口定义:ResponseData<String> createEvidence(Credential credential, WeIdPrivateKey weIdPrivateKey)
   接口详细描述: 生成凭证存证信息并上链，有判断要求数据有效，相关非空验证等
   注意：本接口并不进行凭证的有效性验证，也就是说，上链的凭证源有可能无效。
   调用方有义务事先调用CredentialService.verifyCredential()进行判断以避免脏数据。
   传入的私钥将会成为链上存证的签名方。此签名方和凭证的Issuer可以不是同一方。

**接口入参**\ : 

com.webank.weid.protocol.base.Credential

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - context
     - String
     - Y
     - 版本
     - 默认为v1
   * - id
     - String
     - Y
     - 证书编号
     - 
   * - cptId
     - Integer
     - Y
     - cptId
     - 
   * - issuer
     - String
     - Y
     - WeIdentity DID
     - 
   * - issuranceDate
     - Long
     - Y
     - 创建日期
     - 
   * - expirationDate
     - Long
     - Y
     - 到期日期
     - 
   * - signature
     - String
     - Y
     - 签名数据
     - 
   * - claimData
     - String
     - Y
     - Claim数据
     - 

com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - privateKey
     - String
     - 数字私钥
     - 使用十进制数字表示

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
     - 创建的凭证合约地址
     - 为空则表示失败

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CPT_JSON_SCHEMA_INVALID
     - 100301
     - Json Schema非法
   * - CREDENTIAL_ERROR
     - 100400
     - Credential标准错误
   * - CREDENTIAL_EXPIRED
     - 100402
     - 过期
   * - CREDENTIAL_ISSUER_MISMATCH
     - 100403
     - issuer与签名不匹配
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名破坏
   * - CREDENTIAL_REVOKED
     - 100406
     - 已被撤销
   * - CREDENTIAL_ISSUER_NOT_EXISTS
     - 100407
     - WeIdentity DID不能为空
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
   * - CREDENTIAL_EXPIRE_DATE_ILLEGAL
     - 100409
     - 到期日期格式非法
   * - CREDENTIAL_CLAIM_NOT_EXISTS
     - 100410
     - Claim数据不能为空
   * - CREDENTIAL_CLAIM_DATA_ILLEGAL
     - 100411
     - Claim数据无效
   * - CREDENTIAL_ID_NOT_EXISTS
     - 100412
     - ID为空
   * - CREDENTIAL_CONTEXT_NOT_EXISTS
     - 100413
     - context为空
   * - CREDENTIAL_CPT_NOT_EXISTS
     - 100416
     - cpt不存在
   * - CREDENTIAL_WEID_DOCUMENT_ILLEGAL
     - 100417
     - WeIdentity Document为空
   * - CREDENTIAL_EVIDENCE_BASE_ERROR
     - 100500
     - Evidence标准错误
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java
   @Autowired
   private CredentialService credentialService;
   private EvidenceService evidenceService;
   String schema1Data =  "{\"/\":{\"device\":\"/dev/sda1\",\"fstype\":\"btrfs\",\"options\":[\"ssd\"]},\"swap\":{\"device\":\"/dev/sda2\",\"fstype\":\"swap\"},\"/tmp\":{\"device\":\"tmpfs\",\"fstype\":\"tmpfs\",\"options\":[\"size=64M\"]},\"/var/lib/mysql\":{\"device\":\"/dev/data/mysql\",\"fstype\":\"btrfs\"}}";
   CreateCredentialArgs args = new CreateCredentialArgs();
   args.setClaimData(RequestUtil.schema1Data);
   args.setCptId(155);
   args.setExpirationDate(21313312312312312L);
   args.setIssuer("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey(new BigInteger(1231232142).toString());
   args.setWeIdPrivateKey(weIdPrivateKey);
   ResponseData<Credential> credResponse = credentialService.createCredential(args);
   ResponseData<String> response = evidenceService.createEvidence(credResponse.getResult(), weIdPrivateKey);
   return response;


.. code-block:: text

   返回结果如：
   result: 0x425c613348946c3a84861c56808710ea4ba5c961
   errorCode: 0
   errorMessage: success

**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant EvidenceService
   participant 区块链节点
   调用者->>EvidenceService: 调用CreateEvidence()
   EvidenceService->>EvidenceService: 入参非空、格式及合法性检查
   opt 入参校验失败
   EvidenceService-->>调用者: 报错，提示参数不合法并退出
   end
   EvidenceService->>EvidenceService: 生成凭证Hash
   EvidenceService->>EvidenceService: 基于凭证Hash生成签名值
   EvidenceService->>区块链节点: 调用智能合约，创建并上传凭证存证
   区块链节点-->>EvidenceService: 返回创建结果
   opt 创建失败
   EvidenceService-->>调用者: 报错并退出
   end
   EvidenceService-->>调用者: 返回成功

----

2. getEvidence
~~~~~~~~~~~~~~~~~~~


**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.EvidenceService.getEvidence
   接口定义:ResponseData<Evidence> getEvidence(String evidenceAddress)
   接口详细描述: 根据传入的凭证存证地址，在链上查找凭证存证信息。


**接口入参**\ :   String

**接口返回**\ :   com.webank.weid.protocol.base.EvidenceInfo;

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - credentialHash
     - String
     - Y
     - 凭证Hash值
     - 是一个66个字节的字符串，以0x开头
   * - signers
     - List<String>
     - Y
     - 凭证签发者
     - 链上允许存在多个凭证签发者
   * - signatures
     - List<String>
     - Y
     - 签发者生成签名
     - 和每个签发者一一按序对应的签名值

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CREDENTIAL_EVIDENCE_NOT_EXISTS_ON_CHAIN
     - 100401
     - Credential入参为空
   * - CREDENTIAL_EVIDENCE_BASE_ERROR
     - 100500
     - Evidence标准错误
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java
   @Autowired
   private EvidenceService evidenceService;
   ResponseData<Evidence> response = evidenceService.getEvidence("0x425c613348946c3a84861c56808710ea4ba5c961");
   return response;


.. code-block:: text

   返回结果如：
   result: (com.webank.weid.protocol.base.EvidenceInfo)
      credentialHash: c8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92
      signers: 0x0106595955ce4713fd169bfa68e599eb99ca2e9f
      signature: HO+/ve+/vXfvv70hQO+/vQwMVO+/vSot77+977+9UGEhLO+/vW4277+977+977+9HO+/ve+/vSTvv70777+9f14=
   errorCode: 0
   errorMessage: success

**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant EvidenceService
   participant 区块链节点
   调用者->>EvidenceService: 调用GetEvidence()
   EvidenceService->>EvidenceService: 入参非空、格式及合法性检查
   opt 入参校验失败
   EvidenceService-->>调用者: 报错，提示参数不合法并退出
   end
   EvidenceService->>区块链节点: 调用智能合约，查询凭证存证内容
   区块链节点-->>EvidenceService: 返回查询结果
   opt 查询出错
   EvidenceService-->>调用者: 报错并退出
   end
   EvidenceService-->>调用者: 返回成功

----

3. verify()
~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.EvidenceService.verify
   接口定义:ResponseData<Boolean> verifyEvidence(Credential credential, String evidenceAddress)
   接口详细描述: 根据传入的凭证和链上凭证对比，验证其是否遭到篡改。

**接口入参**\ : com.webank.weid.protocol.base.Credential

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - context
     - String
     - Y
     - 版本
     - 默认为v1
   * - id
     - String
     - Y
     - 证书编号
     - 
   * - cptId
     - Integer
     - Y
     - cptId
     - 
   * - issuer
     - String
     - Y
     - WeIdentity DID
     - 
   * - issuranceDate
     - Long
     - Y
     - 创建日期
     - 
   * - expirationDate
     - Long
     - Y
     - 到期日期
     - 
   * - signature
     - String
     - Y
     - 签名数据
     - 
   * - claimData
     - String
     - Y
     - Claim数据
     - 

String：以地址形式存在的String，会进行入参检查

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
     - 是否set成功
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
   * - CPT_JSON_SCHEMA_INVALID
     - 100301
     - Json Schema非法
   * - CREDENTIAL_ERROR
     - 100400
     - Credential标准错误
   * - CREDENTIAL_EXPIRED
     - 100402
     - 过期
   * - CREDENTIAL_ISSUER_MISMATCH
     - 100403
     - issuer与签名不匹配
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名破坏
   * - CREDENTIAL_REVOKED
     - 100406
     - 已被撤销
   * - CREDENTIAL_ISSUER_NOT_EXISTS
     - 100407
     - WeIdentity DID不能为空
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
   * - CREDENTIAL_EXPIRE_DATE_ILLEGAL
     - 100409
     - 到期日期格式非法
   * - CREDENTIAL_CLAIM_NOT_EXISTS
     - 100410
     - Claim数据不能为空
   * - CREDENTIAL_CLAIM_DATA_ILLEGAL
     - 100411
     - Claim数据无效
   * - CREDENTIAL_ID_NOT_EXISTS
     - 100412
     - ID为空
   * - CREDENTIAL_CONTEXT_NOT_EXISTS
     - 100413
     - context为空
   * - CREDENTIAL_CPT_NOT_EXISTS
     - 100416
     - cpt不存在
   * - CREDENTIAL_WEID_DOCUMENT_ILLEGAL
     - 100417
     - WeIdentity Document为空
   * - CREDENTIAL_EVIDENCE_BASE_ERROR
     - 100500
     - Evidence标准错误
   * - CREDENTIAL_EVIDENCE_HASH_MISMATCH
     - 100501
     - Evidence Hash不匹配
   * - CREDENTIAL_EVIDENCE_ID_MISMATCH
     - 100502
     - Evidence ID不匹配
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空
   * - CREDENTIAL_EVIDENCE_CONTRACT_FAILURE_ALREADY_EXISTS
     - 500401
     - Evidence ID已存在
   * - CREDENTIAL_EVIDENCE_CONTRACT_FAILURE_NO_PERMISSION
     - 500402
     - Evidence操作无权限


**调用示例**

.. code-block:: java
   @Autowired
   private CredentialService credentialService;
   private EvidenceService evidenceService;
   String schema1Data =  "{\"/\":{\"device\":\"/dev/sda1\",\"fstype\":\"btrfs\",\"options\":[\"ssd\"]},\"swap\":{\"device\":\"/dev/sda2\",\"fstype\":\"swap\"},\"/tmp\":{\"device\":\"tmpfs\",\"fstype\":\"tmpfs\",\"options\":[\"size=64M\"]},\"/var/lib/mysql\":{\"device\":\"/dev/data/mysql\",\"fstype\":\"btrfs\"}}";
   CreateCredentialArgs args = new CreateCredentialArgs();
   args.setClaimData(RequestUtil.schema1Data);
   args.setCptId(155);
   args.setExpirationDate(21313312312312312L);
   args.setIssuer("did:weid:1:0x0106595955ce4713fd169bfa68e599eb99ca2e9f");
   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey(new BigInteger(1231232142).toString());
   args.setWeIdPrivateKey(weIdPrivateKey);
   ResponseData<Credential> credResponse = credentialService.createCredential(args);
   ResponseData<String> response = evidenceService.createEvidence(credResponse.getCredential(), weIdPrivateKey);
   return evidenceService.verify(credResponse.getResult(), response.getResult());
   
.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success


**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant EvidenceService
   participant WeIdService
   participant 区块链节点
   调用者->>EvidenceService: 调用VerifyEvidence()
   EvidenceService->>EvidenceService: 入参非空、格式及合法性检查
   opt 入参校验失败
   EvidenceService-->>调用者: 报错，提示参数不合法并退出
   end
   EvidenceService->>EvidenceService: 调用GetEvidence()查询凭证内容
   EvidenceService->>区块链节点: 调用智能合约，查询凭证存证内容
   区块链节点-->>EvidenceService: 返回查询结果
   opt 查询出错
   EvidenceService-->>调用者: 返回验证失败，报错并退出
   end
   EvidenceService->>EvidenceService: 生成凭证Hash，与链上凭证Hash对比是否一致
   opt Hash不一致
   EvidenceService-->>调用者: 返回验证失败，报错并退出
   end
   EvidenceService->>WeIdService: 根据存证中签名方信息，调用GetWeIdDocument()查询WeID公钥
   WeIdService->>区块链节点: 调用智能合约，查询WeID公钥
   区块链节点-->>WeIdService: 返回查询结果
   EvidenceService->>EvidenceService: 验证存证中签名是否为与凭证Hash一致
   opt 验签失败
   EvidenceService-->>调用者: 返回验证失败，报错并退出
   end
   EvidenceService-->>调用者: 返回验证成功
