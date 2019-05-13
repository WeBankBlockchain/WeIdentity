/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.suite.transportation.json.protocol;

import lombok.Getter;
import lombok.Setter;

/**
 * JSON协议实体.
 * @author v_wbgyang
 *
 */
@Getter
@Setter
public class JsonBaseData {  
    
    /**
     * JSON协议版本.
     */
    private int version;
    
    /**
     * JSON协议编解码方式.
     */
    private int encodeType;
    
    /**
     * user agent的机构ID.
     */
    private String orgId;
    
    /**
     * 协议数据Id.
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
    
}
