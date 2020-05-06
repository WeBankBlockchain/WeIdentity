.. role:: raw-html-m2r(raw)
   :format: html


1. encrypt
-----------------------------

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.crypto.inf.CryptoService.encrypt
   接口定义: public String encrypt(String content, String key) throws EncodeSuiteException;
   接口描述: 根据不同类型加密算法对数据进行加密

.. note::
     注意：目前提供服务的加密算法有CryptoType.AES和CryptoType.ECIES, 加密返回数据为Base64字符串。ECIES加解密请通过build-tools获取libffi_ecies.so和WeDPR-ecies.jar


**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - content
     - String
     - Y
    - 需要加密的数据
     -UTF-8格式数据
   * - key
     - String
     - Y
    - 加密使用的秘钥
     -非对称秘钥请使用Base64处理


**接口返回**\ :   String;

**调用示例**

.. code-block:: java

   String key = "abc";
   String original = "123";
   // AES加密
   String encrypt = CryptoServiceFactory.getCryptoService(CryptoType.AES).encrypt(original, key);

   // ECIES加密
   key = "APOsCflGTsr7ltZBRRA5WS7KL8FzJ8NquybVadp2GsRVmtzTSEYSgW1i76jLOCTJoUPlB+J0KFTG3WKYoltMll0=";// weid公钥BASE64
   original = "123";
   String encrypt = CryptoServiceFactory.getCryptoService(CryptoType.ECIES).encrypt(original, key);


2. decrypt
-----------------------------

**基本信息**

.. code-block:: text

   接口名称: com.webank.weid.suite.api.crypto.inf.CryptoService.decrypt
   接口定义: public String decrypt(String content, String key) throws EncodeSuiteException;
   接口描述: 根据加密的Base64字符串进行解密，并返回原字符串

**接口入参**\ :

.. list-table::
   :header-rows: 1

   * - 名称
     - 类型
     - 非空
     - 说明
     - 备注
   * - content
     - String
     - Y
     - 待解密字符串
     -加密后并使用Base64处理的数据
   * - key
     - String
     - Y
     - 解密数据所使用的秘钥
     -非对称秘钥请使用Base64处理

**接口返回**\ :   String;

**调用示例**

.. code-block:: java

   String key = "abc"; //AES秘钥
   String encrypt = "xxxx";//密文数据
   // AES解密
   String decrypt = CryptoServiceFactory.getCryptoService(CryptoType.AES).decrypt(encrypt, key);

   key = "AMcwy+851eDtxY/1vcTtxttwqTaBfczp7Q7fL41fGCag"; // weid私钥BASE64
   encrypt = "xxxx";//密文数据
   // AES解密
   String decrypt = CryptoServiceFactory.getCryptoService(CryptoType.ECIES).decrypt(encrypt, key);
----