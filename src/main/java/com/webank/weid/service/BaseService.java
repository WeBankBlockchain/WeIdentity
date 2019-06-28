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

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.config.ContractConfig;
import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.amop.AmopRequestBody;
import com.webank.weid.protocol.amop.CheckAmopMsgHealthArgs;
import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;
import com.webank.weid.protocol.response.AmopNotifyMsgResult;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.callback.RegistCallBack;
import com.webank.weid.service.fisco.WeServer;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.util.DataToolUtils;

/**
 * The BaseService for other RPC classes.
 *
 * @author tonychen
 */
public abstract class BaseService {

    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    protected static FiscoConfig fiscoConfig;

    protected static WeServer<?, ?, ?> weServer;

    static {
        fiscoConfig = new FiscoConfig();
        if (!fiscoConfig.load()) {
            logger.error("[BaseService] Failed to load Fisco-BCOS blockchain node information.");
        }
        if (StringUtils.isEmpty(fiscoConfig.getCurrentOrgId())) {
            logger.error("[BaseService] the blockchain orgId is blank.");
        }
    }

    /**
     * Constructor.
     */
    public BaseService() {
        if (weServer == null) {
            init();
        }
    }

    /**
     * initialization WeServer.
     */
    protected static void init() {
        if (weServer == null) {
            weServer = WeServer.init(fiscoConfig);
        }
    }

    /**
     * Gets the web3j.
     *
     * @return the web3j
     */
    public static Object getWeb3j() {
        if (weServer == null) {
            init();
        }
        return weServer.getWeb3j();
    }

    /**
     * Gets the web3j class.
     *
     * @return the web3j
     */
    protected static Class<?> getWeb3jClass() {
        if (weServer == null) {
            init();
        }
        return weServer.getWeb3jClass();
    }

    /**
     * get current blockNumber.
     *
     * @return return blockNumber
     * @throws IOException possible exceptions to sending transactions
     */
    protected static int getBlockNumber() throws IOException {
        if (weServer == null) {
            init();
        }
        return weServer.getBlockNumber();
    }

    /**
     * Get the Sequence parameter.
     *
     * @return the seq
     */
    protected static String getSeq() {
        return DataToolUtils.getUuId32();
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

    /**
     * Get the RegistCallBack.
     *
     * @return the RegistCallBack
     */
    protected RegistCallBack getPushCallback() {
        if (weServer == null) {
            init();
        }
        return weServer.getPushCallback();
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
            fiscoConfig.getCurrentOrgId(),
            toOrgId,
            arg,
            CheckAmopMsgHealthArgs.class,
            AmopNotifyMsgResult.class,
            AmopMsgType.TYPE_CHECK_DIRECT_ROUTE_MSG_HEALTH,
            WeServer.AMOP_REQUEST_TIMEOUT
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

        String msgBody = DataToolUtils.serialize(arg);
        AmopRequestBody amopRequestBody = new AmopRequestBody();
        amopRequestBody.setMsgType(msgType);
        amopRequestBody.setMsgBody(msgBody);
        String requestBodyStr = DataToolUtils.serialize(amopRequestBody);

        AmopCommonArgs amopCommonArgs = new AmopCommonArgs();
        amopCommonArgs.setToOrgId(toOrgId);
        amopCommonArgs.setMessage(requestBodyStr);
        amopCommonArgs.setMessageId(getSeq());
        logger.info("direct route request, seq : {}, body ：{}", amopCommonArgs.getMessageId(),
            requestBodyStr);
        AmopResponse response = weServer.sendChannelMessage(amopCommonArgs, timeOut);
        logger.info("direct route response, seq : {}, errorCode : {}, body : {}",
            response.getMessageId(),
            response.getErrorCode(),
            response.getResult()
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
        T msgBodyObj = DataToolUtils.deserialize(response.getResult(), resultClass);
        if (null == msgBodyObj) {
            responseStruct.setErrorCode(ErrorCode.UNKNOW_ERROR);
        }
        responseStruct.setResult(msgBodyObj);
        return responseStruct;
    }

}