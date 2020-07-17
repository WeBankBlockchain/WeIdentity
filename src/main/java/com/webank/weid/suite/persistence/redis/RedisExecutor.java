package com.webank.weid.suite.persistence.redis;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.RedisDriverConstant;
import com.webank.weid.protocol.request.TransactionArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.persistence.mysql.SqlExecutor;
import com.webank.weid.util.DataToolUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.*;
import org.redisson.client.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author karenli
 * @program: weid-java-sdk
 * @description:
 * @date 2020-07-09 19:43:30
 */

public class RedisExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SqlExecutor.class);

    /**
     * the split for value.
     */
    private static final String VALUE_SPLIT_CHAR = ":";

    private RedisDomain redisDomain;

    RedissonConfig redissonConfig = new RedissonConfig();
    RedissonClient client = redissonConfig.redissonClusterClient();
    /**
     * 根据domain创建SQL执行器.
     *
     * @param redisDomain the redisDomain
     */
    public RedisExecutor(RedisDomain redisDomain) {

        if (redisDomain != null) {
            this.redisDomain = redisDomain;
        } else {
            this.redisDomain = new RedisDomain();
        }
    }

    /**
     * 查询操作.
     *
     * @param tableDomain key的部分映射
     * @param datakey 查询所需要的数据
     * @return 返回查询出来的单个数据
     */
    public ResponseData<String> executeQuery(String tableDomain, String datakey) {

        ResponseData<String> result = new ResponseData<String>();
        try {
            if (client == null) {
                return
                        new ResponseData<String>(null, ErrorCode.REDIS_GET_CONNECTION_ERROR);
            }
            RBucket<String> rBucket = client.getBucket(tableDomain +VALUE_SPLIT_CHAR+ datakey);
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(rBucket.get());
        } catch (Exception e) {
            logger.error("Query data from {{}} with exception", redisDomain.getTableDomain(), e);
            result.setErrorCode(ErrorCode.REDIS_EXECUTE_FAILED);
        } finally {
            client.shutdown();
        }
        return result;
    }

    /**
     * 增加的执行方法.
     *
     * @param dataKey Hash(id)
     * @param datas 所需要的数据
     * @return 返回执行成功或失败
     */
    public ResponseData<Integer> execute(String dataKey, Object... datas) {

        ResponseData<Integer> result = new ResponseData<Integer>();
        try {
            if (client == null) {
                return
                        new ResponseData<Integer>(null, ErrorCode.REDIS_GET_CONNECTION_ERROR);
            }
            TransactionArgs transactionArgs = new TransactionArgs();
            DefaultValue value = new DefaultValue();
            if (datas.length==6){
                transactionArgs.setRequestId((String) datas[0]);
                transactionArgs.setMethod((String) datas[1]);
                transactionArgs.setArgs((String) datas[2]);
                transactionArgs.setTimeStamp((Long) datas[3]);
                transactionArgs.setExtra((String)datas[4]);
                transactionArgs.setBatch((String)datas[5]);

                String valueString = DataToolUtils.serialize(transactionArgs);
                RBucket<String> rBucket=client.getBucket(redisDomain.getTableDomain()+VALUE_SPLIT_CHAR+dataKey);
                rBucket.set(valueString);
            }else{
                value.setData((String) datas[0]);
                value.setId(dataKey);
                value.setExpire(redisDomain.getExpire());
                if (datas.length==3){
                    value.setCreated((Date)datas[1]);
                    value.setUpdated((Date)datas[2]);
                    //datas.lenth==2时为UpDate
                }else if(datas.length==2){
                    value.setUpdated((Date)datas[1]);
                }
                String valueString = DataToolUtils.serialize(value);
                RBucket<String> rBucket=client.getBucket(redisDomain.getTableDomain()+VALUE_SPLIT_CHAR+dataKey);
                //解决重复写问题
                if (datas.length==3 && rBucket.get()!=null){
                    return
                            new ResponseData<Integer>(
                                    RedisDriverConstant.REDISSON_EXECUTE_FAILED_STATUS,
                                    ErrorCode.REDIS_EXECUTE_FAILED
                            );
                }
                rBucket.set(valueString);
            }
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(RedisDriverConstant.REDISSON_EXECUTE_SUCESS_STATUS);

        } catch (Exception e) {
            logger.error("Update data into {{}} with exception", redisDomain.getBaseDomain(), e);
            result.setErrorCode(ErrorCode.REDIS_EXECUTE_FAILED);
            result.setResult(RedisDriverConstant.REDISSON_EXECUTE_FAILED_STATUS);
        } finally {
            client.shutdown();
        }
        return result;
    }

    /**
     * 删除执行方法.
     *
     * @param dataKey Hash(id)
     * @return 返回执行成功或失败
     */
    public ResponseData<Integer> executeDelete(String dataKey) {

        ResponseData<Integer> result = new ResponseData<Integer>();
        try {
            if (client == null) {
                return
                        new ResponseData<Integer>(null, ErrorCode.REDIS_GET_CONNECTION_ERROR);
            }

            RBucket<String> rBucket=client.getBucket(redisDomain.getTableDomain()+VALUE_SPLIT_CHAR+dataKey);
            if (rBucket.get()==null){
                result.setErrorCode(ErrorCode.SUCCESS);
                result.setResult(RedisDriverConstant.REDISSON_EXECUTE_FAILED_STATUS);
            }else{
                rBucket.delete();
                result.setErrorCode(ErrorCode.SUCCESS);
                result.setResult(RedisDriverConstant.REDISSON_EXECUTE_SUCESS_STATUS);
            }
        } catch (Exception e) {
            logger.error("Delete data into {{}} with exception", redisDomain.getBaseDomain(), e);
            result.setErrorCode(ErrorCode.REDIS_EXECUTE_FAILED);
            result.setResult(RedisDriverConstant.REDISSON_EXECUTE_FAILED_STATUS);
        } finally {
            client.shutdown();
        }
        return result;
    }

    /**
     * 批量新增的语句.
     *
     * @param dataList 占位符所需要的数据
     * @return 返回受影响的行数
     */
    public ResponseData<Integer> batchSave(List<List<Object>> dataList) {

        ResponseData<Integer> result = new ResponseData<Integer>();
        List<DefaultValue> value = new ArrayList<>();
        BatchOptions batchOptions = BatchOptions.defaults()
                .executionMode(BatchOptions.ExecutionMode.IN_MEMORY)
                .skipResult()
                .syncSlaves(2, 1, TimeUnit.SECONDS)
                .responseTimeout(2, TimeUnit.SECONDS)
                .retryInterval(2, TimeUnit.SECONDS)
                .retryAttempts(4);
        RBatch rBatch = client.createBatch(batchOptions);

        try {
            if (client == null) {
                return
                        new ResponseData<Integer>(null, ErrorCode.REDIS_GET_CONNECTION_ERROR);
            }
            List<Object> values = dataList.get(dataList.size() - 1);
            int size = values.size();
            for (List<Object> list : dataList) {
                if (CollectionUtils.isEmpty(list) || list.size() != size) {
                    return
                            new ResponseData<Integer>(
                                    RedisDriverConstant.REDISSON_EXECUTE_FAILED_STATUS,
                                    ErrorCode.PRESISTENCE_BATCH_SAVE_DATA_MISMATCH
                            );
                }
            }

            for(int i = 0; i < size; i++){
                DefaultValue val = new DefaultValue();
                val.setId((String) dataList.get(0).get(i));
                val.setData((String)dataList.get(1).get(i));
                val.setExpire((Date)dataList.get(2).get(i));
                val.setCreated((Date)dataList.get(3).get(i));
                val.setUpdated((Date)dataList.get(4).get(i));
                value.add(val);
            }

            for (DefaultValue val : value){
                rBatch.getBucket(redisDomain.getTableDomain()+VALUE_SPLIT_CHAR+val.getId()).setAsync(DataToolUtils.serialize(val));
            }
            BatchResult<?> batchResult = rBatch.execute();

            result.setResult(size);
        } catch (RedisException e) {
            logger.error("Batch add data to {{}} with exception", redisDomain.getBaseDomain(), e);
            result.setErrorCode(ErrorCode.REDIS_EXECUTE_FAILED);
            result.setResult(RedisDriverConstant.REDISSON_EXECUTE_FAILED_STATUS);
        } finally {
            client.shutdown();
        }
        return result;
    }
}
