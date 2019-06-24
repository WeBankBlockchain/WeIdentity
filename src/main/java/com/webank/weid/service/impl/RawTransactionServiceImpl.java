/*
 *       Copyright© (2019) WeBank Co., Ltd.
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

package com.webank.weid.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v1.AuthorityIssuerController;
import com.webank.weid.contract.v1.AuthorityIssuerController.AuthorityIssuerRetLogEventResponse;
import com.webank.weid.contract.v1.WeIdContract;
import com.webank.weid.contract.v1.WeIdContract.WeIdAttributeChangedEventResponse;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.RawTransactionService;
import com.webank.weid.service.BaseService;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.TransactionUtils;

/**
 * Service interface for operations on direct transactions on blockchain.
 *
 * @author chaoxinhu 2019.4
 */
public class RawTransactionServiceImpl extends BaseService implements RawTransactionService {

    private static final Logger logger = LoggerFactory.getLogger(RawTransactionServiceImpl.class);

    /**
     * Create a WeIdentity DID from the provided public key, with preset transaction hex value.
     *
     * @param transactionHex the transaction hex value
     * @return Error message if any
     */
    @Override
    public ResponseData<String> createWeId(String transactionHex) {
        try {
            if (StringUtils.isEmpty(transactionHex)) {
                logger.error("WeID transaction error");
                return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
            }
            TransactionReceipt transactionReceipt = TransactionUtils
                .sendTransaction(getWeb3j(), transactionHex);
            List<WeIdAttributeChangedEventResponse> response =
                WeIdContract.getWeIdAttributeChangedEvents(transactionReceipt);
            if (!CollectionUtils.isEmpty(response)) {
                return new ResponseData<>(Boolean.TRUE.toString(), ErrorCode.SUCCESS);
            }
        } catch (Exception e) {
            logger.error("[createWeId] create failed due to unknown transaction error. ", e);
        }
        return new ResponseData<>(StringUtils.EMPTY, ErrorCode.TRANSACTION_EXECUTE_ERROR);
    }

    /**
     * Register a new Authority Issuer on Chain with preset transaction hex value. The inputParam is
     * a Json String, with two keys: WeIdentity DID and Name. Parameters will be ordered as
     * mentioned after validity check; then transactionHex will be sent to blockchain.
     *
     * @param transactionHex the transaction hex value
     * @return true if succeeds, false otherwise
     */
    @Override
    public ResponseData<String> registerAuthorityIssuer(String transactionHex) {
        try {
            if (StringUtils.isEmpty(transactionHex)) {
                logger.error("AuthorityIssuer transaction error");
                return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
            }
            TransactionReceipt transactionReceipt = TransactionUtils
                .sendTransaction(getWeb3j(), transactionHex);

            List<AuthorityIssuerRetLogEventResponse> eventList =
                AuthorityIssuerController.getAuthorityIssuerRetLogEvents(transactionReceipt);
            AuthorityIssuerRetLogEventResponse event = eventList.get(0);
            ErrorCode errorCode = TransactionUtils.verifyAuthorityIssuerRelatedEvent(event,
                WeIdConstant.ADD_AUTHORITY_ISSUER_OPCODE);
            Boolean result = errorCode.getCode() == ErrorCode.SUCCESS.getCode();
            return new ResponseData<>(result.toString(), errorCode);
        } catch (Exception e) {
            logger.error("[registerAuthorityIssuer] register failed due to transaction error.", e);
        }
        return new ResponseData<>(StringUtils.EMPTY, ErrorCode.TRANSACTION_EXECUTE_ERROR);
    }


    /**
     * Register a new CPT to the blockchain with preset transaction hex value.
     *
     * @param transactionHex the transaction hex value
     * @return The registered CPT info
     */
    public ResponseData<String> registerCpt(String transactionHex) {
        try {
            if (StringUtils.isEmpty(transactionHex)) {
                logger.error("CptService transaction error");
                return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
            }
            TransactionReceipt transactionReceipt = TransactionUtils
                .sendTransaction(getWeb3j(), transactionHex);
            CptBaseInfo cptBaseInfo = TransactionUtils.resolveRegisterCptEvents(transactionReceipt)
                .getResult();
            if (cptBaseInfo != null) {
                return new ResponseData<>(DataToolUtils.objToJsonStrWithNoPretty(cptBaseInfo),
                    ErrorCode.SUCCESS);
            }
        } catch (Exception e) {
            logger.error("[registerCpt] register failed due to unknown transaction error. ", e);
        }
        return new ResponseData<>(StringUtils.EMPTY, ErrorCode.TRANSACTION_EXECUTE_ERROR);
    }
}