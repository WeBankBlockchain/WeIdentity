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
import com.webank.weid.suite.api.transportation.params.TransmissionType;
import com.webank.weid.suite.transmission.amop.AmopTransmission;
import com.webank.weid.suite.transmission.http.HttpTransmission;

/**
 * 传输工厂.
 * 
 * @author yanggang
 *
 */
public class TransmissionFactory {
    
    /**
     * 请求处理器容器.
     * 
     */
    private static final Map<String, Transmission> transmission_map =
        new HashMap<String, Transmission>();
    
    static { 
        transmission_map.put(TransmissionType.AMOP.name(), new AmopTransmission());
        transmission_map.put(TransmissionType.HTTP.name(), new HttpTransmission());
        transmission_map.put(TransmissionType.HTTPS.name(), new HttpTransmission());
    }
    
    /**
     * 根据请求传输类型获取对应的传输处理器.
     * 
     * @param transmissionType 传输类型
     * @return 返回传输处理器
     */
    public static Transmission getChannel(TransmissionType transmissionType) {
        Transmission transmission = transmission_map.get(transmissionType.name());
        if (transmission == null) {
            throw new WeIdBaseException("the channel is null.");
        }
        return transmission;
    }
}
