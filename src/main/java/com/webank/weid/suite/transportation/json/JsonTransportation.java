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

package com.webank.weid.suite.transportation.json;

import com.webank.weid.protocol.base.JsonSerializer;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.transportation.json.protocol.JsonProtocolProperty;

/**
 * JSON协议的传输.
 * @author v_wbgyang
 *
 */
public interface JsonTransportation {

    /**
     * JSON协议传输序列化接口.
     * @param object 协议存储的实体数据对象
     * @param property 协议的配置对象
     * @return 返回协议字符串数据
     */
    public <T extends JsonSerializer> ResponseData<String> serialize(
        T object,
        JsonProtocolProperty property
    );

    /**
     * JSON协议反序列化接口.
     * @param transString JSON格式的协议数据字符串
     * @param clazz 需要转换成的Class类型
     * @return 返回PresentationE对象数据
     */
    public <T extends JsonSerializer> ResponseData<T> deserialize(
        String transString,
        Class<T> clazz
    );
    
}
