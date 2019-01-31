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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.DynamicArray;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Uint8;
import org.bcos.web3j.crypto.Keys;
import org.bcos.web3j.crypto.Sign;
import org.bcos.web3j.crypto.Sign.SignatureData;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.webank.weid.config.ContractConfig;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.Evidence;
import com.webank.weid.contract.EvidenceFactory;
import com.webank.weid.contract.EvidenceFactory.CreateEvidenceLogEventResponse;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.EvidenceInfo;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.EvidenceService;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.BaseService;
import com.webank.weid.util.CredentialUtils;
import com.webank.weid.util.DataTypetUtils;
import com.webank.weid.util.SignatureUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * Service implementations for operations on Evidence.
 *
 * @author chaoxinhu 2019.1
 */
@Component
public class EvidenceServiceImpl extends BaseService implements EvidenceService {

    private static final Logger logger = LoggerFactory.getLogger(EvidenceServiceImpl.class);

    // Evidence Factory contract instance
    private static EvidenceFactory evidenceFactory;

    // Evidence Factory contract address
    private static String evidenceFactoryAddress;

    private WeIdService weIdService = new WeIdServiceImpl();

    /**
     * Instantiates a new evidence service impl.
     */
    public EvidenceServiceImpl() {
        init();
    }

    private static void init() {
        ContractConfig config = context.getBean(ContractConfig.class);
        evidenceFactoryAddress = config.getEvidenceAddress();
        evidenceFactory = (EvidenceFactory) getContractService(
            evidenceFactoryAddress,
            EvidenceFactory.class);
    }

    /**
     * Create a new evidence to the blockchain and store its address into the credential.
     */
    @Override
    public ResponseData<String> createEvidence(
        Credential credential,
        WeIdPrivateKey weIdPrivateKey) {

        ResponseData<Boolean> innerResponse = CredentialUtils
            .isCreateEvidenceArgsValid(credential, weIdPrivateKey);
        if (!innerResponse.getResult()) {
            logger.error("Create Evidence input format error!");
            return new ResponseData<>(
                StringUtils.EMPTY,
                innerResponse.getErrorCode(),
                innerResponse.getErrorMessage());
        }

        innerResponse = CredentialUtils.isCredentialValid(credential);
        if (!innerResponse.getResult()) {
            logger.error("Create Evidence input format error: credential!");
            return new ResponseData<>(
                StringUtils.EMPTY,
                innerResponse.getErrorCode(),
                innerResponse.getErrorMessage());
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
            Sign.SignatureData sigData = SignatureUtils
                .signMessage(credentialHash, weIdPrivateKey.getPrivateKey());
            Bytes32 r = DataTypetUtils.bytesArrayToBytes32(sigData.getR());
            Bytes32 s = DataTypetUtils.bytesArrayToBytes32(sigData.getS());
            Uint8 v = DataTypetUtils.intToUnt8(new Byte(sigData.getV()).intValue());
            List<Address> signer = new ArrayList<>();
            signer.add(new Address(Keys.getAddress(SignatureUtils
                .createKeyPairFromPrivate(new BigInteger(weIdPrivateKey.getPrivateKey())))));
            Future<TransactionReceipt> future = evidenceFactory.createEvidence(
                new DynamicArray<Bytes32>(generateBytes32List(hashAttributes)),
                new DynamicArray<Address>(signer),
                r,
                s,
                v,
                new DynamicArray<Bytes32>(generateBytes32List(extraValueList))
            );

            TransactionReceipt receipt = future.get(
                WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT,
                TimeUnit.SECONDS);
            List<CreateEvidenceLogEventResponse> eventResponseList =
                EvidenceFactory.getCreateEvidenceLogEvents(receipt);
            CreateEvidenceLogEventResponse event = eventResponseList.get(0);

            if (event != null) {
                innerResponse = verifyCreateEvidenceEvent(event);
                if (!innerResponse.getResult()) {
                    return new ResponseData<>(
                        StringUtils.EMPTY,
                        innerResponse.getErrorCode(),
                        innerResponse.getErrorMessage());
                }
                return new ResponseData<>(event.addr.toString(), ErrorCode.SUCCESS);
            } else {
                logger
                    .error(
                        "create evidence failed due to transcation event decoding failure. ");
                return new ResponseData<>(StringUtils.EMPTY,
                    ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
            }
        } catch (TimeoutException e) {
            logger.error("create evidencefailed due to system timeout. ", e);
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("create evidence failed due to transaction error. ", e);
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("create evidence failed due to system error. ", e);
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }

    private List<Bytes32> generateBytes32List(List<String> bytes32List) {
        int desiredLength = bytes32List.size();
        List<Bytes32> finalList = new ArrayList<>();
        for (int i = 0; i < desiredLength; i++) {
            finalList.add(DataTypetUtils.stringToBytes32(bytes32List.get(i)));
        }
        return finalList;
    }

    /**
     * Get the evidence from blockchain.
     *
     * @param evidenceAddress the evidence address on chain
     * @return The EvidenceInfo
     */
    @Override
    public ResponseData<EvidenceInfo> getEvidence(String evidenceAddress) {
        ResponseData<EvidenceInfo> responseData = new ResponseData<>();
        if (StringUtils.isEmpty(evidenceAddress) || !WeIdUtils.isValidAddress(evidenceAddress)) {
            logger.error("Evidence argument illegal input: address. ");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }

        Evidence evidence = (Evidence) getContractService(evidenceAddress, Evidence.class);

        try {
            List<Type> rawResult =
                evidence.getInfo()
                    .get(WeIdConstant.TRANSACTION_RECEIPT_TIMEOUT, TimeUnit.SECONDS);
            if (rawResult == null) {
                return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
            }

            List<Bytes32> credentialHashList = ((DynamicArray<Bytes32>) rawResult.get(0))
                .getValue();
            List<Address> issuerList = ((DynamicArray<Address>) rawResult.get(1)).getValue();

            EvidenceInfo evidenceInfoData = new EvidenceInfo();
            evidenceInfoData.setCredentialHash(
                WeIdConstant.HEX_PREFIX + DataTypetUtils
                    .bytes32ToString(credentialHashList.get(0))
                    + DataTypetUtils.bytes32ToString(credentialHashList.get(1)));

            List<String> signerStringList = new ArrayList<>();
            for (Address addr : issuerList) {
                signerStringList.add(addr.toString());
            }
            evidenceInfoData.setSigners(signerStringList);

            List<String> signaturesList = new ArrayList<>();
            List<Bytes32> rlist = ((DynamicArray<Bytes32>) rawResult.get(2)).getValue();
            List<Bytes32> slist = ((DynamicArray<Bytes32>) rawResult.get(3)).getValue();
            List<Uint8> vlist = ((DynamicArray<Uint8>) rawResult.get(4)).getValue();
            byte v;
            byte[] r;
            byte[] s;
            for (int index = 0; index < rlist.size(); index++) {
                v = (byte) (vlist.get(index).getValue().intValue());
                r = rlist.get(index).getValue();
                s = slist.get(index).getValue();
                SignatureData sigData = new SignatureData(v, r, s);
                signaturesList.add(new String(
                    SignatureUtils
                        .base64Encode(SignatureUtils.simpleSignatureSerialization(sigData)),
                    WeIdConstant.UTF_8));
            }
            evidenceInfoData.setSignatures(signaturesList);

            responseData.setResult(evidenceInfoData);
            return responseData;
        } catch (TimeoutException e) {
            logger.error("get evidence failed due to system timeout. ", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_TIMEOUT);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("get evidence failed due to transaction error. ", e);
            return new ResponseData<>(null, ErrorCode.TRANSACTION_EXECUTE_ERROR);
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
        ResponseData<Boolean> innerResponse = CredentialUtils
            .isCredentialValid(credential);
        if (!innerResponse.getResult()) {
            logger.error("Verify EvidenceInfo input illegal: credential");
            return innerResponse;
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
                innerEvidenceResponseData.getErrorCode(),
                innerEvidenceResponseData.getErrorMessage());
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
                    SignatureUtils.simpleSignatureDeserialization(
                        SignatureUtils.base64Decode(
                            signature.getBytes(WeIdConstant.UTF_8)));

                ResponseData<Boolean> innerResponseData = verifySignatureToSigner(
                    hashOffChain,
                    WeIdUtils.convertAddressToWeId(signer),
                    signatureData);
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

    private ResponseData<Boolean> verifyCreateEvidenceEvent(CreateEvidenceLogEventResponse event) {
        if (event.retCode == null || event.addr == null) {
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        Integer eventRetCode = event.retCode.getValue().intValue();
        if (eventRetCode
            .equals(ErrorCode.CREDENTIAL_EVIDENCE_CONTRACT_FAILURE_ILLEAGAL_INPUT.getCode())) {
            return new ResponseData<>(false,
                ErrorCode.CREDENTIAL_EVIDENCE_CONTRACT_FAILURE_ILLEAGAL_INPUT);
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }

    private ResponseData<Boolean> verifySignatureToSigner(String rawData,
        String signerWeId, SignatureData signatureData) {

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
            return SignatureUtils
                .verifySignatureFromWeId(rawData, signatureData, weIdDocument);
        } catch (Exception e) {
            logger.error("error occurred during verifying signatures from chain: ", e);
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }
}
