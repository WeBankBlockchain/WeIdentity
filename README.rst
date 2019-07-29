中文版本 | `English Version <./README-en.rst>`_

.. image:: docs/zh_CN/docs/images/weidentity-logo.png
   :target: docs/zh_CN/docs/images/weidentity-logo.png
   :alt: weidentity-logo.jpeg


----

什么是 WeIdentity？
========================

WeIdentity是一套分布式多中心的技术解决方案，可承载实体对象（人或者物）的现实身份与链上身份的可信映射、以及实现实体对象之间安全的访问授权与数据交换。WeIdentity由微众银行自主研发并完全开源，秉承公众联盟链整合资源、交换价值、服务公众的理念，致力于成为链接多个垂直行业领域的分布式商业基础设施，促进泛行业、跨机构、跨地域间的身份认证和数据合作。

模块介绍
--------

WeIdentity目前主要包含两大模块：WeIdentity DID以及WeIdentity Credential。

分布式身份标识 (WeIdentity DID)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

传统方式中，用户的注册和身份管理完全依赖于单一中心的注册机构；随着分布式账本技术（例如区块链）的出现，分布式多中心的身份注册、标识和管理成为可能。
WeIdentity DID模块在\ `FISCO-BCOS区块链底层平台 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/>`_\ 上实现了一套符合\ `W3C DID规范 <https://w3c-ccg.github.io/did-spec/>`_\ 的分布式多中心的身份标识协议，使实体（人或物）的现实身份实现了链上的身份标识；同时，WeIdentity DID给与Subject（人或者物）直接拥有和控制自己身份ID的能力。

WeIdentity DID秉承以下设计理念：

.. raw:: html

    <embed>
      <table style="width:100%;border-collapse:collapse">
         <tr>
            <th width="100">目标</th>
            <th>说明</th>
         </tr>
         <tr>
            <td>多中心</td>
            <td>分布式多中心的ID注册机制，摆脱对传统模式下单一中心ID注册的依赖</td>
         </tr>
         <tr>
            <td>开源开放</td>
            <td>技术方案完全开源，面向政府、企业、开发者服务</td>
         </tr>
         <tr>
            <td>隐私保护</td>
            <td>实体的现实身份和可验证数字凭证的内容进行链下存储。支持实体将信息最小化或者选择性披露给其他机构，同时防止任何第三方反向推测出实体在现实世界或其他场景语义中的身份</td>
         </tr>
         <tr>
            <td>可移植性</td>
            <td>基于WeIdentity规范，数据可移植至遵循同样规范的其他平台，兼容业务主流区块链底层平台</td>
         </tr>
         <tr>
            <td>互操作性</td>
            <td>提供标准化接口，支持跨链、跨平台互操作</td>
         </tr>
         <tr>
            <td>可扩展性</td>
            <td>保证操作性，可移植性或简单性的情况下，数据模型可以通过多种不同方式进行扩展</td>
         </tr>
      </table>
      <br />
    </embed>



可验证数字凭证 (WeIdentity Credential)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

现实世界中存在着各种各样用于描述实体身份、实体间关系的数据，如身份证、行驶证、存款证明、处方、毕业证、房产证、信用报告等。WeIdentity Credential提供了一整套基于\ `W3C VC规范 <https://w3c.github.io/vc-data-model/>`_\ 的解决方案，旨在对这一类数据进行标准化、电子化，生成可验证、可交换的「凭证」（Credential）。

WeIdentity支持认证机构自行注册标准化凭证模板，共同丰富公众联盟链的生态。

更多
^^^^

*
   `案例及场景 <https://weidentity.readthedocs.io/zh_CN/latest/docs/use-cases.html>`_

*
   `WeIdentity规范文档 <https://weidentity.readthedocs.io/zh_CN/latest/docs/weidentity-spec.html>`_


开始使用
--------

WeIdentity目前支持基于FISCO-BCOS运行，并提供JAVA SDK供调用。具体的规范文档、安装部署和SDK使用指引请参考如下文档：


.. raw:: html

    <embed>
      <table style="border-collapse:collapse">
         <tr>
            <th width="20%">集成方法</th>
            <th width="30%">文档入口</th>
            <th width="50%">当前状态</th>
         </tr>
         <tr>
            <td style="text-align:center"><a href="https://github.com/WeBankFinTech/weid-java-sdk">JAVA SDK</a></td>
            <td>
               <ul>
                 <li><a href="https://weidentity.readthedocs.io/zh_CN/latest/docs/weidentity-installation.html">安装部署文档</a></li>
                 <li><a href="https://weidentity.readthedocs.io/projects/javasdk/zh_CN/latest/docs/weidentity-java-sdk-doc.html">SDK使用文档</a></li>
               </ul>
            </td>
            <td>
               <a href="https://travis-ci.org/WeBankFinTech/weid-java-sdk"><img src="https://travis-ci.org/WeBankFinTech/weid-java-sdk.svg?branch=develop" /></a>
               <a href="https://github.com/WeBankFinTech/weid-java-sdk/releases/latest"><img src="https://img.shields.io/github/release/WeBankFinTech/weid-java-sdk.svg" /></a>
               <a href="https://search.maven.org/search?q=g:%22com.webank%22%20AND%20a:%22weid-java-sdk%22"><img src="https://img.shields.io/maven-central/v/com.webank/weid-java-sdk.svg?label=Maven%20Central" /></a>
               <a href="https://app.codacy.com/app/webankadmin/weid-java-sdk?utm_source=github.com&utm_medium=referral&utm_content=WeBankFinTech/weid-java-sdk&utm_campaign=Badge_Grade_Dashboard"><img src="https://api.codacy.com/project/badge/Grade/9fc044b36fff4985bd69f1232380d5ee" /></a>
               <a href="https://www.codefactor.io/repository/github/WeBankFinTech/weid-java-sdk"><img src="https://www.codefactor.io/repository/github/WeBankFinTech/weid-java-sdk/badge" /></a>
               <a href="https://codecov.io/gh/WeBankFinTech/weid-java-sdk"><img src="https://codecov.io/gh/WeBankFinTech/weid-java-sdk/branch/master/graph/badge.svg" /></a>
               <a href="https://www.gnu.org/licenses/lgpl-3.0"><img src="https://img.shields.io/badge/license-GNU%20LGPL%20v3.0-blue.svg" /></a>
            </td>
         </tr>
      </table>
      <br />
    </embed>


联系我们
--------

邮箱：weidentity@webank.com
