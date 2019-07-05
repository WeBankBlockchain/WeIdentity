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
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.util.PropertyUtils;

public class ConnectionPool {
    
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

    private static  Map<String, BasicDataSource> connectionPoolMap = null;
    
    private static final LinkedList<String> SOURCE_NAME_LIST = new LinkedList<String>();
    
    static {
        String dataSourceNameStr = 
            PropertyUtils.getProperty(DataDriverConstant.JDBC_DATASOURCE_NAME);
        String[] dataSourceNames = dataSourceNameStr.split(",");
        for (String string : dataSourceNames) {
            SOURCE_NAME_LIST.add(string);
        }
    }
    
    private ConnectionPool() {
        
    }
    
    /**
     * 初始化连接池配置.
     * 
     * @return 返回配置对象
     */
    private static Properties initProperties(String dsNamePrefix) {
        // 连接URL
        String dbUrl = PropertyUtils.getProperty(dsNamePrefix + DataDriverConstant.JDBC_URL);
        // 用户名
        String userName = 
            PropertyUtils.getProperty(dsNamePrefix + DataDriverConstant.JDBC_USER_NAME);
        // 密码
        String passWord = 
            PropertyUtils.getProperty(dsNamePrefix + DataDriverConstant.JDBC_USER_PASSWORD);
        // 最大活跃链接
        String maxActive = 
            PropertyUtils.getProperty(dsNamePrefix + DataDriverConstant.JDBC_MAX_ACTIVE);
        // 最小空闲链接
        String minIdle = 
            PropertyUtils.getProperty(dsNamePrefix + DataDriverConstant.JDBC_MIN_IDLE);
        // 最大空闲链接
        String maxIdle = 
            PropertyUtils.getProperty(dsNamePrefix + DataDriverConstant.JDBC_MAX_IDLE);
        // 获取链接的最大等待时间
        String maxWait = 
            PropertyUtils.getProperty(dsNamePrefix + DataDriverConstant.JDBC_MAX_WAIT);
        // 轮询检查连接间隔时间
        String timeBetweenErm = 
            PropertyUtils.getProperty(dsNamePrefix + DataDriverConstant.JDBC_TIME_BETWEEN_ERM);
        // 单次检查连接数据
        String numTestsPerEr = 
            PropertyUtils.getProperty(dsNamePrefix + DataDriverConstant.JDBC_NUM_TEST_PER_ER);
        // 最小空闲时间的连接将被检查
        String minEtm = PropertyUtils.getProperty(dsNamePrefix + DataDriverConstant.JDBC_MIN_EITM);
        
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
    private static void init() throws Exception {
        if (connectionPoolMap == null) {
            synchronized (ConnectionPool.class) {
                if (connectionPoolMap == null) {
                    connectionPoolMap = new ConcurrentHashMap<>();
                    for (int i = 0; i < SOURCE_NAME_LIST.size(); i++) {
                        Properties properties = initProperties(SOURCE_NAME_LIST.get(i) + ".");
                        BasicDataSource connectionPool = 
                            (BasicDataSource) BasicDataSourceFactory.createDataSource(properties);
                        connectionPoolMap.put(SOURCE_NAME_LIST.get(i), connectionPool);
                    }  
                }
            }
        }
    }
    
    /**
     * 从连接池中获取连接.
     * 
     * @param dsName 数据源名称
     * @return 返回连接对象
     */
    public static Connection getConnection(String dsName) {
        Connection conn = null;
        try {
            if (connectionPoolMap == null) {
                init();
            }
            if (connectionPoolMap != null) {
                conn = connectionPoolMap.get(dsName).getConnection();
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            logger.error("get connection error, please check the log.", e);
        }
        return conn;
    }
    
    /**
     * 关闭数据库连接资源.
     * 
     * @param conn 数据库连接对象
     * @param pstmt 预编译SQL对象
     * @param rs 结果集对象
     */
    public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
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
    public static void close(Connection conn, PreparedStatement pstmt) {
        close(conn, pstmt, null);
    }

    /**
     * 关闭数据库连接资源.
     * 
     * @param conn 数据库连接对象
     */
    public static void close(Connection conn) {
        close(conn, null, null);
    }

    private static void closeResources(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                logger.error("close Connection error.", e);
            }
        }
    }

    private static void closeResources(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                logger.error("close PreparedStatement error.", e);
            }
        }
    }

    private static void closeResources(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("close ResultSet error.", e);
            }
        }
    }
    
    /**
     * 获取第一个数据源名称.
     * 
     * @return 返回数据源名称
     */
    public static String getFirstDataSourceName() {
        return SOURCE_NAME_LIST.getFirst();
    }
    
    /**
     * 检查数据源是否存在.
     * 
     * @param dataSourceName 数据源名称
     * @return  如果存在返回 true, 否则返回 false
     */
    public static boolean checkDataSourceName(String dataSourceName) {
        return SOURCE_NAME_LIST.contains(dataSourceName);
    }
}
