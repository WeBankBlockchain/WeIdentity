

.. image:: docs/images/weidentity-logo.png
   :target: docs/images/weidentity-logo.png
   :alt: weidentity-logo.jpeg

----

什么是WeIdentity？
==================

WeIdentity是一个基于公众联盟链技术构建的实体身份认证与可信数据交换的生态体系。WeIdentity为实体对象（人或者物）的现实身份与链上身份映射、以及实体对象之间的数据授权与交换过程提供了一套分布式多中心的技术解决方案。WeIdentity由微众银行自主研发并完全开源，秉承公众联盟链整合资源、交换价值、服务公众的理念，致力于成为链接多个垂直行业领域的分布式商业基础设施，促进泛行业、跨机构、跨地域间的身份认证和数据合作。

模块介绍
--------

WeIdentity目前主要包含两大模块：WeIdentity DID以及WeIdentity Credential。

分布式身份标识 (WeIdentity DID)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

传统方式中，用户的注册和身份管理完全依赖于单一中心的注册机构；随着分布式账本技术（例如区块链）的出现，分布式多中心的身份注册、标识和管理成为可能。
WeIdentity DID模块在\ `FISCO-BCOS区块链底层平台 <https://github.com/FISCO-BCOS/FISCO-BCOS>`_\ 上实现了一套分布式多中心的身份标识协议，使实体（人或物）的现实身份实现了链上的身份标识；同时，WeIdentity DID给与Subject（人或者物）直接拥有和控制自己身份ID的能力。

WeIdentity DID秉承以下设计理念：


.. include:: docs/design-goals.rst


可验证数字凭证 (WeIdentity Credential)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

现实世界中存在着各种各样用于描述实体身份、实体间关系的数据，如身份证、行驶证、存款证明、处方、毕业证、房产证、信用报告等。WeIdentity Credential提供了一整套基于\ `W3C VC规范 <https://w3c.github.io/vc-data-model/>`_\ 的解决方案，旨在对这一类数据进行标准化、电子化，生成可验证、可交换的「凭证」（Credential）。

WeIdentity支持认证机构自行注册标准化凭证模板，共同丰富公众联盟链的生态。

更多
^^^^

*
   `案例及场景 <https://weidentity.readthedocs.io/zh_CN/stable/docs/use-cases.html>`_

*
   `WeIdentity规范文档 <https://weidentity.readthedocs.io/zh_CN/stable/docs/weidentity-spec.html>`_


开始使用
--------

WeIdentity目前支持基于FISCO-BCOS运行，并提供JAVA SDK供调用。具体的规范文档、安装部署和SDK使用指引请参考如下文档：


*
   `安装部署文档 <https://weidentity.readthedocs.io/zh_CN/stable/docs/weidentity-installation.html>`_

*
   `SDK使用文档 <https://weidentity.readthedocs.io/zh_CN/stable/docs/weidentity-java-sdk-doc.html>`_


联系我们
--------

邮箱：weidentity@webank.com
