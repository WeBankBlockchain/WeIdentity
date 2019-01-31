### v1.1.0 (2019-01-31)

Features:
1.The first version implementation of selective disclosure.
2.The first version implementation of evidence.
3.Download weidentity-contract.jar from maven instead of local.
4.Use JSON string as parameter in class cptServiceImpl and credentialServiceImpl instead of plain string.

Documents modifications:
1.Move SDK document from WeIdentity project to this project.
2.Use embedded mermaidjs plugin to render sequences diagram instead of PNG files.

### v1.0.3 (2018-12-20)

* Features:
1. Add cache rules in travis-ci config. Only apply CI build on master, develop and tag.
2. Auto release when add a new tag.
3. Minor issues fix in gradle build configuration.

* Bugfixes:
1. Fix issues found by code safe scan.
2. Fix issues found by checkstyle and spotbugs.

### v1.0.2 (2018-12-14)

* Features:
1. Remove codecov patch check
2. Modify FISCO-BCOS sdk ca.crt and client.keystore file
3. Use correct badge url from WeBankFinTech

* Bugfixes:
1. Fix issues found by codacy.

### v1.0.1 (2018-11-30)

* 新增功能：
1. 增加了对Travis CI（travis-ci.org）, Maven中央仓库, 代码质量检测（codefactor.io & codacy.com）, 代码覆盖率检测（codecov.io）等工具的集成。
2. 增加了完整的Demo及单元测试的集成。

* 问题修复：
1. 修复了大量代码格式问题及输入参数错误问题。

* Features:
1. Added Travis CI (travis-ci.org), Maven central repository, code quality tools (codefactor.io & codacy.com), and code coverage tools (codecov.io) integration.
2. Added extensive Demo and Unit Test integration.

* Bugfixes:
1. Miscellenous code style and input parameter validation issues are fixed.

### v1.0.0 (2018-10-30)
首次release.

* 新增功能:
1. WeIdService相关接口及实现
2. AuthorityIssuerService相关接口及实现
3. CptService相关接口及实现
4. CredentialService相关接口及实现
5. 支持FISCO-BCOS相关操作接口，包括合约部署、调用，支持通过配置私钥方式进行FISCO-BCOS合约调用
6. 支持一键快速部署

First release for weidentity-java-sdk, with the core features of WeIdentity Spec.

* Features:
1. WeIdentityService Interface and corresponding implementation.
2. AuthorityIssuerService Interface and corresponding implementation.
3. CptService Interface and corresponding implementation.
4. CredentialService Interface and corresponding implementation.
5. Support set the whole environment upon FISCO-BCOS, including contract deploying, contract function call.
6. Provide a hassle-free installation tools for quick environment setup.
