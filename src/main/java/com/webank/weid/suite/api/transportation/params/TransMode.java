

package com.webank.weid.suite.api.transportation.params;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;

/**
 * Transportation的数据模式, 目前支持二维码的纯数据模式和下载模式
 * 在使用transportation序列化二维码的时候可以通过这个指定二维码包含的是纯数据，还是下载模式.
 * 纯数据模式(DATA_MODE):表示序列化出来的数据为实际数据，此模式下的内容会过大,可能无法编码到二维码中.
 *下载模式(DOWNLOAD_MODE):将协议数据存储在数据库中，通过短编码进行映射. 
 * 
 * @author yanggang
 *
 */
public enum TransMode {
    
    /**
     * 纯数据模式.
     */
    DATA_MODE(0),
    
    /**
     * 下载模式.
     */
    DOWNLOAD_MODE(1),
    ;

    private Integer code;

    TransMode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    /**
     * get TransMode By code.
     *
     * @param code the TransMode code
     * @return TransMode
     */
    public static TransMode getTransModeByCode(Integer code) {
        for (TransMode type : TransMode.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new WeIdBaseException(ErrorCode.TRANSPORTATION_TRANSMODE_TYPE_INVALID);
    }
    
}
