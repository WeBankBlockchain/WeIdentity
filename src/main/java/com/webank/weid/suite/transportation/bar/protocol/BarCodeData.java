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

package com.webank.weid.suite.transportation.bar.protocol;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.suite.transportation.json.protocol.JsonBaseData;

import lombok.Getter;
import lombok.Setter;

/**
 * 条码协议数据体.
 * 
 * @author yanggang
 *
 */
@Getter
@Setter
public class BarCodeData extends JsonBaseData{

    private static final String SPLIT_CHAR = "/";
    
    private static final int ORG_ID_INDEX = 0;
    
    private static final int RESOURCE_ID_INDEX = 1;
    
    /**
     * 获取协议数据.
     * 
     * @return 返回协议字符串
     */
    public String getBarCodeString() {
        StringBuffer buffer = new StringBuffer(String.valueOf(super.getTransmission()));
        buffer.append(super.getOrgId()).append(SPLIT_CHAR)
            .append(super.getId());
        return buffer.toString();
    }
    /**
     * 通过协议数据解析基本的协议对象.
     * 
     * @param barCodeString 协议字符串
     * @return 返回协议对象
     */
    public static BarCodeData buildByBarCodeString(String barCodeString) {
        BarCodeData barCodeData = new BarCodeData();
        //解析第一个字符
        String transmissionTypeStr = barCodeString.substring(0, 1);
        Integer transmissionTypeCode = Integer.parseInt(transmissionTypeStr);
        barCodeData.setTransmission(transmissionTypeCode);
        barCodeString = barCodeString.substring(1);
        String[] array = barCodeString.split(SPLIT_CHAR);
        if (array.length != 2) {
            throw new WeIdBaseException(ErrorCode.TRANSPORTATION_PROTOCOL_DATA_INVALID);
        }
        barCodeData.setOrgId(array[ORG_ID_INDEX]);
        barCodeData.setId(array[RESOURCE_ID_INDEX]);
        return barCodeData;
    }
}
