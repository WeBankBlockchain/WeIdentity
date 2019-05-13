package com.webank.weid.service.impl.callback;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.suite.persistence.PersistenceApi;
import com.webank.weid.suite.persistence.driver.MysqlDriver;
import com.webank.weid.protocol.amop.GetEncryptKeyArgs;
import com.webank.weid.protocol.response.GetEncryptKeyResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.callback.AmopCallback;

public class KeyManagerCallback extends AmopCallback {
    
    private static final Logger logger =  LoggerFactory.getLogger(KeyManagerCallback.class);
    
    private PersistenceApi dataDriver = new MysqlDriver();
    
    private static final String TRANSENCRYPTIONDOMAIN = "transEncryption";
    
    @Override
    public GetEncryptKeyResponse onPush(GetEncryptKeyArgs arg) {
        logger.info("begin query key param:{}", arg);
        GetEncryptKeyResponse encryptKeyResponse = new GetEncryptKeyResponse(); 
        ResponseData<String>  keyResponse = dataDriver.get(TRANSENCRYPTIONDOMAIN, arg.getKeyId());
        if (keyResponse.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()
            && StringUtils.isBlank(keyResponse.getResult())) {
            logger.info("the encrypt key is not exists.");
            encryptKeyResponse.setEncryptKey(StringUtils.EMPTY);
            encryptKeyResponse.setErrorCode(ErrorCode.ENCRYPT_KEY_NOT_EXISTS.getCode());
            encryptKeyResponse.setErrorMessage(ErrorCode.ENCRYPT_KEY_NOT_EXISTS.getCodeDesc());
        } else {
            encryptKeyResponse.setEncryptKey(keyResponse.getResult());
            encryptKeyResponse.setErrorCode(keyResponse.getErrorCode().intValue());
            encryptKeyResponse.setErrorMessage(keyResponse.getErrorMessage()); 
        }
        return encryptKeyResponse;
    }
}
