.. role:: raw-html-m2r(raw)
   :format: html


1. createEvidence
-----------------------------

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.EvidenceService.createEvidence
   接口定义:ResponseData<String> createEvidence(Hashable object, WeIdPrivateKey weIdPrivateKey)
   接口描述: 将传入Object计算Hash值生成存证上链，返回存证hash值。传入的私钥将会成为链上存证的签名方。此签名方和凭证的Issuer可以不是同一方。此接口返回的Hash值和generateHash()接口返回值一致。同样的传入Object可以由不同的私钥注册存证，它们的链上存证值将会共存。

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


2. createEvidenceWithLogAndCustomKey
----------------------------------------

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.EvidenceService.createEvidence
   接口定义:ResponseData<String> createEvidence(Hashable object, WeIdPrivateKey weIdPrivateKey, String log, String customKey)
   接口描述: 将传入Object计算Hash值生成存证上链。此方法允许在创建存证时写入额外信息。额外信息为一个log记录，从后往前叠加存储。不同私钥发交易方的额外信息也是共存且相互独立存储的。如果您重复调用此接口，那么新写入的额外值会以列表的形式添加到之前的log列表之后。此方法还允许传入一个用户自定义的custom key，用来查询链上的存证（而不是通过hash）。

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


3. getEvidence
-----------------------------

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
   EvidenceService->>区块链节点: 调用智能合约，查询凭证存证内容
   区块链节点-->>EvidenceService: 返回查询结果
   opt 查询出错
   EvidenceService-->>调用者: 报错并退出
   end
   EvidenceService-->>调用者: 返回成功


4. getEvidenceByCustomKey
-----------------------------

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
   EvidenceService->>区块链节点: 调用智能合约，查询凭证存证内容
   区块链节点-->>EvidenceService: 返回查询结果
   opt 查询出错
   EvidenceService-->>调用者: 报错并退出
   end
   EvidenceService-->>调用者: 返回成功


5. verifySigner
-----------------------------

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


6. verifySigner（传入公钥）
-----------------------------

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


7. generateHash
-----------------------------


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


8. addLogByHash / addLogByCustomKey
---------------------------------------

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.EvidenceService.addLogByHash / addLogByCustomKey
   接口定义:ResponseData<Boolean> addLogByHash(String hashValue / customKey, String log, WeIdPrivateKey weIdPrivateKey)
   接口描述: 为一个已经在链上存在的存证添加额外信息记录存入其log中。有两个接口，一个是以hash值为索引，一个可以接受用户自定义索引。

**接口入参**\ :   String

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

