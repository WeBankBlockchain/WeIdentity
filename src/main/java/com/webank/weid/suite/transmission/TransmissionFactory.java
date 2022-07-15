

package com.webank.weid.suite.transmission;

import java.util.HashMap;
import java.util.Map;

import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.suite.api.transportation.params.TransType;
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
        transmission_map.put(TransType.AMOP.name(), new AmopTransmission());
        transmission_map.put(TransType.HTTP.name(), new HttpTransmission());
        transmission_map.put(TransType.HTTPS.name(), new HttpTransmission());
    }
    
    /**
     * 根据请求传输类型获取对应的传输处理器.
     * 
     * @param transmissionType 传输类型
     * @return 返回传输处理器
     */
    public static Transmission getTransmisson(TransType transmissionType) {
        Transmission transmission = transmission_map.get(transmissionType.name());
        if (transmission == null) {
            throw new WeIdBaseException("the channel is null.");
        }
        return transmission;
    }
}
