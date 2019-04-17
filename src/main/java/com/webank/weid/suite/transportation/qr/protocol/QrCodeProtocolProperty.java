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

package com.webank.weid.suite.transportation.qr.protocol;

import com.webank.weid.suite.entity.EncodeProperty;
import com.webank.weid.suite.entity.EncodeType;
import com.webank.weid.suite.entity.QrCodeVersion;

/**
 * 协议属性配置.
 * @author v_wbgyang
 *
 */
public class QrCodeProtocolProperty extends EncodeProperty {
    
    /**
     * 协议默认版本.
     */
    private QrCodeVersion version = QrCodeVersion.V1;

    public QrCodeVersion getVersion() {
        return version;
    }

    public QrCodeProtocolProperty(EncodeType encodeType) {
        super(encodeType);
    }
    
    @Override
    public String toString() {
        return "QrCodeProtocolProperty [version=" + version + ",encodeType=" 
            + super.getEncodeType().getCode() + "]";
    } 
}
