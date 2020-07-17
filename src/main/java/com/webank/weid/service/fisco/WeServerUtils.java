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

package com.webank.weid.service.fisco;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.channel.handler.ChannelConnections;
import org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.exception.InitWeb3jException;

/**
 * WeServer辅助服务类.
 * @author yanggang
 *
 */
public class WeServerUtils {

    /**
     * log4j.
     */
    private static final Logger logger = LoggerFactory.getLogger(WeServerUtils.class);
    // 存放每个一个节点的web3j对象，用于获取当前节点所属的群组，key : ipPort, value : Web3j对象  
    private static final Map<String, Web3j> WEB3J_MAP = new ConcurrentHashMap<String, Web3j>();
    // 存放群组的节点列表, key - 群组Id, value - 节点列表
    private static final Map<String, List<String>> GROUP_NODE_MAP = 
        new ConcurrentHashMap<String, List<String>>();
    private static final Integer DEFAULT_GROUP_ID = 0;
    private static final String DEFAULT_POOL_NAME = "web3sdk-group";
    private static final Integer DEFAULT_CORE_POOL_SIZE = 1;
    private static final Integer DEFAULT_MAX_POOL_SIZE = 2;
    private static final Integer QUEUECAPACITY_SIZE = 10;
    private static final Integer KEEPALIVE_SECONDS = 30;
    private static ThreadPoolTaskExecutor executor;
    private static Timer timer;
    private static final Long DEFAULT_INTEVAL_PERIOD = 60L * 5 * 1000;

    static {
        // 初始化线程池
        executor = initializePool();
        // 初始化每个节点的web3j对象
        init();
        // 初次加载群组
        loadGroup();
        // 启动定时监听加载群组
        startMonitor();
    }

    private static void init() {
        logger.info("[init] begin init FiscoConfig.");
        // 读取最新配置
        FiscoConfig fiscoConfig = new FiscoConfig();
        // 加载配置
        fiscoConfig.load();
        logger.info("[init] begin check FiscoConfig.");
        // 配置检查
        fiscoConfig.check();
        // 获取最新的ip端口配置
        List<String> nodeList = Arrays.asList(fiscoConfig.getNodes().split(","));
        logger.info("[init] the current nodes: {}.", nodeList);
        // 遍历每个节点
        for (String node : nodeList) {
            // 判断当前节点是否已存在缓存中
            Web3j web3j = WEB3J_MAP.get(node);
            // 如果不存在则初始化
            if (web3j == null) {
                logger.info("[init] begin init web3j, node: {}.", node);
                WEB3J_MAP.put(node, buildWeb3j(fiscoConfig, node));
            }
        }
    }

    private static Web3j buildWeb3j(FiscoConfig fiscoConfig, String node) {
        Service service = new Service();
        service.setOrgID(getOrgId(fiscoConfig));
        service.setConnectSeconds(Integer.valueOf(fiscoConfig.getWeb3sdkTimeout()));
        // group info
        service.setGroupId(DEFAULT_GROUP_ID);
        List<String> nodeList = new ArrayList<String>();
        nodeList.add(node);
        GroupChannelConnectionsConfig connectionsConfig = buildGroupChannelConnectionsConfig(
            DEFAULT_GROUP_ID, fiscoConfig, nodeList);
        service.setAllChannelConnections(connectionsConfig);
        // thread pool params
        service.setThreadPool(executor);
        try {
            logger.info("[init] begin run the service， node: {}.", node);
            service.run();
        } catch (Exception e) {
            logger.error("[init] Service init failed. ", e);
            throw new InitWeb3jException(e);
        }
        ChannelEthereumService channelEthereumService = buildChannelEthereumService(service);
        logger.info("[init] begin build the Web3j, node: {}.", node);
        return Web3j.build(channelEthereumService, DEFAULT_GROUP_ID);
    }
    
    /**
     * 构建GroupChannelConnectionsConfig.
     * @param groupId 群组编号
     * @param fiscoConfig fisco配置
     * @param nodeList 节点列表
     * @return 返回GroupChannelConnectionsConfig对象
     */
    public static GroupChannelConnectionsConfig buildGroupChannelConnectionsConfig(
        Integer groupId,
        FiscoConfig fiscoConfig,
        List<String> nodeList
    ) {
        // connect key and string
        ChannelConnections channelConnections = new ChannelConnections();
        channelConnections.setGroupId(groupId);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        channelConnections
            .setCaCert(resolver.getResource("classpath:" + fiscoConfig.getV2CaCrtPath()));
        channelConnections
            .setSslCert(resolver.getResource("classpath:" + fiscoConfig.getV2NodeCrtPath()));
        channelConnections
            .setSslKey(resolver.getResource("classpath:" + fiscoConfig.getV2NodeKeyPath()));

        channelConnections.setConnectionsStr(nodeList);
        GroupChannelConnectionsConfig connectionsConfig = new GroupChannelConnectionsConfig();
        connectionsConfig.setAllChannelConnections(Arrays.asList(channelConnections));
        return connectionsConfig;
    }

    /**
     * 构建ChannelEthereumService.
     * @param service Web3sdk中的 Service对象
     * @return 返回ChannelEthereumService对象
     */
    public static ChannelEthereumService buildChannelEthereumService(Service service) {
        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        channelEthereumService.setChannelService(service);
        channelEthereumService.setTimeout(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT * 1000);
        return channelEthereumService;
    }
    
    private static String getOrgId(FiscoConfig fiscoConfig) {
        return fiscoConfig.getCurrentOrgId() + "_group";
    }

    private static ThreadPoolTaskExecutor initializePool() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setBeanName(DEFAULT_POOL_NAME);
        pool.setCorePoolSize(DEFAULT_CORE_POOL_SIZE);
        pool.setMaxPoolSize(DEFAULT_MAX_POOL_SIZE);
        pool.setQueueCapacity(QUEUECAPACITY_SIZE);
        pool.setKeepAliveSeconds(KEEPALIVE_SECONDS);
        pool.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.AbortPolicy());
        pool.initialize();
        return pool;
    }

    private static synchronized void loadGroup() {
        logger.info("[loadGroup] begin loading group.");
        Set<Entry<String, Web3j>> entrySet = WEB3J_MAP.entrySet();
        for (Entry<String, Web3j> entry : entrySet) {
            String node = entry.getKey();
            Web3j web3j = entry.getValue();
            logger.info("[loadGroup] the node:{}.", node);
            try {
                logger.info("[loadGroup] begin get groupList from the node: {}.", node);
                List<String> groupList = web3j.getGroupList().send().getResult();
                logger.info("[loadGroup] get groupList successfully, groupList : {}.", groupList);
                for (String groupId : groupList) {
                    List<String> list = GROUP_NODE_MAP.get(groupId);
                    if (list ==  null) {
                        logger.info("[loadGroup] add a new group to mapping, group: {}.", groupId);
                        GROUP_NODE_MAP.put(groupId, new ArrayList<String>());
                        list = GROUP_NODE_MAP.get(groupId);
                    }
                    if (!list.contains(node)) {
                        logger.info(
                            "[loadGroup] add a node to group, group: {}, node: {}.", 
                            groupId, 
                            node
                        );
                        list.add(node);
                    }
                }
            } catch (Throwable e) {
                logger.warn("[loadGroup] get groupList has error.", e);  
            }
        }
    }

    /**
     * 启动监听, 定时刷新配置.
     */
    private static void startMonitor() {
        Long intevalPeriod = DEFAULT_INTEVAL_PERIOD;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                loadGroup();
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(task, intevalPeriod, intevalPeriod);
    }
    
    /**
     * 获取群组到节点的映射关系.
     * 
     * @return 返回群组到节点的映射关系
     */
    public static Map<String, List<String>> getGroupMapping() {
        return new HashMap<String, List<String>>(GROUP_NODE_MAP);
    }

    /**
     * 获取群组集合.
     * 
     * @return 返回群组列表
     */
    public static List<String> getGroupList() {
        return new ArrayList<String>(GROUP_NODE_MAP.keySet());
    }
}
