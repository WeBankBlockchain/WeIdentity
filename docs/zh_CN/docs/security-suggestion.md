# 安全建议


## 1.网络安全：
* 与合作方传输数据需要使用加密协议，并验证身份有效性，如通讯使用HTTPS
* 接口数据除了HTTPS，接口内容尽量再次加密
* 防中间人劫持，APP写死服务器的证书（可定期下发更换），APP连接到服务器前，会检查是否证书对的上
* 网络区域隔离： 如生产与开发、测试、办公隔离
* 外网出口部署流量清洗、DDOS防护等安全措施（可以使用云服务商或者运营服务厂商提供的服务）

## 2.主机安全：
* 主机防护：提供外网服户的WEB系统请求应经WAF过滤，降低恶意请求访问的风险；ids入侵检测
* 主机防入侵检测，可使用云服务提供商的防入侵检测服务
* Windows机器部署杀毒软件
* 接入业务风控(接入反欺诈/常用设备监控)
* 不允许非标准操作系统及非标准软件，例如破解或者盗版软件
* 禁止使用弱密码，建立弱密码扫描检测机制
* 禁止开放高危端口和服务

## 3.数据安全：
* iOS防双击home键系统屏幕截图信息泄漏（加模糊或者新生成图片，防止iOS在APP切换界面能看到敏感信息）
* 密码键盘，自行实现无序的密码键盘(恶意软件有可能会通过监控屏幕点击，监控系统键盘)
* 密码输入需要 * 遮挡、后台一些重要数据脱敏后返回前台
* 禁止在前台（Web和APP）和后台server日志和配置文件中，明文记录用户名，密码或者密钥，也不打印和保存客户敏感信息，防止信息的泄露。
* SDK里面的密码等敏感信息用完即销毁（APP不缓存密码）
* 敏感数据要做好加密存储，例如对Credential数据：加密保存，秘钥如何存（KeyCenter保存）
* 数据出生产控制，比如使用Citrix
* 能查看所有用户信息的管理台，做好相应的鉴权。

## 4.应用安全：
* 登录态和其它业务独立，避免登录泄漏影响到其它业务
* 敏感接口调用，建议接入2FA双因子验证或者MFA，比如必须输入图形验证码，手机验证码，人脸识别等
* 密码恢复，需要2FA或者MFA
* 互联网APP类、Web类业务系统发布前应进行代码扫描和接口安全扫描及安全渗透性测试，相应测试结果及整改情况应提交系统上线检视会进行评估。 对外的接口需要做渗透测试。
例如常见的厂家：
绿盟 http://www.nsfocus.com.cn/  ；
漏洞盒子  https://www.vulbox.com/。
常见漏洞见：
https://www.owasp.org/index.php/Category:OWASP_Top_Ten_Project 或者 http://www.owasp.org.cn/owasp-project/2017-owasp-top-10
* SDK被嵌入执行的父应用判断，避免恶意应用嵌入
* SDK升级能力，保存在APP里面的证书需要定时更新
* Android接入腾讯乐固加固和腾讯金刚扫描（发现四大组件权限设置问题，如调用方权限；防止仿编译等。）
参考：http://wiki.open.qq.com/wiki/%E5%BA%94%E7%94%A8%E5%8A%A0%E5%9B%BA
* Android监测到root设备提示用户不安全问用户是否继续执行
* Android APP防止二次打包（对比签名）。
参考： https://blog.csdn.net/lostinai/article/details/46726559
* iOS反调试代码，监测是否越狱

## 5.其它
* 安全编码规范：OWASP 安全编码规范:
(English Version) https://www.owasp.org/index.php/File:OWASP_SCP_Quick_Reference_Guide_v2.pdf
(中文) https://www.owasp.org/index.php/File:OWASP_SCP_Quick_Reference_Guide_(Chinese).pdf
