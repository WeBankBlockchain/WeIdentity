

package com.webank.weid.service.fisco;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.PrivateKeyIllegalException;
import com.webank.weid.rpc.callback.OnNotifyCallbackV2;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.AmopCallback;
import org.fisco.bcos.sdk.amop.AmopMsgOut;
import org.fisco.bcos.sdk.amop.AmopResponseCallback;
import org.fisco.bcos.sdk.amop.topic.TopicType;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.model.AmopTopic;
import org.fisco.bcos.sdk.config.model.ConfigProperty;
import org.fisco.bcos.sdk.contract.precompiled.cns.CnsInfo;

import org.fisco.bcos.sdk.contract.precompiled.cns.CnsService;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.constant.CnsType;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.rpc.callback.RegistCallBack;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.service.impl.callback.CommonCallback;
import com.webank.weid.service.impl.callback.KeyManagerCallback;
import com.webank.weid.util.PropertyUtils;

public class WeServer {

    /*
     * Maximum Timeout period in milliseconds.
     */
    public static final int MAX_AMOP_REQUEST_TIMEOUT = 50000;
    public static final int AMOP_REQUEST_TIMEOUT = Integer
            .valueOf(PropertyUtils.getProperty("amop.request.timeout", "5000"));

    /**
     * log4j.
     */
    private static final Logger logger = LoggerFactory.getLogger(WeServer.class);

    private static WeServer weServer;

    /**
     * WeServer对象上下文.
     */
    /*private static ConcurrentHashMap<Integer, WeServer>  weServerContext =
        new ConcurrentHashMap<>();*/

    /**
     * bucket地址映射Map.
     */
    private static ConcurrentHashMap<String, CnsInfo> bucketAddressMap =
            new ConcurrentHashMap<>();

    /**
     * FISCO配置对象.
     */
    //protected FiscoConfig fiscoConfig;

    private BcosSDK bcosSdk;

    private Client client;

    private Integer masterGroupId;

    private CnsService cnsService;

    /**
     * 获取Client对象所属的类型,此处是为了给动态加载合约使用.
     *
     * @return Client的Class
     */
    public Class<?> getClientClass() {
        return Client.class;
    }

    /**
     * AMOP回调处理注册器.
     */
    //protected RegistCallBack pushCallBack;
    private RegistCallBack pushCallBack;

    /**
     * 构造WeServer对象,此时仅为初始化做准备.
     *
     * @param fiscoConfig FISCO配置对象
     */
    protected WeServer(FiscoConfig fiscoConfig) {
        this.masterGroupId = Integer.parseInt(fiscoConfig.getGroupId());
        this.pushCallBack = new OnNotifyCallbackV2();
        logger.info("[WeServer] begin load property.");
        ConfigProperty configProperty = loadConfigProperty(fiscoConfig);
        logger.info("[WeServer] begin init bcos sdk.");
        initBcosSdk(configProperty);
        logger.info("[WeServer] begin init CnsService.");
        initCnsService();
        logger.info("[WeServer] begin init initAmopCallBack.");
        initAmopCallBack(fiscoConfig);
        logger.info("[WeServer] WeServer init successfully.");
    }

    /**
     * 初始化WeServer服务,进行多线程安全保护,确保整个应用只初始化一次 并且根据配置FISCO的版本来自动初始化对应版本的服务.
     *
     * @param fiscoConfig FISCO配置对象
     * @return 返回WeServer对象
     */
    /*public static synchronized WeServer getInstance(
        FiscoConfig fiscoConfig, 
        Integer groupId
    ) {
        WeServer weServer = weServerContext.get(groupId);*/
    public static WeServer getInstance(FiscoConfig fiscoConfig) {
        if (weServer == null) {
            synchronized (WeServer.class) {
                //weServer = weServerContext.get(groupId);
                if (weServer == null) {
                    weServer = new WeServer(fiscoConfig);
                    //weServer.initClient(groupId);
                    //weServerContext.put(groupId, weServer);
                }
            }
        }
        return weServer;
    }

    /**
     * 注册默认的callback.
     */
    /*private void registDefaultCallback() {
        pushCallBack.registAmopCallback(
            AmopMsgType.GET_ENCRYPT_KEY.getValue(),
            new KeyManagerCallback()
        );
        pushCallBack.registAmopCallback(
            AmopMsgType.COMMON_REQUEST.getValue(),
            new CommonCallback()
        );
    }*/
    private ConfigProperty loadConfigProperty(FiscoConfig fiscoConfig) {
        ConfigProperty configProperty = new ConfigProperty();
        // init account
        initAccount(configProperty, fiscoConfig);
        // init amop topic
        initAmopTopic(configProperty, fiscoConfig);
        // init netWork
        initNetWork(configProperty, fiscoConfig);
        // init ThreadPool
        initThreadPool(configProperty, fiscoConfig);
        // init CryptoMaterial
        initCryptoMaterial(configProperty, fiscoConfig);
        return configProperty;
    }

    private void initAccount(ConfigProperty configProperty, FiscoConfig fiscoConfig) {
        Map<String, Object> account = new HashMap<String, Object>();
        account.put("keyStoreDir", "account");
        account.put("accountFileFormat", "pem");
        logger.info("[initAccount] the account: {}.", account);
        configProperty.setAccount(account);
    }

    //这里暂时只注册一个topic
    private void initAmopTopic(ConfigProperty configProperty, FiscoConfig fiscoConfig) {
        logger.info("[initAmopTopic] the amopId: {}", fiscoConfig.getAmopId());
        AmopTopic amopTopic = new AmopTopic();
        amopTopic.setTopicName(fiscoConfig.getAmopId());
        List<AmopTopic> amop = new ArrayList<AmopTopic>();
        amop.add(amopTopic);
        configProperty.setAmop(amop);
    }

    private void initNetWork(ConfigProperty configProperty, FiscoConfig fiscoConfig) {
        List<String> nodeList = Arrays.asList(fiscoConfig.getNodes().split(","));
        logger.info("[initNetWork] the current nodes: {}.", nodeList);
        Map<String, Object> netWork = new HashMap<String, Object>();
        netWork.put("peers", nodeList);
        configProperty.setNetwork(netWork);
    }

    private void initThreadPool(ConfigProperty configProperty, FiscoConfig fiscoConfig) {
        Map<String, Object> threadPool = new HashMap<String, Object>();
        threadPool.put("channelProcessorThreadSize", fiscoConfig.getWeb3sdkMaxPoolSize());
        threadPool.put("receiptProcessorThreadSize", fiscoConfig.getWeb3sdkMaxPoolSize());
        threadPool.put("maxBlockingQueueSize", fiscoConfig.getWeb3sdkQueueSize());
        logger.info("[initThreadPool] the threadPool: {}.", threadPool);
        configProperty.setThreadPool(threadPool);
    }

    private void initCryptoMaterial(ConfigProperty configProperty, FiscoConfig fiscoConfig) {
        Map<String, Object> cryptoMaterial = new HashMap<String, Object>();
        cryptoMaterial.put("caCert",
                FiscoConfig.class.getResource("/").getPath() + fiscoConfig.getV2CaCrtPath());
        cryptoMaterial.put("sslCert",
                FiscoConfig.class.getResource("/").getPath() + fiscoConfig.getV2NodeCrtPath());
        cryptoMaterial.put("sslKey",
                FiscoConfig.class.getResource("/").getPath() + fiscoConfig.getV2NodeKeyPath());
        logger.info("[initThreadPool] the cryptoMaterial: {}.", cryptoMaterial);
        configProperty.setCryptoMaterial(cryptoMaterial);
    }

    private void initBcosSdk(ConfigProperty configProperty) {
        if (bcosSdk == null) {
            synchronized (WeServer.class) {
                logger.info("[WeServer] the WeServer class is locked.");
                if (bcosSdk == null) {
                    logger.info("[WeServer] the bcosSdk is null and build BcosSDK.");
                    try {
                        bcosSdk = new BcosSDK(new ConfigOption(configProperty));
                        client = bcosSdk.getClient(Integer.valueOf(this.masterGroupId));
                    } catch (Exception e) {
                        logger.error("[build] the ConfigOption build fail.", e);
                        throw new WeIdBaseException("the ConfigOption build fail.");
                    }
                } else {
                    logger.info("[WeServer] the bcosSdk is not null.");
                }
                logger.info("[WeServer] the WeServer class is unlock");
                if (bcosSdk != null) {
                    logger.info("[WeServer] the bcosSdk is build successfully.");
                } else {
                    throw new WeIdBaseException("the bcosSdk build fail.");
                }
            }
        }
    }

    private void initAmopCallBack(FiscoConfig fiscoConfig) {
        pushCallBack.registAmopCallback(
                AmopMsgType.GET_ENCRYPT_KEY.getValue(),
                new KeyManagerCallback()
        );
        pushCallBack.registAmopCallback(
                AmopMsgType.COMMON_REQUEST.getValue(),
                new CommonCallback()
        );
        bcosSdk.getAmop().setCallback((AmopCallback) pushCallBack);
        bcosSdk.getAmop().subscribeTopic(getTopic(fiscoConfig), (AmopCallback) pushCallBack);
    }

    private void initCnsService() {
        Client client = this.getClient(this.masterGroupId);
        this.cnsService = new CnsService(client, client.getCryptoSuite().getCryptoKeyPair());
    }

    public CryptoKeyPair getDefaultCryptoKeyPair() {
        Client client = this.getClient(this.masterGroupId);
        return client.getCryptoSuite().getCryptoKeyPair();
    }

    /**
     * 获取Client对象.
     *
     * @return 返回Client对象
     */
    public Client getClient() {
        return client;
    }

    public Client getClient(Integer groupId) {
        return bcosSdk.getClient(groupId);
    }

    public Integer getBlockNumber(Integer groupId) {
        return this.getClient(groupId).getBlockNumber().getBlockNumber().intValue();
    }

    public Integer getBlockNumber() {
        return this.getClient(this.masterGroupId).getBlockNumber().getBlockNumber().intValue();
    }

    public Integer getMasterGroupId() {
        return this.masterGroupId;
    }

    public Set<Integer> getGroupList() {
        return bcosSdk.getGroupManagerService().getGroupList();
    }

    /**
     * 获取PushCallback对象，用于给使用者注册callback处理器.
     *
     * @return 返回RegistCallBack
     */
    public RegistCallBack getPushCallback() {
        return pushCallBack;
    }

    /**
     * 获取超时时间，如果超时时间非法，则返回默认的超时时间.
     *
     * @param timeOut 调用对应AMOP请求接口的超时时间,毫秒单位.
     * @return 返回正确有效的超时时间
     */
    private int getTimeOut(int timeOut) {
        if (timeOut > MAX_AMOP_REQUEST_TIMEOUT || timeOut < 0) {
            logger.error("invalid timeOut : {}", timeOut);
            return MAX_AMOP_REQUEST_TIMEOUT;
        } else {
            return timeOut;
        }
    }

    /**
     * 获取AMOP监听的topic.
     *
     * @return 返回topic集合，目前sdk只支持单topic监听
     */
    private String getTopic(FiscoConfig fiscoConfig) {
        if (StringUtils.isNotBlank(FiscoConfig.topic)) {
            return fiscoConfig.getAmopId() + "_" + FiscoConfig.topic;
        } else {
            return fiscoConfig.getAmopId();
        }
    }

    public BcosSDK getSDK() {
        return bcosSdk;
    }

    /**
     * 根据传入的私钥(16进制数字私钥)，进行动态创建Credentials对象.
     *
     * @param privateKey 数字私钥
     * @return 返回Credentials对象
     */
    public CryptoKeyPair createCryptoKeyPair(String privateKey) {
        try {
            return client.getCryptoSuite().getKeyPairFactory().createKeyPair(privateKey);
        } catch (Exception e) {
            throw new PrivateKeyIllegalException(e);
        }
    }

    /**
     * 获取Credentials对象.
     *
     * @return 返回Credentials对象
     */
    public CryptoKeyPair createCryptoKeyPair() {
        return client.getCryptoSuite().createKeyPair();
    }

    /**
     * 发送AMOP消息.
     *
     * @param amopCommonArgs AMOP请求体
     * @param timeOut        AMOP请求超时时间
     * @return 返回AMOP响应体.
     */
    public AmopResponse sendChannelMessage(AmopCommonArgs amopCommonArgs, int timeOut) {
        AmopMsgOut out = new AmopMsgOut();
        out.setType(TopicType.NORMAL_TOPIC);
        out.setContent(amopCommonArgs.getMessage().getBytes());
        out.setTimeout(getTimeOut(timeOut));
        out.setTopic(amopCommonArgs.getTopic());
        ArrayBlockingQueue<AmopResponse> queue = new ArrayBlockingQueue<>(1);
        bcosSdk.getAmop().sendAmopMsg(out, new AmopResponseCallback() {
            @Override
            public void onResponse(org.fisco.bcos.sdk.amop.AmopResponse response) {
                AmopResponse amopResponse = new AmopResponse();
                amopResponse.setMessageId(response.getMessageID());
                amopResponse.setErrorCode(response.getErrorCode());
                if (response.getAmopMsgIn() != null) {
                    amopResponse.setResult(new String(response.getAmopMsgIn().getContent()));
                }
                amopResponse.setErrorMessage(response.getErrorMessage());
                queue.add(amopResponse);
            }
        });
        try {
            AmopResponse response = queue.poll(out.getTimeout(), TimeUnit.MILLISECONDS);
            if (response == null) {
                response = new AmopResponse();
                response.setErrorCode(102);
                response.setMessageId(amopCommonArgs.getMessageId());
                response.setErrorMessage("Amop timeout");
            }
            if (StringUtils.isBlank(response.getResult())) {
                response.setResult("{}");
            }
            return response;
        } catch (Exception e) {
            logger.error("[sendChannelMessage] wait for callback has error.", e);
            throw new WeIdBaseException(ErrorCode.UNKNOW_ERROR);
        }
    }


    /**
     * 获取FISCO-BCOS版本.
     * 
     * @return 返回版本信息
     * @throws IOException 可能出现的异常.
     */
    public String getVersion() throws IOException {
        return this.getClient().getNodeVersion().getNodeVersion().getVersion();
    }

    /**
     * 查询bucketAddress.
     * 
     * @param cnsType cns类型枚举
     * @return 返回CnsInfo
     * @throws WeIdBaseException 查询合约地址异常
     */
    private CnsInfo queryCnsInfo(CnsType cnsType) throws WeIdBaseException {
        try {
            logger.info("[queryBucketFromCns] query address by type = {}.", cnsType.getName());
            List<CnsInfo> cnsInfoList = cnsService.selectByName(cnsType.getName());
            if (cnsInfoList != null) {
                // 获取当前cnsType的大版本前缀
                String cnsTypeVersion = cnsType.getVersion();
                String preV = cnsTypeVersion.substring(0, cnsTypeVersion.indexOf(".") + 1);
                //从后往前找到相应大版本的数据
                for (int i = cnsInfoList.size() - 1; i >= 0; i--) {
                    CnsInfo cnsInfo = cnsInfoList.get(i);
                    if (cnsInfo.getVersion().startsWith(preV)) {
                        logger.info("[queryBucketFromCns] query address form CNS successfully.");
                        return cnsInfo;
                    }
                }
            }
            logger.warn("[queryBucketFromCns] can not find data from CNS.");
            return null;
        } catch (Exception e) {
            logger.error("[queryBucketFromCns] query address has error.", e);
            throw new WeIdBaseException(ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * 获取Bucket地址.
     * 
     * @param cnsType cns类型枚举
     * @return 返回bucket地址
     */
    public CnsInfo getBucketByCns(CnsType cnsType) {
        CnsInfo cnsInfo = bucketAddressMap.get(cnsType.toString());
        if (cnsInfo == null) {
            cnsInfo = this.queryCnsInfo(cnsType);
            if (cnsInfo != null) {
                bucketAddressMap.put(cnsType.toString(), cnsInfo);
            }
        }
        return cnsInfo;
    }
}
