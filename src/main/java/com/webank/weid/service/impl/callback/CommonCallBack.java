/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
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

package com.webank.weid.service.impl.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ServiceType;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.callback.AmopCallback;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.service.impl.inner.DownBarCodeDataService;
import com.webank.weid.suite.transmission.TransmissionServcieCenter;
import com.webank.weid.suite.transmission.TransmissionService;
import com.webank.weid.util.DataToolUtils;

/**
 * AMOP公共回调处理.
 * @author yanggang
 *
 */
public class CommonCallBack extends AmopCallback {
    
    private static final Logger logger =  LoggerFactory.getLogger(CommonCallBack.class);
    
    static {
        // 初始化默认的服务处理
        initDefaultService();
    }
    
    private static void initDefaultService() {
        register(ServiceType.SYS_GET_BARCODE_DATA.name(),new DownBarCodeDataService());
    }
    
    private static void register(String serviceType, TransmissionService<?> service) {
        TransmissionServcieCenter.registerService(serviceType, service); 
    }
    
    @Override
    public AmopResponse onPush(AmopCommonArgs arg) {
        logger.info("[CommonCallBack] request param: {}.", arg);
        TransmissionService<?> service = TransmissionServcieCenter.getService(arg.getServiceType());
        if (service == null) {
            logger.error("[CommonCallBack] no found the service for {}.", arg.getServiceType()); 
            return super.onPush(arg);
        }
        ResponseData<?> response = service.service(arg.getMessage());
        return buildAmopResponse(response, arg);
    }
    
    private <T> AmopResponse buildAmopResponse(ResponseData<T> response, AmopCommonArgs arg) {
        AmopResponse result = new AmopResponse();
        if (response.getResult() instanceof String) {
            result.setResult((String)response.getResult());
        } else {
            result.setResult(DataToolUtils.serialize(response.getResult()));
        }
        result.setErrorCode(response.getErrorCode());
        result.setMessageId(arg.getMessageId());
        result.setErrorMessage(response.getErrorMessage());
        result.setServiceType(arg.getServiceType());
        return result;
    }
}
