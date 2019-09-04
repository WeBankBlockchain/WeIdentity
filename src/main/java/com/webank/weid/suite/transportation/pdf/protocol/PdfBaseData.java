package com.webank.weid.suite.transportation.pdf.protocol;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PdfBaseData {

    /**
     * 协议版本.
     */
    private int version;

    /**
     * 协议编解码方式-加密/非加密.
     */
    private int encodeType;

    /**
     * 协议数据签发机构.
     */
    private String orgId;

    /**
     * 协议负载数据编码.
     */
    private String id;

    /**
     * 协议数据体.
     */
    private Object data;

    /**
     * 协议通讯类型.
     */
    private String type = "AMOP";

    /**
     * 存证地址.
     */
    private String address;
}
