package com.webank.weid.protocol.response;

import lombok.Data;

@Data
public class HandleEntity {
    /**
     * 返回的消息
     */
    private String result;
    
    /**
     * 错误码
     */
    private Integer errorCode;
    
    /**
     * 错误信息
     */
    protected String errorMessage;
}
