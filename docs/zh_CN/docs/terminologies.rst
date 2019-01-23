
.. _terminologies:

术语
====

.. list-table::
   :header-rows: 1
   :widths: 39 18 100

   * - 术语
     - 对应中文
     - 说明
   * - JSON-LD
     -
     - 详见：\ `JSON-LD官网 <https://json-ld.org/>`_ 和 `JSON-LD Wiki <https://en.wikipedia.org/wiki/JSON-LD>`_
   * - Authorization
     - 授权
     - 详见：\ `Authorization vs Authentication <https://stackoverflow.com/questions/6556522/authentication-versus-authorization>`_
   * - Authentication
     - 身份验证
     - 详见：\ `Authorization vs Authentication <https://stackoverflow.com/questions/6556522/authentication-versus-authorization>`_
   * - Public Key
     - 公钥
     - 详见：\ `Public Key Wiki定义 <https://en.wikipedia.org/wiki/Public-key_cryptography>`_
   * - Private Key
     - 私钥
     - 详见（内容描述中包含Private Key）：\ `Public Key Wiki定义 <https://en.wikipedia.org/wiki/Public-key_cryptography>`_
   * - Signature
     - 数字签名
     - 详见：\ `Digital Signature Wiki定义 <https://en.wikipedia.org/wiki/Digital_signature>`_
   * - DID
     - 分布式身份标识
     - W3C的DID规范定义的去中心化ID。详见：\ `W3C DID规范 <https://w3c-ccg.github.io/did-spec/>`_
   * - WeIdentity DID
     -
     - 分布式身份标识，WeIdentity的分布式多中心的ID注册机制下生成的实体的ID。符合W3C DID规范
   * - WeIdentity Document
     -
     - 描述如何使用DID，至少包含了3个字段：披露的公钥列表；Authentication描述如何Authenticate；Service Endpoint
   * - Claim
     - 声明
     - 对实体的一个声明或者主张，用于装载凭证（Credential）业务数据的字段，例如电子驾照的各项信息就是存在一个Claim结构中
   * - WeIdentity Credential
     - 可验证数字凭证
     - 简称‘凭证’，遵循W3C Verifiable Credential规范的电子凭证，可用来抽象现实世界凭证类的对象，一个Credential可以包含一个或者多个Claim。例如电子驾照，电子学历等
   * - Verifiable Credential
     - 同上
     - 同上
   * - Credential
     - 同上
     - 同上
   * - Notification
     - 通知
     - WeIdentity体系实现的通知机制
   * - CPT
     - 凭证的声明类型
     - Claim Protocol Type,不同的Issuer按业务场景需要，各自定义不同类型数据结构的Claim，各种各样的Claim用不同的CPT来定义
   * - net-id
     - 网络id
     - 用于路由到不同的网络
   * - Service Endpoint
     - 服务端点
     - Subject暴露的服务的地址，例如暴露自己的Credential托管的服务地址，暴露自己的文件存储服务地址
   * - publish
     - 发布
     - 一个Issuer（包括Authority Issuer）发行一种新的CPT，这个动作称为publish
   * - issue
     - 发行
     - 一个Issuer（包括Authority Issuer）按照某种CPT定义的格式，发行Credential，这个动作叫issue
   * - payload
     - 有效载荷
     - Notification机制里，用以描述业务相关信息的字段
   * - Trusted Data
     - 可信数据
     - 由权威机构发行的与实体关联的数据，通过摘要算法与签名算法，在保证数据隐私前提下，实现数据不可篡改与可验证
   * - Issuer
     - 凭证发行者
     - 任意拥有WeIdentity DID的Subject都可以作为Issuer来发行Credential
   * - Authority Issuer
     - 权威凭证发行者
     - 例如对Claim进行认证的权威机构或者可信机构，例如下发驾照的交通事务局
   * - Subject
     - 主体
     - WeIdentity Document或Credential描述的主体，即拥有WeIdentity DID的人或者物
   * - Verifier
     - 凭证验证者
     - 使用凭证的第三方，会验证这个凭证是否经过权威机构认证，例如劳工局办理业务时需要用户提供驾照，这时劳工局就是Verifier
   * - Data Repository
     - 数据托管机构
     - 数据的托管机构，例如用户把Credential托管在某个用户身份管理APP里面
   * - User Agent
     - 用户代理
     - 用户私钥托管机构，例如某个用户身份管理APP。依据业务场景需求，可以是云端保存机制的，也可以是客服端本地保存机制的
   * - Publisher
     - CPT发布者
     - 发布一个新的CPT的角色，被称为Publisher
