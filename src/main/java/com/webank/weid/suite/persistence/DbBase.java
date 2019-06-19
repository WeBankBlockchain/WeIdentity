package com.webank.weid.suite.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.PropertyUtils;

/**
 * 数据库操作辅助类.
 * 
 * @author v_wbgyang
 *
 */
public class DbBase {

    private static final Logger logger = LoggerFactory.getLogger(DbBase.class);

    private static BasicDataSource connectionPool = null;
    
    private String driverClassName;
    
    public DbBase(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    /**
     * 初始化连接池配置.
     * 
     * @return 返回配置对象
     */
    private static Properties initProperties(String driverClassName) {
        // 连接URL
        String dbUrl = PropertyUtils.getProperty(DataDriverConstant.JDBC_URL);
        // 用户名
        String userName = PropertyUtils.getProperty(DataDriverConstant.JDBC_USER_NAME);
        // 密码
        String passWord = PropertyUtils.getProperty(DataDriverConstant.JDBC_USER_PASSWORD);
        // 最大活跃链接
        String maxActive = PropertyUtils.getProperty(DataDriverConstant.JDBC_MAX_ACTIVE);
        // 最小空闲链接
        String minIdle = PropertyUtils.getProperty(DataDriverConstant.JDBC_MIN_IDLE);
        // 最大空闲链接
        String maxIdle = PropertyUtils.getProperty(DataDriverConstant.JDBC_MAX_IDLE);
        // 获取链接的最大等待时间
        String maxWait = PropertyUtils.getProperty(DataDriverConstant.JDBC_MAX_WAIT);
        // 轮询检查连接间隔时间
        String timeBetweenErm = PropertyUtils.getProperty(DataDriverConstant.JDBC_TIME_BETWEEN_ERM);
        // 单次检查连接数据
        String numTestsPerEr = PropertyUtils.getProperty(DataDriverConstant.JDBC_NUM_TEST_PER_ER);
        // 最小空闲时间的连接将被检查
        String minEtm = PropertyUtils.getProperty(DataDriverConstant.JDBC_MIN_EITM);
        
        // 是否自动回收超时连接
        String rmAbandoned = DataDriverConstant.JDBC_REMOVE_ABANDONED;
        // 是否自动回收超时连接的超时时间
        String rmAbandonedTimeout = DataDriverConstant.JDBC_REMOVE_ABANDONED_TIMEOUT;
        // 是否测试借出连接
        String testOnBorrow = DataDriverConstant.JDBC_TEST_ON_BORROW;
        // 是否开启定时检查
        String testOnWhile = DataDriverConstant.JDBC_TEST_ON_WHILE;
        // 检查连接sql
        String validationQuery = DataDriverConstant.JDBC_VALIDATION_QUERY;

        Properties p = new Properties();
        p.setProperty(DataDriverConstant.JDBC_MYSQL_DRIVER_CLASS_NAME, driverClassName);
        p.setProperty(DataDriverConstant.POOL_URL, dbUrl);
        p.setProperty(DataDriverConstant.POOL_USERNAME, userName);
        p.setProperty(DataDriverConstant.POOL_PASSWORD, passWord);
        p.setProperty(DataDriverConstant.POOL_MAX_ACTIVE, maxActive);
        p.setProperty(DataDriverConstant.POOL_MIN_IDLE, minIdle);
        p.setProperty(DataDriverConstant.POOL_MAX_IDLE, maxIdle);
        p.setProperty(DataDriverConstant.POOL_MAX_WAIT, maxWait);
        p.setProperty(DataDriverConstant.POOL_MAX_REMOVE_ABANDONED, rmAbandoned);
        p.setProperty(DataDriverConstant.POOL_MAX_REMOVE_ABANDONED_TIMEOUT, rmAbandonedTimeout);
        p.setProperty(DataDriverConstant.POOL_TEST_ON_BORROW, testOnBorrow);  
        p.setProperty(DataDriverConstant.POOL_TEST_ON_WHILE, testOnWhile);
        p.setProperty(DataDriverConstant.POOL_TIME_BETWEEN_ERM, timeBetweenErm);
        p.setProperty(DataDriverConstant.POOL_NUM_TEST_PER_ER, numTestsPerEr);
        p.setProperty(DataDriverConstant.POOL_VALIDATION_QUERY, validationQuery);
        p.setProperty(DataDriverConstant.POOL_MIN_EITM, minEtm);
        return p;
    }

    /**
     * 初始化连接池.
     * 
     * @throws Exception 初始化异常
     */
    private void init(String driverClassName) throws Exception {
        if (connectionPool == null) {
            synchronized (this.getClass()) {
                if (connectionPool == null) {
                    Properties properties = initProperties(driverClassName);
                    connectionPool = 
                        (BasicDataSource) BasicDataSourceFactory.createDataSource(properties);
                }
            }
        }
    }

    /**
     * 从连接池中获取连接.
     * 
     * @return 返回连接对象
     */
    public Connection getConnection() {
        Connection conn = null;
        try {
            if (connectionPool == null) {
                init(driverClassName);
            }
            if (connectionPool != null) {
                conn = connectionPool.getConnection();
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            logger.error("get connection error, please check the log.", e);
        }
        return conn;
    }
    
    /**
     * 查询操作.
     * 
     * @param sql 需要被执行的SQL语句
     * @param data 占位符所需要的数据
     * @return 返回查询出来的单个数据
     */
    protected ResponseData<String> executeQuery(String sql, String... data) {
        ResponseData<String> result = new ResponseData<String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            if (conn == null) {
                return 
                    new ResponseData<String>(
                        StringUtils.EMPTY, 
                        ErrorCode.SQL_GET_CONNECTION_ERROR
                    );
            }
            ps = conn.prepareStatement(sql);
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
            logger.error("Query data from mysql with exception", e);
            result.setErrorCode(ErrorCode.SQL_EXECUTE_FAILED);
            result.setResult(StringUtils.EMPTY);
        } finally {
            close(conn, ps, rs); 
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
    protected ResponseData<Integer> execute(String sql, String... data) {
        ResponseData<Integer> result = new ResponseData<Integer>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            if (conn == null) {
                return 
                    new ResponseData<Integer>(
                        DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                        ErrorCode.SQL_GET_CONNECTION_ERROR
                    );  
            }
            ps = conn.prepareStatement(sql);
            for (int i = 0; data != null && i < data.length; i++) {
                ps.setString(i + 1, data[i]);
            }
            int rs = ps.executeUpdate();
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(rs);
        } catch (SQLException e) {
            logger.error("Update data into mysql with exception", e);
            result.setErrorCode(ErrorCode.SQL_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.SQL_EXECUTE_FAILED_STATUS);
        } finally {
            close(conn, ps); 
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
    protected ResponseData<Integer> batchSave(String sql, List<List<String>> dataList) {
        ResponseData<Integer> result = new ResponseData<Integer>();
        Connection conn = null;
        PreparedStatement psts = null;
        try {
            conn = getConnection();
            if (conn == null) {
                return 
                    new ResponseData<Integer>(
                        DataDriverConstant.SQL_EXECUTE_FAILED_STATUS, 
                        ErrorCode.SQL_GET_CONNECTION_ERROR
                    );                
            }
            conn.setAutoCommit(false);
            psts = conn.prepareStatement(sql);
            int count = 0;
            List<String> values = dataList.get(dataList.size() - 1);
            for (int i = 0; i < values.size(); i++) {
                for (int j = 0; j < dataList.size(); j++) {
                    psts.setString(j + 1, dataList.get(j).get(i));
                }
                psts.addBatch();
                // 每500提交一次
                if (i % 500 == 0) {
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
            logger.error("Batch save data to mysql with exception", e);
            result.setErrorCode(ErrorCode.SQL_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.SQL_EXECUTE_FAILED_STATUS);
        } finally {
            close(conn, psts); 
        }
        return result;
    }
    
    /**
     * 关闭数据库连接资源.
     * 
     * @param conn 数据库连接对象
     * @param pstmt 预编译SQL对象
     * @param rs 结果集对象
     */
    public void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        closeResources(rs);
        closeResources(pstmt);
        closeResources(conn);
    }

    /**
     * 关闭数据库连接资源.
     * 
     * @param conn 数据库连接对象
     * @param pstmt 预编译SQL对象
     */
    public void close(Connection conn, PreparedStatement pstmt) {
        close(conn, pstmt, null);
    }

    /**
     * 关闭数据库连接资源.
     * 
     * @param conn 数据库连接对象
     */
    public void close(Connection conn) {
        close(conn, null, null);
    }

    private void closeResources(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                logger.error("close Connection error.", e);
            }
        }
    }

    private void closeResources(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                logger.error("close PreparedStatement error.", e);
            }
        }
    }

    private void closeResources(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("close ResultSet error.", e);
            }
        }
    }
}
