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

package com.webank.weid.service.impl;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.weid.config.ContractConfig;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v1.WeIdContract;
import com.webank.weid.contract.v1.WeIdContract.WeIdAttributeChangedEventResponse;
import com.webank.weid.exception.PrivateKeyIllegalException;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.EngineResultData;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.TransactionInfo;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.BaseService;
import com.webank.weid.service.impl.engine.WeIdServiceEngine;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.DateUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * Service implementations for operations on WeIdentity DID.
 *
 * @author tonychen 2018.10
 */
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

    private static WeIdServiceEngine engine = new WeIdServiceEngine();
    
    
    /**
     * Instantiates a new WeIdentity DID service.
     */
    public WeIdServiceImpl() {
        init();
    }

    private static void init() {

        // initialize the WeIdentity DID contract
        ContractConfig config = buildContractConfig();
        weIdContractAddress = config.getWeIdAddress();
        weIdContract = (WeIdContract) getContractService(weIdContractAddress, WeIdContract.class);
    }

    /**
     * Create a WeIdentity DID with null input param.
     *
     * @return the response data
     */
    @Override
    public ResponseData<CreateWeIdDataResult> createWeId() {

    	EngineResultData<CreateWeIdDataResult>  result = engine.createWeId();
    	ResponseData<CreateWeIdDataResult>resp = new ResponseData<CreateWeIdDataResult>();
    	return resp;
    }
    /**
     * Create a WeIdentity DID.
     *
     * @param createWeIdArgs the create WeIdentity DID args
     * @return the response data
     */
    @Override
    public ResponseData<String> createWeId(CreateWeIdArgs createWeIdArgs) {

        if (createWeIdArgs == null) {
            logger.error("[createWeId]: input parameter createWeIdArgs is null.");
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
        }
        if (!WeIdUtils.isPrivateKeyValid(createWeIdArgs.getWeIdPrivateKey())) {
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        String privateKey = createWeIdArgs.getWeIdPrivateKey().getPrivateKey();
        String publicKey = createWeIdArgs.getPublicKey();
        if (StringUtils.isNotBlank(publicKey)) {
            if (!WeIdUtils.isKeypairMatch(privateKey, publicKey)) {
                return new ResponseData<>(
                    StringUtils.EMPTY,
                    ErrorCode.WEID_PUBLICKEY_AND_PRIVATEKEY_NOT_MATCHED
                );
            }
            String weId = WeIdUtils.convertPublicKeyToWeId(publicKey);
            ResponseData<Boolean> isWeIdExistResp = this.isWeIdExist(weId);
            if (isWeIdExistResp.getResult() == null || isWeIdExistResp.getResult()) {
                logger
                    .error("[createWeId]: create weid failed, the weid :{} is already exist", weId);
                return new ResponseData<>(StringUtils.EMPTY, ErrorCode.WEID_ALREADY_EXIST);
            }
            EngineResultData<String>  result = engine.createWeId(createWeIdArgs);
//            ResponseData<Boolean> innerResp = processCreateWeId(weId, publicKey, privateKey);
            ResponseData<Boolean> innerResp = new ResponseData<Boolean>();
            if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                    "[createWeId]: create weid failed. error message is :{}, public key is {}",
                    innerResp.getErrorMessage(),
                    publicKey
                );
                return new ResponseData<>(StringUtils.EMPTY,
                    ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()),
                    innerResp.getTransactionInfo());
            }
            return new ResponseData<>(weId, ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()),
                innerResp.getTransactionInfo());
        } else {
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.WEID_PUBLICKEY_INVALID);
        }
    }


    /**
     * Get a WeIdentity DID Document.
     *
     * @param weId the WeIdentity DID
     * @return the WeIdentity DID document
     */
    @Override
    public ResponseData<WeIdDocument> getWeIdDocument(String weId) {

    	EngineResultData<WeIdDocument> result = engine.getWeIdDocument(weId);
    	ResponseData<WeIdDocument>response = new ResponseData<WeIdDocument>();
    	return response;
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

        if (result == null) {
            return new ResponseData<>(
                StringUtils.EMPTY,
                ErrorCode.getTypeByErrorCode(responseData.getErrorCode())
            );
        }
        ObjectMapper mapper = new ObjectMapper();
        String weIdDocument;
        try {
            weIdDocument = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        } catch (Exception e) {
            return new ResponseData<>(
                StringUtils.EMPTY,
                ErrorCode.getTypeByErrorCode(responseData.getErrorCode())
            );
        }
        weIdDocument =
            new StringBuffer()
                .append(weIdDocument)
                .insert(1, WeIdConstant.WEID_DOC_PROTOCOL_VERSION)
                .toString();

        ResponseData<String> responseDataJson = new ResponseData<String>();
        responseDataJson.setResult(weIdDocument);
        responseDataJson.setErrorCode(ErrorCode.getTypeByErrorCode(responseData.getErrorCode()));

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
        EngineResultData<Boolean> result = engine.setPublicKey(setPublicKeyArgs);
        ResponseData<Boolean>response = new ResponseData<Boolean>();
        return response;
        
    }

    private boolean verifySetPublicKeyArgs(SetPublicKeyArgs setPublicKeyArgs) {

        return !(setPublicKeyArgs == null
            || setPublicKeyArgs.getType() == null
            || setPublicKeyArgs.getUserWeIdPrivateKey() == null
            || setPublicKeyArgs.getPublicKey() == null);
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
        EngineResultData<Boolean> result = engine.setService(setServiceArgs);
        ResponseData<Boolean> response = new ResponseData<Boolean>();
        return response;
    }

    private boolean verifySetServiceArgs(SetServiceArgs setServiceArgs) {

        return !(setServiceArgs == null
            || setServiceArgs.getType() == null
            || setServiceArgs.getUserWeIdPrivateKey() == null
            || setServiceArgs.getServiceEndpoint() == null);
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
                    logger.error("[setAuthentication]: owner : {} is invalid.", owner);
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
                        DataToolUtils.stringToBytes32(WeIdConstant.WEID_DOC_AUTHENTICATE_PREFIX),
                        DataToolUtils.stringToDynamicBytes(
                            new StringBuffer()
                                .append(setAuthenticationArgs.getPublicKey())
                                .append(WeIdConstant.SEPARATOR)
                                .append(owner)
                                .toString()),
                        DateUtils.getCurrentTimeStampInt256());
                TransactionReceipt receipt =
                    future.get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
                List<WeIdAttributeChangedEventResponse> response =
                    WeIdContract.getWeIdAttributeChangedEvents(receipt);
                TransactionInfo info = new TransactionInfo(receipt);
                if (CollectionUtils.isNotEmpty(response)) {
                    return new ResponseData<>(true, ErrorCode.SUCCESS, info);
                } else {
                    logger.error("Set authenticate failed. Error message :{}",
                        ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCodeDesc());
                    return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH,
                        info);
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Set authenticate failed. Error message :{}", e);
                return new ResponseData<>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
            } catch (TimeoutException e) {
                logger.error("Set authenticate timeout. Error message :{}", e);
                return new ResponseData<>(false, ErrorCode.TRANSACTION_TIMEOUT);
            } catch (PrivateKeyIllegalException e) {
                logger.error("Set authenticate with private key exception. Error message :{}", e);
                return new ResponseData<>(false, e.getErrorCode());
            } catch (Exception e) {
                logger.error("Set authenticate failed. Error message :{}", e);
                return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
            }
        } else {
            logger.error("Set authenticate failed. weid : {} is invalid.", weId);
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
    }

    private boolean verifySetAuthenticationArgs(SetAuthenticationArgs setAuthenticationArgs) {

        return !(setAuthenticationArgs == null
            || setAuthenticationArgs.getUserWeIdPrivateKey() == null
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
        engine.isWeIdExist(weId);
        ResponseData<Boolean> resp = new ResponseData<Boolean>();
        return resp;
    }

}
