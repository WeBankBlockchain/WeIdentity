package com.webank.weid.suite.persistence.ipfs;

import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.util.DataToolUtils;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ipf执行人
 *
 * @author 刘家辉
 * @date 2023/08/24
 */
public class IpfsExecutor {
    private static final Logger logger = LoggerFactory.getLogger(
            IpfsExecutor.class);

    private IpfsDomain ipfsDomain;



    /**
     * 根据domain创建ipfs执行器.
     *
     * @param ipfsDomain the redisDomain
     */
    public IpfsExecutor(IpfsDomain ipfsDomain) {
        if (ipfsDomain != null) {
            this.ipfsDomain = ipfsDomain;
        } else {
            this.ipfsDomain = new IpfsDomain();
        }
    }

    /**
     * 执行查询
     *
     * @param path    路径
     * @param datakey datakey
     * @param client  客户端
     * @return {@link ResponseData}<{@link String}>
     */
    public ResponseData<String> executeQuery(String path, String datakey,
                                             IPFS client) {
        ResponseData<String> result = new ResponseData<String>();
        try {
            if (client == null) {
                return
                        new ResponseData<String>(null, ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR);
            }
            Map<String, String> jsons = DataToolUtils.readFromLocal(path);
            String cid = jsons.get(datakey);
            if(cid!=null){
                String str = downloadIpfs(client, jsons.get(datakey));
                result.setResult(str);
            }
            result.setErrorCode(ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("Query data from {{}} with exception", ipfsDomain.getTableDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
        return result;
    }

    /**
     * 执行查询行
     *
     * @param path   路径
     * @param datas  数据
     * @param client 客户端
     * @return {@link ResponseData}<{@link List}<{@link String}>>
     */
    public ResponseData<List<String>> executeQueryLines(String path, int[] datas, IPFS client) {
        ResponseData<List<String>> result = new ResponseData<List<String>>();
        ArrayList<String> list = new ArrayList<>();
        try {
            if (client == null) {
                return new ResponseData<>(null, ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR);

            }
            Map<String, String> jsons = DataToolUtils.readFromLocal(path);
            switch (datas[2]){
                case DataDriverConstant.IPFS_ONLY_ID_LINES:
                    for (Map.Entry<String, String> entry : jsons.entrySet()) {
                        if (entry.getKey().compareTo(String.valueOf(datas[0])) >= 0 && entry.getKey().compareTo(String.valueOf(datas[1])) < 0) {
                            // 遍历 范围之间的entry
                            list.add(entry.getValue());
                        }
                    }
                    break;
                case DataDriverConstant.IPFS_READ_CID_LINES:
                    for (Map.Entry<String, String> entry : jsons.entrySet()) {
                        if (entry.getKey().compareTo(String.valueOf(datas[0])) >= 0 && entry.getKey().compareTo(String.valueOf(datas[1])) < 0) {
                            list.add(downloadIpfs(client,jsons.get(entry.getValue())));
                        }
                    }
                    break;
                default: throw new Exception();
            }
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(list);
        } catch (Exception e) {
            logger.error("Query data from {{}} with exception", ipfsDomain.getTableDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
        return result;
    }

    /**
     * 执行查询数
     *
     * @param path   路径
     * @param client 客户端
     * @return {@link ResponseData}<{@link Integer}>
     */
    public ResponseData<Integer> executeQueryCount(String path, IPFS client) {
        ResponseData<Integer> result = new ResponseData<Integer>();
        try {
            if (client == null) {
                return
                        new ResponseData<Integer>(null, ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR);
            }
            Map<String, String> jsons = DataToolUtils.readFromLocal(path);
            int count = jsons.size() / 2;
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(count);
        } catch (Exception e) {
            logger.error("Query data from {{}} with exception", ipfsDomain.getTableDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
        return result;
    }


    /**
     * 执行插入更新操作
     *
     * @param client     客户端
     * @param path       路径
     * @param dataKey    数据关键
     * @param valueClass 值类
     * @param datas      数据
     * @return {@link ResponseData}<{@link Integer}>
     */
    public ResponseData<Integer> execute(IPFS client,String path,String dataKey,Class<?> valueClass,Object... datas) {
        ResponseData<Integer> result = new ResponseData<Integer>();
        try {
            if (client == null) {
                return
                        new ResponseData<Integer>(null, ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR);
            }
            Object obj = valueClass.newInstance();
            Field[] fields = valueClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                field.set(obj, datas[i]);
            }
            String valueString = DataToolUtils.serialize(obj);
            String cid = uploadIpfs(client, valueString.getBytes());
            Map<String, String> jsons = DataToolUtils.readFromLocal(path);
            jsons.put(dataKey,cid);
            jsons.put(String.valueOf((jsons.size()+1)/2),dataKey);
            if(DataToolUtils.writeInLocal(path,jsons)!=DataDriverConstant.IPFS_WRITE_SUCCESS){
                throw new IOException();
            }
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(DataDriverConstant.IPFS_EXECUTE_FAILED_STATUS);
        } catch (Exception e) {
            logger.error("Update data into {{}} with exception", ipfsDomain.getBaseDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
        return result;
    }


    /**
     * 上传至ipfs
     *
     * @param ipfs ipf
     * @param data 数据
     * @return {@link String}
     */
    public static String uploadIpfs(IPFS ipfs,byte[] data)  {
        NamedStreamable.ByteArrayWrapper file = new NamedStreamable.ByteArrayWrapper(data);
        MerkleNode addResult = null;
        try {
            addResult = ipfs.add(file).get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return addResult.hash.toString();
    }

    /**
     * 下载到ipfs
     *
     * @param ipfs ipf
     * @param hash 哈希
     * @return {@link String}
     * @throws IOException ioexception
     */
    public static String downloadIpfs(IPFS ipfs,String hash) throws IOException {
        byte[] data = null;
        try {
            data = ipfs.cat(Multihash.fromBase58(hash));
        } catch (IOException e) {
            logger.error("下载文件失败", e);
        }
        return DataToolUtils.bytesToJsonStr(data);
    }


}
