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

package com.webank.weid.suite.transmission;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.suite.auth.impl.WeIdAuthImpl;
import com.webank.weid.suite.auth.inf.WeIdAuth;
import com.webank.weid.suite.auth.inf.WeIdAuthCallback;

/**
 * 传输处理服务中心.
 * 
 * @author yanggang
 *
 */
public class TransmissionServiceCenter {
    
    private static final Logger logger = LoggerFactory.getLogger(TransmissionServiceCenter.class);
    
    /**
     * 传输服务.
     */
    private static Map<String, TransmissionService<?>> transmissionServiceContext = new HashMap<>();
   
    private static WeIdAuth weIdAuthService;

    private static WeIdAuth getWeIdAuthService() {
        if (weIdAuthService == null) {
            weIdAuthService = new WeIdAuthImpl();
        }
        return weIdAuthService;
    }
    
    /**
     * 注册传输处理服务.
     * 
     * @param serviceType 服务名称
     * @param service 服务实例
     */
    public static void registerService(String serviceType, TransmissionService<?> service) {
        if (transmissionServiceContext.containsKey(serviceType)) {
            logger.error("[registerService] the service register fail because it is exists.");
            throw new WeIdBaseException("the service type is exist.");
        }
        logger.info("[registerService] the service register successfully.");
        transmissionServiceContext.put(serviceType, service);
    }
    
    /**
     * 获取传输处理服务.
     * 
     * @param serviceType 服务名称
     * @return 服务实例
     */
    public static TransmissionService<?> getService(String serviceType) {
        return transmissionServiceContext.get(serviceType);
    }
    
    /**
     * 注册服务,用于拿到用户的WeIdAuthentication回调处理与机构连接回调处理.
     * 
     * @param weIdAuthCallback 服务名称
     * @return 返回注册结果, true表示成功, false表示失败
     */
    public static boolean registerWeIdAuthCallBack(WeIdAuthCallback weIdAuthCallback) {
        WeIdAuthCallback callBack = getWeIdAuthService().getCallBack();
        if (callBack != null) {
            logger.error("[registerWeIdAuthCallBack] register fail because it is exists.");
            return false;
        }
        logger.info("[registerWeIdAuthCallBack] the callback register successfully.");
        return getWeIdAuthService().registerCallBack(weIdAuthCallback) == 0;
    }
}
