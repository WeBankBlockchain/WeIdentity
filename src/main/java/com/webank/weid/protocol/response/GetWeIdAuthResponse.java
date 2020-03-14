package com.webank.weid.protocol.response;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tonychen 2020年3月10日
 *
 */
@Getter
@Setter
public class GetWeIdAuthResponse {


    /**
     * encrypt data, including challenge sign, weIdAuthObj.
     */
    private byte[] data;
    
    /**
     * error code.
     */
    private Integer errorCode;

    /**
     * error message.
     */
    protected String errorMessage;
}
