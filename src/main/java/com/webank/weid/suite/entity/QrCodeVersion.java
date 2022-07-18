

package com.webank.weid.suite.entity;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.suite.transportation.qr.protocol.QrCodeVersion1;
import com.webank.weid.suite.transportation.qr.protocol.QrCodeVersion2;

public enum QrCodeVersion {
    
    V1(1, QrCodeVersion1.class),
    
    V2(2, QrCodeVersion2.class);
    
    private int code;
    
    private Class<?> clz;

    QrCodeVersion(int code, Class<?> clz) {
        this.code = code;
        this.clz = clz;
    }

    public int getCode() {
        return code;
    }
    
    public Class<?> getClz() {
        return clz;
    }
    
    /**
     * get MetaVersion by code.
     * @param code code value
     * @return QrCodeVersion
     */
    public static QrCodeVersion getVersion(int code) {
        for (QrCodeVersion version : QrCodeVersion.values()) {
            if (version.getCode() == code) {
                return version;
            }
        }
        throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PROTOCOL_VERSION_ERROR);
    }
}
