# Task31文档

### 目前完成了WeId接口的redis和ipfs方式的实现

## Redis

基于已写好的RedisDriver的通用方法，将要存储的值对象转化为json字符串:data,再调用对应的通用方法。

例如在addWeId时

```java
 WeIdDocumentValue value = new WeIdDocumentValue();
            value.setWeid(weId);
            value.setUpdated(now);
            value.setCreated(now);
            value.setDeactivated(0);
            value.setDocument_schema(documentSchema);
            String data = DataToolUtils.serialize(value);

            return add(domain,weId,data);
```

query数据时则获得data并解开json字符串

```java
   ResponseData<String> response = new RedisExecutor(redisDomain)
                    .executeQuery(redisDomain.getTableDomain(), dataKey, client);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                //通过util方法解开返回的json字符串
                WeIdDocumentValue tableData = DataDriverUtils.decodeValueForNeedObj(response.getResult(), WeIdDocumentValue.class);
                if (StringUtils.isNotBlank(tableData.getDocument_schema())) {
                    return new ResponseData<>(WeIdDocument.fromJson(tableData.getDocument_schema()), ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
            }
```

另有基于对应id的hash值实现的分页查询方法

## IPFS

选用ipfs需要在weidentity.properties提供对应的ipfs-api地址 

```properties
#ipfs config
# enter your IPFS node api
ipfs.api=/ip4/127.0.0.1/tcp/5001
```

由于键值对存储方式与redis相像 所以在executor方面很大程度上借鉴了redis

ipfs主要采用本地存储 存储于output文件夹中 通过domain来决定json文件的名字 会自动创建

```
存储路径 output/local
```

#### ipfsexecutor

效仿redis的实现 不同点是最后的存储处理

execute方法

```java
  //从本地取json数据
  Map<String, String> jsons = DataToolUtils.readFromLocal(path);
  ....
  //序列化数据存入本地
  String valueString = DataToolUtils.serialize(value);
  jsons.put(dataKey,DataDriverUtils.uploadIpfs(client, valueString.getBytes()));
  jsons.put(String.valueOf((jsons.size())/2),dataKey);
  DataToolUtils.writeInLocal(path,jsons);
 
```

query则通过domain来查找json文件以及对应key来查找对应数据

```java
Map<String, String> jsons = DataToolUtils.readFromLocal(path);
String cid = jsons.get(datakey);
```

