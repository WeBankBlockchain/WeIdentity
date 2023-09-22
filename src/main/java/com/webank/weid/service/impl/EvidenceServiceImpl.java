

package com.webank.weid.service.impl;

import com.google.common.base.Charsets;
import com.google.common.io.Files;


import com.webank.weid.service.local.EvidenceServiceLocal;
import com.webank.weid.service.local.WeIdServiceLocal;
import com.webank.weid.service.rpc.EvidenceService;
import com.webank.weid.service.rpc.WeIdService;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.constant.ProcessingMode;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.protocol.base.*;
import com.webank.weid.protocol.inf.Hashable;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service implementations for operations on Evidence.
 *
 * @author afeexian 2022.10
 */
public class EvidenceServiceImpl implements EvidenceService {

    private static final Logger logger = LoggerFactory.getLogger(EvidenceServiceImpl.class);

    private WeIdService weIdService = new WeIdServiceImpl();

    private ProcessingMode processingMode = ProcessingMode.IMMEDIATE;

    //private EvidenceServiceEngine evidenceServiceEngine;

    private static com.webank.weid.blockchain.rpc.EvidenceService evidenceBlockchainService;

    //private String groupId;

    public EvidenceServiceImpl() {
        //super();
        //initEvidenceServiceEngine(masterGroupId);
        evidenceBlockchainService = getEvidenceService(StringUtils.EMPTY);
    }

    private static com.webank.weid.blockchain.rpc.EvidenceService getEvidenceService(String groupId) {
        if(evidenceBlockchainService != null) {
            return evidenceBlockchainService;
        } else {
            String type = PropertyUtils.getProperty("deploy.style");
            if (type.equals("blockchain")) {
                if(!groupId.equals(StringUtils.EMPTY)) {
                    return new com.webank.weid.blockchain.service.impl.EvidenceServiceImpl(groupId);
                } else {
                    return new com.webank.weid.blockchain.service.impl.EvidenceServiceImpl();
                }
            } else {
                // default database
                return new EvidenceServiceLocal();
            }
        }
    }

    /**
     * 传入processingMode来决定上链模式，仅适用于FiscoBcos区块链，用于初始化不同群组的evidence服务.
     *
     * @param processingMode 上链模式
     * @param groupId 群组编号
     */
    public EvidenceServiceImpl(ProcessingMode processingMode, String groupId) {
        //super(groupId);
        this.processingMode = processingMode;
        //initEvidenceServiceEngine(groupId);
        evidenceBlockchainService = getEvidenceService(groupId);
    }

    /*private void initEvidenceServiceEngine(String groupId) {
        evidenceServiceEngine = EngineFactory.createEvidenceServiceEngine(groupId);
        this.groupId = groupId;
    }*/

    @Override
    public ResponseData<Boolean> createRawEvidenceWithCustomKey(
        String hashValue,
        String signature,
        String log,
        Long timestamp,
        String extraKey,
        String privateKey
    ) {
        com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> hashResp = evidenceBlockchainService.createEvidenceWithCustomKey(
            hashValue,
            signature,
            log,
            timestamp,
            extraKey,
            privateKey
        );
        if (hashResp.getErrorCode() == ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(true, ErrorCode.SUCCESS);
        } else {
            return new ResponseData<>(false, hashResp.getErrorCode(), hashResp.getErrorMessage());
        }
    }


    @Override
    public ResponseData<Boolean> createRawEvidenceWithSpecificSigner(
        String hashValue,
        String signature,
        String log,
        Long timestamp,
        String extraKey,
        String signer,
        String privateKey
    ) {
        List<String> hashValues = new ArrayList<>();
        hashValues.add(hashValue);
        List<String> signatures = new ArrayList<>();
        signatures.add(signature);
        List<String> logs = new ArrayList<>();
        logs.add(log);
        List<Long> timestamps = new ArrayList<>();
        timestamps.add(timestamp);
        List<String> signers = new ArrayList<>();
        signers.add(signer);
        if (StringUtils.isEmpty(extraKey)) {
            com.webank.weid.blockchain.protocol.response.ResponseData<List<Boolean>> hashResp = evidenceBlockchainService.batchCreateEvidence(
                    hashValues,
                    signatures,
                    logs,
                    timestamps,
                    signers,
                    privateKey
            );
            if (hashResp.getErrorCode() == ErrorCode.SUCCESS.getCode() && hashResp.getResult().get(0)) {
                return new ResponseData<>(true, ErrorCode.SUCCESS);
            } else {
                return new ResponseData<>(false, hashResp.getErrorCode(), hashResp.getErrorMessage());
            }
        } else {
            List<String> extraKeys = new ArrayList<>();
            extraKeys.add(extraKey);
            com.webank.weid.blockchain.protocol.response.ResponseData<List<Boolean>> hashResp = evidenceBlockchainService.batchCreateEvidenceWithCustomKey(
                    hashValues,
                    signatures,
                    logs,
                    timestamps,
                    signers,
                    extraKeys,
                    privateKey
            );
            if (hashResp.getErrorCode() == ErrorCode.SUCCESS.getCode() && hashResp.getResult().get(0)) {
                return new ResponseData<>(true, ErrorCode.SUCCESS);
            } else {
                return new ResponseData<>(false, hashResp.getErrorCode(), hashResp.getErrorMessage());
            }
        }
    }

    /**
     * Create a new evidence to the blockchain and get the evidence address.
     *
     * @param object the given Java object
     * @param weIdPrivateKey the caller WeID Authentication
     * @return Evidence address
     */
    @Override
    public ResponseData<String> createEvidence(Hashable object, WeIdPrivateKey weIdPrivateKey) {
        ResponseData<String> hashResp = getHashValue(object);
        if (StringUtils.isEmpty(hashResp.getResult())) {
            return new ResponseData<>(StringUtils.EMPTY, hashResp.getErrorCode(),
                hashResp.getErrorMessage());
        }
        if (!WeIdUtils.isPrivateKeyValid(weIdPrivateKey)) {
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        return hashToNewEvidence(hashResp.getResult(), weIdPrivateKey.getPrivateKey(),
            "empty log");
    }

    /**
     * Create a new evidence to blockchain and return the hash value, with appending log. This will
     * fail if evidence already exists.
     *
     * @param object the given Java object
     * @param log appendable log entry - can be null or empty
     * @param weIdAuthentication weid authentication (only checks private key)
     */
    @Override
    public ResponseData<String> createEvidenceWithLog(
        Hashable object,
        String log,
        WeIdAuthentication weIdAuthentication
    ) {
        ResponseData<String> hashResp = getHashValue(object);
        if (StringUtils.isEmpty(hashResp.getResult())) {
            return new ResponseData<>(StringUtils.EMPTY, hashResp.getErrorCode(),
                hashResp.getErrorMessage());
        }
        if (weIdAuthentication == null
            || !WeIdUtils.isPrivateKeyValid(weIdAuthentication.getWeIdPrivateKey())) {
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        if (!DataToolUtils.isUtf8String(log)) {
            logger.error("Evidence argument illegal input: log.");
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
        }
        if (!isChainStringLengthValid(log)) {
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ON_CHAIN_STRING_TOO_LONG);
        }
        return hashToNewEvidence(
            hashResp.getResult(),
            weIdAuthentication.getWeIdPrivateKey().getPrivateKey(),
            log);
    }

    /**
     * Add log entry for an existing evidence. This log will be recorded on blockchain permanently,
     * and finally it will be fetched as a list when trying to get evidence.
     *
     * @param hashValue hash value
     * @param log not null log entry
     * @param weIdPrivateKey the signer WeID's private key
     */
    @Override
    public ResponseData<Boolean> addLogByHash(String hashValue, String log,
        WeIdPrivateKey weIdPrivateKey) {
        return addByHash(
            hashValue,
            log,
            weIdPrivateKey,
            false
        );
    }

    /**
     * Add signature and log as a new signer to an existing evidence. Log can be empty.
     *
     * @param hashValue hash value
     * @param log log entry
     * @param weIdPrivateKey the signer WeID's private key
     * @return true if succeeded, false otherwise
     */
    @Override
    public ResponseData<Boolean> addSignatureAndLogByHash(String hashValue, String log,
        WeIdPrivateKey weIdPrivateKey) {
        return addByHash(
            hashValue,
            log,
            weIdPrivateKey,
            true
        );
    }

    private ResponseData<Boolean> addByHash(
        String hashValue,
        String log,
        WeIdPrivateKey weIdPrivateKey,
        boolean requireSig
    ) {
        if (!DataToolUtils.isValidHash(hashValue) || (StringUtils.isEmpty(log) && !requireSig)
            || !DataToolUtils.isUtf8String(log)) {
            logger.error("Evidence argument illegal input: hash or log.");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (!isChainStringLengthValid(log)) {
            return new ResponseData<>(false, ErrorCode.ON_CHAIN_STRING_TOO_LONG);
        }
        if (!WeIdUtils.isPrivateKeyValid(weIdPrivateKey)) {
            return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        Long timestamp = DateUtils.getNoMillisecondTimeStamp();
        if (requireSig) {
            /*String signature = DataToolUtils.secp256k1Sign(hashValue,
                new BigInteger(weIdPrivateKey.getPrivateKey()));*/
            String signature = DataToolUtils.SigBase64Serialization(
                    DataToolUtils.signToRsvSignature(hashValue, weIdPrivateKey.getPrivateKey())
            );
            com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> hashResp = evidenceBlockchainService.addLog(
                    hashValue,
                    signature,
                    log,
                    timestamp,
                    weIdPrivateKey.getPrivateKey()
            );
            if (hashResp.getErrorCode() == ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(true, ErrorCode.SUCCESS);
            } else {
                return new ResponseData<>(false, hashResp.getErrorCode(), hashResp.getErrorMessage());
            }
        } else {
            com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> hashResp = evidenceBlockchainService.addLog(
                    hashValue,
                    StringUtils.EMPTY,
                    log,
                    timestamp,
                    weIdPrivateKey.getPrivateKey()
            );
            if (hashResp.getErrorCode() == ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(true, ErrorCode.SUCCESS);
            } else {
                return new ResponseData<>(false, hashResp.getErrorCode(), hashResp.getErrorMessage());
            }
        }
    }

    /**
     * Add log entry for an existing evidence, appending on existing log entries. This log will be
     * recorded on blockchain permanently, and finally it will be fetched as a list when trying to
     * get evidence. Log must not be empty. It will firstly try to fetch the hash value given the
     * custom key, and if the hash value does not exist, it will use the supplementing hash value
     * (1st parameter) to make up.
     *
     * @param hashValueSupplement the hash value supplement if the custom key does not exist
     * @param customKey custom key
     * @param log Not null log entry
     * @param weIdPrivateKey the signer WeID's private key
     * @return true if succeeded, false otherwise
     */
    @Override
    public ResponseData<Boolean> addLogByCustomKey(
        String hashValueSupplement,
        String customKey,
        String log,
        WeIdPrivateKey weIdPrivateKey) {
        return addByCustomKey(
            hashValueSupplement,
            customKey,
            log,
            weIdPrivateKey,
            false
        );
    }

    /**
     * Add signature and log as a new signer to an existing evidence. Log can be empty.
     *
     * @param hashValueSupplement the hash value supplement if the custom key does not exist
     * @param customKey custom key
     * @param log log entry
     * @param weIdPrivateKey the signer WeID's private key
     * @return true if succeeded, false otherwise
     */
    @Override
    public ResponseData<Boolean> addSignatureAndLogByCustomKey(
        String hashValueSupplement,
        String customKey,
        String log,
        WeIdPrivateKey weIdPrivateKey
    ) {
        return addByCustomKey(
            hashValueSupplement,
            customKey,
            log,
            weIdPrivateKey,
            true
        );
    }

    private ResponseData<Boolean> addByCustomKey(
        String hashValueSupplement,
        String customKey,
        String log,
        WeIdPrivateKey weIdPrivateKey,
        boolean requireSig
    ) {
        if ((StringUtils.isEmpty(log) && !requireSig) || !DataToolUtils.isUtf8String(log)) {
            logger.error("Evidence argument illegal input: log.");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (StringUtils.isEmpty(customKey) || !DataToolUtils.isUtf8String(customKey)) {
            logger.error("Evidence argument illegal input: customKey.");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (!isChainStringLengthValid(log)) {
            return new ResponseData<>(false, ErrorCode.ON_CHAIN_STRING_TOO_LONG);
        }
        com.webank.weid.blockchain.protocol.response.ResponseData<String> hashResp = evidenceBlockchainService.getHashByCustomKey(customKey);
        String hashValue = hashResp.getResult();
        if (StringUtils.isEmpty(hashValue)) {
            logger.error("Failed to find the hash value from custom key: ", customKey);
            if (StringUtils.isEmpty(hashValueSupplement)) {
                return new ResponseData<>(false, hashResp.getErrorCode(),
                    hashResp.getErrorMessage());
            }
            hashValue = hashValueSupplement;
        }
        Long timestamp = DateUtils.getNoMillisecondTimeStamp();
        if (requireSig) {
            /*String signature = DataToolUtils.secp256k1Sign(hashValue,
                new BigInteger(weIdPrivateKey.getPrivateKey()));*/
            String signature = DataToolUtils.SigBase64Serialization(
                    DataToolUtils.signToRsvSignature(hashValue, weIdPrivateKey.getPrivateKey())
            );
            com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> resp = evidenceBlockchainService.addLogByCustomKey(
                    hashValue,
                    signature,
                    log,
                    timestamp,
                    customKey,
                    weIdPrivateKey.getPrivateKey()
            );
            if (resp.getErrorCode() == ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(true, ErrorCode.SUCCESS);
            } else {
                return new ResponseData<>(false, resp.getErrorCode(), resp.getErrorMessage());
            }
        } else {
            com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> resp = evidenceBlockchainService.addLogByCustomKey(
                    hashValue,
                    StringUtils.EMPTY,
                    log,
                    timestamp,
                    customKey,
                    weIdPrivateKey.getPrivateKey()
            );
            if (resp.getErrorCode() == ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(true, ErrorCode.SUCCESS);
            } else {
                return new ResponseData<>(false, resp.getErrorCode(), resp.getErrorMessage());
            }
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.rpc.generateHash
     * #generateHash(T object)
     */
    @Override
    public <T> ResponseData<HashString> generateHash(T object) {
        if (object == null) {
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        //替换国密
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
            return new ResponseData<>(new HashString(DataToolUtils.hash(rawData)),
                ErrorCode.SUCCESS);
        }
        if (object instanceof String) {
            if (StringUtils.isEmpty((String) object)) {
                logger.error("Input String is blank, ignored..");
                return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
            }
            return new ResponseData<>(new HashString(DataToolUtils.hash((String) object)),
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
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
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
            //替换国密
            //String signature = DataToolUtils.secp256k1Sign(hashValue, new BigInteger(privateKey));
            String signature = DataToolUtils.SigBase64Serialization(
                    DataToolUtils.signToRsvSignature(hashValue, privateKey)
            );
            Long timestamp = DateUtils.getCurrentTimeStamp();
            if (processingMode == ProcessingMode.PERIODIC_AND_BATCH) {
                String[] args = new String[6];
                args[0] = hashValue;
                args[1] = signature;
                args[2] = extra;
                args[3] = String.valueOf(timestamp);
                args[4] = privateKey;
                args[5] = String.valueOf(evidenceBlockchainService.getGroupId());
                String rawData = new StringBuffer()
                    .append(hashValue)
                    .append(signature)
                    .append(extra)
                    .append(timestamp)
                    .append(WeIdUtils.getWeIdFromPrivateKey(privateKey))
                    .append(evidenceBlockchainService.getGroupId()).toString();
                //替换国密
                String hash = DataToolUtils.hash(rawData);
                String requestId = new BigInteger(hash.substring(2), 16).toString();
                boolean isSuccess = BatchTransactionUtils
                    .writeTransaction(requestId, "createEvidence", args, StringUtils.EMPTY);
                if (isSuccess) {
                    return new ResponseData<>(hashValue, ErrorCode.SUCCESS);
                } else {
                    return new ResponseData<>(hashValue, ErrorCode.OFFLINE_EVIDENCE_SAVE_FAILED);
                }
            }
            com.webank.weid.blockchain.protocol.response.ResponseData<String> resp = evidenceBlockchainService.createEvidence(
                    hashValue,
                    signature,
                    extra,
                    timestamp,
                    privateKey
            );
            if (resp.getErrorCode() == ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(resp.getResult(), ErrorCode.SUCCESS);
            } else {
                return new ResponseData<>(StringUtils.EMPTY, resp.getErrorCode(), resp.getErrorMessage());
            }
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
        com.webank.weid.blockchain.protocol.response.ResponseData<com.webank.weid.blockchain.protocol.base.EvidenceInfo> hashResp = evidenceBlockchainService.getInfo(evidenceKey);
        if(hashResp.getErrorCode() == ErrorCode.SUCCESS.getCode()){
            EvidenceInfo evidenceInfo = EvidenceInfo.fromBlockChain(hashResp.getResult());
            return new ResponseData<>(evidenceInfo, ErrorCode.SUCCESS);
        } else {
            return new ResponseData<>(null, hashResp.getErrorCode(), hashResp.getErrorMessage());
        }
    }

    /**
     * Validate whether a credential created the evidence, and this evidence is signed by this WeID
     * - will perform on-Chain key check.
     *
     * @param credentialPojo the credentialPojo
     * @param evidenceInfo the evidence info fetched from chain
     * @param weId the WeID
     * @return true if yes, false otherwise
     */
    @Override
    public ResponseData<Boolean> verifySigner(
        CredentialPojo credentialPojo,
        EvidenceInfo evidenceInfo,
        String weId
    ) {
        return verifySigner(credentialPojo, evidenceInfo, weId, null);
    }

    /**
     * Validate whether a credential created the evidence, and this evidence is signed by this WeID
     * based on the passed-in publicKey.
     *
     * @param credentialPojo the credentialPojo
     * @param evidenceInfo the evidence info fetched from chain
     * @param weId the WeID
     * @param publicKey the public key
     * @return true if yes, false otherwise
     */
    @Override
    public ResponseData<Boolean> verifySigner(
        CredentialPojo credentialPojo,
        EvidenceInfo evidenceInfo,
        String weId,
        String publicKey) {
        if (evidenceInfo == null || evidenceInfo.getSigners().isEmpty()) {
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if (!WeIdUtils.isWeIdValid(weId)) {
            return new ResponseData<>(false, ErrorCode.WEID_INVALID);
        }
        if (!evidenceInfo.getSigners().contains(WeIdUtils.convertWeIdToAddress(weId))) {
            logger.error("This Evidence does not contain the provided WeID: {}", weId);
            return new ResponseData<>(false, ErrorCode.WEID_DOES_NOT_EXIST);
        }

        // 1st: verify hash (accept both thumbprint hash or credential.getHash())
        if (!evidenceInfo.getCredentialHash().equalsIgnoreCase(credentialPojo.getHash())) {
            if (CredentialPojoUtils.isLiteCredential(credentialPojo)) {
                if (!evidenceInfo.getCredentialHash().equalsIgnoreCase(DataToolUtils.hash(
                    CredentialPojoUtils.getLiteCredentialThumbprintWithoutSig(credentialPojo)))) {
                    logger.error("Evidence hash mismatches the lite credential hash or thumbprint");
                    return new ResponseData<>(false, ErrorCode.CREDENTIAL_EVIDENCE_HASH_MISMATCH);
                }
            } else {
                if (CredentialPojoUtils.isEmbeddedCredential(credentialPojo)) {
                    // Currently unsupported for embedded credential thumbprint hash case
                    logger.error("Evidence hash mismatches the embedded credential hash");
                    return new ResponseData<>(false, ErrorCode.CREDENTIAL_EVIDENCE_HASH_MISMATCH);
                } else {
                    if (!evidenceInfo.getCredentialHash().equalsIgnoreCase(
                            DataToolUtils.hash(
                            CredentialPojoUtils.getCredentialThumbprintWithoutSig(credentialPojo,
                                credentialPojo.getSalt(), null)))) {
                        logger.error("Evidence hash mismatches the non-embedded credential hash or"
                            + "thumbprint");
                        return new ResponseData<>(false,
                            ErrorCode.CREDENTIAL_EVIDENCE_HASH_MISMATCH);
                    }
                }
            }
        }

        // 2nd: verify signer = issuer
        if (!credentialPojo.getIssuer().equalsIgnoreCase(weId)) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_ISSUER_MISMATCH);
        }

        // 3rd: verify signature w.r.t. weid (must exist and must be the signer (from pubkey))
        EvidenceSignInfo signInfo = evidenceInfo.getSignInfo().get(WeIdUtils.convertWeIdToAddress(weId));
        String signature = signInfo.getSignature();
        if (!DataToolUtils.isValidBase64String(signature)) {
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_EVIDENCE_SIGNATURE_BROKEN);
        }
        /*SignatureData signatureData =
            DataToolUtils.simpleSignatureDeserialization(
                DataToolUtils.base64Decode(signature.getBytes(StandardCharsets.UTF_8))
            );*/

        // Firstly, we check the secp256k1 style signature
        if (StringUtils.isEmpty(publicKey)) {
            ResponseData<Boolean> verifyResp = verifySignatureToSigner(
                evidenceInfo.getCredentialHash(),
                    weId,
                signature);
            return verifyResp;
        } else {
            try {
                /*boolean result = DataToolUtils
                    .verifySecp256k1Signature(evidenceInfo.getCredentialHash(), signature,
                        new BigInteger(publicKey));*/
                boolean result = DataToolUtils.verifySignature(evidenceInfo.getCredentialHash(), signature,
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

    private ResponseData<Boolean> verifySignatureToSigner(
        String rawData,
        String signerWeId,
        String secp256k1sig
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
            /*ErrorCode errorCode = DataToolUtils
                .verifySecp256k1SignatureFromWeId(rawData, secp256k1sig, weIdDocument, null);*/
            ErrorCode errorCode = DataToolUtils
                    .verifySignatureFromWeId(rawData, secp256k1sig, weIdDocument, null);
            if (errorCode.getCode() != ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(false, errorCode);
            }
            return new ResponseData<>(true, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("error occurred during verifying signatures from chain: ", e);
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.rpc.EvidenceService#createEvidenceWithLogAndCustomKey(
     * com.webank.weid.protocol.inf.Hashable, com.webank.weid.protocol.base.WeIdPrivateKey,
     * java.lang.String)
     */
    @Override
    public ResponseData<String> createEvidenceWithLogAndCustomKey(
        Hashable object,
        WeIdPrivateKey weIdPrivateKey,
        String log,
        String customKey) {
        if (StringUtils.isEmpty(customKey) || !DataToolUtils.isUtf8String(customKey)) {
            logger.error("Custom key must be non-empty and must not be of hash format.");
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
        }
        if (!DataToolUtils.isUtf8String(log)) {
            logger.error("Log format illegal.");
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
        }
        if (StringUtils.isEmpty(log)) {
            log = StringUtils.EMPTY;
        }
        if (!isChainStringLengthValid(log) || !isChainStringLengthValid(customKey)) {
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ON_CHAIN_STRING_TOO_LONG);
        }
        if (StringUtils.isEmpty(customKey)) {
            customKey = StringUtils.EMPTY;
        }
        ResponseData<String> hashResp = getHashValue(object);
        String hashValue = hashResp.getResult();
        if (StringUtils.isEmpty(hashResp.getResult())) {
            return new ResponseData<>(StringUtils.EMPTY, hashResp.getErrorCode(),
                hashResp.getErrorMessage());
        }
        if (!WeIdUtils.isPrivateKeyValid(weIdPrivateKey)) {
            return new ResponseData<>(StringUtils.EMPTY,
                ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        String privateKey = weIdPrivateKey.getPrivateKey();
        try {
            //String signature = DataToolUtils.secp256k1Sign(hashValue, new BigInteger(privateKey));
            String signature = DataToolUtils.SigBase64Serialization(
                    DataToolUtils.signToRsvSignature(hashValue, weIdPrivateKey.getPrivateKey())
            );
            Long timestamp = DateUtils.getCurrentTimeStamp();

            if (processingMode == ProcessingMode.PERIODIC_AND_BATCH) {
                String[] args = new String[7];
                args[0] = hashValue;
                args[1] = signature;
                args[2] = log;
                args[3] = String.valueOf(timestamp);
                args[4] = customKey;
                args[5] = privateKey;
                args[6] = String.valueOf(evidenceBlockchainService.getGroupId());
                String rawData = new StringBuffer()
                    .append(hashValue)
                    .append(signature)
                    .append(log)
                    .append(timestamp)
                    .append(customKey)
                    .append(WeIdUtils.getWeIdFromPrivateKey(privateKey))
                    .append(evidenceBlockchainService.getGroupId()).toString();
                String hash = DataToolUtils.hash(rawData);
                String requestId = new BigInteger(hash.substring(2), 16).toString();
                boolean isSuccess = BatchTransactionUtils
                    .writeTransaction(requestId, "createEvidenceWithCustomKey", args,
                        StringUtils.EMPTY);
                if (isSuccess) {
                    return new ResponseData<>(hashValue, ErrorCode.SUCCESS);
                } else {
                    return new ResponseData<>(hashValue, ErrorCode.OFFLINE_EVIDENCE_SAVE_FAILED);
                }
            }
            com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> resp = evidenceBlockchainService.createEvidenceWithCustomKey(
                    hashValue,
                    signature,
                    log,
                    timestamp,
                    customKey,
                    privateKey
            );
            if (resp.getErrorCode() == ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(hashValue, ErrorCode.SUCCESS);
            } else {
                return new ResponseData<>(StringUtils.EMPTY, resp.getErrorCode(), resp.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("create evidence failed due to system error. ", e);
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.rpc.EvidenceService#getEvidenceByCustomKey(java.lang.String)
     */
    @Override
    public ResponseData<EvidenceInfo> getEvidenceByCustomKey(String customKey) {
        if (!isChainStringLengthValid(customKey)) {
            return new ResponseData<>(null, ErrorCode.ON_CHAIN_STRING_TOO_LONG);
        }
        com.webank.weid.blockchain.protocol.response.ResponseData<com.webank.weid.blockchain.protocol.base.EvidenceInfo> hashResp = evidenceBlockchainService.getInfoByCustomKey(customKey);
        if(hashResp.getErrorCode() == ErrorCode.SUCCESS.getCode()){
            EvidenceInfo evidenceInfo = EvidenceInfo.fromBlockChain(hashResp.getResult());
            return new ResponseData<>(evidenceInfo, ErrorCode.SUCCESS);
        } else {
            return new ResponseData<>(null, hashResp.getErrorCode(), hashResp.getErrorMessage());
        }
    }

    private boolean isChainStringLengthValid(String string) {
        return string.length() < WeIdConstant.ON_CHAIN_STRING_LENGTH;
    }

    /**
     * Revoke an evidence - which can be un-revoked.
     *
     * @param object the object
     * @param weIdAuthentication the weid authentication
     * @return true if yes, false otherwise, with error codes
     */
    @Override
    public ResponseData<Boolean> revoke(Hashable object, WeIdAuthentication weIdAuthentication) {
        ResponseData<String> hashResp = getHashValue(object);
        if (StringUtils.isEmpty(hashResp.getResult())) {
            return new ResponseData<>(false, hashResp.getErrorCode(),
                hashResp.getErrorMessage());
        }
        if (weIdAuthentication == null
            || !WeIdUtils.isPrivateKeyValid(weIdAuthentication.getWeIdPrivateKey())) {
            return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        Long timestamp = DateUtils.getNoMillisecondTimeStamp();
        com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> resp = evidenceBlockchainService.revoke(
                hashResp.getResult(),
                true,
                timestamp,
                weIdAuthentication.getWeIdPrivateKey().getPrivateKey()
        );
        if (resp.getErrorCode() == ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(resp.getResult(), ErrorCode.SUCCESS);
        } else {
            return new ResponseData<>(false, resp.getErrorCode(), resp.getErrorMessage());
        }
    }

    /**
     * Revoke an evidence - which can be un-revoked.
     *
     * @param object the object
     * @param weIdAuthentication the weid authentication
     * @return true if yes, false otherwise, with error codes
     */
    @Override
    public ResponseData<Boolean> unRevoke(Hashable object, WeIdAuthentication weIdAuthentication) {
        ResponseData<String> hashResp = getHashValue(object);
        if (StringUtils.isEmpty(hashResp.getResult())) {
            return new ResponseData<>(false, hashResp.getErrorCode(),
                hashResp.getErrorMessage());
        }
        if (weIdAuthentication == null
            || !WeIdUtils.isPrivateKeyValid(weIdAuthentication.getWeIdPrivateKey())) {
            return new ResponseData<>(false, ErrorCode.WEID_PRIVATEKEY_INVALID);
        }
        Long timestamp = DateUtils.getNoMillisecondTimeStamp();
        com.webank.weid.blockchain.protocol.response.ResponseData<Boolean> resp = evidenceBlockchainService.revoke(
                hashResp.getResult(),
                false,
                timestamp,
                weIdAuthentication.getWeIdPrivateKey().getPrivateKey()
        );
        if (resp.getErrorCode() == ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(resp.getResult(), ErrorCode.SUCCESS);
        } else {
            return new ResponseData<>(false, resp.getErrorCode(), resp.getErrorMessage());
        }
    }

    /**
     * Check whether this evidence is revoked by this WeID.
     *
     * @param evidenceInfo the EvidenceInfo
     * @param weId the signer WeID
     * @return true if revoked, false otherwise
     */
    @Override
    public ResponseData<Boolean> isRevoked(EvidenceInfo evidenceInfo, String weId) {
        if (evidenceInfo == null) {
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        Map<String, EvidenceSignInfo> evidenceSignInfos = evidenceInfo.getSignInfo();
        if (evidenceSignInfos == null || evidenceSignInfos.size() == 0
            || evidenceSignInfos.get(WeIdUtils.convertWeIdToAddress(weId)) == null) {
            return new ResponseData<>(false, ErrorCode.WEID_DOES_NOT_EXIST);
        }
        return new ResponseData<>(evidenceSignInfos.get(WeIdUtils.convertWeIdToAddress(weId)).getRevoked(), ErrorCode.SUCCESS);
    }
}
