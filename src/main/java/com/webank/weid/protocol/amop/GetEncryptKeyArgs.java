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


package com.webank.weid.protocol.amop;

import lombok.Getter;
import lombok.Setter;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;

/**
 * the request body for get EncryptKey.
 * 
 * @author tonychen 2019年5月7日.
 *
 */
@Getter
@Setter
public class GetEncryptKeyArgs extends AmopBaseMsgArgs {

    /**
     * the encrypKey Id.
     */
    private String keyId;
    
    /**
     * weId信息.
     */
    private String weId;
    
    /**
     * 签名信息.
     */
    private String signValue;
    
    /**
     * 扩展字符串字段.
     */
    private String extra;
}
