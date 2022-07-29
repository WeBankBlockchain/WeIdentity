

package com.webank.weid.service.fisco;

import com.webank.weid.config.FiscoConfig;
import com.webank.weid.service.BaseService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import org.fisco.bcos.sdk.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
//    private static final Map<String, Client> WEB3J_MAP = new ConcurrentHashMap<String, Client>();
//    private static final Map<String, org.fisco.bcos.sdk.v3.client.Client> WEB3J_V3_MAP =
//        new ConcurrentHashMap<String, org.fisco.bcos.sdk.v3.client.Client>();
    // 存放群组的节点列表, key - 群组Id, value - 节点列表
    private static final Map<String, List<String>> GROUP_NODE_MAP =
        new ConcurrentHashMap<String, List<String>>();
    private static Timer timer;
    private static final Long DEFAULT_INTEVAL_PERIOD = 60L * 5 * 1000;
    private static String fiscoVersion;

    static {
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
        // 获取chain version of 2 or 3
        fiscoVersion = fiscoConfig.getVersion();
        logger.info("[init] the current nodes: {}.", nodeList);
        // 遍历每个节点
//        for (String node : nodeList) {
//            // 判断当前节点是否已存在缓存中
//            Client web3j = WEB3J_MAP.get(node);
//            // 如果不存在则初始化
//            if (web3j == null) {
//                logger.info("[init] begin init web3j, node: {}.", node);
//                WEB3J_MAP.put(node, buildWeb3j(fiscoConfig, node));
//            }
//        }
    }
//
//    private static Client buildWeb3j(FiscoConfig fiscoConfig, String node) {
//        Service service = new Service();
//        service.setOrgID(getOrgId(fiscoConfig));
//        service.setConnectSeconds(Integer.valueOf(fiscoConfig.getWeb3sdkTimeout()));
//        // group info
//        service.setGroupId(DEFAULT_GROUP_ID);
//        List<String> nodeList = new ArrayList<String>();
//        nodeList.add(node);
//        GroupChannelConnectionsConfig connectionsConfig = buildGroupChannelConnectionsConfig(
//            DEFAULT_GROUP_ID, fiscoConfig, nodeList);
//        service.setAllChannelConnections(connectionsConfig);
//        // thread pool params
//        service.setThreadPool(executor);
//        try {
//            logger.info("[init] begin run the service， node: {}.", node);
//            service.run();
//        } catch (Exception e) {
//            logger.error("[init] Service init failed. ", e);
//            throw new InitWeb3jException(e);
//        }
//        ChannelEthereumService channelEthereumService = buildChannelEthereumService(service);
//        logger.info("[init] begin build the Web3j, node: {}.", node);
//        return Web3j.build(channelEthereumService, DEFAULT_GROUP_ID);
//    }
//
//    /**
//     * 构建GroupChannelConnectionsConfig.
//     * @param groupId 群组编号
//     * @param fiscoConfig fisco配置
//     * @param nodeList 节点列表
//     * @return 返回GroupChannelConnectionsConfig对象
//     */
//    public static GroupChannelConnectionsConfig buildGroupChannelConnectionsConfig(
//        Integer groupId,
//        FiscoConfig fiscoConfig,
//        List<String> nodeList
//    ) {
//        // connect key and string
//        ChannelConnections channelConnections = new ChannelConnections();
//        channelConnections.setGroupId(groupId);
//        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//
//        if (fiscoConfig.getEncryptType().equals(String.valueOf(EncryptType.ECDSA_TYPE))) {
//            EncryptType encryptType = new EncryptType(EncryptType.ECDSA_TYPE);
//            channelConnections.setCaCert(
//                    resolver.getResource("classpath:" + fiscoConfig.getV2CaCrtPath()));
//            channelConnections.setSslCert(
//                    resolver.getResource("classpath:" + fiscoConfig.getV2NodeCrtPath()));
//            channelConnections.setSslKey(
//                    resolver.getResource("classpath:" + fiscoConfig.getV2NodeKeyPath()));
//        } else {
//            EncryptType encryptType = new EncryptType(EncryptType.SM2_TYPE);
//
//            // gmca.crt
//            channelConnections.setGmCaCert(
//                    resolver.getResource("classpath:" + fiscoConfig.getGmCaCrtPath()));
//            // gmsdk.crt
//            channelConnections.setGmSslCert(
//                    resolver.getResource("classpath:" + fiscoConfig.getGmSdkCrtPath()));
//            // gmsdk.key
//            channelConnections.setGmSslKey(
//                    resolver.getResource("classpath:" + fiscoConfig.getGmSdkKeyPath()));
//            // gmensdk.crt
//            channelConnections.setGmEnSslCert(
//                    resolver.getResource("classpath:" + fiscoConfig.getGmenSdkCrtPath()));
//            // gmensdk.key
//            channelConnections.setGmEnSslKey(
//                    resolver.getResource("classpath:" + fiscoConfig.getGmenSdkKeyPath()));
//        }
//
//        channelConnections.setConnectionsStr(nodeList);
//        GroupChannelConnectionsConfig connectionsConfig = new GroupChannelConnectionsConfig();
//        connectionsConfig.setAllChannelConnections(Arrays.asList(channelConnections));
//        return connectionsConfig;
//    }
//


    private static String getOrgId(FiscoConfig fiscoConfig) {
        return fiscoConfig.getCurrentOrgId() + "_group";
    }


    /**
     * todo check
     */
    private static synchronized void loadGroup() {
        logger.info("[loadGroup] begin loading group.");
        if ("2".equals(fiscoVersion)) {
            Client client = (Client) BaseService.getClient();
            List<String> groupList = null;
            try {
                logger.info("[loadGroup] begin get groupList from the sdk.");
                groupList = client.getGroupList().getGroupList();
                logger.info("[loadGroup] get groupList successfully, groupList : {}.", groupList);
            } catch (Throwable e) {
                logger.warn("[loadGroup] get groupList has error.", e);
                groupList = Collections.emptyList();
            }
            for (String groupId : groupList) {
                logger.info("[loadGroup] the node:{}.", groupId);
                List<String> nodeList = GROUP_NODE_MAP.get(groupId);
                if (nodeList == null) {
                    logger.info("[loadGroup] add a new group to mapping, group: {}.", groupId);
                    nodeList = client.getGroupPeers().getGroupPeers();
                    GROUP_NODE_MAP.put(groupId, nodeList);
                }
            }
        } else {
            org.fisco.bcos.sdk.v3.client.Client client = (org.fisco.bcos.sdk.v3.client.Client) BaseService.getClient();
            List<String> groupList = null;
            try {
                logger.info("[loadGroup] begin get groupList from the sdk.");
                groupList = client.getGroupList().getResult().getGroupList();
                logger.info("[loadGroup] get groupList successfully, groupList : {}.", groupList);
            } catch (Throwable e) {
                logger.warn("[loadGroup] get groupList has error.", e);
                groupList = Collections.emptyList();
            }
            for (String groupId : groupList) {
                logger.info("[loadGroup] the node:{}.", groupId);
                List<String> nodeList = GROUP_NODE_MAP.get(groupId);
                if (nodeList == null) {
                    logger.info("[loadGroup] add a new group to mapping, group: {}.", groupId);
                    nodeList = client.getGroupPeers().getGroupPeers(); // todo 3.0此处包含所有的节点
                    GROUP_NODE_MAP.put(groupId, nodeList);
                }
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
