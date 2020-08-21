.. role:: raw-html-m2r(raw)
   :format: html

.. _weidentity-java-sdk-doc:

WeIdentity JAVA SDK文档
=======================

总体介绍
--------

WeIdentity Java SDK提供了一整套对WeIdentity进行管理操作的Java库。目前，SDK支持本地密钥管理、数字身份标识（WeIdentity DID）管理、电子凭证（WeIdentity Credential）管理、授权机构（Authority Issuer）管理、CPT（Claim Protocol Type）管理、存证（Evidence）管理、AMOP链上数据通道、凭证传输（Transportation）管理等功能，未来还将支持更丰富的功能和应用。

术语
----

* 请参阅：`术语表 <https://weidentity.readthedocs.io/zh_CN/latest/docs/terminologies.html>`_

部署SDK
-------

* `WeIdentity JAVA SDK 安装部署文档 <https://weidentity.readthedocs.io/zh_CN/latest/docs/weidentity-installation.html>`_

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
#. 生成凭证：通过CredentialPojoService的CreateCredential()，根据CPT模板，生成一份Credential；
#. 查询凭证：调用CredentialPojoService的Verify()，验证此Credential是否合法；
#. 凭证存证上链：调用EvidenceService的CreateEvidence()，将之前生成的Credential生成一份Hash存证上链；
#. 验证链上凭证存证：调用EvidenceService的VerifyEvidence()，和链上对比，验证Credential是否被篡改。

代码结构说明
------------

.. code-block:: text

   ├─ app：测试小工具
   ├─ config：FISCO-BCOS的合约配置
   ├─ constant：系统常量相关
   └─ contract：通过FISCO-BCOS Web3sdk生成的合约Java接口文件
      └─ deploy: 合约部署相关
   ├─ exception: 异常定义
   └─ protocol：接口参数相关定义
      ├─ base: 基础数据类型定义
      ├─ request: 接口入参定义
      └─ response: 接口出参定义
   ├─ rpc：接口定义
   ├─ service：接口相关实现
   ├─ suite：一些配套的工具
   └─ util：工具类实现

基本数据结构
--------

WeIdDocument
^^^^^^^^^^^^^^^^^^^^^^

**属性**

com.webank.weid.protocol.base.WeIdDocument

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - id
     - String
     - WeIdentity DID
     - 
   * - created
     - Long
     - 创建时间
     - 
   * - updated
     - Long
     - 更新时间
     - 
   * - publicKey
     - List\ :raw-html-m2r:`<PublicKeyProperty>`
     - 
     - 列出公钥集合，见下
   * - authentication
     - List\ :raw-html-m2r:`<AuthenticationProperty>`
     - 
     - 认证方集合，见下
   * - service
     - List\ :raw-html-m2r:`<ServiceProperty>`
     - 
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
     - 默认为：Secp256k1
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
     - 默认为：Secp256k1
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

**方法**

1. toJson
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.protocol.base.WeIdDocument.toJson()
   接口定义:String toJson()
   接口描述: 将WeIdDocument转换成json格式的字符串。
   注意：此方法转换出错会抛DATA_TYPE_CASE_ERROR异常 。

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常
   
**调用示例**

.. code-block:: java
   WeIdService weIdService = new WeIdServiceImpl();
   WeIdDocument weIdDocument = weIdService.getWeIdDocument("did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a").getResult();
   String weIdDocumentJson = weIdDocument.toJson();


2. fromJson
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.protocol.base.WeIdDocument.fromJson(String weIdDocumentJson)
   接口定义:WeIdDocument fromJson(String weIdDocumentJson)
   接口描述: 将json格式的WeIdDocument转换成WeIdDocument对象。
   注意：调用fromJson(String weIdDocumentJson)的入参，必须是通过调用toJson()得到的json格式的WeIdDocument字符串，否则会抛异常 。

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常

**调用示例**

.. code-block:: java
   WeIdService weIdService = new WeIdServiceImpl();
   WeIdDocument weIdDocument = weIdService.getWeIdDocument("did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a").getResult();
   String weIdDocumentJson = weIdDocument.toJson();

   WeIdDocument weIdDocumentFromJson = WeIdDocument.fromJson(weIdDocumentJson);


Challenge
^^^^^^^^^^^^^^^^^^^^^^

**属性**

com.webank.weid.protocol.base.Challenge

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - weId
     - String
     - N
     - WeIdentity DID
     - policy提供给指定的WeIdentity DID
   * - version
     - Integer
     - Y
     - 版本
     -
   * - nonce
     - String
     - Y
     - 随机字符串
     -

**方法**

1. toJson
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.protocol.base.Challenge.toJson()
   接口定义:String toJson()
   接口描述: 将Challenge转换成json格式的字符串。
   注意：此方法转换出错会抛DATA_TYPE_CASE_ERROR异常 。

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常

**调用示例**

.. code-block:: java
   Challenge challenge = Challenge.create("did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a", "1234");
   String challengeJson = challenge.toJson();


2. fromJson
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.protocol.base.Challenge.fromJson(String challengeJson)
   接口定义:Challenge fromJson(String challengeJson)
   接口描述: 将json格式的Challenge转换成Challenge对象。
   注意：调用fromJson(String challengeJson)的入参，必须是通过调用toJson()得到的json格式的Challenge字符串，否则会抛DATA_TYPE_CASE_ERROR异常 。

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常

**调用示例**

.. code-block:: java
   Challenge challenge = Challenge.create("did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a", "1234");
   String challengeJson = challenge.toJson();

   Challenge challengeFromJson = Challenge.fromJson(challengeJson);


CredentialPojo
^^^^^^^^^^^^^^^^^^^^^^

**属性**

com.webank.weid.protocol.base.CredentialPojo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - context
     - String
     -
     -
   * - type
     - List<String>
     -
     -
   * - id
     - String
     - 证书ID
     -
   * - cptId
     - Integer
     - cptId
     -
   * - issuer
     - String
     - issuer 的 WeIdentity DID
     -
   * - issuanceDate
     - Long
     - 创建日期
     -
   * - expirationDate
     - Long
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - 签名数据结构体
     -

**方法**

1. toJson
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.protocol.base.CredentialPojo.toJson()
   接口定义:String toJson()
   接口描述: 将CredentialPojo转换成json格式的字符串。
   注意：此方法转换出错会抛DATA_TYPE_CASE_ERROR异常 。

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常

**调用示例**

.. code-block:: java
   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
   CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs = new CreateCredentialPojoArgs<Map<String, Object>>();
   createCredentialPojoArgs.setCptId(1017);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

   ResponseData<CredentialPojo> credentialResult =
                credentialPojoService.createCredential(createCredentialPojoArgs);
   Map<String, Object> claim = new HashMap<String, Object>();
   claim.put("name", "zhangsan");
   claim.put("gender", "F");
   claim.put("age", 22);
   createCredentialPojoArgs.setClaim(claim);

   ResponseData<CredentialPojo> response = credentialPojoService.createCredential(createCredentialPojoArgs);

   String credentialPojoJson = response.getResult().toJson();


2. fromJson
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.protocol.base.CredentialPojo.fromJson(String credentialPojoJson)
   接口定义:CredentialPojo fromJson(String credentialPojoJson)
   接口描述: 将json格式的CredentialPojo转换成CredentialPojo对象。
   注意：调用fromJson(String credentialPojoJson)的入参，必须是通过调用toJson()得到的json格式的CredentialPojo字符串，否则会抛异常 。

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常

**调用示例**

.. code-block:: java
   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
   CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs = new CreateCredentialPojoArgs<Map<String, Object>>();
   createCredentialPojoArgs.setCptId(1017);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

   ResponseData<CredentialPojo> credentialResult =
                credentialPojoService.createCredential(createCredentialPojoArgs);
   Map<String, Object> claim = new HashMap<String, Object>();
   claim.put("name", "zhangsan");
   claim.put("gender", "F");
   claim.put("age", 22);
   createCredentialPojoArgs.setClaim(claim);

   ResponseData<CredentialPojo> response = credentialPojoService.createCredential(createCredentialPojoArgs);

   String credentialPojoJson = response.getResult().toJson();

   CredentialPojo credentialPojoFromJson = CredentialPojo.fromJson(credentialPojoJson);


PresentationPolicyE
^^^^^^^^^^^^^^^^^^^^^^

**属性**

com.webank.weid.protocol.base.PresentationPolicyE

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - id
     - Integer
     - Y
     - polcyId
     - 策略编号
   * - orgId
     - String
     - Y
     - 机构编号
     -
   * - version
     - Integer
     - Y
     - 版本
     -
   * - policyPublisherWeId
     - String
     - Y
     - WeIdentity DID
     - 创建policy机构的WeIdentity DID
   * - policy
     - Map<Integer, ClaimPolicy>
     - Y
     - 策略配置
     - key: CPTID, value: 披露策略对象
   * - extra
     - Map<String, String>
     - N
     - 扩展字段
     -

**方法**

1. toJson
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.protocol.base.PresentationPolicyE.toJson()
   接口定义:String toJson()
   接口描述: 将PresentationPolicyE转换成json格式的字符串。
   注意：此方法转换出错会抛DATA_TYPE_CASE_ERROR异常 。

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常

**调用示例**

.. code-block:: java
   PresentationPolicyE presentationPolicyE = PresentationPolicyE.create("policy.json");

   String presentationPolicyEJson = presentationPolicyE.toJson();


2. fromJson
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.protocol.base.PresentationPolicyE.fromJson(String presentationPolicyEJson)
   接口定义:PresentationPolicyE fromJson(String presentationPolicyEJson)
   接口描述: 将json格式的PresentationPolicyE转换成PresentationPolicyE对象。
   注意：调用fromJson(String presentationPolicyEJson)的入参，必须是通过调用toJson()得到的json格式的PresentationPolicyE字符串，否则会抛异常 。

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常

**调用示例**

.. code-block:: java
   PresentationPolicyE presentationPolicyE = PresentationPolicyE.create("policy.json");

   String presentationPolicyEJson = presentationPolicyE.toJson();

   PresentationPolicyE presentationPolicyEFromJson = PresentationPolicyE.fromJson(presentationPolicyEJson);


PresentationE
^^^^^^^^^^^^^^^^^^^^^^

**属性**

com.webank.weid.protocol.base.PresentationE

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - context
     - List<String>
     - Y
     - 上下文
     -
   * - type
     - List<String>
     - Y
     - Presentation Type
     -
   * - credentialList
     - List<CredentialPojo>
     - Y
     - 凭证列表
     -
   * - proof
     - Map<String, Object>
     - Y
     - Presentation的签名信息
     -

**方法**

1. toJson
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.protocol.base.PresentationE.toJson()
   接口定义:String toJson()
   接口描述: 将PresentationE转换成json格式的字符串。
   注意：此方法转换出错会抛DATA_TYPE_CASE_ERROR异常 。

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常

**调用示例**

.. code-block:: java
   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
   CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs = new CreateCredentialPojoArgs<Map<String, Object>>();
   createCredentialPojoArgs.setCptId(1101);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

   Map<String, Object> claim = new HashMap<String, Object>();
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 22);
   createCredentialPojoArgs.setClaim(claim);

   //创建CredentialPojo
   ResponseData<CredentialPojo> response = credentialPojoService.createCredential(createCredentialPojoArgs);

   List<CredentialPojo> credentialList = new ArrayList<CredentialPojo>();
   credentialList.add(response.getResult());

   //创建Challenge
   Challenge challenge = Challenge.create("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7", String.valueOf(System.currentTimeMillis()));

   //创建PresentationPolicyE
   String policyJson = "{\"extra\" : {\"extra1\" : \"\",\"extra2\" : \"\"},\"id\" : 123456,\"version\" : 1,\"orgId\" : \"webank\",\"weId\" : \"did:weid:1000:0x0231765e19955fc65133ec8591d73e9136306cd0\",\"policy\" : {\"1017\" : {\"fieldsToBeDisclosed\" : {\"gender\" : 0,\"name\" : 1,\"age\" : 0}}}}";
   PresentationPolicyE presentationPolicyE = PresentationPolicyE.fromJson(policyJson);

   //创建Presentation
   ResponseData<PresentationE>  presentationERes = credentialPojoService.createPresentation(credentialList, presentationPolicyE, challenge, weIdAuthentication);

   String presentationEJson = presentationERes.getResult().toJson();


2. fromJson
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.protocol.base.PresentationE.fromJson(String presentationEJson)
   接口定义:PresentationE fromJson(String challengeJson)
   接口描述: 将json格式的PresentationE转换成PresentationE对象。
   注意：调用fromJson(String presentationEJson)的入参，必须是通过调用toJson()得到的json格式的PresentationE字符串，否则会抛异常 。

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - DATA_TYPE_CASE_ERROR
     - 160008
     - 数据转换异常

**调用示例**

.. code-block:: java
      CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
   CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs = new CreateCredentialPojoArgs<Map<String, Object>>();
   createCredentialPojoArgs.setCptId(1101);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

   Map<String, Object> claim = new HashMap<String, Object>();
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 22);
   createCredentialPojoArgs.setClaim(claim);

   //创建CredentialPojo
   ResponseData<CredentialPojo> response = credentialPojoService.createCredential(createCredentialPojoArgs);

   List<CredentialPojo> credentialList = new ArrayList<CredentialPojo>();
   credentialList.add(response.getResult());

   //创建Challenge
   Challenge challenge = Challenge.create("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7", String.valueOf(System.currentTimeMillis()));

   //创建PresentationPolicyE
   String policyJson = "{\"extra\" : {\"extra1\" : \"\",\"extra2\" : \"\"},\"id\" : 123456,\"version\" : 1,\"orgId\" : \"webank\",\"weId\" : \"did:weid:1000:0x0231765e19955fc65133ec8591d73e9136306cd0\",\"policy\" : {\"1017\" : {\"fieldsToBeDisclosed\" : {\"gender\" : 0,\"name\" : 1,\"age\" : 0}}}}";
   PresentationPolicyE presentationPolicyE = PresentationPolicyE.fromJson(policyJson);

   //创建Presentation
   ResponseData<PresentationE>  presentationERes = credentialPojoService.createPresentation(credentialList, presentationPolicyE, challenge, weIdAuthentication);

   String presentationEJson = presentationERes.getResult().toJson();

   PresentationE presentationE = PresentationE.fromJson(presentationEJson);


3. push
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.protocol.base.PresentationE.push
   接口定义: boolean push(CredentialPojo credentialPojo)
   接口描述: 将非policy里面的Credential添加到Presentation中
  注意：调用 push(CredentialPojo credentialPojo) 添加完所有Credential后需要调用 commit(WeIdAuthentication weIdAuthentication) 进行重新签名，否则验证Presentation时会失败

**调用示例**

.. code-block:: java
   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
   CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs = new CreateCredentialPojoArgs<Map<String, Object>>();
   createCredentialPojoArgs.setCptId(1101);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

   Map<String, Object> claim = new HashMap<String, Object>();
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 22);
   createCredentialPojoArgs.setClaim(claim);

   //创建CredentialPojo
   ResponseData<CredentialPojo> response = credentialPojoService.createCredential(createCredentialPojoArgs);

   List<CredentialPojo> credentialList = new ArrayList<CredentialPojo>();
   credentialList.add(response.getResult());

   //创建Challenge
   Challenge challenge = Challenge.create("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7", String.valueOf(System.currentTimeMillis()));

   //创建PresentationPolicyE
   String policyJson = "{\"extra\" : {\"extra1\" : \"\",\"extra2\" : \"\"},\"id\" : 123456,\"version\" : 1,\"orgId\" : \"webank\",\"weId\" : \"did:weid:1000:0x0231765e19955fc65133ec8591d73e9136306cd0\",\"policy\" : {\"1017\" : {\"fieldsToBeDisclosed\" : {\"gender\" : 0,\"name\" : 1,\"age\" : 0}}}}";
   PresentationPolicyE presentationPolicyE = PresentationPolicyE.fromJson(policyJson);

   //创建Presentation
   ResponseData<PresentationE>  presentationERes = credentialPojoService.createPresentation(credentialList, presentationPolicyE, challenge, weIdAuthentication);

   //将非policy要求的Credential添加到presentation中
   ResponseData<CredentialPojo> responseNew = credentialPojoService.createCredential(createCredentialPojoArgs);
   presentationERes.getResult().push(responseNew.getResult());

4. commit
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.protocol.base.PresentationE.commit
   接口定义: boolean commit(WeIdAuthentication weIdAuthentication)
   接口描述: 添加完Credential对Presentation重新签名处理了

**调用示例**

.. code-block:: java
   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
   CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs = new CreateCredentialPojoArgs<Map<String, Object>>();
   createCredentialPojoArgs.setCptId(1101);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

   Map<String, Object> claim = new HashMap<String, Object>();
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 22);
   createCredentialPojoArgs.setClaim(claim);

   //创建CredentialPojo
   ResponseData<CredentialPojo> response = credentialPojoService.createCredential(createCredentialPojoArgs);

   List<CredentialPojo> credentialList = new ArrayList<CredentialPojo>();
   credentialList.add(response.getResult());

   //创建Challenge
   Challenge challenge = Challenge.create("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7", String.valueOf(System.currentTimeMillis()));

   //创建PresentationPolicyE
   String policyJson = "{\"extra\" : {\"extra1\" : \"\",\"extra2\" : \"\"},\"id\" : 123456,\"version\" : 1,\"orgId\" : \"webank\",\"weId\" : \"did:weid:1000:0x0231765e19955fc65133ec8591d73e9136306cd0\",\"policy\" : {\"1017\" : {\"fieldsToBeDisclosed\" : {\"gender\" : 0,\"name\" : 1,\"age\" : 0}}}}";
   PresentationPolicyE presentationPolicyE = PresentationPolicyE.fromJson(policyJson);

   //创建Presentation
   ResponseData<PresentationE>  presentationERes = credentialPojoService.createPresentation(credentialList, presentationPolicyE, challenge, weIdAuthentication);

   //将非policy要求的Credential添加到presentation中
   ResponseData<CredentialPojo> responseNew = credentialPojoService.createCredential(createCredentialPojoArgs);
   presentationERes.getResult().push(responseNew.getResult());
   presentationERes.getResult().commit(weIdAuthentication)


接口简介
--------

整体上，WeIdentity Java SDK包括五个主要的接口，它们分别是：WeIdService、AuthorityIssuerService、CptService、CredentialService / CredentialPojoService、EvidenceService、AmopService。


* WeIdService

WeIdentity DID相关功能的核心接口。

本接口提供WeIdentity DID的创建、获取信息、设置属性等相关操作。


* AuthorityIssuerService

在WeIdentity的整体架构中，存在着可信的“授权机构”这一角色。一般来说，授权机构特指那些广为人知的、具有一定公信力的、并且有相对频繁签发Credential需求的实体。

本接口提供了对这类授权签发Credential的机构的注册、移除、查询信息等操作。


* CptService

任何凭证的签发，都需要将数据转换成已经注册的CPT (Claim Protocol Type)格式规范，也就是所谓的“标准化格式化数据”。相关机构事先需要注册好CPT，在此之后，签发机构会根据CPT提供符合格式的数据，进而进行凭证的签发。

本接口提供了对CPT的注册、更新、查询等操作。


* CredentialService / CredentialPojoService

凭证签发相关功能的核心接口。

本接口提供凭证的签发和验证操作、Verifiable Presentation的签发和验证操作。


* EvidenceService

凭证存证上链的相关接口。

本接口提供凭证的Hash存证的生成上链、链上查询及校验等操作。


* AmopService

AMOP通讯相关接口。

本接口提供AMOP的请求和注册。


接口列表
--------

WeIdService
^^^^^^^^^^^

1. createWeId
~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.createWeId
   接口定义:ResponseData<CreateWeIdDataResult> createWeId()
   接口描述: 内部创建公私钥，并链上注册WeIdentity DID， 并返回公钥、私钥以及WeIdentity DID。

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
     -
     - 见下
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
     - 格式: did:weid:1000:0x………………….
   * - userWeIdPublicKey
     - WeIdPublicKey
     -
     -
   * - userWeIdPrivateKey
     - WeIdPrivateKey
     -
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
     - 私钥
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
     - 100107
     - 创建密钥对失败
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥和weid不匹配
   * - UNKNOW_ERROR
     - 160003
     - 其他错误


**调用示例**

.. code-block:: java

   WeIdService weIdService = new WeIdServiceImpl();
   ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();


.. code-block:: text

   输出结果如下：
   result:(com.webank.weid.protocol.response.CreateWeIdDataResult)
      weId: did:weid:101:0xf4e5f96de0627960c8b91c1cc126f7b5cdeacbd0
      userWeIdPublicKey:(com.webank.weid.protocol.base.WeIdPublicKey)
      publicKey: 3140516665390655972698269231665028730625296545812754612198268107926656717368563044260511639762256438305037318801307432426840176526241566631412406151716674
      userWeIdPrivateKey:(com.webank.weid.protocol.base.WeIdPrivateKey)
      privateKey: 70694712486452850283637015242845250545254342779640874305734061338958342229003
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 30005
      transactionHash: 0x7e4fcacdd296f10936e53d64c7d6470dd4ffa52e22405c86ed8f72389419821f
      transactionIndex: 0


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
   接口描述: 根据传入的公私钥，链上注册WeIdentity DID，并返回WeIdentity DID。

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
     - Y
     -
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
     - 私钥
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
     - 如：did:weid:1000:0x………………….
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - WEID_PUBLICKEY_INVALID
     - 100102
     - 公钥无效
   * - WEID_PRIVATEKEY_INVALID
     - 100103
     - 私钥格式非法
   * - WEID_ALREADY_EXIST
     - 100105
     - WeIdentity DID已存在
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥不与WeIdentity DID所对应
   * - WEID_PUBLICKEY_AND_PRIVATEKEY_NOT_MATCHED
     - 100108
     - 公私钥不成对
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - UNKNOW_ERROR
     - 160003
     - 其他异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   WeIdService weIdService = new WeIdServiceImpl();

   CreateWeIdArgs createWeIdArgs = new CreateWeIdArgs();
   createWeIdArgs.setPublicKey(
      "2905679808560626772263712571437125497429146398815877180317365034921958007199576809718056336050058032599743534507469742764670961100255274766148096681073592");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("7581560237967740187496354914151086729152742173850631851769274217992481997665");

   createWeIdArgs.setWeIdPrivateKey(weIdPrivateKey);

   ResponseData<String> response = weIdService.createWeId(createWeIdArgs);


.. code-block:: text

   输出结果如下：
   result: did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 30007
      transactionHash: 0x7f9e0fe2bcb0e77bad9aa5c38f8440e71a48dc29406d9ad43e12130afd211c67
      transactionIndex: 0


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


3. delegateCreateWeId
~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.delegateCreateWeId
   接口定义:ResponseData<String> delegateCreateWeId(WeIdPublicKey publicKey,WeIdAuthentication weIdAuthentication)
   接口描述: 根据传入的公钥和代理的私钥，通过代理发交易链上注册WeIdentity DID，并返回WeIdentity DID。

**接口入参**\ :  com.webank.weid.protocol.base.WeIdPublicKey

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
     - 数字公钥，代理会根据这个公钥来创建WeID
     -

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
     - WeIdentity DID
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
     - 说明
     - 备注
   * - privateKey
     - String
     - 私钥
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
     - 如：did:weid:1000:0x………………….
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - WEID_PUBLICKEY_INVALID
     - 100102
     - 公钥无效
   * - WEID_PRIVATEKEY_INVALID
     - 100103
     - 私钥格式非法
   * - WEID_ALREADY_EXIST
     - 100105
     - WeIdentity DID已存在
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - UNKNOW_ERROR
     - 160003
     - 其他异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   WeIdService weIdService = new WeIdServiceImpl();

   WeIdPublicKey weIdPublicKey = new WeIdPublicKey();
   weIdPublicKey.setPublicKey(
      "2905679808560626772263712571437125497429146398815877180317365034921958007199576809718056336050058032599743534507469742764670961100255274766148096681073592");
   String delegateWeId = "did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7";
   String delegatePrivateKey = "60866441986950167911324536025850958917764441489874006048340539971987791929772";
   WeIdAuthentication weIdAuthentication = new WeIdAuthentication(delegateWeId, delegatePrivateKey);
   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");

   ResponseData<String> response = weIdService.delegateCreateWeId(weIdPublicKey, weIdAuthentication);


.. code-block:: text

   输出结果如下：
   result: did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 30007
      transactionHash: 0x7f9e0fe2bcb0e77bad9aa5c38f8440e71a48dc29406d9ad43e12130afd211c67
      transactionIndex: 0


**时序图**

.. mermaid::

   sequenceDiagram
   Note over 调用者:传入自己的WeIdentity DID及用作authentication的私钥
   调用者->>WeIdentity SDK:delegateCreateWeId()
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

4. getWeIdDocumentJson
~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.getWeIdDocumentJson
   接口定义:ResponseData<String> getWeIdDocumentJson(String weId)
   接口描述: 根据WeIdentity DID查询WeIdentity DID Document信息，并以JSON格式返回。

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
   * - UNKNOW_ERROR
     - 160003
     -  其他错误


**调用示例**

.. code-block:: java

   WeIdService weIdService = new WeIdServiceImpl();
   ResponseData<String> response = weIdService.getWeIdDocumentJson("did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a");


.. code-block:: text

   返回结果如下：
   result: {"@context" : "https://github.com/WeBankFinTech/WeIdentity/blob/master/context/v1",
      "id" : "did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a",
      "created" : 1560419409898,
      "updated" : 1560419409898,
      "publicKey" : [ {
         "id" : "did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a#keys-0",
         "type" : "Secp256k1",
         "owner" : "did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a",
         "publicKey" : "2905679808560626772263712571437125497429146398815877180317365034921958007199576809718056336050058032599743534507469742764670961100255274766148096681073592"
      } ],
      "authentication" : [ {
         "type" : "Secp256k1",
         "publicKey" : "did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a#keys-0"
      } ],
      "service" : [ {
         "type" : "drivingCardService",
         "serviceEndpoint" : "https://weidentity.webank.com/endpoint/xxxxx"
      } ]
   }
   errorCode: 0
   errorMessage: success
   transactionInfo:null


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

5. getWeIDDocment
~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.getWeIdDocument
   接口定义:ResponseData<WeIdDocument> getWeIdDocument(String weId)
   接口描述: 根据WeIdentity DID查询出WeIdentity DID Document对象。

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
     -
     - 见下
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


com.webank.weid.protocol.base.WeIdDocument

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - id
     - String
     - WeIdentity DID
     -
   * - created
     - Long
     - 创建时间
     -
   * - updated
     - Long
     - 更新时间
     -
   * - publicKey
     - List\ :raw-html-m2r:`<PublicKeyProperty>`
     -
     - 列出公钥集合，见下
   * - authentication
     - List\ :raw-html-m2r:`<AuthenticationProperty>`
     -
     - 认证方集合，见下
   * - service
     - List\ :raw-html-m2r:`<ServiceProperty>`
     -
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
     - 默认为：Secp256k1
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
     - 默认为：Secp256k1
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
   * - UNKNOW_ERROR
     - 160003
     -  其他错误


**调用示例**

.. code-block:: java

   WeIdService weIdService = new WeIdServiceImpl();
   ResponseData<WeIdDocument> response = weIdService.getWeIdDocument("did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a");


.. code-block:: text

   返回结果如下：
   result:(com.webank.weid.protocol.base.WeIdDocument)
      id: did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a
      created: 1560419409898
      updated: 1560419409898
      publicKey:(java.util.ArrayList)
         [0]:com.webank.weid.protocol.base.PublicKeyProperty
            id: did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a#keys-0
            type: Secp256k18
            owner: did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a
            publicKey: 2905679808560626772263712571437125497429146398815877180317365034921958007199576809718056336050058032599743534507469742764670961100255274766148096681073592
      authentication:(java.util.ArrayList)
         [0]:com.webank.weid.protocol.base.AuthenticationProperty
            type: Secp256k1
            publicKey: did:weid:101:0xd9aeaa982fc21ea9addaf09e4f0c6a23a08d306a#keys-0
      service:(java.util.ArrayList)
         [0]:com.webank.weid.protocol.base.ServiceProperty
            type: drivingCardService
            serviceEndpoint: https://weidentity.webank.com/endpoint/8377464
   errorCode: 0
   errorMessage: success
   transactionInfo:null


----

6. addPublicKey
~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:addPublicKey
   接口定义:ResponseData<Integer> addPublicKey(String weId, PublicKeyArgs publicKeyArgs, WeIdPrivateKey weIdPrivateKey)
   接口描述: 为指定WeIdentity DID添加公钥，可以使用getWeIdDocument方法来读取所有挂在此WeID下的公钥，读取出的顺序和添加顺序一致。同时，公钥的ID通过被调用添加的顺序来决定，从而保证ID永远是增长的，且固定下来就永远不会变。返回值为整形，表示所添加的公钥的ID，若失败，返回-1。

**接口入参**\ :

String

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

com.webank.weid.protocol.request.PublicKeyArgs

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - owner
     - String
     - N
     - 所有者
     - 如不传入，则默认为当前WeIdentity DID
   * - publicKey
     - String
     - Y
     - 数字公钥
     - 使用十进制数字表示


com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - privateKey
     - String
     - 私钥
     - 使用十进制数字表示


**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<Integer>;

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
     - Integer
     - 所添加的公钥的ID，若为-1，则表示失败
     -
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
   * - UNKNOW_ERROR
     - 160003
     -  其他错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   WeIdService weIdService = new WeIdServiceImpl();

   PublicKeyArgs setPublicKeyArgs = new SetPublicKeyArgs();
   setPublicKeyArgs.setPublicKey(
      "13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   ResponseData<Boolean> response = weIdService.addPublicKey("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7", setPublicKeyArgs, weIdPrivateKey);


.. code-block:: text

   返回结果如下：
   result: 1
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 30011
      transactionHash: 0xda4a1c64a3991170975475fdd6604bb2897512948ea491d3c88f24c4c3fd0028
      transactionIndex: 0

**时序图**

.. mermaid::

   sequenceDiagram
   Note over 调用者:传入自己的WeIdentity DID及用作authentication的公私钥
   调用者->>WeIdentity SDK : 调用addPublicKey来添加公钥。
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

7. delegateAddPublicKey
~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:delegateAddPublicKey
   接口定义:ResponseData<Integer> delegateAddPublicKey(String weId, PublicKeyArgs publicKeyArgs, WeIdPrivateKey delegateAuth)
   接口描述: 和addPublicKey调用方式和返回值一致，但由**代理方**来给WeIdentity DID添加公钥。仅支持联盟链管理员或委员会成员作为代理方调用。

**接口入参**\ :

String

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

com.webank.weid.protocol.request.PublicKeyArgs

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
     - 如：did:weid:1000:1:0x....
   * - owner
     - String
     - N
     - 所有者
     - 默认为当前WeIdentity DID
   * - publicKey
     - String
     - Y
     - 数字公钥
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
     - 私钥
     - 使用十进制数字表示


**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<Integer>;

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
     - Integer
     - 所添加成功的公钥ID，若失败，返回-1
     -
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
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - UNKNOW_ERROR
     - 160003
     -  其他错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   WeIdService weIdService = new WeIdServiceImpl();

   PublicKeyArgs publicKeyArgs = new PublicKeyArgs();
   publicKeyArgs.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   publicKeyArgs.setPublicKey(
      "13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342");
   String delegatePrivateKey = "60866441986950167911324536025850958917764441489874006048340539971987791929772";

   ResponseData<Boolean> response = weIdService.delegateSetPublicKey("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7", publicKeyArgs, new WeIdPrivateKey(delegatePrivateKey));


.. code-block:: text

   返回结果如下：
   result: 1
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 30011
      transactionHash: 0xda4a1c64a3991170975475fdd6604bb2897512948ea491d3c88f24c4c3fd0028
      transactionIndex: 0



**时序图**

.. mermaid::

   sequenceDiagram
   Note over 调用者:传入自己的WeIdentity DID及用作authentication的公私钥
   调用者->>WeIdentity SDK : 调用delegateSetPublicKey来添加公钥。
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

8. setService
~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.setService
   接口定义:ResponseData<Boolean> setService(String weId, ServiceArgs setServiceArgs, WeIdPrivateKey weIdPrivateKey)
   接口描述: 根据WeIdentity DID添加Service信息。

**接口入参**\ :

String

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

com.webank.weid.protocol.request.ServiceArgs

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - type
     - String
     - Y
     - 类型
     - 如：drivingCardService
   * - serviceEndpoint
     - String
     - Y
     - 服务端点
     - 如："https://weidentity.webank.com/endpoint/8377464"


com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - privateKey
     - String
     - 私钥
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
   * - WEID_SERVICE_TYPE_OVERLIMIT
     - 100110
     - type字段超长
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - UNKNOW_ERROR
     - 160003
     -  其他错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   WeIdService weIdService = new WeIdServiceImpl();

   ServiceArgs setServiceArgs = new SetServiceArgs();
   setServiceArgs.setType("drivingCardService");
   setServiceArgs.setServiceEndpoint("https://weidentity.webank.com/endpoint/8377464");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   ResponseData<Boolean> response = weIdService.setService("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7", setServiceArgs, weIdPrivateKey);


.. code-block:: text

   返回结果如下：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 30012
      transactionHash: 0xf4992c4d190a9338f13119125861aaa3fa86622de1ab6862d06c05c6e6d1d9be
      transactionIndex: 0


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


9. delegateSetService
~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.delegateSetService
   接口定义:ResponseData<Boolean> setService(ServiceArgs serviceArgs，WeIdAuthentication delegateAuth)
   接口描述: 由**代理方**来给WeIdentity DID添加Service信息。仅支持联盟链管理员或委员会成员作为代理方调用。

**接口入参**\ :

String

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

com.webank.weid.protocol.request.ServiceArgs

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
     - 如：did:weid:101:0x.....
   * - type
     - String
     - Y
     - 类型
     - 如：drivingCardService
   * - serviceEndpoint
     - String
     - Y
     - 服务端点
     - 如："https://weidentity.webank.com/endpoint/8377464"

com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - privateKey
     - String
     - 私钥
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
   * - WEID_SERVICE_TYPE_OVERLIMIT
     - 100110
     - type字段超长
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - UNKNOW_ERROR
     - 160003
     -  其他错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   WeIdService weIdService = new WeIdServiceImpl();

   ServiceArgs serviceArgs = new ServiceArgs();
   serviceArgs.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   serviceArgs.setType("drivingCardService");
   serviceArgs.setServiceEndpoint("https://weidentity.webank.com/endpoint/8377464");

   String delegatePrivateKey = "60866441986950167911324536025850958917764441489874006048340539971987791929772";

   ResponseData<Boolean> response = weIdService.delegateSetService("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7", serviceArgs, new WeIdPrivateKey(delegatePrivateKey));


.. code-block:: text

   返回结果如下：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 30012
      transactionHash: 0xf4992c4d190a9338f13119125861aaa3fa86622de1ab6862d06c05c6e6d1d9be
      transactionIndex: 0


**时序图**

.. mermaid::

   sequenceDiagram
   Note over 调用者:传入自己的WeIdentity DID及要用作<br>authentication的私钥，<br>以及service endpoint
   调用者->>WeIdentity SDK : 调用delegateSetService来添加认证。
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

10. setAuthentication
~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.setAuthentication
   接口定义:ResponseData<Boolean> setAuthentication(String weId, SetAuthenticationArgs setAuthenticationArgs)
   接口描述: 由**代理方**来给WeIdentity DID添加Authentication信息。仅支持联盟链管理员或委员会成员作为代理方调用。

**接口入参**\ :

String

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

com.webank.weid.protocol.request.AuthenticationArgs

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - owner
     - String
     - N
     - 所有者
     - 默认为当前WeIdentity DID
   * - publicKey
     - String
     - Y
     - 数字公钥
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
     - 私钥
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
   * - UNKNOW_ERROR
     - 160003
     -  其他错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   WeIdService weIdService = new WeIdServiceImpl();

   AuthenticationArgs setAuthenticationArgs = new SetAuthenticationArgs();
   setAuthenticationArgs.setPublicKey(
      "13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   ResponseData<Boolean> response = weIdService.setAuthentication("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7", setAuthenticationArgs, weIdPrivateKey);


.. code-block:: text

   返回结果如下：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 30013
      transactionHash: 0xfbf8338e7df2af0612eca5107c0d2ed75dfd7a795988687f49c010112678f847
      transactionIndex: 0


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


11. delegateSetAuthentication
~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.delegateSetAuthentication
   接口定义:ResponseData<Boolean> delegateSetAuthentication(String weId, AuthenticationArgs authenticationArgs，WeIdAuthentication delegateAuth)
   接口描述: 由**代理方**来给WeIdentity DID添加Authentication信息。仅支持联盟链管理员或委员会成员作为代理方调用。

**接口入参**\ :

String

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

com.webank.weid.protocol.request.AuthenticationArgs

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - owner
     - String
     - N
     - 所有者
     - 默认为当前WeIdentity DID
   * - publicKey
     - String
     - Y
     - 数字公钥
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
     - 私钥
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
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - UNKNOW_ERROR
     - 160003
     -  其他错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   WeIdService weIdService = new WeIdServiceImpl();

   AuthenticationArgs authenticationArgs = new AuthenticationArgs();
   authenticationArgs.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   authenticationArgs.setPublicKey(
      "13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342");

   String delegatePrivateKey = "60866441986950167911324536025850958917764441489874006048340539971987791929772";

   ResponseData<Boolean> response = weIdService.delegateSetAuthentication("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7", authenticationArgs, new WeIdPrivateKey(delegatePrivateKey));


.. code-block:: text

   返回结果如下：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 30013
      transactionHash: 0xfbf8338e7df2af0612eca5107c0d2ed75dfd7a795988687f49c010112678f847
      transactionIndex: 0


**时序图**

.. mermaid::

   sequenceDiagram
   Note over 调用者:传入自己的WeIdentity DID及用作authentication的公私钥
   调用者->>WeIdentity SDK : 调用delegateSetAuthentication来添加认证。
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

12. isWeIdExist
~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.isWeIdExist
   接口定义:ResponseData<Boolean> isWeIdExist(String weId)
   接口描述: 根据WeIdentity DID判断链上是否存在。


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
     - WeIdentity DID格式字符串
     - 如：did:weid:101:0x....


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
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - UNKNOW_ERROR
     - 160003
     - 未知异常


**调用示例**

.. code-block:: java

   WeIdService weIdService = new WeIdServiceImpl();
   ResponseData<Boolean> response = weIdService.isWeIdExist("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");


.. code-block:: text

   返回结果如下：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:null


----

**时序图**

.. mermaid::

   sequenceDiagram
         调用者->>WeIdentity SDK : 传入WeIdentity DID，调用isWeIdExist来判断是否存在。
   opt 参数校验
   Note over WeIdentity SDK:非空检查和有效性检查
   WeIdentity SDK->>WeIdentity SDK:报错，提示参数不合法并退出
   end
   WeIdentity SDK->>区块链节点: 传入WeIdentity DID链上存在性校验
         区块链节点-->>WeIdentity SDK:返回
   WeIdentity SDK-->>调用者:返回调用结果


----


AuthorityIssuerService
^^^^^^^^^^^^^^^^^^^^^^

1. registerAuthorityIssuer
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.AuthorityIssuerService.registerAuthorityIssuer
   接口定义:ResponseData<Boolean> registerAuthorityIssuer(RegisterAuthorityIssuerArgs args)
   接口描述: 注册成为权威机构。
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
     -
     - AuthorityIssuer信息，见下
   * - weIdPrivateKey
     - WeIdPrivateKey
     - Y
     -
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
     - 机构名称必须小于32个字节，非空，且仅包含ASCII码可打印字符（ASCII值位于32~126）
   * - created
     - Long
     - N
     - 创建日期
     - 注册时不需要传入，SDK内置默认为当前时间
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


**调用示例**

.. code-block:: java

   AuthorityIssuerService authorityIssuerService = new AuthorityIssuerServiceImpl();

   AuthorityIssuer authorityIssuer = new AuthorityIssuer();
   authorityIssuer.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   authorityIssuer.setName("webank1");
   authorityIssuer.setAccValue("0");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("36162289879206412028682370838615850457668262092955617990245744195910144330785");

   RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs = new RegisterAuthorityIssuerArgs();
   registerAuthorityIssuerArgs.setAuthorityIssuer(authorityIssuer);
   registerAuthorityIssuerArgs.setWeIdPrivateKey(weIdPrivateKey);

   ResponseData<Boolean> response = authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);


.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 29963
      transactionHash: 0x97a5cc2f4f7888e788a22e7c9bef1a293614bceec4721810511d07fc5b748f33
      transactionIndex: 0


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
   接口定义:ResponseData<Boolean> removeAuthorityIssuer(RemoveAuthorityIssuerArgs args)
   接口描述: 注销权威机构。
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
   * - AUTHORITY_ISSUER_OPCODE_MISMATCH
     - 100205
     - 操作码不匹配
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

   AuthorityIssuerService authorityIssuerService = new AuthorityIssuerServiceImpl();

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("36162289879206412028682370838615850457668262092955617990245744195910144330785");

   RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs = new RemoveAuthorityIssuerArgs();
   removeAuthorityIssuerArgs.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   removeAuthorityIssuerArgs.setWeIdPrivateKey(weIdPrivateKey);

   ResponseData<Boolean> response = authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);


.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 29951
      transactionHash: 0xb9a2ef2a6045e0804b711e0ce39f7187de08e329160d5a5a00a1815e067f15e5
      transactionIndex: 0


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
   接口描述: 根据WeIdentity DID判断是否为权威机构。

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
     - 实体不存在


**调用示例**

.. code-block:: java

   AuthorityIssuerService authorityIssuerService = new AuthorityIssuerServiceImpl();
   String weId = "did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7";
   ResponseData<Boolean> response = authorityIssuerService.isAuthorityIssuer(weId);


.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:null


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
   接口描述: 根据WeIdentity DID查询权威机构信息。

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
     -
     - 授权机构信息，见下
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
   * - created
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
     - 实体不存在


**调用示例**

.. code-block:: java

   AuthorityIssuerService authorityIssuerService = new AuthorityIssuerServiceImpl();
   String weId = "did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7";
   ResponseData<AuthorityIssuer> response = authorityIssuerService.queryAuthorityIssuerInfo(weId);


.. code-block:: text

   返回数据如：
   result:(com.webank.weid.protocol.base.AuthorityIssuer)
      weId: did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7
      name: webank1
      created: 1560412556901
      accValue:
   errorCode: 0
   errorMessage: success
   transactionInfo:null


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


5. getAllAuthorityIssuerList
~~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.rpc.AuthorityIssuerService.getAllAuthorityIssuerList
   接口定义: ResponseData<List<AuthorityIssuer>> getAllAuthorityIssuerList(Integer index, Integer num)
   接口描述: 查询指定范围内的issuer列表。

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - index
     - Integer
     - Y
     - 检索的开始位置
     -
   * - num
     - Integer
     - Y
     - 检索的数据条数
     - 单次最多可以检索50条

**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<List\<AuthorityIssuer>>;

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
     - List<AuthorityIssuer>
     -
     - 授权机构信息，见下
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
   * - created
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
   * - AUTHORITY_ISSUER_ERROR
     - 100200
     - 授权标准异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   AuthorityIssuerService authorityIssuerService = new AuthorityIssuerServiceImpl();
   ResponseData<List<AuthorityIssuer>> response = authorityIssuerService.getAllAuthorityIssuerList(0, 2);


.. code-block:: text

   返回数据如：
   result: (java.util.ArrayList)
      [0]: com.webank.weid.protocol.base.AuthorityIssuer
         weId: did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7
         name: webank1
         created: 1560412556901
         accValue:
      [1]: com.webank.weid.protocol.base.AuthorityIssuer
         weId: did:weid:101:0x48f56f6b8cd77409447014ceb060243b914cb2a9
         name: webank2
         created: 1560632118000
         accValue:
   errorCode: 0
   errorMessage: success
   transactionInfo:null

**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant AuthorityIssuerService
   participant 区块链节点
   调用者->>AuthorityIssuerService: getAllAuthorityIssuerList()
   AuthorityIssuerService->>AuthorityIssuerService: 入参非空、格式及合法性检查
   opt 入参校验失败
   AuthorityIssuerService-->>调用者: 报错，提示参数不合法并退出
   end
   AuthorityIssuerService->>区块链节点: 调用查询详细信息合约
   区块链节点->>区块链节点: 执行合约查询指定数目的授权机构，打包返回结果
   区块链节点-->>AuthorityIssuerService: 返回查询结果
   AuthorityIssuerService-->>调用者: 返回查询结果

----


6. registerIssuerType
~~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.rpc.AuthorityIssuerService.registerIssuerType
   接口定义: ResponseData<Boolean> registerIssuerType(WeIdAuthentication callerAuth, String issuerType)
   接口描述: 指定并注册不同issuer的类型，如学校、政府机构等。
   权限说明：本方法对传入的WeIdAuthentication没有特定权限要求。

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - callerAuth
     - WeIdAuthentication
     - Y
     - weId身份信息
     -
   * - issuerType
     - String
     - Y
     - 机构类型
     -


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
     - WeIdentity DID
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


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<Boolean>;

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
     - 是否注册成功
     -
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - AUTHORITY_ISSUER_ERROR
     - 100200
     - 授权标准异常
   * - SPECIFIC_ISSUER_TYPE_ILLEGAL
     - 100208
     - 机构类型非法
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


**调用示例**

.. code-block:: java

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
     AuthorityIssuerService authorityIssuerService = new AuthorityIssuerServiceImpl();
   ResponseData<List<AuthorityIssuer>> response = authorityIssuerService.registerIssuerType(weIdAuthentication, "College");

.. code-block:: text

   返回数据如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo: (com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 29950
      transactionHash: 0xe3f48648beee61d17de609d32af36ac0bf4d68a9352890b04d53841c4949bd13
      transactionIndex: 0

**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant AuthorityIssuerService
   participant 区块链节点
   调用者->>AuthorityIssuerService: registerIssuerType()
   AuthorityIssuerService->>AuthorityIssuerService: 入参非空、格式及合法性检查
   opt 入参校验失败
   AuthorityIssuerService-->>调用者: 报错，提示参数不合法并退出
   end
   AuthorityIssuerService->>区块链节点: 调用注册授权机构类型合约
   区块链节点->>区块链节点: 执行合约注册授权机构类型
   区块链节点-->>AuthorityIssuerService: 返回执行结果
   AuthorityIssuerService-->>调用者: 返回执行结果


----


7. addIssuerIntoIssuerType
~~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.rpc.AuthorityIssuerService.addIssuerIntoIssuerType
   接口定义: ResponseData<Boolean> addIssuerIntoIssuerType(WeIdAuthentication callerAuth, String issuerType, String targetIssuerWeId)
   接口描述: 向指定的issuerType中添加成员。
   权限说明：方法的调用者至少需要是Authority Issuer才能成功。

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - callerAuth
     - WeIdAuthentication
     - Y
     - weId身份信息
     -
   * - issuerType
     - String
     - Y
     - 机构类型
     -
   * - targetIssuerWeId
     - String
     - Y
     - issuer的WeIdentity DID
     -

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
     - WeIdentity DID
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


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<Boolean>;

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
     - 是否添加成员成功
     -
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - WEID_DOES_NOT_EXIST
     - 100104
     - WeIdentity DID不存在
   * - AUTHORITY_ISSUER_ERROR
     - 100200
     -  授权标准异常
   * - WEID_INVALID
     - 100201
     -  无效的WeIdentity DID
   * - AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL
     - 100202
     -  私钥格式非法
   * - SPECIFIC_ISSUER_TYPE_ILLEGAL
     - 100208
     - 机构类型非法
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


**调用示例**

.. code-block:: java

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
     AuthorityIssuerService authorityIssuerService = new AuthorityIssuerServiceImpl();
   ResponseData<List<AuthorityIssuer>> response = authorityIssuerService.addIssuerIntoIssuerType(weIdAuthentication, "College", "did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

.. code-block:: text

   返回数据如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo: (com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 29950
      transactionHash: 0xe3f48648beee61d17de609d32af36ac0bf4d68a9352890b04d53841c4949bd13
      transactionIndex: 0

**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant AuthorityIssuerService
   participant 区块链节点
   调用者->>AuthorityIssuerService: addIssuerIntoIssuerType()
   AuthorityIssuerService->>AuthorityIssuerService: 入参非空、格式及合法性检查
   opt 入参校验失败
   AuthorityIssuerService-->>调用者: 报错，提示参数不合法并退出
   end
   AuthorityIssuerService->>区块链节点: 调用添加授权机构合约
   区块链节点->>区块链节点: 执行合约添加授权机构
   区块链节点-->>AuthorityIssuerService: 返回执行结果
   AuthorityIssuerService-->>调用者: 返回执行结果

----


8. removeIssuerFromIssuerType
~~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.rpc.AuthorityIssuerService.removeIssuerFromIssuerType
   接口定义: ResponseData<Boolean> removeIssuerFromIssuerType(WeIdAuthentication callerAuth, String issuerType, String targetIssuerWeId)
   接口描述: 移除指定issuerType里面的WeId成员。

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - callerAuth
     - WeIdAuthentication
     - Y
     - weId身份信息
     -
   * - issuerType
     - String
     - Y
     - 机构类型
     -
   * - targetIssuerWeId
     - String
     - Y
     - issuer的WeIdentity DID
     -

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
     - WeIdentity DID
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


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<Boolean>;

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
     - 是否移除成功
     -
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - WEID_DOES_NOT_EXIST
     - 100104
     - WeIdentity DID不存在
   * - AUTHORITY_ISSUER_ERROR
     - 100200
     -  授权标准异常
   * - WEID_INVALID
     - 100201
     -  无效的WeIdentity DID
   * - AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL
     - 100202
     -  私钥格式非法
   * - SPECIFIC_ISSUER_TYPE_ILLEGAL
     - 100208
     - 机构类型非法
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


**调用示例**

.. code-block:: java

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
     AuthorityIssuerService authorityIssuerService = new AuthorityIssuerServiceImpl();
   ResponseData<List<AuthorityIssuer>> response = authorityIssuerService.removeIssuerFromIssuerType(weIdAuthentication, "College", "did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

.. code-block:: text

   返回数据如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo: (com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 29950
      transactionHash: 0xe3f48648beee61d17de609d32af36ac0bf4d68a9352890b04d53841c4949bd13
      transactionIndex: 0

**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant AuthorityIssuerService
   participant 区块链节点
   调用者->>AuthorityIssuerService: removeIssuerIntoIssuerType()
   AuthorityIssuerService->>AuthorityIssuerService: 入参非空、格式及合法性检查
   opt 入参校验失败
   AuthorityIssuerService-->>调用者: 报错，提示参数不合法并退出
   end
   AuthorityIssuerService->>区块链节点: 调用移除授权机构合约
   区块链节点->>区块链节点: 执行合约移除授权机构
   区块链节点-->>AuthorityIssuerService: 返回执行结果
   AuthorityIssuerService-->>调用者: 返回执行结果

----


9. isSpecificTypeIssuer
~~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.rpc.AuthorityIssuerService.isSpecificTypeIssuer
   接口定义: ResponseData<Boolean> isSpecificTypeIssuer(String issuerType, String targetIssuerWeId)
   接口描述: 判断issuer是否为指定机构里面的成员。

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - issuerType
     - String
     - Y
     - 机构类型
     -
   * - targetIssuerWeId
     - String
     - Y
     - issuer的WeIdentity DID
     -


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<Boolean>;

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
     - 是否为指定类型中的成员
     -
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - WEID_DOES_NOT_EXIST
     - 100104
     - WeIdentity DID不存在
   * - AUTHORITY_ISSUER_ERROR
     - 100200
     -  授权标准异常
   * - SPECIFIC_ISSUER_TYPE_ILLEGAL
     - 100208
     - 机构类型非法
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空
   * - SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_NOT_EXIST
     - 500502
     - 授权人不存在


**调用示例**

.. code-block:: java

   AuthorityIssuerService authorityIssuerService = new AuthorityIssuerServiceImpl();
   String weId = "did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7";
   ResponseData<Boolean> response = authorityIssuerService.isAuthorityIssuer(weId);


.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:null


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


10. getAllSpecificTypeIssuerList
~~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.rpc.AuthorityIssuerService.getAllSpecificTypeIssuerList
   接口定义: ResponseData<List<String>> getAllSpecificTypeIssuerList(String issuerType, Integer index, Integer num)
   接口描述: 获取指定索引范围内的issuer列表。

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - issuerType
     - String
     - Y
     - 机构类型
     -
   * - index
     - Integer
     - Y
     - 检索的开始下标位置
     -
   * - num
     - Integer
     - Y
     - 检索数据个数
     - 单次最多可以检索50条


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<List\<String>>;

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
     - List<String>
     - issuer列表
     -
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - AUTHORITY_ISSUER_ERROR
     - 100200
     -  授权标准异常
   * - SPECIFIC_ISSUER_TYPE_ILLEGAL
     - 100208
     - 机构类型非法
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


**调用示例**

.. code-block:: java



.. code-block:: text

   返回数据如：


----


CptService
^^^^^^^^^^

1. registerCpt
~~~~~~~~~~~~~~

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


----


2. registerCpt
~~~~~~~~~~~~~~

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


----


3. registerCpt
~~~~~~~~~~~~~~

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


----


4. registerCpt
~~~~~~~~~~~~~~

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


----


5. queryCpt
~~~~~~~~~~~

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


----

6. updateCpt
~~~~~~~~~~~~

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


----

7. updateCpt
~~~~~~~~~~~~

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


----

CredentialService
^^^^^^^^^^^^^^^^^

1. createCredential
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialService.createCredential
   接口定义:ResponseData<CredentialWrapper> createCredential(CreateCredentialArgs args)
   接口描述: 创建电子凭证，默认是original类型，还支持轻量级lite1类型和基于零知识证明的zkp类型的credential。

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
     - CPT编号
     -
   * - issuer
     - String
     - Y
     - 发行方WeIdentity DID
     - WeIdentity DID格式数据
   * - expirationDate
     - Long
     - Y
     - 到期日
     -
   * - claim
     - Map<String, Object>
     - Y
     - Map类型的claim数据
     - 凭证所需数据
   * - weIdPrivateKey
     - WeIdPrivateKey
     - Y
     -
     - 签名所用Issuer WeIdentity DID私钥，见下
   * - type
     - CredentialType
     - Y
     - 默认值是ORIGINAL，还支持ZKP和Lite类型
     - 创建的credential的类型


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


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<CredentialWrapper>;

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
     - CredentialWrapper
     -
     - 见下
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


com.webank.weid.protocol.base.CredentialWrapper

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
     - 凭证信息
     - 具体见下
   * - disclosure
     - Map<String, Object>
     - Y
     - 披露属性
     - 默认为全披露


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
     - 证书ID
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
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
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
     - JsonSchema无效
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_ERROR
     - 100400
     - Credential标准错误
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
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
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**
Original类型的credential生成示例：

.. code-block:: java

   CredentialService credentialService = new CredentialServiceImpl();

   HashMap<String, Object> claim = new HashMap<String, Object>(3);
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 18);

   CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
   createCredentialArgs.setClaim(claim);
   createCredentialArgs.setCptId(1017);
   createCredentialArgs.setExpirationDate(1551448312461L);
   createCredentialArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   createCredentialArgs.setWeIdPrivateKey(weIdPrivateKey);

   ResponseData<CredentialWrapper> response = credentialService.createCredential(createCredentialArgs);


.. code-block:: text

   返回结果如下：
   {
    "errorCode":0,
    "errorMessage":"success",
    "result":{
        "claim":{
            "age":18,
            "gender":"F",
            "name":"zhangsan"
        },
        "context":"https://github.com/WeBankFinTech/WeIdentity/blob/master/context/v1",
        "cptId":2000082,
        "expirationDate":1588776752,
        "id":"0d633260-d31c-4155-b79d-a9eb67df7bab",
        "issuanceDate":1588065179,
        "issuer":"did:weid:101:0x9bd9897fcdb98428f7b152ce8a06cb16758ccd17",
        "proof":{
            "created":1588065179,
            "creator":"did:weid:101:0x9bd9897fcdb98428f7b152ce8a06cb16758ccd17#keys-0",
            "salt":{
                "age":"exkEX",
                "gender":"ya9jA",
                "name":"Q4BDW"
            },
            "signatureValue":"G51huya0Q4Nz4HGa+dUju3GVrR0ng+atlXeouEKe60ImLMl6aihwZsSGExOgC8KwP3sUjeiggdba3xjVE9SSI/g=",
            "type":"Secp256k1"
        },
        "type":[
            "VerifiableCredential",
            "original"
        ]
    },
    "transactionInfo":null
}

Lite类型的credential生成示例：

.. code-block:: java

   CredentialService credentialService = new CredentialServiceImpl();

   HashMap<String, Object> claim = new HashMap<String, Object>(3);
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 18);

   CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
   createCredentialArgs.setClaim(claim);
   createCredentialArgs.setCptId(1017);
   createCredentialArgs.setExpirationDate(1551448312461L);
   createCredentialArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   //如果不设置type为LITE1，则默认生成ORIGINAL类型
   createCredentialArgs.setType(CredentialType.LITE1);
   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   createCredentialArgs.setWeIdPrivateKey(weIdPrivateKey);

   ResponseData<CredentialWrapper> response = credentialService.createCredential(createCredentialArgs);


.. code-block:: text

   返回结果如下，lite 类型的credential会比original类型的credential少salt等一些字段，更轻量：
   {
    "errorCode":0,
    "errorMessage":"success",
    "result":{
        "claim":{
            "age":18,
            "gender":"F",
            "name":"zhangsan"
        },
        "context":"https://github.com/WeBankFinTech/WeIdentity/blob/master/context/v1",
        "cptId":2000082,
        "expirationDate":1588776636,
        "id":"c85cbaea-753b-4ae7-830f-20fb718b01b7",
        "issuanceDate":1588065063,
        "issuer":"did:weid:101:0x9bd9897fcdb98428f7b152ce8a06cb16758ccd17",
        "proof":{
            "signatureValue":"YopZgmhvi6ob9xPiROLb4p2WJ7j7RTwydGDUbonO9GEZBkpYVfcnlrbJ2H1vuyaVaoR46goJWfDWG3s1woY1/AE=",
            "type":"Secp256k1"
        },
        "type":[
            "VerifiableCredential",
            "lite1"
        ]
    },
    "transactionInfo":null
}

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

2. verify
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialService.verify
   接口定义:ResponseData<Boolean> verify(Credential credential);
   接口描述: 验证凭证是否正确。

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
     - 证书ID
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
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
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
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - CREDENTIAL_EXCEPTION_VERIFYSIGNATURE
     - 100419
     - 验证签名异常
   * - CREDENTIAL_SIGNATURE_TYPE_ILLEGAL
     - 100429
     - 验证签名类型异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   CredentialService credentialService = new CredentialServiceImpl();

   HashMap<String, Object> claim = new HashMap<String, Object>(3);
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 18);

   CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
   createCredentialArgs.setClaim(claim);
   createCredentialArgs.setCptId(1017);
   createCredentialArgs.setExpirationDate(1561448312461L);
   createCredentialArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   createCredentialArgs.setWeIdPrivateKey(weIdPrivateKey);

   //创建Credential
   ResponseData<CredentialWrapper> response = credentialService.createCredential(createCredentialArgs);

   //验证Credential
   ResponseData<Boolean> responseVerify = credentialService.verify(response.getResult().getCredential());


.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:null


**时序图**

（同时也包含verifyCredentialWithSpecifiedPubKey时序）

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant CredentialService
   participant CptService
   participant WeIdService
   participant 区块链节点
   调用者->>CredentialService: 调用verify()或verifyCredentialWithSpecifiedPubKey()
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
   接口定义: ResponseData<Boolean> verifyCredentialWithSpecifiedPubKey(CredentialWrapper credentialWrapper, WeIdPublicKey weIdPublicKey)
   接口描述: 验证凭证是否正确，需传入公钥。

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - credentialWrapper
     - CredentialWrapper
     - Y
     -
     - 凭证信息，见下
   * - weIdPublicKey
     - WeIdPublicKey
     - Y
     -
     - 公钥信息，见下


com.webank.weid.protocol.base.CredentialWrapper

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
     - 凭证信息
     - 具体见下
   * - disclosure
     - Map<String, Object>
     - N
     - 披露属性
     - 默认为全披露


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
     - 证书ID
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
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
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
     - 返回结果值
     -
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
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
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - CREDENTIAL_EXCEPTION_VERIFYSIGNATURE
     - 100419
     - 验证签名异常
   * - CREDENTIAL_SIGNATURE_TYPE_ILLEGAL
     - 100429
     - 验证签名类型异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   CredentialService credentialService = new CredentialServiceImpl();

   HashMap<String, Object> claim = new HashMap<String, Object>(3);
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 18);

   CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
   createCredentialArgs.setClaim(claim);
   createCredentialArgs.setCptId(1017);
   createCredentialArgs.setExpirationDate(1561448312461L);
   createCredentialArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   createCredentialArgs.setWeIdPrivateKey(weIdPrivateKey);

   // 创建Credential
   ResponseData<CredentialWrapper> response = credentialService.createCredential(createCredentialArgs);

   WeIdPublicKey weIdPublicKey = new WeIdPublicKey();
   weIdPublicKey.setPublicKey(
      "9202079291855274840499629257327649367489192973501473466426182121217769706994308329953406897395674428921435762028726727399019951049448689033610431403383875");

   //使用公钥验证
   ResponseData<Boolean> responseVerify = credentialService
      .verifyCredentialWithSpecifiedPubKey(response.getResult(), weIdPublicKey);


.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:null


----

4. getCredentialHash
~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialService.getCredentialHash
   接口定义:ResponseData<String> getCredentialHash(Credential args)
   接口描述: 传入Credential信息生成Credential整体的Hash值，一般在生成Evidence时调用。

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
     - 证书ID
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
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
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
     - 返回结果值
     -
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_EXPIRED
     - 100402
     - 过期
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名破坏
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
   * - CREDENTIAL_EXPIRE_DATE_ILLEGAL
     - 100409
     - 到期日期格式非法
   * - CREDENTIAL_CLAIM_NOT_EXISTS
     - 100410
     - Claim数据不能为空
   * - CREDENTIAL_ID_NOT_EXISTS
     - 100412
     - ID为空
   * - CREDENTIAL_CONTEXT_NOT_EXISTS
     - 100413
     - context为空
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - CREDENTIAL_SIGNATURE_TYPE_ILLEGAL
     - 100429
     - 验证签名类型异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   CredentialService credentialService = new CredentialServiceImpl();

   HashMap<String, Object> claim = new HashMap<String, Object>(3);
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 18);

   CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
   createCredentialArgs.setClaim(claim);
   createCredentialArgs.setCptId(1017);
   createCredentialArgs.setExpirationDate(1561448312461L);
   createCredentialArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

    createCredentialArgs.setWeIdPrivateKey(weIdPrivateKey);
   //创建Credentia
   ResponseData<CredentialWrapper> response = credentialService.createCredential(createCredentialArgs);

   //获取Credentia的Hash
   ResponseData<String> responseHash = credentialService.getCredentialHash(response.getResult().getCredential());


.. code-block:: text

   返回结果如：
   result: 0x06173e4b714d57565ae5ddf23c4e84cb0a9824cb72eab476303d2dd1cc0a4728
   errorCode: 0
   errorMessage: success
   transactionInfo:null


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

5. getCredentialHash
~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialService.getCredentialHash
   接口定义:ResponseData<String> getCredentialHash(CredentialWrapper args)
   接口描述: 传入Credential信息生成Credential整体的Hash值，一般在生成Evidence时调用。

**接口入参**\ :   com.webank.weid.protocol.base.CredentialWrapper


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
     - 凭证信息
     - 具体见下
   * - disclosure
     - Map<String, Object>
     - Y
     - 披露属性
     - 默认为全披露


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
     - 证书ID
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
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
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
     - 返回结果值
     -
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_EXPIRED
     - 100402
     - 过期
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名破坏
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
   * - CREDENTIAL_EXPIRE_DATE_ILLEGAL
     - 100409
     - 到期日期格式非法
   * - CREDENTIAL_CLAIM_NOT_EXISTS
     - 100410
     - Claim数据不能为空
   * - CREDENTIAL_ID_NOT_EXISTS
     - 100412
     - ID为空
   * - CREDENTIAL_CONTEXT_NOT_EXISTS
     - 100413
     - context为空
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - CREDENTIAL_SIGNATURE_TYPE_ILLEGAL
     - 100429
     - 验证签名类型异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   CredentialService credentialService = new CredentialServiceImpl();

   HashMap<String, Object> claim = new HashMap<String, Object>(3);
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 18);

   CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
   createCredentialArgs.setClaim(claim);
   createCredentialArgs.setCptId(1017);
   createCredentialArgs.setExpirationDate(1561448312461L);
   createCredentialArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

     createCredentialArgs.setWeIdPrivateKey(weIdPrivateKey);
   //创建CredentialWrapper
   ResponseData<CredentialWrapper> response = credentialService.createCredential(createCredentialArgs);

   //获取CredentialWrapper的Hash
   ResponseData<String> responseHash = credentialService.getCredentialHash(response.getResult());


.. code-block:: text

   返回结果如：
   result: 0x06173e4b714d57565ae5ddf23c4e84cb0a9824cb72eab476303d2dd1cc0a4728
   errorCode: 0
   errorMessage: success
   transactionInfo:null


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


6. addSignature
~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialService.addSignature
   接口定义:ResponseData<Credential> addSignature(List<Credential> credentialList, WeIdPrivateKey weIdPrivateKey)
   接口描述:多签，在原凭证列表的基础上，创建包裹成一个新的多签凭证，由传入的私钥所签名。此凭证的CPT为一个固定值。在验证一个多签凭证时，会迭代验证其包裹的所有子凭证。本接口不支持创建选择性披露的多签凭证。

**接口入参**\ :   java.util.ArrayList


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
     - 证书ID
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
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
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
     -
     - 见下
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
     - 证书ID
     -
   * - cptId
     - Integer
     - Y
     - cptId
     - 默认为106
   * - issuer
     - String
     - Y
     - WeIdentity DID
     -
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
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
     - JsonSchema无效
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_ERROR
     - 100400
     - Credential标准错误
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
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
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   CredentialService credentialService = new CredentialServiceImpl();

   HashMap<String, Object> claim = new HashMap<String, Object>(3);
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 18);

   CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
   createCredentialArgs.setClaim(claim);
   createCredentialArgs.setCptId(1017);
   createCredentialArgs.setExpirationDate(1551448312461L);
   createCredentialArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   createCredentialArgs.setWeIdPrivateKey(weIdPrivateKey);

   ResponseData<CredentialWrapper> response = credentialService.createCredential(createCredentialArgs);
   List<Credential> credList = new ArrayList<>();
   credList.add(response.getResult().getCredential());
      Long expirationDate = DateUtils.convertToNoMillisecondTimeStamp(
       createCredentialArgs.getExpirationDate() + 24 * 60 * 60);
   createCredentialArgs.setExpirationDate(expirationDate);
   Credential tempCredential =
       credentialService.createCredential(createCredentialArgs).getResult().getCredential();
   credentialList.add(tempCredential);
   ResponseData<Credential> multiSignedResp = credentialService.addSignature(credList, weIdPrivateKey);
   System.out.println(multiSignedResp);

.. code-block:: text

   返回结果如：
   result:(com.webank.weid.protocol.base.CredentialWrapper)
      credential:(com.webank.weid.protocol.base.Credential) {
        "claim": {
          "credentialList": [
            {
              "claim": {
                "age": 18,
                "gender": "F",
                "id": "did:weid:101:0xe4bee5a07f282ffd3109699e21663cde0210fb64",
                "name": "zhang san"
              },
              "context": "https:\/\/github.com\/WeBankFinTech\/WeIdentity\/blob\/master\/context\/v1",
              "cptId": 2000084,
              "expirationDate": 1567488114,
              "id": "a8b1c030-231d-49de-9618-b5ed7f3e6d2e",
              "issuanceDate": 1567401714,
              "issuer": "did:weid:1000:1:0x92d5472954c38375371f8bdd2bcce2e64aab1f99",
              "proof": {
                "created": "1567401714",
                "creator": "did:weid:1000:1:0x92d5472954c38375371f8bdd2bcce2e64aab1f99",
                "signature": "GwKcDoEseYdJxI7M\/R4RAdGcV5SJoFVvg8Z53BVa76LMV8eqbX3F4rb1dWjhqI286AvPECx6uuuo9cTAKuNHRXM=",
                "type": "Secp256k1"
              }
            },
            {
              "claim": {
                "age": 18,
                "gender": "F",
                "id": "did:weid:101:0xe4bee5a07f282ffd3109699e21663cde0210fb64",
                "name": "zhang san"
              },
              "context": "https:\/\/github.com\/WeBankFinTech\/WeIdentity\/blob\/master\/context\/v1",
              "cptId": 2000084,
              "expirationDate": 1567488201,
              "id": "2130908d-fb2a-4675-8bf1-727f354ca8e4",
              "issuanceDate": 1567401715,
              "issuer": "did:weid:1000:1:0x92d5472954c38375371f8bdd2bcce2e64aab1f99",
              "proof": {
                "created": "1567401715",
                "creator": "did:weid:1000:1:0x92d5472954c38375371f8bdd2bcce2e64aab1f99",
                "signature": "HJXDuvg2l8jRbL5ymmBSAo\/6DMKbCv3P1XoP67S+OVzSbRVDNFXY1CsqpTqT5MAkSY4+UwPLwCfXrLtHsZQ6GOo=",
                "type": "Secp256k1"
              }
            }
          ]
        },
        "context": "https:\/\/github.com\/WeBankFinTech\/WeIdentity\/blob\/master\/context\/v1",
        "cptId": 106,
        "expirationDate": 1567488201,
        "id": "d8642623-703f-447a-8765-dab1dab4df0a",
        "issuanceDate": 1567401717,
        "issuer": "did:weid:1000:1:0x92d5472954c38375371f8bdd2bcce2e64aab1f99",
        "proof": {
          "created": "1567401717",
          "creator": "did:weid:1000:1:0x92d5472954c38375371f8bdd2bcce2e64aab1f99",
          "signature": "HKXEwzDEwqte4aAUBLvQjiI3C0cw5V\/iWeKWmBs7HIG0IRzgbXnMj8kYw37y5yJE4KdsWCuehBUGuW7WdihL560=",
          "type": "Secp256k1"
        }
      }
   errorCode: 0
   errorMessage: success
   transactionInfo:null


**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant CredentialService
   调用者->>CredentialService: 调用addSignature()
   CredentialService->>CredentialService: 入参非空、格式及合法性检查
   opt 入参校验失败
   CredentialService-->>调用者: 报错，提示参数不合法并退出
   end
   CredentialService->>CredentialService: 生成签发日期、以原凭证列表为Claim生成数字签名
   CredentialService-->>调用者: 返回凭证


----

EvidenceService
^^^^^^^^^^^^^^^^^

1. createEvidence
~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.EvidenceService.createEvidence
   接口定义:ResponseData<String> createEvidence(Hashable object, WeIdPrivateKey weIdPrivateKey)
   接口描述: 为一个**未曾上过链**的Object，将传入的Object计算Hash值生成存证上链，返回存证hash值。传入的私钥将会成为链上存证的签名方。此签名方和凭证的Issuer可以不是同一方。此接口返回的Hash值和generateHash()接口返回值一致。同样的传入Object可以由不同的私钥注册存证，它们的链上存证值将会共存。

**接口入参**\ :

Hashable java.lang.Object

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - Object
     - Hashable object
     - N
     - 实现了Hashable接口的任意Object
     - 当前支持Credential，CredentialWrapper，CredentialPojo

com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - privateKey
     - String
     - 私钥
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
     - 创建的凭证hash值
     - 业务数据
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CREDENTIAL_PRIVATE_KEY_NOT_EXISTS
     - 100415
     - 私钥为空
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
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
   * - CREDENTIAL_EVIDENCE_CONTRACT_FAILURE_ILLEAGAL_INPUT
     - 500401
     - Evidence参数非法

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

2. createEvidenceWithLogAndCustomKey / createEvidenceWithLog
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.EvidenceService.createEvidence
   接口定义:ResponseData<String> createEvidence(Hashable object, WeIdPrivateKey weIdPrivateKey, String log, String customKey)
   接口描述: 为一个**未曾上过链**的Object，将传入Object计算Hash值生成存证上链。此方法允许在创建存证时写入额外信息。额外信息为一个log记录，从后往前叠加存储。不同私钥发交易方的额外信息也是共存且相互独立存储的。如果您重复调用此接口，那么新写入的额外值会以列表的形式添加到之前的log列表之后。此方法还允许传入一个用户自定义的custom key，用来查询链上的存证（而不是通过hash）。

**接口入参**\ :

Hashable java.lang.Object

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - Object
     - Hashable object
     - N
     - 实现了Hashable接口的任意Object
     - 当前支持Credential，CredentialWrapper，CredentialPojo

String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - log
     - String
     - Y
     - 额外信息
     - 长度不能超过2M，必须为UTF-8

String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - customKey
     - String
     - Y
     - 用户自定索引关键字
     - 长度不能超过2M，必须为UTF-8

com.webank.weid.protocol.base.WeIdPrivateKey

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - privateKey
     - String
     - 私钥
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
     - 创建的凭证hash值
     - 业务数据
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CREDENTIAL_PRIVATE_KEY_NOT_EXISTS
     - 100415
     - 私钥为空
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
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
   * - CREDENTIAL_EVIDENCE_CONTRACT_FAILURE_ILLEAGAL_INPUT
     - 500401
     - Evidence参数非法

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
   EvidenceService->>区块链节点: 调用智能合约，创建并上传凭证存证及额外值
   区块链节点-->>EvidenceService: 返回创建结果
   opt 创建失败
   EvidenceService-->>调用者: 报错并退出
   end
   EvidenceService-->>调用者: 返回成功


----

3. getEvidence
~~~~~~~~~~~~~~~~~~~


**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.EvidenceService.getEvidence
   接口定义:ResponseData<EvidenceInfo> getEvidence(String hashValue)
   接口描述: 根据传入的凭证存证hash值，在链上查找凭证在链上是否存在。如果存在，则返回所有为此hash值创建过存证的创建方，及其创建时间、额外信息。


**接口入参**\ :   String

**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<EvidenceInfo>;

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
     - EvidenceInfo
     - 创建的凭证合约地址
     - 业务数据
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


com.webank.weid.protocol.base.EvidenceInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - credentialHash
     - String
     - 凭证Hash值
     - 是一个66个字节的字符串，以0x开头
   * - signInfo
     - Map<String, EvidenceSignInfo>
     - 存证创建者信息
     - 链上允许一个存证存在多个创建者

com.webank.weid.protocol.base.EvidenceSignInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - signature
     - String
     - 存证的签名
     - 以Base64编码的存证签名值
   * - timestamp
     - String
     - 存证创建时间
     -
   * - logs
     - List<String>
     - 额外信息列表
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
   EvidenceService->>区块链节点: 调用智能合约，查询凭证存证内容，反向构建Evidence
   区块链节点-->>EvidenceService: 返回查询结果
   opt 查询出错
   EvidenceService-->>调用者: 报错并退出
   end
   EvidenceService-->>调用者: 返回成功

----

4. getEvidenceByCustomKey
~~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.EvidenceService.getEvidenceByCustomKey
   接口定义:ResponseData<EvidenceInfo> getEvidenceByCustomKey(String customKey)
   接口描述: 根据传入的自定义索引，在链上查找凭证在链上是否存在。如果存在，则返回所有为此索引值值创建过存证的创建方，及其创建时间、额外信息。


**接口入参**\ :   String

**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<EvidenceInfo>;

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
     - EvidenceInfo
     - 创建的凭证合约地址
     - 业务数据
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


com.webank.weid.protocol.base.EvidenceInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - credentialHash
     - String
     - 凭证Hash值
     - 是一个66个字节的字符串，以0x开头
   * - signInfo
     - Map<String, EvidenceSignInfo>
     - 存证创建者信息
     - 链上允许一个存证存在多个创建者

com.webank.weid.protocol.base.EvidenceSignInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - signature
     - String
     - 存证的签名
     - 以Base64编码的存证签名值
   * - timestamp
     - String
     - 存证创建时间
     -
   * - logs
     - List<String>
     - 额外信息列表
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
   EvidenceService->>区块链节点: 调用智能合约，查询凭证存证内容，反向构建Evidence
   区块链节点-->>EvidenceService: 返回查询结果
   opt 查询出错
   EvidenceService-->>调用者: 报错并退出
   end
   EvidenceService-->>调用者: 返回成功

----

5. verifySigner
~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.EvidenceService.verifySigner
   接口定义:ResponseData<Boolean> verify(EvidenceInfo evidenceInfo, String weId)
   接口描述: 根据传入的存证信息和WeID，从链上根据WeID的公钥，判断此存证是否合法。

**接口入参**\ :

com.webank.weid.protocol.base.EvidenceInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - credentialHash
     - String
     - 凭证Hash值
     - 是一个66个字节的字符串，以0x开头
   * - signInfo
     - Map<String, EvidenceSignInfo>
     - 存证创建者信息
     - 链上允许一个存证存在多个创建者

java.lang.String

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
     - 用户WeID
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
     - 是否验证成功
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
   * - CREDENTIAL_EVIDENCE_SIGNATURE_BROKEN
     - 100431
     - 存证签名异常
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

.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:null


**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant EvidenceService
   participant WeIdService
   participant 区块链节点
   调用者->>EvidenceService: 调用VerifySi
   EvidenceService->>EvidenceService: 入参非空、格式及合法性检查
   opt 入参校验失败
   EvidenceService-->>调用者: 报错，提示参数不合法并退出
   end
   EvidenceService->>WeIdService: 根据存证中签名方信息，调用GetWeIdDocument()查询WeID公钥
   WeIdService->>区块链节点: 调用智能合约，查询WeID公钥
   区块链节点-->>WeIdService: 返回查询结果
   EvidenceService->>EvidenceService: 验证存证中签名是否为与凭证Hash一致
   opt 验签失败
   EvidenceService-->>调用者: 返回验证失败，报错并退出
   end
   EvidenceService-->>调用者: 返回验证成功

----

6. verifySigner（传入公钥）
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.EvidenceService.verifySigner
   接口定义:ResponseData<Boolean> verify(EvidenceInfo evidenceInfo, String weId, String publicKey)
   接口描述: 根据传入的存证信息和WeID，及传入的公钥，判断此WeID是否为存证的合法创建者。不需要链上交互。

**接口入参**\ :

com.webank.weid.protocol.base.EvidenceInfo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - credentialHash
     - String
     - 凭证Hash值
     - 是一个66个字节的字符串，以0x开头
   * - signInfo
     - Map<String, EvidenceSignInfo>
     - 存证创建者信息
     - 链上允许一个存证存在多个创建者

java.lang.String

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
     - 用户WeID
     -

java.lang.String

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
     - 传入公钥
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
     - 是否验证成功
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
   * - CREDENTIAL_EVIDENCE_SIGNATURE_BROKEN
     - 100431
     - 存证签名异常
   * - CREDENTIAL_EVIDENCE_BASE_ERROR
     - 100500
     - Evidence标准错误
   * - WEID_PUBLICKEY_INVALID
     - 100102
     - 公钥格式非法
   * - TRANSACTION_TIMEOUT
     - 160001
     - 超时
   * - TRANSACTION_EXECUTE_ERROR
     - 160002
     - 交易错误
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空

.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:null


**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant EvidenceService
   participant WeIdService
   participant 区块链节点
   调用者->>EvidenceService: 调用VerifySigner()
   EvidenceService->>EvidenceService: 入参非空、格式及合法性检查
   opt 入参校验失败
   EvidenceService-->>调用者: 报错，提示参数不合法并退出
   end
   EvidenceService->>EvidenceService: 验证存证中签名是否合法且WeID存在
   opt 验签失败
   EvidenceService-->>调用者: 返回验证失败，报错并退出
   end
   EvidenceService-->>调用者: 返回验证成功

----

7. generateHash
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.rpc.EvidenceService.generateHash
   接口定义: ResponseData<HashString> generateHash(T object)
   接口描述: 将传入的任意Object计算Hash值，不需网络。可以接受**任意Hashable对象**（如凭证）、**File**（Java里的文件实例）、**String**（字符串）。对于不符合类型的入参，将返回类型不支持错误。返回值为HashString，可以直接传入CreateEvidence接口用于存证创建。

**接口入参**\ :

T java.lang.Object

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - Object
     - T object
     - N
     - 任意Object
     - 当前支持Hashable，File, String


**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<HashString>;

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
     - HashString
     - 创建的符合Hashable接口的存证值对象
     - 存证值，可以直接传入createEvidence用于存证上链

**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - ILLEGAL_INPUT
     - 160004
     - 入参非法


**调用示例**

.. code-block:: java

   CredentialService credentialService = new CredentialServiceImpl();
   EvidenceService evidenceService = new EvidenceServiceImpl();

   HashMap<String, Object> claim = new HashMap<String, Object>(3);
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 18);

   CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
   createCredentialArgs.setClaim(claim);
   createCredentialArgs.setCptId(1017);
   createCredentialArgs.setExpirationDate(1561448312461L);
   createCredentialArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   createCredentialArgs.setWeIdPrivateKey(weIdPrivateKey);

   // 创建Credential
   ResponseData<CredentialWrapper> response = credentialService.createCredential(createCredentialArgs);

   // 直接将凭证传入generateHash
   HashString hashString = evidenceService.generateHash(response.getResult().getCredential()).getResult();
   ResponseData<String> responseCreateEvidence = evidenceService.createEvidence(hashString, weIdPrivateKey);


.. code-block:: text

   返回结果如：
   result: 0xa3203e054bb7a7f0dec134c7510299869e343e8d
   errorCode: 0
   errorMessage: success
   transactionInfo:(com.webank.weid.protocol.response.TransactionInfo)
      blockNumber: 30014
      transactionHash: 0x1f9e62fa152eb5fce859dcf81c7c0eddcbcab63c40629d1c745058c227693dae
      transactionIndex: 0


**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant EvidenceService
   participant 区块链节点
   调用者->>EvidenceService: 调用generateHash()
   EvidenceService->>EvidenceService: 入参非空、格式及合法性检查（Hashable, File, String）
   opt 入参校验失败
   EvidenceService-->>调用者: 报错，提示参数不合法并退出
   end
   EvidenceService-->>调用者: 返回HashString及成功

----

8. addLogByHash / addLogByCustomKey
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.EvidenceService.addLogByHash / addLogByCustomKey
   接口定义:ResponseData<Boolean> addLogByHash(String hashValueSupplement（仅在customKey中用到）, String hashValue / customKey, String log, WeIdPrivateKey weIdPrivateKey)
   接口描述: 为一个**已经在链上存在的存证**添加额外信息记录存入其log中。有两个接口，一个是以hash值为索引，一个可以接受用户自定义索引（customKey）；如果自定义索引不存在，则会使用替补hash作为上链索引。

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
     - 成功与否
     -
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
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

**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant EvidenceService
   participant 区块链节点
   调用者->>EvidenceService: 调用addLog()
   EvidenceService->>EvidenceService: 入参非空、格式及合法性检查
   opt 入参校验失败
   EvidenceService-->>调用者: 报错，提示参数不合法并退出
   end
   EvidenceService->>区块链节点: 调用智能合约，添加额外信息
   区块链节点-->>EvidenceService: 返回查询结果
   opt 查询出错
   EvidenceService-->>调用者: 报错并退出
   end
   EvidenceService-->>调用者: 返回成功


CredentialPojoService
^^^^^^^^^^^^^^^^^^^^^^^^^

1. createCredential
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.createCredential
   接口定义:<T> ResponseData<CredentialPojo> createCredential(CreateCredentialPojoArgs<T> args)
   接口描述: 根据传入的claim对象生成Credential。

**接口入参**\ :

com.webank.weid.protocol.request.CreateCredentialPojoArgs<T>

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
     - CPT ID
     -
   * - issuer
     - String
     - Y
     - WeIdentity DID
     -
   * - expirationDate
     - Long
     - Y
     - 到期时间
     -
   * - claim
     - T
     - Y
     - 创建凭证需要的claim数据，参数类型为泛型，为POJO对象（不同的CPT对应不同的POJO类）。
     - 需要通过build-tool工具根据CPT ID生成对应的jar包，
   * - weIdAuthentication
     - WeIdAuthentication
     - Y
     - weId身份信息
     -
   * - type
     - CredentialType
     - N
     - 凭证类型enum，默认为Original，可选ZKP类型和Lite类型
     -

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
     - WeIdentity DID
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
     - 私钥
     - 使用十进制数字表示

**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<CredentialPojo>;

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
     - CredentialPojo
     - 凭证对象
     - 业务数据
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


com.webank.weid.protocol.base.CredentialPojo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - context
     - String
     -
     -
   * - type
     - List<String>
     -
     -
   * - id
     - String
     - 证书ID
     -
   * - cptId
     - Integer
     - cptId
     -
   * - issuer
     - String
     - issuer 的 WeIdentity DID
     -
   * - issuanceDate
     - Long
     - 创建日期
     -
   * - expirationDate
     - Long
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - 签名数据结构体
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
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥和weid不匹配
   * - CREDENTIAL_ERROR
     - 100400
     - credential处理未知异常
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
   * - CREDENTIAL_EXPIRE_DATE_ILLEGAL
     - 100409
     - 到期日期无效
   * - CREDENTIAL_CLAIM_NOT_EXISTS
     - 100410
     - Claim数据不能为空
   * - CREDENTIAL_CLAIM_DATA_ILLEGAL
     - 100411
     - Claim非法
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - ILLEGAL_INPUT
     - 160004
     - 参数非法

**调用示例**

.. code-block:: java

   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
   CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs = new CreateCredentialPojoArgs<Map<String, Object>>();
   createCredentialPojoArgs.setCptId(1017);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

   Map<String, Object> claim = new HashMap<String, Object>();
   claim.put("name", "zhangsan");
   claim.put("gender", "F");
   claim.put("age", 22);
   createCredentialPojoArgs.setClaim(claim);

   ResponseData<CredentialPojo> response = credentialPojoService.createCredential(createCredentialPojoArgs);


.. code-block:: text

   返回结果如：
   result:(com.webank.weid.protocol.base.CredentialPojo)
      context: https://github.com/WeBankFinTech/WeIdentity/blob/master/context/v1
      id: 04a3e89d-825a-49fe-b8f5-8ccb9f487a52
      cptId: 1017
      issuer: did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7
      issuanceDate: 1560420878712
      expirationDate: 1560470944120
      claim:(java.util.HashMap)
         gender: F
         name: zhangsan
         age: 22
      proof:(java.util.HashMap)
         creator: did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0
         salt:(java.util.HashMap)
            gender: ibu7f
            name: el1w8
            age: ajqkr
         created: 1560420878712
         type: Secp256k1
         signatureValue: G7UPiw08P5E9dEcSJEo9zpKu/nsUrpn00xDE+mwDXn9gJEohIlRUX5XTGQB4G1w3yThp6R/2RqjUYkuQTaUXbIU=
      type:(java.util.ArrayList)
         [0]:VerifiableCredential
   errorCode: 0
   errorMessage: success
   transactionInfo:null



**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant CredentialPojoService
   调用者->>CredentialPojoService: 调用CreateCredential()
   CredentialPojoService->>CredentialPojoService: 入参非空、格式及合法性检查
   opt 入参校验失败
   CredentialPojoService-->>调用者: 报错，提示参数不合法并退出
   end
   CredentialPojoService->>CredentialPojoService: 为claim中的每个字段生成盐值
   CredentialPojoService->>CredentialPojoService: 生成签发日期、生成数字签名
   CredentialPojoService-->>调用者: 返回凭证

----

2. prepareZkpCredential
~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.prepareZkpCredential
   接口定义:<T> ResponseData<CredentialPojo> createCredential(CredentialPojo preCredential, String claimJson, WeIdAuthentication weIdAuthentication)
   接口描述: 此接口仅在使用WeDPR的选择性披露时才需要调用，用于生成一些中间数据。用户根据传入的preCredential，claimJson以及weIdAuthentication生成基于系统CPT 111的credential。

**接口入参**\ :

com.webank.weid.protocol.base.CredentialPojo

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
     -
     -
   * - type
     - List<String>
     - Y
     -
     -
   * - id
     - String
     - Y
     - 证书ID
     -
   * - cptId
     - Integer
     - Y
     - cptId
     -
   * - issuer
     - String
     - Y
     - issuer 的 WeIdentity DID
     -
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
     -


java.lang.String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - claimJson
     - String
     - Y
     - User claim
     - 用户填入的claim

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
     - WeIdentity DID
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
     - 私钥
     - 使用十进制数字表示

**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<CredentialPojo>;

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
     - CredentialPojo
     - 凭证对象
     - 业务数据
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


com.webank.weid.protocol.base.CredentialPojo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - context
     - String
     -
     -
   * - type
     - List<String>
     -
     -
   * - id
     - String
     - 证书ID
     -
   * - cptId
     - Integer
     - cptId
     -
   * - issuer
     - String
     - issuer 的 WeIdentity DID
     -
   * - issuanceDate
     - Long
     - 创建日期
     -
   * - expirationDate
     - Long
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - 签名数据结构体
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
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_ISSUER_MISMATCH
     - 100403
     - issuerWeId跟Credential中的issuer不匹配
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
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
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
     - 获取weIdDocument异常
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名验证不通过
   * - CREDENTIAL_EXCEPTION_VERIFYSIGNATURE
     - 100419
     - 签名验证异常
   * - CREDENTIAL_SIGNATURE_TYPE_ILLEGAL
     - 100429
     - 验证签名类型异常
   * - CREDENTIAL_SALT_ILLEGAL
     - 100430
     - 盐值非法
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空

**调用示例**

.. code-block:: java

   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
   CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs = new CreateCredentialPojoArgs<Map<String, Object>>();
   createCredentialPojoArgs.setCptId(110);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

   Map<String, Object> claim = new HashMap<String, Object>();
   claim.put("id", "d5e68eb5-0417-47b0-b678-5eb86c50bf22");
   claim.put("issuer", "did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   claim.put("expirationDate", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);
   claim.put("cptId", 2000003);
   claim.put(issuanceDate, System.currentTimeMillis());
   createCredentialPojoArgs.setClaim(claim);

   ResponseData<CredentialPojo> response = credentialPojoService.createCredential(createCredentialPojoArgs);
     CredentialPojo credential = response.getResult;
   Map<String, Object> userClaim = new HashMap<String, Object>();
   userClaim.put("name", "zhangsan");
   userClaim.put("age", 18);
   userClaim.put("gender", "F");
   String claimJson = DataToolUtils.serialize(userClaim);

   WeIdAuthentication userAuth = new WeIdAuthentication();
   userAuth.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey userPrivateKey = new WeIdPrivateKey();
   userPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   userAuth.setWeIdPrivateKey(userPrivateKey);

   userAuth.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   ResponseData<CredentialPojo> prepareZkpResponse = credentialPojoService.prepareZkpCredential(credential, claimJson, userAuth);


.. code-block:: text

   返回结果如：
   result:(com.webank.weid.protocol.base.CredentialPojo)
      context: https://github.com/WeBankFinTech/WeIdentity/blob/master/context/v1
      id: 04a3e89d-825a-49fe-b8f5-8ccb9f487a52
      cptId: 1017
      issuer: did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7
      issuanceDate: 1560420878712
      expirationDate: 1560470944120
      claim:(java.util.HashMap)
         cptId: 2000003
         credentialSignatureRequest: YWjF2cFZnPT0SKAomEiRkNWU2OGViNS0wNDE3LTQ3YjAtYjY3OC01ZWI4NmM1MGJmMj
         userNonce: mNXpIM2lJaUh2STNtc3hvTHgxMHQxZz09Egg1ZTU2MjBmMhpICixsS2NSNWx
      proof:(java.util.HashMap)
         creator: did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0
         salt:(java.util.HashMap)
            cptId: ibu7f
            credentialSignatureRequest: el1w8
            userNonce: ajqkr
         created: 1560420878712
         type: Secp256k1
         signatureValue: G7UPiw08P5E9dEcSJEo9zpKu/nsUrpn00xDE+mwDXn9gJEohIlRUX5XTGQB4G1w3yThp6R/2RqjUYkuQTaUXbIU=
      type:(java.util.ArrayList)
         [0]:VerifiableCredential
   errorCode: 0
   errorMessage: success
   transactionInfo:null



**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant CredentialPojoService
   调用者->>CredentialPojoService: prepareZkpCredential()
   CredentialPojoService->>CredentialPojoService: 入参非空、格式及合法性检查
   opt 入参校验失败
   CredentialPojoService-->>调用者: 报错，提示参数不合法并退出
   end
   CredentialPojoService->>CredentialPojoService: 为claim中的每个字段生成盐值
   CredentialPojoService->>CredentialPojoService: 生成签发日期、生成数字签名
   CredentialPojoService-->>调用者: 返回凭证

----

3. createSelectiveCredential
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.createSelectiveCredential
   接口定义: ResponseData<CredentialPojo> createSelectiveCredential(CredentialPojo credentialPojo, ClaimPolicy claimPolicy)
   接口描述: 通过原始凭证和披露策略，创建选择性披露的Credential。

.. note:;

   ClaimPolicy内部对选择性披露的策略定义在fieldsToBeDisclosed。它是一个Json字符串，和Claim中定义的Key完全对应，Value为1则为披露（在生成的凭证中显示为原文），Value为0则为不披露（显示为加盐的hash值）。如您的Claim包括name、gender、age三项，想披露name和age，不披露gender，则对应的ClaimPolicy为"{\"name\":1,\"gender\":0,\"age\":1}"

.. note::

   注意：对于已经创建好的选择性披露凭证，不允许再次进行选择性披露。

**接口入参**\ :

com.webank.weid.protocol.base.CredentialPojo

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
     -
     -
   * - type
     - List<String>
     - Y
     -
     -
   * - id
     - String
     - Y
     - 证书ID
     -
   * - cptId
     - Integer
     - Y
     - cptId
     -
   * - issuer
     - String
     - Y
     - issuer 的 WeIdentity DID
     -
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
     -


com.webank.weid.protocol.base.ClaimPolicy

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - fieldsToBeDisclosed
     - String
     - Y
     - 披露配置
     - 根据claim匹配的结构，为一个Json字符串，和Claim字段格式匹配。详见调用示例


**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<CredentialPojo>;

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
     - CredentialPojo
     - 凭证对象
     - 业务数据
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_ERROR
     - 100400
     - Credential标准错误
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名破坏
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
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
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - CREDENTIAL_CLAIM_POLICY_NOT_EXIST
     - 100420
     - 披露策略为null
   * - CREDENTIAL_POLICY_DISCLOSUREVALUE_ILLEGAL
     - 100423
     - policy披露信息非法
   * - CREDENTIAL_POLICY_FORMAT_DOSE_NOT_MATCH_CLAIM
     - 100427
     - 披露策略与Claim不匹配
   * - CREDENTIAL_DISCLOSURE_DATA_TYPE_ILLEGAL
     - 100428
     - 披露数据格式错误
   * - CREDENTIAL_SIGNATURE_TYPE_ILLEGAL
     - 100429
     - 验证签名类型异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数非法
   * - CREDENTIAL_NOT_SUPPORT_SELECTIVE_DISCLOSURE
     - 100440
     - lite credential不支持选择性披露


**调用示例**

.. code-block:: java

   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
   CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
        new CreateCredentialPojoArgs<Map<String, Object>>();
   createCredentialPojoArgs.setCptId(1017);
   createCredentialPojoArgs
        .setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   createCredentialPojoArgs
        .setExpirationDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey(
        "60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication
        .setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

   Map<String, Object> claim = new HashMap<String, Object>();
   claim.put("name", "zhangsan");
   claim.put("gender", "F");
   claim.put("age", 22);
   createCredentialPojoArgs.setClaim(claim);

   ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);

   // 选择性披露
   ClaimPolicy claimPolicy = new ClaimPolicy();
   claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1}");
   ResponseData<CredentialPojo> selectiveResponse =
            credentialPojoService.createSelectiveCredential(response.getResult(), claimPolicy);


.. code-block:: text

   返回结果如：
   result:(com.webank.weid.protocol.base.CredentialPojo)
      context: https://github.com/WeBankFinTech/WeIdentity/blob/master/context/v1
      id: c4f8ca00-7c1b-4ba0-993f-008106075d9c
      cptId: 1017
      issuer: did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7
      issuanceDate: 1560420975268
      expirationDate: 1560471040676
      claim:(java.util.HashMap)
         gender: 0x0756ccf78a0ebd5bd186b054376f1e9d86139bf04f660e9171a74673e5a21c75
         name: zhangsan
         age: 22
      proof:(java.util.HashMap)
         creator: did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0
         salt:(java.util.HashMap)
            gender: 0
            name: rr3g0
            age: 9ysgr
         created: 1560420975268
         type: Secp256k1
         signatureValue: GxVcZJFEnC7w+ZKOZAjmKy5JfFxoEFqffmCMvbUnVYmzEVKIUtDCiDmokZ2X3jIV/uFvUHQ4DWXksrD6Opr1vLo=
      type:(java.util.ArrayList)
         [0]:VerifiableCredential
   errorCode: 0
   errorMessage: success
   transactionInfo:null



**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant CredentialPojoService
   调用者->>CredentialPojoService: 调用createSelectiveCredential()，传入原始凭证
   CredentialPojoService->>CredentialPojoService: 入参非空、格式及合法性检查
   opt 入参校验失败
   CredentialPojoService-->>调用者: 报错，提示参数不合法并退出
   end
   CredentialPojoService->>CredentialPojoService: 根据claimPolicy来隐藏不披露的字段
   CredentialPojoService->>CredentialPojoService: 生成签发日期、生成数字签名
   CredentialPojoService-->>调用者: 返回凭证


----

4. verify
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.verify
   接口定义: ResponseData<Boolean> verify(String issuerWeId, CredentialPojo credential)
   接口描述: 验证credential。

**接口入参**\ :

java.lang.String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - issuerWeId
     - String
     - Y
     - WeIdentity DID
     -


com.webank.weid.protocol.base.CredentialPojo

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
     -
     -
   * - type
     - List<String>
     - Y
     -
     -
   * - id
     - String
     - Y
     - 证书ID
     -
   * - cptId
     - Integer
     - Y
     - cptId
     -
   * - issuer
     - String
     - Y
     - issuer 的 WeIdentity DID
     -
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
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
     - 验证结果
     - 业务数据
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_ISSUER_MISMATCH
     - 100403
     - issuerWeId跟Credential中的issuer不匹配
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
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
   * - CREDENTIAL_TYPE_IS_NULL
     - 100414
     - type为空
   * - CREDENTIAL_CPT_NOT_EXISTS
     - 100416
     - cpt不存在
   * - CREDENTIAL_WEID_DOCUMENT_ILLEGAL
     - 100417
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
     - 获取weIdDocument异常
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名验证不通过
   * - CREDENTIAL_EXCEPTION_VERIFYSIGNATURE
     - 100419
     - 签名验证异常
   * - CREDENTIAL_SIGNATURE_TYPE_ILLEGAL
     - 100429
     - 验证签名类型异常
   * - CREDENTIAL_SALT_ILLEGAL
     - 100430
     - 盐值非法
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
   CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs = new CreateCredentialPojoArgs<Map<String, Object>>();
   createCredentialPojoArgs.setCptId(1017);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

   Map<String, Object> claim = new HashMap<String, Object>();
   claim.put("name", "zhangsan");
   claim.put("gender", "F");
   claim.put("age", 22);
   createCredentialPojoArgs.setClaim(claim);

   ResponseData<CredentialPojo> response = credentialPojoService.createCredential(createCredentialPojoArgs);

   ResponseData<Boolean> responseVerify = credentialPojoService.verify("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7", response.getResult());


.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:null


**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant CredentialPojoService
   participant CptService
   participant WeIdService
   participant 区块链节点
   调用者->>CredentialPojoService: 调用verify()
   CredentialPojoService->>CredentialPojoService: 入参非空、格式及合法性检查
   opt 入参校验失败
   CredentialPojoService-->>调用者: 报错，提示参数不合法并退出
   end
   CredentialPojoService->>WeIdService: 查询WeIdentity DID存在性
   WeIdService->>区块链节点: 调用智能合约，查询WeIdentity DID属性
   区块链节点-->>WeIdService: 返回查询结果
   WeIdService-->>CredentialPojoService: 返回查询结果
   opt 查询不存在
   CredentialPojoService-->>调用者: 报错并退出
   end
   CredentialPojoService->>CptService: 查询CPT存在性及Claim关联语义
   CptService->>区块链节点: 调用智能合约，查询CPT
   区块链节点-->>CptService: 返回查询结果
   CptService-->>CredentialPojoService: 返回查询结果
   opt 不符合CPT格式要求
   CredentialPojoService-->>调用者: 报错并退出
   end
   CredentialPojoService->>CredentialPojoService: 验证过期、撤销与否
   opt 任一验证失败
   CredentialPojoService-->>调用者: 报错并退出
   end
   opt 未提供验签公钥
   CredentialPojoService->>WeIdService: 查询Issuer对应公钥
   WeIdService->>区块链节点: 调用智能合约，查询Issuer的WeIdentity DID Document
   区块链节点-->>WeIdService: 返回查询结果
   WeIdService-->>CredentialPojoService: 返回查询结果
   end
   CredentialPojoService->>CredentialPojoService: 通过公钥与签名对比，验证Issuer是否签发此凭证
   opt 验证签名失败
   CredentialPojoService-->>调用者: 报错并退出
   end
   CredentialPojoService-->>调用者: 返回成功

----

5. verify
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.verify
   接口定义: ResponseData<Boolean> verify(WeIdPublicKey issuerPublicKey, CredentialPojo credential)
   接口描述: 使用指定公钥验证credential。

**接口入参**\ :

com.webank.weid.protocol.base.WeIdPublicKey

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
     - 公钥
     -


com.webank.weid.protocol.base.CredentialPojo

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
     -
     -
   * - type
     - List<String>
     - Y
     -
     -
   * - id
     - String
     - Y
     - 证书ID
     -
   * - cptId
     - Integer
     - Y
     - cptId
     -
   * - issuer
     - String
     - Y
     - issuer 的 WeIdentity DID
     -
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
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
     - 验证结果
     - 业务数据
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_ISSUER_MISMATCH
     - 100403
     - issuerWeId跟Credential中的issuer不匹配
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名验证不通过
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
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
   * - CREDENTIAL_TYPE_IS_NULL
     - 100414
     - type为空
   * - CREDENTIAL_CPT_NOT_EXISTS
     - 100416
     - cpt不存在
   * - CREDENTIAL_WEID_DOCUMENT_ILLEGAL
     - 100417
     - 获取weIdDocument异常
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - CREDENTIAL_EXCEPTION_VERIFYSIGNATURE
     - 100419
     - 签名验证异常
   * - CREDENTIAL_PUBLIC_KEY_NOT_EXISTS
     - 100421
     - 公钥不存在
   * - CREDENTIAL_SIGNATURE_TYPE_ILLEGAL
     - 100429
     - 验证签名类型异常
   * - CREDENTIAL_SALT_ILLEGAL
     - 100430
     - 盐值非法
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
   CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs = new CreateCredentialPojoArgs<Map<String, Object>>();
   createCredentialPojoArgs.setCptId(1017);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

   Map<String, Object> claim = new HashMap<String, Object>();
   claim.put("name", "zhangsan");
   claim.put("gender", "F");
   claim.put("age", 22);
   createCredentialPojoArgs.setClaim(claim);

   ResponseData<CredentialPojo> response = credentialPojoService.createCredential(createCredentialPojoArgs);

   WeIdPublicKey weIdPublicKey = new WeIdPublicKey();
   weIdPublicKey.setPublicKey("9202079291855274840499629257327649367489192973501473466426182121217769706994308329953406897395674428921435762028726727399019951049448689033610431403383875");

   ResponseData<Boolean> responseVerify = credentialPojoService.verify(weIdPublicKey, response.getResult());


.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:null


**时序图**

.. mermaid::


   sequenceDiagram
   participant 调用者
   participant CredentialPojoService
   participant CptService
   participant 区块链节点
   调用者->>CredentialPojoService: 调用verify()
   CredentialPojoService->>CredentialPojoService: 入参非空、格式及合法性检查
   opt 入参校验失败
   CredentialPojoService-->>调用者: 报错，提示参数不合法并退出
   end
   CredentialPojoService->>CptService: 查询CPT存在性及Claim关联语义
   CptService->>区块链节点: 调用智能合约，查询CPT
   区块链节点-->>CptService: 返回查询结果
   CptService-->>CredentialPojoService: 返回查询结果
   opt 不符合CPT格式要求
   CredentialPojoService-->>调用者: 报错并退出
   end
   CredentialPojoService->>CredentialPojoService: 验证过期、撤销与否
   opt 任一验证失败
   CredentialPojoService-->>调用者: 报错并退出
   end
   CredentialPojoService->>CredentialPojoService: 通过公钥与签名对比，验证Issuer是否签发此凭证
   opt 验证签名失败
   CredentialPojoService-->>调用者: 报错并退出
   end
   CredentialPojoService-->>调用者: 返回成功

----

6. verify
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.verify
   接口定义: ResponseData<Boolean> verify(String presenterWeId, PresentationPolicyE presentationPolicyE, Challenge challenge, PresentationE presentationE)
   接口描述: 验证Presentation。

**接口入参**\ :

java.lang.String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - presenterWeId
     - String
     - Y
     - WeIdentity DID
     - 用户的WeIdentity DID


com.webank.weid.protocol.base.PresentationPolicyE

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - id
     - Integer
     - Y
     - polcyId
     - 策略编号
   * - orgId
     - String
     - Y
     - 机构编号
     -
   * - version
     - Integer
     - Y
     - 版本
     -
   * - policyPublisherWeId
     - String
     - Y
     - WeIdentity DID
     - 创建policy机构的WeIdentity DID
   * - policy
     - Map<Integer, ClaimPolicy>
     - Y
     - 策略配置
     - key: CPTID, value: 披露策略对象
   * - extra
     - Map<String, String>
     - N
     - 扩展字段
     -


com.webank.weid.protocol.base.Challenge

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - weId
     - String
     - N
     - WeIdentity DID
     - policy提供给指定的WeIdentity DID
   * - version
     - Integer
     - Y
     - 版本
     -
   * - nonce
     - String
     - Y
     - 随机字符串
     -


com.webank.weid.protocol.base.PresentationE

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - context
     - List<String>
     - Y
     - 上下文
     -
   * - type
     - List<String>
     - Y
     - Presentation Type
     -
   * - credentialList
     - List<CredentialPojo>
     - Y
     - 凭证列表
     -
   * - proof
     - Map<String, Object>
     - Y
     - Presentation的签名信息
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
     - 验证结果
     - 业务数据
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_ISSUER_MISMATCH
     - 100403
     - issuerWeId跟Credential中的issuer不匹配
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名验证不通过
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
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
   * - CREDENTIAL_TYPE_IS_NULL
     - 100414
     - type为空
   * - CREDENTIAL_CPT_NOT_EXISTS
     - 100416
     - cpt不存在
   * - CREDENTIAL_WEID_DOCUMENT_ILLEGAL
     - 100417
     - 获取weIdDocument异常
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - CREDENTIAL_EXCEPTION_VERIFYSIGNATURE
     - 100419
     - 签名验证异常
   * - CREDENTIAL_SIGNATURE_NOT_EXISTS
     - 100422
   * - CREDENTIAL_POLICY_DISCLOSUREVALUE_ILLEGAL
     - 100423
     - policy披露信息非法
   * - CREDENTIAL_DISCLOSUREVALUE_NOTMATCH_SALTVALUE
     - 100424
     - Credential披露信息跟盐信息不一致
   * - CREDENTIAL_CPTID_NOTMATCH
     - 100425
     - CPT不匹配
     - 签名不存在
   * - CREDENTIAL_PRESENTERWEID_NOTMATCH
     - 100426
     - presenterWeId跟challenge不匹配
   * - CREDENTIAL_SIGNATURE_TYPE_ILLEGAL
     - 100429
     - 验证签名类型异常
   * - CREDENTIAL_SALT_ILLEGAL
     - 100430
     - 盐值非法
   * - CREDENTIAL_USE_VERIFY_FUNCTION_ERROR
     - 100439
     - 使用了错误的verify方法
   * - ILLEGAL_INPUT
     - 160004
     - 参数非法
   * - PRESENTATION_CHALLENGE_NONCE_MISMATCH
     - 100605
     - challenge随机数不匹配
   * - PRESENTATION_SIGNATURE_MISMATCH
     - 100606
     - presentation验签失败


**调用示例**

.. code-block:: java

   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
   CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs = new CreateCredentialPojoArgs<Map<String, Object>>();
   createCredentialPojoArgs.setCptId(1017);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

   Map<String, Object> claim = new HashMap<String, Object>();
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 22);
   createCredentialPojoArgs.setClaim(claim);

   //创建CredentialPojo
   ResponseData<CredentialPojo> response = credentialPojoService.createCredential(createCredentialPojoArgs);

   List<CredentialPojo> credentialList = new ArrayList<CredentialPojo>();
   credentialList.add(response.getResult());

   //创建Challenge
   Challenge challenge = Challenge.create("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7", String.valueOf(System.currentTimeMillis()));

   //创建PresentationPolicyE
   String policyJson = "{\"extra\" : {\"extra1\" : \"\",\"extra2\" : \"\"},\"id\" : 123456,\"version\" : 1,\"orgId\" : \"webank\",\"weId\" : \"did:weid:1000:0x0231765e19955fc65133ec8591d73e9136306cd0\",\"policy\" : {\"1017\" : {\"fieldsToBeDisclosed\" : {\"gender\" : 0,\"name\" : 1,\"age\" : 0}}}}";
   PresentationPolicyE presentationPolicyE = PresentationPolicyE.fromJson(policyJson);

   //创建Presentation
   ResponseData<PresentationE>  presentationERes = credentialPojoService.createPresentation(credentialList, presentationPolicyE, challenge, weIdAuthentication);

   //验证Presentation
   ResponseData<Boolean> verifyRes = credentialPojoService.verify("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7", presentationPolicyE, challenge, presentationERes.getResult());


.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:null


**时序图**

.. mermaid::


   sequenceDiagram
   participant 调用者
   participant CredentialPojoService
   participant CptService
   participant 区块链节点
   调用者->>CredentialPojoService: 调用verify()
   CredentialPojoService->>CredentialPojoService: 入参非空、格式及合法性检查
   opt 入参校验失败
   CredentialPojoService-->>调用者: 报错，提示参数不合法并退出
   end
   loop 遍历credentialPojo列表
   CredentialPojoService->>CredentialPojoService: 验证policy和claim里的key是否一致
   opt 任一验证失败
   CredentialPojoService-->>调用者: 报错并退出
   end
   CredentialPojoService->>CptService: 查询CPT存在性及Claim关联语义
   CptService->>区块链节点: 调用智能合约，查询CPT
   区块链节点-->>CptService: 返回查询结果
   CptService-->>CredentialPojoService: 返回查询结果
   opt 不符合CPT格式要求
   CredentialPojoService-->>调用者: 报错并退出
   end
   CredentialPojoService->>CredentialPojoService: 验证过期、撤销与否
   opt 任一验证失败
   CredentialPojoService-->>调用者: 报错并退出
   end
   CredentialPojoService->>CredentialPojoService: 通过公钥与签名对比，验证Issuer是否签发此凭证
   opt 验证签名失败
   CredentialPojoService-->>调用者: 报错并退出
   end
   end
   CredentialPojoService-->>调用者: 返回成功

----

7. verifyPresentationFromPdf
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.verifyPresentationFromPdf
   接口定义: ResponseData<Boolean> verifyPresentationFromPdf(String pdfTemplatePath, byte[] serializePdf, String presenterWeId, PresentationPolicyE presentationPolicyE, Challenge challenge, PresentationE presentationE)
   接口描述: 验证由PDF Transportation传输的Presentation。

**接口入参**\ :

java.lang.String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - pdfTemplatePath
     - String
     - Y
     - PDF模板路径
     - 用于PDF序列化的PDF模板路径

java.lang.byte

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - serializePdf
     - byte[]
     - Y
     - 包含PDF数据的byte数组
     - 序列化生成包含PDF数据的byte数组

java.lang.String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - presenterWeId
     - String
     - Y
     - WeIdentity DID
     - 用户的WeIdentity DID


com.webank.weid.protocol.base.PresentationPolicyE

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - id
     - Integer
     - Y
     - polcyId
     - 策略编号
   * - orgId
     - String
     - Y
     - 机构编号
     -
   * - version
     - Integer
     - Y
     - 版本
     -
   * - policyPublisherWeId
     - String
     - Y
     - WeIdentity DID
     - 创建policy机构的WeIdentity DID
   * - policy
     - Map<Integer, ClaimPolicy>
     - Y
     - 策略配置
     - key: CPTID, value: 披露策略对象
   * - extra
     - Map<String, String>
     - N
     - 扩展字段
     -


com.webank.weid.protocol.base.Challenge

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - weId
     - String
     - N
     - WeIdentity DID
     - policy提供给指定的WeIdentity DID
   * - version
     - Integer
     - Y
     - 版本
     -
   * - nonce
     - String
     - Y
     - 随机字符串
     -


com.webank.weid.protocol.base.PresentationE

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - context
     - List<String>
     - Y
     - 上下文
     -
   * - type
     - List<String>
     - Y
     - Presentation Type
     -
   * - credentialList
     - List<CredentialPojo>
     - Y
     - 凭证列表
     -
   * - proof
     - Map<String, Object>
     - Y
     - Presentation的签名信息
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
     - 验证结果
     - 业务数据
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_ISSUER_MISMATCH
     - 100403
     - issuerWeId跟Credential中的issuer不匹配
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名验证不通过
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
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
     - 获取weIdDocument异常
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - CREDENTIAL_EXCEPTION_VERIFYSIGNATURE
     - 100419
     - 签名验证异常
   * - CREDENTIAL_SIGNATURE_NOT_EXISTS
     - 100422
   * - CREDENTIAL_POLICY_DISCLOSUREVALUE_ILLEGAL
     - 100423
     - policy披露信息非法
   * - CREDENTIAL_DISCLOSUREVALUE_NOTMATCH_SALTVALUE
     - 100424
     - Credential披露信息跟盐信息不一致
   * - CREDENTIAL_CPTID_NOTMATCH
     - 100425
     - CPT不匹配
     - 签名不存在
   * - CREDENTIAL_PRESENTERWEID_NOTMATCH
     - 100426
     - presenterWeId跟challenge不匹配
   * - CREDENTIAL_SIGNATURE_TYPE_ILLEGAL
     - 100429
     - 验证签名类型异常
   * - CREDENTIAL_SALT_ILLEGAL
     - 100430
     - 盐值非法
   * - ILLEGAL_INPUT
     - 160004
     - 参数非法
   * - PRESENTATION_CHALLENGE_NONCE_MISMATCH
     - 100605
     - challenge随机数不匹配
   * - PRESENTATION_SIGNATURE_MISMATCH
     - 100606
     - presentation验签失败
   * - TRANSPORTATION_PDF_VERIFY_ERROR
     - 100809
     - PDF验证失败


**调用示例**

.. code-block:: java

   //序列化presentation，生成包含PDF信息的byte数组
   ResponseData<byte[]> retSerialize = TransportationFactory.newPdfTransportation()
       .serializeWithTemplate(
           presentationE1,
           new ProtocolProperty(EncodeType.ORIGINAL),
           "src/test/resources/test-template.pdf");

   //反序列化包含PDF信息的byte数组为Presentation
   ResponseData<PresentationE> retDeserialize = TransportationFactory.newPdfTransportation()
       .deserialize(
           retSerialize.getResult(),
           PresentationE.class,
           weIdAuthentication);

   //验证presentation
   ResponseData<Boolean> response = credentialPojoService.verifyPresentationFromPdf(
       "src/test/resources/test-template.pdf",
       retSerialize.getResult(),
       credentialPojoNew1.getIssuer(),
       presentationPolicyE1,
       challenge1,
       retDeserialize.getResult());


.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:null

----

8. createPresentation
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.createPresentation
   接口定义: ResponseData<PresentationE> createPresentation(List<CredentialPojo> credentialList, PresentationPolicyE presentationPolicyE, Challenge challenge, WeIdAuthentication weIdAuthentication)
   接口描述: 创建Presentation。

**接口入参**\ :


java.uitl.List<com.webank.weid.protocol.base.CredentialPojo>

com.webank.weid.protocol.base.CredentialPojo

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
     -
     -
   * - type
     - List<String>
     - Y
     -
     -
   * - id
     - String
     - Y
     - 证书ID
     -
   * - cptId
     - Integer
     - Y
     - cptId
     -
   * - issuer
     - String
     - Y
     - issuer 的 WeIdentity DID
     -
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
     -


com.webank.weid.protocol.base.PresentationPolicyE

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - id
     - Integer
     - Y
     - polcyId
     - 策略编号
   * - orgId
     - String
     - Y
     - 机构编号
     -
   * - version
     - Integer
     - Y
     - 版本
     -
   * - policyPublisherWeId
     - String
     - Y
     - WeIdentity DID
     - 创建policy机构的WeIdentity DID
   * - policy
     - Map<Integer, ClaimPolicy>
     - Y
     - 策略配置
     - key: CPTID, value: 披露策略对象
   * - extra
     - Map<String, String>
     - N
     - 扩展字段
     -


com.webank.weid.protocol.base.Challenge

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - weId
     - String
     - N
     - WeIdentity DID
     - policy提供给指定的WeIdentity DID
   * - version
     - Integer
     - Y
     - 版本
     -
   * - nonce
     - String
     - Y
     - 随机字符串
     -


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
     - Y
     - 公钥Id
     -
   * - weIdPrivateKey
     - WeIdPrivateKey
     - Y
     -
     - 交易私钥，见下


**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<PresentationE>;

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
     - PresentationE
     - 创建的Presentation
     - 业务数据
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


com.webank.weid.protocol.base.PresentationE

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - context
     - List<String>
     - 上下文
     -
   * - type
     - List<String>
     - Presentation Type
     -
   * - credentialList
     - List<CredentialPojo>
     - 凭证列表
     -
   * - proof
     - Map<String, Object>
     - Presentation的签名信息
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
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 用户weId不匹配其私钥
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_ERROR
     - 100400
     - Credential标准错误
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名破坏
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
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - CREDENTIAL_CLAIM_POLICY_NOT_EXIST
     - 100420
     - 披露策略为null
   * - CREDENTIAL_POLICY_FORMAT_DOSE_NOT_MATCH_CLAIM
     - 100427
     - 披露策略与Claim不匹配
   * - CREDENTIAL_SIGNATURE_TYPE_ILLEGAL
     - 100429
     - 验证签名类型异常
   * - PRESENTATION_CHALLENGE_INVALID
     - 100600
     - challenge无效
   * - PRESENTATION_CHALLENGE_WEID_MISMATCH
     - 100601
     - challenge中的weId不匹配用户的weId
   * - PRESENTATION_POLICY_INVALID
     - 100602
     - policy无效
   * - PRESENTATION_CREDENTIALLIST_MISMATCH_CLAIM_POLICY
     - 100603
     - credentialList不匹配Policy
   * - PRESENTATION_WEID_PUBLICKEY_ID_INVALID
     - 100604
     - 公钥编号无效
   * - PRESENTATION_POLICY_PUBLISHER_WEID_INVALID
     - 100609
     - policy中的publisherWeId无效
   * - PRESENTATION_POLICY_PUBLISHER_WEID_NOT_EXIST
     - 100610
     - policy中的publisherWeId不存在
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数非法


**调用示例**

.. code-block:: java

   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
   CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs = new CreateCredentialPojoArgs<Map<String, Object>>();
   createCredentialPojoArgs.setCptId(1017);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

   Map<String, Object> claim = new HashMap<String, Object>();
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 22);
   createCredentialPojoArgs.setClaim(claim);

   //创建CredentialPojo
   ResponseData<CredentialPojo> response = credentialPojoService.createCredential(createCredentialPojoArgs);

   List<CredentialPojo> credentialList = new ArrayList<CredentialPojo>();
   credentialList.add(response.getResult());

   //创建Challenge
   Challenge challenge = Challenge.create("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7", String.valueOf(System.currentTimeMillis()));

   //创建PresentationPolicyE
   String policyJson = "{\"extra\" : {\"extra1\" : \"\",\"extra2\" : \"\"},\"id\" : 123456,\"version\" : 1,\"orgId\" : \"webank\",\"weId\" : \"did:weid:1000:0x0231765e19955fc65133ec8591d73e9136306cd0\",\"policy\" : {\"1017\" : {\"fieldsToBeDisclosed\" : {\"gender\" : 0,\"name\" : 1,\"age\" : 0}}}}";
   PresentationPolicyE presentationPolicyE = PresentationPolicyE.fromJson(policyJson);

   //创建Presentation
   ResponseData<PresentationE>  presentationE = credentialPojoService.createPresentation(credentialList, presentationPolicyE, challenge, weIdAuthentication);


.. code-block:: text

   返回结果如：
   result:(com.webank.weid.protocol.base.PresentationE)
      context:(java.util.ArrayList)
         [0]:https://github.com/WeBankFinTech/WeIdentity/blob/master/context/v1
      type:(java.util.ArrayList)
         [0]:VerifiablePresentation
      verifiableCredential:(java.util.ArrayList)
         [0]:com.webank.weid.protocol.base.CredentialPojo
            context: https://github.com/WeBankFinTech/WeIdentity/blob/master/context/v1
            id: 67598cc5-a922-4e9f-ae0a-90c6285a8236
            cptId: 1017
            issuer: did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7
            issuanceDate: 1560425696276
            expirationDate: 1560475761684
            claim:(java.util.HashMap)
               gender: 0x8dba4ce05ca123e0c48b877f461e1b8c362fcab9d03330dcb80d7d039081f50b
               name: zhangsan
               age: 0xdeb5a47d7ab03d9fefe2169cc59db146cec6f24005bcf0b2e2a0c95bfe7adde5
            proof:(java.util.HashMap)
               creator: did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0
               salt:(java.util.HashMap)
                  gender: 0
                  name: yjckg
                  age: 0
               created: 1560425696276
               type: Secp256k1
               signatureValue: HCgmoX0f7ZYkwpd+pJ2+RCRKNt5lf9nbl8g9YWTSuA32IIoRSjMr7GPZVbe5bcu+hD/pnkAJbbinJo4/YqOOync=
            type:(java.util.ArrayList)
               [0]:VerifiableCredential
      proof:(java.util.HashMap)
         created: 1560425696412
         type: Secp256k1
         verificationMethod: did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0
         nonce: DJulONVxD2TFidB8vaYH
         signatureValue: G8ivS1e625NT8qSzLEugbqkRW6HDJNA4Lfcl7uCXV+uEffPMVF6Bwnk0pyCOd+4bbw90pMaj+EVxeL79acYPzM4=
   errorCode: 0
   errorMessage: success
   transactionInfo:null


**时序图**

.. mermaid::


   sequenceDiagram
   participant 调用者
   participant CredentialPojoService
   调用者->>CredentialPojoService: 调用verify()
   CredentialPojoService->>CredentialPojoService: 入参非空、格式及合法性检查
   opt 入参校验失败
   CredentialPojoService-->>调用者: 报错，提示参数不合法并退出
   end
   loop 遍历credentialPojo列表
   CredentialPojoService->>CredentialPojoService: 根据credentialPojo中的cptId获取对应的claimPolicy
   opt claimPolicy
   CredentialPojoService-->>调用者: continue
   end
   CredentialPojoService->>CredentialPojoService: 根据claimPolicy，调用createSelectiveCredential()方法，做选择性披露
   opt 选择性披露失败
   CredentialPojoService-->>调用者: 失败退出
   end
   end
   CredentialPojoService->>CredentialPojoService: 设置context等元数据属性
   CredentialPojoService->>CredentialPojoService: 对presentation整体签名，放入proof结构，同时设置其他proof属性，用于验证
   CredentialPojoService-->>调用者: 返回成功

----


9. getCredentialPojoHash
~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.getCredentialPojoHash
   接口定义:ResponseData<String> getCredentialHash(CredentialPojo args)
   接口描述: 传入CredentialPojo信息生成CredentialPojo整体的Hash值，一般在生成Evidence时调用。

**接口入参**\ :   com.webank.weid.protocol.base.CredentialPojo


com.webank.weid.protocol.base.CredentialPojo

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
     -
     -
   * - type
     - List<String>
     - Y
     -
     -
   * - id
     - String
     - Y
     - 证书ID
     -
   * - cptId
     - Integer
     - Y
     - cptId
     -
   * - issuer
     - String
     - Y
     - issuer 的 WeIdentity DID
     -
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
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
     - 返回结果值
     -
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


**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_EXPIRED
     - 100402
     - 过期
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名破坏
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
   * - CREDENTIAL_EXPIRE_DATE_ILLEGAL
     - 100409
     - 到期日期格式非法
   * - CREDENTIAL_CLAIM_NOT_EXISTS
     - 100410
     - Claim数据不能为空
   * - CREDENTIAL_ID_NOT_EXISTS
     - 100412
     - ID为空
   * - CREDENTIAL_CONTEXT_NOT_EXISTS
     - 100413
     - context为空
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - CREDENTIAL_SIGNATURE_TYPE_ILLEGAL
     - 100429
     - 验证签名类型异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java


   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
   CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs = new CreateCredentialPojoArgs<Map<String, Object>>();
   createCredentialPojoArgs.setCptId(1017);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

   Map<String, Object> claim = new HashMap<String, Object>();
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 22);
   createCredentialPojoArgs.setClaim(claim);

   //创建CredentialPojo
   ResponseData<CredentialPojo> response = credentialPojoService.createCredential(createCredentialPojoArgs);

   ResponseData<String> resp = credentialPojoService.getCredentialPojoHash(response.getResult());

.. code-block:: text

   返回结果如：
   result: 0x06173e4b714d57565ae5ddf23c4e84cb0a9824cb72eab476303d2dd1cc0a4728
   errorCode: 0
   errorMessage: success
   transactionInfo:null


**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant CredentialPojoService
   调用者->>CredentialPojoService: 调用GetCredentialPojoHash()
   CredentialPojoService->>CredentialPojoService: 入参非空、格式及合法性检查
   opt 入参校验失败
   CredentialPojoService-->>调用者: 报错，提示参数不合法并退出
   end
   CredentialPojoService->>CredentialPojoService: 生成凭证Hash
   CredentialPojoService-->>调用者: 返回凭证Hash


10. addSignature
~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.addSignature
   接口定义:ResponseData<Credential> addSignature(List<Credential> credentialList, WeIdPrivateKey weIdPrivateKey)
   接口描述:多签，在原凭证列表的基础上，创建包裹成一个新的多签凭证，由传入的私钥所签名。此凭证的CPT为一个固定值。在验证一个多签凭证时，会迭代验证其包裹的所有子凭证。本接口不支持创建选择性披露的多签凭证。

**接口入参**\ :   java.util.ArrayList


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
     - 证书ID
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
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
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
     -
     - 见下
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
     - 证书ID
     -
   * - cptId
     - Integer
     - Y
     - cptId
     - 默认为106
   * - issuer
     - String
     - Y
     - WeIdentity DID
     -
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
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
     - JsonSchema无效
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_ERROR
     - 100400
     - Credential标准错误
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
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
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();

   HashMap<String, Object> claim = new HashMap<String, Object>(3);
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 18);

   CreateCredentialPojoArgs createCredentialPojoArgs = new CreateCredentialPojoArgs();
   createCredentialPojoArgs.setClaim(claim);
   createCredentialPojoArgs.setCptId(1017);
   createCredentialPojoArgs.setExpirationDate(1551448312461L);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   createCredentialPojoArgs.setWeIdPrivateKey(weIdPrivateKey);

   ResponseData<CredentialWrapper> response = credentialPojoService.createCredentialPojo(createCredentialArgs);
   List<CredentialPojo> credList = new ArrayList<>();
   credList.add(response.getResult().getCredentialPojo());
   Long expirationDate = DateUtils.convertToNoMillisecondTimeStamp(
      createCredentialPojoArgs.getExpirationDate() + 24 * 60 * 60);
   createCredentialPojoArgs.setExpirationDate(expirationDate);
   CredentialPojo tempCredential =
       credentialPojoService.createCredentialPojo(createCredentialPojoArgs).getResult().getCredentialPojo();
   credentialList.add(tempCredential);
   ResponseData<CredentialPojo> multiSignedResp = credentialService.addSignature(credList, weIdPrivateKey);
   System.out.println(multiSignedResp);

.. code-block:: text

   返回结果如：
   result:(com.webank.weid.protocol.base.CredentialPojo)
      credentialPojo:(com.webank.weid.protocol.base.CredentialPojo) {
      {
        "claim": {
          "credentialList": [
            {
              "claim": {
                "age": 1,
                "gender": "F",
                "id": "did:weid:1000:1:0xa4c2666560499868baf1906941f806b6d1c26e33",
                "name": "1"
              },
              "context": "https:\/\/github.com\/WeBankFinTech\/WeIdentity\/blob\/master\/context\/v1",
              "cptId": 2000087,
              "expirationDate": 1567491752,
              "id": "6ea6e209-10e9-4a93-b6be-12af1a32655b",
              "issuanceDate": 1567405352,
              "issuer": "did:weid:1000:1:0xa4c2666560499868baf1906941f806b6d1c26e33",
              "proof": {
                "created": 1567405352,
                "creator": "did:weid:1000:1:0xa4c2666560499868baf1906941f806b6d1c26e33#keys-0",
                "salt": {
                  "age": "yOwN7",
                  "gender": "jjB85",
                  "id": "BmRYI",
                  "name": "BjYqF"
                },
                "signatureValue": "G+SNG3rBZNDvRNgRtJugPtX1FmE8XJIkV4CGPK\/nt\/breIPMJ5wYxImTp2QAxBUe5HMwCe9PPGhhMJJAazM5u9k=",
                "type": "Secp256k1"
              },
              "type": [
                "VerifiableCredential"
              ]
            },
            {
              "claim": {
                "age": 1,
                "gender": "F",
                "id": "did:weid:1000:1:0x309320a01f215a380c6950e80a89181ad8a8cd53",
                "name": "1"
              },
              "context": "https:\/\/github.com\/WeBankFinTech\/WeIdentity\/blob\/master\/context\/v1",
              "cptId": 2000087,
              "expirationDate": 1567491842,
              "id": "a3544a9c-6cb6-4688-9622-bb935fb0d93f",
              "issuanceDate": 1567405355,
              "issuer": "did:weid:1000:1:0x309320a01f215a380c6950e80a89181ad8a8cd53",
              "proof": {
                "created": 1567405355,
                "creator": "did:weid:1000:1:0x309320a01f215a380c6950e80a89181ad8a8cd53#keys-0",
                "salt": {
                  "age": "5nImi",
                  "gender": "Me224",
                  "id": "5pYs2",
                  "name": "z6VmW"
                },
                "signatureValue": "HC8OAG\/dRmteGSIGWIDekp8fC1KJI8EEDZBb29HiTLXvVj350l9yTOHeGSBCr2VRY\/DSHT5ONjlvcrO4Mqa3Auo=",
                "type": "Secp256k1"
              },
              "type": [
                "VerifiableCredential"
              ]
            }
          ]
        },
        "context": "https:\/\/github.com\/WeBankFinTech\/WeIdentity\/blob\/master\/context\/v1",
        "cptId": 107,
        "expirationDate": 1567491842,
        "id": "ad5d5a54-4574-4b3b-b1df-9d0687b6a0ac",
        "issuanceDate": 1567405359,
        "issuer": "did:weid:1000:1:0x4e9a111867ed6370e1e23f7a79426f6649eb78c6",
        "proof": {
          "created": 1567405359,
          "creator": "did:weid:1000:1:0x4e9a111867ed6370e1e23f7a79426f6649eb78c6#keys-0",
          "salt": {
            "credentialList": ""
          },
          "signatureValue": "HC1y3rfyb\/2sg+E2Uulczm8VDtmQ6VrU\/9ow4e4nP3lVUOv4Gz41pfBrJHnV4wQoUbQsCYpezFx5sdaUwUILV1I=",
          "type": "Secp256k1"
        },
        "type": [
          "VerifiableCredential"
        ]
      }
   errorCode: 0
   errorMessage: success
   transactionInfo:null


**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant CredentialPojoService
   调用者->>CredentialPojoService: 调用addSignature()
   CredentialPojoService->>CredentialPojoService: 入参非空、格式及合法性检查
   opt 入参校验失败
   CredentialPojoService-->>调用者: 报错，提示参数不合法并退出
   end
   CredentialPojoService->>CredentialPojoService: 生成签发日期、以原凭证列表为Claim生成数字签名
   CredentialPojoService-->>调用者: 返回凭证



11. createTrustedTimestamp
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.createTrustedTimestamp
   接口定义:ResponseData<CredentialPojo> createTrustedTimestamp(List<CredentialPojo> credentialList, WeIdAuthentication weIdAuthentication)
   接口描述: 使用第三方可信时间戳服务，创建一个可信时间戳凭证。

.. note::
     注意：本服务需要您先行配置好时间戳服务的相关参数，请参见\ `时间戳服务配置步骤 <https://weidentity.readthedocs.io/zh_CN/latest/docs/faq-java.html>`__\ 。当前，可信时间戳服务支持使用WeSign（微鉴证）集成。

.. note::
     注意：创建可信时间戳凭证的输入参数是一个凭证list。当前，因为一些技术限制，还不支持对**已经选择性披露的凭证**进行可信时间戳的创建。也就是说，如果您传入的凭证list里面有任何一个凭证是选择性披露的，那么创建将会失败。

.. note::
     注意：对于已经创建好的可信时间戳凭证，您可以通过调用createSelectiveCredential对其进行选择性披露。

**接口入参**\ :   java.util.ArrayList


com.webank.weid.protocol.base.CredentialPojo

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
     - 证书ID
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
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
     -

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
     - Y
     - 公钥Id
     -
   * - weIdPrivateKey
     - WeIdPrivateKey
     - Y
     -
     - 交易私钥，见下


**接口返回**\ :    com.webank.weid.protocol.response.ResponseData\<CredentialPojo>;

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
     -
     - 见下
   * - transactionInfo
     - TransactionInfo
     - 交易信息
     -


com.webank.weid.protocol.base.CredentialPojo

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
     - 证书ID
     -
   * - cptId
     - Integer
     - Y
     - cptId
     - 默认为106
   * - issuer
     - String
     - Y
     - WeIdentity DID
     -
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
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
     - JsonSchema无效
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_ERROR
     - 100400
     - Credential标准错误
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
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
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - TIMESTAMP_SERVICE_BASE_ERROR
     - 100433
     - 时间戳服务一般错误，请参照log检查具体错误
   * - CREDENTIAL_SYSTEM_CPT_CLAIM_VERIFY_ERROR
     - 100434
     - 凭证签名验证通过，但是内部系统CPT内容验证失败
   * - TIMESTAMP_SERVICE_UNCONFIGURED
     - 100435
     - 时间戳服务未配置
   * - TIMESTAMP_SERVICE_WESIGN_ERROR
     - 100436
     - 时间戳服务微鉴证侧出错，请参照log检查具体错误
   * - TIMESTAMP_VERIFICATION_FAILED
     - 100437
     - 时间戳验证不通过（可能是hash值/时间/时间戳签名任一错误）
   * - TIMESTAMP_CREATION_FAILED_FOR_SELECTIVELY_DISCLOSED
     - 100438
     - 时间戳服务不支持对已经选择性披露的凭证进行创建时间戳
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空

**调用示例**

.. code-block:: java

   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();

   HashMap<String, Object> claim = new HashMap<String, Object>(3);
   claim.put("name", "zhang san");
   claim.put("gender", "F");
   claim.put("age", 18);

   CreateCredentialPojoArgs createCredentialPojoArgs = new CreateCredentialPojoArgs();
   createCredentialPojoArgs.setClaim(claim);
   createCredentialPojoArgs.setCptId(1017);
   createCredentialPojoArgs.setExpirationDate(1551448312461L);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   createCredentialPojoArgs.setWeIdPrivateKey(weIdPrivateKey);

   ResponseData<CredentialWrapper> response = credentialPojoService.createCredentialPojo(createCredentialArgs);
   List<CredentialPojo> credList = new ArrayList<>();
   credList.add(response.getResult().getCredentialPojo());
   Long expirationDate = DateUtils.convertToNoMillisecondTimeStamp(
      createCredentialPojoArgs.getExpirationDate() + 24 * 60 * 60);
   createCredentialPojoArgs.setExpirationDate(expirationDate);
   CredentialPojo tempCredential =
       credentialPojoService.createCredentialPojo(createCredentialPojoArgs).getResult().getCredentialPojo();
   credentialList.add(tempCredential);
   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);
   ResponseData<CredentialPojo> trustedCred = credentialService.createTrustedTimestamp(credList, weIdAuthentication);
   System.out.println(trustedCred);

.. code-block:: text

   返回结果如：
   result:(com.webank.weid.protocol.base.CredentialPojo)
      credentialPojo:(com.webank.weid.protocol.base.CredentialPojo) {
      {
        "claim": {
          "credentialList": [
            {
              "claim": {
                "age": 1,
                "gender": "F",
                "id": "did:weid:1000:1:0xa4c2666560499868baf1906941f806b6d1c26e33",
                "name": "1"
              },
              "context": "https:\/\/github.com\/WeBankFinTech\/WeIdentity\/blob\/master\/context\/v1",
              "cptId": 2000087,
              "expirationDate": 1567491752,
              "id": "6ea6e209-10e9-4a93-b6be-12af1a32655b",
              "issuanceDate": 1567405352,
              "issuer": "did:weid:1000:1:0xa4c2666560499868baf1906941f806b6d1c26e33",
              "proof": {
                "created": 1567405352,
                "creator": "did:weid:1000:1:0xa4c2666560499868baf1906941f806b6d1c26e33#keys-0",
                "salt": {
                  "age": "yOwN7",
                  "gender": "jjB85",
                  "id": "BmRYI",
                  "name": "BjYqF"
                },
                "signatureValue": "G+SNG3rBZNDvRNgRtJugPtX1FmE8XJIkV4CGPK\/nt\/breIPMJ5wYxImTp2QAxBUe5HMwCe9PPGhhMJJAazM5u9k=",
                "type": "Secp256k1"
              },
              "type": [
                "VerifiableCredential"
              ]
            },
            {
              "claim": {
                "age": 1,
                "gender": "F",
                "id": "did:weid:1000:1:0x309320a01f215a380c6950e80a89181ad8a8cd53",
                "name": "1"
              },
              "context": "https:\/\/github.com\/WeBankFinTech\/WeIdentity\/blob\/master\/context\/v1",
              "cptId": 2000087,
              "expirationDate": 1567491842,
              "id": "a3544a9c-6cb6-4688-9622-bb935fb0d93f",
              "issuanceDate": 1567405355,
              "issuer": "did:weid:1000:1:0x309320a01f215a380c6950e80a89181ad8a8cd53",
              "proof": {
                "created": 1567405355,
                "creator": "did:weid:1000:1:0x309320a01f215a380c6950e80a89181ad8a8cd53#keys-0",
                "salt": {
                  "age": "5nImi",
                  "gender": "Me224",
                  "id": "5pYs2",
                  "name": "z6VmW"
                },
                "signatureValue": "HC8OAG\/dRmteGSIGWIDekp8fC1KJI8EEDZBb29HiTLXvVj350l9yTOHeGSBCr2VRY\/DSHT5ONjlvcrO4Mqa3Auo=",
                "type": "Secp256k1"
              },
              "type": [
                "VerifiableCredential"
              ]
            }
          ]
        "timestampAuthority": "wesign",
        "authoritySignature": "MhmbHC1y3rfyb\/2sg+E2Uulczm8VDtmQ6VrU\/9ow4e4nP3lVUOv4Gz41pfBrJHnV4wQoUbQsCYpezFx5sdaUwUILV1I=HC1y3rfyb\/2sg+E2Uulczm8VDtmQ6VrU\/9ow4e4nP3lVUOv4Gz41pfBrJHnV4wQoUbQsCYpezFx5sdaUwUILV1I=HC1y3rfyb\/2sg+E2Uulczm8VDtmQ6VrU\/9ow4e4nP3lVUOv4Gz41pfBrJHnV4wQoUbQsCYpezFx5sdaUwUILV1I=a235==",
        "timestamp": 151233113000,
        "claimHash": "0xe3f48648beee61d17de609d32af36ac0bf4d68a9352890b04d53841c4949bd13"
        },
        "context": "https:\/\/github.com\/WeBankFinTech\/WeIdentity\/blob\/master\/context\/v1",
        "cptId": 108,
        "expirationDate": 1567491842,
        "id": "ad5d5a54-4574-4b3b-b1df-9d0687b6a0ac",
        "issuanceDate": 1567405359,
        "issuer": "did:weid:1000:1:0x4e9a111867ed6370e1e23f7a79426f6649eb78c6",
        "proof": {
          "created": 1567405359,
          "creator": "did:weid:1000:1:0x4e9a111867ed6370e1e23f7a79426f6649eb78c6#keys-0",
          "salt": {
            "credentialList": ""
          },
          "signatureValue": "HC1y3rfyb\/2sg+E2Uulczm8VDtmQ6VrU\/9ow4e4nP3lVUOv4Gz41pfBrJHnV4wQoUbQsCYpezFx5sdaUwUILV1I=",
          "type": "Secp256k1"
        },
        "type": [
          "VerifiableCredential"
        ]
      }
   errorCode: 0
   errorMessage: success
   transactionInfo:null


**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant CredentialPojoService
   调用者->>CredentialPojoService: 调用createTrustedTimestamp()
   CredentialPojoService->>CredentialPojoService: 入参非空、格式及合法性检查
   opt 入参校验失败
   CredentialPojoService-->>调用者: 报错，提示参数不合法并退出
   end
   CredentialPojoService->>CredentialPojoService: 生成全部不披露的凭证并生成hash
   CredentialPojoService->>时间戳服务: 发送hash，生成时间戳
   时间戳服务-->>CredentialPojoService: 返回时间戳
   CredentialPojoService->>CredentialPojoService: 生成时间戳凭证
   CredentialPojoService-->>调用者: 返回凭证


----

12. createDataAuthToken
~~~~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.createDataAuthToken
   接口定义:ResponseData<CredentialPojo> createCredential(Cpt101 authInfo, WeIdAuthentication weIdAuthentication)
   接口描述: 根据传入的授权要求信息，生成符合CPT101格式规范的数据授权凭证。该凭证需要被verify之后和Endpoint Service结合使用。

..note::

   注意：使用这个接口的前提是首先需要将CPT 101注册到链上。如果您是新搭了一条WeIdentity 1.6.0+的链，那么搭链过程中这一步已经自动完成了。否则（如您是升级SDK），您需要使用部署WeIdentity合约的私钥（ecdsa_key）去将CPT 101注册到链上。下文的代码范例中我们给出了详细的流程

**接口入参**\ :

com.webank.weid.protocol.cpt.Cpt101

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - fromWeId
     - String
     - Y
     - 发起授权的WeIdentity DID（必须同时是Issuer）
     - 必须在链上存在，且需要传入私钥作为Issuer
   * - toWeId
     - String
     - Y
     - 接受授权的WeIdentity DID
     - 必须在链上存在且和fromWeId不同
   * - serviceUrl
     - String
     - Y
     - 所授权内容在Endpoint Service上注册的service URL
     - 必须是一个包含主机名，端口号，以及端点地址的标准URL
   * - resourceId
     - String
     - Y
     - UUID
     - 用于标识资源的符合UUID格式字符串
   * - duration
     - Long
     - Y
     - 授权有效时间
     - 同时决定了凭证的expirationDate

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
     - WeIdentity DID
     - 必须和fromWeId一致
   * - weIdPublicKeyId
     - String
     - N
     - 公钥Id
     -
   * - weIdPrivateKey
     - WeIdPrivateKey
     - Y
     -
     - 交易私钥，必须和fromWeId在链上所公开的某个公钥一致

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
     - 私钥
     - 使用十进制数字表示

**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<CredentialPojo>;

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
     - CredentialPojo
     - 凭证对象
     - 业务数据
   * - transactionInfo
     - TransactionInfo
     - 交易信息
     -

com.webank.weid.protocol.base.CredentialPojo

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - context
     - String
     -
     -
   * - type
     - List<String>
     -
     -
   * - id
     - String
     - 证书ID
     -
   * - cptId
     - Integer
     - cptId
     -
   * - issuer
     - String
     - issuer 的 WeIdentity DID
     -
   * - issuanceDate
     - Long
     - 创建日期
     -
   * - expirationDate
     - Long
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - 签名数据结构体
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
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - WEID_PRIVATEKEY_DOES_NOT_MATCH
     - 100106
     - 私钥和weid不匹配
   * - CREDENTIAL_ERROR
     - 100400
     - credential处理未知异常
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
   * - CREDENTIAL_EXPIRE_DATE_ILLEGAL
     - 100409
     - 到期日期无效
   * - CREDENTIAL_CLAIM_NOT_EXISTS
     - 100410
     - Claim数据不能为空
   * - CREDENTIAL_CLAIM_DATA_ILLEGAL
     - 100411
     - Claim非法
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - AUTHORIZATION_FROM_TO_MUST_BE_DIFFERENT
     - 100450
     - fromWeId和toWeId必须不同
   * - AUTHORIZATION_CANNOT_AUTHORIZE_OTHER_WEID_RESOURCE
     - 100451
     - fromWeId必须和Issuer相同
   * - ILLEGAL_INPUT
     - 160004
     - 参数非法

**调用示例**

.. code-block:: java

   // Enforce a Register/Update system CPT first
   WeIdAuthentication sdkAuthen = new WeIdAuthentication();
   ECKeyPair keyPair = ECKeyPair.create(new BigInteger(privateKey));
   String keyWeId = WeIdUtils
       .convertAddressToWeId(new Address(Keys.getAddress(keyPair)).toString());
   sdkAuthen.setWeId(keyWeId);
   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey(privateKey);
   sdkAuthen.setWeIdPrivateKey(weIdPrivateKey);
   if (!weIdService.isWeIdExist(keyWeId).getResult()) {
       CreateWeIdArgs wargs = new CreateWeIdArgs();
       wargs.setWeIdPrivateKey(weIdPrivateKey);
       wargs.setPublicKey(keyPair.getPublicKey().toString(10));
       weIdService.createWeId(wargs);
   }
   String cptJsonSchema = DataToolUtils
       .generateDefaultCptJsonSchema(Class.forName("com.webank.weid.protocol.cpt.Cpt101"));
   CptStringArgs args = new CptStringArgs();
   args.setCptJsonSchema(cptJsonSchema);
   args.setWeIdAuthentication(sdkAuthen);
   if (cptService.queryCpt(CredentialConstant.AUTHORIZATION_CPT).getResult() == null) {
       cptService.registerCpt(args, CredentialConstant.AUTHORIZATION_CPT);
   } else {
       cptService.updateCpt(args, CredentialConstant.AUTHORIZATION_CPT);
   }

   // Init params
   Cpt101 authInfo = new Cpt101();
   authInfo.setFromWeId(createWeIdResultWithSetAttr.getWeId());
   String toWeId = this.createWeIdWithSetAttr().getWeId();
   authInfo.setToWeId(toWeId);
   authInfo.setDuration(360000L);
   authInfo.setResourceId(UUID.randomUUID().toString());
   authInfo.setServiceUrl("http://127.0.0.1:6011/fetch-data");
   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId(createWeIdResultWithSetAttr.getWeId());
   weIdAuthentication.setWeIdPrivateKey(createWeIdResultWithSetAttr.getUserWeIdPrivateKey());
   weIdAuthentication.setWeIdPublicKeyId(createWeIdResultWithSetAttr.getWeId() + "#keys-0");

   // Create and check
   ResponseData<CredentialPojo> authTokenCredResp = credentialPojoService
       .createDataAuthToken(authInfo, weIdAuthentication);
   System.out.println(DataToolUtils.deserialize(authTokenCredResp.getResult()));

.. code-block:: text

   返回结果如：
   result:(com.webank.weid.protocol.base.CredentialPojo)
      {
          "claim": {
              "duration": 360000,
              "fromWeId": "did:weid:101:0x69cd071e4be5fd878e1519ff476563dc2f4c6168",
              "resourceId": "4b077c17-9612-42ee-9e36-3a3d46b27e81",
              "serviceUrl": "http://127.0.0.1:6010/fetch-data",
              "toWeId": "did:weid:101:0x68bedb2cbe55b4c8e3473faa63f121c278f6dba9"
          },
          "context": "https://github.com/WeBankFinTech/WeIdentity/blob/master/context/v1",
          "cptId": 101,
          "expirationDate": 1581347039,
          "id": "48b75424-9411-4d22-b925-4e730b445a31",
          "issuanceDate": 1580987039,
          "issuer": "did:weid:101:0x69cd071e4be5fd878e1519ff476563dc2f4c6168",
          "proof": {
              "created": 1580987039,
              "creator": "did:weid:101:0x69cd071e4be5fd878e1519ff476563dc2f4c6168#keys-0",
              "salt": {
                  "duration": "fmk5A",
                  "fromWeId": "DEvFy",
                  "resourceId": "ugVeN",
                  "serviceUrl": "nVdeE",
                  "toWeId": "93Z1E"
              },
              "signatureValue": "HCZwyTzGst87cjCDaUEzPrO8QRlsPvCYXvRTUVBUTDKRSoGDgu4h4HLrMZ+emDacRnmQ/yke38u1jBnilNnCh6c=",
              "type": "Secp256k1"
          },
          "type": ["VerifiableCredential", "hashTree"]
      }
   errorCode: 0
   errorMessage: success
   transactionInfo:null



**时序图**

.. mermaid::

   sequenceDiagram
   participant 调用者
   participant CredentialPojoService
   调用者->>CredentialPojoService: 调用createDataAuthToken()
   CredentialPojoService->>CredentialPojoService: 入参非空、格式及合法性检查
   opt 入参校验失败
   CredentialPojoService-->>调用者: 报错，提示参数不合法并退出
   end
   CredentialPojoService->>CredentialPojoService: 组装符合格式的CPT101的Claim
   CredentialPojoService->>CredentialPojoService: 生成签发日期、生成数字签名
   CredentialPojoService-->>调用者: 返回数据授权凭证

13. verify
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.verify
   接口定义: ResponseData<Boolean> verify(String issuerWeId, String weIdPublicKeyId, CredentialPojo credential)
   接口描述: 通过传入的Issuer的WeID，并指定其链上公钥ID，验证credential。若验证失败，则会遍历所有公钥；如果能够找到一个适配的，那么就返回验证成功但公钥ID不匹配。

**接口入参**\ :

java.lang.String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - issuerWeId
     - String
     - Y
     - WeIdentity DID
     -
   * - weIdPublicKeyId
     - String
     - Y
     - 公钥ID
     -


com.webank.weid.protocol.base.CredentialPojo

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
     -
     -
   * - type
     - List<String>
     - Y
     -
     -
   * - id
     - String
     - Y
     - 证书ID
     -
   * - cptId
     - Integer
     - Y
     - cptId
     -
   * - issuer
     - String
     - Y
     - issuer 的 WeIdentity DID
     -
   * - issuanceDate
     - Long
     - Y
     - 创建日期
     -
   * - expirationDate
     - Long
     - Y
     - 到期日期
     -
   * - claim
     - Map<String, Object>
     - Y
     - Claim数据
     -
   * - proof
     - Map<String, Object>
     - Y
     - 签名数据结构体
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
     - 验证结果
     - 业务数据
   * - transactionInfo
     - TransactionInfo
     - 交易信息
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
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_ISSUER_MISMATCH
     - 100403
     - issuerWeId跟Credential中的issuer不匹配
   * - CREDENTIAL_CREATE_DATE_ILLEGAL
     - 100408
     - 创建日期格式非法
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
   * - CREDENTIAL_TYPE_IS_NULL
     - 100414
     - type为空
   * - CREDENTIAL_CPT_NOT_EXISTS
     - 100416
     - cpt不存在
   * - CREDENTIAL_WEID_DOCUMENT_ILLEGAL
     - 100417
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
     - 获取weIdDocument异常
   * - CREDENTIAL_SIGNATURE_BROKEN
     - 100405
     - 签名验证不通过
   * - CREDENTIAL_EXCEPTION_VERIFYSIGNATURE
     - 100419
     - 签名验证异常
   * - CREDENTIAL_SIGNATURE_TYPE_ILLEGAL
     - 100429
     - 验证签名类型异常
   * - CREDENTIAL_SALT_ILLEGAL
     - 100430
     - 盐值非法
   * - CREDENTIAL_VERIFY_SUCCEEDED_WITH_WRONG_PUBLIC_KEY_ID
     - 100442
     - 验证成功，但公钥ID与传入值不同
   * - ILLEGAL_INPUT
     - 160004
     - 参数为空


**调用示例**

.. code-block:: java

   CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();
   CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs = new CreateCredentialPojoArgs<Map<String, Object>>();
   createCredentialPojoArgs.setCptId(1017);
   createCredentialPojoArgs.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 100);

   WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
   weIdAuthentication.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");
   weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");
   createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

   Map<String, Object> claim = new HashMap<String, Object>();
   claim.put("name", "zhangsan");
   claim.put("gender", "F");
   claim.put("age", 22);
   createCredentialPojoArgs.setClaim(claim);

   ResponseData<CredentialPojo> response = credentialPojoService.createCredential(createCredentialPojoArgs);

   ResponseData<Boolean> responseVerify = credentialPojoService.verify("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7", "0", response.getResult());


.. code-block:: text

   返回结果如：
   result: true
   errorCode: 0
   errorMessage: success
   transactionInfo:null

----


AmopService
^^^^^^^^^^^^^^^^^

1. registerCallback
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.AmopService.registerCallback
   接口定义:void registerCallback(Integer directRouteMsgType, AmopCallback directRouteCallback)
   接口描述: 注册AMOP回调处理。


**接口入参**\ : 

java.lang.Integer

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - directRouteMsgType
     - Integer
     - Y
     - AMOP消息类型
     - 


com.webank.weid.rpc.callback.AmopCallback

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - object
     - AmopCallback
     - 处理消息的callback对象
     - 机构需继承并且重写onPush(AmopCommonArgs arg)

**接口返回**\ :   无;


----

2. request
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.rpc.AmopService.request
   接口定义: ResponseData<AmopResponse> request(String toOrgId, AmopCommonArgs args)
   接口描述: AMOP请求Server。

**接口入参**\ : 

java.lang.String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - toOrgId
     - String
     - Y
     - 目标机构编码
     - 


com.webank.weid.protocol.amop.AmopCommonArgs

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - messageId
     - String
     - N
     - 消息编号
     - 
   * - fromOrgId
     - String
     - N
     - 消息来源机构编号
     - 
   * - toOrgId
     - String
     - N
     - 消息目标机构编号
     - 
   * - message
     - String
     - Y
     - 请求body
     - 


**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<AmopResponse>;

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
     - AmopResponse
     - AMOP响应
     - 业务数据


com.webank.weid.protocol.response.AmopResponse

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - result
     - String
     - AMOP消息响应body
     - 
   * - errorCode
     - Integer
     - 业务结果编码
     -  
   * - errorMessage
     - String
     - 业务结果描述
     - 
   * - messageId
     - String
     - 消息编号
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
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - DIRECT_ROUTE_REQUEST_TIMEOUT
     - 160009
     - AMOP超时
   * - DIRECT_ROUTE_MSG_BASE_ERROR
     - 160010
     - AMOP异常
     
     
----

3. getPolicyAndChallenge
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.rpc.AmopService.getPolicyAndChallenge
   接口定义: ResponseData<PolicyAndChallenge> getPolicyAndChallenge(String orgId, Integer policyId, String targetUserWeId)
   接口描述: 通过AMOP获取PolicyAndChallenge。

**接口入参**\ : 

java.lang.String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - toOrgId
     - String
     - Y
     - 目标机构编码
     - 


java.lang.Integer

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - policyId
     - String
     - Y
     - 策略编号
     -
     
      
java.lang.String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - targetUserWeId
     - String
     - Y
     - 需要被challenge的WeIdentity DID
     - 
     
     
**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<PolicyAndChallenge>;

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
     - PolicyAndChallenge
     - 
     - 业务数据

com.webank.weid.protocol.base.PolicyAndChallenge

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - presentationPolicyE
     - PresentationPolicyE
     - 策略信息
     - 
   * - challenge
     - Challenge
     - 
     - 
     
      
com.webank.weid.protocol.base.PresentationPolicyE

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - id
     - Integer
     - polcyId
     - 策略编号
   * - orgId
     - String
     - 机构编号
     - 
   * - version
     - Integer
     - 版本
     -  
   * - policyPublisherWeId
     - String
     - WeIdentity DID
     - 创建policy机构的WeIdentity DID
   * - policy
     - Map<Integer, ClaimPolicy>
     - 策略配置
     - key:CPTID, value:披露策略对象
   * - extra
     - Map<String, String>
     - 扩展字段
     -  


com.webank.weid.protocol.base.Challenge

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - weId
     - String
     - WeIdentity DID
     - policy提供给指定的WeIdentity DID
   * - version
     - Integer
     - 版本
     -  
   * - nonce
     - String
     - 随机字符串
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
   * - POLICY_SERVICE_NOT_EXISTS
     - 100701
     - policyService不存在
   * - POLICY_SERVICE_CALL_FAIL
     - 100702
     - policyService调用未知异常
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数非法
   * - DIRECT_ROUTE_REQUEST_TIMEOUT
     - 160009
     - AMOP超时
   * - DIRECT_ROUTE_MSG_BASE_ERROR
     - 160010
     - AMOP异常
----


4. requestPolicyAndPreCredential
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.rpc.AmopService.requestPolicyAndPreCredential
   接口定义: ResponseData<PolicyAndChallenge> requestPolicyAndPreCredential(String orgId, GetPolicyAndPreCredentialArgs args)
   接口描述: 通过AMOP获取PolicyAndChallenge和preCredential，在用户向issuer请求发zkp类型的credential时调用。

**接口入参**\ : 

java.lang.String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - toOrgId
     - String
     - Y
     - 目标机构编码
     - 


com.webank.weid.protocol.amop.GetPolicyAndPreCredentialArgs

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - policyId
     - String
     - Y
     - 策略编号
     - 
   * - targetUserWeId
     - String
     - Y
     - 目前用户WeID
     - 
   * - cptId
     - String
     - Y
     - CPT 编号
     - 
   * - claim
     - String
     - Y
     - 用户claim
     - 

     
     
**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<PolicyAndPreCredentialResponse>;

com.webank.weid.protocol.base.PolicyAndPreCredential
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
     - PolicyAndChallenge
     - 
     - 业务数据
   * - preCredential
     - CredentialPojo
     - 
     - 基于CPT 110的元数据的Credential
   * - extra
     - Map
     - 
     - 附加信息

com.webank.weid.protocol.base.PolicyAndChallenge

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - presentationPolicyE
     - PresentationPolicyE
     - 策略信息
     - 
   * - challenge
     - Challenge
     - 
     - 
     
      
com.webank.weid.protocol.base.PresentationPolicyE

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - id
     - Integer
     - polcyId
     - 策略编号
   * - orgId
     - String
     - 机构编号
     - 
   * - version
     - Integer
     - 版本
     -  
   * - policyPublisherWeId
     - String
     - WeIdentity DID
     - 创建policy机构的WeIdentity DID
   * - policy
     - Map<Integer, ClaimPolicy>
     - 策略配置
     - key:CPTID, value:披露策略对象
   * - extra
     - Map<String, String>
     - 扩展字段
     -  


com.webank.weid.protocol.base.Challenge

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - weId
     - String
     - WeIdentity DID
     - policy提供给指定的WeIdentity DID
   * - version
     - Integer
     - 版本
     -  
   * - nonce
     - String
     - 随机字符串
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
   * - POLICY_SERVICE_NOT_EXISTS
     - 100701
     - policyService不存在
   * - POLICY_SERVICE_CALL_FAIL
     - 100702
     - policyService调用未知异常
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数非法
   * - DIRECT_ROUTE_REQUEST_TIMEOUT
     - 160009
     - AMOP超时
   * - DIRECT_ROUTE_MSG_BASE_ERROR
     - 160010
     - AMOP异常
----


5. requestIssueCredential
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.rpc.AmopService.requestIssueCredential
   接口定义: ResponseData<PolicyAndChallenge> requestIssueCredential(String orgId, RequestIssueCredentialArgs args)
   接口描述: 通过AMOP获取zkp类型的Credential，在用户向issuer请求发zkp类型的credential时调用。

**接口入参**\ : 

java.lang.String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - toOrgId
     - String
     - Y
     - 目标机构编码
     - 


com.webank.weid.protocol.amop.RequestIssueCredentialArgs

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - policyAndPreCredential
     - PolicyAndPreCredential
     - Y
     - policyAndChanllenge和基于元数据的precredential
     - 
   * - credentialList
     - List
     - Y
     - 用户根据policy向issuer提供的credential列表
     - 
   * - claim
     - String
     - Y
     - 用户要填入的claim
     - 
   * - auth
     - WeIdAuthentication
     - Y
     - 用户私钥信息
     - 

com.webank.weid.protocol.base.PolicyAndPreCredential
.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - policyAndChallenge
     - PolicyAndChallenge
     - 
     - 业务数据
   * - preCredential
     - CredentialPojo
     - 
     - 基于CPT 110的元数据的Credential
   * - extra
     - Map
     - 
     - 附加信息

com.webank.weid.protocol.base.PolicyAndChallenge

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - presentationPolicyE
     - PresentationPolicyE
     - 策略信息
     - 
   * - challenge
     - Challenge
     - 
     - 
     
      
com.webank.weid.protocol.base.PresentationPolicyE

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - id
     - Integer
     - polcyId
     - 策略编号
   * - orgId
     - String
     - 机构编号
     - 
   * - version
     - Integer
     - 版本
     -  
   * - policyPublisherWeId
     - String
     - WeIdentity DID
     - 创建policy机构的WeIdentity DID
   * - policy
     - Map<Integer, ClaimPolicy>
     - 策略配置
     - key:CPTID, value:披露策略对象
   * - extra
     - Map<String, String>
     - 扩展字段
     -  


com.webank.weid.protocol.base.Challenge

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - weId
     - String
     - WeIdentity DID
     - policy提供给指定的WeIdentity DID
   * - version
     - Integer
     - 版本
     -  
   * - nonce
     - String
     - 随机字符串
     -     
     
**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<RequestIssueCredentialResponse>;

com.webank.weid.protocol.base.RequestIssueCredentialResponse
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
   * - credentialPojo
     - CredentialPojo
     - 
     - 业务数据
   * - credentialSignature
     - String
     - 
     - credential的签名
   * - issuerNonce
     - String
     - 
     - issuer提供的随机数
     
     
**此方法返回code**

.. list-table::
   :header-rows: 1

   * - enum
     - code
     - desc
   * - SUCCESS
     - 0
     - 成功
   * - POLICY_SERVICE_NOT_EXISTS
     - 100701
     - policyService不存在
   * - POLICY_SERVICE_CALL_FAIL
     - 100702
     - policyService调用未知异常
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
   * - ILLEGAL_INPUT
     - 160004
     - 参数非法
   * - DIRECT_ROUTE_REQUEST_TIMEOUT
     - 160009
     - AMOP超时
   * - DIRECT_ROUTE_MSG_BASE_ERROR
     - 160010
     - AMOP异常
----

6. getEncryptKey
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.rpc.AmopService.getEncryptKey
   接口定义: ResponseData<GetEncryptKeyResponse> getEncryptKey(String toOrgId, GetEncryptKeyArgs args)
   接口描述: 通过AMOP获取密钥数据。

**接口入参**\ : 

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - toOrgId
     - String
     - Y
     - 目标机构编码
     - 
   * - args
     - GetEncryptKeyArgs
     - Y
     - 密钥请求数据
     - 
 
 
com.webank.weid.protocol.amop.GetEncryptKeyArgs

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - keyId
     - String
     - Y
     - 用于获取数据的Id
     - 
   * - version
     - Version
     - Y
     - sdk版本信息
     - 
   * - messageId
     - String
     - Y
     - 消息Id
     - 
   * - fromOrgId
     - String
     - Y
     - 数据来源机构
     - 
   * - toOrgId
     - String
     - Y
     - 数据目标机构
     - 

     
**接口返回**\ :   com.webank.weid.protocol.response.ResponseData\<GetEncryptKeyResponse>;

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
     - GetEncryptKeyResponse
     - 
     - 业务数据

com.webank.weid.protocol.response.GetEncryptKeyResponse

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 说明
     - 备注
   * - encryptKey
     - String
     - 密钥数据
     - 
   * - errorCode
     - Integer
     - 错误码
     - 
   * - errorMessage
     - String
     - 错误描述
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
   * - ENCRYPT_KEY_NOT_EXISTS
     - 100700
     - 无法获取秘钥
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - dataKey无效
   * - UNKNOW_ERROR
     - 160003
     - 未知异常
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
----


JsonTransportation
^^^^^^^^^^^^^^^^^

1. specify
~~~~~~~~~~~~~~~~~~~

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


----

2. serialize
~~~~~~~~~~~~~~~~~~~

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



----

3. deserialize
~~~~~~~~~~~~~~~~~~~

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

----


QrCodeTransportation
^^^^^^^^^^^^^^^^^

1. specify
~~~~~~~~~~~~~~~~~~~

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

----

2. serialize
~~~~~~~~~~~~~~~~~~~

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

----

3. serialize
~~~~~~~~~~~~~~~~~~~

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

----

3. deserialize
~~~~~~~~~~~~~~~~~~~

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


----

BarCodeTransportation
^^^^^^^^^^^^^^^^^

1. specify
~~~~~~~~~~~~~~~~~~~

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

----

2. serialize
~~~~~~~~~~~~~~~~~~~

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

----

3. deserialize
~~~~~~~~~~~~~~~~~~~

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

----

PdfTransportation
^^^^^^^^^^^^^^^^^

1. specify
~~~~~~~~~~~~~~~~~~~

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



----

2. serialize
~~~~~~~~~~~~~~~~~~~

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



----


3. serialize
~~~~~~~~~~~~~~~~~~~

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



----

4. serializeWithTemplate
~~~~~~~~~~~~~~~~~~~

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



----

5. serializeWithTemplate
~~~~~~~~~~~~~~~~~~~

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



----

6. deserialize
~~~~~~~~~~~~~~~~~~~

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


----

CacheManager
^^^^^^^^^^^^^^^^^

1. registerCacheNode
~~~~~~~~~~~~~~~~~~~

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
----

2. registerCacheNode
~~~~~~~~~~~~~~~~~~~

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

CryptoService
^^^^^^^^^^^^^^^^^

1. encrypt
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.crypto.inf.CryptoService.encrypt
   接口定义: public String encrypt(String content, String key) throws EncodeSuiteException;
   接口描述: 根据不同类型加密算法对数据进行加密

.. note::
     注意：目前提供服务的加密算法有CryptoType.AES和CryptoType.ECIES, 加密返回数据为Base64字符串。ECIES加解密请通过build-tools获取libffi_ecies.so和WeDPR-ecies.jar


**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - content
     - String
     - Y
    - 需要加密的数据
     -UTF-8格式数据
   * - key
     - String
     - Y
    - 加密使用的秘钥
     -非对称秘钥请使用Base64处理


**接口返回**\ :   String;

**调用示例**

.. code-block:: java
   
   String key = "abc";
   String original = "123";
   // AES加密
   String encrypt = CryptoServiceFactory.getCryptoService(CryptoType.AES).encrypt(original, key);
   
   // ECIES加密
   key = "APOsCflGTsr7ltZBRRA5WS7KL8FzJ8NquybVadp2GsRVmtzTSEYSgW1i76jLOCTJoUPlB+J0KFTG3WKYoltMll0=";// weid公钥BASE64
   original = "123";
   String encrypt = CryptoServiceFactory.getCryptoService(CryptoType.ECIES).encrypt(original, key);
----

2. decrypt
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.crypto.inf.CryptoService.decrypt
   接口定义: public String decrypt(String content, String key) throws EncodeSuiteException;
   接口描述: 根据加密的Base64字符串进行解密，并返回原字符串

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - content
     - String
     - Y
     - 待解密字符串
     -加密后并使用Base64处理的数据
   * - key
     - String
     - Y
     - 解密数据所使用的秘钥
     -非对称秘钥请使用Base64处理

**接口返回**\ :   String;

**调用示例**

.. code-block:: java

   String key = "abc"; //AES秘钥
   String encrypt = "xxxx";//密文数据
   // AES解密
   String decrypt = CryptoServiceFactory.getCryptoService(CryptoType.AES).decrypt(encrypt, key);
   
   key = "AMcwy+851eDtxY/1vcTtxttwqTaBfczp7Q7fL41fGCag"; // weid私钥BASE64
   encrypt = "xxxx";//密文数据
   // AES解密
   String decrypt = CryptoServiceFactory.getCryptoService(CryptoType.ECIES).decrypt(encrypt, key);
----

异常场景对接口的影响
---------------------

- mysql连接异常受影响接口：Persistence相关和serialize密文。

- 节点连接异常受影响接口：createWeId，createEvidence，registerAuthorityIssuer，RegisterIssuerType，RegisterCpt，VerifyCredentialWithSpecifiedPubKey，GetWeIdDocument，verifyLiteCredential。

- 节点连接正常，但不满足3f+1受影响接口：createWeId，createEvidence，registerAuthorityIssuer，RegisterIssuerType，RegisterCpt。

