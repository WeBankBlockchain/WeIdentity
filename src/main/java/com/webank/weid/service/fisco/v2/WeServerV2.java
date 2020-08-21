/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

package com.webank.weid.service.fisco.v2;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.channel.client.ChannelPushCallback;
import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.channel.dto.ChannelRequest;
import org.fisco.bcos.channel.dto.ChannelResponse;
import org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.precompile.cns.CnsInfo;
import org.fisco.bcos.web3j.precompile.cns.CnsService;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.fisco.bcos.web3j.protocol.core.methods.response.BlockNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.CnsType;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.InitWeb3jException;
import com.webank.weid.exception.PrivateKeyIllegalException;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.rpc.callback.OnNotifyCallbackV2;
import com.webank.weid.service.fisco.WeServer;
import com.webank.weid.service.fisco.WeServerUtils;
import com.webank.weid.service.impl.base.AmopCommonArgs;

public final class WeServerV2 extends WeServer<Web3j, Credentials, Service> {

    private static final Logger logger = LoggerFactory.getLogger(WeServerV2.class);

    private Web3j web3j;
    private Service service;
    private Credentials credentials;
    private CnsService cnsService;

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
    protected void initWeb3j(Integer groupId) {
        logger.info("[WeServiceImplV2] begin to init web3j instance..");
        service = buildFiscoBcosService(fiscoConfig, groupId);
        topicListener(groupId);
        try {
            service.run();
        } catch (Exception e) {
            logger.error("[WeServiceImplV2] Service init failed. ", e);
            throw new InitWeb3jException(e);
        }
        
        ChannelEthereumService channelEthereumService = WeServerUtils
            .buildChannelEthereumService(service);
        web3j = Web3j.build(channelEthereumService, service.getGroupId());
        if (web3j == null) {
            logger.error("[WeServiceImplV2] web3j init failed. ");
            throw new InitWeb3jException("web3j init failed.");
        }
        credentials = GenCredential.create();
        if (credentials == null) {
            logger.error("[WeServiceImplV2] the credentials for web3j init failed. ");
            throw new InitWeb3jException("the credentials for web3j init failed.");
        }
        cnsService = new CnsService(web3j, credentials);
        logger.info("[WeServiceImplV2] init web3j instance success..");
    }

    private void topicListener(Integer groupId) {
        // 如果为主群组
        if (fiscoConfig.getGroupId().equals(String.valueOf(groupId))) {
            service.setPushCallback((ChannelPushCallback) pushCallBack);
            // Set topics for AMOP
            service.setTopics(super.getTopic());
        }
    }

    private Service buildFiscoBcosService(FiscoConfig fiscoConfig, Integer groupId) {

        Service service = new Service();
        service.setOrgID(fiscoConfig.getCurrentOrgId());
        service.setConnectSeconds(Integer.valueOf(fiscoConfig.getWeb3sdkTimeout()));
        // group info
        service.setGroupId(groupId);
        
        // 根据群组获取节点列表
        List<String> nodeList = WeServerUtils.getGroupMapping().get(groupId.toString());
        if (CollectionUtils.isEmpty(nodeList)) {
            logger.error("[WeServiceImplV2] the groupId does not exist, please check.");
            throw new InitWeb3jException("the groupId does not exist, groupId = " + groupId + ".");
        }
        GroupChannelConnectionsConfig connectionsConfig = WeServerUtils
            .buildGroupChannelConnectionsConfig(groupId, fiscoConfig, nodeList);
        service.setAllChannelConnections(connectionsConfig);
        // thread pool params
        service.setThreadPool(super.initializePool(groupId));
        return service;
    }

    @Override
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
    }

    @Override
    public int getBlockNumber() throws IOException {
        BlockNumber response = this.getWeb3j().getBlockNumber().send();
        return response.getBlockNumber().intValue();
    }

    @Override
    public String getVersion() throws IOException {
        return this.getWeb3j().getNodeVersion().send().getNodeVersion().getVersion();
    }

    @Override
    protected CnsInfo queryCnsInfo(CnsType cnsType) throws WeIdBaseException {
        try {
            logger.info("[queryBucketFromCns] query address by type = {}.", cnsType.getName());
            List<CnsInfo> cnsInfoList = cnsService.queryCnsByName(cnsType.getName());
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
}
