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
 * Transportation的数据模式, 目前支持二维码的纯数据模式和下载模式
 * 在使用transportation序列化二维码的时候可以通过这个指定二维码包含的是纯数据，还是下载模式.
 * 纯数据模式(DATA_MODEL):表示序列化出来的数据为实际数据，此模式下的内容会过大,可能无法编码到二维码中.
 *下载模式(DOWN_MODEL):将协议数据存储在数据库中，通过短编码进行映射. 
 * 
 * @author yanggang
 *
 */
public enum TransModel {
    
    /**
     * 纯数据模式.
     */
    DATA_MODEL(0),
    
    /**
     * 下载模式.
     */
    DOWN_MODEL(1),
    ;

    private Integer code;

    TransModel(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    /**
     * get TransModel By code.
     *
     * @param code the TransModel code
     * @return TransModel
     */
    public static TransModel getTransModelByCode(Integer code) {
        for (TransModel type : TransModel.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new WeIdBaseException(ErrorCode.TRANSPORTATION_TRANSMODEL_TYPE_INVALID);
    }
    
}
