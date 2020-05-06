.. role:: raw-html-m2r(raw)
   :format: html


1. createCredential
-----------------------------

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


2. prepareZkpCredential
-----------------------------

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
     - 获取weIdDocument异常
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
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

3. createSelectiveCredential
-----------------------------

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.CredentialPojoService.createSelectiveCredential
   接口定义: ResponseData<CredentialPojo> createSelectiveCredential(CredentialPojo credentialPojo, ClaimPolicy claimPolicy)
   接口描述: 通过原始凭证和披露策略，创建选择性披露的Credential。

.. note::

   注意：对于已经创建好的选择性披露凭证，不允许再次进行选择性披露。

.. note::

   ClaimPolicy内部对选择性披露的策略定义在fieldsToBeDisclosed。它是一个Json字符串，和Claim中定义的Key完全对应，Value为1则为披露（在生成的凭证中显示为原文），Value为0则为不披露（显示为加盐的hash值）。如您的Claim包括name、gender、age三项，想披露name和age，不披露gender，则对应的ClaimPolicy为"{\"name\":1,\"gender\":0,\"age\":1}"


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


4. verify
-----------------------------

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
     - 获取weIdDocument异常
   * - CREDENTIAL_ISSUER_INVALID
     - 100418
     - WeIdentity DID无效
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


5. verify
-----------------------------

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


6. verify
-----------------------------

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
     - 签名不存在
   * - CREDENTIAL_POLICY_DISCLOSUREVALUE_ILLEGAL
     - 100423
     - policy披露信息非法
   * - CREDENTIAL_DISCLOSUREVALUE_NOTMATCH_SALTVALUE
     - 100424
     - Credential披露信息跟盐信息不一致
   * - CREDENTIAL_CPTID_NOTMATCH
     - 100425
     - CPT不匹配
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


7. verifyPresentationFromPdf
-----------------------------

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
     - 签名不存在
   * - CREDENTIAL_POLICY_DISCLOSUREVALUE_ILLEGAL
     - 100423
     - policy披露信息非法
   * - CREDENTIAL_DISCLOSUREVALUE_NOTMATCH_SALTVALUE
     - 100424
     - Credential披露信息跟盐信息不一致
   * - CREDENTIAL_CPTID_NOTMATCH
     - 100425
     - CPT不匹配
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


8. createPresentation
-----------------------------

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


9. addSignature
-----------------------------

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

----