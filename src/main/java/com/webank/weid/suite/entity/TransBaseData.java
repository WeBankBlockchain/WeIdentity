/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

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
