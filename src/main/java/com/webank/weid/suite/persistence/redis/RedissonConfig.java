package com.webank.weid.suite.persistence.redis;

import com.webank.weid.constant.RedisDriverConstant;
import com.webank.weid.util.PropertyUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;


/**
 * @author karenli
 * @program: weid-java-sdk
 * @description:
 * @date 2020-07-03 16:29:21
 */

public class RedissonConfig {

    public RedissonConfig() {
    }

    //单节点模式
    public RedissonClient redissonSingleClient(){
        Config config = new Config();
        config.useSingleServer().setAddress(PropertyUtils.getProperty(RedisDriverConstant.REDISSON_URL));
        RedissonClient client= Redisson.create(config);

        return client;
    }

    //集群模式
    public RedissonClient redissonClusterClient()  {

        Config config = new Config();

         //集群状态扫描间隔时间，单位是毫秒, 可以用"rediss://"来启用SSL连接
        try {
            config.useClusterServers().setScanInterval(RedisDriverConstant.SCAN_INTERVAL)
                    .addNodeAddress(PropertyUtils.getProperty(RedisDriverConstant.REDISSON_URL).split(","))
                    .setMasterConnectionMinimumIdleSize(RedisDriverConstant.MASTER_CONNECTION_MINIMUM_IDLE_SIZE)
                    .setMasterConnectionPoolSize(RedisDriverConstant.MASTER_CONNECTION_POOL_SIZE)
                    .setMasterConnectionMinimumIdleSize(RedisDriverConstant.MASTER_CONNECTION_MINIMUM_IDLE_SIZE)
                    .setMasterConnectionPoolSize(RedisDriverConstant.MASTER_CONNECTION_POOL_SIZE)
                    .setConnectTimeout(RedisDriverConstant.CONNECT_TIMEOUT)
                    .setIdleConnectionTimeout(RedisDriverConstant.IDLE_CONNECTION_TIMEOUT)
                    .setTimeout(RedisDriverConstant.TIMEOUT)
                    .setRetryAttempts(RedisDriverConstant.RETRY_ATTEMPTS)
                    .setRetryInterval(RedisDriverConstant.RETRY_INTERVAL);


        } catch (Exception e) {
            e.printStackTrace();
        }
        RedissonClient redisson = Redisson.create(config);

        return redisson;
    }

}

