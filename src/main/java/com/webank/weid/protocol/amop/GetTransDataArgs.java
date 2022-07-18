

package com.webank.weid.protocol.amop;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;

/**
 * the request body for get EncryptKey.
 * 
 * @author tonychen 2019年5月7日.
 *
 */
@Getter
@Setter
@ToString
public class GetTransDataArgs extends AmopBaseMsgArgs {

    /**
     * the resource Id.
     */
    private String resourceId;
    
    /**
     * 数据类型.
     */
    private String className;
    
    /**
     * weId信息.
     */
    private String weId;
    
    /**
     * 签名信息.
     */
    private String signValue;
    
    /**
     * 扩展字符串字段.
     */
    private Map<String, String> extra;
}
