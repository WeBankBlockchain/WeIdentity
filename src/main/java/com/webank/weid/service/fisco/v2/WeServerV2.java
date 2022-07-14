

package com.webank.weid.service.fisco.v2;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.service.impl.callback.CommonCallback;
import com.webank.weid.service.impl.callback.KeyManagerCallback;
import org.apache.commons.collections4.CollectionUtils;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.Amop;
import org.fisco.bcos.sdk.amop.AmopCallback;
import org.fisco.bcos.sdk.amop.AmopMsgOut;
import org.fisco.bcos.sdk.amop.AmopResponseCallback;
import org.fisco.bcos.sdk.amop.topic.TopicType;
import org.fisco.bcos.sdk.channel.model.ChannelRequest;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.contract.precompiled.cns.CnsInfo;
import org.fisco.bcos.sdk.contract.precompiled.cns.CnsService;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.ECDSAKeyPair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.CnsType;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.InitWeb3jException;
import com.webank.weid.exception.PrivateKeyIllegalException;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.service.fisco.WeServer;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.util.DataToolUtils;

/*public final class WeServerV2 extends WeServer {

    private static final Logger logger = LoggerFactory.getLogger(WeServerV2.class);

    private Client client;
    private CnsService cnsService;
    private CryptoKeyPair cryptoKeyPair;
    private static final String configFile = "E:\\java learning projects\\bac-test\\src\\main\\resources\\config.toml";
    private static final BcosSDK sdk = BcosSDK.build(configFile);
    private static final String privateKey = "";


    public WeServerV2(FiscoConfig fiscoConfig) {
        super(fiscoConfig);
    }

    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public Class<?> getClientClass() {
        return Client.class;
    }

    @Override
    public CryptoKeyPair getCredentials() {
        return cryptoKeyPair;
    }

    @Override
    public CryptoKeyPair createCredentials(String privateKey) {
        try {
            return client.getCryptoSuite().getKeyPairFactory().createKeyPair(privateKey);
        } catch (Exception e) {
            throw new PrivateKeyIllegalException(e);
        }
    }

    @Override
    protected void initClient(Integer groupId) {
        logger.info("[WeServiceImplV2] begin to init Fisco client..");
        client = sdk.getClient(Integer.valueOf(groupId));
        if (client == null) {
            logger.error("[WeServiceImplV2] Fisco client failed. ");
            throw new InitWeb3jException("Fisco client failed.");
        }
        cryptoKeyPair = client.getCryptoSuite().createKeyPair();

        cnsService = new CnsService(client, cryptoKeyPair);
        logger.info("[WeServiceImplV2] init Fisco client success..");
    }

    @Override
    public Set<String> getTopic() {
        Amop amop = sdk.getAmop();
        return amop.getSubTopics();
    }

    *//**
     * 获取bcos sdk对象，用于给使用者注册callback处理器.
     *
     * @return 返回SDK
     *//*
    @Override
    public BcosSDK getSDK() {
        return sdk;
    }

    @Override
    protected void setDefaultCallback() {
        Amop amop = sdk.getAmop();
        amop.subscribePrivateTopics(
                "GET_ENCRYPT_KEY",
                privateKey,
                new KeyManagerCallback()
        );
        amop.subscribePrivateTopics(
                "COMMON_REQUEST",
                privateKey,
                new CommonCallback()
        );
    }

    @Override
    public void sendChannelMessage(AmopCommonArgs amopCommonArgs, int timeOut, AmopResponseCallback cb) {
        Amop amop = sdk.getAmop();
        AmopMsgOut out = new AmopMsgOut();
        out.setType(TopicType.PRIVATE_TOPIC);
        out.setContent(amopCommonArgs.getMessage().getBytes(StandardCharsets.UTF_8));
        out.setTimeout(super.getTimeOut(timeOut));
        out.setTopic(amopCommonArgs.getTopic());
        amop.sendAmopMsg(out, cb);
    }

    *//*@Override
    public AmopResponse sendChannelMessage(AmopCommonArgs amopCommonArgs, int timeOut) {

        ChannelRequest request = new ChannelRequest();
        request.setTimeout(super.getTimeOut(timeOut));
        request.setToTopic(amopCommonArgs.getToAmopId());
        request.setMessageID(amopCommonArgs.getMessageId());
        request.setContent(amopCommonArgs.getMessage());

        ChannelResponse response = this.getService().sendChannelMessage2(request);

        AmopResponse amopResponse = new AmopResponse();
        amopResponse.setMessageId(response.getMessageID());
        amopResponse.setErrorCode(response.getErrorCode());
        amopResponse.setResult(response.getContent());
        amopResponse.setErrorMessage(response.getErrorMessage());
        return amopResponse;
    }*//*


    @Override
    public int getBlockNumber() throws IOException {
        BlockNumber response = this.getClient().getBlockNumber();
        return response.getBlockNumber().intValue();
    }

    @Override
    public String getVersion() throws IOException {
        return this.getClient().getNodeVersion().getNodeVersion().getVersion();
    }

    @Override
    protected CnsInfo queryCnsInfo(CnsType cnsType) throws WeIdBaseException {
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
}*/
