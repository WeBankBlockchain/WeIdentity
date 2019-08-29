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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
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
 *
 */
public class SqlExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SqlExecutor.class);
    
    /**
     * 表占位符.
     */
    public static final String TABLE_CHAR = "$1";
    
    /**
     * sql for query.
     */
    public static final String SQL_QUERY = "select id,data,created from $1 where id =?";

    /**
     * sql for save.
     */
    public static final String SQL_SAVE = "insert into $1(id, data) values(?,?)";

    /**
     * sql for update.
     */
    public static final String SQL_UPDATE = "update $1 set updated = ?, data = ? "
        + "where id = ?";

    /**
     * sql for delete.
     */
    public static final String SQL_DELETE = "delete from $1 where id = ?";
    
    /**
     * default table name.
     */
    private static final String DEFAULT_TABLE = "sdk_all_data";
    
    /**
     * 表的默认前缀.
     */
    private static final String DEFAULT_TABLE_PREFIX = "weidentity_";
    
    /**
     * domain 分隔符.
     */
    private static final String SPLIT_CHAR = ":";
    
    /**
     * 批次提交个数.
     */
    private static final int BATCH_COMMIT_COUNT = 200;
    
    /**
     * 库级别domain.
     */
    private String baseDomain;
    
    /**
     * 表级别domain.
     */
    private String tableDomain;
    
    /**
     * tableDomain 与 tableName的映射.
     */
    private static final Map<String, String> TABLE_CACHE = new ConcurrentHashMap<String, String>();
    
    /**
     * 根据domain创建SQL执行器.
     * 
     * @param domain the domain
     */
    public SqlExecutor(String domain) {
        //解析domain
        resolveDomain(domain);
    }
    
    /**
     * 查询操作.
     * 
     * @param sql 需要被执行的SQL语句
     * @param data 占位符所需要的数据
     * @return 返回查询出来的单个数据
     */
    public ResponseData<String> executeQuery(String sql, String... data) {
        ResponseData<String> result = new ResponseData<String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionPool.getConnection(baseDomain);
            if (conn == null) {
                return 
                    new ResponseData<String>(
                        StringUtils.EMPTY, 
                        ErrorCode.SQL_GET_CONNECTION_ERROR
                    );
            }
            ps = conn.prepareStatement(buildExecuteSql(sql));
            for (int i = 0; i < data.length; i++) {
                ps.setString(i + 1, data[i]);
            }
            
            rs = ps.executeQuery();
            String value = null;
            if (rs.next()) {
                value = rs.getString(DataDriverConstant.SQL_COLUMN_DATA);
            }
            rs.close();
            ps.close();
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(value);
        } catch (SQLException e) {
            logger.error("Query data from {{}} with exception", baseDomain, e);
            result.setErrorCode(ErrorCode.SQL_EXECUTE_FAILED);
            result.setResult(StringUtils.EMPTY);
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
            conn = ConnectionPool.getConnection(baseDomain);
            if (conn == null) {
                return 
                    new ResponseData<Integer>(
                        DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                        ErrorCode.SQL_GET_CONNECTION_ERROR
                    );  
            }
            ps = conn.prepareStatement(buildExecuteSql(sql));
            for (int i = 0; data != null && i < data.length; i++) {
                if (data[i] instanceof Date) {
                    Date date = (Date)data[i];
                    ps.setTimestamp(i + 1, new Timestamp(date.getTime()));
                    continue;
                }
                ps.setObject(i + 1, data[i]);
            }
            int rs = ps.executeUpdate();
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(rs);
        } catch (SQLException e) {
            logger.error("Update data into {{}} with exception", baseDomain, e);
            result.setErrorCode(ErrorCode.SQL_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.SQL_EXECUTE_FAILED_STATUS);
        } finally {
            ConnectionPool.close(conn, ps); 
        }
        return result;
    }
    
    /**
     * 新增方法，新增之前先检查是否存在表，不存在则创建表.
     * 
     * @param sql 需要被执行的SQL语句
     * @param checkTableSql 检查表是否存在的SQL
     * @param createTableSql 需要被执行的SQL语句
     * @param data 占位符所需要的数据
     * @return 返回执行受影响的行数
     */
    public ResponseData<Integer> executeSave(
        String sql,
        String checkTableSql,
        String createTableSql,
        String... data) {
        resolveTableDomain(checkTableSql, createTableSql);
        return execute(sql, Arrays.asList(data).toArray());
    }
    
    /**
     * 批量新增的通用语句.
     * 
     * @param sql 需要被执行的数据
     * @param dataList 占位符所需要的数据
     * @return 返回受影响的行数
     */
    public ResponseData<Integer> batchSave(String sql, List<List<String>> dataList) {
        ResponseData<Integer> result = new ResponseData<Integer>();
        Connection conn = null;
        PreparedStatement psts = null;
        try {
            List<String> values = dataList.get(dataList.size() - 1);
            for (List<String> list : dataList) {
                if (CollectionUtils.isEmpty(list) || list.size() != values.size()) {
                    return 
                        new ResponseData<Integer>(
                            DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                            ErrorCode.PRESISTENCE_BATCH_SAVE_DATA_MISMATCH
                        );  
                }
            }
            conn = ConnectionPool.getConnection(baseDomain);
            if (conn == null) {
                return 
                    new ResponseData<Integer>(
                        DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                        ErrorCode.SQL_GET_CONNECTION_ERROR
                    );                
            }
            conn.setAutoCommit(false);
            psts = conn.prepareStatement(buildExecuteSql(sql));
            int count = 0;
            for (int i = 0; i < values.size(); i++) {
                for (int j = 0; j < dataList.size(); j++) {
                    psts.setString(j + 1, dataList.get(j).get(i));
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
            logger.error("Batch save data to {{}} with exception", baseDomain, e);
            result.setErrorCode(ErrorCode.SQL_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.SQL_EXECUTE_FAILED_STATUS);
        } finally {
            ConnectionPool.close(conn, psts); 
        }
        return result;
    }
    
    /**
     * 批量新增的通用方法. 先检查表是否存在，不存在则动态创建表.
     * 
     * @param sql 需要被执行的数据
     * @param checkTableSql 检查表是否存在的SQL
     * @param createTableSql 需要被执行的SQL语句
     * @param dataList 占位符所需要的数据
     * @return 返回受影响的行数
     */
    public ResponseData<Integer> batchSave(
        String sql,
        String checkTableSql,
        String createTableSql,
        List<List<String>> dataList) {
        resolveTableDomain(checkTableSql, createTableSql);
        return batchSave(sql, dataList);
    }
    
    private void resolveDomain(String domain) {
        if (StringUtils.isBlank(domain)) {
            this.baseDomain = ConnectionPool.getFirstDataSourceName();
            this.tableDomain = DEFAULT_TABLE;
        } else if (domain.contains(SPLIT_CHAR) && domain.split(SPLIT_CHAR).length == 2) {
            String[] domains = domain.split(SPLIT_CHAR);
            this.baseDomain = domains[0];
            this.tableDomain = domains[1];
            if (!ConnectionPool.checkDataSourceName(this.baseDomain)) {
                throw new WeIdBaseException(ErrorCode.PRESISTENCE_DOMAIN_INVALID);
            }
        } else {
            throw new WeIdBaseException(ErrorCode.PRESISTENCE_DOMAIN_ILLEGAL);
        }
    }
    
    private void resolveTableDomain(String checkTableSql, String createTableSql) {
        String tableName = TABLE_CACHE.get(tableDomain);
        synchronized (TABLE_CACHE) {
            //说明本地没有此tableDomain
            if (StringUtils.isBlank(tableName)) {
                tableName = getTableName();
                //检查数据库中是否存在此表
                ResponseData<String>  result =  this.executeQuery(checkTableSql);
                //如果数据库中存在此表
                if (tableName.equals(result.getResult())) {
                    //本地缓存记录此表
                    TABLE_CACHE.put(tableDomain, tableName);
                    return;
                }
                //动态创建此表
                ResponseData<Integer> createRes = this.execute(createTableSql);
                //创建失败
                if (createRes.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                    throw new WeIdBaseException(ErrorCode.PRESISTENCE_DOMAIN_INVALID);
                }
                //再查询一次，确认是否创建成功
                result =  this.executeQuery(checkTableSql);
                //如果不相等 则表示创建失败
                if (!tableName.equals(result.getResult())) {
                    throw new WeIdBaseException(ErrorCode.PRESISTENCE_DOMAIN_INVALID);
                }
                //本地缓存记录此表
                TABLE_CACHE.put(tableDomain, tableName);
            }
        }
    }
    
    private String buildExecuteSql(String sql) {
        return new StringBuffer(sql).toString().replace(TABLE_CHAR, getTableName());
    }
    
    private String getTableName() {
        return new StringBuffer(DEFAULT_TABLE_PREFIX).append(tableDomain).toString();
    }
}
