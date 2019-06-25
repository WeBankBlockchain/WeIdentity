# WeIdentity 存储容量预估

WeIdentity 在区块链上存储的信息主要包括：
1. WeIdentity Document（存储在 Event）。
2. WeIdentity DID 的总表。
3. WeIdentity Credential 在链上的存证（即Evidence）。
4. 所有 CPT 的定义（以JSON格式存储）。
5. Authority Issuer。
6. Specific Issuer。

一般情况下，需要注意上面的第1，2，3项的容量。

WeIdentity 容量预估：

```JAVA
链上所占总空间 = WeIdentity DID 数量 * 52 Bytes * 10 +
  Evidence 数量 * 220 Bytes * 10 +
  CPT 数量 * 4560 Bytes * 10 +
  Authority Issuer 数量 * 724 Bytes * 10 +
  Specific Issuer 数量 * 372 Bytes * 10
```

```JAVA
Event 所占总空间 = WeIdentity DID 数量 * 1464 Bytes * 3
```

例如，假设一条联盟链，总共创建`1000万个`WeID，并且将Credential的存证上链，每天 `20000 条`存证上链（这种情况下，容量评估可以只考虑上面列出的前三项）。则`两年`时间，部署 WeIdentity 的区块链节点，大概会占用的空间（仅做参考）：
* 链上所占总空间 = 10000000 * 52 Bytes * 10 + 2 * 365 * 20000 * 220 Bytes * 10 = 35.6 GBytes
* Event 所占总空间 = 10000000 * 1464 Bytes * 3 =  41.9 GBytes

综上，单节点占用空间 35.6 + 41.9 = 77.5 GBytes

## 1. WeIdentity Document，即每个 WeIdentity DID 的属性集合，存储在 Event。

```JSON
所有的 WeIdentity Document 所占 Event 空间 = WeIdentity DID 数量 * 1464 Bytes * 3
```

#### 解释：

Event 的结构如下：
```javascript
event WeIdAttributeChanged(
    address indexed identity ---> 20 Bytes
    bytes32 key ---> 32 Bytes
    bytes value ---> 不同的 Attribute，value所占空间不通。下面详细解释。
    uint previousBlock ---> 8 Bytes
    int updated ---> 8 Bytes
);
```

这里假设，每个 WeIdentity Document 包含了下面的字段：
* 1 个 Created 字段，保存在 1 个 Event 中。long型的 timestamp，转为 String，value 占 13 Bytes。单个 Event 结构占 81 Bytes。
* 3 个 Public Key 字段，保存在 3 个 Event 中。保存形式是`"PublicKey/address"`，value 占64+1+20 = 85 Bytes。单个 Event 结构占 153 Bytes。(即没增加一个 Public Key，增加 153 Bytes)。
* 3 个 Authentication 字段，保存在 3 个 Event 中。保存形式是`"PublicKey/address"`，value 占 64+1+20 = 85 Bytes。单个 Event 结构占 153 Bytes(即没增加一个 Authentication，增加 153 Bytes)。
* 5 个 Service Endpoint 字段，保存在 5 个 Event 中。这里预估每个 value 占 30 Bytes。单个 Event 结构占 98 Bytes。

即总共需要使用 12 个 Event 结构存储 WeIdentity Document 的所有属性。因此，单个 WeIdentity Document 所占 Event 存储空间为 81 + 3 * 153 + 3 * 153 + 5 * 93 = 1464 Bytes。


## 2. WeIdentity DID 的总表所占容量预估

WeIdentity DID 的总表，存储在链上，存储了所有公开的 WeIdentity DID 的 ID到 Document 的映射关系。

```javascript
WeIdentity DID 的总表在链上所占空间 = WeIdentity DID 数量 * 52 Bytes * 10
```

#### 解释：
key 是 WeAddress，使用`address`存储，占 20 Bytes； WeAddress；Event 所在块位置使用`unit`存储，占 32 Bytes（256 bits）。单个 WeID 占用52 Bytes。


## 3. WeIdentity Credential 的 Evidence 所占容量预估

```
所有 Evidence 在链上所占空间 = Evidence 数量 * 220 Bytes * 10
```

#### 解释：
key 是 WeAddress，占 20 Bytes；单个 Evidence 在链上的数据结构如下，占200 Bytes：

```javascript
{
bytes32[] dataHash, ---> 动态数组，目前使用 2 个 byte32，占 64 Bytes。
address[] signer, ---> 动态数组，目前使用 1 个 byte32， 占 32 Bytes。
bytes32 r, ---> 占 32 Bytes。
bytes32 s, ---> 占 32 Bytes。
uint8 v, ---> 占 8 Bytes。
bytes32[] extra ---> 动态数组，目前使用 32 Bytes。
}
```

## 4. 所有 CPT 的定义所占容量预估

```javascript
CPT 在链上所占空间 = CPT 数量 * 4560 Bytes * 10
```

#### 解释：
key 是`uint`，占 32 Bytes；单个 CPT 在链上的存储结构如下，占用空间 4528 Bytes，如下所示：

```javascript
{
uint cptId ---> 8 Bytes
address cptPublisher ---> 32 Bytes
int[8] cptIntArray ---> 64 Bytes
bytes32[8] cptBytes32Array ---> 256 Bytes
bytes32[128] cptJsonSchemaArray ---> 4096 Bytes
uint8 cptV ---> 8 Bytes
bytes32 cptR ---> 32 Bytes
bytes32 cptS ---> 32 Bytes
}
```

## 5. Authority Issuer 所占容量预估

```javascript
Authority Issuer 合约在链上所占空间 = Authority Issuer 数量 * 724 Bytes * 10
```

#### 解释：
key 是 WeAddress，占 20 Bytes；单个Authority Issuer 在链上的存储结构，704 byte

```javascript
{
address addr, ---> 32 Bytes
bytes32[16] attribBytes32, ---> 512 Bytes
int[16] attribInt, ---> 128 Bytes
bytes accValue ---> 32 Bytes
}
```

## 6. Specific Issuer 所占容量预估

```javascript
Specific Issuer 合约在链上所占空间 = Specific Issuer 数量 * 372 Bytes * 10
```

#### 解释：
key 是 WeAddress，占 20 Bytes；单个 Specific Issuer 在链上存储结构，占 352 Bytes,结构如下：

```javascript
{
bytes32 typeName; ---> 32 Bytes
address[] fellow; ---> 32 Bytes
mapping (address => bool) isFellow; ---> 32 Bytes
bytes32[8] extra; ---> 256 Bytes
}
```
