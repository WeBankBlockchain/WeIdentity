package com.webank.weid.suite.persistence.redis.driver;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.RedisDriverConstant;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.request.TransactionArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.persistence.inf.Persistence;
import com.webank.weid.suite.persistence.redis.DefaultValue;
import com.webank.weid.suite.persistence.redis.RedisDomain;
import com.webank.weid.suite.persistence.redis.RedisExecutor;
import com.webank.weid.util.DataToolUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author karenli
 * @program: weid-java-sdk
 * @description:
 * @date 2020-07-09 15:19:48
 */

public class RedisDriver implements Persistence {

    private static final Logger logger = LoggerFactory.getLogger(RedisDriver.class);

    private static final Integer FAILED_STATUS = RedisDriverConstant.REDISSON_EXECUTE_FAILED_STATUS;

    private static final ErrorCode KEY_INVALID = ErrorCode.PRESISTENCE_DATA_KEY_INVALID;


    @Override
    public ResponseData<Integer> add(String domain, String id, String data) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[redis->add] the id of the data is empty.");
            return new ResponseData<>(FAILED_STATUS,KEY_INVALID);
        }
        String dataKey = DataToolUtils.getHash(id);

        try {
            RedisDomain redisDomain = new RedisDomain(domain);
            Date date = new Date();
            Object[] datas = {data,date,date};
            return new RedisExecutor(redisDomain).execute(dataKey, datas);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->add] add the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> batchAdd(String domain, List<String> ids, List<String> dataList) {

        try {
            List<Object> idHashList = new ArrayList<>();
            for (String id : ids) {
                if (StringUtils.isEmpty(id)) {
                    logger.error("[redis->batchAdd] the id of the data is empty.");
                    return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
                }
                idHashList.add(DataToolUtils.getHash(id));
            }
            RedisDomain redisDomain = new RedisDomain(domain);
            List<List<Object>> dataLists = new ArrayList<List<Object>>();
            dataLists.add(idHashList);
            dataLists.add(Arrays.asList(dataList.toArray()));
            //处理失效时间
            dataLists.add(fixedListWithDefault(ids.size(), redisDomain.getExpire()));
            //处理创建时间和更新时间
            List<Object> nowList = fixedListWithDefault(ids.size(), redisDomain.getNow());
            dataLists.add(nowList);
            dataLists.add(nowList);
            return new RedisExecutor(redisDomain).batchSave(dataLists);
        } catch (WeIdBaseException e) {
            logger.error("[redis->batchAdd] batchAdd the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    private List<Object> fixedListWithDefault(int size, Object obj) {

        Object[] dates = new Object[size];
        Arrays.fill(dates, obj);
        List<Object> list = new ArrayList<>();
        list.addAll(Arrays.asList(dates));
        return list;
    }

    @Override
    public ResponseData<String> get(String domain, String id) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[redis->get] the id of the data is empty.");
            return new ResponseData<String>(StringUtils.EMPTY, KEY_INVALID);
        }
        //dataKey:id的hash值
        String dataKey = DataToolUtils.getHash(id);
        try {
            ResponseData<String> result = new ResponseData<String>();
            //设置result初始值为空字符串
            result.setResult(StringUtils.EMPTY);
            RedisDomain redisDomain = new RedisDomain(domain);
            ResponseData<String> response = new RedisExecutor(redisDomain)
                    .executeQuery(redisDomain.getTableDomain(), dataKey);

            if (response.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                DefaultValue Data = DataToolUtils.deserialize(response.getResult(),DefaultValue.class);

                //超过超时时间，log输出data超时
                if (Data != null && Data.getExpire() != null && Data.getExpire().before(new Date())) {
                    logger.error("[redis->get] the data is expire.");
                    //输出empty以及超过超时时间错误代码
                    return new ResponseData<String>(StringUtils.EMPTY, ErrorCode.REDIS_DATA_EXPIRE);
                }
                if (Data != null && StringUtils.isNotBlank(Data.getData())) {
                    result.setResult(
                            new String(
                                    Data.getData().getBytes(StandardCharsets.ISO_8859_1),
                                    StandardCharsets.UTF_8
                            )
                    );
                }
            }
            result.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
            System.out.println("result="+result.getResult());
            System.out.println(result.getResult()==null);
            return result;
        }catch (WeIdBaseException e) {
            logger.error("[redis->get] get the data error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> delete(String domain, String id) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[redis->delete] the id of the data is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        String dataKey = DataToolUtils.getHash(id);
        try {
            RedisDomain redisDomain = new RedisDomain(domain);
            return new RedisExecutor(redisDomain).executeDelete(dataKey);
        } catch (WeIdBaseException e) {
            logger.error("[redis->delete] delete the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> update(String domain, String id, String data) {

        if (StringUtils.isEmpty(id) || StringUtils.isBlank(this.get(domain,id).getResult())) {
            logger.error("[redis->update] the id of the data is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        String dataKey = DataToolUtils.getHash(id);
        Date date = new Date();
        try {
            RedisDomain redisDomain = new RedisDomain(domain);
            Object[] datas = {data, date};
            return new RedisExecutor(redisDomain).execute(dataKey,datas);
        } catch (WeIdBaseException e) {
            logger.error("[redis->update] update the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> saveOrUpdate(String domain, String id, String data) {

        ResponseData<String> getRes = this.get(domain, id);
        //如果查询数据存在，或者失效 则进行更新 否则进行新增
        if ((StringUtils.isNotBlank(getRes.getResult())
                && getRes.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode())
                || getRes.getErrorCode().intValue() == ErrorCode.SQL_DATA_EXPIRE.getCode()) {
            return this.update(domain, id, data);
        }
        return this.add(domain, id, data);
    }

    @Override
    public ResponseData<Integer> saveTransaction(TransactionArgs transactionArgs) {

        if (StringUtils.isEmpty(transactionArgs.getRequestId())) {
            logger.error("[redis->add] the id of the data is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            RedisDomain redisDomain = new RedisDomain(RedisDriverConstant.DOMAIN_DEFAULT_INFO);
            String datakey = transactionArgs.getRequestId();
            Object[] datas = {
                    transactionArgs.getRequestId(),
                    transactionArgs.getMethod(),
                    transactionArgs.getArgs(),
                    transactionArgs.getTimeStamp(),
                    transactionArgs.getExtra(),
                    transactionArgs.getBatch()
            };
            return new RedisExecutor(redisDomain).execute(datakey, datas);
        } catch (WeIdBaseException e) {
            logger.error("[redis->add] add the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }
}
