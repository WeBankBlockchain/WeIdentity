# did-server did认证系统

did-server是对业务系统进行认证、授权的系统，其核心工作原理是通过用户输入的did和签名信息，从链上获取公钥信息进行验证。

该系统需要实现oatuh2.0授权码模式的主要功能，包括：获取授权code、使用code交换token、token校验等功能。


## 项目说明

### 目录结构

代码目录结构如下：
```shell
./
├── Dockerfile
├── LICENSE
├── README.md
├── README_old.md
├── config
│   ├── app.go
│   ├── config.go
│   └── utils.go
├── config.yaml
├── did
│   ├── logic.go
│   └── model.go
├── go.mod
├── go.sum
├── main.go
├── pkg
│   ├── key
│   │   ├── key.go
│   │   ├── key_test.go
│   │   └── pub.key
│   └── session
│       └── session.go
├── server
│   ├── handler.go
│   ├── oauth.go
│   └── router.go
├── static
│   └── icon
│       ├── bootstrap-solid.svg
│       └── feather.svg
└── tpl
    ├── error.html
    └── login.html
```

- config：配置管理模块；
- config.yaml: 实际的配置文件内容；
- did：封装 `WeIdentity-Rest-Service` 的getWeIdDocument接口，通过did获取did doc；
- pkg：服务主要依赖包，包括验签、生成hash值、通过big Int导出公钥及session增删改查等功能；
- server: oauth的核心功能，包括 `authorize`、`token`、`login`等接口；
- tpl、static：前端的html页面及资源存储目录。

### 涉及技术

1. oatuh2.0协议的封装，基于 `github.com/go-oauth2/oauth2/v4` 包进行封装，完成oauth协议的主要功能；
2. http服务，基于 `github.com/gin-gonic/gin` 包进行封装，完成http服务的提供；
3. 密钥，基于 `github.com/ethereum/go-ethereum` 包进行封装；


### 如何运行？

1. 修改配置文件，标准配置文件如下：
```yaml
# 服务运行的http端口
httpPort: 10003
# session 相关配置
session:
  name: session_id
  secret_key: "kkoiybh1ah6rbh0"
  # 过期时间
  # 单位秒
  # 默认20分钟
  max_age: 1200
# oauth2 相关配置
oauth2:
  # access_token 过期时间
  # 单位小时
  # 默认2小时
  access_token_exp: 30
  # 签名 jwt access_token 时所用 key
  jwt_signed_key: "k2bjI75JJHolp0i"
  # oauth2 客户端配置
  # 数组类型
  # 可配置多客户端，这里要与业务系统（did-client目录）中的client id和secret一致
  client:
      # 客户端id 必须全局唯一
    - id: did_client_1
      # 客户端 secret
      secret: "did_54c6cc7f-a784-4255-aa9f-3e3dcd242669"
      # 应用名 在页面上必要时进行显示
      name: did应用
      # 客户端 domain
      # !!注意 http/https 不要写错!!
      domain: http://127.0.0.1:20000
      # 权限范围
      # 颁发的 access_token 中会包含该值 资源方可以对该值进行验证
      scope:
          # 权限范围 id 唯一
        - id: all
          # 权限范围名称
          title: "用户账号、手机、权限、角色等信息"
    - id: did_client_2
      secret: "did_1c3903b3-32f4-4703-85b0-bbca56ea0883"
      name: did应用2
      domain: http://127.0.0.1:20001
      scope:
        - id: all
          title: "用户账号、手机、权限、角色等信息"
# WeIdentity-Rest-Service
didServer:
  ip: "192.168.0.95"
  port: "6001"
```

将配置文件中涉及到的ip和port等信息，修改为你部署的地址，例如 `didServer`配置项的修改。

2. 直接编译运行

```shell
# 请确保已经安装go环境
export GOPROXY="https://goproxy.cn"
export GO111MODULE="on"
export CONF_PATH=./conf_file
go run main.go
```
