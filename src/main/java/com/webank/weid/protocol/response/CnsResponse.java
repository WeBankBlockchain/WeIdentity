

package com.webank.weid.protocol.response;

import lombok.Data;

/**
 *CNS注册返回数据基类.
 * @author v_wbgyang
 *
 */
@Data
public class CnsResponse {
    
    private int code;
    private String msg;
}
