

package com.webank.weid.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.base.*;
import com.webank.weid.protocol.request.AuthenticationArgs;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.ServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.WeIdListResult;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.Multibase.Multibase;
import com.webank.weid.util.Multicodec.Multicodec;
import com.webank.weid.util.Multicodec.MulticodecEncoder;
import com.webank.weid.util.WeIdUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.fisco.bcos.sdk.model.CryptoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service implementations for operations on WeIdentity DID.
 *
 * @author afeexian 2022.08
 */
public class WeIdServiceImpl implements WeIdService {

    /**
     * log4j object, for recording log.
     */
    private static final Logger logger = LoggerFactory.getLogger(WeIdServiceImpl.class);

    private static final com.webank.weid.blockchain.service.impl.WeIdServiceImpl weIdBlockchainService = new com.webank.weid.blockchain.service.impl.WeIdServiceImpl();

    /**
     * Create a WeIdentity DID with null input param.
     *
     * @return the response data
     */
    @Override
    public ResponseData<CreateWeIdDataResult> createWeId() {

        CreateWeIdDataResult result = WeIdUtils.createWeId();
        if (Objects.isNull(result)) {
            logger.error("Create weId failed.");
            return new ResponseData<>(null, ErrorCode.WEID_KEYPAIR_CREATE_FAILED);
        }
        String address = WeIdUtils.convertWeIdToAddress(result.getWeId());
        AuthenticationProperty authenticationProperty = new AuthenticationProperty();
        //在创建weid时默认添加一个id为#keys-[hash(publicKey)]的verification method
        authenticationProperty.setId(result.getWeId() + "#keys-" + DataToolUtils.hash(result.getUserWeIdPublicKey().getPublicKey()).substring(58));
        //verification method controller默认为自己
        authenticationProperty.setController(result.getWeId());
        //这里把publicKey用multicodec编码，然后使用Multibase格式化，国密和非国密使用不同的编码
        byte[] publicKeyEncode = MulticodecEncoder.encode(com.webank.weid.blockchain.util.DataToolUtils.cryptoType == CryptoType.ECDSA_TYPE? Multicodec.ED25519_PUB:Multicodec.SM2_PUB,
                result.getUserWeIdPublicKey().getPublicKey().getBytes(StandardCharsets.UTF_8));
        authenticationProperty.setPublicKeyMultibase(Multibase.encode(Multibase.Base.Base58BTC, publicKeyEncode));
        List<String> authList = new ArrayList<>();
        authList.add(authenticationProperty.toString());
        List<String> serviceList = new ArrayList<>();
        ServiceProperty serviceProperty = new ServiceProperty();
        serviceProperty.setServiceEndpoint("https://github.com/WeBankBlockchain/WeIdentity");
        serviceProperty.setType("WeIdentity");
        serviceProperty.setId(authenticationProperty.getController() + '#' + DataToolUtils.hash(serviceProperty.getServiceEndpoint()).substring(58));
        serviceList.add(serviceProperty.toString());
        com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> innerResp = weIdBlockchainService.createWeId(address, authList, serviceList, result.getUserWeIdPrivateKey().getPrivateKey());
        //ResponseData<Boolean> innerResp = processCreateWeId(result.getWeId(), result.getUserWeIdPublicKey().getPublicKey(), result.getUserWeIdPrivateKey().getPrivateKey());
        if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error(
                "[createWeId] Create weId failed. error message is :{}",
                innerResp.getErrorMessage()
            );
            return new ResponseData<>(null,
                ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()),
                innerResp.getTransactionInfo());
        }
        return new ResponseData<>(result, ErrorCode.SUCCESS, innerResp.getTransactionInfo());
    }

    /**
     * Create a WeIdentity DID with null input param.
     *
     * @return the response data
     */
    @Override
    public ResponseData<String> createWeIdByPublicKey(WeIdPublicKey weIdPublicKey, WeIdPrivateKey weIdPrivateKey) {
        if (!WeIdUtils.isPrivateKeyValid(weIdPrivateKey) || !WeIdUtils
                .isPrivateKeyLengthValid(weIdPrivateKey.getPrivateKey())) {
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        String privateKey = weIdPrivateKey.getPrivateKey();
        String publicKey = weIdPublicKey.getPublicKey();
        if (StringUtils.isNotBlank(publicKey)) {
            String weId = WeIdUtils.convertPublicKeyToWeId(publicKey);
            ResponseData<Boolean> isWeIdExistResp = this.isWeIdExist(weId);
            if (isWeIdExistResp.getResult() == null || isWeIdExistResp.getResult()) {
                logger
                        .error("[createWeId]: create weid failed, the weid :{} is already exist", weId);
                return new ResponseData<>(StringUtils.EMPTY, ErrorCode.WEID_ALREADY_EXIST);
            }
            String address = WeIdUtils.convertWeIdToAddress(weId);
            AuthenticationProperty authenticationProperty = new AuthenticationProperty();
            //在创建weid时默认添加一个id为#keys-[hash(publicKey)]的verification method
            authenticationProperty.setId(weId + "#keys-" + DataToolUtils.hash(publicKey).substring(58));
            //verification method controller默认为自己
            authenticationProperty.setController(weId);
            //这里把publicKey用multicodec编码，然后使用Multibase格式化，国密和非国密使用不同的编码
            byte[] publicKeyEncode = MulticodecEncoder.encode(com.webank.weid.blockchain.util.DataToolUtils.cryptoType == CryptoType.ECDSA_TYPE? Multicodec.ED25519_PUB:Multicodec.SM2_PUB,
                    publicKey.getBytes(StandardCharsets.UTF_8));
            authenticationProperty.setPublicKeyMultibase(Multibase.encode(Multibase.Base.Base58BTC, publicKeyEncode));
            List<String> authList = new ArrayList<>();
            authList.add(authenticationProperty.toString());
            List<String> serviceList = new ArrayList<>();
            ServiceProperty serviceProperty = new ServiceProperty();
            serviceProperty.setServiceEndpoint("https://github.com/WeBankBlockchain/WeIdentity");
            serviceProperty.setType("WeIdentity");
            serviceProperty.setId(authenticationProperty.getController() + '#' + DataToolUtils.hash(serviceProperty.getServiceEndpoint()).substring(58));
            serviceList.add(serviceProperty.toString());
            com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> innerResp = weIdBlockchainService.createWeId(address, authList, serviceList, privateKey);
            //ResponseData<Boolean> innerResp = processCreateWeId(weId, publicKey, privateKey);
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
                    ErrorCode.SUCCESS,
                    innerResp.getTransactionInfo());
        } else {
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.WEID_PUBLICKEY_INVALID);
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
        if (!WeIdUtils.isPrivateKeyValid(createWeIdArgs.getWeIdPrivateKey()) || !WeIdUtils
            .isPrivateKeyLengthValid(createWeIdArgs.getWeIdPrivateKey().getPrivateKey())) {
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        String privateKey = createWeIdArgs.getWeIdPrivateKey().getPrivateKey();
        String publicKey = createWeIdArgs.getPublicKey();
        if (StringUtils.isNotBlank(publicKey)) {
            if (!WeIdUtils.isKeypairMatch(new BigInteger(privateKey), publicKey)) {
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
            String address = WeIdUtils.convertWeIdToAddress(weId);
            AuthenticationProperty authenticationProperty = new AuthenticationProperty();
            //在创建weid时默认添加一个id为#keys-[hash(publicKey)]的verification method
            authenticationProperty.setId(weId + "#keys-" + DataToolUtils.hash(publicKey).substring(58));
            //verification method controller默认为自己
            authenticationProperty.setController(weId);
            //这里把publicKey用multicodec编码，然后使用Multibase格式化，国密和非国密使用不同的编码
            byte[] publicKeyEncode = MulticodecEncoder.encode(com.webank.weid.blockchain.util.DataToolUtils.cryptoType == CryptoType.ECDSA_TYPE? Multicodec.ED25519_PUB:Multicodec.SM2_PUB,
                    publicKey.getBytes(StandardCharsets.UTF_8));
            authenticationProperty.setPublicKeyMultibase(Multibase.encode(Multibase.Base.Base58BTC, publicKeyEncode));
            List<String> authList = new ArrayList<>();
            authList.add(authenticationProperty.toString());
            List<String> serviceList = new ArrayList<>();
            ServiceProperty serviceProperty = new ServiceProperty();
            serviceProperty.setServiceEndpoint("https://github.com/WeBankBlockchain/WeIdentity");
            serviceProperty.setType("WeIdentity");
            serviceProperty.setId(authenticationProperty.getController() + '#' + DataToolUtils.hash(serviceProperty.getServiceEndpoint()).substring(58));
            serviceList.add(serviceProperty.toString());
            com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> innerResp = weIdBlockchainService.createWeId(address, authList, serviceList, privateKey);
            //ResponseData<Boolean> innerResp = processCreateWeId(weId, publicKey, privateKey);
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
                    ErrorCode.SUCCESS,
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
        com.webank.weid.blockchain.protocol.response.ResponseData<com.webank.weid.blockchain.protocol.base.WeIdDocument> innerResp = weIdBlockchainService.getWeIdDocument(weId);
        //ResponseData<WeIdDocument> weIdDocResp = weIdServiceEngine.getWeIdDocument(weId);
        if(innerResp.getErrorCode() == ErrorCode.SUCCESS.getCode()){
            WeIdDocument weIdDocument = WeIdDocument.fromBlockChain(innerResp.getResult());
            return new ResponseData<>(weIdDocument, ErrorCode.SUCCESS);
        } else {
            return new ResponseData<>(null, innerResp.getErrorCode(), innerResp.getErrorMessage());
        }
    }

    /**
     * Get a WeIdentity DID Document Metadata.
     *
     * @param weId the WeIdentity DID
     * @return the WeIdentity DID document
     */
    @Override
    public ResponseData<WeIdDocumentMetadata> getWeIdDocumentMetadata(String weId) {

        if (!WeIdUtils.isWeIdValid(weId)) {
            logger.error("Input weId : {} is invalid.", weId);
            return new ResponseData<>(null, ErrorCode.WEID_INVALID);
        }
        //ResponseData<WeIdDocumentMetadata> weIdDocResp = weIdServiceEngine.getWeIdDocumentMetadata(weId);
        com.webank.weid.blockchain.protocol.response.ResponseData<com.webank.weid.blockchain.protocol.base.WeIdDocumentMetadata> innerResp = weIdBlockchainService.getWeIdDocumentMetadata(weId);
        if(innerResp.getErrorCode() == ErrorCode.SUCCESS.getCode()){
            WeIdDocumentMetadata weIdDocResp = WeIdDocumentMetadata.fromBlockChain(innerResp.getResult());
            return new ResponseData<>(weIdDocResp, ErrorCode.SUCCESS);
        } else {
            return new ResponseData<>(null, innerResp.getErrorCode(), innerResp.getErrorMessage());
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
     * Set service properties.
     *
     * @param weId the WeID to set service to
     * @param serviceArgs your service name and endpoint
     * @param privateKey the private key
     * @return true if the "set" operation succeeds, false otherwise.
     */
    @Override
    public ResponseData<Boolean> setService(String weId, ServiceArgs serviceArgs,
        WeIdPrivateKey privateKey) {
        if (!verifyServiceArgs(serviceArgs)) {
            logger.error("[setService]: input parameter setServiceArgs is illegal.");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (!WeIdUtils.isPrivateKeyValid(privateKey)) {
            return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        return processSetService(
            privateKey.getPrivateKey(),
            weId,
            serviceArgs);

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
        //return weIdServiceEngine.isWeIdExist(weId);
        com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> innerResp =
                weIdBlockchainService.isWeIdExist(weId);
        if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(false,
                    ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()));
        }
        return new ResponseData<>(innerResp.getResult(), ErrorCode.SUCCESS);
    }

    /**
     * Check if WeIdentity DID is deactivated on Chain.
     *
     * @param weId the WeIdentity DID
     * @return true if is deactivated, false otherwise
     */
    @Override
    public ResponseData<Boolean> isDeactivated(String weId) {
        if (!WeIdUtils.isWeIdValid(weId)) {
            logger.error("[isWeIdExist] check weid failed. weid : {} is invalid.", weId);
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
        //return weIdServiceEngine.isDeactivated(weId);
        com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> innerResp =
                weIdBlockchainService.isDeactivated(weId);
        if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(true,
                    ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()));
        }
        return new ResponseData<>(innerResp.getResult(), ErrorCode.SUCCESS);
    }

    /**
     * Set authentications in WeIdentity DID.
     *
     * @param weId the WeID to set auth to
     * @param authenticationArgs A public key is needed
     * @param privateKey the private key
     * @return true if the "set" operation succeeds, false otherwise.
     */
    @Override
    public ResponseData<Boolean> setAuthentication(
        String weId,
        AuthenticationArgs authenticationArgs,
        WeIdPrivateKey privateKey){

        if (!verifyAuthenticationArgs(authenticationArgs)) {
            logger.error("[setAuthentication]: input parameter setAuthenticationArgs is illegal.");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (!WeIdUtils.isPrivateKeyValid(privateKey)) {
            return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        return processSetAuthentication(
            authenticationArgs,
            privateKey.getPrivateKey(),
            weId);
    }

    private ResponseData<Boolean> processSetAuthentication(
        AuthenticationArgs authenticationArgs,
        String privateKey,
        String weId) {
        if (WeIdUtils.isWeIdValid(weId)) {
            //检查目标WeId是否存在和是否被注销
            ResponseData<Boolean> isWeIdExistResp = this.isWeIdExist(weId);
            if (isWeIdExistResp.getResult() == null || !isWeIdExistResp.getResult()) {
                logger.error("[setAuthentication]: failed, the weid :{} does not exist",
                    weId);
                return new ResponseData<>(false, ErrorCode.WEID_DOES_NOT_EXIST);
            }
            ResponseData<Boolean> isDeactivatedResp = this.isDeactivated(weId);
            if (isDeactivatedResp.getResult() == null || isDeactivatedResp.getResult()) {
                logger.error("[setAuthentication]: failed, the weid :{} has been deactivated",
                        weId);
                return new ResponseData<>(false, ErrorCode.WEID_HAS_BEEN_DEACTIVATED);
            }
            //检查authentication的controller WeId是否存在和是否被注销
            if (StringUtils.isEmpty(authenticationArgs.getController())) {
                authenticationArgs.setController(weId);
            }
            if (!WeIdUtils.isWeIdValid(authenticationArgs.getController())) {
                logger.error("[setAuthentication]: controller : {} is invalid.", authenticationArgs.getController());
                return new ResponseData<>(false, ErrorCode.WEID_INVALID);
            }
            isWeIdExistResp = this.isWeIdExist(authenticationArgs.getController());
            if (isWeIdExistResp.getResult() == null || !isWeIdExistResp.getResult()) {
                logger.error("[setAuthentication]: failed, the controller weid :{} does not exist",
                        authenticationArgs.getController());
                return new ResponseData<>(false, ErrorCode.WEID_DOES_NOT_EXIST);
            }
            isDeactivatedResp = this.isDeactivated(authenticationArgs.getController());
            if (isDeactivatedResp.getResult() == null || isDeactivatedResp.getResult()) {
                logger.error("[setAuthentication]: failed, the controller weid :{} has been deactivated",
                        authenticationArgs.getController());
                return new ResponseData<>(false, ErrorCode.WEID_HAS_BEEN_DEACTIVATED);
            }
            WeIdDocument weIdDocument = this.getWeIdDocument(weId).getResult();
            for(int i=0; i<weIdDocument.getAuthentication().size(); i++){
                if(authenticationArgs.getPublicKey().equals(weIdDocument.getAuthentication().get(i).getPublicKey())){
                    logger.error("[setAuthentication]: failed, the Authentication with PublicKeyMultibase :{} exists",
                            authenticationArgs.getPublicKey());
                    return new ResponseData<>(false, ErrorCode.AUTHENTICATION_PUBLIC_KEY_MULTIBASE_EXISTS);
                }
                if(!StringUtils.isEmpty(authenticationArgs.getId()) && authenticationArgs.getId().equals(weIdDocument.getAuthentication().get(i).getId())){
                    logger.error("[setAuthentication]: failed, the Authentication with id :{} exists",
                            authenticationArgs.getId());
                    return new ResponseData<>(false, ErrorCode.AUTHENTICATION_METHOD_ID_EXISTS);
                }
            }
            AuthenticationProperty authenticationProperty = new AuthenticationProperty();
            //如果用户没有指定method id，则系统分配
            authenticationProperty.setId(authenticationArgs.getId());
            if(StringUtils.isBlank(authenticationArgs.getId())){
                authenticationProperty.setId(weId + "#keys-" + DataToolUtils.hash(authenticationArgs.getPublicKey()).substring(58));
            }
            authenticationProperty.setController(authenticationArgs.getController());
            byte[] publicKeyEncode = MulticodecEncoder.encode(com.webank.weid.blockchain.util.DataToolUtils.cryptoType == CryptoType.ECDSA_TYPE? Multicodec.ED25519_PUB:Multicodec.SM2_PUB,
                    authenticationArgs.getPublicKey().getBytes(StandardCharsets.UTF_8));
            authenticationProperty.setPublicKeyMultibase(Multibase.encode(Multibase.Base.Base58BTC, publicKeyEncode));

            List<AuthenticationProperty> authentication = weIdDocument.getAuthentication();
            authentication.add(authenticationProperty);
            weIdDocument.setAuthentication(authentication);
            /*try {
                return weIdServiceEngine
                    .updateWeId(weIdDocument,
                        WeIdUtils.convertWeIdToAddress(weId),
                        privateKey);
            } catch (PrivateKeyIllegalException e) {
                logger.error("Set authenticate with private key exception. Error message :{}", e);
                return new ResponseData<>(false, e.getErrorCode());
            } catch (Exception e) {
                logger.error("Set authenticate failed. Error message :{}", e);
                return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
            }*/
            com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> innerResp =
                    weIdBlockchainService.updateWeId(WeIdDocument.toBlockChain(weIdDocument), privateKey, WeIdUtils.convertWeIdToAddress(weId));
            if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                        "[setAuthentication]: set authenticate failed. error message is :{}",
                        innerResp.getErrorMessage()
                );
                return new ResponseData<>(false,
                        ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()),
                        innerResp.getTransactionInfo());
            }
            return new ResponseData<>(true,
                    ErrorCode.SUCCESS,
                    innerResp.getTransactionInfo());
        } else {
            logger.error("Set authenticate failed. weid : {} is invalid.", weId);
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
    }

    /**
     * Remove an authentication tag in WeID document only - will not affect its public key.
     *
     * @param weId the WeID to remove auth from
     * @param authenticationArgs A public key is needed
     * @param privateKey the private key
     * @return true if succeeds, false otherwise
     */
    @Override
    public ResponseData<Boolean> revokeAuthentication(
        String weId,
        AuthenticationArgs authenticationArgs,
        WeIdPrivateKey privateKey) {

        if (!verifyAuthenticationArgs(authenticationArgs)) {
            logger
                .error("[revokeAuthentication]: input parameter setAuthenticationArgs is illegal.");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (!WeIdUtils.isPrivateKeyValid(privateKey)) {
            return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        if (WeIdUtils.isWeIdValid(weId)) {
            ResponseData<Boolean> isWeIdExistResp = this.isWeIdExist(weId);
            if (isWeIdExistResp.getResult() == null || !isWeIdExistResp.getResult()) {
                logger.error("[revokeAuthentication]: failed, the weid :{} does not exist", weId);
                return new ResponseData<>(false, ErrorCode.WEID_DOES_NOT_EXIST);
            }
            String weAddress = WeIdUtils.convertWeIdToAddress(weId);
            WeIdDocument weIdDocument = this.getWeIdDocument(weId).getResult();
            List<AuthenticationProperty> authentication = weIdDocument.getAuthentication();
            if(!StringUtils.isEmpty(authenticationArgs.getPublicKey())){
                for(int i=0; i<weIdDocument.getAuthentication().size(); i++){
                    if(authenticationArgs.getPublicKey().equals(weIdDocument.getAuthentication().get(i).getPublicKey())){
                        /*try {
                            authentication.remove(i);
                            weIdDocument.setAuthentication(authentication);
                            return weIdServiceEngine
                                    .updateWeId(weIdDocument,
                                            weAddress,
                                            privateKey.getPrivateKey());
                        } catch (PrivateKeyIllegalException e) {
                            logger.error("remove authenticate with private key exception. Error message :{}", e);
                            return new ResponseData<>(false, e.getErrorCode());
                        } catch (Exception e) {
                            logger.error("remove authenticate failed. Error message :{}", e);
                            return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
                        }*/
                        authentication.remove(i);
                        weIdDocument.setAuthentication(authentication);
                        com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> innerResp =
                                weIdBlockchainService.updateWeId(WeIdDocument.toBlockChain(weIdDocument), privateKey.getPrivateKey(), WeIdUtils.convertWeIdToAddress(weId));
                        if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                            logger.error(
                                    "[revokeAuthentication]: remove authenticate failed. error message is :{}",
                                    innerResp.getErrorMessage()
                            );
                            return new ResponseData<>(false,
                                    ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()),
                                    innerResp.getTransactionInfo());
                        }
                        return new ResponseData<>(true,
                                ErrorCode.SUCCESS,
                                innerResp.getTransactionInfo());
                    }
                    logger.error("[revokeAuthentication]: failed, the Authentication with publicKey :{} not exists",
                            authenticationArgs.getPublicKey());
                }
            }
            if(!StringUtils.isEmpty(authenticationArgs.getId())){
                for(int i=0; i<weIdDocument.getAuthentication().size(); i++){
                    if(authenticationArgs.getId().equals(weIdDocument.getAuthentication().get(i).getId())){
                        /*try {
                            authentication.remove(i);
                            weIdDocument.setAuthentication(authentication);
                            return weIdServiceEngine
                                    .updateWeId(weIdDocument,
                                            weAddress,
                                            privateKey.getPrivateKey());
                        } catch (PrivateKeyIllegalException e) {
                            logger.error("remove authenticate with private key exception. Error message :{}", e);
                            return new ResponseData<>(false, e.getErrorCode());
                        } catch (Exception e) {
                            logger.error("remove authenticate failed. Error message :{}", e);
                            return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
                        }*/
                        authentication.remove(i);
                        weIdDocument.setAuthentication(authentication);
                        com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> innerResp =
                                weIdBlockchainService.updateWeId(WeIdDocument.toBlockChain(weIdDocument), privateKey.getPrivateKey(), WeIdUtils.convertWeIdToAddress(weId));
                        if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                            logger.error(
                                    "[revokeAuthentication]: remove authenticate failed. error message is :{}",
                                    innerResp.getErrorMessage()
                            );
                            return new ResponseData<>(false,
                                    ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()),
                                    innerResp.getTransactionInfo());
                        }
                        return new ResponseData<>(true,
                                ErrorCode.SUCCESS,
                                innerResp.getTransactionInfo());
                    }
                    logger.error("[revokeAuthentication]: failed, the Authentication with id :{} not exists",
                            authenticationArgs.getId());
                }
            }
            logger.error("[revokeAuthentication]: failed, the Authentication not exists");
            return new ResponseData<>(false, ErrorCode.AUTHENTICATION_METHOD_NOT_EXISTS);
        } else {
            logger.error("revokeAuthentication failed. weid : {} is invalid.", weId);
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
    }

    private boolean verifyServiceArgs(ServiceArgs serviceArgs) {

        return !(serviceArgs == null
            || StringUtils.isBlank(serviceArgs.getType())
            || StringUtils.isBlank(serviceArgs.getServiceEndpoint()));
    }

    /*private ResponseData<Boolean> processCreateWeId(
        String weId,
        String publicKey,
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
    }*/

    private boolean verifyAuthenticationArgs(AuthenticationArgs authenticationArgs) {

        return !(authenticationArgs == null
            || StringUtils.isEmpty(authenticationArgs.getPublicKey())
            || !(isPublicKeyStringValid(authenticationArgs.getPublicKey())));
    }

    private boolean isPublicKeyStringValid(String pubKey) {
        // Allow base64, rsa (alphaNum) and bigInt
        return (DataToolUtils.isValidBase64String(pubKey)
        //    || StringUtils.isAlphanumeric(pubKey)
            || NumberUtils.isDigits(pubKey));
    }

    private ResponseData<Boolean> processSetService(
        String privateKey,
        String weId,
        ServiceArgs serviceArgs) {
        if (WeIdUtils.isWeIdValid(weId)) {
            ResponseData<Boolean> isWeIdExistResp = this.isWeIdExist(weId);
            if (isWeIdExistResp.getResult() == null || !isWeIdExistResp.getResult()) {
                logger.error("[SetService]: failed, the weid :{} does not exist", weId);
                return new ResponseData<>(false, ErrorCode.WEID_DOES_NOT_EXIST);
            }
            WeIdDocument weIdDocument = this.getWeIdDocument(weId).getResult();
            List<ServiceProperty> service = weIdDocument.getService();
            ServiceProperty serviceProperty = new ServiceProperty();
            serviceProperty.setType(serviceArgs.getType());
            serviceProperty.setServiceEndpoint(serviceArgs.getServiceEndpoint());
            if(weIdDocument.getService().size()==0){
                if(StringUtils.isEmpty(serviceArgs.getId())){
                    serviceProperty.setId(weId + '#' + DataToolUtils.hash(serviceArgs.getServiceEndpoint()));
                }else{
                    serviceProperty.setId(serviceArgs.getId());
                }
            }else{
                if(StringUtils.isEmpty(serviceArgs.getId())){
                    serviceProperty.setId(weId + '#' + DataToolUtils.hash(serviceArgs.getServiceEndpoint()).substring(58));
                }else{
                    for(int i=0; i<weIdDocument.getService().size(); i++){
                        if(serviceArgs.getId().equals(weIdDocument.getService().get(i).getId())){
                            logger.error("[setAuthentication]: failed, the service with id :{} exists",
                                    serviceArgs.getId());
                            return new ResponseData<>(false, ErrorCode.SERVICE_METHOD_ID_EXISTS);
                        }
                    }
                    serviceProperty.setId(serviceArgs.getId());
                }
            }
            service.add(serviceProperty);
            weIdDocument.setService(service);
            /*try {
                return weIdServiceEngine
                        .updateWeId(weIdDocument,
                                WeIdUtils.convertWeIdToAddress(weId),
                                privateKey);
            } catch (PrivateKeyIllegalException e) {
                logger
                    .error("[setService] set service failed because privateKey is illegal. ",
                        e);
                return new ResponseData<>(false, e.getErrorCode());
            } catch (Exception e) {
                logger.error("[setService] set service failed. Error message :{}", e);
                return new ResponseData<>(false, ErrorCode.UNKNOW_ERROR);
            }*/
            com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> innerResp =
                    weIdBlockchainService.updateWeId(WeIdDocument.toBlockChain(weIdDocument), privateKey, WeIdUtils.convertWeIdToAddress(weId));
            if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                        "[createWeId]: set service failed. error message is :{}",
                        innerResp.getErrorMessage()
                );
                return new ResponseData<>(false,
                        ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()),
                        innerResp.getTransactionInfo());
            }
            return new ResponseData<>(true,
                    ErrorCode.SUCCESS,
                    innerResp.getTransactionInfo());
        } else {
            logger.error("[setService] set service failed, weid -->{} is invalid.", weId);
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
    }

    @Override
    public ResponseData<List<String>> getWeIdList(
            Integer first,
            Integer last
    ) {
        /*try {
            logger.info("[getWeIdList] begin get weIdList, first index = {}, last index = {}",
                    first,
                    last
            );
            return weIdServiceEngine.getWeIdList(first, last);
        } catch (Exception e) {
            logger.error("[getWeIdList] get weIdList failed with exception. ", e);
            return new ResponseData<>(null, ErrorCode.UNKNOW_ERROR);
        }*/
        com.webank.weid.blockchain.protocol.response.ResponseData<List<String>> innerResp =
                weIdBlockchainService.getWeIdList(first, last);
        if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(null,
                    ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()));
        }
        return new ResponseData<>(innerResp.getResult(), ErrorCode.SUCCESS);
    }

    @Override
    public ResponseData<Integer> getWeIdCount() {
        //return weIdServiceEngine.getWeIdCount();
        com.webank.weid.blockchain.protocol.response.ResponseData<Integer> innerResp =
                weIdBlockchainService.getWeIdCount();
        if (innerResp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(-1,
                    ErrorCode.getTypeByErrorCode(innerResp.getErrorCode()));
        }
        return new ResponseData<>(innerResp.getResult(), ErrorCode.SUCCESS);
    }

    @Override
    public ResponseData<WeIdListResult> getWeIdListByPubKeyList(List<WeIdPublicKey> pubKeyList) {
        if (pubKeyList == null || pubKeyList.size() == 0) {
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        WeIdListResult weIdListResult = new WeIdListResult();
        weIdListResult.setWeIdList(new ArrayList<>());
        weIdListResult.setErrorCodeList(new ArrayList<>());
        ResponseData<WeIdListResult> responseData = new ResponseData<WeIdListResult>();
        pubKeyList.forEach(weIdPublicKey -> {
            String weId = WeIdUtils.convertPublicKeyToWeId(weIdPublicKey.getPublicKey());
            if (StringUtils.isBlank(weId)) {
                weIdListResult.getWeIdList().add(null);
                weIdListResult.getErrorCodeList().add(ErrorCode.WEID_PUBLICKEY_INVALID.getCode());
            } else {
                if (this.isWeIdExist(weId).getResult()) {
                    weIdListResult.getWeIdList().add(weId);
                    weIdListResult.getErrorCodeList().add(ErrorCode.SUCCESS.getCode());
                } else {
                    weIdListResult.getWeIdList().add(null);
                    weIdListResult.getErrorCodeList().add(
                         ErrorCode.WEID_PUBLIC_KEY_NOT_EXIST.getCode());
                }
            }
        });
        responseData.setResult(weIdListResult);
        return responseData;
    }
}
