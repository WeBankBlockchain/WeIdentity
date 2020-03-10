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

import com.webank.weid.exception.WeIdBaseException;

/**
 * 传输处理服务中心.
 * 
 * @author yanggang
 *
 */
public class TransmissionServcieCenter {
    
    /**
     * 传输服务.
     */
    private static Map<String, TransmissionService<?>> transmissionServiceContext = new HashMap<>();
    
    /**
     * 注册传输处理服务.
     * 
     * @param serviceType 服务名称
     * @param service 服务实例
     */
    public static void registerService(String serviceType, TransmissionService<?> service) {
        if (transmissionServiceContext.containsKey(serviceType)) {
            throw new WeIdBaseException("the servvice type is exist.");
        }
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
}
