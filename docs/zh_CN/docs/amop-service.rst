.. role:: raw-html-m2r(raw)
   :format: html


1. registerCallback
-----------------------------

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


2. request
-----------------------------

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


3. getPolicyAndChallenge
-----------------------------

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


4. requestPolicyAndPreCredential
----------------------------------------------------------

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


5. requestIssueCredential
-----------------------------

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


6. getEncryptKey
-----------------------------

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
