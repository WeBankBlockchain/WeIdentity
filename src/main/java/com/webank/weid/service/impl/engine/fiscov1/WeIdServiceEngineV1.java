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

package com.webank.weid.service.impl.engine.fiscov1;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.EventEncoder;
import org.bcos.web3j.abi.TypeReference;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.Bool;
import org.bcos.web3j.abi.datatypes.DynamicBytes;
import org.bcos.web3j.abi.datatypes.Event;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.core.DefaultBlockParameterNumber;
import org.bcos.web3j.protocol.core.methods.response.EthBlock;
import org.bcos.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.bcos.web3j.protocol.core.methods.response.Log;
import org.bcos.web3j.protocol.core.methods.response.Transaction;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ResolveEventLogStatus;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v1.WeIdContract;
import com.webank.weid.contract.v1.WeIdContract.WeIdAttributeChangedEventResponse;
import com.webank.weid.exception.DataTypeCastException;
import com.webank.weid.exception.ResolveAttributeException;
import com.webank.weid.protocol.base.AuthenticationProperty;
import com.webank.weid.protocol.base.PublicKeyProperty;
import com.webank.weid.protocol.base.ServiceProperty;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.response.ResolveEventLogResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.TransactionInfo;
import com.webank.weid.service.impl.engine.BaseEngine;
import com.webank.weid.service.impl.engine.WeIdServiceEngine;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.DateUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * WeIdServiceEngine calls the weid contract which runs on FISCO BCOS 1.3.x version.
 *
 * @author tonychen 2019年6月21日
 */
public class WeIdServiceEngineV1 extends BaseEngine implements WeIdServiceEngine {

    /**
     * Block number for stopping parsing.
     */
    private static final int STOP_RESOLVE_BLOCK_NUMBER = 0;
    /**
     * WeIdentity DID contract address.
     */

    private static final Logger logger = LoggerFactory.getLogger(WeIdServiceEngineV1.class);
    /**
     * The topic map.
     */
    private static final HashMap<String, String> topicMap;
    /**
     * WeIdentity DID contract object, for calling weIdentity DID contract.
     */
    private static WeIdContract weIdContract;

    static {
        // initialize the event topic
        topicMap = new HashMap<String, String>();
        final Event event =
            new Event(
                WeIdConstant.WEID_EVENT_ATTRIBUTE_CHANGE,
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }),
                Arrays.<TypeReference<?>>asList(
                    new TypeReference<Bytes32>() {
                    },
                    new TypeReference<DynamicBytes>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Int256>() {
                    })
            );
        topicMap.put(
            EventEncoder.encode(event),
            WeIdConstant.WEID_EVENT_ATTRIBUTE_CHANGE
        );
    }

    /**
     * 构造函数.
     */
    public WeIdServiceEngineV1() {
        if (weIdContract == null) {
            reload();
        }
    }

    private static ResolveEventLogResult resolveAttributeEvent(
        String weId,
        TransactionReceipt receipt,
        WeIdDocument result) {

        List<WeIdAttributeChangedEventResponse> eventlog =
            WeIdContract.getWeIdAttributeChangedEvents(receipt);
        ResolveEventLogResult response = new ResolveEventLogResult();

        if (CollectionUtils.isEmpty(eventlog)) {
            response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_EVENTLOG_NULL);
            return response;
        }

        int previousBlock = 0;
        for (WeIdAttributeChangedEventResponse res : eventlog) {
            if (res.identity == null || res.updated == null || res.previousBlock == null) {
                response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_RES_NULL);
                return response;
            }

            String identity = res.identity.toString();
            if (result.getUpdated() == null) {
                long timeStamp = res.updated.getValue().longValue();
                result.setUpdated(timeStamp);
            }
            String weAddress = WeIdUtils.convertWeIdToAddress(weId);
            if (!StringUtils.equals(weAddress, identity)) {
                response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_KEY_NOT_MATCH);
                return response;
            }

            String key = DataToolUtils.bytes32ToString(res.key);
            String value = DataToolUtils.dynamicBytesToString(res.value);
            previousBlock = res.previousBlock.getValue().intValue();
            buildupWeIdAttribute(key, value, weId, result);
        }

        response.setPreviousBlock(previousBlock);
        response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_SUCCESS);
        return response;
    }

    private static void buildupWeIdAttribute(
        String key, String value, String weId, WeIdDocument result) {
        if (StringUtils.startsWith(key, WeIdConstant.WEID_DOC_PUBLICKEY_PREFIX)) {
            buildWeIdPublicKeys(value, weId, result);
        } else if (StringUtils.startsWith(key, WeIdConstant.WEID_DOC_AUTHENTICATE_PREFIX)) {
            buildWeIdPublicKeys(value, weId, result);
            buildWeIdAuthentication(value, weId, result);
        } else if (StringUtils.startsWith(key, WeIdConstant.WEID_DOC_SERVICE_PREFIX)) {
            buildWeIdService(key, value, weId, result);
        } else {
            buildWeIdAttributeDefault(key, value, weId, result);
        }
    }

    private static void buildWeIdPublicKeys(String value, String weId, WeIdDocument result) {

        logger.info("method buildWeIdPublicKeys() parameter::value:{}, weId:{}, "
            + "result:{}", value, weId, result);
        List<PublicKeyProperty> pubkeyList = result.getPublicKey();
        // Only store the latest public key
        // OBSOLETE and non-OBSOLETE public keys are regarded as the same
        String trimmedPubKey = StringUtils
            .splitByWholeSeparator(value.replace(
                WeIdConstant.REMOVED_PUBKEY_TAG, ""), WeIdConstant.SEPARATOR)[0];
        for (PublicKeyProperty pr : pubkeyList) {
            if (pr.getPublicKey().contains(trimmedPubKey)) {
                return;
            }
        }
        PublicKeyProperty pubKey = new PublicKeyProperty();
        pubKey.setId(
            new StringBuffer()
                .append(weId)
                .append("#keys-")
                .append(result.getPublicKey().size())
                .toString()
        );
        String[] publicKeyData = StringUtils.splitByWholeSeparator(value, WeIdConstant.SEPARATOR);
        if (publicKeyData != null && publicKeyData.length == 2) {
            pubKey.setPublicKey(publicKeyData[0]);
            String weAddress = publicKeyData[1];
            String owner = WeIdUtils.convertAddressToWeId(weAddress);
            pubKey.setOwner(owner);
        }
        result.getPublicKey().add(pubKey);
    }

    private static void buildWeIdAuthentication(String value, String weId, WeIdDocument result) {

        logger.info("method buildWeIdAuthentication() parameter::value:{}, weId:{}, "
            + "result:{}", value, weId, result);
        AuthenticationProperty auth = new AuthenticationProperty();
        List<PublicKeyProperty> keys = result.getPublicKey();
        List<AuthenticationProperty> authList = result.getAuthentication();

        // Firstly, if this is an obsolete auth, directly append it and return unless a same
        // one exists; if this is a normal auth, then check whether there is an existing obsolete
        // one. if so, return. if not, go down further.
        if (value.contains(WeIdConstant.REMOVED_AUTHENTICATION_TAG)) {
            for (AuthenticationProperty ap : authList) {
                String pubKeyId = ap.getPublicKey();
                for (PublicKeyProperty pkp : keys) {
                    if (pubKeyId.equalsIgnoreCase(pkp.getId()) && value
                        .contains(pkp.getPublicKey())) {
                        return;
                    }
                }
            }
            auth.setPublicKey(value);
            result.getAuthentication().add(auth);
        } else {
            for (AuthenticationProperty ap : authList) {
                if (ap.getPublicKey()
                    .replace(WeIdConstant.REMOVED_AUTHENTICATION_TAG, "")
                    .contains(value) && ap
                    .getPublicKey()
                    .contains(WeIdConstant.REMOVED_AUTHENTICATION_TAG)) {
                    return;
                }
            }
        }

        for (PublicKeyProperty r : keys) {
            if (StringUtils.contains(value, r.getPublicKey())) {
                for (AuthenticationProperty ar : authList) {
                    if (StringUtils.equals(ar.getPublicKey(), r.getId())) {
                        return;
                    }
                }
                auth.setPublicKey(r.getId());
                result.getAuthentication().add(auth);
            }
        }
    }

    private static void buildWeIdService(String key, String value, String weId,
        WeIdDocument result) {

        logger.info("method buildWeIdService() parameter::key{}, value:{}, weId:{}, "
            + "result:{}", key, value, weId, result);
        String service = StringUtils.splitByWholeSeparator(key, "/")[2];
        List<ServiceProperty> serviceList = result.getService();
        for (ServiceProperty sr : serviceList) {
            if (StringUtils.equals(service, sr.getType())) {
                return;
            }
        }
        ServiceProperty serviceResult = new ServiceProperty();
        serviceResult.setType(service);
        serviceResult.setServiceEndpoint(value);
        result.getService().add(serviceResult);
    }

    private static void buildWeIdAttributeDefault(
        String key, String value, String weId, WeIdDocument result) {

        logger.info("method buildWeIdAttributeDefault() parameter::key{}, value:{}, weId:{}, "
            + "result:{}", key, value, weId, result);
        switch (key) {
            case WeIdConstant.WEID_DOC_CREATED:
                result.setCreated(Long.valueOf(value));
                break;
            default:
                break;
        }
    }

    private static ResolveEventLogResult resolveEventLog(
        String weId, Log log, TransactionReceipt receipt, WeIdDocument result) {
        String topic = log.getTopics().get(0);
        String event = topicMap.get(topic);

        if (StringUtils.isNotBlank(event)) {
            switch (event) {
                case WeIdConstant.WEID_EVENT_ATTRIBUTE_CHANGE:
                    return resolveAttributeEvent(weId, receipt, result);
                default:
            }
        }
        ResolveEventLogResult response = new ResolveEventLogResult();
        response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_EVENT_NULL);
        return response;
    }

    private static void resolveTransaction(
        String weId,
        int blockNumber,
        WeIdDocument result) {

        int previousBlock = blockNumber;
        while (previousBlock != STOP_RESOLVE_BLOCK_NUMBER) {
            int currentBlockNumber = previousBlock;
            EthBlock latestBlock = null;
            try {
                latestBlock =
                    ((Web3j) getWeb3j())
                        .ethGetBlockByNumber(
                            new DefaultBlockParameterNumber(currentBlockNumber),
                            true
                        )
                        .send();
            } catch (IOException e) {
                logger.error(
                    "[resolveTransaction]:get block by number :{} failed. Exception message:{}",
                    currentBlockNumber,
                    e
                );
            }
            if (latestBlock == null) {
                logger.info(
                    "[resolveTransaction]:get block by number :{} . latestBlock is null",
                    currentBlockNumber
                );
                return;
            }
            List<Transaction> transList =
                latestBlock
                    .getBlock()
                    .getTransactions()
                    .stream()
                    .map(transactionResult -> (Transaction) transactionResult.get())
                    .collect(Collectors.toList());

            previousBlock = 0;
            try {
                for (Transaction transaction : transList) {
                    String transHash = transaction.getHash();

                    EthGetTransactionReceipt rec1 = ((Web3j) getWeb3j())
                        .ethGetTransactionReceipt(transHash)
                        .send();
                    TransactionReceipt receipt = rec1.getTransactionReceipt().get();
                    List<Log> logs = rec1.getResult().getLogs();
                    for (Log log : logs) {
                        ResolveEventLogResult returnValue =
                            resolveEventLog(weId, log, receipt, result);
                        if (returnValue.getResultStatus().equals(
                            ResolveEventLogStatus.STATUS_SUCCESS)) {
                            if (returnValue.getPreviousBlock() == currentBlockNumber) {
                                continue;
                            }
                            previousBlock = returnValue.getPreviousBlock();
                        }
                    }
                }
            } catch (IOException | DataTypeCastException e) {
                logger.error(
                    "[resolveTransaction]: get TransactionReceipt by weId :{} failed.",
                    weId,
                    e
                );
                throw new ResolveAttributeException(
                    ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
                    ErrorCode.TRANSACTION_EXECUTE_ERROR.getCodeDesc());
            }
        }
    }

    /**
     * 重新加载静态合约对象.
     */
    public void reload() {
        weIdContract = getContractService(fiscoConfig.getWeIdAddress(), WeIdContract.class);
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.WeIdController#isWeIdExist(java.lang.String)
     */
    @Override
    public ResponseData<Boolean> isWeIdExist(String weId) {
        try {
            Bool isExist = weIdContract
                .isIdentityExist(new Address(WeIdUtils.convertWeIdToAddress(weId)))
                .get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            Boolean result = isExist.getValue();
            return new ResponseData<>(result, ErrorCode.SUCCESS);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("[isWeIdExist] execute failed. Error message :{}", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (TimeoutException e) {
            logger.error("[isWeIdExist] execute with timeout. Error message :{}", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (Exception e) {
            logger.error("[isWeIdExist] execute failed. Error message :{}", e);
            return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.WeIdController#getWeIdDocument(java.lang.String)
     */
    @Override
    public ResponseData<WeIdDocument> getWeIdDocument(String weId) {
        WeIdDocument result = new WeIdDocument();
        result.setId(weId);
        int latestBlockNumber = 0;
        try {
            String identityAddr = WeIdUtils.convertWeIdToAddress(weId);
            latestBlockNumber =
                weIdContract
                    .getLatestRelatedBlock(new Address(identityAddr))
                    .get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS)
                    .getValue()
                    .intValue();
            if (0 == latestBlockNumber) {
                return new ResponseData<>(null, ErrorCode.WEID_DOES_NOT_EXIST);
            }

            resolveTransaction(weId, latestBlockNumber, result);
            return new ResponseData<>(result, ErrorCode.SUCCESS);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Set weId service failed. Error message :{}", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (TimeoutException e) {
            logger.error("Set weId service timeout. Error message :{}", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (ResolveAttributeException e) {
            logger.error("[getWeIdDocument]: resolveTransaction failed. "
                    + "weId: {}, errorCode:{}",
                weId,
                e.getErrorCode(),
                e);
            return new ResponseData<>(null, ErrorCode.getTypeByErrorCode(e.getErrorCode()));
        } catch (Exception e) {
            logger.error("[getWeIdDocument]: exception.", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }


    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.WeIdController
     * #createWeId(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override

    public ResponseData<Boolean> createWeId(
        String weAddress,
        String publicKey,
        String privateKey,
        boolean isDelegate) {

        WeIdContract weIdContract = (WeIdContract) reloadContract(
            fiscoConfig.getWeIdAddress(),
            privateKey,
            WeIdContract.class);
        try {
            DynamicBytes auth = DataToolUtils.stringToDynamicBytes(
                new StringBuffer()
                    .append(publicKey)
                    .append(WeIdConstant.SEPARATOR)
                    .append(weAddress)
                    .toString());
            DynamicBytes created = DataToolUtils
                .stringToDynamicBytes(DateUtils.getNoMillisecondTimeStampString());

            Future<TransactionReceipt> future = null;
            if (isDelegate) {
                return new ResponseData<>(false, ErrorCode.FISCO_BCOS_VERSION_NOT_SUPPORTED);
            } else {
                future = weIdContract.createWeId(
                    new Address(weAddress),
                    auth,
                    created,
                    DateUtils.getNoMillisecondTimeStampInt256()
                );
            }
            TransactionReceipt receipt =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            TransactionInfo info = new TransactionInfo(receipt);
            List<WeIdAttributeChangedEventResponse> response =
                WeIdContract.getWeIdAttributeChangedEvents(receipt);

            if (CollectionUtils.isEmpty(response)) {
                logger.error(
                    "The input private key does not match the current weid, operation of "
                        + "modifying weid is not allowed. we address is {}",
                    weAddress
                );
                return new ResponseData<Boolean>(false,
                    ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH,
                    info);
            }
            return new ResponseData<Boolean>(true, ErrorCode.SUCCESS, info);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Set public key failed. Error message :{}", e);
            return new ResponseData<Boolean>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (TimeoutException e) {
            logger.error("Set public key timeout. Error message :{}", e);
            return new ResponseData<Boolean>(false, ErrorCode.TRANSACTION_TIMEOUT);
        }
    }


    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.WeIdController
     * #setAttribute(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ResponseData<Boolean> setAttribute(
        String weAddress,
        String attributeKey,
        String value,
        String privateKey,
        boolean isDelegate) {

        try {
            WeIdContract weIdContract = (WeIdContract) reloadContract(
                fiscoConfig.getWeIdAddress(),
                privateKey,
                WeIdContract.class);
            Future<TransactionReceipt> future = null;
            if (isDelegate) {
                return new ResponseData<>(false, ErrorCode.FISCO_BCOS_VERSION_NOT_SUPPORTED);
            } else {
                future =
                    weIdContract.setAttribute(
                        new Address(weAddress),
                        DataToolUtils.stringToBytes32(attributeKey),
                        DataToolUtils.stringToDynamicBytes(
                            value),
                        DateUtils.getNoMillisecondTimeStampInt256()
                    );
            }
            TransactionReceipt receipt =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            TransactionInfo info = new TransactionInfo(receipt);
            List<WeIdAttributeChangedEventResponse> response =
                WeIdContract.getWeIdAttributeChangedEvents(receipt);
            if (CollectionUtils.isNotEmpty(response)) {
                return new ResponseData<Boolean>(true, ErrorCode.SUCCESS, info);
            } else {
                return new ResponseData<Boolean>(false,
                    ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH,
                    info);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Set public key failed. Error message :{}", e);
            return new ResponseData<Boolean>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (TimeoutException e) {
            logger.error("Set public key timeout. Error message :{}", e);
            return new ResponseData<Boolean>(false, ErrorCode.TRANSACTION_TIMEOUT);
        }
    }
}
