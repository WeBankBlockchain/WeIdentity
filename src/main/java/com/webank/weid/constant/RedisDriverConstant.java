package com.webank.weid.constant;

/**
 * constant of redis.
 *
 * @author karenli
 */
public class RedisDriverConstant {

    /**
     * redis execute status.
     */
    public static final Integer REDISSON_EXECUTE_FAILED_STATUS = 0;
    public static final Integer REDISSON_EXECUTE_SUCESS_STATUS = 1;

    public static final String DOMAIN_DEFAULT_INFO = "domain.defaultInfo";

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
}
