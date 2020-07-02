package com.webank.weid.service.impl.engine.fiscov2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tuples.generated.Tuple2;
import org.fisco.bcos.web3j.tuples.generated.Tuple4;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.tx.txdecode.ResultEntity;
import org.fisco.bcos.web3j.tx.txdecode.TransactionDecoder;
import org.fisco.bcos.web3j.tx.txdecode.TransactionDecoderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.CnsType;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.contract.v2.DataBucket;
import com.webank.weid.protocol.base.HashContract;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.engine.BaseEngine;
import com.webank.weid.service.impl.engine.DataBucketServiceEngine;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.WeIdUtils;

public class DataBucketServiceEngineV2 extends BaseEngine implements DataBucketServiceEngine {

    private static final Logger logger = LoggerFactory.getLogger(DataBucketServiceEngineV2.class);

    private DataBucket dataBucket;
    private CnsType cnsType;

    private static TransactionDecoder txDecodeSampleDecoder;

    static {
        txDecodeSampleDecoder = TransactionDecoderFactory.buildTransactionDecoder(
                DataBucket.ABI, DataBucket.BINARY);
    }

    /**
     * 构造函数.
     * 
     * @param cnsType cns类型枚举
     */
    public DataBucketServiceEngineV2(CnsType cnsType) {
        this.cnsType = cnsType;
        loadDataBucket();
    }

    private void loadDataBucket() {
        if (dataBucket == null) {
            dataBucket = super.getContractService(
                getBucketByCns(cnsType).getAddress(), 
                DataBucket.class
            );
        }
    }

    private DataBucket getDataBucket(String privateKey) {
        return super.reloadContract(
            getBucketByCns(cnsType).getAddress(), 
            privateKey, 
            DataBucket.class
        );
    }

    @Override
    public ResponseData<Boolean> put(
        String hash, 
        String key, 
        String value, 
        WeIdPrivateKey privateKey) {
        
        Bytes32 keyByte32 = DataToolUtils.bytesArrayToBytes32(key.getBytes());
        try {
            TransactionReceipt receipt = getDataBucket(privateKey.getPrivateKey()).put(
                hash, keyByte32.getValue(), value).send();
            if (StringUtils
                .equals(receipt.getStatus(), ParamKeyConstant.TRNSACTION_RECEIPT_STATUS_SUCCESS)) {
                logger.info("[put] put [{}:{}] into chain success, hash is {}.", key, value, hash);
                ErrorCode  code = analysisErrorCode(receipt);
                return new ResponseData<Boolean>(code == ErrorCode.SUCCESS, code);
            }
            logger.error("[put] put [{}:{}] into chain fail, hash is {}.", key, value, hash);
            return new ResponseData<Boolean>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("[put] put [{}:{}] into chain has excpetion, hash is {}, exception:",
                key, value, hash, e);
            return new ResponseData<Boolean>(false, ErrorCode.UNKNOW_ERROR);
        }
    }

    private ErrorCode analysisErrorCode(TransactionReceipt receipt) {
        ErrorCode errorCode = ErrorCode.UNKNOW_ERROR;
        try {
            InputAndOutputResult objectResult = txDecodeSampleDecoder.decodeOutputReturnObject(
                receipt.getInput(), receipt.getOutput());
            List<ResultEntity> result = objectResult.getResult();
            Integer code = Integer.parseInt(result.get(0).getData().toString());
            switch (code.intValue()) {
                case 100:
                    errorCode = ErrorCode.SUCCESS;
                    break;
                case 101:
                    errorCode = ErrorCode.CNS_NO_PERMISSION;
                    break;
                case 102:
                    errorCode = ErrorCode.CNS_DOES_NOT_EXIST;
                    break;
                case 103:
                    errorCode = ErrorCode.CNS_IS_USED;
                    break;
                case 104:
                    errorCode = ErrorCode.CNS_IS_NOT_USED;
                    break;
                default:
                    errorCode = ErrorCode.CNS_CODE_UNDEFINED;
                    break;
            }
            return errorCode;
        } catch (Exception e) {
            logger.error("[analysisErrorCode] has some error!", e);
            return errorCode;
        } finally {
            logger.info("[analysisErrorCode] decode transaction result:{}-{}", 
                errorCode.getCode(), errorCode.getCodeDesc()); 
        }
    }

    @Override
    public ResponseData<String> get(String hash, String key) {
        Bytes32 keyByte32 = DataToolUtils.bytesArrayToBytes32(key.getBytes());
        try {
            Tuple2<BigInteger, String> tuple = dataBucket.get(hash, keyByte32.getValue()).send();
            int code = tuple.getValue1().intValue();
            if (code == 102) {
                logger.error("[get] the hash does not exits, hash is {}.", hash);
                return new ResponseData<String>(StringUtils.EMPTY, ErrorCode.CNS_DOES_NOT_EXIST);
            }
            logger.info("[get] get address successfully, hash: {}, key: {}, value: {}", 
                hash, key, tuple.getValue2());
            return new ResponseData<String>(tuple.getValue2(), ErrorCode.SUCCESS);  
        } catch (Exception e) {
            logger.error(
                "[get] get data has exception, hash is {}, key is {}, exception:", hash, key, e);
            return new ResponseData<String>(StringUtils.EMPTY, ErrorCode.UNKNOW_ERROR);
        }
    }

    @Override
    public ResponseData<Boolean> removeExtraItem(
        String hash, 
        String key, 
        WeIdPrivateKey privateKey
    ) {
        Bytes32 keyByte32 = null;
        if (key == null) {
            keyByte32 = DataToolUtils.bytesArrayToBytes32(StringUtils.EMPTY.getBytes());
        } else {
            keyByte32 = DataToolUtils.bytesArrayToBytes32(key.getBytes());
        }
        try {
            logger.info("[remove] remove Extra Item, hash is {}, key is {}.", hash, key);
            TransactionReceipt receipt = getDataBucket(privateKey.getPrivateKey()).removeExtraItem(
                hash, keyByte32.getValue()).send();
            if (StringUtils
                .equals(receipt.getStatus(), ParamKeyConstant.TRNSACTION_RECEIPT_STATUS_SUCCESS)) {
                logger.info("[remove] remove {} from chain success, hash is {}.", key, hash);
                ErrorCode  code = analysisErrorCode(receipt);
                return new ResponseData<Boolean>(code == ErrorCode.SUCCESS, code);
            }
            logger.error("[remove] remove {} from chain fail, hash is {}.", key, hash);
            return new ResponseData<Boolean>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("[remove] remove {} from chain has excpetion, hash is {}, exception:",
                key, hash, e);
            return new ResponseData<Boolean>(false, ErrorCode.UNKNOW_ERROR);
        }
    }
    
    @Override
    public ResponseData<Boolean> removeDataBucketItem(
        String hash, 
        boolean force, 
        WeIdPrivateKey privateKey
    ) {
        try {
            logger.info("[remove] remove Bucket Item, hash is {}, force is {}.", hash, force);
            TransactionReceipt receipt = getDataBucket(privateKey.getPrivateKey())
                .removeDataBucketItem(hash, force).send();
            if (StringUtils
                .equals(receipt.getStatus(), ParamKeyConstant.TRNSACTION_RECEIPT_STATUS_SUCCESS)) {
                logger.info("[remove] remove Bucket Item from chain success, hash is {}.", hash);
                ErrorCode  code = analysisErrorCode(receipt);
                return new ResponseData<Boolean>(code == ErrorCode.SUCCESS, code);
            }
            logger.error("[remove] remove Bucket Item from chain fail, hash is {}.", hash);
            return new ResponseData<Boolean>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error(
                "[remove] remove Bucket Item from chain has excpetion, hash is {}, exception:",
                hash, 
                e
            );
            return new ResponseData<Boolean>(false, ErrorCode.UNKNOW_ERROR);
        }
    }
    
    @Override
    public ResponseData<Boolean> enableHash(String hash, WeIdPrivateKey privateKey) {
        try {
            TransactionReceipt receipt = getDataBucket(privateKey.getPrivateKey()).enableHash(
                hash).send();
            if (StringUtils
                .equals(receipt.getStatus(), ParamKeyConstant.TRNSACTION_RECEIPT_STATUS_SUCCESS)) {
                logger.info("[enableHash] enable hash success, hash is {}.", hash);
                ErrorCode  code = analysisErrorCode(receipt);
                return new ResponseData<Boolean>(code == ErrorCode.SUCCESS, code);
            }
            logger.error("[enableHash] enable hash fail, hash is {}.", hash);
            return new ResponseData<Boolean>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("[enableHash] enable hash has excpetion, hash is {}, exception:", hash, e);
            return new ResponseData<Boolean>(false, ErrorCode.UNKNOW_ERROR);
        }
    }

    @Override
    public ResponseData<Boolean> disableHash(String hash, WeIdPrivateKey privateKey) {
        try {
            TransactionReceipt receipt = getDataBucket(privateKey.getPrivateKey()).disableHash(
                hash).send();
            if (StringUtils
                .equals(receipt.getStatus(), ParamKeyConstant.TRNSACTION_RECEIPT_STATUS_SUCCESS)) {
                logger.info("[disableHash] disable hash success, hash is {}.", hash);
                ErrorCode  code = analysisErrorCode(receipt);
                return new ResponseData<Boolean>(code == ErrorCode.SUCCESS, code);
            }
            logger.error("[disableHash] disable hash fail, hash is {}.", hash);
            return new ResponseData<Boolean>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("[disableHash] disable hash has excpetion, hash is {}, exception:", 
                hash, e);
            return new ResponseData<Boolean>(false, ErrorCode.UNKNOW_ERROR);
        }
    }

    @Override
    public ResponseData<List<HashContract>> getAllHash() {
        int startIndex = 0;
        BigInteger num = BigInteger.valueOf(10);
        List<HashContract>  hashContractList = new ArrayList<HashContract>();
        try {
            while (true) {
                BigInteger offset = BigInteger.valueOf(startIndex);
                Tuple4<List<String>, List<String>, List<BigInteger>, BigInteger> data = 
                    dataBucket.getAllHash(offset, num).send();
                List<String> hashList = data.getValue1();
                List<String> ownerList = data.getValue2();
                List<BigInteger> timesList = data.getValue3();
                BigInteger next = data.getValue4();
                for (int i = 0; i < hashList.size(); i++) {
                    if (WeIdUtils.isEmptyStringAddress(ownerList.get(i))) {
                        break;
                    }
                    HashContract hash = new HashContract();
                    hash.setHash(hashList.get(i));
                    hash.setOwner(ownerList.get(i));
                    hash.setTime(timesList.get(i).longValue());
                    hashContractList.add(hash);
                }
                if (next.intValue() == 0) {
                    break;
                }
                startIndex = next.intValue();
            }
            logger.info("[getAllHash] get the all hash success.");
            return new ResponseData<List<HashContract>>(hashContractList, ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.error("[getAllHash] get the all hash fail.", e);
            return new ResponseData<List<HashContract>>(hashContractList, ErrorCode.UNKNOW_ERROR);
        }
    }
    
    @Override
    public ResponseData<Boolean> updateHashOwner(
        String hash, 
        String newOwner, 
        WeIdPrivateKey privateKey
    ) {
        try {
            TransactionReceipt receipt = getDataBucket(privateKey.getPrivateKey()).updateHashOwner(
                hash, newOwner).send();
            if (StringUtils
                .equals(receipt.getStatus(), ParamKeyConstant.TRNSACTION_RECEIPT_STATUS_SUCCESS)) {
                logger.info("[updateHashOwner] update owner success, hash is {}.", hash);
                ErrorCode  code = analysisErrorCode(receipt);
                return new ResponseData<Boolean>(code == ErrorCode.SUCCESS, code);
            }
            logger.error("[updateHashOwner] update owner fail, hash is {}.", hash);
            return new ResponseData<Boolean>(false, ErrorCode.TRANSACTION_EXECUTE_ERROR);
        } catch (Exception e) {
            logger.error("[updateHashOwner] update owner has excpetion, hash is {}, exception:", 
                hash, e);
            return new ResponseData<Boolean>(false, ErrorCode.UNKNOW_ERROR);
        }
    }
}
