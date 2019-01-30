/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;
import org.bcos.web3j.protocol.core.DefaultBlockParameterNumber;
import org.bcos.web3j.protocol.core.methods.response.EthBlock;
import org.bcos.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.bcos.web3j.protocol.core.methods.response.Log;
import org.bcos.web3j.protocol.core.methods.response.Transaction;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.webank.weid.config.ContractConfig;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ResolveEventLogStatus;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.constant.WeIdEventConstant;
import com.webank.weid.contract.WeIdContract;
import com.webank.weid.contract.WeIdContract.WeIdAttributeChangedEventResponse;
import com.webank.weid.exception.DataTypeCastException;
import com.webank.weid.exception.PrivateKeyIllegalException;
import com.webank.weid.exception.ResolveAttributeException;
import com.webank.weid.protocol.base.AuthenticationProperty;
import com.webank.weid.protocol.base.PublicKeyProperty;
import com.webank.weid.protocol.base.ServiceProperty;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResolveEventLogResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.BaseService;
import com.webank.weid.util.DataTypetUtils;
import com.webank.weid.util.DateUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * Service implementations for operations on WeIdentity DID.
 *
 * @author tonychen 2018.10
 */
@Component
public class WeIdServiceImpl extends BaseService implements WeIdService {

    /**
     * log4j object, for recording log.
     */
    private static final Logger logger = LoggerFactory.getLogger(WeIdServiceImpl.class);

    /**
     * WeIdentity DID contract object, for calling weIdentity DID contract.
     */
    private static WeIdContract weIdContract;

    /**
     * WeIdentity DID contract address.
     */
    private static String weIdContractAddress;

    /**
     * The topic map.
     */
    private static final HashMap<String, String> topicMap;

    static {
        // initialize the event topic
        topicMap = new HashMap<String, String>();
        final Event event =
            new Event(
                WeIdEventConstant.WEID_EVENT_ATTRIBUTE_CHANGE,
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
                    }));
        topicMap.put(
            EventEncoder.encode(event),
            WeIdEventConstant.WEID_EVENT_ATTRIBUTE_CHANGE
        );
    }

    /**
     * Instantiates a new WeIdentity DID service.
     */
    public WeIdServiceImpl() {
        init();
    }

    private static void init() {

        // initialize the WeIdentity DID contract
        ContractConfig config = context.getBean(ContractConfig.class);
        weIdContractAddress = config.getWeIdAddress();
        weIdContract = (WeIdContract) getContractService(weIdContractAddress, WeIdContract.class);
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

        WeIdAttributeChangedEventResponse res = eventlog.get(0);
        if (res.identity == null || res.updated == null || res.previousBlock == null) {
            response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_RES_NULL);
            return response;
        }

        String identity = res.identity.toString();
        if (null == result.getUpdated()) {
            long timeStamp = res.updated.getValue().longValue();
            result.setUpdated(timeStamp);
        }
        String weAddress = WeIdUtils.convertWeIdToAddress(weId);
        if (!StringUtils.equals(weAddress, identity)) {
            response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_WEID_NOT_MATCH);
            return response;
        }

        String key = DataTypetUtils.bytes32ToString(res.key);
        String value = DataTypetUtils.dynamicBytesToString(res.value);
        int previousBlock = res.previousBlock.getValue().intValue();
        buildupWeIdAttribute(key, value, weId, result);

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
        for (PublicKeyProperty pr : pubkeyList) {
            if (StringUtils.contains(value, pr.getPublicKey())) {
                return;
            }
        }
        PublicKeyProperty pubKey = new PublicKeyProperty();
        pubKey.setId(
            new StringBuffer()
                .append(weId)
                .append("#keys-")
                .append(result.getPublicKey().size())
                .toString());
        String[] publicKeyData = StringUtils.splitByWholeSeparator(value, "/");
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
                case WeIdEventConstant.WEID_EVENT_ATTRIBUTE_CHANGE:
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

        if (blockNumber == 0) {
            return;
        }
        EthBlock latestBlock = null;
        try {
            latestBlock =
                getWeb3j().ethGetBlockByNumber(new DefaultBlockParameterNumber(blockNumber), true)
                    .send();
        } catch (IOException e) {
            logger.error(
                "[resolveTransaction]:get block by number :{} failed. Exception message:{}",
                blockNumber,
                e);
        }
        if (latestBlock == null) {
            logger.info(
                "[resolveTransaction]:get block by number :{} . latestBlock is null",
                blockNumber);
            return;
        }
        List<Transaction> transList =
            latestBlock
                .getBlock()
                .getTransactions()
                .stream()
                .map(transactionResult -> (Transaction) transactionResult.get())
                .collect(Collectors.toList());

        int previousBlock = 0;
        try {
            for (Transaction transaction : transList) {
                String transHash = transaction.getHash();

                EthGetTransactionReceipt rec1 = getWeb3j().ethGetTransactionReceipt(transHash)
                    .send();
                TransactionReceipt receipt = rec1.getTransactionReceipt().get();
                List<Log> logs = rec1.getResult().getLogs();
                for (Log log : logs) {
                    ResolveEventLogResult returnValue = resolveEventLog(weId, log, receipt, result);
                    if (returnValue.getResultStatus().equals(
                        ResolveEventLogStatus.STATUS_SUCCESS)) {
                        previousBlock = returnValue.getPreviousBlock();
                    }
                }
            }
        } catch (IOException | DataTypeCastException e) {
            logger.error(
                "[resolveTransaction]: get TransactionReceipt by weId :{} failed.",
                weId,
                e);
            throw new ResolveAttributeException(
                ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
                ErrorCode.TRANSACTION_EXECUTE_ERROR.getCodeDesc());
        }

        resolveTransaction(weId, previousBlock, result);
    }

    /**
     * Create a WeIdentity DID with null input param.
     *
     * @return the response data
     */
    @Override
    public ResponseData<CreateWeIdDataResult> createWeId() {

        CreateWeIdDataResult result = new CreateWeIdDataResult();
        ECKeyPair keyPair = null;

        try {
            keyPair = Keys.createEcKeyPair();
        } catch (Exception e) {
            logger.error("Create weId failed.", e);
            return new ResponseData<>(null, ErrorCode.WEID_KEYPAIR_CREATE_FAILED);
        }

        String publicKey = String.valueOf(keyPair.getPublicKey());
        String privateKey = String.valueOf(keyPair.getPrivateKey());
        WeIdPublicKey userWeIdPublicKey = new WeIdPublicKey();
        userWeIdPublicKey.setPublicKey(publicKey);
        result.setUserWeIdPublicKey(userWeIdPublicKey);
        WeIdPrivateKey userWeIdPrivateKey = new WeIdPrivateKey();
        userWeIdPrivateKey.setPrivateKey(privateKey);
        result.setUserWeIdPrivateKey(userWeIdPrivateKey);
        String weId = WeIdUtils.convertPublicKeyToWeId(publicKey);
        result.setWeId(weId);
        ResponseData<CreateWeIdDataResult> responseData = new ResponseData<CreateWeIdDataResult>();
        responseData.setResult(result);

        WeIdContract weIdContract = (WeIdContract) reloadContract(
            weIdContractAddress,
            privateKey,
            WeIdContract.class);
        Future<TransactionReceipt> future =
            weIdContract.setAttribute(
                new Address(WeIdUtils.convertWeIdToAddress(weId)),
                DataTypetUtils.stringToBytes32(WeIdConstant.WEID_DOC_CREATED),
                DataTypetUtils.stringToDynamicBytes(DateUtils.getCurrentTimeStampString()),
                DateUtils.getCurrentTimeStampInt256());

        try {
            TransactionReceipt receipt =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            List<WeIdAttributeChangedEventResponse> response =
                WeIdContract.getWeIdAttributeChangedEvents(receipt);
            if (CollectionUtils.isEmpty(response)) {
                logger.error(
                    "The input private key does not match the current weid, operation of "
                        + "modifying weid is not allowed.");
                return new ResponseData<>(null, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Set authenticate failed. Error message :{}", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (TimeoutException e) {
            return new ResponseData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (Exception e) {
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
        return responseData;
    }

    /**
     * Create a WeIdentity DID.
     *
     * @param createWeIdArgs the create WeIdentity DID args
     * @return the response data
     */
    @Override
    public ResponseData<String> createWeId(CreateWeIdArgs createWeIdArgs) {

        if (null == createWeIdArgs) {
            logger.error("[createWeId]: input parameter createWeIdArgs is null.");
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
        }
        ResponseData<String> responseData = new ResponseData<String>();
        if (!WeIdUtils.isPrivateKeyValid(createWeIdArgs.getWeIdPrivateKey())) {
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        String privateKey = createWeIdArgs.getWeIdPrivateKey().getPrivateKey();
        String publicKey = createWeIdArgs.getPublicKey();
        if (StringUtils.isNotBlank(publicKey)) {
            if (!WeIdUtils.isKeypairMatch(privateKey, publicKey)) {
                return new ResponseData<>(
                    StringUtils.EMPTY, ErrorCode.WEID_PUBLICKEY_AND_PRIVATEKEY_NOT_MATCHED);
            }
            String weId = WeIdUtils.convertPublicKeyToWeId(publicKey);
            ResponseData<Boolean> isWeIdExistResp = this.isWeIdExist(weId);
            if (null == isWeIdExistResp.getResult() || isWeIdExistResp.getResult()) {
                return new ResponseData<>(StringUtils.EMPTY, ErrorCode.WEID_ALREADY_EXIST);
            }
            responseData.setResult(weId);
            try {
                WeIdContract weIdContract = (WeIdContract) reloadContract(
                    weIdContractAddress,
                    privateKey,
                    WeIdContract.class);
                Future<TransactionReceipt> future =
                    weIdContract.setAttribute(
                        new Address(WeIdUtils.convertWeIdToAddress(weId)),
                        DataTypetUtils.stringToBytes32(WeIdConstant.WEID_DOC_CREATED),
                        DataTypetUtils.stringToDynamicBytes(DateUtils.getCurrentTimeStampString()),
                        DateUtils.getCurrentTimeStampInt256());

                TransactionReceipt receipt =
                    future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
                List<WeIdAttributeChangedEventResponse> response =
                    WeIdContract.getWeIdAttributeChangedEvents(receipt);
                if (CollectionUtils.isEmpty(response)) {
                    return new ResponseData<>(StringUtils.EMPTY,
                        ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH);
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("create weid failed. Error message :{}", e);
                return new ResponseData<>(StringUtils.EMPTY,
                    ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH);
            } catch (TimeoutException e) {
                return new ResponseData<>(StringUtils.EMPTY, ErrorCode.TRANSACTION_TIMEOUT);
            } catch (PrivateKeyIllegalException e) {
                return new ResponseData<>(StringUtils.EMPTY, e.getErrorCode());
            } catch (Exception e) {
                return new ResponseData<>(StringUtils.EMPTY, ErrorCode.UNKNOW_ERROR);
            }
        } else {
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.WEID_PUBLICKEY_INVALID);
        }
        return responseData;
    }

    /**
     * Get a WeIdentity DID Document.
     *
     * @param weId the WeIdentity DID
     * @return the WeIdentity DID document
     */
    @Override
    public ResponseData<WeIdDocument> getWeIdDocument(String weId) {

        WeIdDocument result = new WeIdDocument();
        result.setId(weId);

        if (!WeIdUtils.isWeIdValid(weId)) {
            logger.error("Input weId : {} is invalid.", weId);
            return new ResponseData<>(null, ErrorCode.WEID_INVALID);
        }

        ResponseData<WeIdDocument> responseData = new ResponseData<WeIdDocument>();
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
            responseData.setResult(result);
            return responseData;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Set weId service failed. Error message :{}", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (TimeoutException e) {
            return new ResponseData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (ResolveAttributeException e) {
            logger.error("[getWeIdDocument]: resolveTransaction failed. "
                    + "weId: {}, errorCode:{}",
                weId,
                e.getErrorCode(),
                e);
            responseData.setErrorCode(e.getErrorCode());
            responseData.setErrorMessage(e.getErrorMessage());
            return responseData;
        } catch (Exception e) {
            logger.error("[getWeIdDocument]: exception.", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * Get a WeIdentity DID Document Json.
     *
     * @param weId the WeIdentity DID
     * @return the WeIdentity DID document json
     */
    @Override
    public ResponseData<String> getWeIdDocumentJson(String weId) {

        ResponseData<WeIdDocument> responseData = this.getWeIdDocument(weId);
        WeIdDocument result = responseData.getResult();

        if (null == result) {
            return new ResponseData<>(
                StringUtils.EMPTY, responseData.getErrorCode(), responseData.getErrorMessage());
        }
        ObjectMapper mapper = new ObjectMapper();
        String weIdDocument;
        try {
            weIdDocument = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        } catch (Exception e) {
            return new ResponseData<>(
                StringUtils.EMPTY, responseData.getErrorCode(), responseData.getErrorMessage());
        }
        weIdDocument =
            new StringBuffer()
                .append(weIdDocument)
                .insert(1, WeIdConstant.WEID_DOC_PROTOCOL_VERSION)
                .toString();

        ResponseData<String> responseDataJson = new ResponseData<String>();
        responseDataJson.setResult(weIdDocument);
        responseDataJson.setErrorCode(responseData.getErrorCode());
        responseDataJson.setErrorMessage(responseData.getErrorMessage());

        return responseDataJson;
    }

    /**
     * Set Public Key.
     *
     * @param setPublicKeyArgs the set public key args
     * @return the response data
     */
    @Override
    public ResponseData<Boolean> setPublicKey(SetPublicKeyArgs setPublicKeyArgs) {

        if (!verifySetPublicKeyArgs(setPublicKeyArgs)) {
            logger.error("[setPublicKey]: input parameter setPublicKeyArgs is illegal.");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (!WeIdUtils.isPrivateKeyValid(setPublicKeyArgs.getUserWeIdPrivateKey())) {
            return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        String weId = setPublicKeyArgs.getWeId();
        String weAddress = WeIdUtils.convertWeIdToAddress(weId);
        if (StringUtils.isEmpty(weAddress)) {
            logger.error("setPublicKey: weId : {} is invalid.", weId);
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
        String owner = setPublicKeyArgs.getOwner();
        if (StringUtils.isEmpty(owner)) {
            owner = weAddress;
        } else {
            if (WeIdUtils.isWeIdValid(owner)) {
                owner = WeIdUtils.convertWeIdToAddress(owner);
            } else {
                logger.error("setPublicKey: owner : {} is invalid.", owner);
                return new ResponseData<>(false, ErrorCode.WEID_INVALID);
            }
        }
        String pubKey = setPublicKeyArgs.getPublicKey();
        String attributeKey =
            new StringBuffer()
                .append(WeIdConstant.WEID_DOC_PUBLICKEY_PREFIX)
                .append("/")
                .append(setPublicKeyArgs.getType())
                .append("/")
                .append("base64")
                .toString();
        String privateKey = setPublicKeyArgs.getUserWeIdPrivateKey().getPrivateKey();
        try {
            WeIdContract weIdContract = (WeIdContract) reloadContract(
                weIdContractAddress,
                privateKey,
                WeIdContract.class);
            Future<TransactionReceipt> future =
                weIdContract.setAttribute(
                    new Address(weAddress),
                    DataTypetUtils.stringToBytes32(attributeKey),
                    DataTypetUtils.stringToDynamicBytes(
                        new StringBuffer().append(pubKey).append("/").append(owner).toString()),
                    DateUtils.getCurrentTimeStampInt256());
            TransactionReceipt receipt =
                future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            List<WeIdAttributeChangedEventResponse> response =
                WeIdContract.getWeIdAttributeChangedEvents(receipt);
            if (CollectionUtils.isNotEmpty(response)) {
                return new ResponseData<>(true, ErrorCode.SUCCESS);
            } else {
                return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Set public key failed. Error message :{}", e);
            return new ResponseData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (TimeoutException e) {
            return new ResponseData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (PrivateKeyIllegalException e) {
            return new ResponseData<>(false, e.getErrorCode());
        } catch (Exception e) {
            return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
        }
    }

    private boolean verifySetPublicKeyArgs(SetPublicKeyArgs setPublicKeyArgs) {

        return !(null == setPublicKeyArgs
            || null == setPublicKeyArgs.getType()
            || null == setPublicKeyArgs.getUserWeIdPrivateKey()
            || null == setPublicKeyArgs.getPublicKey());
    }

    /**
     * Set Service.
     *
     * @param setServiceArgs the set service args
     * @return the response data
     */
    @Override
    public ResponseData<Boolean> setService(SetServiceArgs setServiceArgs) {

        if (!verifySetServiceArgs(setServiceArgs)) {
            logger.error("[setService]: input parameter setServiceArgs is illegal.");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (!WeIdUtils.isPrivateKeyValid(setServiceArgs.getUserWeIdPrivateKey())) {
            return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        String weId = setServiceArgs.getWeId();
        String serviceType = setServiceArgs.getType();
        String serviceEndpoint = setServiceArgs.getServiceEndpoint();
        if (WeIdUtils.isWeIdValid(weId)) {
            String privateKey = setServiceArgs.getUserWeIdPrivateKey().getPrivateKey();
            try {
                WeIdContract weIdContract = (WeIdContract) reloadContract(
                    weIdContractAddress,
                    privateKey,
                    WeIdContract.class);
                Future<TransactionReceipt> future =
                    weIdContract.setAttribute(
                        new Address(WeIdUtils.convertWeIdToAddress(weId)),
                        DataTypetUtils.stringToBytes32(
                            WeIdConstant.WEID_DOC_SERVICE_PREFIX + "/" + serviceType),
                        DataTypetUtils.stringToDynamicBytes(serviceEndpoint),
                        DateUtils.getCurrentTimeStampInt256());

                TransactionReceipt receipt =
                    future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
                List<WeIdAttributeChangedEventResponse> response =
                    WeIdContract.getWeIdAttributeChangedEvents(receipt);
                if (CollectionUtils.isNotEmpty(response)) {
                    return new ResponseData<>(true, ErrorCode.SUCCESS);
                } else {
                    return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH);
                }
            } catch (InterruptedException | ExecutionException e) {
                return new ResponseData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
            } catch (TimeoutException e) {
                return new ResponseData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
            } catch (PrivateKeyIllegalException e) {
                return new ResponseData<>(false, e.getErrorCode());
            } catch (Exception e) {
                logger.error("Set weId service failed. Error message :{}", e);
                return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
            }
        } else {
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
    }

    private boolean verifySetServiceArgs(SetServiceArgs setServiceArgs) {

        return !(null == setServiceArgs
            || null == setServiceArgs.getType()
            || null == setServiceArgs.getUserWeIdPrivateKey()
            || null == setServiceArgs.getServiceEndpoint());
    }

    /**
     * Set Authentication.
     *
     * @param setAuthenticationArgs the set authentication args
     * @return the response data
     */
    @Override
    public ResponseData<Boolean> setAuthentication(SetAuthenticationArgs setAuthenticationArgs) {

        if (!verifySetAuthenticationArgs(setAuthenticationArgs)) {
            logger.error("[setAuthentication]: input parameter setAuthenticationArgs is illegal.");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (!WeIdUtils.isPrivateKeyValid(setAuthenticationArgs.getUserWeIdPrivateKey())) {
            return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        String weId = setAuthenticationArgs.getWeId();
        if (WeIdUtils.isWeIdValid(weId)) {
            String weAddress = WeIdUtils.convertWeIdToAddress(weId);

            String owner = setAuthenticationArgs.getOwner();
            if (StringUtils.isEmpty(owner)) {
                owner = weAddress;
            } else {
                if (WeIdUtils.isWeIdValid(owner)) {
                    owner = WeIdUtils.convertWeIdToAddress(owner);
                } else {
                    logger.error("setPublicKey: owner : {} is invalid.", owner);
                    return new ResponseData<>(false, ErrorCode.WEID_INVALID);
                }
            }
            String privateKey = setAuthenticationArgs.getUserWeIdPrivateKey().getPrivateKey();
            try {
                WeIdContract weIdContract = (WeIdContract) reloadContract(
                    weIdContractAddress,
                    privateKey,
                    WeIdContract.class);
                Future<TransactionReceipt> future =
                    weIdContract.setAttribute(
                        new Address(weAddress),
                        DataTypetUtils.stringToBytes32(WeIdConstant.WEID_DOC_AUTHENTICATE_PREFIX),
                        DataTypetUtils.stringToDynamicBytes(
                            new StringBuffer()
                                .append(setAuthenticationArgs.getPublicKey())
                                .append("/")
                                .append(owner)
                                .toString()),
                        DateUtils.getCurrentTimeStampInt256());
                TransactionReceipt receipt =
                    future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
                List<WeIdAttributeChangedEventResponse> response =
                    WeIdContract.getWeIdAttributeChangedEvents(receipt);
                if (CollectionUtils.isNotEmpty(response)) {
                    return new ResponseData<>(true, ErrorCode.SUCCESS);
                } else {
                    return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH);
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Set authenticate failed. Error message :{}", e);
                return new ResponseData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
            } catch (TimeoutException e) {
                return new ResponseData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
            } catch (PrivateKeyIllegalException e) {
                return new ResponseData<>(false, e.getErrorCode());
            } catch (Exception e) {
                return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
            }
        } else {
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
    }

    private boolean verifySetAuthenticationArgs(SetAuthenticationArgs setAuthenticationArgs) {

        return !(null == setAuthenticationArgs
            || null == setAuthenticationArgs.getType()
            || null == setAuthenticationArgs.getUserWeIdPrivateKey()
            || StringUtils.isEmpty(setAuthenticationArgs.getPublicKey()));
    }

    /**
     * Check if WeIdentity DID exists on Chain.
     *
     * @param weId the WeIdentity DID
     * @return true if exists, false otherwise
     */
    @Override
    public ResponseData<Boolean> isWeIdExist(String weId) {
        if (!WeIdUtils.isWeIdValid(weId)) {
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
        try {
            Bool isExist = weIdContract
                .isIdentityExist(new Address(WeIdUtils.convertWeIdToAddress(weId)))
                .get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            Boolean result = isExist.getValue();
            return new ResponseData<>(result, ErrorCode.SUCCESS);
        } catch (InterruptedException | ExecutionException e1) {
            return new ResponseData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (TimeoutException e) {
            return new ResponseData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (Exception e) {
            return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
        }
    }
}
