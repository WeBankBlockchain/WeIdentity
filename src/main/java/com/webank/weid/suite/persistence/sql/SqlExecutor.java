/*
 *       Copyright© (2018) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.suite.persistence.sql;

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
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.response.ResponseData;

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
     * sql for save.
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
     * sql for save.
     */
    public static final String SQL_SAVE_TRANSACTION =
        "insert into weidentity_offline_transaction_info"
            + "(request_id, transaction_method, transaction_args, transaction_timestamp, extra, batch) "
            + "values(?,?,?,?,?,?)";
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
                        ErrorCode.SQL_GET_CONNECTION_ERROR
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
            result.setErrorCode(ErrorCode.SQL_EXECUTE_FAILED);
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
                        ErrorCode.SQL_GET_CONNECTION_ERROR
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
            result.setErrorCode(ErrorCode.SQL_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.SQL_EXECUTE_FAILED_STATUS);
        } finally {
            ConnectionPool.close(conn, ps);
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
    public ResponseData<Integer> batchSave(String sql, List<List<Object>> dataList) {
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
                            ErrorCode.PRESISTENCE_BATCH_SAVE_DATA_MISMATCH
                        );
                }
            }
            conn = ConnectionPool.getConnection(sqlDomain.getBaseDomain());
            if (conn == null) {
                return
                    new ResponseData<Integer>(
                        DataDriverConstant.SQL_EXECUTE_FAILED_STATUS,
                        ErrorCode.SQL_GET_CONNECTION_ERROR
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
            logger.error("Batch save data to {{}} with exception", sqlDomain.getBaseDomain(), e);
            result.setErrorCode(ErrorCode.SQL_EXECUTE_FAILED);
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

    private boolean initLocalTable(String checkTableSql) {
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
        String tableName = sqlDomain.getTableName();
        Map<String, String> result = resultRes.getResult();
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

    private String buildExecuteSql(String exeSql, Connection conn) throws SQLException {
        exeSql = exeSql.replace(TABLE_CHAR, sqlDomain.getTableName());
        exeSql = exeSql.replace(DATABASE_CHAR, conn.getCatalog());
        return exeSql;
    }
}
