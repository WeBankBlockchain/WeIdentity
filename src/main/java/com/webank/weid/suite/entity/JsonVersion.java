

package com.webank.weid.suite.entity;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;

/**
 * JSON传输协议枚举.
 * @author v_wbgyang
 *
 */
public enum JsonVersion {

    V1(1);
    
    private int code;

    JsonVersion(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
    
    /**
     * get JsonVersion By code.
     *
     * @param code the JsonVersion
     * @return JsonVersion
     */
    public static JsonVersion getVersion(int code) {
        for (JsonVersion version : JsonVersion.values()) {
            if (version.getCode() == code) {
                return version;
            }
        }
        throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PROTOCOL_VERSION_ERROR);
    }
}
