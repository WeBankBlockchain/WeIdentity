# Java SDK FAQ

---

- **智能合约升级和 SDK 升级相关问题**  

详见[版本号管理]()。

- **Authoritzation是什么？**

这里要区分两种授权类型：
1. 一是 WeIdentity DID 层面的授权，即实体可以（暂时地）将自己的身份的部分或者全部控制权授权给另一个实体，另一个实体可以代替原实体进行相关的操作的过程。
2. 用户在A机构的数据需要流转到B机构，需要得到用户的授权，这是不同于1的机制。例如用户的处方信息存储在机构A，需要流转到机构B，这个时候用户可以授权这个数据流转，并叫授权操作上链。

- **执行某些接口的时候为什么要传入私钥？**

调用合约的时候需要调用方（WeIdentity DID持有者）的私钥，用来签名交易。签发Credential的时候亦然。

- **目前支持哪些密钥管理套件？**

目前，WeIdentity Java SDK使用ECDSA SECP256套件进行密钥生成、签名验证操作。未来会加入国密版本套件。

- **Smart Contract（智能合约）的如何升级？**

WeIdentity 智能合约是按分层设计的，分为：*数据层，逻辑层，权限层*。数据层合约被当做类似数据库的表来使用，充分预留的字段，所以数据层合约基本稳定。在大多数情况下，WeIdentity 智能合约的升级的时候只会升级逻辑层和权限层，所以升级的时候之前的数据完全保留，部署新的合约（需要传入升级前数据合约的合约地址）后，直接使用新的合约即可。如果需要升级数据层合约，则需要做数据迁移或者在逻辑层做特殊逻辑。  
更多信息请看[WeIdentity智能合约设计]()。

- **为什么要使用tx.origin？我听说它有很多问题！**

使用tx.origin是因为比起传统的RBAC/ABAC（Role/ABAC Based Access Control），WeIdentity的权限控制合约需要支持更大的可扩展性，以支持更多公众联盟链的参与成员实现不同的控制合约。在这种情况下，只有tx.origin才能稳定地追踪到调用者的WeIdentity DID，也即“WeIDBAC”（WeIDentity Based Access Control）。此外，在一个不需要token的区块链世界里，tx.origin有着比msg.sender更广泛的适用性。

- **如何查看当前WeIdentity JAVA SDK和WeIdentity Contract的版本号？**
