# 规范

---

## 基本要求：

* 所有文件必须使用 UTF-8 编码，且文件格式为 unix 文件格式，而不能使用 GBK 和 dos。
* 所有代码请统一使用4个空格作为缩进。
* 代码文件中，每行最多100个字符（xml文件每行字符可以超过100个）。
* 双斜杠的注释 ```//``` 只能在函数内部使用，其他注释请使用:

    ```java
    /**
    */
    ```

* 所有英文术语的大小写敏感，并注意拼写，请按照[术语表](https://github.com/webankopen/WeIdentity/blob/master/docs/terminologies.rst)的术语定义来使用。

* 关于空行
  * 函数内部，一般使用空行分割不同的逻辑，例如一个函数可能流程有大概 4 步，则 4 步中的每步之间，用一行空行分割。
  * 类里面的最后一条语句和最后一个大括号 ```}``` 之间不能有空行。
  * 【建议】类定义文件最后有一行空行，也就是在最后一个 ```}``` 后有一行空行。
  * 【建议】函数里面的代码的最后一行和 ```}``` 之间不能有空行，例如最后一个return和 ```}``` 之间不能有空行。
  * 【建议】类成员变量之间需要有空行分割。
  * 【建议】函数申明（定义）后 的第一个括号 ```{``` 跟第一行代码之间，建议有一行空行。

    ```java
    public ResponseData<CreateWeIDResult> createWeID(CreateWeIDArgs createWeIDArgs) {

        ResponseData<CreateWeIDResult> responseData = new ResponseData<CreateWeIDResult>();
        return null;
    }
    ```

---

## 文件名命名规范

* 全小写。
* 单词间使用破折号 `-` 进行分割，例如 ```verify-credential.md```。
* JAVA 代码文件可以不用按照上面的规则，而按 JAVA 原有规范。

---

## 变量命名规范

* **WeIdentity DID KEY**

SDK 测（例如JAVA SDK， C++ SDK）都使用 WeIdentity DID 作为 key。通过 SDK 解析为地址后，在合约中使用地址作为 key，取名 weAddress。类型使用 ```address```。

* **id, Id or ID?**

单词中出现的 “id”，请统一使用 Id （例如变量名 weId，类名就是 WeId*，例如 WeIdUtils 等），而不是用 id，ID 等。例外情况如下：

    * 如果是在单词最前面，则可以使用 id（因驼峰命名法）。
    * 如果是在常量名里面，则因为全部字母都是大写，所以使用 ID。

* **区分 acronym 和 abbreviation**

例如 HTTP 是 “Hyper Text Transport Protoco” 的 acronym，而 id 是 identity 的 abbreviation。
对 acronym，则在命名中所有字母都保留大写，而对 abbreviation 则在驼峰命名中可以使用 Id 或者 id（位于单词最前面）。

例如类名：HelperHTTPRequest，对应的实例化变量：helperHTTPRequest。
一般建议不要把 acronym 放在变量命名单词的最前面，否则这个变量的第一个字母就不是小写了。例如有个变量您可以取名为 niceHTTPRequest，而不是叫 HTTPRequest（看起来就不像是变量名了）。

* **统一的变量命名**

| Key | Type | Description |
| :---: | :---: |:------|
| weId |string| weId 变量名 |
| weAddress |合约中使用 address/java 中对应 string| weId 转换为 address 后的名字 |
| cptId | uint| CPT 类型，例如 cpt101，则值为 101 |
| created |int| 创建时间|
| updated |int| 更新时间 |
| logger |Logger| java 类里面的 logger 变量 |

* **常见技术名词**

| Key | Description |
| :---: | :------|
| SDK | 注意是全部大写，不是 Sdk，也不是 sdk |

---

## JAVA 代码规范

* JAVA 代码对调用者暴露的接口，请按照 javadoc 的模式注释。

* 函数的声明（函数定义）这一行如果超过了 100 个字符，请按每行一个参数的方式缩进（第一个参数从”函数名定义行“的下一行开始定义），且参数定义行相对于“函数名定义行”多缩进 4 个空格，例子如下：

    ```java
    //java函数定义的例子
    private static int resolveAttributeEvent(
        String weid,
        TransactionReceipt receipt,
        GetWeIDDocmentResult result) {
        // logic
    }
    ```
* 函数调用如果超过了 100 个字符，请按每行一个参数的方式缩进，例子如下：

    ```java
    TransactionReceipt transactionReceipt = cptController.registerCpt(
        publisher,
        intArray,
        bytes32Array,
        jsonSchema,
        rsvSignature.getV(),
        rsvSignature.getR(),
        rsvSignature.getS()
    ).get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
    ```

* import 顺序

分成几个section，每个section之间空行分割，section内部按字母序排列。

    ```java
    import java.*
    import javax.*

    import thirdparty-libraries

    //import 当前库的class 和 interface等，
    import com.webank.weid.*

    import static *
    ```

* 以上面列出的规范优先，其余遵守 [Google JAVA 代码规范](https://google.github.io/styleguide/javaguide.html)。

---

## Solidity 代码规范

* Solidity 因为在函数申明的结尾处可以有 modifier 等关键字，所以遵循 Solidity 本身的规范。

    注：soliditiy 因为 solidity 代码规范，所以函数的左括号是在跟 “function” 在同级，而不是放在“上一行”（这一点跟 JAVA 不一致）

```javascript
// solidity 的例子
function thisFunctionNameIsReallyLong(
    address x,
    address y,
    address z,
)
    public
    onlyowner
    priced
    returns (address)
{
    doSomething();
}
```

```javascript
//函数调用的例子
int ret = thisFunctionCallIsReallyLong(
    longArgument1,
    longArgument2,
    longArgument3
);
```

* 以上面列出的规范优先，其余遵守 [Solidity 代码规范](https://solidity.readthedocs.io/en/latest/style-guide.html)。

* 智能合约最佳实践： [smart-contract-best-practices](https://github.com/ConsenSys/smart-contract-best-practices)

* 智能合约静态扫描工具： [Linter-for-Solidity](https://github.com/duaraghav8/Solium)

---