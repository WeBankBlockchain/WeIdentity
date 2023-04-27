package com.webank.weid.service.local;

import com.webank.weid.blockchain.constant.ChainType;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.blockchain.protocol.base.CptBaseInfo;
import com.webank.weid.blockchain.protocol.base.EvidenceInfo;
import com.webank.weid.blockchain.protocol.base.EvidenceSignInfo;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.blockchain.rpc.EvidenceService;
import com.webank.weid.blockchain.util.DataToolUtils;
import com.webank.weid.util.WeIdUtils;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.service.local.role.RoleController;
import com.webank.weid.suite.persistence.EvidenceValue;
import com.webank.weid.suite.persistence.Persistence;
import com.webank.weid.suite.persistence.PersistenceFactory;
import com.webank.weid.suite.persistence.PersistenceType;
import com.webank.weid.util.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("evidenceServiceLocal")
public class EvidenceServiceLocal implements EvidenceService {
    private static final Logger logger = LoggerFactory.getLogger(EvidenceServiceLocal.class);

    private static Persistence dataDriver;
    private static PersistenceType persistenceType;
    public String groupId = "1";

    private static Persistence getDataDriver() {
        String type = PropertyUtils.getProperty("persistence_type");
        if (type.equals("mysql")) {
            persistenceType = PersistenceType.Mysql;
        } else if (type.equals("redis")) {
            persistenceType = PersistenceType.Redis;
        }
        if (dataDriver == null) {
            dataDriver = PersistenceFactory.build(persistenceType);
        }
        return dataDriver;
    }

    @Override
    public String getGroupId(){
        return this.groupId;
    }

    @Override
    public ResponseData<String> createEvidence(
            String hashValue,
            String signature,
            String log,
            Long timestamp,
            String privateKey
    ) {
        if (StringUtils.isEmpty(hashValue) || StringUtils.isEmpty(log) || StringUtils.isEmpty(signature) || StringUtils.isEmpty(privateKey)) {
            logger.error("[createEvidence] input argument is illegal");
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.ILLEGAL_INPUT);
        }
        if(getDataDriver().getEvidenceByHash(DataDriverConstant.LOCAL_EVIDENCE, hashValue).getResult() != null) {
            logger.error("[createEvidence] evidence with the hash value already existed");
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.CREDENTIAL_EVIDENCE_ALREADY_EXISTS);
        }
        ResponseData<Integer> resp =
                getDataDriver().addEvidenceByHash(
                        DataDriverConstant.LOCAL_EVIDENCE,
                        hashValue,
                        WeIdUtils.getWeIdFromPrivateKey(privateKey),
                        signature,
                        log,
                        String.valueOf(timestamp),
                        String.valueOf(0),
                        StringUtils.EMPTY,
                        groupId);
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[createEvidence] save evidence to db failed.");
            throw new DatabaseException("database error!");
        }
        return new ResponseData<>(hashValue, ErrorCode.SUCCESS);
    }

    @Override
    public ResponseData<Boolean> createEvidenceWithCustomKey(
            String hashValue,
            String signature,
            String log,
            Long timestamp,
            String extraKey,
            String privateKey
    ) {
        if (StringUtils.isEmpty(hashValue) || StringUtils.isEmpty(hashValue) || StringUtils.isEmpty(signature) || StringUtils.isEmpty(privateKey)) {
            logger.error("[createEvidenceWithCustomKey] input argument is illegal");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        if(getDataDriver().getEvidenceByHash(DataDriverConstant.LOCAL_EVIDENCE, hashValue).getResult() != null) {
            logger.error("[createEvidenceWithCustomKey] evidence with the hash value already existed");
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_EVIDENCE_ALREADY_EXISTS);
        }

        ResponseData<Integer> resp =
                getDataDriver().addEvidenceByHash(
                        DataDriverConstant.LOCAL_EVIDENCE,
                        hashValue,
                        WeIdUtils.getWeIdFromPrivateKey(privateKey),
                        signature,
                        log,
                        String.valueOf(timestamp),
                        String.valueOf(0),
                        extraKey,
                        groupId);
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[createEvidenceWithCustomKey] save evidence to db failed.");
            throw new DatabaseException("database error!");
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }

    /**
     * Get the evidence from blockchain.
     *
     * @param hashValue the evidence hash on chain
     * @return The EvidenceInfo
     */
    @Override
    public ResponseData<EvidenceInfo> getInfo(String hashValue) {
        if (StringUtils.isEmpty(hashValue)) {
            logger.error("[getInfo] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        EvidenceInfo evidenceInfo = new EvidenceInfo();
        evidenceInfo.setCredentialHash(hashValue);
        EvidenceValue evidenceValue = getDataDriver().getEvidenceByHash(DataDriverConstant.LOCAL_EVIDENCE, hashValue).getResult();
        if(evidenceValue == null){
            logger.error("[getInfo] evidence not exist on chain");
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_NOT_EXIST);
        }
        Map<String, EvidenceSignInfo> signInfoMap = new HashMap<>();
        String[] signerList = evidenceValue.getSigners().split(";");
        String[] signatureList = evidenceValue.getSignatures().split(";");
        String[] timestampList = evidenceValue.getUpdated().split(";");
        String[] logList = evidenceValue.getLogs().split(";");
        String[] revokedList = evidenceValue.getRevoked().split(";");
        for(int i = 0; i < signerList.length; i++){
            if(signInfoMap.containsKey(WeIdUtils.convertWeIdToAddress(signerList[i]))){
                EvidenceSignInfo evidenceSignInfo = signInfoMap.get(WeIdUtils.convertWeIdToAddress(signerList[i]));
                if(!signatureList[i].equals("empty signature")) evidenceSignInfo.setSignature(signatureList[i]);
                evidenceSignInfo.setTimestamp(timestampList[i]);
                evidenceSignInfo.setRevoked(revokedList[i].equals("1"));
                List<String> logs = evidenceSignInfo.getLogs();
                logs.add(logList[i]);
                evidenceSignInfo.setLogs(logs);
                signInfoMap.put(WeIdUtils.convertWeIdToAddress(signerList[i]), evidenceSignInfo);
            } else {
                EvidenceSignInfo evidenceSignInfo = new EvidenceSignInfo();
                if(!signatureList[i].equals("empty signature")) evidenceSignInfo.setSignature(signatureList[i]);
                evidenceSignInfo.setTimestamp(timestampList[i]);
                evidenceSignInfo.setRevoked(revokedList[i].equals("1"));
                List<String> logs = evidenceSignInfo.getLogs();
                logs.add(logList[i]);
                evidenceSignInfo.setLogs(logs);
                signInfoMap.put(WeIdUtils.convertWeIdToAddress(signerList[i]), evidenceSignInfo);
            }
        }
        evidenceInfo.setSignInfo(signInfoMap);
        return new ResponseData<>(evidenceInfo, ErrorCode.SUCCESS);
    }

    @Override
    public ResponseData<List<Boolean>> batchCreateEvidence(
            List<String> hashValues,
            List<String> signatures,
            List<String> logs,
            List<Long> timestamps,
            List<String> signers,
            String privateKey
    ) {
        if (hashValues.size() != signatures.size() || hashValues.size() != logs.size() || hashValues.size() != timestamps.size() || hashValues.size() != signers.size()) {
            logger.error("[batchCreateEvidence] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        List<Boolean> resultList = new ArrayList<>();
        for(int i = 0; i < hashValues.size(); i++){
            if (StringUtils.isEmpty(hashValues.get(i)) || StringUtils.isEmpty(logs.get(i)) || StringUtils.isEmpty(signatures.get(i)) || StringUtils.isEmpty(signers.get(i))) {
                logger.error("[batchCreateEvidence] input argument is illegal");
                resultList.add(false);
                continue;
            }
            if(getDataDriver().getEvidenceByHash(DataDriverConstant.LOCAL_EVIDENCE, hashValues.get(i)).getResult() != null) {
                logger.error("[batchCreateEvidence] evidence with the hash value already existed");
                resultList.add(false);
                continue;
            }
            ResponseData<Integer> resp =
                    getDataDriver().addEvidenceByHash(
                            DataDriverConstant.LOCAL_EVIDENCE,
                            hashValues.get(i),
                            signers.get(i),
                            signatures.get(i),
                            logs.get(i),
                            String.valueOf(timestamps.get(i)),
                            String.valueOf(0),
                            StringUtils.EMPTY,
                            groupId);
            if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[batchCreateEvidence] batch save evidence to db failed.");
                resultList.add(false);
                continue;
            }
            resultList.add(true);
        }
        return new ResponseData<>(resultList, ErrorCode.SUCCESS);
    }

    @Override
    public ResponseData<List<Boolean>> batchCreateEvidenceWithCustomKey(
            List<String> hashValues,
            List<String> signatures,
            List<String> logs,
            List<Long> timestamps,
            List<String> signers,
            List<String> extraKeys,
            String privateKey
    ) {
        if (hashValues.size() != signatures.size() || hashValues.size() != logs.size() || hashValues.size() != timestamps.size() || hashValues.size() != signers.size() || hashValues.size() != extraKeys.size()) {
            logger.error("[batchCreateEvidenceWithCustomKey] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        List<Boolean> resultList = new ArrayList<>();
        for(int i = 0; i < hashValues.size(); i++){
            if (StringUtils.isEmpty(hashValues.get(i)) || StringUtils.isEmpty(logs.get(i)) || StringUtils.isEmpty(signatures.get(i)) || StringUtils.isEmpty(signers.get(i))) {
                logger.error("[batchCreateEvidenceWithCustomKey] input argument is illegal");
                resultList.add(false);
                continue;
            }
            if(getDataDriver().getEvidenceByHash(DataDriverConstant.LOCAL_EVIDENCE, hashValues.get(i)).getResult() != null) {
                logger.error("[batchCreateEvidenceWithCustomKey] evidence with the hash value already existed");
                resultList.add(false);
                continue;
            }
            ResponseData<Integer> resp =
                    getDataDriver().addEvidenceByHash(
                            DataDriverConstant.LOCAL_EVIDENCE,
                            hashValues.get(i),
                            signers.get(i),
                            signatures.get(i),
                            logs.get(i),
                            String.valueOf(timestamps.get(i)),
                            String.valueOf(0),
                            extraKeys.get(i),
                            groupId);
            if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error("[batchCreateEvidenceWithCustomKey] batch save evidence to db failed.");
                resultList.add(false);
                continue;
            }
            resultList.add(true);
        }
        return new ResponseData<>(resultList, ErrorCode.SUCCESS);
    }

    @Override
    public ResponseData<Boolean> addLog(
            String hashValue,
            String signature,
            String log,
            Long timestamp,
            String privateKey
    ) {
        if (StringUtils.isEmpty(hashValue) || StringUtils.isEmpty(log) ||  StringUtils.isEmpty(privateKey)) {
            logger.error("[addLog] input argument is illegal");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        EvidenceValue evidenceValue = getDataDriver().getEvidenceByHash(DataDriverConstant.LOCAL_EVIDENCE, hashValue).getResult();
        if(evidenceValue == null) {
            logger.error("[addLog] evidence with the hash value not existed");
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_EVIDENCE_NOT_EXIST);
        }
        if(signature.equals(StringUtils.EMPTY)) signature = "empty signature";
        ResponseData<Integer> resp =
                getDataDriver().addSignatureAndLogs(
                        DataDriverConstant.LOCAL_EVIDENCE,
                        hashValue,
                        evidenceValue.getSigners() + ';' + WeIdUtils.getWeIdFromPrivateKey(privateKey),
                        evidenceValue.getSignatures() + ';' + signature,
                        evidenceValue.getLogs() + ';' + log,
                        evidenceValue.getUpdated() + ';' + timestamp,
                        evidenceValue.getRevoked() + ';' + 0,
                        evidenceValue.getExtra_key());
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[addLog] update evidence to db failed.");
            throw new DatabaseException("database error!");
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }

    /**
     * Get the hash info from blockchain using custom key.
     *
     * @param customKey the customKey on chain
     * @return The EvidenceInfo
     */
    @Override
    public ResponseData<String> getHashByCustomKey(String customKey) {
        if (StringUtils.isEmpty(customKey)) {
            logger.error("[getHashByCustomKey] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        EvidenceValue evidenceValue = getDataDriver().getEvidenceByExtraKey(DataDriverConstant.LOCAL_EVIDENCE, customKey).getResult();
        if(evidenceValue == null){
            logger.error("[getHashByCustomKey] evidence not exist on chain");
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_NOT_EXIST);
        }

        return new ResponseData<>(evidenceValue.getHash(), ErrorCode.SUCCESS);
    }

    @Override
    public ResponseData<Boolean> addLogByCustomKey(
            String hashValue,
            String signature,
            String log,
            Long timestamp,
            String customKey,
            String privateKey
    ) {
        if (StringUtils.isEmpty(hashValue) || StringUtils.isEmpty(log) || StringUtils.isEmpty(privateKey) || StringUtils.isEmpty(customKey)) {
            logger.error("[addLogByCustomKey] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        EvidenceValue evidenceValue = getDataDriver().getEvidenceByHash(DataDriverConstant.LOCAL_EVIDENCE, hashValue).getResult();
        if(evidenceValue == null){
            logger.error("[addLogByCustomKey] evidence not exist on chain");
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_NOT_EXIST);
        }
        if(signature.equals(StringUtils.EMPTY)) signature = "empty signature";
        ResponseData<Integer> resp =
                getDataDriver().addSignatureAndLogs(
                        DataDriverConstant.LOCAL_EVIDENCE,
                        hashValue,
                        evidenceValue.getSigners() + ';' + WeIdUtils.getWeIdFromPrivateKey(privateKey),
                        evidenceValue.getSignatures() + ';' + signature,
                        evidenceValue.getLogs() + ';' + log,
                        evidenceValue.getUpdated() + ';' + timestamp,
                        evidenceValue.getRevoked() + ';' + 0,
                        customKey);
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[addLogByCustomKey] update evidence to db failed.");
            throw new DatabaseException("database error!");
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }

    /**
     * Get the evidence from blockchain.
     *
     * @param customKey the evidence hash on chain
     * @return The EvidenceInfo
     */
    @Override
    public ResponseData<EvidenceInfo> getInfoByCustomKey(String customKey) {
        if (StringUtils.isEmpty(customKey)) {
            logger.error("[getInfo] input argument is illegal");
            return new ResponseData<>(null, ErrorCode.ILLEGAL_INPUT);
        }
        EvidenceValue evidenceValue = getDataDriver().getEvidenceByExtraKey(DataDriverConstant.LOCAL_EVIDENCE, customKey).getResult();
        if(evidenceValue == null){
            logger.error("[getInfo] evidence not exist on chain");
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_NOT_EXIST);
        }
        return getInfo(evidenceValue.getHash());
    }

    /**
     * Revoke an evidence - which can be un-revoked.
     *
     * @param hash the hash
     * @param revokeStage the revokeStage
     * @param timestamp the timestamp
     * @param privateKey the weid privateKey
     * @return true if yes, false otherwise, with error codes
     */
    @Override
    public ResponseData<Boolean> revoke(String hash, Boolean revokeStage, Long timestamp, String privateKey) {
        if (StringUtils.isEmpty(hash) || StringUtils.isEmpty(privateKey)) {
            logger.error("[revoke] input argument is illegal");
            return new ResponseData<>(false, ErrorCode.ILLEGAL_INPUT);
        }
        EvidenceValue evidenceValue = getDataDriver().getEvidenceByHash(DataDriverConstant.LOCAL_EVIDENCE, hash).getResult();
        if(evidenceValue == null) {
            logger.error("[revoke] evidence with the hash value not existed");
            return new ResponseData<>(false, ErrorCode.CREDENTIAL_EVIDENCE_NOT_EXIST);
        }
        String[] signerList = evidenceValue.getSigners().split(";");
        String[] revokedList = evidenceValue.getRevoked().split(";");
        String[] timestampList = evidenceValue.getUpdated().split(";");
        String revoked = revokedList[0];
        String timestampStr = timestampList[0];
        for(int i = 0; i < signerList.length; i++){
            if(signerList[i].equals(WeIdUtils.getWeIdFromPrivateKey(privateKey))){
                revokedList[i] = String.valueOf(revokeStage? 1:0);
                timestampList[i] = String.valueOf(timestamp);
            }
        }
        String newRevoked = revokedList[0];
        if(revokedList.length != 1) {
            for(int i = 1; i < revokedList.length; i++){
                newRevoked = newRevoked + ';' + revokedList[i];
            }
        }
        String newTimestamp = timestampList[0];
        if(timestampList.length != 1) {
            for(int i = 1; i < timestampList.length; i++){
                newTimestamp = newTimestamp + ';' + timestampList[i];
            }
        }
        ResponseData<Integer> resp =
                getDataDriver().addSignatureAndLogs(
                        DataDriverConstant.LOCAL_EVIDENCE,
                        hash,
                        evidenceValue.getSigners(),
                        evidenceValue.getSignatures(),
                        evidenceValue.getLogs(),
                        newTimestamp,
                        newRevoked,
                        evidenceValue.getExtra_key());
        if (resp.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("[revoke] update evidence to db failed.");
            throw new DatabaseException("database error!");
        }
        return new ResponseData<>(true, ErrorCode.SUCCESS);
    }

}
