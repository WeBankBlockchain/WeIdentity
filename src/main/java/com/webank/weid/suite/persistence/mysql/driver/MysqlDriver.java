

package com.webank.weid.suite.persistence.mysql.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.request.TransactionArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.persistence.inf.Persistence;
import com.webank.weid.suite.persistence.DefaultValue;
import com.webank.weid.suite.persistence.mysql.SqlDomain;
import com.webank.weid.suite.persistence.mysql.SqlExecutor;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.PropertyUtils;

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

    private static final Integer FAILED_STATUS = DataDriverConstant.SQL_EXECUTE_FAILED_STATUS;

    private static final ErrorCode KEY_INVALID = ErrorCode.PRESISTENCE_DATA_KEY_INVALID;

    private static Boolean isinit = false;

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
        String dataKey = DataToolUtils.getHash(id);
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
        String dataKey = DataToolUtils.getHash(id);
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
                idHashList.add(DataToolUtils.getHash(id));
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
        String dataKey = DataToolUtils.getHash(id);
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
        String dataKey = DataToolUtils.getHash(id);
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
        for (String domainKey : domainKeySet) {
            SqlExecutor sqlExecutor = new SqlExecutor(new SqlDomain(domainKey));
            sqlExecutor.resolveTableDomain(CHECK_TABLE_SQL, CREATE_TABLE_SQL);
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
                && key.startsWith(SqlDomain.PREFIX)) {
                domainKeySet.add(key);
            }
        }
        return domainKeySet;
    }

    /* (non-Javadoc)
     * @see com.webank.weid.suite.api.persistence.inf.Persistence#addOrUpdate(java.lang.String,
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
     * @see com.webank.weid.suite.api.persistence.inf.Persistence#addTransaction(
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
}
