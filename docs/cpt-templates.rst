
.. _cpt-templates:

CPT例子
==========

单次文件转移凭证(CPT101)
~~~~~~~~~~~~~~~~~~~~~~~~

适用场景：用户授权后，用户的数据由一个机构给到另外一个机构。credential的ID用于标识本次文件转移。这一类Claim一般用户是issuer。

.. list-table::
   :header-rows: 1

   * - 角色
     - 说明
   * - 数据提供者
     - 数据提供者对用户的链外数据、链外资产或服务进行认证登记并负责其真实性。
   * - 数据使用者
     - 引导获取用户授权，通过授权代理获取到用户授权后，获取到用户的数据资产。
   * - 用户授权代理
     - 1.完成对用户的KYC 2.用户授权信息的上链。例如微众银行WeIdentity小程序就是用户授权代理的角色。
   * - 用户
     - 用户无法直接接入区块链，而是通过以上三方任意一方接入。用户的认证等级见图。


.. list-table::
   :header-rows: 1

   * - Properties
     - 说明
   * - @context
     - 用于描述CPT等信息
   * - version
     - 该CPT的版本，endpoint至少需要保证向后兼容性
   * - fileType
     - 文件类型的标识符，用于机构间互通，可以是CPT类型
   * - fileId
     - 文件的标识符，用于机构间互通，可以是某Credential的WeIdentity
   * - hash
     - 文件的hash
   * - receiver
     - 文件的使用者的WeIdentity
   * - signatureValue
     - 文件的hash
   * - validDate
     - 有效期起始日期
   * - expireDate
     - 凭证过期日期
   * - signature
     - 文件所有者的签名列表，是一个数组
   * - signature.type
     - 签名类型
   * - signature.created
     - 签名的创建时间
   * - signature.creator
     - 签名人的WeIdentity
   * - signature.nonce
     - 随机数
   * - signature.signatureValue
     - 签名的具体value，对整个Credential结构中除去signature字段的其他字段做签名


.. code-block:: javascript

   "Claim": {
     "@context": "https://weidentity.webank.com/cpt101/v1",
     "version": "v1",
     "id": "",
     "hash": "sdfwefwefwfawef2fasdvs3241e132112e1",
     "validDate": "2016-06-20T21:19:10Z",
     "expireDate": "2026-06-20T21:19:10Z",
     "signature": [{
       "type": "LinkedDataSignature2015",
       "created": "2016-06-18T21:19:10Z",
       "creator": "did:weid:2323e3e3dweweewew2",
       "domain": "www.diriving_card.com",
       "nonce": "598c63d6",
       "signatureValue": "BavEll0/I1zpYw8XNi1bgVg/sCneO4Jugez8RwDg/+MCRVpjOboDoe4SxxKjkC
     OvKiCHGDvc4krqi6Z1n0UfqzxGfmatCuFibcC1wpsPRdW+gGsutPTLzvueMWmFhwYmfIFpbBu95t501+r
       SLHIEuujM/+PXr9Cky6Ed+W3JT24="
     }]
   }

中国内地驾照(CPT***)
~~~~~~~~~~~~~~~~~~~~

.. list-table::
   :header-rows: 1

   * - Properties
     - 说明
   * - @context
     - 用于描述CPT等信息
   * - version
     - 该CPT的版本，endpoint至少需要保证向后兼容性
   * - id
     - 驾照的ID
   * - name
     - 驾照持有者姓名
   * - sex
     - 性别
   * - nationality
     - 国籍
   * - address
     - 地址
   * - class
     - 准驾车型
   * - issueDate
     - 初次领证日期
   * - validDate
     - 有效期起始日期
   * - expireDate
     - 过期日期


.. code-block:: javascript

   "Claim": {
     "@context" : "https://weidentity.webank.com/cpt100/v1",
     "version" : "v1",
     "id" : "",
     "weid" : "did:weid:1:0xdfsdf...."
     "name" : "张小明",
     "sex" : "男",
     "nationality" : "中国",
     "address" : "重庆",
     "class" : "C1",
     "issueDate" : "2010-06-20T21:19:10Z",
     "validDate" : "2016-06-20T21:19:10Z",
     "expireDate" : "2026-06-20T21:19:10Z",
   }

香港地区驾照(CPT***)
~~~~~~~~~~~~~~~~~~~~

数据结构待定

PADI潜水执照(CPT***)
~~~~~~~~~~~~~~~~~~~~

数据结构待定
