.. role:: raw-html-m2r(raw)
   :format: html


WeIdDocument
----------------

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
^^^^^^^^^^^

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
^^^^^^^^^^^^^

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
-----------

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
^^^^^^^^^^^

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
^^^^^^^^^^^^^^^^^^^^^^

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
-----------------

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
^^^^^^^^^^^

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
^^^^^^^^^^^^^^^^^^^^^^

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
-----------------------

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
^^^^^^^^^^^^^^^^^^^^^^

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
^^^^^^^^^^^^^^^^^^^^^^

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
----------------

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
^^^^^^^^^^^

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
^^^^^^^^^^^^^^^^^^^^^^

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
^^^^^^^^^^^

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
^^^^^^^^^^^

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
