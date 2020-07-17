package com.webank.weid.service.impl.inner;

import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.impl.WeIdServiceImpl;
import com.webank.weid.suite.api.persistence.Persistence;
import com.webank.weid.suite.persistence.sql.driver.MysqlDriver;

public abstract class InnerService {
    
    private Persistence dataDriver;
    
    private WeIdService weidService;
    
    protected WeIdService getWeIdService() {
        if (weidService == null) {
            weidService = new WeIdServiceImpl();
        }
        return weidService;
    }
    
    protected Persistence getDataDriver() {
        if (dataDriver == null) {
            dataDriver = new MysqlDriver();
        }
        return dataDriver;
    }
}
