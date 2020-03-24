/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

/**
 * 编解码配置.
 * @author v_wbgyang
 *
 */
public class ProtocolProperty {
    
    /**
     * 协议编解码类型, 用于决定数据是否加密.
     */
    private EncodeType encodeType;
    
    /**
     * 数据传输类型, 默认为AMOP通道进行数据传输.
     */
    private TransType transType = TransType.AMOP;
    
    /**
     * 条码类型, 用于控制条码协议中第三段是机构名/短URI/长URI.
     * 目前默认为ORG,表示协议第三段为机构名如: 00org/resourceId
     */
    private UriType uriType = UriType.ORG;

    public EncodeType getEncodeType() {
        return encodeType;
    }
    
    public ProtocolProperty(EncodeType encodeType) {
        this.encodeType = encodeType;
    }

    public TransType getTransType() {
        return transType;
    }

    public UriType getUriType() {
        return uriType;
    }
    
}
