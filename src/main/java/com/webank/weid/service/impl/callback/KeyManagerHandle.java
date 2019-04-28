package com.webank.weid.service.impl.callback;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.connectivity.driver.DataDriver;
import com.webank.weid.connectivity.driver.MysqlDriver;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.response.HandleEntity;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.DataToolUtils;

public class KeyManagerHandle {
    
    private static final Logger logger =  LoggerFactory.getLogger(KeyManagerHandle.class);
    
    private DataDriver dataDriver = new MysqlDriver();
    
    public String queryKey(String id) {
        logger.info("begin query key by id:{}", id);
        HandleEntity entity = new HandleEntity(); 
        ResponseData<String>  response = dataDriver.getData(id);
        if (response.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()
            && StringUtils.isBlank(response.getResult())) {
            logger.info("the encrypt key is not exists.");
            entity.setErrorCode(ErrorCode.ENCRYPT_KEY_NOT_EXISTS.getCode());
            entity.setErrorMessage(ErrorCode.ENCRYPT_KEY_NOT_EXISTS.getCodeDesc());
        } else {
            entity.setResult(response.getResult());
            entity.setErrorCode(response.getErrorCode().intValue());
            entity.setErrorMessage(response.getErrorMessage()); 
        }
        return DataToolUtils.serialize(entity);
    }
}
