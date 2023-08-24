

package com.webank.weid.suite.persistence.redis;

import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.blockchain.protocol.base.CptBaseInfo;
import com.webank.weid.blockchain.protocol.base.WeIdDocument;
import com.webank.weid.blockchain.protocol.base.WeIdDocumentMetadata;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.request.TransactionArgs;
import com.webank.weid.suite.persistence.*;
import com.webank.weid.util.DataToolUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * redis Driver.
 *
 * @author karenli
 */
public class RedisDriver implements Persistence {

    private static final Logger logger = LoggerFactory.getLogger(RedisDriver.class);

    private static final Integer FAILED_STATUS = DataDriverConstant.REDISSON_EXECUTE_FAILED_STATUS;

    private static final ErrorCode KEY_INVALID = ErrorCode.PRESISTENCE_DATA_KEY_INVALID;

    private static int CPT_DEFAULT_VERSION = 1;

    RedissonConfig redissonConfig = new RedissonConfig();

    RedissonClient client = redissonConfig.redismodelRecognition();

    @Override
    public ResponseData<Integer> add(String domain, String id, String data) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[redis->add] the id of the data is empty.");
            return new ResponseData<>(FAILED_STATUS, KEY_INVALID);
        }
        String dataKey = DataToolUtils.hash(id);
        try {
            RedisDomain redisDomain = new RedisDomain(domain);
            Date date = new Date();
            Object[] datas = {data, date, date};
            return new RedisExecutor(redisDomain).execute(client, dataKey, datas);
        } catch (WeIdBaseException e) {
            logger.error("[redis->add] add the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> batchAdd(String domain, Map<String, String> keyValueList) {

        try {
            List<Object> idHashList = new ArrayList<>();
            List<Object> dataList = new ArrayList<>();
            Iterator<String> iterator = keyValueList.keySet().iterator();
            while (iterator.hasNext()) {
                String id = iterator.next();
                String data = keyValueList.get(id);
                if (StringUtils.isEmpty(id)) {
                    logger.error("[redis->batchAdd] the id of the data is empty.");
                    return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
                }
                idHashList.add(DataToolUtils.hash(id));
                dataList.add(data);
            }
            RedisDomain redisDomain = new RedisDomain(domain);
            List<List<Object>> dataLists = new ArrayList<List<Object>>();
            dataLists.add(idHashList);
            dataLists.add(Arrays.asList(dataList.toArray()));
            //处理失效时间
            dataLists.add(fixedListWithDefault(idHashList.size(), redisDomain.getExpire()));
            //处理创建时间和更新时间
            List<Object> nowList = fixedListWithDefault(idHashList.size(), redisDomain.getNow());
            dataLists.add(nowList);
            dataLists.add(nowList);
            return new RedisExecutor(redisDomain).batchAdd(dataLists, client);
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
        String dataKey = DataToolUtils.hash(id);
        try {
            ResponseData<String> result = new ResponseData<String>();
            //设置result初始值为空字符串
            result.setResult(StringUtils.EMPTY);
            RedisDomain redisDomain = new RedisDomain(domain);
            ResponseData<String> response = new RedisExecutor(redisDomain)
                    .executeQuery(redisDomain.getTableDomain(), dataKey, client);

            if (response.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                DefaultValue data = DataToolUtils.deserialize(
                        response.getResult(), DefaultValue.class);
                //超过超时时间，log输出data超时
                if (data != null && data.getExpire() != null
                        && data.getExpire().before(new Date())) {
                    logger.error("[redis->get] the data is expire.");
                    //输出empty以及超过超时时间错误代码
                    return new ResponseData<String>(StringUtils.EMPTY,
                            ErrorCode.PERSISTENCE_DATA_EXPIRE);
                }
                if (data != null && StringUtils.isNotBlank(data.getData())) {
                    result.setResult(
                            new String(
                                    data.getData().getBytes(
                                            DataDriverConstant.STANDARDCHARSETS_ISO),
                                    DataDriverConstant.STANDARDCHARSETS_UTF_8
                            )
                    );
                }
            }
            result.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
            System.out.println("result=" + result.getResult());
            System.out.println(result.getResult() == null);
            return result;
        } catch (WeIdBaseException e) {
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
        String dataKey = DataToolUtils.hash(id);
        try {
            RedisDomain redisDomain = new RedisDomain(domain);
            return new RedisExecutor(redisDomain).executeDelete(dataKey, client);
        } catch (WeIdBaseException e) {
            logger.error("[redis->delete] delete the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> update(String domain, String id, String data) {

        if (StringUtils.isEmpty(id) || StringUtils.isBlank(this.get(domain, id).getResult())) {
            logger.error("[redis->update] the id of the data is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        String dataKey = DataToolUtils.hash(id);
        Date date = new Date();
        try {
            RedisDomain redisDomain = new RedisDomain(domain);
            Object[] datas = {data, date};
            return new RedisExecutor(redisDomain).execute(client, dataKey, datas);
        } catch (WeIdBaseException e) {
            logger.error("[redis->update] update the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> addOrUpdate(String domain, String id, String data) {

        ResponseData<String> getRes = this.get(domain, id);
        //如果查询数据存在，或者失效 则进行更新 否则进行新增
        if ((StringUtils.isNotBlank(getRes.getResult())
                && getRes.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode())
                ||
                getRes.getErrorCode().intValue() == ErrorCode.PERSISTENCE_DATA_EXPIRE.getCode()) {
            return this.update(domain, id, data);
        }
        return this.add(domain, id, data);
    }

    @Override
    public ResponseData<Integer> addTransaction(TransactionArgs transactionArgs) {

        if (StringUtils.isEmpty(transactionArgs.getRequestId())) {
            logger.error("[redis->add] the id of the data is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            RedisDomain redisDomain = new RedisDomain(
                    DataDriverConstant.DOMAIN_OFFLINE_TRANSACTION_INFO);
            String datakey = transactionArgs.getRequestId();
            Object[] datas = {
                    transactionArgs.getRequestId(),
                    transactionArgs.getMethod(),
                    transactionArgs.getArgs(),
                    transactionArgs.getTimeStamp(),
                    transactionArgs.getExtra(),
                    transactionArgs.getBatch()
            };
            return new RedisExecutor(redisDomain).execute(client, datakey, datas);
        } catch (WeIdBaseException e) {
            logger.error("[redis->add] add the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    /*
    以下方法暂不需要，本地部署不需要使用redis方式，默认使用Mysql
     */

    @Override
    public ResponseData<Integer> addWeId(String domain, String weId, String documentSchema) {
        if (StringUtils.isEmpty(weId)) {
            logger.error("[redis->addWeId] the weId is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            RedisDomain redisDomain = new RedisDomain(domain);
            Date now = redisDomain.getNow();
            Object[] datas = {weId, now, now, 1, 0, documentSchema};
            addIndexForMsg(weId,redisDomain.getTableDomain()+DataDriverConstant.REDIS_INDEX_WEID);
            return new RedisExecutor(redisDomain).execute(client, WeIdDocumentValue.class,weId, datas);
        } catch (WeIdBaseException e) {
            logger.error("[redis->addWeId] add the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> updateWeId(String domain, String weId, String documentSchema) {
        if (StringUtils.isEmpty(weId)) {
            logger.error("[redis->updateWeId] the weId is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        Date date = new Date();
        try {
            RedisDomain redisDomain = new RedisDomain(domain);
            ResponseData<String> response = new RedisExecutor(redisDomain)
                    .executeQuery(redisDomain.getTableDomain(), weId, client);
            if (response.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                WeIdDocumentValue tableData = DataToolUtils.deserialize(
                        response.getResult(), WeIdDocumentValue.class);
                if(tableData.getDeactivated() == 1){
                    logger.error("[redis->updateWeId] the weid is deactivated.");
                    return new ResponseData<>(FAILED_STATUS,
                            ErrorCode.WEID_HAS_BEEN_DEACTIVATED);
                }
                if (StringUtils.isNotBlank(tableData.getDocument_schema())) {
                    int version = tableData.getVersion();
                    version++;
                    Object[] datas = {weId,tableData.getCreated(),date, version, tableData.getDeactivated(), documentSchema};
                    return new RedisExecutor(redisDomain).execute(client,WeIdDocumentValue.class, weId, datas);
                }
            }
            return new ResponseData<>(FAILED_STATUS, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[redis->updateWeId] update the weid error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<WeIdDocument> getWeIdDocument(String domain, String weId) {
        if (StringUtils.isEmpty(weId)) {
            logger.error("[redis->getWeIdDocument] the weId is empty.");
            return new ResponseData<>(null, KEY_INVALID);
        }
        try {
            RedisDomain redisDomain = new RedisDomain(domain);
            ResponseData<String> response = new RedisExecutor(redisDomain)
                    .executeQuery(redisDomain.getTableDomain(), weId, client);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                WeIdDocumentValue tableData = DataToolUtils.deserialize(
                        response.getResult(), WeIdDocumentValue.class);
                if (StringUtils.isNotBlank(tableData.getDocument_schema())) {
                    return new ResponseData<>(WeIdDocument.fromJson(tableData.getDocument_schema()), ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
            }
            return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
        } catch (WeIdBaseException e) {
            logger.error("[redis->getWeIdDocument] get the weIdDocument error.", e);
            return new ResponseData<>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<WeIdDocumentMetadata> getMeta(String domain, String weId) {
        if (StringUtils.isEmpty(weId)) {
            logger.error("[redis->getMeta] the weId is empty.");
            return new ResponseData<>(null, KEY_INVALID);
        }
        try {
            RedisDomain redisDomain = new RedisDomain(domain);
            /////
            ResponseData<String> response = new RedisExecutor(redisDomain)
                    .executeQuery(redisDomain.getTableDomain(), weId, client);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                WeIdDocumentValue tableData = DataToolUtils.deserialize(
                        response.getResult(), WeIdDocumentValue.class);
                if (StringUtils.isNotBlank(tableData.getDocument_schema())) {
                    WeIdDocumentMetadata weIdDocumentMetadata = new WeIdDocumentMetadata();
                    weIdDocumentMetadata.setCreated(tableData.getCreated().getTime());
                    weIdDocumentMetadata.setUpdated(tableData.getUpdated().getTime());
                    weIdDocumentMetadata.setVersionId(tableData.getVersion());
                    weIdDocumentMetadata.setDeactivated(tableData.getDeactivated() == 1);
                    return new ResponseData<>(weIdDocumentMetadata, ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
            }
            return new ResponseData<>(null, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[redis->getMeta] getMeta error.", e);
            return new ResponseData<>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> deactivateWeId(String domain, String weId, Boolean state) {

        if (StringUtils.isEmpty(weId)) {
            logger.error("[redis->deactivateWeId] the weId is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        String dataKey = DataToolUtils.hash(weId);
        Date date = new Date();
        try {
            RedisDomain redisDomain = new RedisDomain(domain);
            ResponseData<String> response = new RedisExecutor(redisDomain)
                    .executeQuery(redisDomain.getTableDomain(), weId, client);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                WeIdDocumentValue tableData = DataToolUtils.deserialize(
                        response.getResult(), WeIdDocumentValue.class);
                if(tableData.getDeactivated() == 1){
                    logger.error("[mysql->deactivateWeId] the weid is deactivated.");
                    return new ResponseData<>(FAILED_STATUS,
                            ErrorCode.WEID_HAS_BEEN_DEACTIVATED);
                }
                if (StringUtils.isNotBlank(tableData.getDocument_schema())) {
                    Object[] datas = {date, tableData.getVersion(), state ? 1:0, tableData.getDocument_schema(), weId};
                    return new RedisExecutor(redisDomain).execute(client, weId, datas);
                }
            }
            return new ResponseData<>(FAILED_STATUS, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[redis->deactivateWeId] deactivate the weId error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<List<String>> getWeIdList(String domain, Integer first, Integer last) {
        try {
            RedisDomain redisDomain = new RedisDomain(domain);
            int[] datas = {first, last - first + 1};
            ResponseData<List<String>> response = new RedisExecutor(redisDomain).executeQueryLines(
                    redisDomain.getTableDomain(),redisDomain.getTableDomain()+DataDriverConstant.REDIS_INDEX_WEID,datas, client);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                return new ResponseData<>(response.getResult(), ErrorCode.SUCCESS);
            }
            return new ResponseData<>(null, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[redis->getWeIdList] get the data error.", e);
            return new ResponseData<>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> getWeIdCount(String domain) {
        try {
            RedisDomain redisDomain = new RedisDomain(domain);
            ResponseData<Integer> response = new RedisExecutor(redisDomain)
                    .executeQueryCount(redisDomain.getTableDomain()+DataDriverConstant.REDIS_INDEX_WEID, client);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                return new ResponseData<>(response.getResult(), ErrorCode.SUCCESS);
            }
            return new ResponseData<>(0, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[redis->getWeIdCount] get the data error.", e);
            return new ResponseData<>(0, e.getErrorCode());
        }
    }
    //**
    //目前只实现了WeId相关接口
    @Override
    public ResponseData<CptValue> getCpt(String domain, int cptId) {
        return null;
    }

    @Override
    public ResponseData<CptBaseInfo> addCpt(String domain, int cptId, String publisher, String description, String cptSchema, String cptSignature) {
        return null;
    }

    @Override
    public ResponseData<PolicyValue> getPolicy(String domain, int policyId) {
        return null;
    }

    @Override
    public ResponseData<Integer> addPolicy(String domain, int policyId, String publisher, String description, String cptSchema, String cptSignature) {
        return null;
    }

    @Override
    public ResponseData<PresentationValue> getPresentation(String domain, int presentationId) {
        return null;
    }

    @Override
    public ResponseData<Integer> addPresentation(String domain, int presentationId, String creator, String policies) {
        return null;
    }

    @Override
    public ResponseData<Integer> updateCpt(String domain, int cptId, int cptVersion, String publisher, String description, String cptSchema, String cptSignature) {
        return null;
    }

    @Override
    public ResponseData<Integer> updateCredentialTemplate(String domain, int cptId, String credentialPublicKey, String credentialProof) {
        return null;
    }

    @Override
    public ResponseData<Integer> updateCptClaimPolicies(String domain, int cptId, String policies) {
        return null;
    }

    @Override
    public ResponseData<List<Integer>> getCptIdList(String domain, Integer first, Integer last) {
        return null;
    }

    @Override
    public ResponseData<Integer> getCptCount(String domain) {
        return null;
    }

    @Override
    public ResponseData<List<Integer>> getPolicyIdList(String domain, Integer first, Integer last) {
        return null;
    }

    @Override
    public ResponseData<Integer> getPolicyCount(String domain) {
        return null;
    }

    @Override
    public ResponseData<Integer> addAuthorityIssuer(String domain, String weId, String name, String desc, String accValue, String extraStr, String extraInt) {
        return null;
    }

    @Override
    public ResponseData<Integer> removeAuthorityIssuer(String domain, String weId) {
        return null;
    }

    @Override
    public ResponseData<AuthorityIssuerInfo> getAuthorityIssuerByWeId(String domain, String weId) {
        return null;
    }

    @Override
    public ResponseData<AuthorityIssuerInfo> getAuthorityIssuerByName(String domain, String name) {
        return null;
    }

    @Override
    public ResponseData<Integer> updateAuthorityIssuer(String domain, String weId, Integer recognize) {
        return null;
    }

    @Override
    public ResponseData<Integer> getAuthorityIssuerCount(String domain) {
        return null;
    }

    @Override
    public ResponseData<Integer> getRecognizedIssuerCount(String domain) {
        return null;
    }

    @Override
    public ResponseData<Integer> addRole(String domain, String weId, Integer roleValue) {
        return null;
    }

    @Override
    public ResponseData<RoleValue> getRole(String domain, String weId) {
        return null;
    }

    @Override
    public ResponseData<Integer> updateRole(String domain, String weId, Integer roleValue) {
        return null;
    }

    @Override
    public ResponseData<Integer> addSpecificType(String domain, String typeName, String owner) {
        return null;
    }

    @Override
    public ResponseData<SpecificTypeValue> getSpecificType(String domain, String typeName) {
        return null;
    }

    @Override
    public ResponseData<Integer> removeSpecificType(String domain, String typeName) {
        return null;
    }

    @Override
    public ResponseData<Integer> updateSpecificTypeFellow(String domain, String typeName, String fellow) {
        return null;
    }

    @Override
    public ResponseData<Integer> getIssuerTypeCount(String domain) {
        return null;
    }

    @Override
    public ResponseData<List<String>> getIssuerTypeList(String domain, Integer first, Integer last) {
        return null;
    }

    @Override
    public ResponseData<Integer> addEvidenceByHash(String domain, String hashValue, String signer, String signature, String log, String updated, String revoked, String extraKey, String group_id) {
        return null;
    }

    @Override
    public ResponseData<EvidenceValue> getEvidenceByHash(String domain, String hash) {
        return null;
    }

    @Override
    public ResponseData<Integer> addSignatureAndLogs(String domain, String hashValue, String signer, String signature, String log, String updated, String revoked, String extraKey) {
        return null;
    }

    @Override
    public ResponseData<EvidenceValue> getEvidenceByExtraKey(String domain, String extraKey) {
        return null;
    }


    public void addIndexForMsg(String msg,String domain){
        RScoredSortedSet<Object> set = client.getScoredSortedSet(domain);
        set.add(set.size(),msg);
    }
  }