

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
    public static final int REDISSON_EXECUTE_FAILED_STATUS = 0;
    public static final int REDISSON_EXECUTE_SUCESS_STATUS = 1;

    /**
     * redis mode name.
     */
    public static final String REDIS_SINGLE = "redis_single";
    public static final String REDIS_CLUSTER = "redis_cluster";

    /**
     * redis url properties.
     */
    public static final String REDIS_URL = "redis.url";

    /**
     * redis user password properties.
     */
    public static final String REDIS_PASSWORD = "redis.password";

    /**
     * redis single database properties.
     */
    public static final String REDIS_DATABASE = "database";

    /**
     * redis cluster idle_connection_timeout properties.
     */
    public static final String IDLE_CONNECTION_TIMEOUT = "idle_connection_timeout";

    /**
     * redis cluster connect_timeout properties.
     */
    public static final String CONNECT_TIMEOUT = "connect_timeout";

    /**
     * redis cluster timeout properties.
     */
    public static final String TIMEOUT = "timeout";

    /**
     * redis cluster slave_connection_minimum_idle_size properties.
     */
    public static final String SLAVE_CONNECTION_MINIMUM_IDLE_SIZE =
            "slave_connection_minimum_idle_size";

    /**
     * redis cluster slave_connection_pool_size properties.
     */
    public static final String SLAVE_CONNECTION_POOL_SIZE =
            "slave_connection_pool_size";

    /**
     * redis cluster master_connection_minimum_idle_size properties.
     */
    public static final String MASTER_CONNECTION_MINIMUM_IDLE_SIZE =
            "master_connection_minimum_idle_size";

    /**
     * redis cluster master_connection_pool_size properties.
     */
    public static final String MASTER_CONNECTION_POOL_SIZE =
            "master_connection_pool_size";

    /**
     * the default value for redis single config.
     */
    public static final String DATABASE_DEFAULT_VALUE = "0";

    /**
     * the default value for redis cluster config.
     */
    public static final String IDLE_CONNECTION_TIMEOUT_DEFAULT_VALUE = "10000";
    public static final String CONNECT_TIMEOUT_DEFAULT_VALUE = "10000";
    public static final String TIMEOUT_DEFAULT_VALUE = "3000";
    public static final String SLAVE_CONNECTION_MINIMUM_IDLE_SIZE_DEFAULT_VALUE = "10";
    public static final String SLAVE_CONNECTION_POOL_SIZE_DEFAULT_VALUE = "64";
    public static final String MASTER_CONNECTION_MINIMUM_IDLE_SIZE_DEFAULT_VALUE = "10";
    public static final String MASTER_CONNECTION_POOL_SIZE_DEFAULT_VALUE = "64";

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
     * jdbc maxIdle properties.
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
    public static final int SQL_EXECUTE_FAILED_STATUS = 0;

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
     * redis存储的离线交易记录.
     */
    public static final String DOMAIN_OFFLINE_TRANSACTION_INFO = "offline_transaction_info";

    /**
     * 系统domain之私钥存储domainKey.
     */
    public static final String DOMAIN_ENCRYPTKEY = "domain.encryptKey";

    public static final String DOMAIN_ISSUER_TEMPLATE_SECRET = "domain.templateSecret";

    public static final String DOMAIN_USER_MASTER_SECRET = "domain.masterKey";

    public static final String DOMAIN_USER_CREDENTIAL_SIGNATURE = "domain.credentialSignature";

    public static final String DOMAIN_RESOURCE_INFO = "domain.resourceInfo";

    public static final String DOMAIN_WEID_AUTH = "domain.weIdAuth";

    /**
     * 本地运行所需表.
     */

    public static final String LOCAL_WEID_DOCUMENT = "local.weIdDocument";

    public static final String LOCAL_CPT = "local.cpt";

    public static final String LOCAL_POLICY = "local.policy";

    public static final String LOCAL_PRESENTATION = "local.presentation";

    public static final String LOCAL_ROLE = "local.role";

    public static final String LOCAL_AUTHORITY_ISSUER = "local.authorityIssuer";

    public static final String LOCAL_SPECIFIC_ISSUER = "local.specificIssuer";

    public static final String LOCAL_EVIDENCE = "local.evidence";
    /**
     * redis本地运行所需记录索引.
     */

    public static final String REDIS_INDEX_WEID = "weId";

    public static final String REDIS_INDEX_WEID_DOCUMENT = "weIdDocument";

    public static final String REDIS_INDEX_CPT = "cpt";

    public static final String REDIS_INDEX_POLICY = "policy";

    public static final String REDIS_INDEX_PRESENTATION = "presentation";

    public static final String REDIS_INDEX_ROLE = "role";

    public static final String REDIS_INDEX_AUTHORITY_ISSUER = "authorityIssuer";

    public static final String REDIS_INDEX_SPECIFIC_ISSUER = "specificIssuer";

    public static final String REDIS_INDEX_EVIDENCE = "evidence";

    /**
     * ipfs所需常量.
     */
    public static final int IPFS_EXECUTE_FAILED_STATUS = 0;

    public static final String IPFS_API ="ipfs.api";

    public static final String IPFS_WEID_PATH ="src/main/resources/weId.json";

    public static final int IPFS_WRITE_SUCCESS = 0;

    public static final int IPFS_ONLY_ID_LINES = 1;

    public static final int IPFS_READ_CID_LINES = 2;

}