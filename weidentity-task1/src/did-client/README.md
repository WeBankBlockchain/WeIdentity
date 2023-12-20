# did client

did-client的代码实现了整个项目架构中的业务系统，基于go语言（go 1.20）实现，通过`gin`框架实现http服务。

## 项目结构

代码目录结构如下：

```shell
├── README.md
├── config
│   └── config.go
├── config.yaml
├── go.mod
├── go.sum
├── httpx
│   ├── handler.go
│   └── router.go
└── main.go
```
- httpx: 对外提供http服务，供前端调用，router是具体的路由方法，handler是对应的实现；
- config：配置管理功能，读取`config.yaml`文件，该文件中有各配置项的说明；
- main.go: 方法入口。

## api接口

该客户端提供的主要接口。

1. loginWithDid did登录接口

    该接口调用后跳转到did登录的认证服务，登录成功后，did认证服务会返回code码。

2. callback 回调接口

    该接口将`loginWithDid`中获取到的code拼接到url上，并重定向至前端地址，该步骤是为了把code传给前端页面。

3. loginWithCode 根据code调用中心系统接口获取token

    该接口通过调用完成code换取token的步骤。

## 使用方式

简单介绍系统如何编译和运行。

### 启动服务

```shell
# 确保系统已经安装go环境
export GOPROXY="https://goproxy.cn"
export GO111MODULE="on"
export CONF_PATH=./conf_file
go run main.go
```
