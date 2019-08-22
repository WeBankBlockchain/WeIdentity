.. role:: raw-html-m2r(raw)
   :format: html

.. _weidentity-quick-tools:

WeIdentity JAVA SDK 便捷使用工具
============================================================

整体介绍
--------

   通过便捷工具， 您可以快速的体验和使用 WeIdentity JAVA SDK。

部署步骤
--------

在使用之前，要确保您已完成 WeIdentity JAVA SDK 的安装部署，若您还没有完成，请参考\ `WeIdentity JAVA SDK 安装部署（部署智能合约） <./weidentity-build-with-deploy.html>`__\ 或者\ `WeIdentity JAVA SDK 安装部署（不部署智能合约） <./weidentity-build-without-deploy.html>`__\。


此步骤提供快速创建 WeIdentity DID、注册 Authority Issuer、发布 CPT、拉取 CPT 并生成 presentation policy 的能力。


.. raw:: html

   <div id="section-1">

1 创建您的 WeIdentity DID
''''''''''''''''''''''''''''''

.. raw:: html

   </div>


这个步骤会帮您快速创建一个 WeIdentity DID。

::

    cd ../tools
    chmod +x *.sh
    ./create_weid.sh

若执行成功，则会打印以下信息。

::

    New weid has been created ----> did:weid:1:0x405a7ae297fc6d6fb02fb548db64b29f08114ca1
    The related private key and public key can be found at /home/app/tonychen/test_gradle/weid-build-tools/output/create_weid/0x405a7ae297fc6d6fb02fb548db64b29f08114ca1.

表明创建的 ``WeID`` 是did:weid:1:0x405a7ae297fc6d6fb02fb548db64b29f08114ca1。

在 ``weid-build-tools/output/create_weid/`` 目录下看到一些以 0x 开头的目录，找到跟刚刚生成的 WeIdentity DID 匹配的目录，里面包含了 WeIdentity DID，公钥 ``ecdsa_key.pub`` 和私钥 ``ecdsa_key``。

2 注册 Authority Issuer (权威凭证发行者)
'''''''''''''''''''''''''''''''''''''''''''''''

.. note::
    只有 Committee Member（委员会机构成员）可以进行本节操作，若您不是 Committee Member，您可以将您的 WeIdentity DID 和机构名称发给 Committee Member，让其帮您注册成 Authority Issuer。

- 注册 Authority Issuer

假设您要注册的 Authority Issuer 的 WeIdentity DID 为did:weid:1:0x5efd256d02c1a27675de085b86989fa2ac1baddb，机构名称是 test。
::

    ./register_authority_issuer.sh --org-id test --weid did:weid:1:0x5efd256d02c1a27675de085b86989fa2ac1baddb

如果执行成功，会打印以下信息。
::

    Registering authorityissuer ---> did:weid:1:0x5efd256d02c1a27675de085b86989fa2ac1baddb, name is :test
    Execute succeed.

- 移除Authority Issuer

如果您需要移除某个Authority Issuer，比如您要移除did:weid:1:0x5efd256d02c1a27675de085b86989fa2ac1baddb：

::

    ./register_authority_issuer.sh --remove-issuer did:weid:1:0x5efd256d02c1a27675de085b86989fa2ac1baddb

若执行成功，则会打印以下信息。
::

    Removing authority issuer ---> did:weid:1:0x5efd256d02c1a27675de085b86989fa2ac1baddb...
    Execute succeed.


3 注册 Specific Issuer(特定类型的发行者)
''''''''''''''''''''''''''''''''''''''''''''''''''''''''''

.. note::
    只有委员会成员（ Committee Member ）可以进行本节操作，若您不是委员会成员，您可以将您的 WeIdentity DID 和机构名称发给委员会成员，让其帮您注册成 Specific Issuer。

- 注册特定类型机构

假设您要注册的机构的 WeIdentity DID 为did:weid:1:0x5efd256d02c1a27675de085b86989fa2ac1baddb，注册类型为 college，只需执行此下命令：

::

    ./register_specific_issuer.sh --type college --weid did:weid:1:0x5efd256d02c1a27675de085b86989fa2ac1baddb

执行成功，则会打印以下信息。
::

    [RegisterIssuer] Adding WeIdentity DID did:weid:1:0xe10e52f6b7c6751bd03afc023b8e617d7fd0429c in type: college
    Specific issuers and types have been successfully registered on blockchain.

如果您需要注册多个机构，请将其 WeIdentity DID 用分号分割开，如下所示：

::

    ./register_specific_issuer.sh --type college --weid did:weid:1:0x5efd256d02c1a27675de085b86989fa2ac1baddb;did:weid:0x6efd256d02c1a27675de085b86989fa2ac1baddb

- 移除特定类型机构

比如您要从 college 类型中移除 WeID 为 did:weid:1:0x6efd256d02c1a27675de085b86989fa2ac1baddb 的 Specific Issuer：

::

    ./register_specific_issuer.sh --type college --remove-issuer did:weid:1:0x6efd256d02c1a27675de085b86989fa2ac1baddb

4 机构发布 CPT
''''''''''''''''''''''''''''''

此步骤会帮助机构发布指定的 CPT 到区块链上。


如果您的 WeIdentity DID 是执行\ `第1节 <#section-1>`__\生成的，您可以不用传入私钥，只用指定 CPT 的路径和 WeID 即可。

.. note::
     以下样例中的 ``test_data/single/`` 已预置测试CPT。您也可以更改为其他包含您CPT的目录。

::

    ./register_cpt.sh --cpt-dir test_data/single/ --weid did:weid:1:0x5efd256d02c1a27675de085b86989fa2ac1baddb

若执行成功，则会打印以下信息：
::

    [RegisterCpt] register cpt file:JsonSchema.json result ---> success. cpt id ---> 1000
    [RegisterCpt] register cpt file:JsonSchema.json with success.
    Execute succeed.

如果您是通过其他途径创建的 WeIdentity DID，您需要自己指定私钥的位置。
假如机构的 WeID 是 did:weid:1:0x5efd256d02c1a27675de085b86989fa2ac1baddb，私钥文件路径为 ``/home/test/private_key/ecdsa_key``：

::

    ./register_cpt.sh --cpt-dir test_data/single/ --weid did:weid:1:0x5efd256d02c1a27675de085b86989fa2ac1baddb --private-key /home/test/private_key/ecdsa_key

若执行成功，则会打印以下信息：
::

    [RegisterCpt] register cpt file:JsonSchema.json result ---> success. cpt id ---> 1000
    [RegisterCpt] register cpt file:JsonSchema.json with success.
    Execute succeed.


5 拉取CPT并生成presentation policy模板
'''''''''''''''''''''''''''''''''''''''''''
.. note::
    此步骤，可以帮使用者从区块链上拉取指定的已发布的 CPT，并转化成 POJO，同时也会根据您生成一个 presentation policy 模板。

假如您需要将 CPT id 为 1000 的 CPT 从区块链上拉取下来，并基于 CPT 1000 生成 presentation policy 的配置模板。


::

    ./cpt_to_pojo.sh --cpt-list 1000

若执行成功，则会打印以下信息。
::
 
    Begin to generate pojo from cpt...
    All cpt:[1000] are successfully transformed to pojo.

    The weidentity-cpt.jar can be found in /home/app/tonychen/test_gradle/weid-build-tools/dist/app/
    Begin to generate presentation policy ...
    Presentation policy template is successfully generated, you can find it at /home/app/tonychen/test_gradle/weid-build-tools/output/presentation_policy.

表明生成的 CPT 的 POJO 的jar包在 ``/home/app/tonychen/test_gradle/weid-build-tools/dist/app/`` 目录下， 生成的 presentation policy 模板在 ``/home/app/tonychen/test_gradle/weid-build-tools/output/presentation_policy`` 。
