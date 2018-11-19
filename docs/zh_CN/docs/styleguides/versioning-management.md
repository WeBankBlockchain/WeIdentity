# 版本号管理

我们的版本号管理遵循 [Semantic Versioning 2.0.0](https://semver.org/)；
例如 v1.2.3，`MAJOR` 版本号是`1`，`MINOR` 版本号是2，`PATCH` 版本号是3；  
* `MAJOR` 版本号的变更可能会出现不兼容的 API 改变或者升级。
* `MINOR` 版本号变更会添加新 feature，同时保证向后兼容性。  
* `PATCH` 版本号变更主要是用于 bug 修复，同时保证向后兼容性。  

## SDK或者智能合约使用过程中的升级问题

如果WeIdentity使用过程中遇到了 bug，需要升级，可能会出现下面的几种情况：

SDK 搭配智能合约运行了一段时间，发现一些 bug，解决方案可能会出现下面的几种情况：

1. SDK 需要升级，智能合约不升级。如果 SDK 已经有版本修复了这个 bug，并且 SDK 的版本号只有 `MINOR` 和 `PATCH` 版本号跟当前使用的 SDK 不一致，因为这类 SDK 的升级保证了向后兼容性，从而肯定能兼容当前使用的智能合约，所以可以直接升级 SDK。例如使用 `weidentity-java-sdk-v1.2.3` 的过程中遇到了 bug，然后这个 bug 在 `weidentity-java-sdk-v1.3.0` 版本中得到了修复，则可以直接升级。
    > 注意:
如果这个时候没有找到向后兼容的 SDK 升级版本,例如使用 `weidentity-java-sdk-v1.2.3` 的过程中遇到了 bug, 然后这个 bug 在 `weidentity-java-sdk-v2.0.1` 版本得到了修复，这个时候如果升级SDK，可能会遇到接口不兼容或者不兼容当前使用的智能合约版本的情况。这个时候可以给我们提 issue，我们可以提供特定的 bugfix 版本。

2. SDK 不需要升级，智能合约需要升级。合约的升级同样满足版本号管理的约束，例如当前使用的 `weidentity-contract-v1.4.0` 有 bug，然后这个bug在 `weidentity-contract-v1.5.2` 版本中得到了修复。则可以直接升级合约。

3. SDK 和智能合约都需要升级。这种情况建议两者都升级到最新的 release 版本，最新版本的 SDK 一定兼容最新版本的智能合约。
