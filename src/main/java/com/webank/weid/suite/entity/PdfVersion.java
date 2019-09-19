package com.webank.weid.suite.entity;

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
    public static PdfVersion getPdfVersion(int code) {
        for (PdfVersion version : PdfVersion.values()) {
            if (version.getCode() == code) {
                return version;
            }
        }
        return null;
    }
}
