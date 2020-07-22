/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
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

package com.webank.weid.suite.persistence.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.util.PropertyUtils;

/**
 * redisson配置类.
 *
 * @author karenli 2020年7月3日
 */
public class RedissonConfig {

    private static final Config config = new Config();
    //连接URL
    private static final String redisUrl = PropertyUtils.getProperty(
            DataDriverConstant.REDIS_URL);

    private static final List<String> redisNodes = Arrays.asList(redisUrl.split(","));

    private static final String password = PropertyUtils.getProperty(
            DataDriverConstant.PASSWORD);

    /**
     * 单节点模式.
     *
     * @return 返回单节点模式的redissonclient.
     */
    public RedissonClient redissonSingleClient() {

        String redisPrefix = DataDriverConstant.REDIS_SINGLE;
        //数据库选择，默认为db0
        String databaseKey = redisPrefix + DataDriverConstant.DATABASE;
        String passwd = null;
        if (StringUtils.isNoneBlank(password)) {
            passwd = password;
        }
        int database = Integer.parseInt(PropertyUtils.getProperty(
                databaseKey, DataDriverConstant.DATABASE_DEFAULT_VALUE));
        config.useSingleServer().setAddress("redis://" + redisUrl)
                .setPassword(passwd)
                .setDatabase(database);
        RedissonClient client = Redisson.create(config);
        return client;
    }

    /**
     * 集群模式.
     *
     * @return 返回集群模式的redissonclient.
     */
    public RedissonClient redissonClusterClient() {

        //配置文件的前缀
        String redisPrefix = DataDriverConstant.REDIS_CLUSTER;
        //连接空闲超时时间
        String idleConnectionTimeoutKey =
                redisPrefix + DataDriverConstant.IDLE_CONNECTION_TIMEOUT;
        int idleConnectionTimeout = Integer.parseInt(PropertyUtils.getProperty(
                idleConnectionTimeoutKey,
                DataDriverConstant.IDLE_CONNECTION_TIMEOUT_DEFAULT_VALUE));
        //连接超时时间
        String connectTimeoutKey = redisPrefix + DataDriverConstant.CONNECT_TIMEOUT;
        int connectTimeout = Integer.parseInt(PropertyUtils.getProperty(
                connectTimeoutKey, DataDriverConstant.CONNECT_TIMEOUT_DEFAULT_VALUE));
        //等待节点回复命令的时间
        String timeoutKey = redisPrefix + DataDriverConstant.TIMEOUT;
        int timeout = Integer.parseInt(PropertyUtils.getProperty(
                timeoutKey, DataDriverConstant.TIMEOUT_DEFAULT_VALUE));
        //从节点最小空闲连接数
        String slaveConnMinIdleSizeKey =
                redisPrefix + DataDriverConstant.SLAVE_CONNECTION_MINIMUM_IDLE_SIZE;
        int slaveConnMinIdleSize = Integer.parseInt(PropertyUtils.getProperty(
                slaveConnMinIdleSizeKey,
                DataDriverConstant.SLAVE_CONNECTION_MINIMUM_IDLE_SIZE_DEFAULT_VALUE));
        //从节点连接池大小
        String slaveConnPoolSizeKey =
                redisPrefix + DataDriverConstant.SLAVE_CONNECTION_POOL_SIZE;
        int slaveConnPoolSize = Integer.parseInt(PropertyUtils.getProperty(
                slaveConnPoolSizeKey,
                DataDriverConstant.SLAVE_CONNECTION_POOL_SIZE_DEFAULT_VALUE));
        //主节点最小空闲连接数
        String masterConnMinIdleSizeKey =
                redisPrefix + DataDriverConstant.MASTER_CONNECTION_MINIMUM_IDLE_SIZE;
        int masterConnMinIdleSize = Integer.parseInt(PropertyUtils.getProperty(
                masterConnMinIdleSizeKey,
                DataDriverConstant.MASTER_CONNECTION_MINIMUM_IDLE_SIZE_DEFAULT_VALUE));
        //主节点连接池大小
        String masterConnPoolSizeKey =
                redisPrefix + DataDriverConstant.MASTER_CONNECTION_POOL_SIZE;
        int masterConnPoolSize = Integer.parseInt(PropertyUtils.getProperty(
                masterConnPoolSizeKey,
                DataDriverConstant.MASTER_CONNECTION_POOL_SIZE_DEFAULT_VALUE));

        //Url添加redis://前缀
        List<String> clusterNodes = new ArrayList<>();
        for (int i = 0; i < redisNodes.size(); i++) {
            clusterNodes.add("redis://" + redisNodes.get(i));
        }
        //读取配置文件中的password，默认为null
        String passwd = null;
        if (StringUtils.isNoneBlank(password)) {
            passwd = password;
        }

        config.useClusterServers().addNodeAddress(clusterNodes.toArray(
                new String[clusterNodes.size()]))
                .setPassword(passwd)
                .setIdleConnectionTimeout(idleConnectionTimeout)
                .setConnectTimeout(connectTimeout)
                .setTimeout(timeout)
                .setSlaveConnectionMinimumIdleSize(slaveConnMinIdleSize)
                .setSlaveConnectionPoolSize(slaveConnPoolSize)
                .setMasterConnectionMinimumIdleSize(masterConnMinIdleSize)
                .setMasterConnectionPoolSize(masterConnPoolSize);

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    /**
     * 判断redis配置为单节点模式还是集群模式.
     *
     * @return 返回集群模式/单节点模式的redissonclient.
     */
    public RedissonClient redismodelRecognition() {

        if (redisNodes.size() > 1) {
            return redissonClusterClient();
        } else {
            return redissonSingleClient();
        }
    }
}
