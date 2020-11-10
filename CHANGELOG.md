### V1.6.3-hotfix-1 (2020-08-26)
- Bugfixes:
1. Use batch block transaction receipts getter to improve evidence performance.

### V1.6.5 (2020-08-14)
- Features:
1. Significantly improve build speed when use together with build-tools.
2. Support WeID owners to add/revoke public keys and authentications.
3. Allow to use public key ID to verify a CredentialPojo.
4. Authority Issuer now requires to call recognizeAuthorityIssuer() from admin/committee.

- Bugfixes:
1. Fix a minor issue in manipulating multi-group operations.
2. Fix a minor issue in CNS configurations in multiple org cases.
3. Orders of WeID public keys and authentications are preserved when added/removed.

### V1.6.4 (2020-06-16)
- Bugfixes:
1. Update all dependencies to mitigate potential security risks.

### V1.6.3 (2020-06-01)
- Features:
1. Multi-group deployment support to separate Evidence and other smart contracts.
2. Users can choose Evidence processing mode (instant or batch-type).
3. Configurations in DB and properties can now be updated automatically.
4. Signature creation is now unified to use Ethereum type with Base64 encoding (v = 0/1).

- Bugfixes:
1. All WeID delegate calls now requires admin/committee privilege from private key.
2. BarCode and QR Code types are merged.
3. AMOP Listening now requires to specify topic.
4. LiteCredential's Hash and Signature's raw message now correctly includes issuanceDate.
5. Migrate to GitHub Actions for CI and pipeline.

### V1.6.2 (2020-05-07)
- Features:
1. CryptoService, built-in support for ECIES and RSA encryption and decryption.
2. Support delegate creation and modification of WeID.
3. Lite Credential support, a light-weight portable format which uses Secp256k1 universal signature.
4. Support Offline verification of Credential.
5. Allow customize public key types for WeID e.g. RSA public keys.

- Bugfixes:
1. Fix a DB Connection Pool initialization issue.
2. Fix a hash conflict issue in offline Evidence transactions.
3. Allow long blockchain operations correctly timed-out.
4. Fix an issue where credential's hash generation contains escape characters in inner Claim json.

### V1.6.1 (2020-04-12)
- Features:
1. QRCode transportation supports AMOP channel.
2. Evidence supports offline, delayed and timed/cron-job manner blockchain transactions.
3. Evidence supports batch creation transactions.
4. Visualized build-tools now supports batch evidence transactions display and management.

- Bugfixes:
1. Fix a data inconsistency issue when using AMOP channel in multi-active scenarios.

### V1.6.0 (2020-03-24)
- Features:
1. Endpoint service now supports reentrance and whitelist for remote servers.
2. Extra values in Evidence is now as log append-only style. The R/W efficiency of Evidence is also improved.
3. Data authorization scheme based on Endpoint service.
4. A new WeIdAuth module to support build of authenticated trusted channel between communicating parties.
5. Transportation module now supports Bar Codes and is refactored.
6. WeIdentity smart contracts deployment now supports CNS management.
7. A new Transmission module to support Cross-process Remote Method Invocation.

### V1.5.2 (2020-02-24)
- Features:
1. PDF Transportation now forbids content tampering during transportation.
2. Evidence now returns evidence hash instead of address (see API doc for details).
3. Evidence creation and query efficiency is greatly improved.


### V1.5.1 (2020-01-22)
- Features:
1. Queried CPT can be cached to local machine (require MySQL setup).
2. New interface generateHash(), accepting File, Credential and String as hashable input.

- Bugfixes:
1. Multiple miscellaneous bugfixes and development experience enhancements.

### V1.5.0 (2019-12-30)
- Features:
1. CredentialPojo support Zero-Knowledge-Proof based disclosure.
2. Trusted Timestamp creation based on any non-selectively-disclosed CredentialPojo.

- Bugfixes:
1. Fix multiple security risk issues from dependencies.
2. Fix multiple JDK version incompatibility issues.


### V1.4.2 (2019-12-10)
- Features:
1. Allow WeID owners to remove unused public keys and authentications in WeID Document.
2. Embedded CredentialPojo now supports change disclosure policy of inner CredentialPojo.
3. WeIdentity now supports deployment on different FISCO-BCOS Groups (check docs for details).

- Bugfixes:
1. Fix various issues when serializing Embedded CredentialPojo.
2. Bump jackson, jacoco and jmockit for security concerns.
3. WeIdentity can now work with JDK1.8+ up to openjdk13.
4. Fix broken Document URLs and simplify the document repository structure.

### V1.4.1 (2019-11-01)
- Bugfixes:
1. Fixed multiple potential leaks which might cause NPE.
2. Change default deposit signature algorithm when creating PDF to keccak256.
3. Fixed multiple invalid URL links in documentation.
4. Bump lombok to 1.18.10 to be compatible for OpenJDK11.

### V1.4.0 (2019-09-30)
- Features:
1. Supports PDF creation and transportation from CredentialPojo and Presentation.
2. Credential, CredentialPojo and CredentialWrapper now supports Evidence.
3. Credential supports multi-sign
4. Evidence supports multi-sign
5. Empty Evidence can be created with hash value appended separately.
6. Add an reference implementation of Endpoint Service (used w/ Rest Service).
7. Travis CI pipeline now resides on FISCO-BCOS 2.0.
8. Add an information collection command line tool.
9. Support data timeout in domain storage.

- Bugfixes:
1. Re-Selectively disclose a selectively disclosed Credential is disallowed.
2. Evidence Info fetched from getEvidence() now uses WeID instead of plain address.
3. Credential and CredentialPojo dates are now in second format.
4. Cipher suites are now unified in Secp256k1.
5. Fix miscellenous errors in sequence diagrams.

### v1.3.2 (2019-08-16)
- Features:
1. weid-java-sdk supports ci pipeline.
2. Add Command line tool to check AMOP health and WeID existence.
3. Persistence layer supports domain configuration.
4. Support expiration period for Presentation encryption key (24 hrs by default).
5. Transportation now supports Specify(), to allow only eligible WeIDs to access.
6. Add sequence diagrams to all new interfaces in WeID-Java-SDK API document.

- Bugfixes:
1. Credential Verification now requires CPT format checks.
2. Enhances the parameter validity check in all modules.


### v1.3.0 (2019-06-28)
- Features:
1. Official support for FISCO-BCOS 1.3 and 2.0 blockchain nodes and smart contracts by extracting a proxy layer.
2. Combine weidentity.properties and fisco.properties.
3. Support Gradle 5.x, offline compilation for weid-java-sdk and build-tools (docs attached).
4. Credential issuance date can be customized.
5. Credential adds a new interface to extract disclosed claim data directly.

- Bugfixes:
1. Fixed a bug where dates are not consistent after transfer by fromJson() and toJson().
2. Project renamed from weidentity-java-sdk to weid-java-sdk.
3. Credential Claim structure now requires "weid" as an obligatory attribute.
4. Added multiple unit test.

### v1.2.1 (2019-06-19)

- Features:
1. Remove spring dependencies in weid-java-sdk.
2. Optimize weidentity.properties and fisco.properties files.
3. Incorporate new random salt generator.
4. Support connection pool for persistence layer.
5. Modify docs.

- Bugfixes:
1. Fixed issue of printing exception log.

### v1.2.0 (2019-06-06)

- Features:
1. Add AMOP function to support communication between organizations by blockchain nodes.
2. Modify CreateWeId method to support automatically add public key to user's weidentity did document.
3. Add presentation support.
4. Support of generating QR code.
5. Support creating credential by POJO.

- Bugfixes:
1. Fixed issue of error result of parsing two event log in one trasaction.

### v1.1.2 (2019-05-22)

- Features:
1. ResponseData now contains blockchain transaction information

- Bugfixes:
1. CreateEvidence should use input DID instead of default private key for authentication
2. WeIdentity Contract dependency now targets to specific instead of latest versions

### v1.1.1 (2019-03-29)

- Features:
1. Add adaption of new changes in RestAPI service.
2. Modify @context field and add Credential Serilization interface
3. Add Evidence support in Build-tools
4. Add fundamental support of storage connectivity layer

- Bugfixes:
1. Fixed the query error in Multithread Synchronized Block Output
2. Fixed numerous image and URL errors in SDK documentation.
3. Multiple Code Quality improvements

### v1.1.0 (2019-01-31)

- Features:
1. The first version implementation of selective disclosure.
2. The first version implementation of evidence.
3. Download weidentity-contract.jar from maven instead of local.
4. Use JSON string as parameter in class cptServiceImpl and credentialServiceImpl instead of plain string.

- Documents modifications:
1. Move SDK document from WeIdentity project to this project.
2. Use embedded mermaidjs plugin to render sequences diagram instead of PNG files.

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
