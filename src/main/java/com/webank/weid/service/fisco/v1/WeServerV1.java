package com.webank.weid.service.fisco.v1;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bcos.channel.client.ChannelPushCallback;
import org.bcos.channel.client.Service;
import org.bcos.channel.dto.ChannelRequest;
import org.bcos.channel.dto.ChannelResponse;
import org.bcos.channel.handler.ChannelConnections;
import org.bcos.web3j.crypto.Credentials;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.GenCredential;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.bcos.web3j.protocol.core.Response;
import org.bcos.web3j.protocol.core.methods.response.EthBlockNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.config.FiscoConfig;
import com.webank.weid.exception.InitWeb3jException;
import com.webank.weid.exception.PrivateKeyIllegalException;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.rpc.callback.OnNotifyCallbackV1;
import com.webank.weid.service.fisco.WeServer;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.util.DataToolUtils;

public final class WeServerV1 extends WeServer<Web3j,Credentials,Service> {
    
    private static final Logger logger = LoggerFactory.getLogger(WeServerV1.class);
    
    private static Web3j web3j;
    private static Service service;
    private static Credentials credentials;

    public WeServerV1(FiscoConfig fiscoConfig) {
        super(fiscoConfig, new OnNotifyCallbackV1());
    }

    @Override
    public Web3j getWeb3j() {
        return web3j;
    }

    @Override
    public Service getService() {
        return service;
    }

    @Override
    public Credentials getCredentials() {
        return credentials;
    }

    @Override
    protected void initWeb3j() {
        logger.info("[WeServiceImplV1] begin to init web3j instance..");
        service = buildFiscoBcosService(fiscoConfig);        
        service.setPushCallback((ChannelPushCallback)pushCallBack);
        // Set topics for AMOP
        List<String> topics = new ArrayList<String>();
        topics.add(fiscoConfig.getCurrentOrgId());
        service.setTopics(topics);
        
        try {
            service.run();
        } catch (Exception e) {
            logger.error("[WeServiceImplV1] Service init failed. ", e);
            throw new InitWeb3jException(e);
        }

        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        channelEthereumService.setChannelService(service);
        web3j = Web3j.build(channelEthereumService);
        if (web3j == null) {
            logger.error("[WeServiceImplV1] web3j init failed. ");
            throw new InitWeb3jException();
        }
        
        credentials = GenCredential.create();
        if (credentials == null) {
            logger.error("[WeServiceImplV1] credentials init failed. ");
            throw new InitWeb3jException();
        }
        logger.info("[WeServiceImplV1] init web3j instance success..");
    }
    
    private Service buildFiscoBcosService(FiscoConfig fiscoConfig) {

        Service service = new Service();
        service.setOrgID(fiscoConfig.getCurrentOrgId());
        service.setConnectSeconds(Integer.valueOf(fiscoConfig.getWeb3sdkTimeout()));

        // connection params
        ChannelConnections channelConnections = new ChannelConnections();
        channelConnections.setCaCertPath("classpath:" + fiscoConfig.getV1CaCrtPath());
        channelConnections.setClientCertPassWord(fiscoConfig.getV1ClientCrtPassword());
        channelConnections
            .setClientKeystorePath("classpath:" + fiscoConfig.getV1ClientKeyStorePath());
        channelConnections.setKeystorePassWord(fiscoConfig.getV1KeyStorePassword());
        channelConnections.setConnectionsStr(Arrays.asList(fiscoConfig.getNodes().split(",")));
        ConcurrentHashMap<String, ChannelConnections> allChannelConnections =
            new ConcurrentHashMap<>();
        allChannelConnections.put(fiscoConfig.getCurrentOrgId(), channelConnections);
        service.setAllChannelConnections(allChannelConnections);

        // thread pool params
        service.setThreadPool(super.initializePool());
        return service;
    }

    @Override
    public Credentials createCredentials(String privateKey) {
        Credentials credentials;
        try {
            ECKeyPair keyPair = ECKeyPair.create(new BigInteger(privateKey));
            credentials = Credentials.create(keyPair);
            return credentials;
        } catch (Exception e) {
            throw new PrivateKeyIllegalException(e);
        }
    }

    @Override
    public AmopResponse sendChannelMessage(AmopCommonArgs amopCommonArgs, int timeOut) {
        
        ChannelRequest request = new ChannelRequest();
        request.setTimeout(super.getTimeOut(timeOut));
        request.setToTopic(amopCommonArgs.getToOrgId());
        request.setMessageID(DataToolUtils.getUuId32());
        request.setContent(amopCommonArgs.getMessage());
        
        ChannelResponse response = this.getService().sendChannelMessage2(request);
        
        AmopResponse amopResponse = new AmopResponse();
        amopResponse.setMessageId(response.getMessageID());
        amopResponse.setErrorCode(response.getErrorCode());
        amopResponse.setResult(response.getContent());
        amopResponse.setErrorMessage(response.getErrorMessage());
        return amopResponse;
    }

    @Override
    public int getBlockNumber() throws IOException {
        Response<String> response = getWeb3j().ethBlockNumber().send();
        if (response instanceof EthBlockNumber) {
            EthBlockNumber ethBlockNumber = (EthBlockNumber) response;
            return ethBlockNumber.getBlockNumber().intValue();
        }
        return 0;
    }

    @Override
    public Class<?> getWeb3jClass() {
        return Web3j.class;
    }
}
