/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
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

import java.io.File;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.crypto.Sign;
import org.bcos.web3j.crypto.Sign.SignatureData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.base.EvidenceInfo;
import com.webank.weid.protocol.base.EvidenceSignInfo;
import com.webank.weid.protocol.base.HashString;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.inf.Hashable;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.EvidenceService;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.DateUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * Service implementations for operations on Evidence.
 *
 * @author chaoxinhu 2019.1
 */
public class EvidenceServiceImpl extends AbstractService implements EvidenceService {

    private static final Logger logger = LoggerFactory.getLogger(EvidenceServiceImpl.class);

    private WeIdService weIdService = new WeIdServiceImpl();

    /**
     * Create a new evidence to the blockchain and get the evidence address.
     *
     * @param object the given Java object
     * @param weIdPrivateKey the caller WeID Authentication
     * @return Evidence address
     */
    @Override
    public ResponseData<String> createEvidence(Hashable object, WeIdPrivateKey weIdPrivateKey) {
        return createEvidence(object, weIdPrivateKey, null);
    }

    @Override
    public ResponseData<String> createEvidence(Hashable object, WeIdPrivateKey weIdPrivateKey,
        Map<String, String> extra) {
        ResponseData<String> hashResp = getHashValue(object);
        if (StringUtils.isEmpty(hashResp.getResult())) {
            return new ResponseData<>(StringUtils.EMPTY, hashResp.getErrorCode(),
                hashResp.getErrorMessage());
        }
        if (!WeIdUtils.isPrivateKeyValid(weIdPrivateKey)) {
            return new ResponseData<>(StringUtils.EMPTY,
                ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS);
        }
        String extraValue = StringUtils.EMPTY;
        if (extra != null && !extra.isEmpty()) {
            try {
                extraValue = URLEncoder.encode(DataToolUtils.stringMapToCompactJson(extra),
                    StandardCharsets.UTF_8.name());
            } catch (Exception e) {
                logger.error("extra value illegal: {}", extra.toString());
                return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
            }
        }
        return hashToNewEvidence(hashResp.getResult(), weIdPrivateKey.getPrivateKey(), extraValue);
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.generateHash
     * #generateHash(T object)
     */
    @Override
    public <T> ResponseData<HashString> generateHash(T object) {
        if (object == null) {
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        if (object instanceof Hashable) {
            ResponseData<String> hashResp = getHashValue((Hashable) object);
            if (StringUtils.isEmpty(hashResp.getResult())) {
                return new ResponseData<>(null, hashResp.getErrorCode(),
                    hashResp.getErrorMessage());
            }
            return new ResponseData<>(new HashString(hashResp.getResult()), ErrorCode.SUCCESS);
        }
        if (object instanceof File) {
            // This will convert all types of file into String stream
            String rawData = convertFileToString((File) object);
            if (StringUtils.isEmpty(rawData)) {
                logger.error("Failed to convert file into String: {}", ((File) object).getName());
                return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
            }
            return new ResponseData<>(new HashString(DataToolUtils.sha3(rawData)),
                ErrorCode.SUCCESS);
        }
        if (object instanceof String) {
            if (StringUtils.isEmpty((String) object)) {
                logger.error("Input String is blank, ignored..");
                return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
            }
            return new ResponseData<>(new HashString(DataToolUtils.sha3((String) object)),
                ErrorCode.SUCCESS);
        }
        logger.error("Unsupported input object type: {}", object.getClass().getCanonicalName());
        return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
    }

    private String convertFileToString(File file) {
        try {
            return Files.asByteSource(file).asCharSource(Charsets.UTF_8).read();
        } catch (Exception e) {
            logger.error("Failed to load file as String.", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * Obtain the hash value of a given object - supports Credential, Wrapper and Pojo, and also
     * plain hash value (no extra hashing required).
     *
     * @param object any object
     * @return hash value
     */
    private ResponseData<String> getHashValue(Hashable object) {
        if (object == null) {
            // Support empty hash value
            return new ResponseData<>(WeIdConstant.HEX_PREFIX, ErrorCode.SUCCESS);
        }
        try {
            String hashValue = object.getHash();
            if (StringUtils.isEmpty(hashValue)) {
                return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
            }
            return new ResponseData<>(hashValue, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("Input Object type unsupported: {}", object.getClass().getName(), e);
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
        }
    }

    /**
     * Actual method to upload to blockchain, varied in different blockchain versions.
     *
     * @param hashValue the hash value to be uploaded
     * @param privateKey the private key to reload contract and sign txn
     * @param extra the extra value (compact json formatted blob)
     */
    private ResponseData<String> hashToNewEvidence(String hashValue, String privateKey,
        String extra) {
        try {
            Sign.SignatureData sigData =
                DataToolUtils.signMessage(hashValue, privateKey);
            String signature = new String(
                DataToolUtils.base64Encode(DataToolUtils.simpleSignatureSerialization(sigData)),
                StandardCharsets.UTF_8);
            Long timestamp = DateUtils.getNoMillisecondTimeStamp();
            return evidenceServiceEngine.createEvidence(
                hashValue,
                signature,
                extra,
                timestamp,
                privateKey
            );
        } catch (Exception e) {
            logger.error("create evidence failed due to system error. ", e);
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }

    /**
     * Get the evidence from blockchain.
     *
     * @param evidenceKey the evidence hash on chain
     * @return The EvidenceInfo
     */
    @Override
    public ResponseData<EvidenceInfo> getEvidence(String evidenceKey) {
        if (!DataToolUtils.isValidHash(evidenceKey)) {
            logger.error("Evidence argument illegal input: evidence hash. ");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        try {
            return evidenceServiceEngine.getInfo(evidenceKey);
        } catch (Exception e) {
            logger.error("get evidence failed.", e);
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }

    private ResponseData<Boolean> verifySignatureToSigner(
        String rawData,
        String signerWeId,
        SignatureData signatureData
    ) {
        try {
            ResponseData<WeIdDocument> innerResponseData =
                weIdService.getWeIdDocument(signerWeId);
            if (innerResponseData.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                    "Error occurred when fetching WeIdentity DID document for: {}, msg: {}",
                    signerWeId, innerResponseData.getErrorMessage());
                return new ResponseData<>(false, ErrorCode.CREDENTIAL_WEID_DOCUMENT_ILLEGAL);
            }
            WeIdDocument weIdDocument = innerResponseData.getResult();
            ErrorCode errorCode = DataToolUtils
                .verifySignatureFromWeId(rawData, signatureData, weIdDocument);
            if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(false, errorCode);
            }
            return new ResponseData<>(true, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("error occurred during verifying signatures from chain: ", e);
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }

    /**
     * Validate whether an evidence is signed by this WeID.
     *
     * @param evidenceInfo the evidence info fetched from chain
     * @param weId the WeID
     * @return true if yes, false otherwise
     */
    @Override
    public ResponseData<Boolean> verifySigner(EvidenceInfo evidenceInfo, String weId) {
        return verifySigner(evidenceInfo, weId, null);
    }


    /**
     * Validate whether an evidence is signed by this WeID with passed-in public key.
     *
     * @param evidenceInfo the evidence info fetched from chain
     * @param weId the WeID
     * @param publicKey the public key
     * @return true if yes, false otherwise
     */
    @Override
    public ResponseData<Boolean> verifySigner(
        EvidenceInfo evidenceInfo,
        String weId,
        String publicKey) {
        if (evidenceInfo == null || evidenceInfo.getSigners().isEmpty()) {
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (!WeIdUtils.isWeIdValid(weId)) {
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
        if (!evidenceInfo.getSigners().contains(weId)) {
            logger.error("This Evidence does not contain the provided WeID: {}", weId);
            return new ResponseData<>(false, ErrorCode.WEID_DOES_NOT_EXIST);
        }
        EvidenceSignInfo signInfo = evidenceInfo.getSignInfo().get(weId);
        String signature = signInfo.getSignature();
        if (!DataToolUtils.isValidBase64String(signature)) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_EVIDENCE_SIGNATURE_BROKEN);
        }
        SignatureData signatureData =
            DataToolUtils.simpleSignatureDeserialization(
                DataToolUtils.base64Decode(signature.getBytes(StandardCharsets.UTF_8))
            );
        if (StringUtils.isEmpty(publicKey)) {
            return verifySignatureToSigner(
                evidenceInfo.getCredentialHash(),
                WeIdUtils.convertAddressToWeId(weId),
                signatureData
            );
        } else {
            try {
                boolean result = DataToolUtils
                    .verifySignature(evidenceInfo.getCredentialHash(), signatureData,
                        new BigInteger(publicKey));
                if (!result) {
                    logger.error("Public key does not match signature.");
                    return new ResponseData<>(false, ErrorCode.CREDENTIAL_SIGNATURE_BROKEN);
                }
                return new ResponseData<>(true, ErrorCode.SUCCESS);
            } catch (Exception e) {
                logger.error("Passed-in signature illegal");
                return new ResponseData<>(false, ErrorCode.WEID_PUBLICKEY_INVALID);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.EvidenceService#createEvidenceWithCustomKey(
     * com.webank.weid.protocol.inf.Hashable, com.webank.weid.protocol.base.WeIdPrivateKey,
     * java.util.Map, java.lang.String)
     */
    @Override
    public ResponseData<String> createEvidenceWithCustomKey(
        Hashable object,
        WeIdPrivateKey weIdPrivateKey,
        Map<String, String> extra,
        String extraKey) {

        ResponseData<String> hashResp = getHashValue(object);
        String hashValue = hashResp.getResult();
        if (StringUtils.isEmpty(hashResp.getResult())) {
            return new ResponseData<>(StringUtils.EMPTY, hashResp.getErrorCode(),
                hashResp.getErrorMessage());
        }
        if (!WeIdUtils.isPrivateKeyValid(weIdPrivateKey)) {
            return new ResponseData<>(StringUtils.EMPTY,
                ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS);
        }
        String privateKey = weIdPrivateKey.getPrivateKey();
        String extraValue = StringUtils.EMPTY;
        if (extra != null && !extra.isEmpty()) {
            try {
                extraValue = URLEncoder.encode(DataToolUtils.stringMapToCompactJson(extra),
                    StandardCharsets.UTF_8.name());
            } catch (Exception e) {
                logger.error("extra value illegal: {}", extra.toString());
                return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
            }
        }
        try {
            Sign.SignatureData sigData =
                DataToolUtils.signMessage(hashValue, privateKey);
            String signature = new String(
                DataToolUtils.base64Encode(DataToolUtils.simpleSignatureSerialization(sigData)),
                StandardCharsets.UTF_8);
            Long timestamp = DateUtils.getNoMillisecondTimeStamp();
            return evidenceServiceEngine.createEvidenceWithCustomKey(
                hashValue,
                signature,
                extraValue,
                timestamp,
                extraKey,
                privateKey
            );
        } catch (Exception e) {
            logger.error("create evidence failed due to system error. ", e);
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.EvidenceService#getEvidenceByExtraKey(java.lang.String)
     */
    @Override
    public ResponseData<EvidenceInfo> getEvidenceByExtraKey(String extraKey) {

        try {
            return evidenceServiceEngine.getInfoByExtraKey(extraKey);
        } catch (Exception e) {
            logger.error("get evidence failed.", e);
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }
}
