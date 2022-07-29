

package com.webank.weid.service.fisco.v3;

import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.constant.CnsType;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.PrivateKeyIllegalException;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.CnsInfo;
import com.webank.weid.rpc.callback.OnNotifyCallbackV2;
import com.webank.weid.rpc.callback.OnNotifyCallbackV3;
import com.webank.weid.service.fisco.WeServer;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.service.impl.callback.CommonCallback;
import com.webank.weid.service.impl.callback.KeyManagerCallback;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.PropertyUtils;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.jni.amop.AmopRequestCallback;
import org.fisco.bcos.sdk.jni.amop.AmopResponseCallback;
import org.fisco.bcos.sdk.jni.common.Response;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.config.model.AmopTopic;
import org.fisco.bcos.sdk.v3.config.model.ConfigProperty;
import org.fisco.bcos.sdk.v3.contract.precompiled.bfs.BFSPrecompiled.BfsInfo;
import org.fisco.bcos.sdk.v3.contract.precompiled.bfs.BFSService;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeServerV3 extends WeServer<BcosSDK, Client, CryptoKeyPair> {

    /*
     * Maximum Timeout period in milliseconds.
     */
    public static final int MAX_AMOP_REQUEST_TIMEOUT = 50000;
    public static final int AMOP_REQUEST_TIMEOUT = Integer
        .valueOf(PropertyUtils.getProperty("amop.request.timeout", "5000"));

    /**
     * log4j.
     */
    private static final Logger logger = LoggerFactory.getLogger(WeServerV3.class);

    private BcosSDK bcosSdk;

    private Client client;

    private BFSService bfsService;

    /**
     * 获取Client对象所属的类型,此处是为了给动态加载合约使用.
     *
     * @return Client的Class
     */
    public Class<?> getClientClass() {
        return Client.class;
    }

    /**
     * 构造WeServer对象,此时仅为初始化做准备.
     *
     * @param fiscoConfig FISCO配置对象
     */
    public WeServerV3(FiscoConfig fiscoConfig) {
        super(fiscoConfig, new OnNotifyCallbackV3());
        initWeb3j(fiscoConfig.getGroupId());
    }

    /**
     * 获取Client对象.
     *
     * @return 返回Client对象
     */
    @Override
    public Client getWeb3j() {
        return client;
    }

    public Client getWeb3j(String groupId) {
        logger.debug("getWeb3j groupId{}", groupId);
        return bcosSdk.getClient(groupId);
    }


    @Override
    public Class<?> getWeb3jClass() {
        return Client.class;
    }

    @Override
    public CryptoKeyPair getCredentials() {
        Client client = this.getWeb3j(fiscoConfig.getGroupId());
        return client.getCryptoSuite().getCryptoKeyPair();
    }

    @Override
    public CryptoKeyPair createCredentials(String privateKey) {
        return this.client.getCryptoSuite().getKeyPairFactory().createKeyPair(new BigInteger(privateKey));
    }

    @Override
    public void initWeb3j(String masterGroupId) {
        this.pushCallBack = new OnNotifyCallbackV3();
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
     * 发送AMOP消息.
     *
     * @param amopCommonArgs AMOP请求体
     * @param timeOut        AMOP请求超时时间
     * @return 返回AMOP响应体.
     */
    @Override
    public AmopResponse sendChannelMessage(AmopCommonArgs amopCommonArgs, int timeOut) {
        String topic = amopCommonArgs.getTopic();
        byte[] content = amopCommonArgs.getMessage().getBytes();
        int timeout = getTimeOut(timeOut);
        ArrayBlockingQueue<AmopResponse> queue = new ArrayBlockingQueue<>(1);
        bcosSdk.getAmop().sendAmopMsg(topic, content, timeout, new AmopResponseCallback() {
            @Override
            public void onResponse(Response response) {
                AmopResponse amopResponse = new AmopResponse();
//                amopResponse.setMessageId(response.getMessageID()); todo message id empty
                amopResponse.setErrorCode(response.getErrorCode());
                amopResponse.setErrorMessage(response.getErrorMessage());
                if (response.getData() != null) {
                    amopResponse.setResult(new String(response.getData(), StandardCharsets.UTF_8));
                }
                queue.add(amopResponse);
            }

        });
        try {
            AmopResponse response = queue.poll(timeout, TimeUnit.MILLISECONDS);
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


    @Override
    public int getBlockNumber() {
        return this.getWeb3j(fiscoConfig.getGroupId()).getBlockNumber().getBlockNumber().intValue();
    }

    /**
     * 获取FISCO-BCOS版本.
     *
     * @return 返回版本信息
     * @throws IOException 可能出现的异常.
     */
    @Override
    public String getVersion() throws IOException {
        String nodeEndPoint = fiscoConfig.getNodes();
        if (nodeEndPoint.contains(",")) {
            nodeEndPoint = nodeEndPoint.split(",")[0];
        }
        return this.getWeb3j().getGroupNodeInfo(nodeEndPoint).getResult().getIniConfig().getBinaryInfo().getVersion();
    }


    /**
     * 查询bucketAddress.
     *
     * @param cnsType cns类型枚举
     * @return 返回CnsInfo
     * @throws WeIdBaseException 查询合约地址异常
     */
    @Override
    protected CnsInfo queryCnsInfo(CnsType cnsType) throws WeIdBaseException {
        try {
            logger.info("[queryBucketFromCns] query address by type = {}", cnsType.getName());
            // /apps/helloworld/1.0
            List<BfsInfo> bfsInfoList = null;
            try {
                bfsInfoList = bfsService.list("/apps/" + cnsType.getName());
            } catch (ContractException ex) {
                bfsInfoList = new ArrayList<>();
            }
            // 获取 /apps/helloworld下所有的版本记录，1.0 2.0
            List<BfsInfo> versionInfoList = bfsInfoList.stream().filter(bfs
                -> "link".equals(bfs.getFileType())).collect(Collectors.toList());
            if (!versionInfoList.isEmpty()) {
                // 获取当前cnsType的大版本前缀
                String cnsTypeVersion = cnsType.getVersion();
                String preV = cnsTypeVersion.substring(0, cnsTypeVersion.indexOf(".") + 1);
                // 从后往前找到相应大版本的数据
                for (int i = versionInfoList.size() - 1; i >= 0; i--) {
                    BfsInfo versionInfo = versionInfoList.get(i);
                    String version = versionInfo.getFileName();
                    if (version.startsWith(preV)) {
//                        String address = bfsService.readlink("/apps/" + cnsType.getName() + "/" + version);
                        List<BfsInfo> cnsInfoList = bfsService.list("/apps/" + cnsType.getName() + "/" + version);
                        if (!cnsInfoList.isEmpty()) {
                            BfsInfo cnsInfo = cnsInfoList.iterator().next();
                            List<String> addressAndAbi = cnsInfo.getExt();
                            if (addressAndAbi.size() != 2) {
                                logger.info("bfs return ext of address and abi is invalid, {}",
                                    versionInfo);
                                throw new WeIdBaseException(ErrorCode.UNKNOW_ERROR);
                            }
                            // 读取真正的地址
                            String address = addressAndAbi.get(0);
                            String abi = addressAndAbi.get(1);
                            logger
                                .info("[queryBucketFromCns] query address form CNS successfully.");
                            return new CnsInfo(cnsType.getName(), version, address, abi);
                        }
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


    private ConfigProperty loadConfigProperty(FiscoConfig fiscoConfig) {
        ConfigProperty configProperty = new ConfigProperty();
        // init account
//        initAccount(configProperty, fiscoConfig);
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

//    private void initAccount(ConfigProperty configProperty, FiscoConfig fiscoConfig) {
//        Map<String, Object> account = new HashMap<String, Object>();
//        account.put("keyStoreDir", "account");
//        account.put("accountFileFormat", "pem");
//        logger.info("[initAccount] the account: {}.", account);
//        configProperty.setAccount(account);
//    }

    //这里暂时只注册一个topic
    private void initAmopTopic(ConfigProperty configProperty, FiscoConfig fiscoConfig) {
        logger.info("[initAmopTopic] the amopId: {}", fiscoConfig.getAmopId());
        AmopTopic amopTopic = new AmopTopic();
        amopTopic.setTopicName(fiscoConfig.getAmopId());
        // 配置amop用到的私钥文件，写入的是public keys的路径和p12私钥的路径及p12密码
        amopTopic.setPublicKeys(Arrays.asList(fiscoConfig.getAmopPubPath()));
        amopTopic.setPrivateKey(fiscoConfig.getPrivateKey());
        amopTopic.setPassword(fiscoConfig.getAmopP12Password());
        List<AmopTopic> amop = new ArrayList<AmopTopic>();
        amop.add(amopTopic);
        configProperty.setAmop(amop);
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
        cryptoMaterial.put("useSMCrypto", fiscoConfig.getSdkSMCrypto());
        cryptoMaterial.put("certPath", fiscoConfig.getSdkCertPath());
        logger.info("path:{} before", cryptoMaterial.get("certPath"));
        cryptoMaterial.put("certPath", "D:\\projects\\weid\\WeIdentity\\out\\test\\resources");
        logger.info("path:{}", cryptoMaterial.get("certPath"));
        cryptoMaterial.put("certPath", "D:\\projects\\weid\\WeIdentity\\out\\production\\resources");
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
                        client = bcosSdk.getClient(fiscoConfig.getGroupId());
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
        // todo check
        bcosSdk.getAmop().setCallback((AmopRequestCallback) pushCallBack);
        bcosSdk.getAmop().subscribeTopic(getTopic(fiscoConfig), (AmopRequestCallback) pushCallBack);

    }

    private void initCnsService() {
        Client client = this.getWeb3j(fiscoConfig.getGroupId());
        this.bfsService = new BFSService(client, client.getCryptoSuite().getCryptoKeyPair());
    }



    @Override
    public Set<String> getGroupList() {
        List<String> groupStrList = bcosSdk.getClient().getGroupList().getResult().getGroupList();
        Set<String> groupList = new HashSet<>(groupStrList);
        return groupList;
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

    @Override
    public BcosSDK getBcosSDK() {
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
            return client.getCryptoSuite().getKeyPairFactory().createKeyPair(new BigInteger(privateKey));
        } catch (Exception e) {
            throw new PrivateKeyIllegalException(e);
        }
    }

    /**
     * 获取Credentials对象.
     *
     * @return 返回Credentials对象
     */
    public CryptoKeyPair createRandomCryptoKeyPair() {
        return client.getCryptoSuite().getKeyPairFactory().generateKeyPair();
    }

}
