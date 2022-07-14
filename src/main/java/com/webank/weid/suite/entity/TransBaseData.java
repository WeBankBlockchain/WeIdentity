

package com.webank.weid.suite.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransBaseData {
    /**
     * 协议分隔符.
     */
    public static final String PROTOCOL_PARTITION = "|";
    
    /**
     * 协议分隔符.
     */
    public static final String PARTITION_FOR_SPLIT = "\\" + PROTOCOL_PARTITION;
    
    /**
     * 协议版本.
     */
    private int version;
    
    /**
     * JSON协议编解码方式.
     */
    private int encodeType;
    
    /**
     * user agent的Amop ID.
     */
    private String amopId;
    
    /**
     * 协议数据Id.
     */
    private String id;
    
    /**
     * 协议数据体.
     */
    private Object data;
    
    /**
     * 协议扩展字段.
     */
    private String extendData;
}
