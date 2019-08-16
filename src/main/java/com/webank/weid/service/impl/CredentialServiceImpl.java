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

package com.webank.weid.service.impl;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.crypto.Sign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.CredentialFieldDisclosureValue;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.CredentialWrapper;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.CptService;
import com.webank.weid.rpc.CredentialService;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.BaseService;
import com.webank.weid.util.CredentialUtils;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.DateUtils;

/**
 * Service implementations for operations on Credential.
 *
 * @author chaoxinhu 2019.1
 */
public class CredentialServiceImpl extends BaseService implements CredentialService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialServiceImpl.class);

    private CptService cptService = new CptServiceImpl();

    private WeIdService weIdService = new WeIdServiceImpl();


    /**
     * Generate a credential.
     *
     * @param args the args
     * @return the Credential response data
     */
    @Override
    public ResponseData<CredentialWrapper> createCredential(CreateCredentialArgs args) {

        CredentialWrapper credentialWrapper = new CredentialWrapper();
        try {
            ErrorCode innerResponse = checkCreateCredentialArgsValidity(args, true);
            if (ErrorCode.SUCCESS.getCode() != innerResponse.getCode()) {
                logger.error("Generate Credential input format error!");
                return new ResponseData<>(null, innerResponse);
            }

            Credential result = new Credential();
            String context = CredentialUtils.getDefaultCredentialContext();
            result.setContext(context);
            result.setId(UUID.randomUUID().toString());
            result.setCptId(args.getCptId());
            result.setIssuer(args.getIssuer());
            Long issuanceDate = args.getIssuanceDate();
            if (issuanceDate == null) {
                result.setIssuanceDate(System.currentTimeMillis());
            } else {
                result.setIssuanceDate(issuanceDate);
            }
            result.setExpirationDate(args.getExpirationDate());
            result.setClaim(args.getClaim());
            Map<String, Object> disclosureMap = new HashMap<>(args.getClaim());
            for (Map.Entry<String, Object> entry : disclosureMap.entrySet()) {
                disclosureMap.put(
                    entry.getKey(),
                    CredentialFieldDisclosureValue.DISCLOSED.getStatus()
                );
            }
            credentialWrapper.setDisclosure(disclosureMap);
            
            // Construct Credential Proof
            Map<String, String> credentialProof = CredentialUtils.buildCredentialProof(
                result,
                args.getWeIdPrivateKey().getPrivateKey(),
                disclosureMap);
            result.setProof(credentialProof);

            credentialWrapper.setCredential(result);
            ResponseData<CredentialWrapper> responseData = new ResponseData<>(
                credentialWrapper,
                ErrorCode.SUCCESS
            );

            return responseData;
        } catch (Exception e) {
            logger.error("Generate Credential failed due to system error. ", e);
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_ERROR);
        }
    }

    /**
     * Verify the validity of a credential without public key provided.
     *
     * @param credentialWrapper the credential wrapper.
     * @return the Boolean response data
     */
    @Override
    public ResponseData<Boolean> verify(CredentialWrapper credentialWrapper) {
        return verifyCredentialContent(credentialWrapper, null);
    }

    /**
     * Verify Credential validity.
     */
    @Override
    public ResponseData<Boolean> verify(Credential credential) {
        Map<String, Object> disclosureMap = new HashMap<>(credential.getClaim());
        for (Map.Entry<String, Object> entry : disclosureMap.entrySet()) {
            disclosureMap.put(entry.getKey(), CredentialFieldDisclosureValue.DISCLOSED.getStatus());
        }
        CredentialWrapper credentialWrapper = new CredentialWrapper();
        credentialWrapper.setCredential(credential);
        credentialWrapper.setDisclosure(disclosureMap);
        return verifyCredentialContent(credentialWrapper, null);
    }

    /**
     * Verify the validity of a credential with public key provided.
     *
     * @param credentialWrapper the args
     * @param weIdPublicKey the specific public key to verify the credential.
     * @return the Boolean response data
     */
    @Override
    public ResponseData<Boolean> verifyCredentialWithSpecifiedPubKey(
        CredentialWrapper credentialWrapper,
        WeIdPublicKey weIdPublicKey) {
        if (credentialWrapper == null) {
            return new ResponseData<Boolean>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (weIdPublicKey == null) {
            return new ResponseData<Boolean>(false, ErrorCode.CREDENTIAL_ISSUER_MISMATCH);
        }
        return verifyCredentialContent(credentialWrapper, weIdPublicKey.getPublicKey());
    }

    /**
     * The only standardized inf to create a full Credential Hash for a given Credential.
     *
     * @param args the args
     * @return the Boolean response data
     */
    @Override
    public ResponseData<String> getCredentialHash(Credential args) {
        ErrorCode innerResponse = CredentialUtils.isCredentialValid(args);
        if (ErrorCode.SUCCESS.getCode() != innerResponse.getCode()) {
            logger.error("Credential input format error!");
            return new ResponseData<>(StringUtils.EMPTY, innerResponse);
        }

        ResponseData<String> responseData = new ResponseData<>(
            CredentialUtils.getCredentialHash(args),
            ErrorCode.SUCCESS
        );
        return responseData;
    }

    private ResponseData<Boolean> verifyCredentialContent(CredentialWrapper credentialWrapper,
        String publicKey) {

        try {
            Credential credential = credentialWrapper.getCredential();
            ErrorCode innerResponse = CredentialUtils.isCredentialValid(credential);
            if (ErrorCode.SUCCESS.getCode() != innerResponse.getCode()) {
                logger.error("Credential input format error!");
                return new ResponseData<>(false, innerResponse);
            }

            ResponseData<Boolean> responseData = verifyIssuerExistence(credential.getIssuer());
            if (!responseData.getResult()) {
                return responseData;
            }

            ErrorCode errorCode = verifyCptFormat(
                credential.getCptId(),
                credential.getClaim()
            );
            if (ErrorCode.SUCCESS.getCode() != errorCode.getCode()) {
                return new ResponseData<>(null, errorCode);
            }

            responseData = verifyNotExpired(credential);
            if (!responseData.getResult()) {
                return responseData;
            }
            responseData = verifySignature(credentialWrapper, publicKey);
            return responseData;
        } catch (Exception e) {
            logger.error("Verify Credential failed due to generic error: ", e);
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_ERROR);
        }
    }

    private ErrorCode checkCreateCredentialArgsValidity(
        CreateCredentialArgs args, boolean privateKeyRequired) {
        ErrorCode innerResponseData = CredentialUtils.isCreateCredentialArgsValid(args);
        if (ErrorCode.SUCCESS.getCode() != innerResponseData.getCode()) {
            logger.error("Create Credential Args illegal: {}", innerResponseData.getCodeDesc());
            return innerResponseData;
        }
        if (privateKeyRequired
            && StringUtils.isEmpty(args.getWeIdPrivateKey().getPrivateKey())) {
            logger.error(ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS.getCodeDesc());
            return ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS;
        }
        return ErrorCode.SUCCESS;
    }


    private ResponseData<Boolean> verifyIssuerExistence(String issuerWeId) {
        ResponseData<Boolean> responseData = weIdService.isWeIdExist(issuerWeId);
        if (responseData == null || !responseData.getResult()) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_ISSUER_NOT_EXISTS);
        }
        return responseData;
    }

    private ErrorCode verifyCptFormat(Integer cptId, Map<String, Object> claim) {

        try {
            //String claimStr = JsonUtil.objToJsonStr(claim);
            String claimStr = DataToolUtils.serialize(claim);
            Cpt cpt = cptService.queryCpt(cptId).getResult();
            if (cpt == null) {
                logger.error(ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCodeDesc());
                return ErrorCode.CREDENTIAL_CPT_NOT_EXISTS;
            }
            //String cptJsonSchema = JsonUtil.objToJsonStr(cpt.getCptJsonSchema());
            String cptJsonSchema = DataToolUtils.serialize(cpt.getCptJsonSchema());

            if (!DataToolUtils.isCptJsonSchemaValid(cptJsonSchema)) {
                logger.error(ErrorCode.CPT_JSON_SCHEMA_INVALID.getCodeDesc());
                return ErrorCode.CPT_JSON_SCHEMA_INVALID;
            }
            if (!DataToolUtils.isValidateJsonVersusSchema(claimStr, cptJsonSchema)) {
                logger.error(ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL.getCodeDesc());
                return ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL;
            }
            return ErrorCode.SUCCESS;
        } catch (Exception e) {
            logger.error(
                "Generic error occurred during verify cpt format when verifyCredential: " + e);
            return ErrorCode.CREDENTIAL_ERROR;
        }
    }

    private ResponseData<Boolean> verifyNotExpired(Credential credential) {
        try {
            Date expireDate = new Date(credential.getExpirationDate().longValue());
            Date currentDate = new Date();
            boolean result = currentDate.before(expireDate);
            ResponseData<Boolean> responseData = new ResponseData<>(
                result,
                ErrorCode.SUCCESS
            );
            if (!result) {
                responseData.setErrorCode(ErrorCode.CREDENTIAL_EXPIRED);
            }
            return responseData;
        } catch (Exception e) {
            logger.error(
                "Generic error occurred during verify expiration when verifyCredential: " + e);
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_ERROR);
        }
    }

    private ResponseData<Boolean> verifySignature(
        CredentialWrapper credentialWrapper,
        String publicKey) {

        try {
            Credential credential = credentialWrapper.getCredential();
            Map<String, Object> disclosureMap = credentialWrapper.getDisclosure();
            String rawData = CredentialUtils
                .getCredentialThumbprintWithoutSig(credential, disclosureMap);
            Sign.SignatureData signatureData =
                DataToolUtils.simpleSignatureDeserialization(
                    DataToolUtils.base64Decode(
                        credential.getSignature().getBytes(StandardCharsets.UTF_8)
                    )
                );

            if (StringUtils.isEmpty(publicKey)) {
                // Fetch public key from chain
                String credentialIssuer = credential.getIssuer();
                ResponseData<WeIdDocument> innerResponseData =
                    weIdService.getWeIdDocument(credentialIssuer);
                if (innerResponseData.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                    logger.error(
                        "Error occurred when fetching WeIdentity DID document for: {}, msg: {}",
                        credentialIssuer, innerResponseData.getErrorMessage());
                    return new ResponseData<>(false, ErrorCode.CREDENTIAL_WEID_DOCUMENT_ILLEGAL);
                } else {
                    WeIdDocument weIdDocument = innerResponseData.getResult();
                    ErrorCode errorCode =  DataToolUtils
                        .verifySignatureFromWeId(rawData, signatureData, weIdDocument);
                    if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                        return new ResponseData<>(false, errorCode);
                    }
                    return new ResponseData<>(true, ErrorCode.SUCCESS);
                }
            } else {
                boolean result =
                    DataToolUtils
                        .verifySignature(rawData, signatureData, new BigInteger(publicKey));
                if (!result) {
                    return new ResponseData<>(false, ErrorCode.CREDENTIAL_SIGNATURE_BROKEN);
                }
                return new ResponseData<>(true, ErrorCode.SUCCESS);
            }
        } catch (SignatureException e) {
            logger.error(
                "Generic signatureException occurred during verify signature "
                    + "when verifyCredential: ", e);
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_EXCEPTION_VERIFYSIGNATURE);
        } catch (WeIdBaseException e) {
            logger.error(
                "Generic signatureException occurred during verify signature ", e);
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_SIGNATURE_BROKEN);
        } catch (Exception e) {
            logger.error(
                "Generic exception occurred during verify signature when verifyCredential: ", e);
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_ERROR);
        }
    }

    /**
     * Generate a credential with selected data.
     *
     * @param credential the credential
     * @param disclosure the keys which select to disclosure
     * @return credential
     */
    @Override
    public ResponseData<CredentialWrapper> createSelectiveCredential(
        Credential credential,
        String disclosure) {

        //Map<String, Object> disclosureMap = (Map<String, Object>) JsonUtil.jsonStrToObj(
        //    new HashMap<String, Object>(), disclosure);
        Map<String, Object> disclosureMap = DataToolUtils.deserialize(disclosure, HashMap.class);

        //setp 1: check if the input args is illegal.
        CredentialWrapper credentialResult = new CredentialWrapper();
        ErrorCode checkResp = CredentialUtils.isCredentialValid(credential);
        if (ErrorCode.SUCCESS.getCode() != checkResp.getCode()) {
            return new ResponseData<>(credentialResult, checkResp);
        }

        //step 2: convet values of claim to hash by disclosure status
        Map<String, Object> claim = credential.getClaim();
        Map<String, Object> hashMap = new HashMap<String, Object>(claim);

        for (Map.Entry<String, Object> entry : claim.entrySet()) {
            claim.put(entry.getKey(), CredentialUtils.getFieldHash(entry.getValue()));
        }

        for (Map.Entry<String, Object> entry : disclosureMap.entrySet()) {
            if (CredentialFieldDisclosureValue.DISCLOSED.getStatus()
                .equals(entry.getValue())) {
                claim.put(entry.getKey(), hashMap.get(entry.getKey()));
            }
        }

        // step 3: build response of selective credential to caller.
        credentialResult.setCredential(credential);
        credentialResult.setDisclosure(disclosureMap);

        return new ResponseData<>(credentialResult, ErrorCode.SUCCESS);
    }

    /**
     * Get the Json String of a Credential. All fields in the Credential will be included. This also
     * supports the selectively disclosed Credential.
     *
     * @param credential the credential wrapper
     * @return the Credential Json value in String
     */
    @Override
    public ResponseData<String> getCredentialJson(Credential credential) {
        ErrorCode errorCode = CredentialUtils.isCredentialValid(credential);
        if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(
                StringUtils.EMPTY,
                ErrorCode.getTypeByErrorCode(errorCode.getCode())
            );
        }
        // Convert timestamp into UTC timezone
        try {
            Map<String, Object> credMap = DataToolUtils.objToMap(credential);
            String issuanceDate = DateUtils.convertTimestampToUtc(credential.getIssuanceDate());
            String expirationDate = DateUtils.convertTimestampToUtc(credential.getExpirationDate());
            credMap.put(ParamKeyConstant.ISSUANCE_DATE, issuanceDate);
            credMap.put(ParamKeyConstant.EXPIRATION_DATE, expirationDate);
            credMap.remove(ParamKeyConstant.CONTEXT);
            credMap.put(CredentialConstant.CREDENTIAL_CONTEXT_PORTABLE_JSON_FIELD,
                CredentialConstant.DEFAULT_CREDENTIAL_CONTEXT);
            String credentialString = DataToolUtils.mapToCompactJson(credMap);
            return new ResponseData<>(credentialString, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("Json conversion failed in getCredentialJson: ", e);
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.CREDENTIAL_ERROR);
        }
    }
}
