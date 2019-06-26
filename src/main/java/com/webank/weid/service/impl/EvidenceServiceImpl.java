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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.crypto.Sign;
import org.bcos.web3j.crypto.Sign.SignatureData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.EvidenceInfo;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.EvidenceService;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.BaseService;
import com.webank.weid.service.impl.engine.EngineFactory;
import com.webank.weid.service.impl.engine.EvidenceServiceEngine;
import com.webank.weid.util.CredentialUtils;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * Service implementations for operations on Evidence.
 *
 * @author chaoxinhu 2019.1
 */
public class EvidenceServiceImpl extends BaseService implements EvidenceService {

    private static final Logger logger = LoggerFactory.getLogger(EvidenceServiceImpl.class);

    private WeIdService weIdService = new WeIdServiceImpl();
    
    private EvidenceServiceEngine evidenceServiceEngine = 
        EngineFactory.createEvidenceServiceEngine();
    
    /**
     * Create a new evidence to the blockchain and store its address into the credential.
     */
    @Override
    public ResponseData<String> createEvidence(
        Credential credential,
        WeIdPrivateKey weIdPrivateKey) {

        ErrorCode innerResponse = CredentialUtils
            .isCreateEvidenceArgsValid(credential, weIdPrivateKey);
        if (ErrorCode.SUCCESS.getCode() != innerResponse.getCode()) {
            logger.error("Create Evidence input format error!");
            return new ResponseData<>(StringUtils.EMPTY, innerResponse);
        }

        innerResponse = CredentialUtils.isCredentialValid(credential);
        if (ErrorCode.SUCCESS.getCode() != innerResponse.getCode()) {
            logger.error("Create Evidence input format error: credential!");
            return new ResponseData<>(StringUtils.EMPTY, innerResponse);
        }

        try {
            String credentialHash = CredentialUtils.getCredentialHash(credential);
            String credentialHashOnChain = credentialHash
                .replaceAll(WeIdConstant.HEX_PREFIX, StringUtils.EMPTY);
            List<String> hashAttributes = new ArrayList<>();
            hashAttributes.add(
                credentialHashOnChain.substring(0, WeIdConstant.BYTES32_FIXED_LENGTH));
            hashAttributes.add(
                credentialHashOnChain.substring(
                    WeIdConstant.BYTES32_FIXED_LENGTH,
                    WeIdConstant.BYTES32_FIXED_LENGTH * 2
                ));
            List<String> extraValueList = new ArrayList<>();
            extraValueList.add(StringUtils.EMPTY);
            Sign.SignatureData sigData = 
                DataToolUtils.signMessage(credentialHash, weIdPrivateKey.getPrivateKey());
            return evidenceServiceEngine.createEvidence(
                sigData,
                hashAttributes,
                extraValueList,
                weIdPrivateKey.getPrivateKey()
            );
        }  catch (Exception e) {
            logger.error("create evidence failed due to system error. ", e);
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }

    /**
     * Get the evidence from blockchain.
     *
     * @param evidenceAddress the evidence address on chain
     * @return The EvidenceInfo
     */
    @Override
    public ResponseData<EvidenceInfo> getEvidence(String evidenceAddress) {
        if (StringUtils.isEmpty(evidenceAddress) || !WeIdUtils.isValidAddress(evidenceAddress)) {
            logger.error("Evidence argument illegal input: address. ");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        try {
             return evidenceServiceEngine.getInfo(evidenceAddress);
        } catch (Exception e) {
            logger.error("get evidence failed.", e);
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }

    /**
     * Verify a Credential based on its Evidence info. A Credential might contain multiple evidence
     * addresses. Anyone successfully verified will lead to a true outcome.
     *
     * @param credential the args
     * @return true if succeeds, false otherwise
     */
    @Override
    public ResponseData<Boolean> verify(Credential credential, String evidenceAddress) {
        ErrorCode innerResponse = CredentialUtils
            .isCredentialValid(credential);
        if (ErrorCode.SUCCESS.getCode() != innerResponse.getCode()) {
            logger.error("Verify EvidenceInfo input illegal: credential");
            return new ResponseData<>(
                false,
                innerResponse
            );
        }
        if (!WeIdUtils.isValidAddress(evidenceAddress)) {
            logger.error("Verify EvidenceInfo input illegal: evidenceInfo address");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }

        // Step 1: Get EvidenceInfo from chain
        ResponseData<EvidenceInfo> innerEvidenceResponseData = getEvidence(evidenceAddress);
        if (innerEvidenceResponseData.getResult() == null) {
            return new ResponseData<>(
                false,
                ErrorCode.getTypeByErrorCode(innerEvidenceResponseData.getErrorCode())
            );
        }

        EvidenceInfo evidenceInfo = innerEvidenceResponseData.getResult();

        // Step 2: Verify Hash value
        String hashOffChain = CredentialUtils.getCredentialHash(credential);
        if (!StringUtils.equalsIgnoreCase(hashOffChain, evidenceInfo.getCredentialHash())) {
            logger.error(
                "credential hash mismatches. Off-chain: {}, on-chain: {}", hashOffChain,
                evidenceInfo.getCredentialHash());
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_EVIDENCE_HASH_MISMATCH);
        }

        // Step 3: Verify each signature value in EvidenceInfo wrt the signer based on their their
        // publickeys from WeIDContract. Here each signature/signer pair must pass the verification.
        try {
            for (int i = 0; i < evidenceInfo.getSignatures().size(); i++) {
                String signer = evidenceInfo.getSigners().get(i);
                String signature = evidenceInfo.getSignatures().get(i);
                if (WeIdUtils.isEmptyAddress(new Address(signer))) {
                    break;
                }
                SignatureData signatureData =
                    DataToolUtils.simpleSignatureDeserialization(
                        DataToolUtils.base64Decode(signature.getBytes(StandardCharsets.UTF_8))
                    );

                ResponseData<Boolean> innerResponseData = verifySignatureToSigner(
                    hashOffChain,
                    WeIdUtils.convertAddressToWeId(signer),
                    signatureData
                );
                if (!innerResponseData.getResult()) {
                    return innerResponseData;
                }
            }
        } catch (Exception e) {
            logger.error(
                "Generic error occurred during verify evidenceInfo: ", e);
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
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
}
