.. role:: raw-html-m2r(raw)
   :format: html


1. createWeId
---------------

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


2. createWeId
---------------

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

3. delegateCreateWeId
------------------------------

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


4. getWeIdDocumentJson
------------------------------

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


5. getWeIDDocment
------------------------------

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


6. setPublicKey
---------------


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

7. delegateSetPublicKey
------------------------------

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.delegateSetPublicKey
   接口定义:ResponseData<Boolean> setPublicKey(PublicKeyArgs publicKeyArgs, WeIdAuthentication delegateAuth)
   接口描述: 由代理来给WeIdentity DID添加公钥。

**接口入参**\ :   com.webank.weid.protocol.request.PublicKeyArgs

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

   PublicKeyArgs publicKeyArgs = new PublicKeyArgs();
   publicKeyArgs.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7");
   publicKeyArgs.setPublicKey(
      "13161444623157635919577071263152435729269604287924587017945158373362984739390835280704888860812486081963832887336483721952914804189509503053687001123007342");
   String delegateWeId = "did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7";
   String delegatePrivateKey = "60866441986950167911324536025850958917764441489874006048340539971987791929772";
   WeIdAuthentication weIdAuthentication = new WeIdAuthentication(delegateWeId, delegatePrivateKey);
   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");

   ResponseData<Boolean> response = weIdService.delegateSetPublicKey(publicKeyArgs,weIdAuthentication);


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


8. setService
---------------

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


9. delegateSetService
------------------------------

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.delegateSetService
   接口定义:ResponseData<Boolean> setService(ServiceArgs serviceArgs，WeIdAuthentication delegateAuth)
   接口描述: 根据WeIdentity DID添加Service信息。

**接口入参**\ :   com.webank.weid.protocol.request.ServiceArgs

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

   String delegateWeId = "did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7";
   String delegatePrivateKey = "60866441986950167911324536025850958917764441489874006048340539971987791929772";
   WeIdAuthentication weIdAuthentication = new WeIdAuthentication(delegateWeId, delegatePrivateKey);
   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");

   ResponseData<Boolean> response = weIdService.delegateSetService(serviceArgs, weIdAuthentication);


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


10. setAuthentication
------------------------------

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

11. delegateSetAuthentication
------------------------------

**基本信息**

.. code-block:: text

   接口名称:com.webank.weid.rpc.WeIdService.delegateSetAuthentication
   接口定义:ResponseData<Boolean> delegateSetAuthentication(AuthenticationArgs authenticationArgs，WeIdAuthentication delegateAuth)
   接口描述: 根据WeIdentity DID添加认证者。

**接口入参**\ :   com.webank.weid.protocol.request.AuthenticationArgs

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

   String delegateWeId = "did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7";
   String delegatePrivateKey = "60866441986950167911324536025850958917764441489874006048340539971987791929772";
   WeIdAuthentication weIdAuthentication = new WeIdAuthentication(delegateWeId, delegatePrivateKey);
   weIdAuthentication.setWeIdPublicKeyId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b65600e7#key0");

   ResponseData<Boolean> response = weIdService.delegateSetAuthentication(authenticationArgs,weIdAuthentication);


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


12. isWeIdExist
------------------------------

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