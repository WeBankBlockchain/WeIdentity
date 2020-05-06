.. role:: raw-html-m2r(raw)
   :format: html


1. createCredential
-----------------------------

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


2. verify
-----------------------------

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


3. verifyCredentialWithSpecifiedPubKey
----------------------------------------------------------

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

4. addSignature
-----------------------------

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
