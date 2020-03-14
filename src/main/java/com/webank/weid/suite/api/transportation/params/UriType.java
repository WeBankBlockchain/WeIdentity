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
 * 协议URI类型.
 * 
 * @author yanggang
 *
 */
public enum UriType {
    /**
     * 通过AMOP请求数据.
     */
    AMOP(0);
    
    private Integer code;

    UriType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    /**
     * get UriType By code.
     *
     * @param code the Uri type code
     * @return UriType 返回类型
     */
    public static UriType getUriByCode(Integer code) {
        for (UriType type : UriType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new WeIdBaseException(ErrorCode.TRANSPORTATION_URI_TYPE_INVALID);
    }
}
