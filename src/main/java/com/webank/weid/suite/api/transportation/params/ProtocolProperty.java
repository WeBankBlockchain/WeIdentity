

package com.webank.weid.suite.api.transportation.params;

/**
 * 编解码配置.
 * @author v_wbgyang
 *
 */
public class ProtocolProperty {
    
    /**
     * 协议编解码类型, 用于决定数据是否加密.
     */
    private EncodeType encodeType;
    
    /**
     * 数据传输类型, 默认为AMOP通道进行数据传输.
     */
    private TransType transType = TransType.AMOP;
    
    /**
     * 条码类型, 用于控制条码协议中第三段是机构名/短URI/长URI.
     * 目前默认为ORG,表示协议第三段为机构名如: 00org/resourceId
     */
    private UriType uriType = UriType.ORG;
    
    /**
     * 二维码的数据模式: 默认为纯数据模式.
     */
    private TransMode transMode = TransMode.DATA_MODE;

    public EncodeType getEncodeType() {
        return encodeType;
    }
    
    public ProtocolProperty(EncodeType encodeType) {
        this.encodeType = encodeType;
    }

    public ProtocolProperty(EncodeType encodeType, TransMode transMode) {
        this(encodeType);
        this.transMode = transMode;
    }
    
    public TransType getTransType() {
        return transType;
    }

    public UriType getUriType() {
        return uriType;
    }

    public TransMode getTransMode() {
        return transMode;
    }
    
}
