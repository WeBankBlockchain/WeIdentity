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

package com.webank.weid.suite.transportation;

import java.lang.reflect.Method;
import java.util.List;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.service.BaseService;
import com.webank.weid.suite.api.transportation.inf.JsonTransportation;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;

/**
 * 二维码传输协议抽象类定义.
 * @author v_wbgyang
 *
 */
public abstract class AbstractJsonTransportation 
    extends BaseService 
    implements JsonTransportation {
    
    private List<String> verifierWeIdList;
    
    /**
     * 验证协议配置.
     * @param encodeProperty 协议配置实体
     * @return Error Code and Message
     */
    protected ErrorCode checkEncodeProperty(ProtocolProperty encodeProperty) {
        if (encodeProperty == null) {
            return ErrorCode.TRANSPORTATION_PROTOCOL_PROPERTY_ERROR;
        }
        if (encodeProperty.getEncodeType() == null) {
            return ErrorCode.TRANSPORTATION_PROTOCOL_ENCODE_ERROR;
        }
        return ErrorCode.SUCCESS;
    }
    
    /**
     * 验证wrapper数据.
     * @param obj wrapper数据,作为协议的rawData部分
     * @return Error Code and Message
     */
    protected ErrorCode checkProtocolData(Object obj) {
        if (obj == null) {
            return ErrorCode.TRANSPORTATION_PROTOCOL_DATA_INVALID;
        }
        return ErrorCode.SUCCESS; 
    }

    public List<String> getVerifiers() {
        return verifierWeIdList;
    }

    public void setVerifier(List<String> verifierWeIdList) {
        this.verifierWeIdList = verifierWeIdList;
    }

    public JsonTransportation specify(List<String> verifierWeIdList) {
        this.setVerifier(verifierWeIdList);
        return this;
    }
    
    /**
     * getFromJsonMethod.
     * @param clazz Class
     * @return Method
     */
    public Method getFromJsonMethod(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        Method targetMethod = null;
        for (Method method : methods) {
            if (method.getName().equals("fromJson") 
                && method.getParameterTypes().length == 1
                && method.getParameterTypes()[0] == String.class) {
                targetMethod = method;
            }
        }
        return targetMethod;
    }
}
