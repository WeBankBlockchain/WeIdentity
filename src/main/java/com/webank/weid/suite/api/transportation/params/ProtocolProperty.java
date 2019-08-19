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
     * 协议编解码类型.
     */
    private EncodeType encodeType;
    
    /**
     * 秘钥过期时间配置,单位秒,默认为24小时.
     */
    private int keyExpireTime = 86400;

    public EncodeType getEncodeType() {
        return encodeType;
    }
    
    public int getKeyExpireTime() {
        return keyExpireTime;
    }
    
    public ProtocolProperty(EncodeType encodeType) {
        this.encodeType = encodeType;
    }
    
    public ProtocolProperty(EncodeType encodeType, int keyExpireTime) {
        this(encodeType);
        this.keyExpireTime = keyExpireTime;
    }
}
