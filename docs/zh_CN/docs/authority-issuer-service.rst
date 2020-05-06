.. role:: raw-html-m2r(raw)
   :format: html


1. registerAuthorityIssuer
-----------------------------

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

2. removeAuthorityIssuer
-----------------------------

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


3. isAuthorityIssuer
-----------------------------

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


4. queryAuthorityIssuerInfo
-----------------------------


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


5. getAllAuthorityIssuerList
-----------------------------

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


6. registerIssuerType
-----------------------------

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


7. addIssuerIntoIssuerType
-----------------------------

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


8. removeIssuerFromIssuerType
-------------------------------

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


9. isSpecificTypeIssuer
-----------------------------

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


10. getAllSpecificTypeIssuerList
---------------------------------

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

