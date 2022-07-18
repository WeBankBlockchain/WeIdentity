

package com.webank.weid.suite.transmission;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.exception.WeIdBaseException;

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
}
