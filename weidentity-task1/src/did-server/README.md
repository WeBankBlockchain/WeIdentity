# did-server did认证系统

did-server是对业务系统进行认证、授权的系统，其核心工作原理是通过用户输入的did和签名信息，从链上获取公钥信息进行验证。

该系统需要实现oatuh2.0授权码模式的主要功能，包括：获取授权code、使用code交换token、token校验等功能。


## 项目说明

### api接口：

#### 1. login登录

登录功能，服务端生成随机待签名信息（uuid），用户复制该uuid使用私钥签名，将did同签名后信息传到后端，然后根据用户did获取的公钥对签名后的信息进行验签，验签成功则代表登录成功；

![img.png](assets/img.png)

**请求方式**

`POST` `login`

**参数说明**

| 参数           |类型| 说明                                                                                                            |
|--------------|-|---------------------------------------------------------------------------------------------------------------|
| username     |string| 用户did                                                                                                         |
| password     |string| 用户使用私钥签名后的值                                                                                                   |

#### 2. 获取授权code

授权三方业务系统，成功后会将code返回；

**请求方式**

`GET` `/authorize`

**参数说明**

|参数|类型| 说明                                                                                                           |
|-|-|--------------------------------------------------------------------------------------------------------------|
|client_id|string| 在oauth2 server注册的client_id,详见配置文件                                                                            |
|response_type|string| 固定值:`code`                                                                                                   |
|scope|string| 权限范围,如:`str1,str2,str3`,str为配置文件的值 |
|state|string| 表示客户端的当前状态,可以指定任意值,认证服务器会原封不动地返回这个值                                                                          |
|redirect_uri|string| 回调uri,会在后面添加query参数`?code=xxx&state=xxx`,发放的code就在其中                                                         |

**请求示例**

```shell
# 业务系统请求
http://server_ip:server_port/authorize?client_id=clientId&response_type=code&scope=all&state=xyz&redirect_uri=http://client_ip:client_port/callback
# 302跳转,返回code
http://client_ip:client_port/callback?code=XUNKO4OPPROWAPFKEWNZWA&state=xyz
```

#### 3. 使用`code`交换`token`

根据请求者的信息，验证成功后，返回token等相关信息；

**请求方式**

`POST` `/token`

**请求头 Authorization**

- basic auth
- username: `client_id`
- password: `client_secret`

**Header**  
`Content-Type: application/x-www-form-urlencoded`

**Body参数说明**

|参数|类型| 说明                      |
|-|-|-------------------------|
|grant_type|string| 固定值`authorization_code` |
|code|string| 2 发放的code               |
|redirect_uri|string| 2 填写的redirect_uri       |

**Response返回示例**

```json
{
    "access_token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiIyMjIyMjIiLCJleHAiOjE1ODU3MTU1NTksInN1YiI6InRlc3QifQ.ZMgIDQMW7FGxbF1V8zWOmEkmB7aLH1suGYjhDdrT7aCYMEudWUoiCkWHSvBmJahGm0RDXa3IyDoGFxeMfzlDNQ",
    "expires_in": 7200,
    "refresh_token": "JG7_WGLWXUOW2KV2VLJKSG",
    "scope": "all",
    "token_type": "Bearer"
}
```

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
3. 密钥，基于 `crypto`、`go-ethereum/crypto` 等包进行封装。

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

