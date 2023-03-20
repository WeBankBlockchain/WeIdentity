

package com.webank.weid.suite.persistence.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.blockchain.protocol.response.ResponseData;


/**
 * 数据库操作辅助类.
 *
 * @author v_wbgyang
 */
public class SqlExecutor {

    /**
     * 表占位符.
     */
    public static final String TABLE_CHAR = "$1";
    /**
     * 库占位符.
     */
    public static final String DATABASE_CHAR = "$2";
    /**
     * sql for query.
     */
    public static final String SQL_QUERY = "select id,data,created,expire from $1 where id =?";
    /**
     * sql for add.
     */
    public static final String SQL_SAVE = "insert into $1(id, data, expire, created, updated) "
            + "values(?,?,?,?,?)";
    /**
     * sql for update.
     */
    public static final String SQL_UPDATE = "update $1 set updated = ?, data = ?, expire = ? "
            + "where id = ?";
    /**
     * sql for delete.
     */
    public static final String SQL_DELETE = "delete from $1 where id = ?";
    /**
     * sql for add transaction.
     */
    public static final String SQL_SAVE_TRANSACTION =
        "insert into weidentity_offline_transaction_info"
        + "(request_id, transaction_method, transaction_args, transaction_timestamp, extra, batch)"
        + " values(?,?,?,?,?,?)";
    /**
     * sql for query total lines of data.
     */
    public static final String SQL_QUERY_TOTAL_LINE = "select COUNT(*) from $1";
    /**
     * sql for query several weId from firstIndex.
     */
    public static final String SQL_QUERY_SEVERAL_WEID = "select weid from $1 LIMIT ?, ?";
    /**
     * sql for insert weIdDocument and metaDta.
     */
    public static final String SQL_SAVE_WEID = "insert into $1 (weid,created,updated,version,deactivated,documentSchema) values(?,?,?,?,?,?)";
    /**
     * sql for query weIdDocument and metaDta.
     */
    public static final String SQL_QUERY_WEID = "select weid,created,updated,version,deactivated,documentSchema from $1 where weid=?";
    /**
     * sql for update weIdDocument and metaDta.
     */
    public static final String SQL_UPDATE_WEID = "update $1 set updated = ?, version = ?, deactivated = ?, documentSchema = ? where weid = ?";
    /**
     * sql for query cpt.
     */
    public static final String SQL_QUERY_CPT = "select cpt_id,created,updated,cpt_version,publisher,description,cpt_schema,cpt_signature from $1 where cpt_id =?";
    /**
     * sql for insert cpt.
     */
    public static final String SQL_SAVE_CPT = "insert into $1 (cptId,created,updated,cptVersion,publisher,description,cptSchema,cptSignature) values(?,?,?,?,?,?,?,?)";
    /**
     * sql for update cpt.
     */
    public static final String SQL_UPDATE_CPT = "update $1 set updated = ?, version = ?, deactivated = ?, documentSchema = ? where weid = ?";
    private static final Logger logger = LoggerFactory.getLogger(SqlExecutor.class);
    /**
     * 批次提交个数.
     */
    private static final int BATCH_COMMIT_COUNT = 200;
    /**
     * tableDomain 与 tableName的映射.
     */
    private static final Map<String, String> TABLE_CACHE = new ConcurrentHashMap<String, String>();

    /**
     * the sql domain.
     */
    private SqlDomain sqlDomain;

    /**
     * 根据domain创建SQL执行器.
     *
     * @param sqlDomain the Sqldomain
     */
    public SqlExecutor(SqlDomain sqlDomain) {
        if (sqlDomain != null) {
            this.sqlDomain = sqlDomain;
        } else {
            this.sqlDomain = new SqlDomain();
        }
    }

    /**
     * 根据数据源名称构建SQL执行器.
     * 
     * @param dataSourceName 数据源名称
     */
    public SqlExecutor(String dataSourceName) {
        this.sqlDomain = new SqlDomain();
        this.sqlDomain.setBaseDomain(dataSourceName);
    }
    
    /**
     * 查询操作.
     *
     * @param sql 需要被执行的SQL语句
     * @param data 占位符所需要的数据
     * @return 返回查询出来的单个数据
     */
    public ResponseData<Map<String, String>> executeQuery(String sql, Object... data) {
        ResponseData<Map<String, String>> result = new ResponseData<Map<String, String>>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionPool.getConnection(sqlDomain.getBaseDomain());
            if (conn == null) {
                return
                    new ResponseData<Map<String, String>>(
                        null,
                        ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR
                    );
            }

            ps = conn.prepareStatement(buildExecuteSql(sql, conn));
            for (int i = 0; i < data.length; i++) {
                ps.setObject(i + 1, data[i]);
            }

            rs = ps.executeQuery();
            Map<String, String> dataMap = null;
            if (rs.next()) {
                dataMap = new HashMap<String, String>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    int type = metaData.getColumnType(i);
                    if (type == Types.TIMESTAMP) {
                        Timestamp timestamp = rs.getTimestamp(columnLabel);
                        if (timestamp != null) {
                            dataMap.put(columnLabel, String.valueOf(timestamp.getTime()));
                        }
                    } else {
                        dataMap.put(columnLabel, rs.getString(columnLabel));
                    }
                }
            }
            rs.close();
            ps.close();
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(dataMap);
        } catch (SQLException e) {
            logger.error("Query data from {{}} with exception", sqlDomain.getBaseDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        } finally {
            ConnectionPool.close(conn, ps, rs);
        }
        return result;
    }

    /**
     * 增删改的通用执行方法.
     *
     * @param sql 需要被执行的SQL语句
     * @param data 占位符所需要的数据
     * @return 返回执行受影响的行数
     */
    public ResponseData<Integer> execute(String sql, Object... data) {
        ResponseData<Integer> result = new ResponseData<Integer>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionPool.getConnection(sqlDomain.getBaseDomain());
            if (conn == null) {
                return
                    new ResponseData<Integer>(
                        DataDriverConstant.SQL_EXECUTE_FAILED_STATUS,
                        ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR
                    );
            }
            ps = conn.prepareStatement(buildExecuteSql(sql, conn));
            for (int i = 0; data != null && i < data.length; i++) {
                if (data[i] instanceof Date) {
                    Date date = (Date) data[i];
                    ps.setTimestamp(i + 1, new Timestamp(date.getTime()));
                    continue;
                }
                ps.setObject(i + 1, data[i]);
            }
            int rs = ps.executeUpdate();
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(rs);
        } catch (SQLException e) {
            logger.error("Update data into {{}} with exception", sqlDomain.getBaseDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.SQL_EXECUTE_FAILED_STATUS);
        } finally {
            ConnectionPool.close(conn, ps);
        }
        return result;
    }

    /**
     * 查询操作.
     *
     * @param sql 需要被执行的SQL语句
     * @param data 占位符所需要的数据
     * @return 返回查询出来的多行数据，每行仅查询一列，仅用于按序号查询多个weid
     */
    public ResponseData<List<String>> executeQueryLines(String sql, Object... data) {
        ResponseData<List<String>> result = new ResponseData<List<String>>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionPool.getConnection(sqlDomain.getBaseDomain());
            if (conn == null) {
                return
                        new ResponseData<List<String>>(
                                null,
                                ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR
                        );
            }

            ps = conn.prepareStatement(buildExecuteSql(sql, conn));
            for (int i = 0; i < data.length; i++) {
                ps.setObject(i + 1, data[i]);
            }

            rs = ps.executeQuery();
            List<String> dataList = null;
            if (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                String columnLabel = metaData.getColumnLabel(1);
                dataList.add(rs.getString(columnLabel));
                /*int type = metaData.getColumnType(i);
                if (type == Types.TIMESTAMP) {
                    Timestamp timestamp = rs.getTimestamp(columnLabel);
                    if (timestamp != null) {
                        dataMap.put(columnLabel, String.valueOf(timestamp.getTime()));
                    }
                } else {
                    dataMap.put(columnLabel, rs.getString(columnLabel));
                }*/
            }
            rs.close();
            ps.close();
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(dataList);
        } catch (SQLException e) {
            logger.error("Query data from {{}} with exception", sqlDomain.getBaseDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        } finally {
            ConnectionPool.close(conn, ps, rs);
        }
        return result;
    }

    /**
     * 查询操作.
     *
     * @param sql 需要被执行的SQL语句
     * @return 返回查询数据总行数
     */
    public ResponseData<Integer> executeQueryAmounts(String sql) {
        ResponseData<Integer> result = new ResponseData<Integer>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionPool.getConnection(sqlDomain.getBaseDomain());
            if (conn == null) {
                return
                        new ResponseData<Integer>(
                                0,
                                ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR
                        );
            }
            ps = conn.prepareStatement(buildExecuteSql(sql, conn));
            rs = ps.executeQuery();
            int rowCount = 0;
            if(rs.next()) {
                rowCount = rs.getInt("totalCount ");
            }
            rs.close();
            ps.close();
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(rowCount);
        } catch (SQLException e) {
            logger.error("Query data from {{}} with exception", sqlDomain.getBaseDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
        } finally {
            ConnectionPool.close(conn, ps, rs);
        }
        return result;
    }

    /**
     * 批量新增的通用语句.
     *
     * @param sql 需要被执行的数据
     * @param dataList 占位符所需要的数据
     * @return 返回受影响的行数
     */
    public ResponseData<Integer> batchAdd(String sql, List<List<Object>> dataList) {
        ResponseData<Integer> result = new ResponseData<Integer>();
        Connection conn = null;
        PreparedStatement psts = null;
        try {
            List<Object> values = dataList.get(dataList.size() - 1);
            for (List<Object> list : dataList) {
                if (CollectionUtils.isEmpty(list) || list.size() != values.size()) {
                    return
                        new ResponseData<Integer>(
                            DataDriverConstant.SQL_EXECUTE_FAILED_STATUS,
                            ErrorCode.PERSISTENCE_BATCH_ADD_DATA_MISMATCH
                        );
                }
            }
            conn = ConnectionPool.getConnection(sqlDomain.getBaseDomain());
            if (conn == null) {
                return
                    new ResponseData<Integer>(
                        DataDriverConstant.SQL_EXECUTE_FAILED_STATUS,
                        ErrorCode.PERSISTENCE_GET_CONNECTION_ERROR
                    );
            }
            conn.setAutoCommit(false);
            psts = conn.prepareStatement(buildExecuteSql(sql, conn));
            int count = 0;
            for (int i = 0; i < values.size(); i++) {
                for (int j = 0; j < dataList.size(); j++) {
                    psts.setObject(j + 1, dataList.get(j).get(i));
                }
                psts.addBatch();
                // 每500提交一次
                if (i % BATCH_COMMIT_COUNT == 0) {
                    int[] counts = psts.executeBatch();
                    conn.commit();
                    psts.clearBatch();
                    for (int j : counts) {
                        count += j;
                    }
                }
            }
            int[] counts = psts.executeBatch();
            conn.commit();
            psts.clearBatch();
            for (int j : counts) {
                count += j;
            }
            result.setResult(count);
        } catch (SQLException e) {
            logger.error("Batch add data to {{}} with exception", sqlDomain.getBaseDomain(), e);
            result.setErrorCode(ErrorCode.PERSISTENCE_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.SQL_EXECUTE_FAILED_STATUS);
        } finally {
            ConnectionPool.close(conn, psts);
        }
        return result;
    }

    /**
     * 检查表是否存在，如果不存在则创建表.
     *
     * @param checkTableSql 检查表是存在的sql语句
     * @param createTableSql 创建表的sql语句
     */
    public void resolveTableDomain(String checkTableSql, String createTableSql) {
        synchronized (TABLE_CACHE) {
            String tableName = TABLE_CACHE.get(sqlDomain.getKey());
            //说明本地没有此tableDomain
            if (StringUtils.isBlank(tableName)) {
                if (this.initLocalTable(checkTableSql)) {
                    return;
                }
                this.createTable(createTableSql);
                if (!this.initLocalTable(checkTableSql)) {
                    logger.error(
                        "[resolveTableDomain] the domain {{}:{}} is invalid.",
                        sqlDomain.getKey(),
                        sqlDomain.getValue()
                    );
                    throw new WeIdBaseException(ErrorCode.PRESISTENCE_DOMAIN_INVALID);
                }
            }
        }
    }
    
    // 检查表是否存在，如果存在则返回表名
    private Map<String, String> checkTable(String checkTableSql) {
        //检查数据库中是否存在此表
        ResponseData<Map<String, String>> resultRes = this.executeQuery(checkTableSql);
        if (resultRes.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            logger.error(
                "[initLocalTable] execute query table name fail, code:{}, message:{}.",
                resultRes.getErrorCode(),
                resultRes.getErrorMessage()
            );
            throw new WeIdBaseException(
                ErrorCode.getTypeByErrorCode(resultRes.getErrorCode()));
        }
        Map<String, String> result = resultRes.getResult();
        //如果数据库中存在此表
        if (result != null) {
            return result;
        }
        return null;
    }
    
    private boolean initLocalTable(String checkTableSql) {
        Map<String, String> result = checkTable(checkTableSql);
        String tableName = sqlDomain.getTableName();
        //如果数据库中存在此表
        if (result != null
            && tableName.equalsIgnoreCase(result.get(DataDriverConstant.SQL_COLUMN_DATA))) {
            //本地缓存记录此表
            TABLE_CACHE.put(sqlDomain.getKey(), tableName);
            logger.info(
                "[initLocalTable] the domain {{}:{}} is init success.",
                sqlDomain.getKey(),
                sqlDomain.getValue()
            );
            return true;
        }
        return false;
    }

    private void createTable(String createTableSql) {
        //动态创建此表
        ResponseData<Integer> createRes = this.execute(createTableSql);
        //创建失败
        if (createRes.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            logger.error(
                "[createTable] execute create table fail, code:{}, message:{}.",
                createRes.getErrorCode(),
                createRes.getErrorMessage()
            );
            throw new WeIdBaseException(
                ErrorCode.getTypeByErrorCode(createRes.getErrorCode()));
        }
    }
    
    /**
     * 此方法用于非Domain体系创建自定义表使用.
     * 
     * @param checkTableSql 检查表是否存在的SQL语句
     * @param createTableSql 创建表的SQL语句
     * @return 返回表是否创建成功
     */
    public boolean createTable(String checkTableSql, String createTableSql) {
        // 先检查表
        Map<String, String> result = checkTable(checkTableSql);
        if (result == null) {
            // 说明表不存在,创建表
            this.createTable(createTableSql);
            // 再确认一次是否存在
            result = checkTable(checkTableSql);
            if (result == null) {
                return false;
            }
        }
        return true;
    }

    private String buildExecuteSql(String exeSql, Connection conn) throws SQLException {
        exeSql = exeSql.replace(TABLE_CHAR, sqlDomain.getTableName());
        exeSql = exeSql.replace(DATABASE_CHAR, conn.getCatalog());
        return exeSql;
    }
}
