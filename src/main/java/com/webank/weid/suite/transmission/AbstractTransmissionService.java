

package com.webank.weid.suite.transmission;

/**
 * 请求处理服务, 用于机构间的数据传输服务处理.
 * 
 * @author yanggang
 *
 */
public abstract class AbstractTransmissionService<T> implements TransmissionService<T> {
    
    /**
     * 自动注册服务构造器.
     * 
     * @param serviceType 服务名称
     */
    public AbstractTransmissionService(String serviceType) {
        TransmissionServiceCenter.registerService(serviceType, this);
    }
}
