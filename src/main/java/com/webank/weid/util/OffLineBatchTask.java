package com.webank.weid.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.request.TransactionArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.AbstractService;
import com.webank.weid.suite.api.crypto.CryptoServiceFactory;
import com.webank.weid.suite.api.crypto.params.CryptoType;
import com.webank.weid.suite.api.persistence.Persistence;
import com.webank.weid.suite.persistence.sql.driver.MysqlDriver;

/**
 * 离线交易批处理工具类.
 *
 * @author tonychen 2020年4月4日
 */

public class OffLineBatchTask extends AbstractService {

    private static final Logger logger = LoggerFactory.getLogger(OffLineBatchTask.class);

    private static Map<String, String> userKey = new HashMap<>();

    private static String secretKey;

    private static String privateKey;

    /**
     * persistence.
     */
    private static Persistence dataDriver;

    static {

        try {
            ECKeyPair keyPair = Keys.createEcKeyPair();
            privateKey = String.valueOf(keyPair.getPrivateKey());
        } catch (Exception e) {
            logger.error("Create weId failed.", e);
        }
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

        List<String> hashValues = new ArrayList<>();
        List<String> signatures = new ArrayList<>();
        List<String> logs = new ArrayList<>();
        List<Long> timestamp = new ArrayList<>();
        List<String> signers = new ArrayList<>();
        List<String> customKeys = new ArrayList<>();

        for (TransactionArgs transaction : transactionArgs) {

            String args = transaction.getArgs();
            String[] argArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(args, ",");
            hashValues.add(argArray[0]);
            signatures.add(argArray[1]);
            logs.add(argArray[2]);
            timestamp.add(Long.valueOf(argArray[3]));

            String method = transaction.getMethod();
            switch (method) {

                case "createEvidence":
                    customKeys.add(StringUtils.EMPTY);
                    signers.add(argArray[4]);
                    break;
                case "createEvidenceWithCustomKey":
                    //批量接口
                    customKeys.add(argArray[4]);
                    signers.add(argArray[5]);
                    break;
                default:
                    break;
            }
        }
        return null;
        //return evidenceServiceEngine
        //    .batchCreateEvidenceWithCustomKey(
        //        hashValues,
        //        signatures,
        //        logs,
        //        timestamp,
        //        signers,
        //        customKeys,
        //        privateKey);
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
