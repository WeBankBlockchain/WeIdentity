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

package com.webank.weid.suite.transportation.json.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.inf.JsonSerializer;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.transportation.inf.JsonTransportation;
import com.webank.weid.suite.api.transportation.params.EncodeType;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;
import com.webank.weid.suite.api.transportation.params.TransMode;
import com.webank.weid.suite.encode.EncodeProcessorFactory;
import com.webank.weid.suite.entity.EncodeData;
import com.webank.weid.suite.entity.JsonVersion;
import com.webank.weid.suite.transportation.AbstractJsonTransportation;
import com.webank.weid.suite.transportation.json.protocol.JsonBaseData;
import com.webank.weid.util.DataToolUtils;

/**
 * JSON协议的传输.
 *
 * @author v_wbgyang
 */
public class JsonTransportationImpl
    extends AbstractJsonTransportation
    implements JsonTransportation {

    private static final Logger logger =
        LoggerFactory.getLogger(JsonTransportationImpl.class);

    private static final JsonVersion version = JsonVersion.V1;

    @Override
    public <T extends JsonSerializer> ResponseData<String> serialize(
        T object,
        ProtocolProperty property
    ) {
        if (property != null && property.getTransMode() == TransMode.DOWNLOAD_MODE) {
            logger.error(
                "[serialize] should to call serialize(WeIdAuthentication weIdAuthentication, "
                + "T object, ProtocolProperty property).");
            return new ResponseData<String>(StringUtils.EMPTY, ErrorCode.THIS_IS_UNSUPPORTED); 
        }
        return serializeInner(object, property);
    }
    
    @Override
    protected <T extends JsonSerializer> ResponseData<String> serializeInner(
        T object,
        ProtocolProperty property
    ) {
        logger.info("[serialize] begin to execute JsonTransportation serialization, property:{}.",
            property);
        logger.info("[serialize] begin to execute JsonTransportation serialization, object:{}.",
            object);
        // 检查协议配置完整性
        ErrorCode errorCode = checkEncodeProperty(property);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("[serialize] checkEncodeProperty fail, errorCode:{}.", errorCode);
            return new ResponseData<String>(StringUtils.EMPTY, errorCode);
        }
        // 检查presentation完整性
        errorCode = checkProtocolData(object);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("[serialize] checkProtocolData fail, errorCode:{}.", errorCode);
            return new ResponseData<String>(StringUtils.EMPTY, errorCode);
        }

        try {
            // 构建JSON协议数据
            JsonBaseData jsonBaseData = buildJsonData(property);
            logger.info("[serialize] encode by {}.", property.getEncodeType().name());
            // 如果是原文方式，则直接放对象,data为对象类型
            if (property.getEncodeType() == EncodeType.ORIGINAL) {
                jsonBaseData.setData(object.toJson());
            } else {
                // 非原文格式，根据data进行编解码，data为字符串类型
                // 创建编解码实体对象，对此实体中的data编码操作
                EncodeData encodeData =
                    new EncodeData(
                        jsonBaseData.getId(),
                        jsonBaseData.getAmopId(),
                        object.toJson(),
                        super.getVerifiers()
                    );

                String data =
                    EncodeProcessorFactory
                        .getEncodeProcessor(property.getEncodeType())
                        .encode(encodeData);
                jsonBaseData.setData(data);
            }
            // 将jsonBaseData转换成JSON字符串
            String jsonData = DataToolUtils.objToJsonStrWithNoPretty(jsonBaseData);
            logger.info("[serialize] JsonTransportation serialization finished.");
            return new ResponseData<String>(jsonData, ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("[serialize] JsonTransportation serialization due to base error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, e.getErrorCode());
        } catch (Exception e) {
            logger.error("[serialize] JsonTransportation serialization due to unknown error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, ErrorCode.TRANSPORTATION_BASE_ERROR);
        }
    }
    
    @Override
    public <T extends JsonSerializer> ResponseData<T> deserialize(
        String transString,
        Class<T> clazz
    ) {
        return deserializeInner(null, transString, clazz);
    }

    @Override
    public <T extends JsonSerializer> ResponseData<T> deserialize(
        WeIdAuthentication weIdAuthentication,
        String transString, 
        Class<T> clazz
    ) {
        // 检查WeIdAuthentication合法性
        ErrorCode errorCode = checkWeIdAuthentication(weIdAuthentication);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error(
                "[deserialize] checkWeIdAuthentication fail, errorCode:{}.", 
                errorCode
            );
            return new ResponseData<T>(null, errorCode);
        }
        return deserializeInner(weIdAuthentication, transString, clazz);
    }
    
    private <T extends JsonSerializer> ResponseData<T> deserializeInner(
        WeIdAuthentication weIdAuthentication,
        String transString, 
        Class<T> clazz
    ) {
        try {
            logger.info("[deserialize] begin to execute JsonTransportation deserialize.");
            logger.info("[deserialize] the transString:{}.", transString);
            if (StringUtils.isBlank(transString)) {
                logger.error("[deserialize] the transString is blank.");
                return new ResponseData<T>(null, ErrorCode.TRANSPORTATION_PROTOCOL_DATA_INVALID);
            }
           
            //将JSON字符串解析成JsonBaseData对象
            JsonBaseData jsonBaseData = DataToolUtils.deserialize(
                transString, 
                JsonBaseData.class);
            //检查JsonBaseData合法性
            ErrorCode errorCode = checkJsonBaseData(jsonBaseData);
            if (errorCode != ErrorCode.SUCCESS) {
                logger.error("[deserialize] checkJsonBaseData fail, errorCode:{}.", errorCode);
                return new ResponseData<T>(null, errorCode);
            }

            Object data = jsonBaseData.getData();
            // 如果解析出来的data为map类型，则说明 data存放的为对象，而非字符串
            if (data instanceof Map) {
                jsonBaseData
                    .setData(DataToolUtils.objToJsonStrWithNoPretty(jsonBaseData.getData()));
            }
            //创建编解码实体对象，对此实体中的data解码操作
            EncodeData encodeData =
                new EncodeData(
                    jsonBaseData.getId(),
                    jsonBaseData.getAmopId(),
                    jsonBaseData.getData().toString(),
                    weIdAuthentication
                );
            //根据编解码类型获取编解码枚举对象
            EncodeType encodeType = EncodeType.getEncodeType(jsonBaseData.getEncodeType());
            if (encodeType == null) {
                return new ResponseData<T>(null, ErrorCode.TRANSPORTATION_PROTOCOL_ENCODE_ERROR);
            }
            logger.info("[deserialize] decode by {}.", encodeType.name());
            //进行解码操作
            String jsonString =
                EncodeProcessorFactory
                    .getEncodeProcessor(encodeType)
                    .decode(encodeData);
            return super.buildObject(jsonString, clazz);
        } catch (WeIdBaseException e) {
            logger.error("[deserialize] JsonTransportation deserialize due to base error.", e);
            return new ResponseData<T>(null, e.getErrorCode());
        } catch (Exception e) {
            logger.error("[deserialize] JsonTransportation deserialize due to unknown error.", e);
            return new ResponseData<T>(null, ErrorCode.TRANSPORTATION_BASE_ERROR);
        }
    }
    
    /**
     * 构建协议实体数据.
     *
     * @param property 协议配置对象
     * @return 返回协议实体对象
     */
    private JsonBaseData buildJsonData(ProtocolProperty property) {
        JsonBaseData jsonBaseData = new JsonBaseData();
        jsonBaseData.setEncodeType(property.getEncodeType().getCode());
        jsonBaseData.setId(DataToolUtils.getUuId32());
        jsonBaseData.setAmopId(fiscoConfig.getAmopId());
        jsonBaseData.setVersion(version.getCode());
        return jsonBaseData;
    }

    /**
     * 检查jsonBaseData合法性.
     *
     * @param jsonBaseData JSON协议实体数据
     * @return 返回错误码
     */
    private ErrorCode checkJsonBaseData(JsonBaseData jsonBaseData) {
        if (jsonBaseData == null
            || StringUtils.isBlank(jsonBaseData.getId())
            || StringUtils.isBlank(jsonBaseData.getAmopId())
            || jsonBaseData.getData() == null
            || StringUtils.isBlank(jsonBaseData.getData().toString())) {
            return ErrorCode.TRANSPORTATION_PROTOCOL_DATA_INVALID;
        }
        if (JsonVersion.getVersion(jsonBaseData.getVersion()) == null) {
            return ErrorCode.TRANSPORTATION_PROTOCOL_VERSION_ERROR;
        }
        if (EncodeType.getEncodeType(jsonBaseData.getEncodeType()) == null) {
            return ErrorCode.TRANSPORTATION_PROTOCOL_ENCODE_ERROR;
        }
        return ErrorCode.SUCCESS;
    }
}
