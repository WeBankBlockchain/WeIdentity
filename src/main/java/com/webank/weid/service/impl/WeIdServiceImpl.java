/*
 *       Copyright漏 (2018-2019) WeBank Co., Ltd.
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

package com.webank.weid.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.exception.LoadContractException;
import com.webank.weid.exception.PrivateKeyIllegalException;
import com.webank.weid.protocol.base.AuthenticationProperty;
import com.webank.weid.protocol.base.PublicKeyProperty;
import com.webank.weid.protocol.base.WeIdAuthentication;
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
import com.webank.weid.util.WeIdUtils;

/**
 * Service implementations for operations on WeIdentity DID.
 *
 * @author tonychen 2018.10
 */
public class WeIdServiceImpl extends AbstractService implements WeIdService {

    /**
     * log4j object, for recording log.
     */
    private static final Logger logger = LoggerFactory.getLogger(WeIdServiceImpl.class);

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
            return new ResponseData<>(null,
                ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()),
                innerResp.getTransactionInfo());
        }
        return new ResponseData<>(result, ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()),
            innerResp.getTransactionInfo());
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
            return new ResponseData<>(weId,
                ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()),
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

        if (!WeIdUtils.isWeIdValid(weId)) {
            logger.error("Input weId : {} is invalid.", weId);
            return new ResponseData<>(null, ErrorCode.WEID_INVALID);
        }
        ResponseData<WeIdDocument> weIdDocResp = weIdServiceEngine.getWeIdDocument(weId);
        if (weIdDocResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return weIdDocResp;
        }
        return new ResponseData<>(trimObsoleteWeIdDocument(weIdDocResp.getResult()),
            weIdDocResp.getErrorCode(), weIdDocResp.getErrorMessage());
    }

    private WeIdDocument trimObsoleteWeIdDocument(WeIdDocument originalDocument) {
        List<PublicKeyProperty> pubKeysToRemove = new ArrayList<>();
        List<AuthenticationProperty> authToRemove = new ArrayList<>();
        for (PublicKeyProperty pr : originalDocument.getPublicKey()) {
            if (pr.getPublicKey().contains(WeIdConstant.REMOVED_PUBKEY_TAG)) {
                pubKeysToRemove.add(pr);
                for (AuthenticationProperty ap : originalDocument.getAuthentication()) {
                    if (ap.getPublicKey().equalsIgnoreCase(pr.getId())) {
                        authToRemove.add(ap);
                    }
                }
            }
        }
        for (AuthenticationProperty ap : originalDocument.getAuthentication()) {
            if (ap.getPublicKey().contains(WeIdConstant.REMOVED_AUTHENTICATION_TAG)) {
                authToRemove.add(ap);
            }
        }
        originalDocument.getPublicKey().removeAll(pubKeysToRemove);
        originalDocument.getAuthentication().removeAll(authToRemove);
        return originalDocument;
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
            logger.error("write object to String fail.", e);
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
     * Remove a public key enlisted in WeID document together with the its authentication.
     *
     * @param setPublicKeyArgs the to-be-deleted publicKey
     * @return true if succeeds, false otherwise
     */
    @Override
    public ResponseData<Boolean> removePublicKeyWithAuthentication(
        SetPublicKeyArgs setPublicKeyArgs) {
        if (!verifySetPublicKeyArgs(setPublicKeyArgs)) {
            logger.error("[removePublicKey]: input parameter setPublicKeyArgs is illegal.");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (!WeIdUtils.isPrivateKeyValid(setPublicKeyArgs.getUserWeIdPrivateKey())) {
            return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }

        String weId = setPublicKeyArgs.getWeId();
        ResponseData<WeIdDocument> responseData = this.getWeIdDocument(weId);
        if (responseData.getResult() == null) {
            return new ResponseData<>(false,
                ErrorCode.getTypeByErrorCode(responseData.getErrorCode())
            );
        }
        List<PublicKeyProperty> publicKeys = responseData.getResult().getPublicKey();
        for (PublicKeyProperty pk : publicKeys) {
            // TODO in future, add authorization check
            if (pk.getPublicKey().equalsIgnoreCase(setPublicKeyArgs.getPublicKey())) {
                if (publicKeys.size() == 1) {
                    return new ResponseData<>(false,
                        ErrorCode.WEID_CANNOT_REMOVE_ITS_OWN_PUB_KEY_WITHOUT_BACKUP);
                }
            }
        }

        // Add correct tag by externally call removeAuthentication once
        SetAuthenticationArgs setAuthenticationArgs = new SetAuthenticationArgs();
        setAuthenticationArgs.setWeId(weId);
        WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
        weIdPrivateKey.setPrivateKey(setPublicKeyArgs.getUserWeIdPrivateKey().getPrivateKey());
        setAuthenticationArgs.setUserWeIdPrivateKey(weIdPrivateKey);
        setAuthenticationArgs.setPublicKey(setPublicKeyArgs.getPublicKey());
        setAuthenticationArgs.setOwner(setPublicKeyArgs.getOwner());
        ResponseData<Boolean> removeAuthResp = this.removeAuthentication(setAuthenticationArgs);
        if (!removeAuthResp.getResult()) {
            logger.error("Failed to remove authentication: " + removeAuthResp.getErrorMessage());
            return removeAuthResp;
        }

        String owner = setPublicKeyArgs.getOwner();
        String weAddress = WeIdUtils.convertWeIdToAddress(setPublicKeyArgs.getWeId());

        if (StringUtils.isEmpty(owner)) {
            owner = weAddress;
        } else {
            if (WeIdUtils.isWeIdValid(owner)) {
                owner = WeIdUtils.convertWeIdToAddress(owner);
            } else {
                logger.error("removePublicKey: owner : {} is invalid.", owner);
                return new ResponseData<>(false, ErrorCode.WEID_INVALID);
            }
        }
        try {
            String attributeKey =
                new StringBuffer()
                    .append(WeIdConstant.WEID_DOC_PUBLICKEY_PREFIX)
                    .append(WeIdConstant.SEPARATOR)
                    .append(setPublicKeyArgs.getType())
                    .append(WeIdConstant.SEPARATOR)
                    .append("base64")
                    .toString();
            String privateKey = setPublicKeyArgs.getUserWeIdPrivateKey().getPrivateKey();
            String publicKey = setPublicKeyArgs.getPublicKey();
            String attrValue = new StringBuffer()
                .append(publicKey)
                .append(WeIdConstant.REMOVED_PUBKEY_TAG).append("/")
                .append(owner)
                .toString();
            return weIdServiceEngine.setAttribute(weAddress, attributeKey, attrValue, privateKey);
        } catch (PrivateKeyIllegalException e) {
            logger.error("[removePublicKey] set PublicKey failed because privateKey is illegal. ",
                e);
            return new ResponseData<>(false, e.getErrorCode());
        } catch (Exception e) {
            logger.error("[removePublicKey] set PublicKey failed with exception. ", e);
            return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
        }
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

        String privateKey = setPublicKeyArgs.getUserWeIdPrivateKey().getPrivateKey();
        try {
            String attributeKey =
                new StringBuffer()
                    .append(WeIdConstant.WEID_DOC_PUBLICKEY_PREFIX)
                    .append(WeIdConstant.SEPARATOR)
                    .append(setPublicKeyArgs.getType())
                    .append(WeIdConstant.SEPARATOR)
                    .append("base64")
                    .toString();
            String attrValue = new StringBuffer().append(pubKey).append("/").append(owner)
                .toString();
            return weIdServiceEngine.setAttribute(weAddress, attributeKey, attrValue, privateKey);
        } catch (PrivateKeyIllegalException e) {
            logger.error("[setPublicKey] set PublicKey failed because privateKey is illegal. ",
                e);
            return new ResponseData<>(false, e.getErrorCode());
        } catch (Exception e) {
            logger.error("[setPublicKey] set PublicKey failed with exception. ", e);
            return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
        }
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
        if (!verifyServiceType(setServiceArgs.getType())) {
            logger.error("[setService]: the length of service type is overlimit");
            return new ResponseData<>(false, ErrorCode.WEID_SERVICE_TYPE_OVERLIMIT);
        }
        String weId = setServiceArgs.getWeId();
        String serviceType = setServiceArgs.getType();
        String serviceEndpoint = setServiceArgs.getServiceEndpoint();
        if (WeIdUtils.isWeIdValid(weId)) {
            String privateKey = setServiceArgs.getUserWeIdPrivateKey().getPrivateKey();
            try {
                String attributeKey = new StringBuffer()
                    .append(WeIdConstant.WEID_DOC_SERVICE_PREFIX)
                    .append(WeIdConstant.SEPARATOR)
                    .append(serviceType)
                    .toString();
                return weIdServiceEngine
                    .setAttribute(WeIdUtils.convertWeIdToAddress(weId), attributeKey,
                        serviceEndpoint, privateKey);

            } catch (PrivateKeyIllegalException e) {
                logger
                    .error("[setService] set PublicKey failed because privateKey is illegal. ",
                        e);
                return new ResponseData<>(false, e.getErrorCode());
            } catch (Exception e) {
                logger.error("[setService] set service failed. Error message :{}", e);
                return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
            }
        } else {
            logger.error("[setService] set service failed, weid -->{} is invalid.", weId);
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }

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
            logger.error("[isWeIdExist] check weid failed. weid : {} is invalid.", weId);
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
        return weIdServiceEngine.isWeIdExist(weId);
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
                String attrValue = new StringBuffer()
                    .append(setAuthenticationArgs.getPublicKey())
                    .append(WeIdConstant.SEPARATOR)
                    .append(owner)
                    .toString();
                return weIdServiceEngine
                    .setAttribute(weAddress,
                        WeIdConstant.WEID_DOC_AUTHENTICATE_PREFIX,
                        attrValue,
                        privateKey);
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

    /**
     * Remove an authentication tag in WeID document only - will not affect its public key.
     *
     * @param setAuthenticationArgs the to-be-deleted publicKey
     * @return true if succeeds, false otherwise
     */
    public ResponseData<Boolean> removeAuthentication(SetAuthenticationArgs setAuthenticationArgs) {

        if (!verifySetAuthenticationArgs(setAuthenticationArgs)) {
            logger
                .error("[removeAuthentication]: input parameter setAuthenticationArgs is illegal.");
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
                    logger.error("[removeAuthentication]: owner : {} is invalid.", owner);
                    return new ResponseData<>(false, ErrorCode.WEID_INVALID);
                }
            }
            String privateKey = setAuthenticationArgs.getUserWeIdPrivateKey().getPrivateKey();
            try {
                String attrValue = new StringBuffer()
                    .append(setAuthenticationArgs.getPublicKey())
                    .append(WeIdConstant.REMOVED_AUTHENTICATION_TAG)
                    .append(WeIdConstant.SEPARATOR)
                    .append(owner)
                    .toString();
                return weIdServiceEngine
                    .setAttribute(weAddress,
                        WeIdConstant.WEID_DOC_AUTHENTICATE_PREFIX,
                        attrValue,
                        privateKey);
            } catch (PrivateKeyIllegalException e) {
                logger
                    .error("remove authenticate with private key exception. Error message :{}", e);
                return new ResponseData<>(false, e.getErrorCode());
            } catch (Exception e) {
                logger.error("remove authenticate failed. Error message :{}", e);
                return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
            }
        } else {
            logger.error("Set authenticate failed. weid : {} is invalid.", weId);
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
    }

    private boolean verifySetServiceArgs(SetServiceArgs setServiceArgs) {

        return !(setServiceArgs == null
            || StringUtils.isBlank(setServiceArgs.getType())
            || setServiceArgs.getUserWeIdPrivateKey() == null
            || StringUtils.isBlank(setServiceArgs.getServiceEndpoint()));
    }

    private boolean verifyServiceType(String type) {
        String serviceType = new StringBuffer()
            .append(WeIdConstant.WEID_DOC_SERVICE_PREFIX)
            .append(WeIdConstant.SEPARATOR)
            .append(type)
            .toString();
        int serviceTypeLength = serviceType.getBytes(StandardCharsets.UTF_8).length;
        return serviceTypeLength <= WeIdConstant.BYTES32_FIXED_LENGTH;
    }

    private ResponseData<Boolean> processCreateWeId(String weId, String publicKey,
        String privateKey) {

        String address = WeIdUtils.convertWeIdToAddress(weId);
        try {
            return weIdServiceEngine.createWeId(address, publicKey, privateKey);
        } catch (PrivateKeyIllegalException e) {
            logger.error("[createWeId] create weid failed because privateKey is illegal. ",
                e);
            return new ResponseData<>(false, e.getErrorCode());
        } catch (LoadContractException e) {
            logger.error("[createWeId] create weid failed because Load Contract with "
                    + "exception. ",
                e);
            return new ResponseData<>(false, e.getErrorCode());
        } catch (Exception e) {
            logger.error("[createWeId] create weid failed with exception. ", e);
            return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
        }
    }

    private boolean verifySetPublicKeyArgs(SetPublicKeyArgs setPublicKeyArgs) {

        return !(setPublicKeyArgs == null
            || setPublicKeyArgs.getType() == null
            || setPublicKeyArgs.getUserWeIdPrivateKey() == null
            || StringUtils.isBlank(setPublicKeyArgs.getPublicKey()));
    }

    private boolean verifySetAuthenticationArgs(SetAuthenticationArgs setAuthenticationArgs) {

        return !(setAuthenticationArgs == null
            || setAuthenticationArgs.getUserWeIdPrivateKey() == null
            || StringUtils.isEmpty(setAuthenticationArgs.getPublicKey()));
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.WeIdService#delegateCreateWeId(
     * com.webank.weid.protocol.base.WeIdPublicKey,
     * com.webank.weid.protocol.base.WeIdAuthentication)
     */
    @Override
    public ResponseData<String> delegateCreateWeId(
        WeIdPublicKey publicKey,
        WeIdAuthentication weIdAuthentication) {

        return null;
    }
}
