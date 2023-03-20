

package com.webank.weid.suite.persistence.mysql.driver;

import com.webank.weid.blockchain.protocol.base.CptBaseInfo;
import com.webank.weid.blockchain.protocol.base.WeIdDocumentMetadata;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.AuthenticationProperty;
import com.webank.weid.blockchain.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.TransactionArgs;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.suite.persistence.CptValue;
import com.webank.weid.suite.persistence.Persistence;
import com.webank.weid.suite.persistence.DefaultValue;
import com.webank.weid.suite.persistence.WeIdDocumentValue;
import com.webank.weid.suite.persistence.mysql.SqlDomain;
import com.webank.weid.suite.persistence.mysql.SqlExecutor;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * mysql operations.
 *
 * @author tonychen 2019年3月18日
 */
public class MysqlDriver implements Persistence {

    private static final Logger logger = LoggerFactory.getLogger(
            MysqlDriver.class);

    private static final String CHECK_TABLE_SQL =
        "SELECT table_name "
            + DataDriverConstant.SQL_COLUMN_DATA
            + " FROM information_schema.TABLES "
            + " WHERE upper(table_name) = upper('$1')"
            + " and upper(table_schema) = upper('$2')";

    private static final String CREATE_TABLE_SQL =
        "CREATE TABLE `$1` ("
            + "`id` varchar(128) NOT NULL COMMENT 'primary key',"
            + "`data` blob DEFAULT NULL COMMENT 'the add data', "
            + "`created` datetime DEFAULT NULL COMMENT 'created', "
            + "`updated` datetime DEFAULT NULL COMMENT 'updated', "
            + "`protocol` varchar(32) DEFAULT NULL COMMENT 'protocol', "
            + "`expire` datetime DEFAULT NULL COMMENT 'the expire time', "
            + "`version` varchar(10) DEFAULT NULL COMMENT 'the data version', "
            + "`ext1` int DEFAULT NULL COMMENT 'extend field1', "
            + "`ext2` int DEFAULT NULL COMMENT 'extend field2', "
            + "`ext3` varchar(500) DEFAULT NULL COMMENT 'extend field3', "
            + "`ext4` varchar(500) DEFAULT NULL COMMENT 'extend field4', "
            + "PRIMARY KEY (`id`) "
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='the data table'";

    private static final String CREATE_TABLE_WEID_DOCUMENT_SQL =
            "CREATE TABLE table_weid_document ("
                    + "`weid` varchar(60) NOT NULL COMMENT 'weid',"
                    + "`created` datetime DEFAULT NULL COMMENT 'created', "
                    + "`updated` datetime DEFAULT NULL COMMENT 'updated', "
                    + "`version` int DEFAULT NULL COMMENT 'the document version', "
                    + "`deactivated` int DEFAULT NULL COMMENT 'deactivated', "
                    + "`document_schema` blob DEFAULT NULL COMMENT 'json schema of document', "
                    + "PRIMARY KEY (`weid`) "
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='the weid document table'";

    private static final String CREATE_TABLE_CPT_SQL =
            "CREATE TABLE table_cpt ("
                    + "`cpt_id` int NOT NULL COMMENT 'cpt id',"
                    + "`created` datetime DEFAULT NULL COMMENT 'created', "
                    + "`updated` datetime DEFAULT NULL COMMENT 'updated', "
                    + "`cpt_version` int DEFAULT NULL COMMENT 'the cpt version', "
                    + "`publisher` varchar(60) DEFAULT NULL COMMENT 'publisher', "
                    + "`description` varchar(1000) DEFAULT NULL COMMENT 'description of cpt', "
                    + "`cpt_schema` blob DEFAULT NULL COMMENT 'json schema of cpt', "
                    + "`cpt_signature` varchar(500) DEFAULT NULL COMMENT 'signature of cpt', "
                    + "PRIMARY KEY (`cpt_id`) "
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='the cpt table'";

    private static final String CREATE_TABLE_ROLE_SQL =
            "CREATE TABLE table_role ("
                    + "`weid` varchar(60) NOT NULL COMMENT 'weid',"
                    + "`created` datetime DEFAULT NULL COMMENT 'created', "
                    + "`updated` datetime DEFAULT NULL COMMENT 'updated', "
                    + "`authority_role` int DEFAULT NULL COMMENT 'the authority role', "
                    + "`committee_role` int DEFAULT NULL COMMENT 'the committee role', "
                    + "`admin_role` int DEFAULT NULL COMMENT 'the admin role', "
                    + "PRIMARY KEY (`weid`) "
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='the role table'";

    private static final String CREATE_TABLE_AUTHORITY_ISSUER_SQL =
            "CREATE TABLE table_authority_issuer ("
                    + "`weid` varchar(60) NOT NULL COMMENT 'weid',"
                    + "`name` varchar(60) UNIQUE DEFAULT NULL COMMENT 'name',"
                    + "`desc` varchar(1000) DEFAULT NULL COMMENT 'desc',"
                    + "`created` datetime DEFAULT NULL COMMENT 'created', "
                    + "`updated` datetime DEFAULT NULL COMMENT 'updated', "
                    + "`recognize` int DEFAULT NULL COMMENT 'is recognized', "
                    + "`accValue` varchar(1000) DEFAULT NULL COMMENT 'the accValue', "
                    + "PRIMARY KEY (`weid`) "
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='the authority issuer table'";

    private static final String CREATE_TABLE_SPECIFIC_ISSUER_SQL =
            "CREATE TABLE table_authority_issuer ("
                    + "`type_name` varchar(60) NOT NULL COMMENT 'specific issuer type name',"
                    + "`fellow` blob DEFAULT NULL COMMENT 'fellow addresses',"
                    + "`created` datetime DEFAULT NULL COMMENT 'created', "
                    + "`updated` datetime DEFAULT NULL COMMENT 'updated', "
                    + "`owner` varchar(60) DEFAULT NULL COMMENT 'owner',"
                    + "PRIMARY KEY (`type_name`) "
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='the specific issuer table'";

    private static final Integer FAILED_STATUS = DataDriverConstant.SQL_EXECUTE_FAILED_STATUS;

    private static final ErrorCode KEY_INVALID = ErrorCode.PRESISTENCE_DATA_KEY_INVALID;

    private static Boolean isinit = false;

    private static int CPT_DEFAULT_VERSION = 1;

    /**
     * the Constructor and init all domain.
     */
    public MysqlDriver() {
        if (!isinit) {
            synchronized (MysqlDriver.class) {
                if (!isinit) {
                    initDomain();
                    isinit = true;
                }
            }
        }
    }

    @Override
    public ResponseData<String> get(String domain, String id) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[mysql->get] the id of the data is empty.");
            return new ResponseData<String>(StringUtils.EMPTY, KEY_INVALID);
        }
        String dataKey = DataToolUtils.hash(id);
        try {
            ResponseData<String> result = new ResponseData<String>();
            result.setResult(StringUtils.EMPTY);
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                .executeQuery(SqlExecutor.SQL_QUERY, dataKey);
            if (response.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()
                && response.getResult() != null) {
                DefaultValue tableData = DataToolUtils.deserialize(
                    DataToolUtils.serialize(response.getResult()), DefaultValue.class);
                if (tableData.getExpire() != null && tableData.getExpire().before(new Date())) {
                    logger.error("[mysql->get] the data is expire.");
                    return new ResponseData<String>(StringUtils.EMPTY,
                            ErrorCode.PERSISTENCE_DATA_EXPIRE);
                }
                if (StringUtils.isNotBlank(tableData.getData())) {
                    result.setResult(tableData.getData());
                }
            }
            result.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
            return result;
        } catch (WeIdBaseException e) {
            logger.error("[mysql->get] get the data error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, e.getErrorCode());
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#add(java.lang.String, java.lang.String)
     */
    @Override
    public ResponseData<Integer> add(String domain, String id, String data) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[mysql->add] the id of the data is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        String dataKey = DataToolUtils.hash(id);
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            Object[] datas = {dataKey, data, sqlDomain.getExpire(), now, now};
            return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE, datas);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->add] add the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }


    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#batchAdd(java.util.List, java.util.List)
     */
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
                    logger.error("[mysql->batchAdd] the id of the data is empty.");
                    return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
                }
                idHashList.add(DataToolUtils.hash(id));
                dataList.add(data);
            }
            SqlDomain sqlDomain = new SqlDomain(domain);
            List<List<Object>> dataLists = new ArrayList<List<Object>>();
            dataLists.add(idHashList);
            dataLists.add(Arrays.asList(dataList.toArray()));
            dataLists.add(fixedListWithDefault(idHashList.size(), sqlDomain.getExpire()));

            //处理创建时间和更新时间
            List<Object> nowList = fixedListWithDefault(idHashList.size(), sqlDomain.getNow());
            dataLists.add(nowList);
            dataLists.add(nowList);
            return new SqlExecutor(sqlDomain).batchAdd(SqlExecutor.SQL_SAVE, dataLists);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->batchAdd] batchAdd the data error.", e);
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

    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#delete(java.lang.String)
     */
    @Override
    public ResponseData<Integer> delete(String domain, String id) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[mysql->delete] the id of the data is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        String dataKey = DataToolUtils.hash(id);
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_DELETE, dataKey);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->delete] delete the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#update(java.lang.String, java.lang.String)
     */
    @Override
    public ResponseData<Integer> update(String domain, String id, String data) {

        if (StringUtils.isEmpty(id)) {
            logger.error("[mysql->update] the id of the data is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        String dataKey = DataToolUtils.hash(id);
        Date date = new Date();
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Object[] datas = {date, data, sqlDomain.getExpire(), dataKey};
            return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE, datas);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->update] update the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    /**
     * 初始化domain.
     */
    private void initDomain() {
        Set<String> domainKeySet = analyzeDomainValue();
        Set<String> localKeySet = analyzeLocalValue();
        for (String domainKey : domainKeySet) {
            SqlExecutor sqlExecutor = new SqlExecutor(new SqlDomain(domainKey));
            sqlExecutor.resolveTableDomain(CHECK_TABLE_SQL, CREATE_TABLE_SQL);
        }
        for (String localKey : localKeySet) {
            SqlExecutor sqlExecutor = new SqlExecutor(new SqlDomain(localKey));
            switch (localKey) {
                case "local.weIdDocument" : sqlExecutor.resolveTableDomain(CHECK_TABLE_SQL, CREATE_TABLE_WEID_DOCUMENT_SQL);
                case "local.cpt" : sqlExecutor.resolveTableDomain(CHECK_TABLE_SQL, CREATE_TABLE_CPT_SQL);
            }
        }
    }

    /**
     * 分析配置中的domain配置, 并且获取对应的配置项key.
     *
     * @return 返回配置值
     */
    private Set<String> analyzeDomainValue() {
        Set<Object> keySet = PropertyUtils.getAllPropertyKey();
        Set<String> domainKeySet = new HashSet<>();
        for (Object object : keySet) {
            String key = String.valueOf(object);
            if (key.indexOf(SqlDomain.KEY_SPLIT_CHAR) == key.lastIndexOf(SqlDomain.KEY_SPLIT_CHAR)
                && key.startsWith(SqlDomain.DOMAIN_PREFIX)) {
                domainKeySet.add(key);
            }
        }
        return domainKeySet;
    }

    /**
     * 分析配置中的local配置, 并且获取对应的配置项key.
     *
     * @return 返回配置值
     */
    private Set<String> analyzeLocalValue() {
        Set<Object> keySet = PropertyUtils.getAllPropertyKey();
        Set<String> localKeySet = new HashSet<>();
        for (Object object : keySet) {
            String key = String.valueOf(object);
            if (key.indexOf(SqlDomain.KEY_SPLIT_CHAR) == key.lastIndexOf(SqlDomain.KEY_SPLIT_CHAR)
                    && key.startsWith(SqlDomain.LOCAL_PREFIX)) {
                localKeySet.add(key);
            }
        }
        return localKeySet;
    }

    /* (non-Javadoc)
     * @see com.webank.weid.suite.persistence.Persistence#addOrUpdate(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public ResponseData<Integer> addOrUpdate(String domain, String id, String data) {
        ResponseData<String> getRes = this.get(domain, id);
        //如果查询数据存在，或者失效 则进行更新 否则进行新增
        if ((StringUtils.isNotBlank(getRes.getResult())
            && getRes.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode())
            || getRes.getErrorCode().intValue() == ErrorCode.PERSISTENCE_DATA_EXPIRE.getCode()) {
            return this.update(domain, id, data);
        }
        return this.add(domain, id, data);
    }


    /* (non-Javadoc)
     * @see com.webank.weid.suite.persistence.Persistence#addTransaction(
     * com.webank.weid.protocol.request.TransactionArgs)
     */
    @Override
    public ResponseData<Integer> addTransaction(TransactionArgs transactionArgs) {

        if (StringUtils.isEmpty(transactionArgs.getRequestId())) {
            logger.error("[mysql->add] the id of the data is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(DataDriverConstant.DOMAIN_DEFAULT_INFO);
            Object[] datas = {
                transactionArgs.getRequestId(),
                transactionArgs.getMethod(),
                transactionArgs.getArgs(),
                transactionArgs.getTimeStamp(),
                transactionArgs.getExtra(),
                transactionArgs.getBatch()
            };
            return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_TRANSACTION, datas);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->add] add the data error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> addWeId(String domain, String weId, String documentSchema) {

        if (StringUtils.isEmpty(weId)) {
            logger.error("[mysql->addWeId] the weId is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            Object[] datas = {weId, now, now, 1, 0, documentSchema};
            return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_WEID, datas);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->addWeId] addWeId error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> updateWeId(String domain, String weId, String documentSchema) {

        if (StringUtils.isEmpty(weId)) {
            logger.error("[mysql->updateWeId] the weId is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        Date date = new Date();
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_WEID, weId);
            if (response.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                WeIdDocumentValue tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), WeIdDocumentValue.class);
                if(tableData.getDeactivated() == 1){
                    logger.error("[mysql->updateWeId] the weid is deactivated.");
                    return new ResponseData<>(FAILED_STATUS,
                            ErrorCode.WEID_HAS_BEEN_DEACTIVATED);
                }
                if (StringUtils.isNotBlank(tableData.getDocumentSchema())) {
                    int version = tableData.getVersion();
                    version++;
                    Object[] datas = {date, version, tableData.getDeactivated(), documentSchema, weId};
                    return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_WEID, datas);
                }
            }
            return new ResponseData<>(FAILED_STATUS, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[mysql->updateWeId] update the weid error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<WeIdDocument> getWeIdDocument(String domain, String weId) {

        if (StringUtils.isEmpty(weId)) {
            logger.error("[mysql->getWeIdDocument] the weId is empty.");
            return new ResponseData<>(null, KEY_INVALID);
        }
        String dataKey = DataToolUtils.hash(weId);
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_WEID, dataKey);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                WeIdDocumentValue tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), WeIdDocumentValue.class);
                if (StringUtils.isNotBlank(tableData.getDocumentSchema())) {
                    return new ResponseData<>(WeIdDocument.fromJson(tableData.getDocumentSchema()), ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
            }
            return new ResponseData<>(null, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[mysql->getWeIdDocument] get the weIdDocument error.", e);
            return new ResponseData<>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<WeIdDocumentMetadata> getMeta(String domain, String weId) {

        if (StringUtils.isEmpty(weId)) {
            logger.error("[mysql->getMeta] the weId is empty.");
            return new ResponseData<>(null, KEY_INVALID);
        }
        String dataKey = DataToolUtils.hash(weId);
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_WEID, dataKey);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                WeIdDocumentValue tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), WeIdDocumentValue.class);
                if (StringUtils.isNotBlank(tableData.getDocumentSchema())) {
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
            logger.error("[mysql->getMeta] getMeta error.", e);
            return new ResponseData<>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> deactivateWeId(String domain, String weId, Boolean state) {

        if (StringUtils.isEmpty(weId)) {
            logger.error("[mysql->deactivateWeId] the weId is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        String dataKey = DataToolUtils.hash(weId);
        Date date = new Date();
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_WEID, dataKey);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                WeIdDocumentValue tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), WeIdDocumentValue.class);
                if(tableData.getDeactivated() == 1){
                    logger.error("[mysql->updateWeId] the weid is deactivated.");
                    return new ResponseData<>(FAILED_STATUS,
                            ErrorCode.WEID_HAS_BEEN_DEACTIVATED);
                }
                if (StringUtils.isNotBlank(tableData.getDocumentSchema())) {
                    Object[] datas = {date, tableData.getVersion(), state ? 1:0, tableData.getDocumentSchema(), weId};
                    return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_WEID, datas);
                }
            }
            return new ResponseData<>(FAILED_STATUS, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[mysql->deactivateWeId] deactivate the weId error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<List<String>> getWeIdList(String domain, Integer first, Integer last) {
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Object[] datas = {first, last - first + 1};
            ResponseData<List<String>> response = new SqlExecutor(sqlDomain)
                    .executeQueryLines(SqlExecutor.SQL_QUERY_SEVERAL_WEID, datas);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                    return new ResponseData<>(response.getResult(), ErrorCode.SUCCESS);
            }
            return new ResponseData<>(null, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[mysql->get] get the data error.", e);
            return new ResponseData<>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> getWeIdCount(String domain) {
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Integer> response = new SqlExecutor(sqlDomain)
                    .executeQueryAmounts(SqlExecutor.SQL_QUERY_TOTAL_LINE);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                return new ResponseData<>(response.getResult(), ErrorCode.SUCCESS);
            }
            return new ResponseData<>(0, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[mysql->get] get the data error.", e);
            return new ResponseData<>(0, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<CptValue> getCpt(String domain, int cptId) {

        if (cptId<=0) {
            logger.error("[mysql->getCpt] the cptId is invalid.");
            return new ResponseData<CptValue>(null, KEY_INVALID);
        }
        try {
            ResponseData<CptValue> result = new ResponseData<CptValue>();
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_CPT, cptId);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                CptValue tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), CptValue.class);
                if (StringUtils.isNotBlank(tableData.getCptSchema())) {
                    result.setResult(tableData);
                    return new ResponseData<>(tableData, ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
            }
            result.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
            return result;
        } catch (WeIdBaseException e) {
            logger.error("[mysql->getCpt] getCpt error.", e);
            return new ResponseData<CptValue>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<CptBaseInfo> addCpt(String domain, int cptId, String publisher, String description, String cptSchema, String cptSignature) {

        if (cptId<=0) {
            logger.error("[mysql->addCpt] the cptId is invalid.");
            return new ResponseData<CptBaseInfo>(null, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            Object[] datas = {cptId, now, now, CPT_DEFAULT_VERSION, publisher, description, cptSchema, cptSignature};
            ResponseData<Integer> result = new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_CPT, datas);
            if (result.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[addCpt] add Cpt to db failed.");
                throw new DatabaseException("database error!");
            }
            CptBaseInfo cptBaseInfo = new CptBaseInfo();
            cptBaseInfo.setCptId(cptId);
            cptBaseInfo.setCptVersion(CPT_DEFAULT_VERSION);
            return new ResponseData<>(cptBaseInfo, ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->addWeId] addWeId error.", e);
            return new ResponseData<CptBaseInfo>(null, e.getErrorCode());
        }
    }
}
