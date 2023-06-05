

package com.webank.weid.suite.persistence.mysql.driver;

import com.webank.weid.blockchain.protocol.base.AuthorityIssuer;
import com.webank.weid.blockchain.protocol.base.CptBaseInfo;
import com.webank.weid.blockchain.protocol.base.WeIdDocumentMetadata;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.contract.deploy.AddressProcess;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.AuthenticationProperty;
import com.webank.weid.blockchain.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.TransactionArgs;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.service.local.CptServiceLocal;
import com.webank.weid.suite.persistence.*;
import com.webank.weid.suite.persistence.mysql.SqlDomain;
import com.webank.weid.suite.persistence.mysql.SqlExecutor;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.PropertyUtils;
import com.webank.weid.util.WeIdUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
            "CREATE TABLE `$1` ("
                    + "`weid` varchar(100) NOT NULL UNIQUE COMMENT 'weid',"
                    + "`created` datetime DEFAULT NULL COMMENT 'created', "
                    + "`updated` datetime DEFAULT NULL COMMENT 'updated', "
                    + "`version` int DEFAULT NULL COMMENT 'the document version', "
                    + "`deactivated` int DEFAULT NULL COMMENT 'deactivated', "
                    + "`document_schema` blob DEFAULT NULL COMMENT 'json schema of document', "
                    + "PRIMARY KEY (`weid`) "
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='the weid document table'";

    private static final String CREATE_TABLE_CPT_SQL =
            "CREATE TABLE `$1` ("
                    + "`cpt_id` int NOT NULL UNIQUE COMMENT 'cpt id',"
                    + "`created` datetime DEFAULT NULL COMMENT 'created', "
                    + "`updated` datetime DEFAULT NULL COMMENT 'updated', "
                    + "`cpt_version` int DEFAULT NULL COMMENT 'the cpt version', "
                    + "`publisher` varchar(60) DEFAULT NULL COMMENT 'publisher', "
                    + "`description` varchar(1000) DEFAULT NULL COMMENT 'description of cpt', "
                    + "`cpt_schema` blob DEFAULT NULL COMMENT 'json schema of cpt', "
                    + "`cpt_signature` varchar(500) DEFAULT NULL COMMENT 'signature of cpt', "
                    + "`credential_publicKey` varchar(60) DEFAULT NULL COMMENT 'publicKey of credential template', "
                    + "`credential_proof` varchar(1000) DEFAULT NULL COMMENT 'proof of credential template', "
                    + "`claim_policies` varchar(1000) DEFAULT NULL COMMENT 'policy id list', "
                    + "PRIMARY KEY (`cpt_id`) "
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='the cpt table'";

    private static final String CREATE_TABLE_POLICY_SQL =
            "CREATE TABLE `$1` ("
                    + "`policy_id` int NOT NULL UNIQUE COMMENT 'policy id',"
                    + "`created` datetime DEFAULT NULL COMMENT 'created', "
                    + "`updated` datetime DEFAULT NULL COMMENT 'updated', "
                    + "`policy_version` int DEFAULT NULL COMMENT 'the policy version', "
                    + "`publisher` varchar(60) DEFAULT NULL COMMENT 'publisher', "
                    + "`description` varchar(1000) DEFAULT NULL COMMENT 'description of policy', "
                    + "`policy_schema` blob DEFAULT NULL COMMENT 'json schema of policy', "
                    + "`policy_signature` varchar(500) DEFAULT NULL COMMENT 'signature of policy', "
                    + "`credential_publicKey` varchar(60) DEFAULT NULL COMMENT 'publicKey of credential template', "
                    + "`credential_proof` varchar(1000) DEFAULT NULL COMMENT 'proof of credential template', "
                    + "`claim_policies` varchar(1000) DEFAULT NULL COMMENT 'policy id list', "
                    + "PRIMARY KEY (`policy_id`) "
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='the policy table'";

    private static final String CREATE_TABLE_PRESENTATION_SQL =
            "CREATE TABLE `$1` ("
                    + "`presentation_id` int NOT NULL UNIQUE COMMENT 'presentation id',"
                    + "`creator` varchar(60) DEFAULT NULL COMMENT 'creator', "
                    + "`claim_policies` varchar(1000) DEFAULT NULL COMMENT 'policy id list', "
                    + "PRIMARY KEY (`presentation_id`) "
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='the presentation table'";

    private static final String CREATE_TABLE_ROLE_SQL =
            "CREATE TABLE `$1` ("
                    + "`weid` varchar(100) NOT NULL UNIQUE COMMENT 'weid',"
                    + "`created` datetime DEFAULT NULL COMMENT 'created', "
                    + "`updated` datetime DEFAULT NULL COMMENT 'updated', "
                    + "`authority_role` int DEFAULT NULL COMMENT 'the authority role', "
                    + "`committee_role` int DEFAULT NULL COMMENT 'the committee role', "
                    + "`admin_role` int DEFAULT NULL COMMENT 'the admin role', "
                    + "PRIMARY KEY (`weid`) "
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='the role table'";

    private static final String CREATE_TABLE_AUTHORITY_ISSUER_SQL =
            "CREATE TABLE `$1` ("
                    + "`weid` varchar(100) NOT NULL UNIQUE COMMENT 'weid',"
                    + "`name` varchar(60) UNIQUE DEFAULT NULL COMMENT 'name',"
                    + "`description` varchar(1000) DEFAULT NULL COMMENT 'desc',"
                    + "`created` datetime DEFAULT NULL COMMENT 'created', "
                    + "`updated` datetime DEFAULT NULL COMMENT 'updated', "
                    + "`recognize` int DEFAULT NULL COMMENT 'is recognized', "
                    + "`acc_value` varchar(1000) DEFAULT NULL COMMENT 'the accValue', "
                    + "`extra_str` blob DEFAULT NULL COMMENT 'the extraStr', "
                    + "`extra_int` blob DEFAULT NULL COMMENT 'the extraInt', "
                    + "PRIMARY KEY (`weid`) "
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='the authority issuer table'";

    private static final String CREATE_TABLE_SPECIFIC_ISSUER_SQL =
            "CREATE TABLE `$1` ("
                    + "`type_name` varchar(60) NOT NULL UNIQUE COMMENT 'specific issuer type name',"
                    + "`fellow` blob DEFAULT NULL COMMENT 'fellow addresses',"
                    + "`created` datetime DEFAULT NULL COMMENT 'created', "
                    + "`updated` datetime DEFAULT NULL COMMENT 'updated', "
                    + "`owner` varchar(60) DEFAULT NULL COMMENT 'owner',"
                    + "PRIMARY KEY (`type_name`) "
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='the specific issuer table'";

    private static final String CREATE_TABLE_EVIDENCE_SQL =
            "CREATE TABLE `$1` ("
                    + "`hash` varchar(100) NOT NULL UNIQUE COMMENT 'hash of evidence',"
                    + "`signers` blob DEFAULT NULL COMMENT 'signers',"
                    + "`signatures` blob DEFAULT NULL COMMENT 'signatures', "
                    + "`logs` blob DEFAULT NULL COMMENT 'logs', "
                    + "`updated` blob DEFAULT NULL COMMENT 'updated', "
                    + "`revoked` blob DEFAULT NULL COMMENT 'revoked',"
                    + "`extra_key` varchar(100) DEFAULT NULL COMMENT 'extraKey of evidence',"
                    + "`extra_data` blob DEFAULT NULL COMMENT 'extraData of evidence',"
                    + "`group_id` blob NOT NULL COMMENT 'group_id of evidence',"
                    + "PRIMARY KEY (`hash`) "
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='the evidence table'";

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
        String type = PropertyUtils.getProperty("deploy.style");
        if (type.equals("database")) {
            // 初始化表
            for (String localKey : localKeySet) {
                SqlExecutor sqlExecutor = new SqlExecutor(new SqlDomain(localKey));
                switch (localKey) {
                    case "local.weIdDocument" : sqlExecutor.resolveTableDomain(CHECK_TABLE_SQL, CREATE_TABLE_WEID_DOCUMENT_SQL);
                    case "local.cpt" : sqlExecutor.resolveTableDomain(CHECK_TABLE_SQL, CREATE_TABLE_CPT_SQL);
                    case "local.policy" : sqlExecutor.resolveTableDomain(CHECK_TABLE_SQL, CREATE_TABLE_POLICY_SQL);
                    case "local.presentation" : sqlExecutor.resolveTableDomain(CHECK_TABLE_SQL, CREATE_TABLE_PRESENTATION_SQL);
                    case "local.role" : sqlExecutor.resolveTableDomain(CHECK_TABLE_SQL, CREATE_TABLE_ROLE_SQL);
                    case "local.authorityIssuer" : sqlExecutor.resolveTableDomain(CHECK_TABLE_SQL, CREATE_TABLE_AUTHORITY_ISSUER_SQL);
                    case "local.specificIssuer" : sqlExecutor.resolveTableDomain(CHECK_TABLE_SQL, CREATE_TABLE_SPECIFIC_ISSUER_SQL);
                    case "local.evidence" : sqlExecutor.resolveTableDomain(CHECK_TABLE_SQL, CREATE_TABLE_EVIDENCE_SQL);
                }
            }
            // 初始化管理员权限
            String privateKey = getAddressFromFile("private_key");
            RoleValue result = getRole(DataDriverConstant.LOCAL_ROLE, WeIdUtils.getWeIdFromPrivateKey(privateKey)).getResult();
            if(result == null) {
                ResponseData<Integer> resp = addRole(DataDriverConstant.LOCAL_ROLE, WeIdUtils.getWeIdFromPrivateKey(privateKey), 7);
                if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                    logger.error("[initDomain] save admin role to db failed.");
                    throw new DatabaseException("database error!");
                }
            }
        }
    }

    protected static String getAddressFromFile(
            String fileName) {

        BufferedReader br = null;
        try {
            File file = new File(fileName);
            if (file.exists()) {
                br = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file))
                );
                String address = br.readLine();
                return address;
            }
        } catch (IOException e) {
            logger.error("writer file exception", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error("io close exception", e);
                }
            }
        }
        logger.error("getAddressFromFile() the {} does not exists.", fileName);
        return StringUtils.EMPTY;
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
                if (StringUtils.isNotBlank(tableData.getDocument_schema())) {
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
        //String dataKey = DataToolUtils.hash(weId);
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_WEID, weId);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                WeIdDocumentValue tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), WeIdDocumentValue.class);
                if (StringUtils.isNotBlank(tableData.getDocument_schema())) {
                    return new ResponseData<>(WeIdDocument.fromJson(tableData.getDocument_schema()), ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
            }
            return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
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
        //String dataKey = DataToolUtils.hash(weId);
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_WEID, weId);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                WeIdDocumentValue tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), WeIdDocumentValue.class);
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
                    logger.error("[mysql->deactivateWeId] the weid is deactivated.");
                    return new ResponseData<>(FAILED_STATUS,
                            ErrorCode.WEID_HAS_BEEN_DEACTIVATED);
                }
                if (StringUtils.isNotBlank(tableData.getDocument_schema())) {
                    Object[] datas = {date, tableData.getVersion(), state ? 1:0, tableData.getDocument_schema(), weId};
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
            logger.error("[mysql->getWeIdList] get the data error.", e);
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
            logger.error("[mysql->getWeIdCount] get the data error.", e);
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
                if (StringUtils.isNotBlank(tableData.getCpt_schema())) {
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
            logger.error("[mysql->addCpt] addWeId error.", e);
            return new ResponseData<CptBaseInfo>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> updateCpt(String domain, int cptId, int cptVersion, String publisher, String description, String cptSchema, String cptSignature) {

        if (cptId<=0) {
            logger.error("[mysql->updateCpt] the cptId is invalid.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            Object[] datas = {now, cptVersion, publisher, description, cptSchema, cptSignature, cptId};
            return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_CPT, datas);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->updateCpt] updateCpt error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<List<Integer>> getCptIdList(String domain, Integer first, Integer last) {
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Object[] datas = {first, last - first};
            ResponseData<List<String>> response = new SqlExecutor(sqlDomain)
                    .executeQueryLines(SqlExecutor.SQL_QUERY_SEVERAL_CPT, datas);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                List<Integer> cptList = new ArrayList<>();
                for(int i=0; i<response.getResult().size(); i++){
                    cptList.add(Integer.valueOf(response.getResult().get(i)));
                }
                return new ResponseData<>(cptList, ErrorCode.SUCCESS);
            }
            return new ResponseData<>(null, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[mysql->getCptIdList] get the CptIdList error.", e);
            return new ResponseData<>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> getCptCount(String domain) {
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
            logger.error("[mysql->getCptCount] get the count of cpt error.", e);
            return new ResponseData<>(0, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<List<Integer>> getPolicyIdList(String domain, Integer first, Integer last) {
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Object[] datas = {first, last - first};
            ResponseData<List<String>> response = new SqlExecutor(sqlDomain)
                    .executeQueryLines(SqlExecutor.SQL_QUERY_SEVERAL_POLICY, datas);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                List<Integer> cptList = new ArrayList<>();
                for(int i=0; i<response.getResult().size(); i++){
                    cptList.add(Integer.valueOf(response.getResult().get(i)));
                }
                return new ResponseData<>(cptList, ErrorCode.SUCCESS);
            }
            return new ResponseData<>(null, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[mysql->getPolicyIdList] get the PolicyIdList error.", e);
            return new ResponseData<>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> getPolicyCount(String domain) {
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
            logger.error("[mysql->getPolicyCount] get the count of policy error.", e);
            return new ResponseData<>(0, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> updateCredentialTemplate(String domain, int cptId, String credentialPublicKey, String credentialProof) {

        if (cptId<=0) {
            logger.error("[mysql->putCredentialTemplate] the cptId is invalid.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            Object[] datas = {credentialPublicKey, credentialProof, cptId};
            return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_CREDENTIAL_TEMPLATE, datas);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->putCredentialTemplate] updateCpt error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> updateCptClaimPolicies(String domain, int cptId, String policies) {

        if (cptId<=0) {
            logger.error("[mysql->updateCptClaimPolicies] the cptId is invalid.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            Object[] datas = {policies, cptId};
            return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_CLAIM_POLICIES, datas);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->updateCptClaimPolicies] updateCptClaimPolicies error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> addPolicy(String domain, int policyId, String publisher, String description, String cptSchema, String cptSignature) {

        if (policyId<=0) {
            logger.error("[mysql->addPolicy] the policyId is invalid.");
            return new ResponseData<Integer>(null, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            Object[] datas = {policyId, now, now, CPT_DEFAULT_VERSION, publisher, description, cptSchema, cptSignature};
            ResponseData<Integer> result = new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_POLICY, datas);
            if (result.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[addPolicy] add policy to db failed.");
                throw new DatabaseException("database error!");
            }
            return new ResponseData<>(result.getResult(), ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->addPolicy] addPolicy error.", e);
            return new ResponseData<Integer>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<PolicyValue> getPolicy(String domain, int policyId) {
        //Policy和cpt的表一样，所以可以复用CptValue
        if (policyId<=0) {
            logger.error("[mysql->getCpt] the cptId is invalid.");
            return new ResponseData<PolicyValue>(null, KEY_INVALID);
        }
        try {
            ResponseData<PolicyValue> result = new ResponseData<PolicyValue>();
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_POLICY, policyId);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                PolicyValue tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), PolicyValue.class);
                if (StringUtils.isNotBlank(tableData.getPolicy_schema())) {
                    result.setResult(tableData);
                    return new ResponseData<>(tableData, ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.CPT_NOT_EXISTS);
            }
            result.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
            return result;
        } catch (WeIdBaseException e) {
            logger.error("[mysql->getPolicy] getCpt error.", e);
            return new ResponseData<PolicyValue>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> addPresentation(String domain, int presentationId, String creator, String policies) {

        if (presentationId<=0) {
            logger.error("[mysql->addPresentation] the presentationId is invalid.");
            return new ResponseData<Integer>(null, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            Object[] datas = {presentationId, creator, policies};
            ResponseData<Integer> result = new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_PRESENTATION, datas);
            if (result.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[addPresentation] add presentation to db failed.");
                throw new DatabaseException("database error!");
            }
            return new ResponseData<>(result.getResult(), ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->addPresentation] addPresentation error.", e);
            return new ResponseData<Integer>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<PresentationValue> getPresentation(String domain, int presentationId) {
        if (presentationId<=0) {
            logger.error("[mysql->getPresentation] the presentationId is invalid.");
            return new ResponseData<PresentationValue>(null, KEY_INVALID);
        }
        try {
            ResponseData<PresentationValue> result = new ResponseData<PresentationValue>();
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_PRESENTATION, presentationId);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                PresentationValue tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), PresentationValue.class);
                if (StringUtils.isNotBlank(tableData.getClaim_policies())) {
                    result.setResult(tableData);
                    return new ResponseData<>(tableData, ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.PRESENTATION_POLICY_INVALID);
            }
            result.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
            return result;
        } catch (WeIdBaseException e) {
            logger.error("[mysql->getPresentation] getPresentation error.", e);
            return new ResponseData<PresentationValue>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<AuthorityIssuerInfo> getAuthorityIssuerByWeId(String domain, String weId) {

        if (StringUtils.isEmpty(weId)) {
            logger.error("[mysql->getAuthorityIssuerByWeId] the weId is empty.");
            return new ResponseData<AuthorityIssuerInfo>(null, KEY_INVALID);
        }
        try {
            ResponseData<AuthorityIssuerInfo> result = new ResponseData<AuthorityIssuerInfo>();
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_AUTHORITY_ISSUER_BY_ADDRESS, weId);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                AuthorityIssuerInfo tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), AuthorityIssuerInfo.class);
                if (StringUtils.isNotBlank(tableData.getName())) {
                    result.setResult(tableData);
                    return new ResponseData<>(tableData, ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS);
            }
            result.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
            return result;
        } catch (WeIdBaseException e) {
            logger.error("[mysql->getAuthorityIssuerByWeId] getAuthorityIssuerByWeId error.", e);
            return new ResponseData<AuthorityIssuerInfo>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<AuthorityIssuerInfo> getAuthorityIssuerByName(String domain, String name) {

        if (StringUtils.isEmpty(name)) {
            logger.error("[mysql->getAuthorityIssuerByName] the name is empty.");
            return new ResponseData<AuthorityIssuerInfo>(null, KEY_INVALID);
        }
        try {
            ResponseData<AuthorityIssuerInfo> result = new ResponseData<AuthorityIssuerInfo>();
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_AUTHORITY_ISSUER_BY_NAME, name);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                AuthorityIssuerInfo tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), AuthorityIssuerInfo.class);
                if (StringUtils.isNotBlank(tableData.getWeid())) {
                    result.setResult(tableData);
                    return new ResponseData<>(tableData, ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS);
            }
            result.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
            return result;
        } catch (WeIdBaseException e) {
            logger.error("[mysql->getAuthorityIssuerByName] getAuthorityIssuerByName error.", e);
            return new ResponseData<AuthorityIssuerInfo>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> addAuthorityIssuer(String domain, String weId, String name, String desc, String accValue, String extraStr, String extraInt) {
        if (StringUtils.isEmpty(weId)) {
            logger.error("[mysql->addAuthorityIssuer] the weId is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            Object[] datas = {weId, name, desc, now, now, 0, accValue, extraStr, extraInt};
            return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_AUTHORITY_ISSUER, datas);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->addAuthorityIssuer] addAuthorityIssuer error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> removeAuthorityIssuer(String domain, String weId) {
        if (StringUtils.isEmpty(weId)) {
            logger.error("[mysql->removeAuthorityIssuer] the weId is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_DELETE_AUTHORITY_ISSUER, weId);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->removeAuthorityIssuer] addAuthorityIssuer error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> updateAuthorityIssuer(String domain, String weId, Integer recognize) {
        if (StringUtils.isEmpty(weId)) {
            logger.error("[mysql->updateAuthorityIssuer] the weId is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_AUTHORITY_ISSUER_BY_ADDRESS, weId);
            if (response.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                AuthorityIssuerInfo tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), AuthorityIssuerInfo.class);

                if (StringUtils.isNotBlank(tableData.getName())) {
                    return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_AUTHORITY_ISSUER, new Object[]{now, recognize, weId});
                }
            }
            return new ResponseData<>(FAILED_STATUS, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[mysql->updateAuthorityIssuer] update the AuthorityIssuer error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> getAuthorityIssuerCount(String domain) {
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
            logger.error("[mysql->getAuthorityIssuerCount] get the count of AuthorityIssuer error.", e);
            return new ResponseData<>(0, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> getRecognizedIssuerCount(String domain) {
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Integer> response = new SqlExecutor(sqlDomain)
                    .executeQueryAmounts(SqlExecutor.SQL_QUERY_TOTAL_RECOGNIZED_ISSUER);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                return new ResponseData<>(response.getResult(), ErrorCode.SUCCESS);
            }
            return new ResponseData<>(0, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[mysql->getRecognizedIssuerCount] get the count of recognized authority issuer error.", e);
            return new ResponseData<>(0, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<RoleValue> getRole(String domain, String weId) {

        if (StringUtils.isEmpty(weId)) {
            logger.error("[mysql->getRole] the weId is empty.");
            return new ResponseData<RoleValue>(null, KEY_INVALID);
        }
        try {
            ResponseData<RoleValue> result = new ResponseData<RoleValue>();
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_ROLE, weId);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                RoleValue tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), RoleValue.class);
                if (tableData.getUpdated().before(now)) {
                    result.setResult(tableData);
                    return new ResponseData<>(tableData, ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS);
            }
            result.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
            return result;
        } catch (WeIdBaseException e) {
            logger.error("[mysql->getRole] getRole error.", e);
            return new ResponseData<RoleValue>(null, e.getErrorCode());
        }
    }

    //1 as authority_role, 2 as committee_role, 3 as authority_role & committee_role, 4 as admin_role, 5 as authority_role & admin_role, 6 as committee_role & admin_role, 7 as all role
    @Override
    public ResponseData<Integer> addRole(String domain, String weId, Integer roleValue) {
        if (StringUtils.isEmpty(weId)) {
            logger.error("[mysql->addRole] the weId is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        if (roleValue > 7 || roleValue < 1) {
            logger.error("[mysql->addRole] the roleValue is not between 1 and 7.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            switch (roleValue) {
                case 1 : return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_ROLE, new Object[]{weId, now, now, 1, 0, 0});
                case 2 : return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_ROLE, new Object[]{weId, now, now, 0, 1, 0});
                case 3 : return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_ROLE, new Object[]{weId, now, now, 1, 1, 0});
                case 4 : return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_ROLE, new Object[]{weId, now, now, 0, 0, 1});
                case 5 : return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_ROLE, new Object[]{weId, now, now, 1, 0, 1});
                case 6 : return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_ROLE, new Object[]{weId, now, now, 0, 1, 1});
                case 7 : return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_ROLE, new Object[]{weId, now, now, 1, 1, 1});
                default: return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_ROLE, new Object[]{weId, now, now, 0, 0, 0});
            }
        } catch (WeIdBaseException e) {
            logger.error("[mysql->addRole] addRole error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> updateRole(String domain, String weId, Integer roleValue) {
        if (StringUtils.isEmpty(weId)) {
            logger.error("[mysql->updateRole] the weId is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        if (roleValue > 7 || roleValue < 1) {
            logger.error("[mysql->updateRole] the roleValue is not between 1 and 7.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_ROLE, weId);
            if (response.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                RoleValue tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), RoleValue.class);

                if (tableData.getUpdated().before(now)) {
                    switch (roleValue) {
                        case 1 : return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_ROLE, new Object[]{now, 0, 1, 0, 0, weId});
                        case 2 : return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_ROLE, new Object[]{now, 0, 0, 1, 0, weId});
                        case 3 : return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_ROLE, new Object[]{now, 0, 1, 1, 0, weId});
                        case 4 : return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_ROLE, new Object[]{now, 0, 0, 0, 1, weId});
                        case 5 : return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_ROLE, new Object[]{now, 0, 1, 0, 1, weId});
                        case 6 : return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_ROLE, new Object[]{now, 0, 0, 1, 1, weId});
                        case 7 : return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_ROLE, new Object[]{now, 0, 1, 1, 1, weId});
                        default: return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_ROLE, new Object[]{now, 0, 0, 0, 0, weId});
                    }
                }
            }
            return new ResponseData<>(FAILED_STATUS, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[mysql->updateRole] update the weid error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> addSpecificType(String domain, String typeName, String owner) {
        if (StringUtils.isEmpty(typeName)) {
            logger.error("[mysql->addSpecificType] the typeName is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            Object[] datas = {typeName, now, now, owner};
            return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_SPECIFIC_TYPE, datas);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->addSpecificType] addSpecificType error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> removeSpecificType(String domain, String typeName) {
        if (StringUtils.isEmpty(typeName)) {
            logger.error("[mysql->removeSpecificType] the weId is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_DELETE_SPECIFIC_TYPE, typeName);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->removeSpecificType] removeSpecificType error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<SpecificTypeValue> getSpecificType(String domain, String typeName) {

        if (StringUtils.isEmpty(typeName)) {
            logger.error("[mysql->getSpecificType] the typeName is empty.");
            return new ResponseData<SpecificTypeValue>(null, KEY_INVALID);
        }
        try {
            ResponseData<SpecificTypeValue> result = new ResponseData<SpecificTypeValue>();
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_SPECIFIC_TYPE, typeName);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                SpecificTypeValue tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), SpecificTypeValue.class);
                if (tableData.getUpdated().before(now)) {
                    result.setResult(tableData);
                    return new ResponseData<>(tableData, ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_NOT_EXIST);
            }
            result.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
            return result;
        } catch (WeIdBaseException e) {
            logger.error("[mysql->getSpecificType] getSpecificType error.", e);
            return new ResponseData<SpecificTypeValue>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> updateSpecificTypeFellow(String domain, String typeName, String fellow) {
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            Object[] datas = {fellow, typeName};
            return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_SPECIFIC_TYPE_FELLOW, datas);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->updateSpecificTypeFellow] updateSpecificTypeFellow error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> getIssuerTypeCount(String domain) {
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
            logger.error("[mysql->getIssuerTypeCount] get the count of AuthorityIssuer error.", e);
            return new ResponseData<>(0, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<List<String>> getIssuerTypeList(String domain, Integer first, Integer last) {
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Object[] datas = {first, last - first};
            ResponseData<List<String>> response = new SqlExecutor(sqlDomain)
                    .executeQueryLines(SqlExecutor.SQL_QUERY_SEVERAL_SPECIFIC_TYPE, datas);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                return new ResponseData<>(response.getResult(), ErrorCode.SUCCESS);
            }
            return new ResponseData<>(null, ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        } catch (WeIdBaseException e) {
            logger.error("[mysql->getIssuerTypeList] get the IssuerTypeList error.", e);
            return new ResponseData<>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<EvidenceValue> getEvidenceByHash(String domain, String hash) {

        if (StringUtils.isEmpty(hash)) {
            logger.error("[mysql->getEvidenceByHash] the hash is empty.");
            return new ResponseData<EvidenceValue>(null, KEY_INVALID);
        }
        try {
            ResponseData<EvidenceValue> result = new ResponseData<EvidenceValue>();
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_EVIDENCE_BY_HASH, hash);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                EvidenceValue tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), EvidenceValue.class);
                if (StringUtils.isNotBlank(tableData.getSigners())) {
                    result.setResult(tableData);
                    return new ResponseData<>(tableData, ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_NOT_EXIST);
            }
            result.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
            return result;
        } catch (WeIdBaseException e) {
            logger.error("[mysql->getEvidenceByHash] getEvidenceByHash error.", e);
            return new ResponseData<EvidenceValue>(null, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> addEvidenceByHash(String domain, String hashValue, String signer, String signature, String log, String updated, String revoked, String extraKey, String group_id) {
        if (StringUtils.isEmpty(hashValue)) {
            logger.error("[mysql->addEvidenceByHash] the hashValue is empty.");
            return new ResponseData<Integer>(FAILED_STATUS, KEY_INVALID);
        }
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            if(extraKey.equals(StringUtils.EMPTY)){
                Object[] datas = {hashValue, signer, signature, log, updated, revoked, group_id};
                return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_EVIDENCE_BY_HASH, datas);
            }
            Object[] datas = {hashValue, signer, signature, log, updated, revoked, extraKey, group_id};
            return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_SAVE_EVIDENCE_EXTRAKEY, datas);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->addEvidenceByHash] addEvidenceByHash error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<Integer> addSignatureAndLogs(String domain, String hashValue, String signer, String signature, String log, String updated, String revoked, String extraKey) {
        try {
            SqlDomain sqlDomain = new SqlDomain(domain);
            Date now = sqlDomain.getNow();
            Object[] datas = {signer, signature, log, updated, revoked, extraKey, hashValue};
            return new SqlExecutor(sqlDomain).execute(SqlExecutor.SQL_UPDATE_EVIDENCE, datas);
        } catch (WeIdBaseException e) {
            logger.error("[mysql->addSignatureAndLogs] addSignatureAndLogs error.", e);
            return new ResponseData<Integer>(FAILED_STATUS, e.getErrorCode());
        }
    }

    @Override
    public ResponseData<EvidenceValue> getEvidenceByExtraKey(String domain, String extraKey) {

        if (StringUtils.isEmpty(extraKey)) {
            logger.error("[mysql->getEvidenceByExtraKey] the hash is empty.");
            return new ResponseData<EvidenceValue>(null, KEY_INVALID);
        }
        try {
            ResponseData<EvidenceValue> result = new ResponseData<EvidenceValue>();
            SqlDomain sqlDomain = new SqlDomain(domain);
            ResponseData<Map<String, String>> response = new SqlExecutor(sqlDomain)
                    .executeQuery(SqlExecutor.SQL_QUERY_EVIDENCE_BY_EXTRAKEY, extraKey);
            if (response.getErrorCode() == ErrorCode.SUCCESS.getCode()
                    && response.getResult() != null) {
                EvidenceValue tableData = DataToolUtils.deserialize(
                        DataToolUtils.serialize(response.getResult()), EvidenceValue.class);
                if (StringUtils.isNotBlank(tableData.getSigners())) {
                    result.setResult(tableData);
                    return new ResponseData<>(tableData, ErrorCode.SUCCESS);
                }
                return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_NOT_EXIST);
            }
            result.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
            return result;
        } catch (WeIdBaseException e) {
            logger.error("[mysql->getEvidenceByExtraKey] getEvidenceByExtraKey error.", e);
            return new ResponseData<EvidenceValue>(null, e.getErrorCode());
        }
    }

}
