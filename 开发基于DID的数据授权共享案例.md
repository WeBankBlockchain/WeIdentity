# **Task 20**

## **任务** 

开发基于DID的数据授权共享案例(定义具体且通用的数据结构，在数据的拥有者授权下某些应用方可以更改或查询拥有者的部分数据)

## **设计**

#### **设计思路**

1.数据结构定义：需要定义一个通用的数据结构，包含拥有者的身份信息和关键数据字段。这些数据字段可以根据具体应用场景进行扩展和定制化，例如教育经历、项目经验、个人健康信息等。同时，还需要考虑数据的格式和存储方式。

2.DID标识符生成：为每个数据拥有者生成DID标识符，用于唯一标识该拥有者，并与其它相关信息一起存储在链上或其他分布式存储系统中。DID标识符可以使用Decentralized Identifiers (DIDs)标准进行生成和管理。

3.授权管理与验证：拥有者可以通过智能合约实现对自己的数据进行授权管理，即授权特定的应用方查询或修改自己的部分数据。同时，在数据被访问时，需要进行授权验证，以确保只有被授权的应用方才能访问数据。

4.数据传输与加密：在数据传输过程中，需要采用加密算法对数据进行保护，防止数据泄露或篡改。同时，数据的传输方式也需要考虑安全性和效率性的平衡，例如采用点对点传输、区块链存储等方式。

5.数据访问日志与监管：为了保障数据的安全性和合规性，需要记录数据的访问日志，并建立监管机制进行监督和管理。同时，还需要考虑数据访问权限的撤销和失效机制，以保证数据的可控性和安全性。

#### **设计图**

![image-20230530105353141](开发基于DID的数据授权共享案例.assets/image-20230530105353141.png)

#### **合约代码**

```solidity
pragma solidity ^0.8.0;

contract Credential {
    address private issuer; // 颁发机构地址

    uint256 totalCredentialId; //凭证总发行量

    struct CredentialData {
        uint256 credentialId; // 凭证ID
        address wid; // 凭证拥有者的地址(wid)
        string dataHash; // 凭证数据的hash
    }

    //用户数据,0无权限，1仅查看，2可修改  用户地址=》被授权用户地址=》权限
    mapping(address => mapping(address => uint)) public idCard; // 用户身份证授权
    mapping(address => mapping(address => uint)) public phoneNumber; // 用户电话号码授权
    mapping(address => mapping(address => uint)) public email; // 用户邮箱授权
    mapping(address => mapping(address => uint)) public education; // 用户学历信息授权
    mapping(address => mapping(uint => uint)) public learningExperience; // 用户学习项目授权
    mapping(address => mapping(uint => uint)) public projectExperience; //  用户项目经历授权
    mapping(address => mapping(uint => uint)) public enterpriseContract; // 用户企业合同授权

    mapping(address => CredentialData) public credentials; // 存储每个用户的CredentialData
    mapping(address => mapping(uint => bool)) public isCredentialOwner; // 是否是数据拥有者

    event CredentialIssued(
        uint256 credentialId,
        address indexed owner,
        address indexed issuer
    );
    event CredentialUpdated(uint256 credentialId, string newData);
    event CredentialApprovalChanged(uint256 credentialId, bool isApproved);

    constructor() {
        issuer = msg.sender;
    }

    modifier onlyIssuer() {
        require(
            msg.sender == issuer,
            "Only the authority can call this function"
        );
        _;
    }

    modifier onlyCredentialOwner(address _owner, uint _credentialId) {
        require(
            isCredentialOwner[_owner][_credentialId],
            "Only credential owner can call this function"
        );
        _;
    }

    // 允许颁发机构更新颁发机构的地址
    function setIssuer(address _issuer) external onlyIssuer {
        issuer = _issuer;
    }

    // 颁发机构可以给每个用户颁发对应的凭证
    function issueCredential(
        address user,
        string memory dataHash
    ) external onlyIssuer {
        CredentialData storage newCredential = credentials[user];
        newCredential.credentialId = totalCredentialId;
        newCredential.wid = user;
        newCredential.dataHash = dataHash;

        emit CredentialIssued(totalCredentialId, user, issuer);
        totalCredentialId++;
    }

    // 对用户数据进行查看和操作的时候，先验证hash是否一致
    function verifyHash(
        address user,
        string memory dataHash
    ) internal view returns (bool) {
        require(credentials[user].credentialId != 0, "user has no credentials");
        return
            keccak256(abi.encodePacked(credentials[user].dataHash)) ==
            keccak256(abi.encodePacked(dataHash));
    }

    /**
     * @dev 允许用户授权其他用户访问自己的信息
     * @param user 被授权用户
     * @param credentialId 用户IDcredentialId
     * @param accessLevel 授权状态
     * @param infoIndex 信息索引
     * @param learningExperienceId 学习经历ID
     * @param projectExperienceId 项目经历ID
     * @param enterpriseContractId 企业合同ID
     */
    function authorizeAccess(
        address user,
        uint256 credentialId,
        uint[] memory accessLevel,
        uint[] memory infoIndex,
        uint learningExperienceId,
        uint projectExperienceId,
        uint enterpriseContractId
    ) external onlyCredentialOwner(msg.sender, credentialId){
        validateCredential(user, credentialId);
        require(
            accessLevel.length == infoIndex.length,
            "array length mismatch"
        );

        for (uint i = 0; i < infoIndex.length; i++) {
            require(
                infoIndex[i] >= 1 && infoIndex[i] <= 7,
                "invalid information index"
            );

            if (infoIndex[i] == 1) {
                idCard[msg.sender][user] = accessLevel[i];
            } else if (infoIndex[i] == 2) {
                phoneNumber[msg.sender][user] = accessLevel[i];
            } else if (infoIndex[i] == 3) {
                email[msg.sender][user] = accessLevel[i];
            } else if (infoIndex[i] == 4) {
                education[msg.sender][user] = accessLevel[i];
            } else if (infoIndex[i] == 5) {
                learningExperience[msg.sender][
                    learningExperienceId
                ] = accessLevel[i];
            } else if (infoIndex[i] == 6) {
                projectExperience[msg.sender][
                    projectExperienceId
                ] = accessLevel[i];
            } else if (infoIndex[i] == 7) {
                enterpriseContract[msg.sender][
                    enterpriseContractId
                ] = accessLevel[i];
            } else {
                revert("invalid information index");
            }
        }
    }

    /**
     * @dev 检查用户是否有权限访问信息
     * @param user 被查看信息的用户
     * @param infoIndex 信息索引
     * @param learningExperienceId 学习经历ID
     * @param projectExperienceId  项目经历ID
     * @param enterpriseContractId  企业合同ID
     */
    function checkAccess(
        address user,
        uint infoIndex,
        uint learningExperienceId,
        uint projectExperienceId,
        uint enterpriseContractId
    ) public view returns (bool) {
        if (infoIndex == 1) {
            return idCard[user][msg.sender] > 0;
        } else if (infoIndex == 2) {
            return phoneNumber[user][msg.sender] > 0;
        } else if (infoIndex == 3) {
            return email[user][msg.sender] > 0;
        } else if (infoIndex == 4) {
            return education[user][msg.sender] > 0;
        } else if (infoIndex == 5) {
            return learningExperience[user][learningExperienceId] > 0;
        } else if (infoIndex == 6) {
            return projectExperience[user][projectExperienceId] > 0;
        } else if (infoIndex == 7) {
            return enterpriseContract[user][enterpriseContractId] > 0;
        } else {
            revert("invalid information index");
        }

        return false;
    }

    /**
     * @dev 撤销用户对其他用户信息访问授权
     * @param revokedUser 被撤销访问权限的用户
     * @param credentialId 用户credentialId
     * @param infoIndex 信息索引
     * @param learningExperienceId 学习经历ID
     * @param projectExperienceId 项目经历ID
     * @param enterpriseContractId 企业合同ID
     */
    function revokeAccess(
        address revokedUser,
        uint256 credentialId,
        uint infoIndex,
        uint learningExperienceId,
        uint projectExperienceId,
        uint enterpriseContractId
    ) external onlyCredentialOwner(msg.sender, credentialId){

        if (infoIndex == 1) {
            idCard[revokedUser][msg.sender] = 0;
        } else if (infoIndex == 2) {
            phoneNumber[revokedUser][msg.sender] = 0;
        } else if (infoIndex == 3) {
            email[revokedUser][msg.sender] = 0;
        } else if (infoIndex == 4) {
            education[revokedUser][msg.sender] = 0;
        } else if (infoIndex == 5) {
            learningExperience[revokedUser][learningExperienceId] = 0;
        } else if (infoIndex == 6) {
            projectExperience[revokedUser][projectExperienceId] = 0;
        } else if (infoIndex == 7) {
            enterpriseContract[revokedUser][enterpriseContractId] = 0;
        } else {
            revert("invalid information index");
        }
    }

    //允许凭证的拥有者更新其凭证的数据
    function updateCredentialData(
        uint256 credentialId,
        string memory newData
    ) external onlyCredentialOwner(msg.sender, credentialId){
        validateCredential(msg.sender, credentialId);
        require(verifyHash(msg.sender, newData), "data inconsistency");

        credentials[msg.sender].dataHash = newData;
        emit CredentialUpdated(credentialId, newData);
    }

    //允许任何用户查看特定凭证的详细信息
    function getCredentialInfo(
        address user,
        uint256 credentialId
    ) external view returns (uint256, address, string memory) {
        validateCredential(user, credentialId);
        CredentialData storage credential = credentials[user];
        return (credential.credentialId, credential.wid, credential.dataHash);
    }

    // 检查凭证是否有效
    function validateCredential(
        address user,
        uint256 credentialId
    ) internal view {
        require(credentials[user].credentialId != 0, "user has no credentials");
        require(
            credentialId < credentials[user].credentialId,
            "invalid credential id"
        );
    }
}


```

