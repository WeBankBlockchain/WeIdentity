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

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.suite.transportation.bar.protocol.BarCodeVersion1;

/**
 * JSON传输协议枚举.
 * @author v_wbgyang
 *
 */
public enum BarCodeVersion {

    V1(1, BarCodeVersion1.class);
    
    private int code;
    
    private Class<?> clz;

    BarCodeVersion(int code, Class<?> clz) {
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
     * get BarCodeVersion By code.
     *
     * @param code the BarCodeVersion
     * @return BarCodeVersion
     */
    public static BarCodeVersion getVersion(int code) {
        for (BarCodeVersion version : BarCodeVersion.values()) {
            if (version.getCode() == code) {
                return version;
            }
        }
        throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PROTOCOL_VERSION_ERROR);
    }
}
