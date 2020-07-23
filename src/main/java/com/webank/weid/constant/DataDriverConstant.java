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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * constant for DataDriver.
 *
 * @author tonychen 2019年3月25日
 */
public final class DataDriverConstant {

    /**
     *encoding form.
     */
    public static final Charset STANDARDCHARSETS_ISO = StandardCharsets.ISO_8859_1;
    public static final Charset STANDARDCHARSETS_UTF_8 = StandardCharsets.UTF_8;

    /**
     * redis execute status.
     */
    public static final Integer REDISSON_EXECUTE_FAILED_STATUS = 0;
    public static final Integer REDISSON_EXECUTE_SUCESS_STATUS = 1;

    /**
     * redis url properties.
     */
    public static final String REDISSON_URL = "redisson.url";

    /**
     * the default value for redis cluster config.
     */
    public static final Integer IDLE_CONNECTION_TIMEOUT = 10000;
    public static final Integer PING_TIMEOUT = 1000;
    public static final Integer CONNECT_TIMEOUT = 10000;
    public static final Integer TIMEOUT = 3000;
    public static final Integer RETRY_ATTEMPTS = 3;
    public static final Integer RETRY_INTERVAL = 1500;
    public static final Integer RECONNECTION_TIMEOUT = 3000;
    public static final Integer FAILED_ATTEMPTS = 3;
    public static final String PASSWORD = "DO_NOT_USE_IF_IT_IS_NOT_SET";
    public static final Integer SUBSCRIPTIONS_PER_CONNECTION = 5;
    public static final String CLIENT_NAME = null;
    public static final String LOAD_BALANCER_REF = "MYLOADBALANCER";
    public static final Integer SUBSCRIPTION_CONNECTION_MINIMUM_IDLE_SIZE = 1;
    public static final Integer SUBSCRIPTION_CONNECTION_POOL_SIZE = 50;
    public static final Integer SLAVE_CONNECTION_MINIMUM_IDLE_SIZE = 10;
    public static final Integer SLAVE_CONNECTION_POOL_SIZE = 64;
    public static final Integer MASTER_CONNECTION_MINIMUM_IDLE_SIZE = 10;
    public static final Integer MASTER_CONNECTION_POOL_SIZE = 64;
    public static final String READ_MODE = "SLAVE";
    public static final String SUBSCRIPTION_MODE = "SLAVE";
    public static final Integer SCAN_INTERVAL = 1000;

    /**
     * jdbc url properties.
     */
    public static final String JDBC_DATASOURCE_NAME = "datasource.name";

    /**
     * jdbc url properties.
     */
    public static final String JDBC_URL = "jdbc.url";
    
    /**
     * jdbc driver properties.
     */
    public static final String JDBC_DRIVER = "jdbc.driver";

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
     * jdbc minEvictableIdleTimeMillis properties.
     */
    public static final String JDBC_MIN_EITM = "jdbc.minEvictableIdleTimeMillis";
    
    /**
     * jdbc initialSize properties.
     */
    public static final String JDBC_INIT_SIZE = "jdbc.initialSize";

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
    public static final String POOL_INITIAL_SIZE = "initialSize";
    public static final String POOL_MAX_ACTIVE = "maxTotal";
    public static final String POOL_MIN_IDLE = "minIdle";
    public static final String POOL_MAX_IDLE = "maxIdle";
    public static final String POOL_MAX_WAIT = "maxWaitMillis";
    public static final String POOL_MAX_REMOVE_ABANDONED = "removeAbandonedOnBorrow";
    public static final String POOL_MAX_REMOVE_MAINTENANCE = "removeAbandonedOnMaintenance";
    public static final String POOL_MAX_REMOVE_ABANDONED_TIMEOUT = "removeAbandonedTimeout";
    public static final String POOL_TEST_ON_BORROW = "testOnBorrow";
    public static final String POOL_TEST_ON_WHILE = "testWhileIdle";
    public static final String POOL_TIME_BETWEEN_ERM = "timeBetweenEvictionRunsMillis";
    public static final String POOL_NUM_TEST_PER_ER = "numTestsPerEvictionRun";
    public static final String POOL_VALIDATION_QUERY = "validationQuery";
    public static final String POOL_MIN_EITM = "minEvictableIdleTimeMillis";

    /**
     * the default value for pool.
     */
    public static final String POOL_DRIVER_NAME_DEFAULT_VALUE = "com.mysql.jdbc.Driver";
    public static final String POOL_MAX_ACTIVE_DEFAULT_VALUE = "50";
    public static final String POOL_MIN_IDLE_DEFAULT_VALUE = "5";
    public static final String POOL_INIT_DEFAULT_VALUE = "5";
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

    public static final String DOMAIN_WEID_AUTH = "domain.weIdAuth";
}