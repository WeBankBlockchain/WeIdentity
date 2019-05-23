package com.webank.weid.protocol.response;

import lombok.Getter;
import lombok.Setter;

/**
 * the getEncryptKey response.
 * @author tonychen 2019年5月7日
 *
 */
@Getter
@Setter
public class GetEncryptKeyResponse {

    /**
     * 返回的消息.
     */
    private String encryptKey;

    /**
     * 错误码.
     */
    private Integer errorCode;

    /**
     * 错误信息.
     */
    protected String errorMessage;
}
