# 如何贡献

---

非常感谢您对 WeIdentity 有兴趣。欢迎提交 Pull Request ，参与 bug 修复，文档优化，或者任何优化意见。

---

## 1. 发现 bug

使用过程中遇到的任何问题，可以查看 [FAQ](../docs/zh_CN/docs/faq.md) ，或者给我们提 [issue](https://github.com/webankopen/WeIdentity/issues)。

## 2. 优化建议

有任何优化建议，欢迎给我们提 [issue](https://github.com/webankopen/WeIdentity/issues)。

---

## 3. 贡献代码

### 3.1 开始之前

请先浏览WeIdentity工程遵循的相关规范，包括分支管理，代码规范，commit规范。

#### 分支管理

代码类工程我们使用 [git-flow](https://nvie.com/posts/a-successful-git-branching-model/) 进行分支管理。包括这些工程： [weidentity-java-sdk](https://github.com/webankopen/weidentity-java-sdk)， [weidentity-contract](https://github.com/webankopen/weidentity-contract)。  
文档类工程我们使用 [github-flow](http://scottchacon.com/2011/08/31/github-flow.html) 进行分支管理。包括这些工程： [WeIdentity](https://github.com/webankopen/WeIdentity)。

#### 代码规范

建议先浏览 [WeIdentity 代码规范](../docs/zh_CN/docs/styleguides/styleguides.md)。

#### 测试

所有提交的代码必须通过单元测试，如果提交的代码目前的单元测试用例无法覆盖，可以修改单元测试代码从而覆盖用例，或者在 Pull Request 里面说明情况，然后由我们来补充单元测试用例。详见“[如何运行单元测试](../docs/zh_CN/docs/how-to-run-unit-test.md)”。

#### Commit Messages

使用英文简要描述修改点，每一行描述一个修改点，并以 `*` 开头，如下所示：

```text
first release for weidentity-contract

* Smart Contract for DID identity management, allowing external
addresses which satisfy FISCO-BCOS specifications to be used as the
identification tag.
* Smart Contract for Committee Membership management, including data &
logic contracts.
* Smart Contract for Authority Issuer Membership management, including
data & logic contracts.
```

---

### 3.2 流程

1. Fork 希望提交代码修改的 repository 。例如，如果想要修改 [weidentity-java-sdk](https://github.com/webankopen/weidentity-java-sdk) ，则 Fork 这个 repository。

2. 创建新的分支，例如分支取名为 `feature/add-verfiy-logic` （分支名请使用 `feature/` 开头）。

    ```shell
    git checkout -b feature/add-verfiy-logic
    ```

3. 编写代码。

4. 运行单元测试（会自动运行单元测试，checkstyle，spotbugs）并通过。

5. Push 这个分支 `feature/verfiy-logic-bug-fix` 到自己的 repository。

6. 提交 Pull Request 到 repository 的 `develop` 分支， 并描述您的修改。例如描述修复的 bug，或者描述新增的功能（如果是较大的修改，建议在代码实现前提交一个[优化建议issue](https://github.com/webankopen/WeIdentity/issues)，提前描述自己的设计）。并标记任意一个 maintainer 进行 review。

7. maintainer 完成review，合并您的 Pull Request 到 `develop` 分支，然后我们会在下一个发布时间点将其合并到 `master` 分支。感谢您的贡献。
