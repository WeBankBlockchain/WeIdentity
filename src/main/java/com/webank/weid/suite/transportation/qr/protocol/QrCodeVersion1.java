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

import com.webank.weid.util.DataToolUtils;

/**
 * 协议版本V1.
 * @author v_wbgyang
 *
 */
public class QrCodeVersion1 extends QrCodeBaseData {

    private static final String protocol = 
        PROTOCOL_VERSION + "encodeType|orgId|id|data|extendData";
    
    private static final String[] protocols;
    
    static {
        //得到协议模板配置的协议字段
        protocols = protocol.split(PROTOCOL_PARTITION_DIVISION);
    }

    public QrCodeBaseData buildBuffer() {
        super.buildBuffer(protocols);
        return this;
    }

    public QrCodeBaseData buildData(String transString) {
        super.buildData(protocols, transString);
        return this;
    }

    @Override
    public void buildQrCodeData(
        QrCodeProtocolProperty protocol,
        String orgId
    ) {
        super.buildHead(
            protocol.getEncodeType(),
            protocol.getVersion()
        );
        super.setOrgId(orgId);
        super.setId(DataToolUtils.getUuId32());
    }
}
