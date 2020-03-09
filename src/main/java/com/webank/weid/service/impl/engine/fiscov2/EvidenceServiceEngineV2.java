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

package com.webank.weid.service.impl.engine.fiscov2;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.DefaultBlockParameterNumber;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosTransactionReceipt;
import org.fisco.bcos.web3j.protocol.core.methods.response.Log;
import org.fisco.bcos.web3j.protocol.core.methods.response.Transaction;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ResolveEventLogStatus;
import com.webank.weid.contract.v2.EvidenceContract;
import com.webank.weid.contract.v2.EvidenceContract.EvidenceAttributeChangedEventResponse;
import com.webank.weid.protocol.base.EvidenceInfo;
import com.webank.weid.protocol.base.EvidenceSignInfo;
import com.webank.weid.protocol.response.ResolveEventLogResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.TransactionInfo;
import com.webank.weid.service.impl.engine.BaseEngine;
import com.webank.weid.service.impl.engine.EvidenceServiceEngine;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * EvidenceServiceEngine calls evidence contract which runs on FISCO BCOS 2.0.
 *
 * @author yanggang, chaoxinhu
 */
public class EvidenceServiceEngineV2 extends BaseEngine implements EvidenceServiceEngine {

    private static final Logger logger = LoggerFactory.getLogger(EvidenceServiceEngineV2.class);

    private static EvidenceContract evidenceContract;

    /**
     * 构造函数.
     */
    public EvidenceServiceEngineV2() {
        if (evidenceContract == null) {
            reload();
        }
    }

    /**
     * 重新加载静态合约对象.
     */
    public void reload() {
        evidenceContract = getContractService(
            fiscoConfig.getEvidenceAddress(), 
            EvidenceContract.class);
    }
    
    @Override
    public ResponseData<String> createEvidence(
        String hashValue,
        String signature,
        String extra,
        Long timestamp,
        String privateKey
    ) {
        try {
            EvidenceContract evidenceContractWriter =
                reloadContract(
                    fiscoConfig.getEvidenceAddress(),
                    privateKey,
                    EvidenceContract.class
                );
            TransactionReceipt receipt =
                evidenceContractWriter.createEvidence(
                    hashValue,
                    signature,
                    extra,
                    new BigInteger(String.valueOf(timestamp), 10)
                ).send();

            TransactionInfo info = new TransactionInfo(receipt);
            List<EvidenceAttributeChangedEventResponse> eventList =
                evidenceContractWriter.getEvidenceAttributeChangedEvents(receipt);
            if (eventList == null || eventList.isEmpty()) {
                return new ResponseData<>(StringUtils.EMPTY,
                    ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR, info);
            } else {
                String address = WeIdUtils
                    .convertWeIdToAddress(DataToolUtils.convertPrivateKeyToDefaultWeId(privateKey));
                for (EvidenceAttributeChangedEventResponse event : eventList) {
                    if (isSignEvent(event) && event.value.equalsIgnoreCase(signature)
                        && event.signer.equalsIgnoreCase(address)) {
                        return new ResponseData<>(hashValue, ErrorCode.SUCCESS, info);
                    }
                }
            }
            return new ResponseData<>(StringUtils.EMPTY,
                ErrorCode.CREDENTIAL_EVIDENCE_CONTRACT_FAILURE_ILLEAGAL_INPUT);
        } catch (Exception e) {
            logger.error("create evidence failed due to system error. ", e);
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }

    private static boolean isSignEvent(EvidenceAttributeChangedEventResponse event) {
        return event.key.equalsIgnoreCase("info");
    }

    private static boolean isExtraEvent(EvidenceAttributeChangedEventResponse event) {
        return event.key.equalsIgnoreCase("extra");
    }

    /**
     * Get an evidence full info.
     *
     * @param hash evidence hash
     * @return evidence info
     */
    @Override
    public ResponseData<EvidenceInfo> getInfo(String hash) {
        EvidenceInfo evidenceInfo = new EvidenceInfo();
        evidenceInfo.setCredentialHash(hash);
        int latestBlockNumber = 0;
        try {
            latestBlockNumber = evidenceContract.getLatestRelatedBlock(hash).send().intValue();
            if (latestBlockNumber == 0) {
                return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_NOT_EXIST);
            }
            resolveTransaction(hash, latestBlockNumber, evidenceInfo);
            return new ResponseData<>(evidenceInfo, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("get evidence failed.", e);
            return new ResponseData<>(null, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }

    private static void resolveTransaction(
        String hash,
        int startBlockNumber,
        EvidenceInfo evidenceInfo) {

        int previousBlock = startBlockNumber;
        while (previousBlock != 0) {
            int currentBlockNumber = previousBlock;
            BcosBlock latestBlock = null;
            try {
                latestBlock = ((Web3j) getWeb3j()).getBlockByNumber(
                    new DefaultBlockParameterNumber(currentBlockNumber), true).send();
            } catch (IOException e) {
                logger.error(
                    "Get block by number:{} failed. Exception message:{}", currentBlockNumber, e);
            }
            if (latestBlock == null) {
                logger.info("Get block by number:{}. latestBlock is null", currentBlockNumber);
                return;
            }
            List<Transaction> transList = latestBlock
                .getBlock()
                .getTransactions()
                .stream()
                .map(transactionResult -> (Transaction) transactionResult.get())
                .collect(Collectors.toList());
            previousBlock = 0;
            try {
                for (Transaction transaction : transList) {
                    String transHash = transaction.getHash();

                    BcosTransactionReceipt rec1 = ((Web3j) getWeb3j())
                        .getTransactionReceipt(transHash)
                        .send();
                    TransactionReceipt receipt = rec1.getTransactionReceipt().get();
                    List<Log> logs = rec1.getResult().getLogs();
                    for (Log log : logs) {
                        ResolveEventLogResult returnValue =
                            resolveEventLog(hash, log, receipt, evidenceInfo);
                        if (returnValue.getResultStatus().equals(
                            ResolveEventLogStatus.STATUS_SUCCESS)) {
                            if (returnValue.getPreviousBlock() == currentBlockNumber) {
                                continue;
                            }
                            previousBlock = returnValue.getPreviousBlock();
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Get TransactionReceipt by key :{} failed.", hash, e);
            }
        }
    }

    private static ResolveEventLogResult resolveEventLog(
        String hash,
        Log log,
        TransactionReceipt receipt,
        EvidenceInfo evidenceInfo) {
        String topic = log.getTopics().get(0);
        if (!StringUtils.isBlank(topic)) {
            return resolveAttributeEvent(hash, receipt, evidenceInfo);
        }
        ResolveEventLogResult response = new ResolveEventLogResult();
        response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_EVENT_NULL);
        return response;
    }

    private static ResolveEventLogResult resolveAttributeEvent(
        String hash,
        TransactionReceipt receipt,
        EvidenceInfo evidenceInfo) {
        List<EvidenceAttributeChangedEventResponse> eventList =
            evidenceContract.getEvidenceAttributeChangedEvents(receipt);
        ResolveEventLogResult response = new ResolveEventLogResult();

        if (CollectionUtils.isEmpty(eventList)) {
            response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_EVENTLOG_NULL);
            return response;
        }

        int previousBlock = 0;
        // Actual construction code
        for (EvidenceAttributeChangedEventResponse event : eventList) {
            if (event.signer == null || event.key == null || event.previousBlock == null) {
                response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_RES_NULL);
                return response;
            }
            if (!hash.equalsIgnoreCase(event.hash)) {
                response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_KEY_NOT_MATCH);
                return response;
            }
            String signerWeId = WeIdUtils.convertAddressToWeId(event.signer);
            if (isSignEvent(event)) {
                // higher block sig will be overwritten anyway - any new one will be accepted
                EvidenceSignInfo signInfo = new EvidenceSignInfo();
                signInfo.setSignature(event.value);
                signInfo.setTimestamp(String.valueOf(event.updated.longValue()));
                if (evidenceInfo.getSignInfo().containsKey(signerWeId)) {
                    signInfo.setExtraValue(
                        evidenceInfo.getSignInfo().get(signerWeId).getExtraValue());
                }
                evidenceInfo.getSignInfo().put(signerWeId, signInfo);
            }
            if (isExtraEvent(event)) {
                // higher block blob will overwrite existing one - any new one will be abandoned
                EvidenceSignInfo signInfo = new EvidenceSignInfo();
                if (evidenceInfo.getSignInfo().containsKey(signerWeId)) {
                    signInfo.setSignature(
                        evidenceInfo.getSignInfo().get(signerWeId).getSignature());
                    signInfo.setTimestamp(
                        evidenceInfo.getSignInfo().get(signerWeId).getTimestamp());
                } else {
                    signInfo.setSignature(StringUtils.EMPTY);
                    signInfo.setTimestamp(StringUtils.EMPTY);
                }
                Map<String, String> newBlob;
                try {
                    newBlob = DataToolUtils.deserialize(URLDecoder.decode(event.value,
                        StandardCharsets.UTF_8.name()), HashMap.class);
                } catch (Exception e) {
                    logger.error("Failed to decode and deserialize extra value: {}", event.value);
                    response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_KEY_NOT_MATCH);
                    return response;
                }
                Map<String, String> oldBlob = evidenceInfo.getSignInfo().get(signerWeId)
                    .getExtraValue();
                if (oldBlob == null || oldBlob.isEmpty()) {
                    oldBlob = new HashMap<>();
                }
                // Iterate each key in the new blob. If it is already in the old one, abandon.
                for (Map.Entry<String, String> entry : newBlob.entrySet()) {
                    if (!oldBlob.containsKey(entry.getKey())) {
                        oldBlob.put(entry.getKey(), entry.getValue());
                    }
                }
                signInfo.setExtraValue(oldBlob);
                evidenceInfo.getSignInfo().put(signerWeId, signInfo);
            }
            previousBlock = event.previousBlock.intValue();
        }
        response.setPreviousBlock(previousBlock);
        response.setResolveEventLogStatus(ResolveEventLogStatus.STATUS_SUCCESS);
        return response;
    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.EvidenceServiceEngine#createEvidence(
     * java.lang.String, java.lang.String, java.lang.String, java.lang.Long, java.lang.String,
     * java.lang.String)
     */
    @Override
    public ResponseData<String> createEvidenceWithCustomKey(
        String hashValue,
        String signature,
        String extra,
        Long timestamp,
        String extraKey,
        String privateKey) {

        try {
            EvidenceContract evidenceContractWriter =
                reloadContract(
                    fiscoConfig.getEvidenceAddress(),
                    privateKey,
                    EvidenceContract.class
                );
            TransactionReceipt receipt =
                evidenceContractWriter.createEvidenceWithCustomKey(
                    hashValue,
                    signature,
                    extra,
                    new BigInteger(String.valueOf(timestamp), 10),
                    extraKey
                ).send();

            TransactionInfo info = new TransactionInfo(receipt);
            List<EvidenceAttributeChangedEventResponse> eventList =
                evidenceContractWriter.getEvidenceAttributeChangedEvents(receipt);
            if (eventList == null || eventList.isEmpty()) {
                return new ResponseData<>(StringUtils.EMPTY,
                    ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR, info);
            } else {
                String address = WeIdUtils
                    .convertWeIdToAddress(DataToolUtils.convertPrivateKeyToDefaultWeId(privateKey));
                for (EvidenceAttributeChangedEventResponse event : eventList) {
                    if (isSignEvent(event) && event.value.equalsIgnoreCase(signature)
                        && event.signer.equalsIgnoreCase(address)) {
                        return new ResponseData<>(hashValue, ErrorCode.SUCCESS, info);
                    }
                }
            }
            return new ResponseData<>(StringUtils.EMPTY,
                ErrorCode.CREDENTIAL_EVIDENCE_CONTRACT_FAILURE_ILLEAGAL_INPUT);
        } catch (Exception e) {
            logger.error("create evidence failed due to system error. ", e);
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }

    }

    /* (non-Javadoc)
     * @see com.webank.weid.service.impl.engine.EvidenceServiceEngine#getInfoByExtraKey(
     * java.lang.String)
     */
    @Override
    public ResponseData<EvidenceInfo> getInfoByExtraKey(String extraKey) {

        if (StringUtils.isBlank(extraKey)) {
            logger.error("[getInfoByExtraKey] extraKey is empty. ");
            return new ResponseData<EvidenceInfo>(null, ErrorCode.ILLEGAL_INPUT);
        }
        try {
            String hash = evidenceContract.getHashByExtraKey(extraKey).send();
            if (StringUtils.isBlank(hash)) {
                logger.error("[getInfoByExtraKey] extraKey dose not match any hash. ");
                return new ResponseData<EvidenceInfo>(null,
                    ErrorCode.CREDENTIAL_EVIDENCE_NOT_EXIST);
            }
            return this.getInfo(hash);
        } catch (Exception e) {
            logger.error("[getInfoByExtraKey] get evidence info failed. ", e);
            return new ResponseData<EvidenceInfo>(null, ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR);
        }
    }
}
