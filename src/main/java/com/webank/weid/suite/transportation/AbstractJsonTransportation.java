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

import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.MysqlDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.inf.JsonSerializer;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.transportation.inf.JsonTransportation;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;
import com.webank.weid.suite.api.transportation.params.TransMode;
import com.webank.weid.util.DataToolUtils;

/**
 * 二维码传输协议抽象类定义.
 * @author v_wbgyang
 *
 */
public abstract class AbstractJsonTransportation 
    extends AbstractTransportation
    implements JsonTransportation {
    
    private static final Logger logger =
        LoggerFactory.getLogger(AbstractJsonTransportation.class);
    
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
        ResponseData<String> response = serializeInner(object, property);
        if (response.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()
            && property.getTransMode() == TransMode.DOWNLOAD_MODE) {
            super.registerWeIdAuthentication(weIdAuthentication);
        }
        return response;
    }
    
    protected abstract <T extends JsonSerializer> ResponseData<String> serializeInner(
        T object, 
        ProtocolProperty property
    );
    
    protected void saveTransData(String id, Object data) {
        ResponseData<Integer> save = getDataDriver().add(
            MysqlDriverConstant.DOMAIN_RESOURCE_INFO,
            id, 
            DataToolUtils.serialize(data)
        );
        if (save.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            throw new WeIdBaseException(ErrorCode.getTypeByErrorCode(save.getErrorCode()));
        }
    }
    
    protected <T extends JsonSerializer> ResponseData<T> buildObject(
        String jsonString,
        Class<T> clazz
    ) throws Exception {
        
        String jsonData = DataToolUtils.convertUtcToTimestamp(jsonString);
        String jsonDataNew = jsonData;
        if (DataToolUtils.isValidFromToJson(jsonData)) {
            jsonDataNew = DataToolUtils.removeTagFromToJson(jsonData);
        }
        T object = null;
        Method method = getFromJsonMethod(clazz);
        if (method == null) {
            //调用工具的反序列化 
            object = (T) DataToolUtils.deserialize(jsonDataNew, clazz);
        } else  {
            object = (T) method.invoke(null, jsonDataNew);
        }
        logger.info("[deserialize] Transportation deserialize finished.");
        return new ResponseData<T>(object, ErrorCode.SUCCESS);
    }
}
