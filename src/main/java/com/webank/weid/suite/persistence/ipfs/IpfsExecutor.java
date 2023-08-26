package com.webank.weid.suite.persistence.ipfs;

import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.protocol.request.TransactionArgs;
import com.webank.weid.suite.persistence.DefaultValue;
import com.webank.weid.util.DataDriverUtils;
import com.webank.weid.util.DataToolUtils;
import io.ipfs.api.IPFS;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.client.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
    private String path;



    /**
     * 根据domain创建ipfs执行器.
     *
     * @param ipfsDomain the redisDomain
     */
    public IpfsExecutor(IpfsDomain ipfsDomain) {
        if (ipfsDomain != null) {
            this.ipfsDomain = ipfsDomain;
            path=DataDriverConstant.IPFS_BASE_PATH+ipfsDomain.getTableDomain()+".json";
        } else {
            this.ipfsDomain = new IpfsDomain();
        }
    }

    public ResponseData<String> executeQuery(String datakey,
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
                String str = DataDriverUtils.downloadIpfs(client, jsons.get(datakey));
                result.setResult(str);
            }
            result.setErrorCode(ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("Query data from {{}} with exception", ipfsDomain.getTableDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
        return result;
    }

    public ResponseData<List<String>> executeQueryLines(int[] datas, IPFS client) {
        
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
                            list.add(DataDriverUtils.uploadIpfs(client,jsons.get(entry.getValue()).getBytes()));
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

    public ResponseData<Integer> executeQueryCount(IPFS client) {
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


    public ResponseData<Integer> execute(IPFS client,String dataKey,Object... datas) {
        ResponseData<Integer> result = new ResponseData<Integer>();
        try {
            if (client == null) {
                return
                        new ResponseData<Integer>(null, ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR);
            }
            TransactionArgs transactionArgs = new TransactionArgs();
            DefaultValue value = new DefaultValue();
            Map<String, String> jsons = DataToolUtils.readFromLocal(path);
            if (datas.length == 6) {
                transactionArgs.setRequestId((String) datas[0]);
                transactionArgs.setMethod((String) datas[1]);
                transactionArgs.setArgs((String) datas[2]);
                transactionArgs.setTimeStamp((Long) datas[3]);
                transactionArgs.setExtra((String)datas[4]);
                transactionArgs.setBatch((String)datas[5]);
                String valueString = DataToolUtils.serialize(transactionArgs);
                jsons.put(dataKey,DataDriverUtils.uploadIpfs(client,valueString.getBytes()));
                jsons.put(String.valueOf((jsons.size())/2),dataKey);
                DataToolUtils.writeInLocal(path,jsons);
            } else {
                value.setData((String) datas[0]);
                value.setId(dataKey);
                value.setExpire(ipfsDomain.getExpire());
                if (datas.length == 3) {
                    value.setCreated((Date)datas[1]);
                    value.setUpdated((Date)datas[2]);
                    //datas.lenth==2时为UpDate
                } else if (datas.length == 2) {
                    value.setUpdated((Date)datas[1]);
                }
                //解决重复写问题
                if (datas.length == 3 &&  jsons.get(dataKey) != null) {
                    return
                            new ResponseData<>(
                                    DataDriverConstant.REDISSON_EXECUTE_FAILED_STATUS,
                                    ErrorCode.PERSISTENCE_EXECUTE_FAILED
                            );
                }
                value.setExt1((jsons.size()+1)/2);
                String valueString = DataToolUtils.serialize(value);
                jsons.put(dataKey,DataDriverUtils.uploadIpfs(client, valueString.getBytes()));
                jsons.put(String.valueOf((jsons.size())/2),dataKey);
                DataToolUtils.writeInLocal(path,jsons);
            }
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(DataDriverConstant.REDISSON_EXECUTE_SUCESS_STATUS);

        } catch (Exception e) {
            logger.error("Update data into {{}} with exception", ipfsDomain.getBaseDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.REDISSON_EXECUTE_FAILED_STATUS);
        }
        return result;
    }


    public ResponseData<Integer> batchAdd(List<List<Object>> dataList, IPFS client) {
        
            ResponseData<Integer> result = new ResponseData<Integer>();
            List<DefaultValue> value = new ArrayList<>();
            try {
                if (client == null) {
                    return
                            new ResponseData<Integer>(null, ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR);
                }
                Map<String, String> jsons = DataToolUtils.readFromLocal(path);
                List<Object> values = dataList.get(dataList.size() - 1);
                int size = values.size();
                for (List<Object> list : dataList) {
                    if (CollectionUtils.isEmpty(list) || list.size() != size) {
                        return
                                new ResponseData<Integer>(
                                        DataDriverConstant.REDISSON_EXECUTE_FAILED_STATUS,
                                        ErrorCode.PERSISTENCE_BATCH_ADD_DATA_MISMATCH
                                );
                    }
                }

                for (int i = 0; i < size; i++) {
                    DefaultValue val = new DefaultValue();
                    val.setId((String) dataList.get(0).get(i));
                    val.setData((String)dataList.get(1).get(i));
                    val.setExpire((Date)dataList.get(2).get(i));
                    val.setCreated((Date)dataList.get(3).get(i));
                    val.setUpdated((Date)dataList.get(4).get(i));
                    value.add(val);
                }

                for (DefaultValue val : value) {
                    val.setExt1((jsons.size()+1)/2);
                    String valueString = DataToolUtils.serialize(val);
                    String cid = DataDriverUtils.uploadIpfs(client, valueString.getBytes());
                    jsons.put(val.getId(),cid);
                    jsons.put(String.valueOf((jsons.size())/2),val.getId());
                }
                DataToolUtils.writeInLocal(path,jsons);
                result.setResult(size);
            } catch (RedisException e) {
                logger.error("Batch add data to {{}} with exception", ipfsDomain.getBaseDomain(), e);
                result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
                result.setResult(DataDriverConstant.REDISSON_EXECUTE_FAILED_STATUS);
            } catch (IOException e) {
                logger.error("read data form local to {{}} with exception", ipfsDomain.getBaseDomain(), e);
                result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
                result.setResult(DataDriverConstant.REDISSON_EXECUTE_FAILED_STATUS);
            }
        return result;
        }

    public ResponseData<Integer> executeDelete(String dataKey, IPFS client) {
        ResponseData<Integer> result = new ResponseData<Integer>();
        try {
            if (client == null) {
                return
                        new ResponseData<Integer>(null, ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR);
            }
            Map<String, String> jsons = DataToolUtils.readFromLocal(path);
            if (jsons.get(dataKey)== null) {
                result.setErrorCode(ErrorCode.SUCCESS);
                result.setResult(DataDriverConstant.REDISSON_EXECUTE_FAILED_STATUS);
            } else {
                String cid = jsons.get(dataKey);
                String value = DataDriverUtils.downloadIpfs(client, cid);
                DefaultValue deserialize = DataToolUtils.deserialize(value, DefaultValue.class);
                jsons.put(dataKey,null);
                jsons.put(String.valueOf(deserialize.getExt1()),null);
                DataToolUtils.writeInLocal(path,jsons);
                result.setErrorCode(ErrorCode.SUCCESS);
                result.setResult(DataDriverConstant.REDISSON_EXECUTE_SUCESS_STATUS);
            }
        } catch (Exception e) {
            logger.error("Delete data into {{}} with exception", ipfsDomain.getBaseDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.REDISSON_EXECUTE_FAILED_STATUS);
        }
        return result;
    }
}

