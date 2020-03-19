/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
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

package com.webank.weid.suite.api.transportation.params;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;

/**
 * 传输协议类型定义, 决定数据是走那种传输模式.
 * 
 * @author yanggang
 *
 */
public enum TransType {
    /**
     * 通过AMOP请求数据.
     */
    AMOP(0),
    
    /**
     * 通过HTTPS请求数据.
     */
    HTTPS(1),
    
    /**
     * 通过HTTP请求数据.
     */
    HTTP(2)
    ;

    private Integer code;

    TransType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    /**
     * get TransmissionType By code.
     *
     * @param code the Transmission type code
     * @return TransmissionType
     */
    public static TransType getTransmissionByCode(Integer code) {
        for (TransType type : TransType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new WeIdBaseException(ErrorCode.TRANSPORTATION_TRANSMISSION_TYPE_INVALID);
    }
}
