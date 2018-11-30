# 如何运行单元测试

## LINUX 系统上通过 gradle 执行单元测试

### 前提条件

单元测试的运行需要提前安装部署好WeIdentity JAVA SDK，请参考 [WeIdentity JAVA SDK 安装部署文档](https://weidentity.readthedocs.io/zh_CN/stable/docs/weidentity-installation.html#)安装部署WeIdentity。
  
### 流程

下载源代码后，以 `weidentity-java-sdk` 根目录为起点：

1, 进入dist/conf目录。

```shell
cd dist/conf
```

2, 将生成好的 `applicationContext.xml` 复制到 `src/test/resources` 目录，WeIdentity 安装部署完会自动生成并配置好 `applicationContext.xml`，所需的节点配置和合约地址配置已完成,可以直接使用。

```shell
cp applicationContext.xml  ../../src/test/resources/
```

3, 将生成好的 `ca.crt` 和 `client.keystore` 复制到 `src/test/resources` 目录,
     这两个证书是 WeIdentity 运行所需要的 SDK 证书

```shell
cp ca.crt client.keystore  ../../src/test/resources/
```

4, 回到项目根目录，执行测试命令。

```shell
cd ../../
gradle test
 ```