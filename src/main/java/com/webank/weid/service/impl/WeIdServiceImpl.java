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

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.weid.config.ContractConfig;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.v1.WeIdContract;
import com.webank.weid.exception.LoadContractException;
import com.webank.weid.exception.PrivateKeyIllegalException;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.BaseService;
import com.webank.weid.service.impl.proxy.WeIdServiceProxy;
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

    private static WeIdServiceProxy proxy = new WeIdServiceProxy();
    
    
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
        
        ResponseData<Boolean> innerResp = processCreateWeId(weId, publicKey, privateKey);
        if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error(
                "[createWeId] Create weId failed. error message is :{}",
                innerResp.getErrorMessage()
            );
            return new ResponseData<>(null, ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()),
                innerResp.getTransactionInfo());
        }
        return new ResponseData<>(result, ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()),
            innerResp.getTransactionInfo());
        
//    	EngineResultData<CreateWeIdDataResult>  result = engine.createWeId();
//    	ResponseData<CreateWeIdDataResult>resp = new ResponseData<CreateWeIdDataResult>();
//    	return resp;
    }
    
    private ResponseData<Boolean> processCreateWeId(String weId, String publicKey,
	        String privateKey) {
	        try {
//	            WeIdContract weIdContract = (WeIdContract) reloadContract(
//	                weIdContractAddress,
//	                privateKey,
//	                WeIdContract.class);

	        	return proxy.createWeId(weId, publicKey, privateKey);
//	        	return new ResponseData<>(false, ErrorCode.SUCCESS);
	        }  catch (PrivateKeyIllegalException e) {
	            return new ResponseData<>(false, e.getErrorCode());
	        } catch (LoadContractException e) {
	            return new ResponseData<>(false, e.getErrorCode());
	        } catch (Exception e) {
	            return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
	        }
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
            ResponseData<Boolean> innerResp = processCreateWeId(weId, publicKey, privateKey);
//            ResponseData<Boolean> innerResp = new ResponseData<Boolean>();
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

    	return proxy.getWeIdDocument(weId);
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
        
//        String privateKey = setPublicKeyArgs.getUserWeIdPrivateKey().getPrivateKey();
        try {
//            WeIdContract weIdContract = (WeIdContract) reloadContract(
//                weIdContractAddress,
//                privateKey,
//                WeIdContract.class
//            );
            String attributeKey =
                    new StringBuffer()
                        .append(WeIdConstant.WEID_DOC_PUBLICKEY_PREFIX)
                        .append(WeIdConstant.SEPARATOR)
                        .append(setPublicKeyArgs.getType())
                        .append(WeIdConstant.SEPARATOR)
                        .append("base64")
                        .toString();
//            engine.setAuthentication(setAuthenticationArgs)
            String attrValue = new StringBuffer().append(pubKey).append("/").append(owner).toString();
            return proxy.setAttribute(weAddress, attributeKey, attrValue);
        } catch (PrivateKeyIllegalException e) {
            return new ResponseData<>(false, e.getErrorCode());
        } catch (Exception e) {
            return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
        }
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
        String weId = setServiceArgs.getWeId();
        String serviceType = setServiceArgs.getType();
        String serviceEndpoint = setServiceArgs.getServiceEndpoint();
        if (WeIdUtils.isWeIdValid(weId)) {
//            String privateKey = setServiceArgs.getUserWeIdPrivateKey().getPrivateKey();
            try {
//                WeIdContract weIdContract = (WeIdContract) reloadContract(
//                    weIdContractAddress,
//                    privateKey,
//                    WeIdContract.class);
            	String attributeKey = WeIdConstant.WEID_DOC_SERVICE_PREFIX + WeIdConstant.SEPARATOR
                        + serviceType;
            	return proxy.setAttribute(WeIdUtils.convertWeIdToAddress(weId), attributeKey, serviceEndpoint);

            }catch (PrivateKeyIllegalException e) {
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
//                WeIdContract weIdContract = (WeIdContract) reloadContract(
//                    weIdContractAddress,
//                    privateKey,
//                    WeIdContract.class);
//            	String attributeKey = 
            	String attrValue = new StringBuffer()
                        .append(setAuthenticationArgs.getPublicKey())
                        .append(WeIdConstant.SEPARATOR)
                        .append(owner)
                        .toString();
            	return proxy.setAttribute(weAddress, WeIdConstant.WEID_DOC_AUTHENTICATE_PREFIX, attrValue);
//            	return new ResponseData<>(false, ErrorCode.SUCCESS);
            }  catch (PrivateKeyIllegalException e) {
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
        return proxy.isWeIdExist(weId);
    }

}
