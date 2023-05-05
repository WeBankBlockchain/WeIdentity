## 前言

### Weidentity

  - 一套分布式多中心的技术解决方案，可承载实体对象（人或者物）的现实身份与链上身份的可信映射、以及实现实体对象之间安全的访问授权与数据交换

  - 包含的两大模块：**WeIdentity DID**以及**WeIdentity Credential**



### DID

  - 基于区块链技术的分布式数字身份是一种自我主权的、可验证的、新型数字身份

  - W3C为这种身份定义了 "分布式数字身份标识符规范"（Decentralized ID，DID）

    > 一种新型的全球标识符

  - 分布式标识符的用途包括以下两个方面

    1. 使用标识符来标识 DID  主体（人员、组织、密钥，服务和其他一般事物）的特定实例

    2. 促进实体之 间创建持久加密的专用通道，无需任何中心化注册机制，它们可以用于诸如凭证交 换和认证

  - 一个实体可 以具有多个 DID，甚至与另一个实体的每个关系可以关联一个或多个DID（成对假名 和一次性标识符）以保护隐私性，身份所有者通过证明拥有与绑定到该 DID的公钥相 关联的私钥来建立 DID 的所有权



## 数据授权共享案例

### 案例背景

  > 基于 WeIdentity的SDK进行医疗用户信息管理的代码示例



在医疗领域中，如何保护用户的个人健康信息，并确保其隐私和安全。传统的医疗信息管理方式存在着信息不透明、数据集中存储、隐私泄露等问题。而基于区块链技术的身份认证和数据管理解决方案，如WeIdentity，可以提供分布式、去中心化、加密保护的数据管理方式，增强了用户对其个人健康信息的掌控权和隐私保护



### 数据结构

1. 描述医疗用户信息的 JSON 格式 的 Schema（模式定义），作为通用数据结构使用

2. 结构内包含了三个主要部分：基本用户信息（BasicInfo）、健康状态信息（HealthStatus）和医学历史信息（MedicalHistory）

  ```JSON
{
    "$schema": "http://json-schema.org/draft-04/schema#",
    "description": "Medical User Information",
    "title": "User Data",
    "type": "object",
    "properties": {
        "BasicInfo": {
            "description": "Basic User Information",
            "type": "object",
            "items": {
                "type": "object",
                "properties": {
                    "name": {
                        "description": "User Name",
                        "maxLength": 30,
                        "type": "string"
                    },
                    "age": {
                        "description": "User Age",
                        "type": "integer"
                    },
                    "birth": {
                        "description": "User Birth",
                        "type": "string"
                    },
                    "gender": {
                        "description": "User Gender",
                        "enum": [
                            "MALE",
                            "FEMALE"
                        ],
                        "type": "string"
                    }
                },
                "required": [
                    "name",
                    "age",
                    "birth",
                    "gender"
                ]
            }
        },
        "HealthStatus": {
            "description": "Health Status Information",
            "type": "object",
            "properties": {
                "height": {
                    "description": "User Height",
                    "type": "number"
                },
                "weight": {
                    "description": "User Weight",
                    "type": "number"
                },
                "blood_type": {
                    "description": "User Blood Type",
                    "enum": [
                        "A",
                        "B",
                        "O",
                        "AB",
                        "OTHER"
                    ],
                    "type": "string"
                },
                "blood_pressure": {
                    "description": "User Blood Pressure",
                    "type": "string"
                }
            },
            "required": [
                "height",
                "weight",
                "blood_type",
                "blood_pressure"
            ]
        },
        "MedicalHistory": {
            "description": "Medical History Information",
            "type": "array",
            "items": {
                "type": "object",
                "properties": {
                    "date": {
                        "description": "Date of the medical event",
                        "type": "string"
                    },
                    "description": {
                        "description": "Description of the medical event",
                        "type": "string"
                    }
                },
                "required": [
                    "date",
                    "description"
                ]
            }
        }
    }
}
```

1. 结构数据释义：

  - 基本用户信息（BasicInfo）

    - 该部分包含了基本的用户信息：姓名（name）、年龄（age）、生日（birth）和性别（gender）四个属性

    - name 属性为字符串类型，最大长度为30个字符
age 属性为整型
birth 属性为字符串类型
gender 属性为字符串类型，取值仅为 "MALE" 或 "FEMALE"

  - 健康状态信息（HealthStatus）

    - 该部分包含了用户的身体健康状态信息，包括身高（height）、体重（weight）、血型（blood_type）和血压（blood_pressure）四个属性

    - height 属性和 weight 属性均为数字类型

      blood_type属性为字符串类型，取值只能是 "A"、"B"、"O"、"AB" 或 "OTHER";

      blood_pressure属性为字符串类型

  - 医疗历史信息（MedicalHistory）

    - 该部分是一个数组类型，包含了多个对象（可随医用诊断次数而增加数据内容），每个对象代表一个医疗事件，包括事件日期（date）和事件描述（description）两个属性

    - date 属性为字符串类型

      description 属性为字符串类型

1. 该 Schema 指定了每个部分的必选属性，通过标准化和规范化用户数据的描述，可以为用户身份认证和数据授权提供基础，确保用户数据的安全性和一致性



### 授权设置

1. 用户可以使用 WeIdentity 生成自己的 DID，并在DID上建立与数据相关的授权规则。例如，用户可以设置只有特定的医疗应用程序可以访问和更改其健康状态数据，而其他应用程序只能查询这些数据

2. 具体示例：

  ```JavaScript
const WeIdentity = require('weidentity-sdk-js');
const did = 'did:weid:example:WeIdentity DID';
const basicAuth = {type: 'BasicAuth', nonce: '...'};
const accessControl = {
  "health_status": {
    "read": ["did:weid:example:WeIdentity DID"],
    "write": ["did:weid:medical_app:MedicalApp"]
  }
};
const accessToken = await WeIdentity.Signer.signRawTransaction({
  "auth": basicAuth,
  "data": accessControl
}, did);

```

1. 示例内容的相关定义：

  2. 通过 `require('weidentity-sdk-js')` 导入 WeIdentity SDK

  3. 定义了一个 DID 变量，其中包含了一个 WeID 格式的身份标识

  4. 定义了一个基本身份验证（BasicAuth）对象（basicAuth），其中包含了身份验证的类型和一个 nonce（用于增加请求的安全性）

  5. 定义了一个访问控制对象（accessControl），其中包含了一个示例的访问控制规则，规定了对于名为 "HealthStatus" 的资源的读写权限。其中，读权限限制为只允许`did:weid:example:WeIdentity DID`进行访问，写权限限制为只允许`did:weid:medical_app:MedicalApp`医用程序进行访问

  6. 通过调用`WeIdentity.Signer.signRawTransaction()`方法，使用指定的身份验证对象（basicAuth）和访问控制对象（accessControl）作为参数，以当前的 DID 进行签名，生成一个访问令牌（accessToken）这个访问令牌可以用于后续的访问控制操作，以便对受限资源进行读写操作

7. 在代码中，用户可使用 DID 来定义数据的访问控制规则

  - 在`accessControl`对象中，`HealthStatus`属性指定了访问控制规则的作用域

  - `read`和`write`属性分别指定了读和写的DID列表

  - 用户可以使用`accessToken`将授权规则保存到区块链上



### 数据访问

1. 应用程序可以使用用户授权生成的 accessToken 来访问数据

2. 具体示例：

  ```JavaScript
const WeIdentity = require('weidentity-sdk-js');
const accessToken = '...'; // user's access token
const healthStatus = await WeIdentity.Did.accessControlDecrypt({
  "accessToken": accessToken,
  "cipherText": "encrypted health status"
});
console.log(HealthStatus);
```

1. 示例内容的相关定义：

  2. 通过 `require('weidentity-sdk-js')` 导入了 WeIdentity SDK（同 → 授权设置）

  3. 定义了一个访问令牌（accessToken）变量，其中包含了用户的访问令牌，用于进行访问控制操作

    > 该accessToken是通过`WeIdentity.Signer.signRawTransaction()` 方法生成的

  1. 调用`WeIdentity.Did.accessControlDecrypt()`方法，将一个包含了加密的健康状态数据的密文（cipherText）作为参数传递给该方法。此方法会使用指定的访问令牌（accessToken）进行解密操作，并返回解密后的健康状态数据（HealthStatus）

2. 通过使用访问令牌对加密数据进行解密，在满足访问控制规则的前提下，获取到健康状态数据的明文内容，从而进行后续的医用业务处理



### 小述

- 定义通用数据结构，其包含用户相关医学信息，通过标准化和规范化用户数据的描述，确保用户信息的安全性、可靠性、一致性

- 通过定义数据模型和访问控制策略，实现对用户信息的合规访问和管理，从而保护用户的隐私和数据安全

