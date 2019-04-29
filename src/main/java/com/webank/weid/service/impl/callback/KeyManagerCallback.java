package com.webank.weid.service.impl.callback;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.persistence.driver.DataDriver;
import com.webank.weid.persistence.driver.MysqlDriver;
import com.webank.weid.rpc.callback.DirectRouteCallback;
import com.webank.weid.protocol.amop.GetEncryptKeyArgs;
import com.webank.weid.protocol.response.GetEncryptKeyResponse;
import com.webank.weid.protocol.response.ResponseData;

public class KeyManagerCallback extends DirectRouteCallback {
    
    private static final Logger logger =  LoggerFactory.getLogger(KeyManagerCallback.class);
    
    private DataDriver dataDriver = new MysqlDriver();
    
    @Override
    public GetEncryptKeyResponse onPush(GetEncryptKeyArgs arg) {
        logger.info("begin query key param:{}", arg);
        GetEncryptKeyResponse encryptKeyResponse = new GetEncryptKeyResponse(); 
        ResponseData<String>  keyResponse = dataDriver.getData(arg.getKeyId());
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
