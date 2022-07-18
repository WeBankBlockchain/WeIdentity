

package com.webank.weid.suite.transmission;

import com.webank.weid.protocol.response.ResponseData;

/**
 * 传输处理服务接口.
 * 
 * @author yanggang
 *
 * @param <T> 服务响应数据类型
 */
public interface TransmissionService<T> {

    /**
     * 服务请求接口.
     * 
     * @param message 请求参数
     * @return 返回响应结果
     */
    ResponseData<T> service(String message);
}
