package com.webank.weid.suite.crypto;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.webank.wedpr.ecies.EciesResult;
import com.webank.wedpr.ecies.NativeInterface;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.utils.Numeric;
import org.fisco.bcos.web3j.crypto.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.EncodeSuiteException;
import com.webank.weid.util.DataToolUtils;

public class EciesCryptService implements CryptService {
    
    private static final Logger logger = LoggerFactory.getLogger(EciesCryptService.class);
    
    @Override
    public String encrypt(String content, String key) throws EncodeSuiteException {
        logger.info("begin encrypt by ecies.");
        checkForEncrypt(content, key);
        String data = Numeric.toHexStringNoPrefix(content.getBytes(StandardCharsets.UTF_8));
        BigInteger pub = new BigInteger(Base64.decodeBase64(key));
        String pubValue = Numeric.toHexStringNoPrefixZeroPadded(
            pub, Keys.PUBLIC_KEY_LENGTH_IN_HEX);
        EciesResult result =  NativeInterface.eciesEncrypt(pubValue, data); // 加密
        if (result != null) {
            if (StringUtils.isBlank(result.wedprErrorMessage)) {
                logger.info("encrypt by ecies successfully.");
                byte[] buffer = Base64.encodeBase64(
                    Numeric.hexStringToByteArray(result.encryptMessage));
                return new String(buffer, StandardCharsets.UTF_8);
            }
            logger.error("encrypt by ecies fail, message = {}.", result.wedprErrorMessage);
            throw new EncodeSuiteException(result.wedprErrorMessage);
        }
        throw new EncodeSuiteException(ErrorCode.UNKNOW_ERROR);
    }
    
    private void checkForEncrypt(String content, String key) {
        // 入参非空检查
        String errorMessage = null;
        if (StringUtils.isEmpty(content)) {
            errorMessage = "input content is null.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        }
        // 检查content是否为utf-8
        boolean isUtf8 = Charset.forName(StandardCharsets.UTF_8.toString())
            .newEncoder().canEncode(content);
        if (!isUtf8) {
            errorMessage = "input content is not utf-8.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        }
        // 入参非空检查
        if (StringUtils.isEmpty(key)) {
            errorMessage = "input publicKey is null.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        }
        // 检查publicKey是否为标准base64格式
        if (!DataToolUtils.isValidBase64String(key)) {
            errorMessage = "input publicKey is not a valid Base64 string.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        }
    }
    
    @Override
    public String decrypt(String content, String key) throws EncodeSuiteException {
        logger.info("begin decrypt by ecies.");
        checkForDecrypt(content, key);
        String data = Numeric.toHexStringNoPrefix(Base64.decodeBase64(content));
        BigInteger pri = new BigInteger(Base64.decodeBase64(key));
        String priValue = Numeric.toHexStringNoPrefixZeroPadded(
            pri, Keys.PRIVATE_KEY_LENGTH_IN_HEX);
        EciesResult deResult =  NativeInterface.eciesDecrypt(priValue, data);
        if (deResult != null) {
            if (StringUtils.isBlank(deResult.wedprErrorMessage)) {
                logger.info("decrypt by ecies successfully.");
                byte[] buffer = Numeric.hexStringToByteArray(deResult.decryptMessage);
                return new String(buffer, StandardCharsets.UTF_8);
            }
            logger.error("decrypt by ecies fail, message = {}.", deResult.wedprErrorMessage);
            throw new EncodeSuiteException(deResult.wedprErrorMessage);
        }
        throw new EncodeSuiteException(ErrorCode.UNKNOW_ERROR);
    }
    
    private void checkForDecrypt(String content, String key) {
        // 入参非空检查
        String errorMessage = null;
        if (StringUtils.isEmpty(content)) {
            errorMessage = "input content is null.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        }
        // 检查content是否为标准base64格式
        if (!DataToolUtils.isValidBase64String(content)) {
            errorMessage = "input content is not a valid Base64 string.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        }
        // 入参非空检查
        if (StringUtils.isEmpty(key)) {
            errorMessage = "input privateKey is null.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        }
        // 检查privateKey是否为标准base64格式
        if (!DataToolUtils.isValidBase64String(key)) {
            errorMessage = "input privateKey is not a valid Base64 string.";
            throw new EncodeSuiteException(ErrorCode.ILLEGAL_INPUT, errorMessage);
        } 
    }
}
