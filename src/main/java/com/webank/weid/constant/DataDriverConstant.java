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

package com.webank.weid.constant;

/**
 * constant for DataDriver.
 * 
 * @author tonychen 2019年3月25日
 */
public final class DataDriverConstant {

    /**
     * jdbc url properties.
     */
    public static final String JDBC_DATASOURCE_NAME = "datasource.name";

    /**
     * jdbc url properties.
     */
    public static final String JDBC_URL = "jdbc.url";

    /**
     * jdbc username properties.
     */
    public static final String JDBC_USER_NAME = "jdbc.username";

    /**
     * jdbc user password properties.
     */
    public static final String JDBC_USER_PASSWORD = "jdbc.password";
    
    /**
     * jdbc maxActive properties.
     */
    public static final String JDBC_MAX_ACTIVE = "jdbc.maxActive";
    
    /**
     * jdbc minIdle properties.
     */
    public static final String JDBC_MIN_IDLE = "jdbc.minIdle";
    
    /**
     * jdbc minIdle properties.
     */
    public static final String JDBC_MAX_IDLE = "jdbc.maxIdle";
    
    /**
     * jdbc maxWait properties.
     */
    public static final String JDBC_MAX_WAIT = "jdbc.maxWait";
    
    /**
     * jdbc timeBetweenEvictionRunsMillis properties.
     */
    public static final String JDBC_TIME_BETWEEN_ERM = "jdbc.timeBetweenEvictionRunsMillis";
    
    /**
     * jdbc numTestsPerEvictionRun properties.
     */
    public static final String JDBC_NUM_TEST_PER_ER = "jdbc.numTestsPerEvictionRun";
    
    /**
     * jdbc maxWait properties.
     */
    public static final String JDBC_MIN_EITM = "jdbc.minEvictableIdleTimeMillis";
    
    /**
     * jdbc driverClassName.
     */
    public static final String JDBC_MYSQL_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
   
    /**
     * jdbc removeAbandoned.
     */
    public static final String JDBC_REMOVE_ABANDONED = "true";
    
    /**
     * jdbc removeAbandonedTimeout.
     */
    public static final String JDBC_REMOVE_ABANDONED_TIMEOUT = "180";
    
    /**
     * jdbc testOnBorrow.
     */
    public static final String JDBC_TEST_ON_BORROW = "false";
    
    /**
     * jdbc testOnWhile.
     */
    public static final String JDBC_TEST_ON_WHILE = "true";
    
    /**
     * jdbc validationQuery.
     */
    public static final String JDBC_VALIDATION_QUERY = "SELECT 1";
    
    /**
     * sql execute status.
     */
    public static final Integer SQL_EXECUTE_FAILED_STATUS = 0;

    /**
     * jdbc user password properties.
     */
    public static final String SQL_COLUMN_DATA = "data";

    /**
     * sql index.
     */
    public static final int SQL_INDEX_FIRST = 1;

    /**
     * sql index.
     */
    public static final int SQL_INDEX_SECOND = 2;

    /**
     * sql index.
     */
    public static final int SQL_INDEX_THIRD = 3;
    
    /**
     * the key of ConnectionPool.
     */
    public static final String POOL_DRIVER_CLASS_NAME = "driverClassName";
    public static final String POOL_URL = "url";
    public static final String POOL_USERNAME = "username";
    public static final String POOL_PASSWORD = "password";
    public static final String POOL_MAX_ACTIVE = "maxActive";
    public static final String POOL_MIN_IDLE = "minIdle";
    public static final String POOL_MAX_IDLE = "maxIdle";
    public static final String POOL_MAX_WAIT = "maxWait";
    public static final String POOL_MAX_REMOVE_ABANDONED = "removeAbandoned";
    public static final String POOL_MAX_REMOVE_ABANDONED_TIMEOUT = "removeAbandonedTimeout";
    public static final String POOL_TEST_ON_BORROW = "testOnBorrow";
    public static final String POOL_TEST_ON_WHILE = "testWhileIdle";
    public static final String POOL_TIME_BETWEEN_ERM = "timeBetweenEvictionRunsMillis";
    public static final String POOL_NUM_TEST_PER_ER = "numTestsPerEvictionRun";
    public static final String POOL_VALIDATION_QUERY = "validationQuery";
    public static final String POOL_MIN_EITM = "minEvictableIdleTimeMillis";
    
    /**
     *  the default value for pool.
     */
    public static final String POOL_MAX_ACTIVE_DEFAULT_VALUE = "50";
    public static final String POOL_MIN_IDLE_DEFAULT_VALUE = "5";
    public static final String POOL_MAX_IDLE_DEFAULT_VALUE = "5";
    public static final String POOL_MAX_WAIT_DEFAULT_VALUE = "10000";
    public static final String POOL_NUM_TEST_PER_ER_DEFAULT_VALUE = "5";
    public static final String POOL_TIME_BETWEEN_ERM_DEFAULT_VALUE = "600000";
    public static final String POOL_MIN_EITM_DEFAULT_VALUE = "1800000";

    /**
     * 系统默认的domain.
     */
    public static final String DOMAIN_DEFAULT_INFO = "domain.defaultInfo";
    public static final String DOMAIN_DEFAULT_INFO_TIMEOUT = "domain.defaultInfo.timeout";
    
    /**
     * 系统domain之私钥存储domainKey.
     */
    public static final String DOMAIN_ENCRYPTKEY = "domain.encryptKey";

    public static final String DOMAIN_ISSUER_TEMPLATE_SECRET = "domain.templateSecret";

    public static final String DOMAIN_USER_MASTER_SECRET = "domain.masterKey";
 
    public static final String DOMAIN_USER_CREDENTIAL_SIGNATURE = "domain.credentialSignature";
    
    public static final String DOMAIN_RESOURCE_INFO = "domain.resourceInfo";
}