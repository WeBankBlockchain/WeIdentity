package com.webank.weid.suite.persistence.redis;

import java.util.Arrays;
import java.util.List;

import com.webank.weid.suite.persistence.mysql.SqlDomain;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import com.webank.weid.constant.RedisDriverConstant;
import com.webank.weid.util.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * redisson配置类.
 *
 * @author karenli 2020年7月3日
 */
public class RedissonConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedissonConfig.class);
    private static final String redisurl = PropertyUtils.getProperty(RedisDriverConstant.REDISSON_URL);

    public RedissonConfig() {
    }

    /**
     * 单节点模式.
     *
     * @return 返回单节点模式的redissonclient.
     */
    public RedissonClient redissonSingleClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisurl);
        RedissonClient client = Redisson.create(config);
        return client;
    }

    /**
     * 集群模式.
     *
     * @return 返回集群模式的redissonclient.
     */
    public RedissonClient redissonClusterClient() {

        Config config = new Config();
        //集群状态扫描间隔时间，单位是毫秒, 可以用"rediss://"来启用SSL连接
        try {
            config.useClusterServers().setScanInterval(RedisDriverConstant.SCAN_INTERVAL)
                    .addNodeAddress(
                            PropertyUtils.getProperty(redisurl))
                    .setMasterConnectionMinimumIdleSize(
                            RedisDriverConstant.MASTER_CONNECTION_MINIMUM_IDLE_SIZE)
                    .setMasterConnectionPoolSize(
                            RedisDriverConstant.MASTER_CONNECTION_POOL_SIZE)
                    .setMasterConnectionMinimumIdleSize(
                            RedisDriverConstant.MASTER_CONNECTION_MINIMUM_IDLE_SIZE)
                    .setMasterConnectionPoolSize(
                            RedisDriverConstant.MASTER_CONNECTION_POOL_SIZE)
                    .setConnectTimeout(
                            RedisDriverConstant.CONNECT_TIMEOUT)
                    .setIdleConnectionTimeout(
                            RedisDriverConstant.IDLE_CONNECTION_TIMEOUT)
                    .setTimeout(RedisDriverConstant.TIMEOUT)
                    .setRetryAttempts(RedisDriverConstant.RETRY_ATTEMPTS)
                    .setRetryInterval(RedisDriverConstant.RETRY_INTERVAL);

        } catch (Exception e) {
            e.printStackTrace();
        }
        RedissonClient redisson = Redisson.create(config);

        return redisson;
    }

    /**
     * 判断redis配置为单节点模式还是集群模式.
     *
     * @return 返回集群模式/单节点模式的redissonclient.
     */
    public RedissonClient redismodelRecognition() {

        List<String> list = Arrays.asList(redisurl.split(","));
        if (list.size() > 1) {
            return redissonClusterClient();
        } else {
            return redissonSingleClient();
        }
    }
}

