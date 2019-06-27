/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.service.fisco.v2;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.fisco.bcos.channel.client.ChannelPushCallback;
import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.channel.dto.ChannelRequest;
import org.fisco.bcos.channel.dto.ChannelResponse;
import org.fisco.bcos.channel.handler.ChannelConnections;
import org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.fisco.bcos.web3j.protocol.core.methods.response.BlockNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.config.FiscoConfig;
import com.webank.weid.exception.InitWeb3jException;
import com.webank.weid.exception.PrivateKeyIllegalException;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.rpc.callback.OnNotifyCallbackV2;
import com.webank.weid.service.fisco.WeServer;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.util.DataToolUtils;

public final class WeServerV2 extends WeServer<Web3j, Credentials, Service> {

    private static final Logger logger = LoggerFactory.getLogger(WeServerV2.class);

    private static Web3j web3j;
    private static Service service;
    private static Credentials credentials;

    public WeServerV2(FiscoConfig fiscoConfig) {
        super(fiscoConfig, new OnNotifyCallbackV2());
    }

    @Override
    public Web3j getWeb3j() {
        return web3j;
    }

    @Override
    public Class<?> getWeb3jClass() {
        return Web3j.class;
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
    protected void initWeb3j() {
        logger.info("[WeServiceImplV2] begin to init web3j instance..");
        service = buildFiscoBcosService(fiscoConfig);
        service.setPushCallback((ChannelPushCallback) pushCallBack);
        // Set topics for AMOP
        Set<String> topics = new HashSet<String>();
        topics.add(fiscoConfig.getCurrentOrgId());
        service.setTopics(topics);
        try {
            service.run();
        } catch (Exception e) {
            logger.error("[WeServiceImplV2] Service init failed. ", e);
            throw new InitWeb3jException(e);
        }

        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        channelEthereumService.setChannelService(service);
        web3j = Web3j.build(channelEthereumService);
        if (web3j == null) {
            logger.error("[WeServiceImplV2] web3j init failed. ");
            throw new InitWeb3jException();
        }

        credentials = GenCredential.create();
        if (credentials == null) {
            logger.error("[WeServiceImplV2] credentials init failed. ");
            throw new InitWeb3jException();
        }
        logger.info("[WeServiceImplV2] init web3j instance success..");
    }

    private Service buildFiscoBcosService(FiscoConfig fiscoConfig) {

        Service service = new Service();
        service.setOrgID(fiscoConfig.getCurrentOrgId());
        service.setConnectSeconds(Integer.valueOf(fiscoConfig.getWeb3sdkTimeout()));
        // group info
        Integer groupId = Integer.valueOf(fiscoConfig.getGroupId());
        service.setGroupId(groupId);

        // connect key and string
        ChannelConnections channelConnections = new ChannelConnections();
        channelConnections.setGroupId(groupId);
        channelConnections.setCaCertPath("classpath:" + fiscoConfig.getV2CaCrtPath());
        channelConnections.setNodeCaPath("classpath:" + fiscoConfig.getV2NodeCrtPath());
        channelConnections.setNodeKeyPath("classpath:" + fiscoConfig.getV2NodeKeyPath());
        channelConnections.setConnectionsStr(Arrays.asList(fiscoConfig.getNodes().split(",")));
        GroupChannelConnectionsConfig connectionsConfig = new GroupChannelConnectionsConfig();
        connectionsConfig.setAllChannelConnections(Arrays.asList(channelConnections));
        service.setAllChannelConnections(connectionsConfig);

        // thread pool params
        service.setThreadPool(super.initializePool());
        return service;
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
        BlockNumber response = this.getWeb3j().getBlockNumber().send();
        return response.getBlockNumber().intValue();
    }
}
