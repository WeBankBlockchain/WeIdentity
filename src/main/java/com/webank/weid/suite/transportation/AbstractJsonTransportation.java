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

package com.webank.weid.suite.transportation;

import java.util.List;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.inf.JsonSerializer;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.transportation.inf.JsonTransportation;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;

/**
 * 二维码传输协议抽象类定义.
 * @author v_wbgyang
 *
 */
public abstract class AbstractJsonTransportation 
    extends AbstractTransportation
    implements JsonTransportation {

    @Override
    public JsonTransportation specify(List<String> verifierWeIdList) {
        this.setVerifier(verifierWeIdList);
        return this;
    }
    
    @Override
    public <T extends JsonSerializer> ResponseData<String> serialize(
        WeIdAuthentication weIdAuthentication, 
        T object,
        ProtocolProperty property
    ) {
        ResponseData<String> response = serialize(object, property);
        if (response.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()) {
            super.registerWeIdAuthentication(weIdAuthentication);
        }
        return response;
    }
}
