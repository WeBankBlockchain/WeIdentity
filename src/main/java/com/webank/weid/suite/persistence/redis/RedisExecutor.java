

package com.webank.weid.suite.persistence.redis;

import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.protocol.request.TransactionArgs;
import com.webank.weid.suite.persistence.DefaultValue;
import com.webank.weid.util.DataToolUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.*;
import org.redisson.client.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * redis操作辅助类.
 *
 * @author karenli
 */
public class RedisExecutor {

    private static final Logger logger = LoggerFactory.getLogger(RedisExecutor.class);

    /**
     * the split for value.
     */
    private static final String VALUE_SPLIT_CHAR = ":";

    private RedisDomain redisDomain;



    /**
     * 根据domain创建Redis执行器.
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
     * @param client redisson连接入口
     * @return 返回查询出来的单个数据
     */
    public ResponseData<String> executeQuery(String tableDomain, String datakey,
                                             RedissonClient client) {

        ResponseData<String> result = new ResponseData<String>();
        try {
            if (client == null) {
                return
                        new ResponseData<String>(null, ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR);
            }
            RBucket<String>  rbucket = client.getBucket(tableDomain + VALUE_SPLIT_CHAR + datakey);
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(rbucket.get());
        } catch (Exception e) {
            logger.error("Query data from {{}} with exception", redisDomain.getTableDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
        return result;
    }

    /**
     * 执行分页查询
     *
     * @param tableDomain 表域
     * @param dataDomain  数据域
     * @param datas       数据
     * @param client      客户端
     * @return {@link ResponseData}<{@link List}<{@link String}>>
     */
    public ResponseData<List<String>> executeQueryLines(String tableDomain,String dataDomain, int[] datas, RedissonClient client) {
        ResponseData<List<String>> result = new ResponseData<List<String>>();
        ArrayList<String> list = new ArrayList<>();
        try {
            if (client == null) {
                return new ResponseData<>(null, ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR);

            }
            RScoredSortedSet<Object> scoredSortedSet = client.getScoredSortedSet(dataDomain);
            Collection<Object> objects = scoredSortedSet.valueRange(datas[0], datas[1]);
            for (Object object : objects) {
                RBucket<String> rbucket = client.getBucket(tableDomain + VALUE_SPLIT_CHAR + object);
                list.add(rbucket.get());
            }
          result.setErrorCode(ErrorCode.SUCCESS);
          result.setResult(list);
        } catch (Exception e) {
            logger.error("Query data from {{}} with exception", redisDomain.getTableDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
        return result;
    }

    /**
     * 执行分页查询id
     *
     * @param dataDomain 数据域
     * @param datas      数据
     * @param client     客户端
     * @return {@link ResponseData}<{@link List}<{@link Integer}>>
     */
    public ResponseData<List<Integer>> executeQueryIdLines(String dataDomain, int[] datas, RedissonClient client) {
        ResponseData<List<Integer>> result = new ResponseData<List<Integer>>();
        ArrayList<Integer> list = new ArrayList<>();
        try {
            if (client == null) {
                return new ResponseData<>(null, ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR);

            }
            RScoredSortedSet<Object> scoredSortedSet = client.getScoredSortedSet(dataDomain);
            Collection<Object> objects = scoredSortedSet.valueRange(datas[0], datas[1]);
            for (Object object : objects) {
                list.add((Integer) object);
            }
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(list);
        } catch (Exception e) {
            logger.error("Query data from {{}} with exception", redisDomain.getTableDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
        return result;
    }

    /**
     * 执行查询总数
     *
     * @param tableDomain 表域
     * @param client      客户端
     * @return {@link ResponseData}<{@link Integer}>
     */
    public ResponseData<Integer> executeQueryCount(String tableDomain, RedissonClient client) {
       client.getScoredSortedSet(tableDomain);
        ResponseData<Integer> result = new ResponseData<Integer>();
        try {
            if (client == null) {
                return new ResponseData<>(null, ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR);
            }
            RScoredSortedSet<Object> scoredSortedSet = client.getScoredSortedSet(tableDomain);
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(scoredSortedSet.size());
        } catch (Exception e) {
            logger.error("Query data from {{}} with exception", redisDomain.getTableDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        }
        return result;
    }


    /**
     * 增加的执行方法.
     *
     * @param client redisson连接入口
     * @param dataKey Hash(id)
     * @param datas 所需要的数据
     * @return 返回执行成功或失败
     */
    public ResponseData<Integer> execute(RedissonClient client, String dataKey, Object... datas) {

        ResponseData<Integer> result = new ResponseData<Integer>();
        try {
            if (client == null) {
                return
                        new ResponseData<Integer>(null, ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR);
            }
            TransactionArgs transactionArgs = new TransactionArgs();
            DefaultValue value = new DefaultValue();
            if (datas.length == 6) {
                transactionArgs.setRequestId((String) datas[0]);
                transactionArgs.setMethod((String) datas[1]);
                transactionArgs.setArgs((String) datas[2]);
                transactionArgs.setTimeStamp((Long) datas[3]);
                transactionArgs.setExtra((String)datas[4]);
                transactionArgs.setBatch((String)datas[5]);
                String valueString = DataToolUtils.serialize(transactionArgs);
                RBucket<String> rbucket = client.getBucket(
                        redisDomain.getTableDomain() + VALUE_SPLIT_CHAR + dataKey);
                addIndexForDataKey(client,dataKey,redisDomain.getTableDomain());
                rbucket.set(valueString);
            } else {
                value.setData((String) datas[0]);
                value.setId(dataKey);
                value.setExpire(redisDomain.getExpire());
                if (datas.length == 3) {
                    value.setCreated((Date)datas[1]);
                    value.setUpdated((Date)datas[2]);
                    //datas.lenth==2时为UpDate
                } else if (datas.length == 2) {
                    value.setUpdated((Date)datas[1]);
                }
                String valueString = DataToolUtils.serialize(value);
                RBucket<String> rbucket = client.getBucket(
                        redisDomain.getTableDomain() + VALUE_SPLIT_CHAR + dataKey);
                //解决重复写问题
                if (datas.length == 3 &&  rbucket.get() != null) {
                    return
                            new ResponseData<Integer>(
                                    DataDriverConstant.REDISSON_EXECUTE_FAILED_STATUS,
                                    ErrorCode.PERSISTENCE_EXECUTE_FAILED
                            );
                }
                addIndexForDataKey(client,dataKey,redisDomain.getTableDomain());
                rbucket.set(valueString);
            }
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(DataDriverConstant.REDISSON_EXECUTE_SUCESS_STATUS);

        } catch (Exception e) {
            logger.error("Update data into {{}} with exception", redisDomain.getBaseDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.REDISSON_EXECUTE_FAILED_STATUS);
        }
        return result;
    }

    /**
     * 删除执行方法.
     *
     * @param dataKey Hash(id)
     * @param client redisson连接入口
     * @return 返回执行成功或失败
     */
    public ResponseData<Integer> executeDelete(String dataKey, RedissonClient client) {

        ResponseData<Integer> result = new ResponseData<Integer>();
        try {
            if (client == null) {
                return
                        new ResponseData<Integer>(null, ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR);
            }
            RBucket<String> rbucket = client.getBucket(
                    redisDomain.getTableDomain() + VALUE_SPLIT_CHAR + dataKey);
            if (rbucket.get() == null) {
                result.setErrorCode(ErrorCode.SUCCESS);
                result.setResult(DataDriverConstant.REDISSON_EXECUTE_FAILED_STATUS);
            } else {
                rbucket.delete();
                result.setErrorCode(ErrorCode.SUCCESS);
                result.setResult(DataDriverConstant.REDISSON_EXECUTE_SUCESS_STATUS);
            }
        } catch (Exception e) {
            logger.error("Delete data into {{}} with exception", redisDomain.getBaseDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.REDISSON_EXECUTE_FAILED_STATUS);
        }
        return result;
    }

    /**
     * 批量新增的语句.
     *
     * @param dataList 占位符所需要的数据
     * @param client redisson连接入口
     * @return 返回受影响的行数
     */
    public ResponseData<Integer> batchAdd(List<List<Object>> dataList, RedissonClient client) {

        ResponseData<Integer> result = new ResponseData<Integer>();
        List<DefaultValue> value = new ArrayList<>();
        RBatch rbatch = client.createBatch();
        try {
            if (client == null) {
                return
                        new ResponseData<Integer>(null, ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR);
            }
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
                rbatch.getBucket(redisDomain.getTableDomain() + VALUE_SPLIT_CHAR + val.getId())
                        .setAsync(DataToolUtils.serialize(val));
                addIndexForDataKey(client,val.getId(),redisDomain.getTableDomain());
            }
            BatchResult<?> batchResult = rbatch.execute();

            result.setResult(size);
        } catch (RedisException e) {
            logger.error("Batch add data to {{}} with exception", redisDomain.getBaseDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.REDISSON_EXECUTE_FAILED_STATUS);
        }
        return result;
    }

    /**
     * 为key添加索引方便分页查询
     *
     * @param dataKey 数据关键
     * @param domain  域
     */
    public void addIndexForDataKey(RedissonClient client, String dataKey, String domain){
        RScoredSortedSet<Object> set = client.getScoredSortedSet(domain);
        set.add(set.size(),dataKey);
    }



}
