
.. _weidentity-rest-api:

WeIdentity RestService API 说明文档
----------------------------------------

1. 总体介绍
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

具体地，每个API的入参都是满足以下格式的json字符串：

.. code-block:: java

    {
        "functionArg": 随调用SDK方法而变的入参json字符串 {
        },
        "transactionArg": 交易参数json字符串 {
            "invokerWeId": 用于索引私钥的WeIdentity DID，服务器端会凭此找到所托管的私钥
        }
        "functionName": 调用SDK方法名
        "v": API版本号
    }

参数说明：

* functionArg是随不同的SDK调用方法而变的，具体的参数可以查看SDK接口文档；后文会为每个所提及的接口给出对应的链接
* transactionArg仅包括一个变量invokerWeId，由传入方决定使用在服务器端托管的具体哪个WeIdentity DID所对应的私钥
    * 非必需，只有在那些需要使用不同身份发交易签名的方法（如CreateAuthorityIssuer等）才会需要；后文中详细说明
* functionName是调用的SDK方法名，用于决定具体调用WeIdentity Java SDK的什么功能
* v是调用的API方法版本

每个API的接口返回都是满足以下格式的json字符串：

.. code-block:: java

    {
        "respBody": 随调用SDK方法而变的输出值json字符串 {
        }
        "ErrorCode": 错误码
        "ErrorMessage": 错误信息，成功时为"success"
    }


其中具体的输出值result亦是随不同的SDK调用方法而变的。

在后文中，我们将会逐一说明目前所提供的功能及其使用方式。

2. 创建WeIdentity DID
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

调用接口：

.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - 标题
     - 描述
   * - 接口名
     - weid/api/invoke
   * - Method
     - POST
   * - Content-Type
     - application/json

接口入参：

.. list-table::
   :header-rows: 1
   :widths: 30 60 20

   * - Key
     - Value
     - Required
   * - functionName
     - createWeId
     - Y
   * - functionArg
     - 
     - Y
   * - transactionArg
     - 
     - Y
   * - v
     - 版本号
     - Y


接口入参示例：

.. code-block:: java

    {
        "functionArg": {
        },
        "transactionArg": {
        },
        "functionName": "createWeId",
        "v": "1.0.0"
    }


接口返回: application/json


.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - Key
     - Value
   * - ErrorCode
     - 错误码，0表示成功
   * - ErrorMessage
     - 错误信息
   * - respBody
     - WeIdentity DID

返回示例：

.. code-block:: java

    {
        "ErrorCode": 0,
        "ErrorMessage": "success",
        "respBody": "did:weid:0x12025448644151248e5c1115b23a3fe55f4158e4153"
    }

3. 获取WeIdentity DID Document
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

调用接口：

.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - 标题
     - 描述
   * - 接口名
     - weid/api/invoke
   * - Method
     - POST
   * - Content-Type
     - application/json

接口入参：

.. list-table::
   :header-rows: 1
   :widths: 30 60 20

   * - Key
     - Value
     - Required
   * - functionName
     - getWeIdDocument
     - Y
   * - functionArg
     - 
     - Y
   * - functionArg.weId
     - WeIdentity DID，与 `SDK直接调用的方式入参 <https://weidentity.readthedocs.io/projects/javasdk/zh_CN/latest/docs/weidentity-java-sdk-doc.html#getweiddocment>`_ 一致，下同
     - Y
   * - transactionArg
     - 
     - N，传空
   * - v
     - 版本号
     - Y

接口入参示例：

.. code-block:: java

    {
        "functionArg": {
            "weId": "did:weid:0x12025448644151248e5c1115b23a3fe55f4158e4153"
        },
        "transactionArg": {
        },
        "functionName": "getWeIdDocument",
        "v": "1.0.0"
    }


接口返回: application/json

.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - Key
     - Value
   * - ErrorCode
     - 错误码，0表示成功
   * - ErrorMessage
     - 错误信息
   * - respBody
     - WeIdentity DID Document

返回示例：

.. code-block:: java

    {
        "respBody": {
            "@context" : "https://w3id.org/did/v1",
            "id" : "did:weid:0x12025448644151248e5c1115b23a3fe55f4158e4153",
            "created" : 1553224394993,
            "updated" : 1553224394993,
            "publicKey" : [ ],
            "authentication" : [ ],
            "service" : [ ]
        },
        "ErrorCode": 0,
        "ErrorMessage": "success"
    }

4. 创建AuthorityIssuer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

调用接口：

.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - 标题
     - 描述
   * - 接口名
     - weid/api/invoke
   * - Method
     - POST
   * - Content-Type
     - application/json


接口入参：


.. list-table::
   :header-rows: 1
   :widths: 30 60 20

   * - Key
     - Value
     - Required
   * - functionName
     - registerAuthorityIssuer
     - Y
   * - functionArg
     - 
     - Y
   * - functionArg.weId
     - WeIdentity DID，与 `SDK直接调用的方式入参 <https://weidentity.readthedocs.io/projects/javasdk/zh_CN/latest/docs/weidentity-java-sdk-doc.html#registercpt>`_ 一致，下同
     - Y
   * - functionArg.name
     - 机构名
     - Y
   * - transactionArg
     - 
     - Y
   * - transactionArg.invokerWeId
     - 用于索引私钥的WeIdentity DID，服务器端会凭此找到所托管的私钥
     - Y
   * - v
     - 版本号
     - Y

接口调用示例：

.. code-block:: java

    {
        "functionArg": {
            "weid": "did:weid:0x1Ae5b88d37327830307ab8da0ec5D8E8692A35D3",
            "name": "Sample College"
        },
        "transactionArg": {
            "invokerWeId": "did:weid:0x12025448644151248e5c1115b23a3fe55f4158e4153"
        },
        "functionName": "registerAuthorityIssuer",
        "v": "1.0.0"
    }


接口返回: application/json


.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - Key
     - Value
   * - ErrorCode
     - 错误码，0表示成功
   * - ErrorMessage
     - 错误信息
   * - respBody
     - True/False

返回示例：

.. code-block:: java

    {
        "ErrorCode": 0,
        "ErrorMessage": "success",
        "respBody": True
    }


5. 查询AuthorityIssuer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

调用接口：

.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - 标题
     - 描述
   * - 接口名
     - weid/api/invoke
   * - Method
     - POST
   * - Content-Type
     - application/json

接口入参：

.. list-table::
   :header-rows: 1
   :widths: 30 60 20

   * - Key
     - Value
     - Required
   * - functionName
     - queryAuthorityIssuer
     - Y
   * - functionArg
     - 
     - Y
   * - functionArg.weId
     - WeIdentity DID，与 `SDK直接调用的方式入参 <https://weidentity.readthedocs.io/projects/javasdk/zh_CN/latest/docs/weidentity-java-sdk-doc.html#queryauthorityissuer>`_ 一致，下同
     - Y
   * - transactionArg
     - 
     - N，传空
   * - v
     - 版本号
     - Y

接口入参示例：

.. code-block:: java

    {
        "functionArg": {
            "weId": "did:weid:0x1ae5b88d37327830307ab8da0ec5d8e8692a35d3"
        },
        "transactionArg": {
        },
        "functionName": "queryAuthorityIssuer",
        "v": "1.0.0"
    }

接口返回: application/json

.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - Key
     - Value
   * - ErrorCode
     - 错误码，0表示成功
   * - ErrorMessage
     - 错误信息
   * - respBody
     - 完整的Authority Issuer信息


.. code-block:: java

    {
        "respBody": {
            "accValue": ,
            "created": 16845611984115,
            "name": "Sample College",
            "weid": "did:weid:0x1ae5b88d37327830307ab8da0ec5d8e8692a35d3"
        }
        "ErrorCode": 0
        "ErrorMessage": "success"
    }

6. 创建CPT
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


调用接口：

.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - 标题
     - 描述
   * - 接口名
     - weid/api/invoke
   * - Method
     - POST
   * - Content-Type
     - application/json


接口入参: 

.. list-table::
   :header-rows: 1
   :widths: 30 60 20

   * - Key
     - Value
     - Required
   * - functionName
     - registerCpt
     - Y
   * - functionArg
     - 
     - Y
   * - functionArg.cptJsonSchema
     - CPT Json Schema，与 `SDK直接调用的方式入参 <https://weidentity.readthedocs.io/projects/javasdk/zh_CN/latest/docs/weidentity-java-sdk-doc.html#registercpt>`_ 一致，下同
     - Y
   * - functionArg.weId
     - CPT创建者
     - Y
   * - transactionArg
     - 
     - Y
   * - transactionArg.invokerWeId
     - 用于索引私钥的WeIdentity DID，服务器端会凭此找到所托管的私钥
     - Y
   * - v
     - 版本号
     - Y

.. code-block:: text

    CPT Json Schema是什么？应该满足什么格式？

    答：Json Schema是一种用来定义Json字符串格式的Json字符串，它定义了CPT应包括的字段、属性及规则。
    WeIdentity可以接受 http://json-schema.org/draft-04/schema# 所定义第四版及之前版本作为入参。


接口入参示例：

.. code-block:: java

      {
        "functionArg": {
            "weId": "did:weid:0x1ae5b88d37327830307ab8da0ec5d8e8692a35d3",
            "cptJsonSchema":{
                "title": "cpt",
                "description": "this is cpt",
                "properties": {
                    "name": {
                        "type": "string",
                        "description": "the name of certificate owner"
                    },
                    "gender": {
                        "enum": [
                            "F",
                            "M"
                        ],
                        "type": "string",
                        "description": "the gender of certificate owner"
                    },
                    "age": {
                        "type": "number",
                        "description": "the age of certificate owner"
                    }
                },
                "required": [
                    "name",
                    "age"
                ]
            }
        },
        "transactionArg": {
            "invokerWeId": "did:weid:0x1ae5b88d37327830307ab8da0ec5d8e8692a35d3"
        }，
        "functionName": "registerCpt"，
        "v": "1.0.0"
      }


接口返回: application/json


.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - Key
     - Value
   * - ErrorCode
     - 错误码，0表示成功
   * - ErrorMessage
     - 错误信息
   * - respBody
     - cptBaseInfo

返回示例：

.. code-block:: java

    {
        "respBody": {
            "cptId": 10,
            "cptVersion": 1
        },
        "ErrorCode": 0,
        "ErrorMessage": "success"
    }

7. 查询CPT
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

调用接口：

.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - 标题
     - 描述
   * - 接口名
     - weid/api/invoke
   * - Method
     - POST
   * - Content-Type
     - application/json

接口入参：

.. list-table::
   :header-rows: 1
   :widths: 30 60 20

   * - Key
     - Value
     - Required
   * - functionName
     - queryCpt
     - Y
   * - functionArg
     - 
     - Y
   * - functionArg.cptId
     - CPT ID，与 `SDK直接调用的方式入参 <https://weidentity.readthedocs.io/projects/javasdk/zh_CN/latest/docs/weidentity-java-sdk-doc.html#querycpt>`_ 一致。
     - Y
   * - transactionArg
     - 
     - N，传空
   * - v
     - 版本号
     - Y

接口入参示例：

.. code-block:: java

    {
        "functionArg": {
            "cptId": 10,
        },
        "transactionArg": {
        },
        "functionName": "queryCpt",
        "v": "1.0.0"
    }

接口返回: application/json

.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - Key
     - Value
   * - ErrorCode
     - 错误码，0表示成功
   * - ErrorMessage
     - 错误信息
   * - respBody
     - 完整的CPT信息

接口返回示例：

.. code-block:: java

    {
        "respBody": {
            "cptBaseInfo" : {
                "cptId" : 10,
                "cptVersion" : 1
            },
            "cptId" : 10,
            "cptJsonSchema" : {
                "$schema" : "http://json-schema.org/draft-04/schema#",
                "title" : "a CPT schema",
                "type" : "object"
            },
            "cptPublisher" : "did:weid:0x104a58c272e8ebde0c29083552ebe78581322908",
            "cptSignature" : "HJPbDmoi39xgZBGi/aj1zB6VQL5QLyt4qTV6GOvQwzfgUJEZTazKZXe1dRg5aCt8Q44GwNF2k+l1rfhpY1hc/ls=",
            "cptVersion" : 1,
            "created" : 1553503354555,
            "metaData" : {
                "cptPublisher" : "did:weid:0x104a58c272e8ebde0c29083552ebe78581322908",
                "cptSignature" : "HJPbDmoi39xgZBGi/aj1zB6VQL5QLyt4qTV6GOvQwzfgUJEZTazKZXe1dRg5aCt8Q44GwNF2k+l1rfhpY1hc/ls=",
                "created" : 1553503354555,
                "updated" : 0
            },
            "updated" : 0
        },
        "ErrorCode": 0,
        "ErrorMessage": "success"
    }

8. 创建Credential
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

调用接口：

.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - 标题
     - 描述
   * - 接口名
     - weid/api/invoke
   * - Method
     - POST
   * - Content-Type
     - application/json

接口入参：

.. list-table::
   :header-rows: 1
   :widths: 30 60 20

   * - Key
     - Value
     - Required
   * - functionName
     - createCredential
     - Y
   * - functionArg
     - 
     - Y
   * - functionArg.claim
     - claim Json结构体，与 `SDK直接调用的方式入参 <https://weidentity.readthedocs.io/projects/javasdk/zh_CN/latest/docs/weidentity-java-sdk-doc.html#createcredential>`_ 一致，下同     - Y
   * - functionArg.cptId
     - CPT ID
     - Y
   * - functionArg.issuer
     - issuer WeIdentity DID
     - Y
   * - functionArg.expirationDate
     - 过期时间（使用UTC格式）
     - Y
   * - transactionArg
     - 
     - Y
   * - transactionArg.invokerWeId
     - 用于索引私钥的WeIdentity DID，服务器端会凭此找到所托管的私钥
     - Y
   * - v
     - 版本号
     - Y

接口入参：Json，以signature代替私钥

.. code-block:: java

    {
        "functionArg": {
            "cptId": 10,
            "issuer": "did:weid:0x12025448644151248e5c1115b23a3fe55f4158e4153",
            "expirationDate": "2019-04-18T21:12:33Z",
            "claim": {
                "name": "zhang san",
                "gender": "F",
                "age": 18
            },
        },
        "transactionArg": {
            "invokerWeId": "did:weid:0x12025448644151248e5c1115b23a3fe55f4158e4153"
        },
        "functionName": "createCredential",
        "v": "1.0.0"
    }

接口返回: application/json

.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - Key
     - Value
   * - ErrorCode
     - 错误码，0表示成功
   * - ErrorMessage
     - 错误信息
   * - respBody
     - 完整的Credential信息


接口返回示例:

.. code-block:: java

    {
        "respBody": {
            "@context": "https://www.w3.org/2018/credentials/v1",
            "cptId": 10,
            "uuid" : "decd7c81-6b41-414d-8323-00161317a38e",
            "issuer": "did:weid:0x12025448644151248e5c1115b23a3fe55f4158e4153",
            "issuranceDate": "2019-03-19T21:12:33Z",
            "expirationDate": "2019-04-18T21:12:33Z",
            "claim": {
                "name": "zhang san",
                "gender": "F",
                "age": 18
            },
            "signature": "MTIzNDU2NzgxMjM0NTY3ODMzMzM0NDQ0MTIzNDU2NzgxMjM0NTY3ODEyMzQ1Njc4MTIzNDU2NzgxMjM0NTY3ODU="
        },
        "ErrorCode": 0,
        "ErrorMessage": "success"
    }


9. 验证Credential
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

调用接口：

.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - 标题
     - 描述
   * - 接口名
     - weid/api/invoke
   * - Method
     - POST
   * - Content-Type
     - application/json


接口入参：

.. list-table::
   :header-rows: 1
   :widths: 30 60 20

   * - Key
     - Value
     - Required
   * - functionName
     - createCredential
     - Y
   * - functionArg
     - 
     - Y
   * - functionArg.claim
     - claim Json 结构体，与 `SDK直接调用的方式入参 <https://weidentity.readthedocs.io/projects/javasdk/zh_CN/latest/docs/weidentity-java-sdk-doc.html#verify>`_ 一致，下同
     - Y
   * - functionArg.cptId
     - CPT ID
     - Y
   * - functionArg.context
     - context值
     - Y
   * - functionArg.uuid
     - Credential的UUID
     - Y
   * - functionArg.issuer
     - issuer WeIdentity DID
     - Y
   * - functionArg.issuranceDate
     - 颁发时间
     - Y
   * - functionArg.expirationDate
     - 过期时间
     - Y
   * - functionArg.signature
     - Credential签名值
     - Y
   * - transactionArg
     - 
     - N，传空
   * - v
     - 版本号
     - Y

接口入参：

.. code-block:: java

    {
        "functionArg": {
            "@context": "https://www.w3.org/2018/credentials/v1",
            "cptId": 10,
            "uuid" : "decd7c81-6b41-414d-8323-00161317a38e",
            "issuer": "did:weid:0x12025448644151248e5c1115b23a3fe55f4158e4153",
            "issuranceDate": "2019-03-19T21:12:33Z",
            "expirationDate": "2019-04-18T21:12:33Z",
            "claim": {
                "name": "zhang san",
                "gender": "F",
                "age": 18
            },
            "signature": "MTIzNDU2NzgxMjM0NTY3ODMzMzM0NDQ0MTIzNDU2NzgxMjM0NTY3ODEyMzQ1Njc4MTIzNDU2NzgxMjM0NTY3ODU="
        },
        "transactionArg": {
        },
        "functionName": "verifyCredential"
        "v": "1.0.0"
    }


接口返回: application/json

.. list-table::
   :header-rows: 1
   :widths: 30 50

   * - Key
     - Value
   * - ErrorCode
     - 错误码，0表示成功
   * - ErrorMessage
     - 错误信息
   * - respBody
     - True/False


接口返回：

.. code-block:: java

    {
        "ErrorCode": 0,
        "ErrorMessage": "success",
        "respBody": True,
    }
