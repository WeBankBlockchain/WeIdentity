/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
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

package com.webank.weid.suite.transportation.bar.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.ProtocolSuiteException;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.inf.JsonSerializer;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.transportation.inf.Transportation;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;
import com.webank.weid.suite.api.transportation.params.TransMode;
import com.webank.weid.suite.api.transportation.params.TransType;
import com.webank.weid.suite.encode.EncodeProcessorFactory;
import com.webank.weid.suite.entity.BarCodeVersion;
import com.webank.weid.suite.entity.EncodeData;
import com.webank.weid.suite.entity.TransCodeBaseData;
import com.webank.weid.suite.transmission.TransmissionFactory;
import com.webank.weid.suite.transportation.AbstractCodeTransportation;
import com.webank.weid.util.DataToolUtils;

/**
 * 条形码Transportation.
 * 
 * @author yanggang
 *
 */
public class BarCodeTransportationImpl 
    extends AbstractCodeTransportation 
    implements Transportation {

    private static final Logger logger =
        LoggerFactory.getLogger(BarCodeTransportationImpl.class);
    
    private static final BarCodeVersion version = BarCodeVersion.V1;

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
    public <T extends JsonSerializer> ResponseData<String> serialize(
        WeIdAuthentication weIdAuthentication, 
        T object,
        ProtocolProperty property
    ) {
        ResponseData<String> response = serializeInner(object, property);
        if (response.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()) {
            super.registerWeIdAuthentication(weIdAuthentication);
        }
        return response;
    }
    
    @Override
    protected <T extends JsonSerializer> ResponseData<String> serializeInner(
        T object, 
        ProtocolProperty property
    ) {
        logger.info(
            "[serialize] begin to execute BarCodeTransportation serialization, property:{}.",
            property);
        logger.info("[serialize] begin to execute BarCodeTransportation serialization, object:{}.",
            object);
        // 检查协议配置完整性
        ErrorCode errorCode = checkInputForserialize(property, object);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("[serialize] checkInput fail, errorCode:{}.", errorCode);
            return new ResponseData<String>(StringUtils.EMPTY, errorCode);
        }
        try {
            TransCodeBaseData codeData = TransCodeBaseData.newInstance(version.getClz());
            // 构建协议基础数据
            codeData.buildCodeData(
                property,
                fiscoConfig.getCurrentOrgId(),
                String.valueOf(nextId())
            );
            logger.info("[serialize] encode by {}.", property.getEncodeType().name());
            // 如果是原文方式，则直接放对象,data为对象类型
            EncodeData encodeData =
                new EncodeData(
                    codeData.getId(),
                    codeData.getOrgId(),
                    object.toJson(),
                    super.getVerifiers()
                );

            String data =
                EncodeProcessorFactory
                    .getEncodeProcessor(property.getEncodeType())
                    .encode(encodeData);
            codeData.setData(data);
            if (!codeData.check()) {
                throw new ProtocolSuiteException(ErrorCode.TRANSPORTATION_PROTOCOL_FIELD_INVALID);
            }
            //save BarCodeData
            ResponseData<Integer> save = getDataDriver().save(
                DataDriverConstant.DOMAIN_RESOURCE_INFO, 
                codeData.getId(), 
                DataToolUtils.serialize(codeData)
            );
            if (save.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                throw new WeIdBaseException(ErrorCode.getTypeByErrorCode(save.getErrorCode()));
            }
            logger.info("[serialize] BarCodeTransportation serialization finished.");
            return new ResponseData<String>(codeData.buildCodeString(), ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("[serialize] BarCodeTransportation serialization due to base error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, e.getErrorCode());
        } catch (Exception e) {
            logger.error(
                "[serialize] BarCodeTransportation serialization due to unknown error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, ErrorCode.TRANSPORTATION_BASE_ERROR);
        }
    }

    @Override
    public <T extends JsonSerializer> ResponseData<T> deserialize(
        String transString, 
        Class<T> clazz
    ) {
        throw new WeIdBaseException(ErrorCode.THIS_IS_UNSUPPORTED);
    }

    @Override
    public <T extends JsonSerializer> ResponseData<T> deserialize(
        WeIdAuthentication weIdAuthentication,
        String transString, 
        Class<T> clazz
    ) {
        try {
            logger.info("[deserialize] begin to execute JsonTransportation deserialize.");
            logger.info("[deserialize] the transString:{}.", transString);
            ErrorCode errorCode = checkInputForDeserialize(weIdAuthentication, transString);
            if (errorCode != ErrorCode.SUCCESS) {
                logger.error("[deserialize] checkEncodeProperty fail, errorCode:{}.", errorCode);
                return new ResponseData<T>(null, errorCode);
            }
            int versionCode = TransCodeBaseData.getVersion(transString);
            BarCodeVersion version = BarCodeVersion.getVersion(versionCode);
            TransCodeBaseData codeData = TransCodeBaseData.newInstance(version.getClz());
            // 解析协议
            codeData.buildCodeData(transString);
            // 获取协议请求下载通过类型
            TransType type = TransType.getTransmissionByCode(codeData.getTransTypeCode());
            // 获取原始数据，两种协议获取方式，目前只实现一种amop，支持https扩展
            ResponseData<String> result = TransmissionFactory.getTransmisson(type)
                .send(buildRequest(type, codeData, weIdAuthentication));
            if (result.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                    "[deserialize] channel request fail:{}-{}.",
                    result.getErrorCode(),
                    result.getErrorMessage()
                );
                return new ResponseData<T>(null, 
                    ErrorCode.getTypeByErrorCode(result.getErrorCode().intValue()));
            }
            codeData = (TransCodeBaseData)DataToolUtils.deserialize(
                result.getResult(), 
                version.getClz()
            );
            return super.buildObject(String.valueOf(codeData.getData()), clazz);
        } catch (WeIdBaseException e) {
            logger.error("[deserialize] BarCodeTransportation deserialize due to base error.", e);
            return new ResponseData<T>(null, e.getErrorCode());
        } catch (Exception e) {
            logger.error(
                "[deserialize] BarCodeTransportation deserialize due to unknown error.", e);
            return new ResponseData<T>(null, ErrorCode.TRANSPORTATION_BASE_ERROR);
        }
    }
    
    private ErrorCode checkInputForDeserialize(
        WeIdAuthentication weIdAuthentication,
        String transString
    ) {
        // 检查WeIdAuthentication合法性
        ErrorCode errorCode = checkWeIdAuthentication(weIdAuthentication);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("[deserialize] checkWeIdAuthentication fail, errorCode:{}.", errorCode);
            return errorCode;
        }
        if (StringUtils.isBlank(transString)) {
            logger.error("[deserialize] the transString is blank.");
            return ErrorCode.TRANSPORTATION_PROTOCOL_DATA_INVALID;
        }
        return ErrorCode.SUCCESS;
    }
    
    private ErrorCode checkInputForserialize(
        ProtocolProperty property,
        Object object
    ) {
        ErrorCode errorCode = checkEncodeProperty(property);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("[serialize] checkEncodeProperty fail, errorCode:{}.", errorCode);
            return errorCode;
        }
        // 检查data完整性
        errorCode = checkProtocolData(object);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("[serialize] checkProtocolData fail, errorCode:{}.", errorCode);
            return errorCode;
        }
        return ErrorCode.SUCCESS;
    }
}
