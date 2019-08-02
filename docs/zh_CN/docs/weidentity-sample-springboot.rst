spring-boot服务方式使用
-------------------

整体介绍
~~~~~~~~

::

    使用 spring-boot 方式，weid-sample 程序将作为一个后台进程运行，您可以使用 http 方式体验交互流程。

1. 下载 weid-sample 源码：
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code:: shell

    git clone https://github.com/WeBankFinTech/weid-sample.git

2. 配置与部署
^^^^^^^^^^^^^^^^^^^^^^^^^^

2.1 下载 weid-sample 源码：
''''''''''''''''''''''''''''''''''''

.. code:: shell

    git clone https://github.com/WeBankFinTech/weid-sample.git
    

2.2 部署 weid-java-sdk 与配置基本信息
''''''''''''''''''''''''''''''''''''''

-  安装部署 weid-java-sdk

   weid-sample 需要依赖 weid-java-sdk，您需要参考\ `WeIdentity JAVA
   SDK安装部署 <https://weidentity.readthedocs.io/zh_CN/latest/docs/weidentity-installation.html>`__\ 完成
   weid-java-sdk
   的安装部署，并参照\ `Java应用集成章节 <https://weidentity.readthedocs.io/zh_CN/latest/docs/weidentity-build-with-deploy.html#weid-java-sdk>`__\ 完成
   weid-sample 的配置。


-  配置 Committee Member 私钥

   将您在\ `部署WeIdentity智能合约阶段 <https://weidentity.readthedocs.io/zh_CN/latest/docs/weidentity-build-with-deploy.html#id7>`__\ 生成的私钥文件拷贝至
   ``weid-sample/keys/priv/`` 目录中，此私钥后续将用于注册 Authority Issuer，weid-sample 会自动加载。
.. note::
   此项配置并非必要，注册 Authority Issuer 需要委员会机构成员（ Committee Member ）权限，发布智能合约时生成的公私钥对会自动成为委员会机构成员，若您不是发布智能合约的机构，您无需关注此配置项。
   若您是智能合约发布的机构，您可以参考以下进行配置：


2.3 基本流程的演示
''''''''''''''''''''''''
2.3.1 编译和运行
>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

- 编译 weid-sample

.. code:: shell

    cd weid-sample
    chmod +x *.sh
    ./build.sh

- 启动 weid-sample 服务:


.. code:: shell

    ./start.sh

若启动成功，则会打印以下信息：

::

    [main] INFO  AnnotationMBeanExporter() - Registering beans for JMX exposure on startup
    [main] INFO  Http11NioProtocol() - Initializing ProtocolHandler ["https-jsse-nio-20190"]
    [main] INFO  Http11NioProtocol() - Starting ProtocolHandler ["https-jsse-nio-20190"]
    [main] INFO  NioSelectorPool() - Using a shared selector for servlet write/read
    [main] INFO  Http11NioProtocol() - Initializing ProtocolHandler ["http-nio-20191"]
    [main] INFO  NioSelectorPool() - Using a shared selector for servlet write/read
    [main] INFO  Http11NioProtocol() - Starting ProtocolHandler ["http-nio-20191"]
    [main] INFO  TomcatEmbeddedServletContainer() - Tomcat started on port(s): 20190 (https) 20191 (http)
    [main] INFO  SampleApp() - Started SampleApp in 3.588 seconds (JVM running for 4.294)

2.3.2 流程演示
>>>>>>>>>>>>>>>>>>>>>>>>

以下将为您演示
假设您的服务部署在本地，地址是 ``127.0.0.1``，服务端口是 ``20191``。

- 创建 WeID
.. code:: shell

    curl -l -H "Content-type: application/json" -X POST   http://127.0.0.1:20191/createWeId

若调用成功，则会打印以下信息：
::

    
    {
        "result":{
            "weId":"did:weid:101:0xd613fbc0249f2ce5088ed484fa6b7b51ecb95e24",
            "userWeIdPublicKey":{
                "publicKey":"3170902924087212850995053706205512080445198963430287429721846825598988998466716040533782467342119206581749393570668868631792331397183368695050591746049552"
            },
            "userWeIdPrivateKey":null
        },
        "errorCode":0,
        "errorMessage":"success",
        "transactionInfo":{
            "blockNumber":60643,
            "transactionHash":"0xc73b7ba6af39614761423dc8fcbbbc7e5f24c82e8187bc467cf0398b4ce4330b",
            "transactionIndex":0
        }
    }

表明创建的 WeID 是 did:weid:101:0xd613fbc0249f2ce5088ed484fa6b7b51ecb95e24。

- 注册 Authority Issuer

.. code:: shell

    curl -l -H "Content-type: application/json" -X POST -d '{"issuer":"did:weid:101:0xd613fbc0249f2ce5088ed484fa6b7b51ecb95e24","org-id":"webank"}'  
    http://127.0.0.1:20191/registerAuthorityIssuer

运行成功，则会打印以下信息：

::

    
    {
        "result":true,
        "errorCode":0,
        "errorMessage":"success",
        "transactionInfo":{
            "blockNumber":60668,
            "transactionHash":"0xa0b84473705da2679cfec9119e2cdef03175df0f1af676e0579d5809e4e8d6cd",
            "transactionIndex":0
        }
    }

- 注册 CPT

.. code:: shell
    curl -l -H "Content-type: application/json" -X POST -d '{"publisher": "did:weid:101:0xd613fbc0249f2ce5088ed484fa6b7b51ecb95e24",
    "claim": {"properties": {"id":{"type":"string","description":"user weid"},"name":{"type":"string","description":"user name"},"gender":{"type":"string","description":"user gender"}}}}' 
    http://127.0.0.1:20191/registCpt


运行成功，则会打印以下信息：
::


    {
        "result":{
            "cptId":1189,
            "cptVersion":1
        },
        "errorCode":0,
        "errorMessage":"success",
        "transactionInfo":{
            "blockNumber":60676,
            "transactionHash":"0x72d55eb1d020acd09b115177a46e230ffdb0177ab5dd74e16765d79338522093",
            "transactionIndex":0
        }
    }

表明注册 CPT 成功，CPT ID 为 1189。

- 创建 Credential

创建 Credential 依赖于具体的 CPT，参数里的 cptId 传入刚刚注册的 CPT 的 ID：

.. code:: shell
    curl -l -H "Content-type: application/json" -X POST -d 
    '{"cptId": "1189","issuer": "did:weid:101:0xd613fbc0249f2ce5088ed484fa6b7b51ecb95e24",
    "claimData": {"id":"did:weid:101:0xf36fb2308d36bb94c579f568bdf670743d949deb","name":"zhangsan","gender":"F"}}' 
    http://127.0.0.1:20191/createCredential

若运行成功，则会打印以下信息：

::


    {
        "result":{
            "credential":{
                "context":"https://github.com/WeBankFinTech/WeIdentity/blob/master/context/v1",
                "id":"e4f4accd-6026-4fd0-9392-1379ddd4f778",
                "cptId":1189,
                "issuer":"did:weid:101:0xd613fbc0249f2ce5088ed484fa6b7b51ecb95e24",
                "issuanceDate":1564371227764,
                "expirationDate":1595475227763,
                "claim":{
                    "gender":"F",
                    "name":"zhangsan",
                    "id":"did:weid:101:0xf36fb2308d36bb94c579f568bdf670743d949deb"
                },
                "proof":{
                    "creator":"did:weid:101:0xd613fbc0249f2ce5088ed484fa6b7b51ecb95e24",
                    "signature":"G2kD4u4jrnmYbq/oVl9idmTEQzP3a0KEomHGJaVpWzhITIE+dDYSRMyF9TDy+jPANpYRJGg7pGnANM+QeJ9Ba00=",
                    "created":"1564371227764",
                    "type":"EcdsaSignature"
                },
                "signature":"G2kD4u4jrnmYbq/oVl9idmTEQzP3a0KEomHGJaVpWzhITIE+dDYSRMyF9TDy+jPANpYRJGg7pGnANM+QeJ9Ba00=",
                "proofType":"EcdsaSignature"
            },
            "disclosure":{
                "name":1,
                "id":1,
                "gender":1
            }
        },
        "errorCode":0,
        "errorMessage":"success",
        "transactionInfo":null
    }

表明创建 Credential 成功，Credential 的具体信息为输出中的 Credential 字段对应的内容。

- 验证 Credential


.. code:: shell
    curl -l -H "Content-type: application/json" -X POST -d 
    '{"context":"https://github.com/WeBankFinTech/WeIdentity/blob/master/context/v1",
    "id":"e4f4accd-6026-4fd0-9392-1379ddd4f778","cptId":1189,"issuer":"did:weid:101:0xd613fbc0249f2ce5088ed484fa6b7b51ecb95e24",
    "issuanceDate":1564371227764,"expirationDate":1595475227763,"claim":{"gender":"F","name":"zhangsan","id":"did:weid:101:0xf36fb2308d36bb94c579f568bdf670743d949deb"},
    "proof":{"creator":"did:weid:101:0xd613fbc0249f2ce5088ed484fa6b7b51ecb95e24","signature":"G2kD4u4jrnmYbq/oVl9idmTEQzP3a0KEomHGJaVpWzhITIE+dDYSRMyF9TDy+jPANpYRJGg7pGnANM+QeJ9Ba00=",
    "created":"1564371227764","type":"EcdsaSignature"},"signature":"G2kD4u4jrnmYbq/oVl9idmTEQzP3a0KEomHGJaVpWzhITIE+dDYSRMyF9TDy+jPANpYRJGg7pGnANM+QeJ9Ba00=","proofType":"EcdsaSignature"},
    "disclosure":{"name":1,"id":1,"gender":1}'  
    http://127.0.0.1:20191/verifyCredential


若运行成功，则会打印以下信息：

::

    {
        "result":true,
        "errorCode":0,
        "errorMessage":"success",
        "transactionInfo":null
    }

表明 Credential 验证成功。

至此，您已经体验了 weid-sample 实现的各个角色的运行流程，实现的入口类在weid-sample工程的 ``com.webank.weid.demo.server.SampleApp``，您可以参考进行您的 Java 应用开发。