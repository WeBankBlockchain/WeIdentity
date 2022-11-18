package com.webank.weid.util;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.request.TransactionArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.crypto.CryptoServiceFactory;
import com.webank.weid.suite.api.crypto.params.CryptoType;
import com.webank.weid.suite.api.persistence.inf.Persistence;
import com.webank.weid.suite.persistence.mysql.driver.MysqlDriver;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 离线交易批处理工具类.
 *
 * @author tonychen 2020年4月4日
 */

public class OffLineBatchTask {

    private static final Logger logger = LoggerFactory.getLogger(OffLineBatchTask.class);

    private static com.webank.weid.blockchain.service.impl.EvidenceServiceImpl evidenceBlockchainService;

    private static Map<String, String> userKey = new HashMap<>();

    private static String secretKey;

    private static String privateKey;

    /**
     * persistence.
     */
    private static Persistence dataDriver;

    static {
        privateKey = DataToolUtils.generatePrivateKey();
    }

    private static Persistence getDataDriver() {

        if (dataDriver == null) {
            dataDriver = new MysqlDriver();
        }
        return dataDriver;
    }

    private static String getKey() {

        if (!StringUtils.isBlank(secretKey)) {
            return secretKey;
        } else {
            ResponseData<String> dbResp = getDataDriver().get(DataDriverConstant.DOMAIN_ENCRYPTKEY,
                PropertyUtils.getProperty("blockchain.orgid"));
            Integer errorCode = dbResp.getErrorCode();
            if (errorCode != ErrorCode.SUCCESS.getCode()) {
                logger
                    .error("[writeTransaction] save encrypt private key to db failed.errorcode:{}",
                        errorCode);
                return null;
            }
            secretKey = dbResp.getResult();
            return secretKey;
        }
    }


    /**
     * 批量上链接口.
     *
     * @param transactionArgs 上链交易参数
     * @return 每条交易的上链结果
     */
    public static ResponseData<List<Boolean>> sendBatchTransaction(
        List<TransactionArgs> transactionArgs) {

        Map<String, List<String>> hashesByGroup = new HashMap<>();
        Map<String, List<String>> signaturesByGroup = new HashMap<>();
        Map<String, List<String>> logsByGroup = new HashMap<>();
        Map<String, List<Long>> timestampsByGroup = new HashMap<>();
        Map<String, List<String>> signersByGroup = new HashMap<>();
        Map<String, List<String>> customKeysByGroup = new HashMap<>();

        // Preserve order
        Map<String, List<Integer>> orderByGroup = new HashMap<>();

        for (TransactionArgs transaction : transactionArgs) {

            String args = transaction.getArgs();
            String[] argArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(args, ",");

            String method = transaction.getMethod();
            String groupId = com.webank.weid.blockchain.service.fisco.CryptoFisco.fiscoConfig.getGroupId();
            switch (method) {

                case "createEvidence":
                    if (argArray.length > 5) {
                        groupId = argArray[5];
                    }
                    updateCommonFields(hashesByGroup, signaturesByGroup, logsByGroup,
                        timestampsByGroup, groupId, argArray);
                    if (CollectionUtils.size(customKeysByGroup.get(groupId)) == 0) {
                        List<String> list = new ArrayList<>();
                        list.add(StringUtils.EMPTY);
                        customKeysByGroup.put(groupId, list);
                    } else {
                        customKeysByGroup.get(groupId).add(StringUtils.EMPTY);
                    }
                    if (CollectionUtils.size(signersByGroup.get(groupId)) == 0) {
                        List<String> list = new ArrayList<>();
                        list.add(argArray[4]);
                        signersByGroup.put(groupId, list);
                    } else {
                        signersByGroup.get(groupId).add(argArray[4]);
                    }
                    if (CollectionUtils.size(orderByGroup.get(groupId)) == 0) {
                        List<Integer> list = new ArrayList<>();
                        list.add(transactionArgs.indexOf(transaction));
                        orderByGroup.put(groupId, list);
                    } else {
                        orderByGroup.get(groupId).add(transactionArgs.indexOf(transaction));
                    }
                    break;
                case "createEvidenceWithCustomKey":
                    if (argArray.length > 6) {
                        groupId = argArray[6];
                    }
                    updateCommonFields(hashesByGroup, signaturesByGroup, logsByGroup,
                        timestampsByGroup, groupId, argArray);
                    if (CollectionUtils.size(customKeysByGroup.get(groupId)) == 0) {
                        List<String> list = new ArrayList<>();
                        list.add(argArray[4]);
                        customKeysByGroup.put(groupId, list);
                    } else {
                        customKeysByGroup.get(groupId).add(argArray[4]);
                    }
                    if (CollectionUtils.size(signersByGroup.get(groupId)) == 0) {
                        List<String> list = new ArrayList<>();
                        list.add(argArray[5]);
                        signersByGroup.put(groupId, list);
                    } else {
                        signersByGroup.get(groupId).add(argArray[5]);
                    }
                    if (CollectionUtils.size(orderByGroup.get(groupId)) == 0) {
                        List<Integer> list = new ArrayList<>();
                        list.add(transactionArgs.indexOf(transaction));
                        orderByGroup.put(groupId, list);
                    } else {
                        orderByGroup.get(groupId).add(transactionArgs.indexOf(transaction));
                    }
                    break;
                default:
                    break;
            }
        }

        List<Boolean> resp = Arrays.asList(new Boolean[transactionArgs.size()]);

        // Separately go batch creation and merge responses
        for (String groupId : hashesByGroup.keySet()) {
           /* EvidenceServiceEngine evidenceServiceEngine = EngineFactory
                .createEvidenceServiceEngine(groupId);*/
            evidenceBlockchainService = new com.webank.weid.blockchain.service.impl.EvidenceServiceImpl(groupId);
            List<Boolean> subResp = evidenceBlockchainService.batchCreateEvidenceWithCustomKey(
                hashesByGroup.get(groupId),
                signaturesByGroup.get(groupId),
                logsByGroup.get(groupId),
                timestampsByGroup.get(groupId),
                signersByGroup.get(groupId),
                customKeysByGroup.get(groupId),
                privateKey
            ).getResult();

            // merge
            List<Integer> orders = orderByGroup.get(groupId);
            int index = 0;
            for (Boolean boolValue : subResp) {
                resp.set(orders.get(index), boolValue);
                index++;
            }
        }
        return new ResponseData<>(resp, ErrorCode.SUCCESS, null);
    }

    private static void updateCommonFields(
        Map<String, List<String>> hashesByGroup,
        Map<String, List<String>> signaturesByGroup,
        Map<String, List<String>> logsByGroup,
        Map<String, List<Long>> timestampsByGroup,
        String groupId,
        String[] argArray
    ) {
        if (CollectionUtils.size(hashesByGroup.get(groupId)) == 0) {
            List<String> list = new ArrayList<>();
            list.add(argArray[0]);
            hashesByGroup.put(groupId, list);
        } else {
            hashesByGroup.get(groupId).add(argArray[0]);
        }
        if (CollectionUtils.size(signaturesByGroup.get(groupId)) == 0) {
            List<String> list = new ArrayList<>();
            list.add(argArray[1]);
            signaturesByGroup.put(groupId, list);
        } else {
            signaturesByGroup.get(groupId).add(argArray[1]);
        }
        if (CollectionUtils.size(logsByGroup.get(groupId)) == 0) {
            List<String> list = new ArrayList<>();
            list.add(argArray[2]);
            logsByGroup.put(groupId, list);
        } else {
            logsByGroup.get(groupId).add(argArray[2]);
        }
        if (CollectionUtils.size(timestampsByGroup.get(groupId)) == 0) {
            List<Long> list = new ArrayList<>();
            list.add(Long.valueOf(argArray[3]));
            timestampsByGroup.put(groupId, list);
        } else {
            timestampsByGroup.get(groupId).add(Long.valueOf(argArray[3]));
        }
    }

    private static String getPrivateKeyByWeId(String weId) {

        if (StringUtils.isBlank(weId)) {
            return null;
        }
        String privateKey = userKey.get(weId);
        if (privateKey == null) {

            ResponseData<String> resp = getDataDriver().get("", weId);
            Integer dbErrorCode = resp.getErrorCode();
            if (dbErrorCode != ErrorCode.SUCCESS.getCode()) {
                return null;
            }
            String deCryptData = resp.getResult();
            privateKey = CryptoServiceFactory.getCryptoService(CryptoType.AES)
                .decrypt(deCryptData, getKey());
            userKey.put(weId, privateKey);
            return privateKey;
        }
        return null;
    }
}
