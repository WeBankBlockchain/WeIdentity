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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.crypto.Sign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.base.AuthenticationProperty;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.PublicKeyProperty;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.VerifyCredentialArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.CptService;
import com.webank.weid.rpc.CredentialService;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.BaseService;
import com.webank.weid.util.CredentialUtils;
import com.webank.weid.util.DateUtils;
import com.webank.weid.util.JsonSchemaValidatorUtils;
import com.webank.weid.util.SignatureUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * Service implementations for operations on Credential.
 *
 * @author chaoxinhu 2018.10
 */
@Component
public class CredentialServiceImpl extends BaseService implements CredentialService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialServiceImpl.class);

    @Autowired
    private CptService cptService;

    @Autowired
    private WeIdService weIdService;


    /**
     * Generate a credential.
     *
     * @param args the args
     * @return the Credential response data
     */
    @Override
    public ResponseData<Credential> createCredential(CreateCredentialArgs args) {
        ResponseData<Credential> responseData = new ResponseData<Credential>();

        try {
            ResponseData<Boolean> innerResponse = checkCreateCredentialArgsValidity(args, true);
            if (!innerResponse.getResult()) {
                logger.error("Generate Credential input format error!");
                return new ResponseData<Credential>(
                    null, innerResponse.getErrorCode(), innerResponse.getErrorMessage());
            }

            Credential result = new Credential();
            String context = CredentialUtils.getDefaultCredentialContext();
            result.setContext(context);
            result.setId(UUID.randomUUID().toString());
            result.setCptId(args.getCptId());
            result.setIssuer(args.getIssuer());
            result.setIssuranceDate(DateUtils.getCurrentTimeStamp());
            result.setExpirationDate(args.getExpirationDate());
            result.setClaim(args.getClaim());
            String rawData = CredentialUtils.getCredentialFields(result);
            String privateKey = args.getWeIdPrivateKey().getPrivateKey();
            Sign.SignatureData sigData = SignatureUtils.signMessage(rawData, privateKey);
            result.setSignature(
                new String(
                    SignatureUtils
                        .base64Encode(SignatureUtils.simpleSignatureSerialization(sigData)),
                        WeIdConstant.UTF_8));
            responseData.setResult(result);
        } catch (Exception e) {
            logger.error("Generate Credential failed due to system error. ", e);
            return new ResponseData<Credential>(null, ErrorCode.CREDENTIAL_ERROR);
        }
        return responseData;
    }

    /**
     * Verify the validity of a credential without public key provided.
     *
     * @param args the args
     * @return the Boolean response data
     */
    @Override
    public ResponseData<Boolean> verifyCredential(Credential args) {
        return verifyCredentialContent(args, null);
    }

    /**
     * Verify the validity of a credential with public key provided.
     *
     * @param args the args
     * @return the Boolean response data
     */
    @Override
    public ResponseData<Boolean> verifyCredentialWithSpecifiedPubKey(VerifyCredentialArgs args) {
        if (args == null) {
            return new ResponseData<Boolean>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (args.getWeIdPublicKey() == null) {
            return new ResponseData<Boolean>(false, ErrorCode.CREDENTIAL_ISSUER_MISMATCH);
        }
        return verifyCredentialContent(args.getCredential(),
            args.getWeIdPublicKey().getPublicKey());
    }

    private ResponseData<Boolean> verifyCredentialContent(Credential credential, String publicKey) {

        try {
            ResponseData<Boolean> innerResponse = checkCredentialArgsValidity(credential);
            if (!innerResponse.getResult()) {
                logger.error("Credential input format error!");
                return new ResponseData<>(
                    false, innerResponse.getErrorCode(), innerResponse.getErrorMessage());
            }

            ResponseData<Boolean> responseData = verifyIssuerExistence(credential.getIssuer());
            if (!responseData.getResult()) {
                return responseData;
            }
            responseData = verifyCptFormat(credential);
            if (!responseData.getResult()) {
                return responseData;
            }
            responseData = verifyNotExpired(credential);
            if (!responseData.getResult()) {
                return responseData;
            }
            responseData = verifySignature(credential, publicKey);
            return responseData;
        } catch (Exception e) {
            logger.error("Verify Credential failed due to generic error: ", e);
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_ERROR);
        }
    }

    private ResponseData<Boolean> checkCreateCredentialArgsValidity(
        CreateCredentialArgs args, boolean privateKeyRequired) {
        ResponseData<Boolean> responseData = new ResponseData<Boolean>();
        if (args == null) {
            logger.error("Credential argument null input error.");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }

        try {
            Integer cptId = args.getCptId();
            if (cptId == null) {
                logger.error(ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCodeDesc());
                return new ResponseData<>(false, ErrorCode.CREDENTIAL_CPT_NOT_EXISTS);
            }

            String credentialIssuer = args.getIssuer();
            if (!WeIdUtils.isWeIdValid(credentialIssuer)) {
                logger.error(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCodeDesc());
                return new ResponseData<>(false, ErrorCode.CREDENTIAL_ISSUER_INVALID);
            }

            Long expirationDate = args.getExpirationDate();
            if (expirationDate == null
                || expirationDate.longValue() < 0
                || expirationDate.longValue() == 0) {
                logger.error(ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCodeDesc());
                return new ResponseData<>(false, ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL);
            }

            String claim = args.getClaim();
            if (StringUtils.isEmpty(claim)) {
                logger.error(ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS.getCodeDesc());
                return new ResponseData<>(false, ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS);
            }

            if (privateKeyRequired 
                && StringUtils.isEmpty(args.getWeIdPrivateKey().getPrivateKey())) {
                logger.error(ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS.getCodeDesc());
                return new ResponseData<>(false, ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS);
            }

            responseData.setResult(true);
            return responseData;
        } catch (Exception e) {
            logger.error("Check create Credential args failed due to generic error: ", e);
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_ERROR);
        }
    }

    private ResponseData<Boolean> checkCredentialArgsValidity(Credential args) {
        ResponseData<Boolean> responseData = new ResponseData<Boolean>();
        if (args == null) {
            logger.error("Credential argument null input error.");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }

        try {
            CreateCredentialArgs generateCredentialArgs = CredentialUtils
                .extractCredentialMetadata(args);
            // Do not need to check privateKey field since it won't be set in this case.
            ResponseData<Boolean> innerResponseData =
                checkCreateCredentialArgsValidity(generateCredentialArgs, false);
            if (!innerResponseData.getResult()) {
                return new ResponseData<>(
                    false, innerResponseData.getErrorCode(), innerResponseData.getErrorMessage());
            }
            // Check new fields: id, context, signature, and issurancedate;
            String credentialId = args.getId();
            if (StringUtils.isEmpty(credentialId)) {
                logger.error(ErrorCode.CREDENTIAL_ID_NOT_EXISTS.getCodeDesc());
                return new ResponseData<>(false, ErrorCode.CREDENTIAL_ID_NOT_EXISTS);
            }

            String context = args.getContext();
            if (StringUtils.isEmpty(context)) {
                logger.error(ErrorCode.CREDENTIAL_CONTEXT_NOT_EXISTS.getCodeDesc());
                return new ResponseData<>(false, ErrorCode.CREDENTIAL_CONTEXT_NOT_EXISTS);
            }

            Long issuranceDate = args.getIssuranceDate();
            if (issuranceDate == null) {
                logger.error(ErrorCode.CREDENTIAL_CREATE_DATE_ILLEGAL.getCodeDesc());
                return new ResponseData<>(false, ErrorCode.CREDENTIAL_CREATE_DATE_ILLEGAL);
            }
            if (issuranceDate.longValue() > args.getExpirationDate().longValue()) {
                logger.error(ErrorCode.CREDENTIAL_EXPIRED.getCodeDesc());
                return new ResponseData<>(false, ErrorCode.CREDENTIAL_EXPIRED);
            }

            String signature = args.getSignature();
            if (StringUtils.isEmpty(signature) || !SignatureUtils.isValidBase64String(signature)) {
                logger.error(ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCodeDesc());
                return new ResponseData<>(false, ErrorCode.CREDENTIAL_SIGNATURE_BROKEN);
            }

            responseData.setResult(true);
            return responseData;
        } catch (Exception e) {
            logger.error("Check create Credential args failed due to generic error: ", e);
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_ERROR);
        }
    }

    private ResponseData<Boolean> verifyIssuerExistence(String issuerWeId) {
        ResponseData<Boolean> responseData = weIdService.isWeIdExist(issuerWeId);
        if (responseData == null || !responseData.getResult()) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_ISSUER_NOT_EXISTS);
        }
        return responseData;
    }

    private ResponseData<Boolean> verifyCptFormat(Credential credential) {
        ResponseData<Boolean> responseData = new ResponseData<Boolean>();
        String claim = credential.getClaim();
        Integer cptId = credential.getCptId();
        Cpt cpt = cptService.queryCpt(cptId).getResult();
        if (cpt == null) {
            logger.error(ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCodeDesc());
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_CPT_NOT_EXISTS);
        }
        String cptJsonSchema = cpt.getCptJsonSchema();
        try {
            if (!JsonSchemaValidatorUtils.isCptJsonSchemaValid(cptJsonSchema)) {
                logger.error(ErrorCode.CPT_JSON_SCHEMA_INVALID.getCodeDesc());
                return new ResponseData<>(false, ErrorCode.CPT_JSON_SCHEMA_INVALID);
            }
            if (!JsonSchemaValidatorUtils.validateJsonVersusSchema(claim, cptJsonSchema)) {
                logger.error(ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL.getCodeDesc());
                return new ResponseData<>(false, ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL);
            }
            responseData.setResult(true);
            return responseData;
        } catch (Exception e) {
            logger.error(
                "Generic error occurred during verify cpt format when verifyCredential: " + e);
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_ERROR);
        }
    }

    private ResponseData<Boolean> verifyNotExpired(Credential credential) {
        ResponseData<Boolean> responseData = new ResponseData<Boolean>();
        Date expireDate = new Date(credential.getExpirationDate().longValue());
        Date currentDate = new Date();
        boolean result = currentDate.before(expireDate);
        responseData.setResult(result);
        if (!result) {
            responseData.setErrorCode(ErrorCode.CREDENTIAL_EXPIRED.getCode());
            responseData.setErrorMessage(ErrorCode.CREDENTIAL_EXPIRED.getCodeDesc());
        }
        return responseData;
    }

    private ResponseData<Boolean> verifySignature(Credential credential, String publicKey) {
    
        ResponseData<Boolean> responseData = new ResponseData<Boolean>();
        try {
            String hashedRawData = CredentialUtils.getCredentialFields(credential);
            Sign.SignatureData signatureData =
                SignatureUtils.simpleSignatureDeserialization(
                    SignatureUtils.base64Decode(
                            credential.getSignature().getBytes(WeIdConstant.UTF_8)));

            if (StringUtils.isEmpty(publicKey)) {
                // Fetch public key from chain
                String credentialIssuer = credential.getIssuer();
                ResponseData<WeIdDocument> innerResponseData =
                    weIdService.getWeIdDocument(credentialIssuer);
                if (innerResponseData.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                    logger.error(
                        "Error occurred when fetching WeIdentity DID document for: "
                            + credentialIssuer
                            + ", msg: "
                            + innerResponseData.getErrorMessage());
                    return new ResponseData<>(false, ErrorCode.CREDENTIAL_WEID_DOCUMENT_ILLEGAL);
                } else {
                    // Traverse public key list indexed Authentication key list
                    WeIdDocument weIdDocument = innerResponseData.getResult();

                    List<String> publicKeysListToVerify = new ArrayList<String>();
                    for (AuthenticationProperty authenticationProperty : weIdDocument
                        .getAuthentication()) {
                        String index = authenticationProperty.getPublicKey();
                        for (PublicKeyProperty publicKeyProperty : weIdDocument.getPublicKey()) {
                            if (publicKeyProperty.getId().equalsIgnoreCase(index)) {
                                publicKeysListToVerify.add(publicKeyProperty.getPublicKey());
                            }
                        }
                    }
                    boolean result = false;
                    for (String publicKeyItem : publicKeysListToVerify) {
                        if (!StringUtils.isEmpty(publicKeyItem)) {
                            result =
                                result
                                    || SignatureUtils.verifySignature(
                                    hashedRawData, signatureData, new BigInteger(publicKeyItem));
                        }
                    }
                    responseData.setResult(result);
                    if (!result) {
                        responseData.setErrorCode(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode());
                        responseData
                            .setErrorMessage(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCodeDesc());
                    }
                    return responseData;
                }
            } else {
                boolean result =
                    SignatureUtils
                        .verifySignature(hashedRawData, signatureData, new BigInteger(publicKey));
                responseData.setResult(result);
                if (!result) {
                    responseData.setErrorCode(ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCode());
                    responseData
                        .setErrorMessage(ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCodeDesc());
                }
                return responseData;
            }
        } catch (Exception e) {
            logger.error(
                "Generic error occurred during verify signature when verifyCredential: " + e);
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_ERROR);
        }
    }
}
