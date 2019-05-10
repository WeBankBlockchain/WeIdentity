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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.amop.JsonSerializer;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.encode.EncodeProcessorFactory;
import com.webank.weid.suite.entity.EncodeData;
import com.webank.weid.suite.entity.EncodeType;
import com.webank.weid.suite.entity.JsonVersion;
import com.webank.weid.suite.transportation.AbstractTransportation;
import com.webank.weid.suite.transportation.json.protocol.JsonBaseData;
import com.webank.weid.suite.transportation.json.protocol.JsonProtocolProperty;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.JsonUtil;

/**
 * JSON协议的传输.
 * 
 * @author v_wbgyang
 *
 */
public class JsonTransportationService
    extends AbstractTransportation
    implements JsonTransportation {

    private static final Logger logger = 
        LoggerFactory.getLogger(JsonTransportationService.class);
    
    @Override
    public <T extends JsonSerializer> ResponseData<String> serialize(
        T object,
        JsonProtocolProperty property) {

        logger.info(
            "begin to execute JsonTransportationService serialization, property:{}.",
            property
        );
        // 检查协议配置完整性
        ErrorCode errorCode = checkEncodeProperty(property);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("checkEncodeProperty fail, errorCode:{}.", errorCode);
            return new ResponseData<String>(StringUtils.EMPTY, errorCode);
        }
        // 检查presentation完整性
        errorCode = checkProtocolData(object);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("checkProtocolData fail, errorCode:{}.", errorCode);
            return new ResponseData<String>(StringUtils.EMPTY, errorCode);
        }
        
        try {
            // 构建JSON协议数据
            JsonBaseData jsonBaseData = buildJsonData(property);
            logger.info("encode by {}.", property.getEncodeType().name());
            // 如果是原文方式，则直接放对象,data为对象类型
            if (property.getEncodeType() == EncodeType.ORIGINAL) {
                jsonBaseData.setData(object);
            } else { 
                // 非原文格式，根据data进行编解码，data为字符串类型
                // 创建编解码实体对象，对此实体中的data编码操作
                EncodeData encodeData = 
                    new EncodeData(
                        jsonBaseData.getId(),
                        jsonBaseData.getOrgId(),
                        object.toJson()
                    );
                
                String data = 
                    EncodeProcessorFactory
                        .getEncodeProcessor(property.getEncodeType())
                        .encode(encodeData);
                jsonBaseData.setData(data);
            }
            // 将jsonBaseData转换成JSON字符串
            String jsonData = JsonUtil.objToJsonStrWithNoPretty(jsonBaseData);
            logger.info("JsonTransportationService serialization finished.");
            return new ResponseData<String>(jsonData, ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("JsonTransportation serialization due to base error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, e.getErrorCode());
        } catch (Exception e) {
            logger.error("JsonTransportation serialization due to unknown error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, ErrorCode.TRANSPORTATION_BASE_ERROR);
        }
    }

    @Override
    public <T extends JsonSerializer> ResponseData<T> deserialize(
        String transString,
        Class<T> clazz) {
        
        logger.info("begin to execute JsonTransportationService deserialization.");
        try {
            if (StringUtils.isBlank(transString)) {
                logger.error("the transString is blank.");
                return new ResponseData<T>(null, ErrorCode.TRANSPORTATION_PROTOCOL_DATA_INVALID);
            }
            //将JSON字符串解析成JsonBaseData对象
            JsonBaseData jsonBaseData = JsonUtil.jsonStrToObj(JsonBaseData.class, transString);
            //检查JsonBaseData合法性
            ErrorCode errorCode = checkJsonBaseData(jsonBaseData);
            if (errorCode != ErrorCode.SUCCESS) {
                logger.error("checkJsonBaseData fail, errorCode:{}.", errorCode);
                return new ResponseData<T>(null, errorCode);
            }
            
            Object data = jsonBaseData.getData();
            // 如果解析出来的data为map类型，则说明 data存放的为对象，而非字符串
            if (data instanceof Map) {
                jsonBaseData.setData(JsonUtil.objToJsonStrWithNoPretty(jsonBaseData.getData()));
            }
            //创建编解码实体对象，对此实体中的data解码操作
            EncodeData encodeData = 
                new EncodeData(
                    jsonBaseData.getId(),
                    jsonBaseData.getOrgId(),
                    jsonBaseData.getData().toString()
                );
            //根据编解码类型获取编解码枚举对象
            EncodeType encodeType = 
                EncodeType.getObject(String.valueOf(jsonBaseData.getEncodeType()));
            if (encodeType == null) {
                return new ResponseData<T>(null, ErrorCode.TRANSPORTATION_PROTOCOL_ENCODE_ERROR);
            }
            logger.info("decode by {}.", encodeType.name());
            //进行解码操作
            String presentationEStr = 
                EncodeProcessorFactory
                    .getEncodeProcessor(encodeType)
                    .decode(encodeData);
            T object = 
                (T) JsonUtil.jsonStrToObj(clazz, presentationEStr);
            logger.info("JsonTransportationService deserialization finished.");
            return new ResponseData<T>(object, ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("JsonTransportation deserialization due to base error.", e);
            return new ResponseData<T>(null, e.getErrorCode());
        } catch (Exception e) {
            logger.error("JsonTransportation deserialization due to unknown error.", e);
            return new ResponseData<T>(null, ErrorCode.TRANSPORTATION_BASE_ERROR);
        }
    }

    /**
     * 构建协议实体数据.
     * @param property 协议配置对象
     * @return 返回协议实体对象
     */
    private JsonBaseData buildJsonData(JsonProtocolProperty property) {
        JsonBaseData jsonBaseData = new JsonBaseData();
        jsonBaseData.setEncodeType(property.getEncodeType().getCode());
        jsonBaseData.setId(DataToolUtils.getUuId32());
        jsonBaseData.setOrgId(fromOrgId);
        jsonBaseData.setVersion(property.getVersion().getCode());
        return jsonBaseData;
    }
    
    /**
     * 检查jsonBaseData合法性.
     * @param jsonBaseData JSON协议实体数据
     * @return 返回错误码
     */
    private ErrorCode checkJsonBaseData(JsonBaseData jsonBaseData) {
        if (jsonBaseData == null 
            || StringUtils.isBlank(jsonBaseData.getId()) 
            || StringUtils.isBlank(jsonBaseData.getOrgId())
            || jsonBaseData.getData() == null
            || StringUtils.isBlank(jsonBaseData.getData().toString())) {
            return ErrorCode.TRANSPORTATION_PROTOCOL_DATA_INVALID;
        }
        if (JsonVersion.getJsonVersion(jsonBaseData.getVersion()) == null) {
            return ErrorCode.TRANSPORTATION_PROTOCOL_VERSION_ERROR;
        }
        if (EncodeType.getObject(String.valueOf(jsonBaseData.getEncodeType())) == null) {
            return ErrorCode.TRANSPORTATION_PROTOCOL_ENCODE_ERROR;
        }
        return ErrorCode.SUCCESS;
    }
}
