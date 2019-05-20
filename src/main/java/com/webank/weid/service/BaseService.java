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

package com.webank.weid.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.bcos.channel.client.Service;
import org.bcos.channel.dto.ChannelRequest;
import org.bcos.channel.dto.ChannelResponse;
import org.bcos.web3j.crypto.Credentials;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.GenCredential;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.bcos.web3j.tx.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.config.ContractConfig;
import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.exception.InitWeb3jException;
import com.webank.weid.exception.LoadContractException;
import com.webank.weid.exception.PrivateKeyIllegalException;
import com.webank.weid.protocol.amop.AmopRequestBody;
import com.webank.weid.protocol.amop.CheckAmopMsgHealthArgs;
import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;
import com.webank.weid.protocol.response.AmopNotifyMsgResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.callback.OnNotifyCallback;
import com.webank.weid.service.impl.callback.KeyManagerCallback;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.PropertyUtils;
import com.webank.weid.util.TransactionUtils;

/**
 * The BaseService for other RPC classes.
 *
 * @author tonychen
 */
public abstract class BaseService {

    /*
     * Maximum Timeout period in milliseconds.
     */
    public static final int MAX_AMOP_REQUEST_TIMEOUT = 50000;
    public static final int AMOP_REQUEST_TIMEOUT = Integer
        .valueOf(PropertyUtils.getProperty("amop.request.timeout", "5000"));
    protected static String fromOrgId = PropertyUtils.getProperty("blockchain.orgId");

    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    protected static FiscoConfig fiscoConfig;
    private static Credentials credentials;
    private static Web3j web3j;
    private static Service service;

    static {
        fiscoConfig = new FiscoConfig();
        if (!fiscoConfig.load()) {
            logger.error("[BaseService] Failed to load Fisco-BCOS blockchain node information.");
        }
    }

    /**
     * Constructor.
     */
    public BaseService() {
        if (web3j == null) {
            initWeb3j();
        }
    }

    private static boolean initWeb3j() {

        logger.info("[BaseService] begin to init web3j instance..");
        service = TransactionUtils.buildFiscoBcosService(fiscoConfig);

        String orgId = PropertyUtils.getProperty("blockchain.orgId");
        OnNotifyCallback pushCallBack = new OnNotifyCallback();
        service.setPushCallback(pushCallBack);
        pushCallBack.registAmopCallback(
            AmopMsgType.GET_ENCRYPT_KEY.getValue(),
            new KeyManagerCallback()
        );

        // Set topics for AMOP
        List<String> topics = new ArrayList<String>();
        topics.add(orgId);
        service.setTopics(topics);

        try {
            service.run();
        } catch (Exception e) {
            logger.error("[BaseService] Service init failed. ", e);
            throw new InitWeb3jException(e);
        }

        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        channelEthereumService.setChannelService(service);
        web3j = Web3j.build(channelEthereumService);
        if (web3j == null) {
            logger.error("[BaseService] web3j init failed. ");
            return false;
        }
        return true;
    }

    /**
     * Inits the credentials.
     *
     * @return true, if successful
     */
    private static boolean initCredentials() {
        logger.info("[BaseService] begin to init credentials..");
        credentials = GenCredential.create();

        if (credentials == null) {
            logger.error("[BaseService] credentials init failed. ");
            return false;
        }
        return true;
    }

    /**
     * Gets the web3j.
     *
     * @return the web3j
     */
    public static Web3j getWeb3j() {
        if (web3j == null && !initWeb3j()) {
            throw new InitWeb3jException();
        }
        return web3j;
    }

    /**
     * Get the service instance.
     *
     * @return the service
     */
    protected static Service getService() {
        return service;
    }

    /**
     * Get the Sequence parameter.
     *
     * @return the seq
     */
    private static String getSeq() {
        return service.newSeq();
    }

    /**
     * On-demand build the contract config info.
     *
     * @return the contractConfig instance
     */
    protected static ContractConfig buildContractConfig() {
        ContractConfig contractConfig = new ContractConfig();
        contractConfig.setWeIdAddress(fiscoConfig.getWeIdAddress());
        contractConfig.setCptAddress(fiscoConfig.getCptAddress());
        contractConfig.setIssuerAddress(fiscoConfig.getIssuerAddress());
        contractConfig.setEvidenceAddress(fiscoConfig.getEvidenceAddress());
        contractConfig.setSpecificIssuerAddress(fiscoConfig.getSpecificIssuerAddress());
        return contractConfig;
    }

    private static Object loadContract(
        String contractAddress,
        Credentials credentials,
        Class<?> cls) throws NoSuchMethodException, IllegalAccessException,
        InvocationTargetException {
        Object contract;
        Method method = cls.getMethod(
            "load",
            String.class,
            Web3j.class,
            Credentials.class,
            BigInteger.class,
            BigInteger.class
        );

        contract = method.invoke(
            null,
            contractAddress,
            getWeb3j(),
            credentials,
            WeIdConstant.GAS_PRICE,
            WeIdConstant.GAS_LIMIT
        );
        return contract;
    }

    /**
     * Reload contract.
     *
     * @param contractAddress the contract address
     * @param privateKey the privateKey of the sender
     * @param cls the class
     * @return the contract
     */
    protected static Contract reloadContract(
        String contractAddress,
        String privateKey,
        Class<?> cls) {
        Credentials credentials;
        try {
            ECKeyPair keyPair = ECKeyPair.create(new BigInteger(privateKey));
            credentials = Credentials.create(keyPair);
        } catch (Exception e) {
            throw new PrivateKeyIllegalException(e);
        }

        Object contract = null;
        try {
            // load contract
            contract = loadContract(contractAddress, credentials, cls);
            logger.info(cls.getSimpleName() + " init succ");
        } catch (Exception e) {
            logger.error("load contract :{} failed. Error message is :{}",
                cls.getSimpleName(), e);
            throw new LoadContractException();
        }

        if (contract == null) {
            throw new LoadContractException();
        }
        return (Contract) contract;
    }

    /**
     * Gets the contract service.
     *
     * @param contractAddress the contract address
     * @param cls the class
     * @return the contract service
     */
    protected static Contract getContractService(String contractAddress, Class<?> cls) {

        Object contract = null;
        try {
            // load contract
            if (credentials == null) {
                initCredentials();
            }
            contract = loadContract(contractAddress, credentials, cls);
            logger.info(cls.getSimpleName() + " init succ");

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("load contract :{} failed. Error message is :{}",
                cls.getSimpleName(), e);
            throw new LoadContractException();
        } catch (Exception e) {
            logger.error("load contract Exception:{} failed. Error message is :{}",
                cls.getSimpleName(), e);
            throw new LoadContractException();
        }

        if (contract == null) {
            throw new LoadContractException();
        }
        return (Contract) contract;
    }

    /**
     * the checkDirectRouteMsgHealth。.
     *
     * @param toOrgId target orgId.
     * @param arg the message
     * @return return the health result
     */
    public ResponseData<AmopNotifyMsgResult> checkDirectRouteMsgHealth(
        String toOrgId,
        CheckAmopMsgHealthArgs arg) {

        return this.getImpl(
            fromOrgId,
            toOrgId,
            arg,
            CheckAmopMsgHealthArgs.class,
            AmopNotifyMsgResult.class,
            AmopMsgType.TYPE_CHECK_DIRECT_ROUTE_MSG_HEALTH,
            AMOP_REQUEST_TIMEOUT
        );
    }

    protected <T, F extends AmopBaseMsgArgs> ResponseData<T> getImpl(
        String fromOrgId,
        String toOrgId,
        F arg,
        Class<F> argsClass,
        Class<T> resultClass,
        AmopMsgType msgType,
        int timeOut
    ) {

        arg.setFromOrgId(fromOrgId);
        arg.setToOrgId(toOrgId);

        ChannelRequest request = new ChannelRequest();
        if (timeOut > MAX_AMOP_REQUEST_TIMEOUT || timeOut < 0) {
            request.setTimeout(MAX_AMOP_REQUEST_TIMEOUT);
            logger.error("invalid timeOut : {}", timeOut);
        } else {
            request.setTimeout(timeOut);
        }
        request.setToTopic(toOrgId);
        request.setMessageID(getSeq());

        String msgBody = DataToolUtils.serialize(arg);
        AmopRequestBody amopRequestBody = new AmopRequestBody();
        amopRequestBody.setMsgType(msgType);
        amopRequestBody.setMsgBody(msgBody);
        String requestBodyStr = DataToolUtils.serialize(amopRequestBody);
        logger.info("direct route request, seq : {}, body ：{}", request.getMessageID(),
            requestBodyStr);
        request.setContent(requestBodyStr);

        ChannelResponse response = getService().sendChannelMessage2(request);
        logger.info("direct route response, seq : {}, errorCode : {}, body : {}",
            response.getMessageID(),
            response.getErrorCode(),
            response.getContent()
        );
        ResponseData<T> responseStruct = new ResponseData<>();
        if (102 == response.getErrorCode()) {
            responseStruct.setErrorCode(ErrorCode.DIRECT_ROUTE_REQUEST_TIMEOUT);
        } else if (0 != response.getErrorCode()) {
            responseStruct.setErrorCode(ErrorCode.DIRECT_ROUTE_MSG_BASE_ERROR);
            return responseStruct;
        } else {
            responseStruct.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        }
        T msgBodyObj = DataToolUtils.deserialize(response.getContent(), resultClass);
        if (null == msgBodyObj) {
            responseStruct.setErrorCode(ErrorCode.UNKNOW_ERROR);
        }
        responseStruct.setResult(msgBodyObj);
        return responseStruct;
    }

}