package com.webank.weid.suite.entity;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;

public enum PdfVersion {
    V1(1);

    private int code;

    PdfVersion(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String toString() {
        return String.valueOf(this.code);
    }

    /**
     * get PdfVersion By code.
     *
     * @param code the PdfVersion
     * @return PdfVersion
     */
    public static PdfVersion getVersion(int code) {
        for (PdfVersion version : PdfVersion.values()) {
            if (version.getCode() == code) {
                return version;
            }
        }
        throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PROTOCOL_VERSION_ERROR);
    }
}
