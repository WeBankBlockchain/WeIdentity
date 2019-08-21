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
#. 生成凭证：通过CredentialService的CreateCredential()，根据CPT模板，生成一份Credential；
#. 查询凭证：调用CredentialService的VerifyCredential()，验证此Credential是否合法；
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
   String policyJson = "{\"extra\" : {\"extra1\" : \"\",\"extra2\" : \"\"},\"id\" : 123456,\"version\" : 1,\"orgId\" : \"webank\",\"weId\" : \"did:weid:0x0231765e19955fc65133ec8591d73e9136306cd0\",\"policy\" : {\"1017\" : {\"fieldsToBeDisclosed\" : {\"gender\" : 0,\"name\" : 1,\"age\" : 0}}}}";
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
   String policyJson = "{\"extra\" : {\"extra1\" : \"\",\"extra2\" : \"\"},\"id\" : 123456,\"version\" : 1,\"orgId\" : \"webank\",\"weId\" : \"did:weid:0x0231765e19955fc65133ec8591d73e9136306cd0\",\"policy\" : {\"1017\" : {\"fieldsToBeDisclosed\" : {\"gender\" : 0,\"name\" : 1,\"age\" : 0}}}}";
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
   String policyJson = "{\"extra\" : {\"extra1\" : \"\",\"extra2\" : \"\"},\"id\" : 123456,\"version\" : 1,\"orgId\" : \"webank\",\"weId\" : \"did:weid:0x0231765e19955fc65133ec8591d73e9136306cd0\",\"policy\" : {\"1017\" : {\"fieldsToBeDisclosed\" : {\"gender\" : 0,\"name\" : 1,\"age\" : 0}}}}";
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
   String policyJson = "{\"extra\" : {\"extra1\" : \"\",\"extra2\" : \"\"},\"id\" : 123456,\"version\" : 1,\"orgId\" : \"webank\",\"weId\" : \"did:weid:0x0231765e19955fc65133ec8591d73e9136306cd0\",\"policy\" : {\"1017\" : {\"fieldsToBeDisclosed\" : {\"gender\" : 0,\"name\" : 1,\"age\" : 0}}}}";
   PresentationPolicyE presentationPolicyE = PresentationPolicyE.fromJson(policyJson);
    
   //创建Presentation
   ResponseData<PresentationE>  presentationERes = credentialPojoService.createPresentation(credentialList, presentationPolicyE, challenge, weIdAuthentication);
   
   //将非policy要求的Credential添加到presentation中
   ResponseData<CredentialPojo> responseNew = credentialPojoService.createCredential(createCredentialPojoArgs);
   presentationERes.getResult().push(responseNew.getResult());
   presentationERes.getResult().commit(weIdAuthentication)


接口简介
--------

整体上，WeIdentity Java SDK包括五个主要的接口，它们分别是：WeIdService、AuthorityIssuerService、CptService、CredentialService、EvidenceService。


* WeIdService

WeIdentity DID相关功能的核心接口。

本接口提供WeIdentity DID的创建、获取信息、设置属性等相关操作。


* AuthorityIssuerService

在WeIdentity的整体架构中，存在着可信的“授权机构”这一角色。一般来说，授权机构特指那些广为人知的、具有一定公信力的、并且有相对频繁签发Credential需求的实体。

本接口提供了对这类授权签发Credential的机构的注册、移除、查询信息等操作。


* CptService

任何凭证的签发，都需要将数据转换成已经注册的CPT (Claim Protocol Type)格式规范，也就是所谓的“标准化格式化数据”。相关机构事先需要注册好CPT，在此之后，签发机构会根据CPT提供符合格式的数据，进而进行凭证的签发。

本接口提供了对CPT的注册、更新、查询等操作。


* CredentialService

凭证签发相关功能的核心接口。

本接口提供凭证的签发和验证操作。


* CredentialPojoService

凭证签发相关功能的核心接口(操作Pojo)。

本接口提供凭证的签发和验证操作。


* EvidenceService

凭证存证上链的相关接口。

本接口提供凭证的Hash存证的生成上链、链上查询及校验等操作。


* AmopService

AMOP通讯相关接口。

本接口提供AMOP的请求和注册。


* Persistence

数据持久化接口，默认为MySql存储操作处理。

本接口提供K-V方式的数据存储服务。


接口列表
--------

WeIDService
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
     - 格式: did:weid:0x………………….
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
     - 如：did:weid:0x………………….
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

3. getWeIdDocumentJson
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

4. getWeIDDocment
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

5. setPublicKey
~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.setPublicKey
   接口定义:ResponseData<Boolean> setPublicKey(SetPublicKeyArgs setPublicKeyArgs)
   接口描述: 根据WeIdentity DID添加公钥。

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
   * - userWeIdPrivateKey
     - WeIdPrivateKey
     - Y
     - 
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

   SetPublicKeyArgs setPublicKeyArgs = new SetPublicKeyArgs();
   setPublicKeyArgs.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   setPublicKeyArgs.setPublicKey(
      "13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   setPublicKeyArgs.setUserWeIdPrivateKey(weIdPrivateKey);

   ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);


.. code-block:: text

   返回结果如下：
   result: true
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
   接口描述: 根据WeIdentity DID添加Service信息。

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
   * - userWeIdPrivateKey
     - WeIdPrivateKey
     - Y
     - 
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

   SetServiceArgs setServiceArgs = new SetServiceArgs();
   setServiceArgs.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   setServiceArgs.setType("drivingCardService");
   setServiceArgs.setServiceEndpoint("https://weidentity.webank.com/endpoint/8377464");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   setServiceArgs.setUserWeIdPrivateKey(weIdPrivateKey);

   ResponseData<Boolean> response = weIdService.setService(setServiceArgs);


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

7. setAuthentication
~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.setAuthentication
   接口定义:ResponseData<Boolean> setAuthentication(SetAuthenticationArgs setAuthenticationArgs)
   接口描述: 根据WeIdentity DID添加认证者。

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
     - 如：did:weid:101:0x....
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
   * - userWeIdPrivateKey
     - WeIdPrivateKey
     - Y
     - 
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

   SetAuthenticationArgs setAuthenticationArgs = new SetAuthenticationArgs();
   setAuthenticationArgs.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   setAuthenticationArgs.setPublicKey(
      "13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   setAuthenticationArgs.setUserWeIdPrivateKey(weIdPrivateKey);

   ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);


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

8. isWeIdExist
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
   接口描述: 创建电子凭证。

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

   返回结果如：
   result:(com.webank.weid.protocol.base.CredentialWrapper)
      credential:(com.webank.weid.protocol.base.Credential)
         context: https://github.com/WeBankFinTech/WeIdentity/blob/master/context/v1
         id: f931b882-00ab-4cb0-9e83-d9bb57212e81
         cptId: 1017
         issuer: did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7
         issuanceDate: 1560416978296
         expirationDate: 1551448312461
         claim:(java.util.HashMap)
            name: zhang san
            gender: F
            age: 18
         proof:(java.util.HashMap)
            creator: did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7
            signature: HHQwJ9eEpyv/BgwtWDveFYAPsKOPtEEWt6ieb28PS76pDwlpFKtbh9Ygog8SUPIXUaWNYS2pLkk4E91hpP8IdbU=
            created: 1560416978296
            type: Secp256k1
      disclosure:(java.util.HashMap)
         name: 1
         gender: 1
         age: 1
   errorCode: 0
   errorMessage: success
   transactionInfo:null


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

----

EvidenceService
^^^^^^^^^^^^^^^^^

1. createEvidence
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.EvidenceService.createEvidence
   接口定义:ResponseData<String> createEvidence(String hashValue, WeIdPrivateKey weIdPrivateKey)
   接口描述: 将Hash值生成存证上链。传入的私钥将会成为链上存证的签名方。此签名方和凭证的Issuer可以不是同一方。

**接口入参**\ :

java.lang.String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - hashValue
     - String
     - Y
     - Hash值
     -
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

   //创建Evidence Address
   ResponseData<String> responseCreateEvidence = evidenceService.createEvidence(credentialService.getCredentialHash(response.getResult().getCredential()), weIdPrivateKey);


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
   接口定义:ResponseData<EvidenceInfo> getEvidence(String evidenceAddress)
   接口描述: 根据传入的凭证存证地址，在链上查找凭证存证信息。


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
   * - signers
     - List<String>
     - 凭证签发者
     - 链上允许存在多个凭证签发者
   * - signatures
     - List<String>
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

   EvidenceService evidenceService = new EvidenceServiceImpl();
   ResponseData<Evidence> response = evidenceService.getEvidence("0xa3203e054bb7a7f0dec134c7510299869e343e8d");


.. code-block:: text

   返回结果如：
   result:(com.webank.weid.protocol.base.EvidenceInfo)
      credentialHash: 0x31c2db44db19ec1af69ed6ad2dc36c7a8068c9871cf1a2bd0e67cb6264531f35
      signers:(java.util.ArrayList)
         [0]:0x39e5e6f663ef77409144014ceb063713b65600e7
      signatures:(java.util.ArrayList)
         [0]:HJoVLhynrqekQWjMEHubd0e5E/J3LLfnWtq3CXpjFaA/Tfj3i0+dDGfa76OqoZhqSuNucXW8f4BZn/Lkd6SPQ/I=
   errorCode: 0
   errorMessage: success
   transactionInfo:null


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

3. verify
~~~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.EvidenceService.verify
   接口定义:ResponseData<Boolean> verify(String hashValue, String evidenceAddress)
   接口描述: 根据传入的存证Hash值和链上值对比，验证其是否遭到篡改。

**接口入参**\ :

java.lang.String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - hashValue
     - String
     - Y
     - Hash值
     -

java.lang.String

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - evidenceAddress
     - String
     - Y
     - 存证地址
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
   * - CPT_ID_ILLEGAL
     - 100303
     - cptId无效
   * - CREDENTIAL_EXPIRED
     - 100402
     - 过期
   * - CREDENTIAL_ISSUER_MISMATCH
     - 100403
     - issuer与签名不匹配
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
   * - CREDENTIAL_WEID_DOCUMENT_ILLEGAL
     - 100417
     - WeIdentity Document为空
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
   * - CREDENTIAL_EXCEPTION_VERIFYSIGNATURE
     - 100419
     -  验证签名异常
   * - CREDENTIAL_SIGNATURE_TYPE_ILLEGAL
     - 100429
     - 验证签名类型异常
   * - CREDENTIAL_EVIDENCE_SIGNATURE_BROKEN
     - 100431
     - 存证签名异常
   * - CREDENTIAL_EVIDENCE_BASE_ERROR
     - 100500
     - Evidence标准错误
   * - CREDENTIAL_EVIDENCE_HASH_MISMATCH
     - 100501
     - Evidence Hash不匹配
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
   createCredentialArgs.setIssuer("did:weid:0x30404b47c6c5811d49e28ea2306c804d16618017");

   WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
   weIdPrivateKey.setPrivateKey("60866441986950167911324536025850958917764441489874006048340539971987791929772");

   createCredentialArgs.setWeIdPrivateKey(weIdPrivateKey);

   //创建Credential
   ResponseData<CredentialWrapper> response = credentialService.createCredential(createCredentialArgs);

   Credential credential = response.getResult().getCredential();
	
   //创建Evidence Address
   ResponseData<String> responseCreateEvidence = evidenceService.createEvidence(credential, weIdPrivateKey);
   
   String evidenceAddress = responseCreateEvidence.getResult();

   //验证Credential by evidenceAddress
   ResponseData<Boolean> responseVerify = evidenceService.verify(credential, evidenceAddress);

   
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
   
   
----


CredentialPojoService
^^^^^^^^^^^^^^^^^

1. createCredential
~~~~~~~~~~~~~~~~~~~

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

2. createSelectiveCredential
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.createSelectiveCredential
   接口定义: ResponseData<CredentialPojo> createSelectiveCredential(CredentialPojo credentialPojo, ClaimPolicy claimPolicy)
   接口描述: 通过原始凭证和披漏策略，创建选择性披露的Credential。

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
     - 根据claim匹配的结构，详见调用示例


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

3. verify
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

4. verify
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.verify
   接口定义: ResponseData<Boolean> verify(WeIdPublicKey issuerPublicKey, CredentialPojo credential)
   接口描述: 使用指定公钥验证credentialWrapper。

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

5. verify
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
   String policyJson = "{\"extra\" : {\"extra1\" : \"\",\"extra2\" : \"\"},\"id\" : 123456,\"version\" : 1,\"orgId\" : \"webank\",\"weId\" : \"did:weid:0x0231765e19955fc65133ec8591d73e9136306cd0\",\"policy\" : {\"1017\" : {\"fieldsToBeDisclosed\" : {\"gender\" : 0,\"name\" : 1,\"age\" : 0}}}}";
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

6. createPresentation
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
   String policyJson = "{\"extra\" : {\"extra1\" : \"\",\"extra2\" : \"\"},\"id\" : 123456,\"version\" : 1,\"orgId\" : \"webank\",\"weId\" : \"did:weid:0x0231765e19955fc65133ec8591d73e9136306cd0\",\"policy\" : {\"1017\" : {\"fieldsToBeDisclosed\" : {\"gender\" : 0,\"name\" : 1,\"age\" : 0}}}}";
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


7. getCredentialPojoHash
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
     - Y
     - 消息编号
     - 
   * - fromOrgId
     - String
     - Y
     - 消息来源机构编号
     - 
   * - toOrgId
     - String
     - Y
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


4. getEncryptKey
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

   JsonTransportation jsonTransportation =TransportationFactory.newJsonTransportation();

   String weId = "did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
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

   String weId = "did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);
   
   PresentationE presentation;
   
   //原文方式调用
   ResponseData<String> result1 = 
       TransportationFactory
           .newJsonTransportation()
           .specify(verifierWeIdList)
           .serialize(presentation,new ProtocolProperty(EncodeType.ORIGINAL));
   
   //密文方式调用
   ResponseData<String> result2 = 
      TransportationFactory
           .newJsonTransportation()
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
   接口定义: <T extends JsonSerializer> ResponseData<T> deserialize(String transString,Class<T> clazz)
   接口描述: 用于反序列化对象,要求目标对象实现JsonSerializer接口。

**接口入参**\ : 

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
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

   String weId = "did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);
   
   String transString="";
   
   //原文方式调用反序列化
   ResponseData<PresentationE> result1 = 
       TransportationFactory
           .newJsonTransportation()
           .specify(verifierWeIdList)
           .deserialize(transString,PresentationE.class);
   
   //密文方式调用反序列化
   ResponseData<PresentationE> result2 = 
      TransportationFactory
           .newJsonTransportation()
           .specify(verifierWeIdList)
           .deserialize(transString,PresentationE.class);


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

   QrCodeTransportation qrCodeTransportation =TransportationFactory.newQrCodeTransportation();

   String weId = "did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);
   JsonTransportation jsonTransportation = qrCodeTransportation.specify(verifierWeIdList);
   

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

   String weId = "did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);
   
   PresentationE presentation;
   
   //原文方式调用
   ResponseData<String> result1 = 
       TransportationFactory
           .newQrCodeTransportation()
           .specify(verifierWeIdList)
           .serialize(presentation,new ProtocolProperty(EncodeType.ORIGINAL));
   
   //密文方式调用
   ResponseData<String> result2 = 
      TransportationFactory
           .newQrCodeTransportation()
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

3. deserialize
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.transportation.inf.QrCodeTransportation.deserialize
   接口定义: <T extends JsonSerializer> ResponseData<T> deserialize(String transString,Class<T> clazz)
   接口描述: 用于反序列化对象,要求目标对象实现JsonSerializer接口。

**接口入参**\ : 

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
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

   String weId = "did:weid:0x0106595955ce4713fd169bfa68e599eb99ca2e9f";
   List<String> verifierWeIdList = new ArrayList<String>();
   verifierWeIdList.add(weId);
   
   String transString="";
   
   //原文方式调用反序列化
   ResponseData<PresentationE> result1 = 
       TransportationFactory
           .newQrCodeTransportation()
           .specify(verifierWeIdList)
           .deserialize(transString,PresentationE.class);
   
   //密文方式调用反序列化
   ResponseData<PresentationE> result2 = 
      TransportationFactory
           .newQrCodeTransportation()
           .specify(verifierWeIdList)
           .deserialize(transString,PresentationE.class);


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


Persistence
^^^^^^^^^^^^^^^^^

1. save
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.persistence.Persistence.save
   接口定义: ResponseData<Integer> save(String domain, String id, String data)
   接口描述: 根据domain定向存储数据。

**接口入参**\ : 

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - domain
     - String
     - Y
     - domain用于分布式存储数据
     - 
   * - id
     - String
     - Y
     - 数据存储编号
     - 
   * - data
     - String
     - Y
     - 存储的数据体
     -           
     
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
     - 存储数据行数
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
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - id无效
   * - PRESISTENCE_DOMAIN_ILLEGAL
     - 100902
     - domain非法
   * - PRESISTENCE_DOMAIN_INVALID
     - 100903
     - domain无效
   * - UNKNOW_ERROR
     - 160003
     - 未知异常，需核对日志
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常
     

**调用示例**

.. code-block:: java

   Persistence persistence = new MysqlDriver();
   ResponseData<Integer> res = persistence.save("domain1", "123456", "data123456");
   
   
.. code-block:: text

返回结果如：
   result: 1
   errorCode: 0
   errorMessage: success
   transactionInfo:null
   
   
----  


2. batchSave
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.persistence.Persistence.batchSave
   接口定义: ResponseData<Integer> batchSave(String domain, List<String> ids, List<String> dataList)
   接口描述: 根据domain定向批量存储数据。

**接口入参**\ : 

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - domain
     - String
     - Y
     - domain用于分布式存储数据
     - 
   * - ids
     - List<String>
     - Y
     - 数据存储编号集合
     - 
   * - data
     - List<String>
     - Y
     - 存储的数据体集合
     -           
     
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
     - 存储数据行数
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
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - id无效
   * - PRESISTENCE_DOMAIN_ILLEGAL
     - 100902
     - domain非法
   * - PRESISTENCE_DOMAIN_INVALID
     - 100903
     - domain无效
   * - PRESISTENCE_BATCH_SAVE_DATA_MISMATCH
     - 100904
     - 保存数据不匹配
   * - UNKNOW_ERROR
     - 160003
     - 未知异常，需核对日志
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常
     

**调用示例**

.. code-block:: java

   Persistence persistence = new MysqlDriver();
   
   List<String> ids = new ArrayList<String>();
   ids.add("123457");
   ids.add("123458");
   
   List<String> datas = new ArrayList<String>();
   datas.add("123457");
   datas.add("123458");
   
   ResponseData<Integer> res = persistence.batchSave("datasource1:sdk_all_data", ids, datas);
   
   
.. code-block:: text

返回结果如：
   result: 2
   errorCode: 0
   errorMessage: success
   transactionInfo:null
   
   
----


3. get
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.persistence.Persistence.get
   接口定义: ResponseData<String> get(String domain, String id)
   接口描述: 根据domain定向查询数据。

**接口入参**\ : 

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - domain
     - String
     - Y
     - domain用于分布式存储数据
     - 
   * - id
     - String
     - Y
     - 数据存储编号
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
     - 查询结果
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
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - id无效
   * - PRESISTENCE_DOMAIN_ILLEGAL
     - 100902
     - domain非法
   * - PRESISTENCE_DOMAIN_INVALID
     - 100903
     - domain无效
   * - UNKNOW_ERROR
     - 160003
     - 未知异常，需核对日志
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常
     

**调用示例**

.. code-block:: java

   Persistence persistence = new MysqlDriver();
   ResponseData<String> res = persistence.get("datasource1:sdk_all_data", "123456");
   
   
.. code-block:: text

返回结果如：
   result: data123456
   errorCode: 0
   errorMessage: success
   transactionInfo:null
   
   
----

4. delete
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.persistence.Persistence.delete
   接口定义: ResponseData<Integer> delete(String domain, String id)
   接口描述: 根据domain定向删除数据。

**接口入参**\ : 

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - domain
     - String
     - Y
     - domain用于分布式存储数据
     - 
   * - id
     - String
     - Y
     - 数据存储编号
     -     
     
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
     - 删除数据行数
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
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - id无效
   * - PRESISTENCE_DOMAIN_ILLEGAL
     - 100902
     - domain非法
   * - PRESISTENCE_DOMAIN_INVALID
     - 100903
     - domain无效
   * - UNKNOW_ERROR
     - 160003
     - 未知异常，需核对日志
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常
     

**调用示例**

.. code-block:: java

   Persistence persistence = new MysqlDriver();
   ResponseData<Integer> res = persistence.delete("datasource1:sdk_all_data", "123456");
   
   
.. code-block:: text

返回结果如：
   result: 1
   errorCode: 0
   errorMessage: success
   transactionInfo:null
   
   
----


5. update
~~~~~~~~~~~~~~~~~~~

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.persistence.Persistence.update
   接口定义: ResponseData<Integer> update(String domain, String id, String data)
   接口描述: 根据domain定向更新数据。

**接口入参**\ : 

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - domain
     - String
     - Y
     - domain用于分布式存储数据
     - 
   * - id
     - String
     - Y
     - 更新数据编号
     -     
   * - data
     - String
     - Y
     - 数据体
     -       
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
     - 更新数据行数
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
   * - PRESISTENCE_DATA_KEY_INVALID
     - 100901
     - id无效
   * - PRESISTENCE_DOMAIN_ILLEGAL
     - 100902
     - domain非法
   * - PRESISTENCE_DOMAIN_INVALID
     - 100903
     - domain无效
   * - UNKNOW_ERROR
     - 160003
     - 未知异常，需核对日志
   * - SQL_EXECUTE_FAILED
     - 160011
     - SQL执行异常
   * - SQL_GET_CONNECTION_ERROR
     - 160013
     - 获取数据源连接异常
     

**调用示例**

.. code-block:: java

   Persistence persistence = new MysqlDriver();
   ResponseData<Integer> res = persistence.update("datasource1:sdk_all_data", "123456", "data456789");
   
   
.. code-block:: text

返回结果如：
   result: 1
   errorCode: 0
   errorMessage: success
   transactionInfo:null
   
   
----

