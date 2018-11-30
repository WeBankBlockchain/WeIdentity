# FAQ

---

技术问题 FAQ 列表：  

* [JAVA SDK FAQ](weidentity-java-sdk/faq.html)

---

- **“把数据主权回归用户”体现在哪些方面？**

数据主权意味着用户对数据的控制，主要体现在下面几个方面：

1. 用户控制自己的数据存储和交换过程。
2. 用户身份及与其相关的凭证、数据都是可移植的。
3. 用户可通过授权方式决定谁有权限访问自己的数据。
4. 针对某个具体的结构化数据，用户可选择性披露其中的某些字段，而数据验证方能判断整个结构化数据的有效性与真实性。

---

- **在公链环境和联盟链环境下，目前该项目面临的挑战是什么？**

从架构设计与技术实现角度考虑，WeIdentity项目可以支持公有链环境和联盟链环境。

如果选择公有链环境，挑战主要有两个方面：

1. 用户身份认证的问题：公有链倾向于“去中心化”，但身份认证的过程是需要解决现实世界的人与网络世界的ID的关联问题，在“去中心化”的环境下，这将构成巨大的挑战；
2. 商业模式探索的问题：公有链目前并没有提出一种合法合规的商业模式来持续维护和运营这个生态；

如果选择联盟链环境，挑战主要在于需要在不同的场景和领域找到对应的关键角色的合作伙伴，共同探索商业模式。

考虑到“个人身份认证”、“个人数据存储”、“数据流转与交换”这些关键模块均需要权威机构的参与，且考虑商业模式需合法合规，我们推荐联盟链环境。

---

- **如果要用接入KYC，需要接入什么机构？现在已经接入了该机构吗？**

WeIdentity项目中提及的KYC过程分为两步：

1. 将现实生活中的人与现实生活中的号码关联起来，比如人和身份证号、人和银行卡号、人和社交网站账号等，这个过程一般由权威机构或企业完成，比如公安局、商业银行、运营方等；
2. 将现实生活中的号码与WeIdentity DID关联起来，比如社交网站账号和WeIdentity DID，这个过程在第1步完成后，由对应的机构来完成；

WeIdentity并不会改变现实世界中KYC的流程，而是与之结合，具体是否需要接入、以及接入什么机构，由业务场景决定，WeIdentity并不会为业务场景分派或指定特定的机构。

---

- **WeIdentity能提供哪些方面的凭证？这些凭证分别依托什么样的权威机构？举例说明？**

WeIdentity项目设计了一套完整的凭证定义机制，目标是为了将现实世界中所有的凭证生成对应的WeIdentity凭证。现实世界中的凭证分为两大类：

1. 权威型凭证：身份证、驾照、护照、学位证、银行交易流水证明、电子处方等，这些凭证的信息理论上均可电子化、结构化表示出来，并且加上权威机构的数字签名即可生成WeIdentity凭证；
2. 自定义凭证：授权凭证、请柬、借据等等，这些凭证与权威型凭证的区别仅仅在于数字签名是由个人签发的，而非权威机构签发；

因此，WeIdentity是依托于现实世界的凭证来生成的，具体能提供哪些凭证、这些凭证依托于什么权威机构是依托于现实世界的规则。

---

- **WeIdentity能否用于“存证/供应链/贸易/文化/游戏”等场景的应用，怎么用？**

WeIdentity能用于一切涉及到身份认证、数据交换的场景，具体用法需深入结合对应场景的需求。

---

- **如果详细的数据放到链下，怎么获取详细数据？**

涉及隐私的数据都是在链外私有存储，获取详细数据有两种模式：
1. 用户通过User Agent提供的服务下载详细数据，线上传输或线下二维码模式提交给数据使用方；
2. 用户通过User Agent授权数据访问，数据使用方与数据提供方建立数据传输通道进行数据传输。

---

- **新用户/新机构怎么登记到链上？**

新用户不直接与链交互，而是通过User Agent代理接入上链；
新机构登记上链需考虑业务场景以及对应角色，有以下建议：
1. 业务发起方机构需直接部署节点组链，并为其他没有部署节点的机构提供开放平台的接入方式；
2. 其它业务参与方机构可在获得业务发起方允许的情况下，参与到联盟链中并部署区块链节点，或通过业务发起方提供的开放平台方式接入联盟链。

---

- **如何保证权威机构的“权威性”，例如：能够长期地、稳定地提供准确的证明和相关数据？如果不能提供，怎么治理？**

WeIdentity项目中提及的权威机构的定义一般都源自现实生活，其“权威性”来自于现实生活中的人对其的肯定与信任、政府部门对其职能的划分与管理，从而使其能长期、稳定的提供相关服务。当其职能发生变化时，WeIdentity应用场景的参与方也需要在达成某种业务共识的前提下作出相应的变更。

---

- **有哪些机构可以参与共识，机构参与的判定标准是什么？**

由具体业务场景和机构所扮演的角色共同决定，根据场景需要，数据提供方、数据使用方、用户代理都可以参与共识。

---

- **生成的WeIdentity DID如何进行数据迁移？**

1. WeIdentity SDK提供数据导出接口，可以以json格式导出用户（人或者物）的WeIdentity Document完整数据，供User Agent数据迁移时使用;
2. WeIdentity SDK也提供数据导入接口，从其他业务平台导出的数据，可以在支持WeIdentity的业务平台导入，即按照提供的WeIdentity Document和公私钥，重新在链上创建WeIdentity Document。  
当用户需要将自己的数据从一个业务平台导出到另外一个业务平台，或者运行WeIdentity的联盟链需要做整体数据迁移的时候，可以使用上面提到的数据导入导出接口。例如某用户想将业务平台A的数据迁移到业务平台B，则业务平台A可以跟业务平台B基于自己的业务需要，基于业务规则，业务平台A和业务平台B通过接口对接，进行相应的数据迁移。

---

- **生成的Credential如何进行数据迁移？**

WeIdentity项目中提及的Credential是遵循W3C Verifiable Credential规范实现的，根据不同的业务场景，用户选择不同的机构进行私有存储。WeIdentity项目会实现统一的“Credential导出”与“Credential导入”接口，在得到用户授权的情况下，机构可调用相应的接口实现Credential的导出与导入，从而实现数据迁移。

---

- **是否支持批量存储？**

目前没有批量接口。后续我们将会提供相关批量接口。

---

- **WeIdentity DID和标准化DID协议的异同是什么？**

WeIdentity DID基于W3C的DID规范，实现了一套分布式多中心的身份标识协议（需依托分布式账本作为WeIdentity的底层运行平台），使实体（人或物）的可以在分布式账本上标识和鉴权其身份；目前W3C DID规范在发展，WeIdentity DID也在发展。

---

- **CPT到底是什么，能否举一个详细的例子？如何生成我自定义的CPT？**

CPT（Claim Protocol Type，也即凭证声明类型）可以理解为各类凭证（Credential）的模板定义结构，例如驾照这类Credential的数据格式跟学位证这类Credential的数据格式肯定是不一样的，所以各类Credential需要定义自己的CPT类型。当机构想在WeIdentity生态发行一种凭证（例如某公司想基于WeIdentity发行员工使用的门禁卡），则可以定义这种门禁卡的数据格式，即定义一种CPT，然后注册到WeIdentity。

---

- **获取详细数据时如何进行鉴权，以确认确实是已经授权过的请求？**

在可信数据交换的场景下，当某机构A需要使用用户X在机构B处存储的数据M。机构A可以获取用户的授权Credential（可以通过 [CPT101](https://weidentity.readthedocs.io/zh_CN/stable/docs/cpt-templates.html#cpt101) 来实现，也可以自行定义符合自己业务需求的 CPT ）。

可信数据交换规范定义了数据获取接口和数据授权提供接口的规范。
机构B需要实现数据授权提供接口，机构A通过数据获取接口向机构B请求这个数据，请求中携带用户授权数据使用的Credential，机构B验证Credential正确后返回数据。

---

- **如果一个发证机构的私钥丢失，要怎么处理？个人的私钥丢失呢？**

机构和个人的WeIdentity DID都通过私钥来控制，如果私钥丢失，需要通过Recovery机制来支持重置WeIdentity的公钥。Recovery机制要求WeIdentity DID持有人设置相应的恢复人。  
未来会支持恢复人的多种模式，例如某个列表中任意一个恢复人可以恢复，或者需要收集列表中至少N个签名才可以恢复。

---

- **已经发出的凭证（Credential）如何撤销和重新生成？**

WeIdentity Credential支持撤销操作，由发行这个Credential的机构或者人来执行。
Credential的重新生成则为重新发行一个Credential ID不相同的全新Credential。同时Credential支持更新和过期时间延期等操作。

---

- **Credential（凭证）有没有伪造的可能，如何防伪造？**  

目前 Credential（凭证） 使用 ECDSA 签名，未来会支持 RSA。链上证明被伪造的可能性，即特定长度密匙的 ECDSA 或 RSA 秘钥被攻破的肯能性。在私钥不泄露，且密匙未被攻破的情况下，目前不存在被伪造的可能性。

> 参见：  
> * [how-big-an-rsa-key-is-considered-secure-today](https://crypto.stackexchange.com/questions/1978/how-big-an-rsa-key-is-considered-secure-today/1982#1982)  
> * [how-much-stronger-is-rsa-2048-compared-to-rsa-1024](https://crypto.stackexchange.com/questions/8971/how-much-stronger-is-rsa-2048-compared-to-rsa-1024?noredirect=1&lq=1)  
> * 2048-bit 长的RSA私钥,在2030年以前是足够安全的。参见：[Asymmetric_algorithm_key_lengths](https://en.wikipedia.org/wiki/Key_size#Asymmetric_algorithm_key_lengths)  
> * [目前RSA因式分解的记录是 768 bit（这个挑战目前已经停止）](https://en.wikipedia.org/wiki/RSA_numbers#RSA-768)  
> * [ECDSA vs RSA (可以直接看Conclusion)](https://www.ssl.com/article/comparing-ecdsa-vs-rsa/)
    
> 注：  
> * 如果假设量子计算机是可行的，任何 ECDSA 和 RSA 密钥都可使用Shor算法攻破。参加：[RSA key length vs. Shor's algorithm](https://security.stackexchange.com/a/37638/18064)，
[Quantum_computing_attacks](https://en.wikipedia.org/wiki/Elliptic-curve_cryptography#Quantum_computing_attacks)
