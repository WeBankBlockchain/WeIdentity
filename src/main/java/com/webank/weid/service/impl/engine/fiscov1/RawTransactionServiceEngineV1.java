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

package com.webank.weid.service.impl.engine.fiscov1;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.bcos.web3j.protocol.core.methods.response.EthSendTransaction;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.bcos.web3j.protocol.exceptions.TransactionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v1.AuthorityIssuerController;
import com.webank.weid.contract.v1.AuthorityIssuerController.AuthorityIssuerRetLogEventResponse;
import com.webank.weid.contract.v1.CptController;
import com.webank.weid.contract.v1.CptController.RegisterCptRetLogEventResponse;
import com.webank.weid.contract.v1.WeIdContract;
import com.webank.weid.contract.v1.WeIdContract.WeIdAttributeChangedEventResponse;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.RawTransactionServiceImpl;
import com.webank.weid.service.impl.engine.BaseEngine;
import com.webank.weid.service.impl.engine.RawTransactionServiceEngine;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.TransactionUtils;

/**
 * awTransactionService runs on FISCO BCOS 1.3.x.
 * @author tonychen 2019年6月26日
 */
public class RawTransactionServiceEngineV1 extends BaseEngine implements
    RawTransactionServiceEngine {


    private static final Logger logger = LoggerFactory.getLogger(RawTransactionServiceImpl.class);

    /**
     * Verify Authority Issuer related events.
     *
     * @param event the Event
     * @param opcode the Opcode
     * @return the ErrorCode
     */
    public static ErrorCode verifyAuthorityIssuerRelatedEvent(
        AuthorityIssuerRetLogEventResponse event,
        Integer opcode) {
        if (event == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        if (event.addr == null || event.operation == null || event.retCode == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        Integer eventOpcode = event.operation.getValue().intValue();
        if (eventOpcode.equals(opcode)) {
            Integer eventRetCode = event.retCode.getValue().intValue();
            return ErrorCode.getTypeByErrorCode(eventRetCode);
        } else {
            return ErrorCode.AUTHORITY_ISSUER_OPCODE_MISMATCH;
        }
    }

    /**
     * Send a transaction to blockchain through web3j instance using the transactionHex value.
     *
     * @param transactionHex the transactionHex value
     * @return the transactionReceipt
     * @throws Exception the exception
     */
    public static TransactionReceipt sendTransaction(String transactionHex)
        throws Exception {
        Web3j web3j = (Web3j) getWeb3j();
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(transactionHex)
            .sendAsync().get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
        if (ethSendTransaction.hasError()) {
            logger.error("Error processing transaction request: "
                + ethSendTransaction.getError().getMessage());
            return null;
        }
        Optional<TransactionReceipt> receiptOptional =
            getTransactionReceiptRequest(web3j, ethSendTransaction.getTransactionHash());
        int sumTime = 0;
        try {
            for (int i = 0; i < WeIdConstant.POLL_TRANSACTION_ATTEMPTS; i++) {
                if (!receiptOptional.isPresent()) {
                    Thread.sleep((long) WeIdConstant.POLL_TRANSACTION_SLEEP_DURATION);
                    sumTime += WeIdConstant.POLL_TRANSACTION_SLEEP_DURATION;
                    receiptOptional = getTransactionReceiptRequest(web3j,
                        ethSendTransaction.getTransactionHash());
                } else {
                    return receiptOptional.get();
                }
            }
        } catch (Exception e) {
            throw new TransactionTimeoutException("Transaction receipt was not generated after "
                + ((sumTime) / 1000
                + " seconds for transaction: " + ethSendTransaction));
        }
        return null;
    }

    /**
     * Get a TransactionReceipt request from a transaction Hash.
     *
     * @param web3j the web3j instance to blockchain
     * @param transactionHash the transactionHash value
     * @return the transactionReceipt wrapper
     * @throws Exception the exception
     */
    private static Optional<TransactionReceipt> getTransactionReceiptRequest(Web3j web3j,
        String transactionHash) throws Exception {

        EthGetTransactionReceipt transactionReceipt =
            web3j.ethGetTransactionReceipt(transactionHash).send();
        if (transactionReceipt.hasError()) {
            logger.error("Error processing transaction request: "
                + transactionReceipt.getError().getMessage());
            return Optional.empty();
        }
        return transactionReceipt.getTransactionReceipt();
    }

    /**
     * Verify Register CPT related events.
     *
     * @param transactionReceipt the TransactionReceipt
     * @return the ErrorCode
     */
    public static ResponseData<CptBaseInfo> resolveRegisterCptEvents(
        TransactionReceipt transactionReceipt) {

        List<RegisterCptRetLogEventResponse> event = CptController.getRegisterCptRetLogEvents(
            transactionReceipt
        );

        if (CollectionUtils.isEmpty(event)) {
            logger.error("[registerCpt] event is empty");
            return new ResponseData<>(null, ErrorCode.CPT_EVENT_LOG_NULL);
        }

        return TransactionUtils.getResultByResolveEvent(
            event.get(0).retCode,
            event.get(0).cptId,
            event.get(0).cptVersion,
            transactionReceipt
        );
    }

    /**
     * Create a WeIdentity DID from the provided public key, with preset transaction hex value.
     *
     * @param transactionHex the transaction hex value
     * @return Error message if any
     */
    @Override
    public ResponseData<String> createWeId(String transactionHex) {

        try {
            TransactionReceipt transactionReceipt = sendTransaction(transactionHex);
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
            TransactionReceipt transactionReceipt = sendTransaction(transactionHex);

            List<AuthorityIssuerRetLogEventResponse> eventList =
                AuthorityIssuerController.getAuthorityIssuerRetLogEvents(transactionReceipt);
            AuthorityIssuerRetLogEventResponse event = eventList.get(0);
            ErrorCode errorCode = verifyAuthorityIssuerRelatedEvent(event,
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
            TransactionReceipt transactionReceipt = sendTransaction(transactionHex);
            CptBaseInfo cptBaseInfo = resolveRegisterCptEvents(transactionReceipt)
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
