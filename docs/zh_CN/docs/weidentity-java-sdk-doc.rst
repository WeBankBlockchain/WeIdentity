.. role:: raw-html-m2r(raw)
   :format: html

.. _weidentity-java-sdk-doc:

WeIdentity JAVA SDK文档
=======================

总体介绍
--------

WeIdentity Java SDK提供了一整套对WeIdentity进行管理操作的Java库。目前，SDK支持本地密钥管理、数字身份标识（WeIdentity DID）管理、电子凭证（WeIdentity Credential）管理、授权机构（Authority Issuer）管理、CPT（Claim Protocol Type）管理、存证（Evidence）管理、AMOP链上数据通道、凭证传输（Transportation）管理等功能，未来还将支持更丰富的功能和应用。

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
#. 生成凭证：通过CredentialPojoService的CreateCredential()，根据CPT模板，生成一份Credential；
#. 查询凭证：调用CredentialPojoService的Verify()，验证此Credential是否合法；
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

您可以访问 \ `基本数据结构 <./basic-data-structure.html>`_\ ，了解WeIdentity Java SDK所定义的核心基本数据结构。

.. toctree::
   :hidden:
   :maxdepth: 4

   basic-data-structure.rst

接口简介
--------

整体上，WeIdentity Java SDK包括五个主要的服务类，它们分别是：WeIdService、AuthorityIssuerService、CptService、CredentialService / CredentialPojoService、EvidenceService、AmopService。
其次，还包括负责链上信息传输的AmopService、Transportation服务，用于缓存的CacheManager服务，及加解密的CryptoService服务。

WeIdService
^^^^^^^^^^^^^

\ `WeIdService接口文档 <./weid-service.html>`_\ 介绍了WeIdentity DID相关功能的核心接口。具体地，
提供WeIdentity DID的创建、获取信息、设置属性等相关操作。

.. toctree::
   :hidden:
   :maxdepth: 2

   weid-service.rst

AuthorityIssuerService
^^^^^^^^^^^^^^^^^^^^^^^^^

在WeIdentity的整体架构中，存在着可信的“授权机构”这一角色。一般来说，授权机构特指那些广为人知的、具有一定公信力的、并且有相对频繁签发Credential需求的实体。
\ `AuthorityIssuerService接口文档 <./authority-issuer-service.html>`_\ 介绍了对这类授权签发Credential的机构的注册、移除、查询信息等操作。

.. toctree::
   :hidden:
   :maxdepth: 2

   authority-issuer-service.rst

CptService
^^^^^^^^^^^^^

任何凭证的签发，都需要将数据转换成已经注册的CPT (Claim Protocol Type)格式规范，也就是所谓的“标准化格式化数据”。
相关机构事先需要注册好CPT，在此之后，签发机构会根据CPT提供符合格式的数据，进而进行凭证的签发。
\ `CptService接口文档 <./cpt-service.html>`_\ 介绍了对CPT的注册、更新、查询等操作的接口。

.. toctree::
   :hidden:
   :maxdepth: 2

   cpt-service.rst

CredentialPojoService
^^^^^^^^^^^^^^^^^^^^^^^^^^^

凭证签发相关功能的核心接口。本接口提供凭证的签发和验证操作、Verifiable Presentation的签发和验证操作。
当前，\ `CredentialPojoService接口文档 <./credentialpojo-service.html>`_\ 内包括了以上的全部功能，也是推荐使用的凭证格式。


.. toctree::
   :hidden:
   :maxdepth: 2

   credentialpojo-service.rst

CredentialService
^^^^^^^^^^^^^^^^^^^^^^^^^^^

您可以参照 \ `CredentialService接口文档 <./credential-service.html>`_\ 了解旧式凭证格式相关接口。

.. toctree::
   :hidden:
   :maxdepth: 2

   credential-service.rst

EvidenceService
^^^^^^^^^^^^^^^^^^^^^^^^^^

\ `EvidenceService接口文档 <./evidence-service.html>`_\ 介绍了存证上链的所有相关接口。具体包括存证的生成上链、链上查询及校验等操作。

.. toctree::
   :hidden:
   :maxdepth: 2

   evidence-service.rst

AmopService
^^^^^^^^^^^^^

\ `AmopService接口文档 <./amop-service.html>`_\ 介绍了AMOP通讯相关接口。本接口提供AMOP的请求和注册。

.. toctree::
   :hidden:
   :maxdepth: 2

   amop-service.rst

Transportation
^^^^^^^^^^^^^^^^^^

WeIdentity支持基于AMOP进行数据的传输，数据传输通过Transportation服务进行。
\ `Transportation接口文档 <./transportation.html>`_\ 中详细介绍了WeIdentity提供的四类传输方式：Json、QRCode、BarCode、PDF方式。

.. toctree::
   :hidden:
   :maxdepth: 4

   transportation.rst

CacheManager
^^^^^^^^^^^^^

为了提高区块链的读写效率，WeIdentity支持将链上的内容进行缓存。
\ `CacheManager接口文档 <./cache-manager.html>`_\ 介绍了标准化的缓存读写相关功能。

.. toctree::
   :hidden:
   :maxdepth: 2

   cache-manager.rst

CryptoService
^^^^^^^^^^^^^^^^^

\ `CryptoService接口文档 <./crypto-service.html>`_\ 介绍了WeIdentity提供的一系列内置的数据加解密服务。

.. toctree::
   :hidden:
   :maxdepth: 2

   crypto-service.rst