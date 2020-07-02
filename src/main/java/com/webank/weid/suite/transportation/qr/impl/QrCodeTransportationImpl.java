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

package com.webank.weid.suite.transportation.qr.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.ProtocolSuiteException;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.inf.JsonSerializer;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.transportation.inf.QrCodeTransportation;
import com.webank.weid.suite.api.transportation.params.EncodeType;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;
import com.webank.weid.suite.api.transportation.params.TransMode;
import com.webank.weid.suite.api.transportation.params.TransType;
import com.webank.weid.suite.encode.EncodeProcessorFactory;
import com.webank.weid.suite.entity.EncodeData;
import com.webank.weid.suite.entity.QrCodeVersion;
import com.webank.weid.suite.entity.TransCodeBaseData;
import com.webank.weid.suite.transmission.TransmissionFactory;
import com.webank.weid.suite.transportation.AbstractCodeTransportation;
import com.webank.weid.util.DataToolUtils;

/**
 * 二维码传输协议业务处理类.
 * @author v_wbgyang
 *
 */
public class QrCodeTransportationImpl 
    extends AbstractCodeTransportation
    implements QrCodeTransportation {

    private static final Logger logger = 
        LoggerFactory.getLogger(QrCodeTransportationImpl.class);
    
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
    
    protected <T extends JsonSerializer> ResponseData<String> serializeInner(
        T object, 
        ProtocolProperty property
    ) {
        logger.info(
            "[serialize] begin to execute QrCodeTransportation serialization, property:{}.",
            property
        );
        logger.info(
            "[serialize] begin to execute QrCodeTransportation serialization, object:{}.", object);

        // 验证协议配置
        ErrorCode errorCode = checkEncodeProperty(property);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("[serialize] checkEncodeProperty fail, errorCode:{}.", errorCode);
            return new ResponseData<String>(StringUtils.EMPTY, errorCode);
        }
        // 验证presentation数据
        errorCode = checkProtocolData(object);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("[serialize] checkProtocolData fail, errorCode:{}.", errorCode);
            return new ResponseData<String>(StringUtils.EMPTY, errorCode);
        }
        try {
            // 根据协议版本生成协议实体对象
            QrCodeVersion version = QrCodeVersion.V1;
            if (property.getTransMode() == TransMode.DOWNLOAD_MODE) {
                // 下载模式
                version = QrCodeVersion.V2;
            }
            TransCodeBaseData codeData = TransCodeBaseData.newInstance(version.getClz());
            // 构建协议基础数据
            String uuId = DataToolUtils.getUuId32();
            codeData.buildCodeData(property, fiscoConfig.getAmopId(), uuId);
            
            // 创建编解码实体对象，对此实体中的data编码操作
            EncodeData encodeData = 
                new EncodeData(
                    codeData.getId(),
                    codeData.getAmopId(),
                    object.toJson(),
                    super.getVerifiers()
                );
            logger.info("[serialize] encode by {}.", property.getEncodeType().name());
            // 进行编码处理
            String data = 
                EncodeProcessorFactory
                    .getEncodeProcessor(property.getEncodeType())
                    .encode(encodeData);
            codeData.setData(data);
            if (!codeData.check()) {
                throw new ProtocolSuiteException(ErrorCode.TRANSPORTATION_PROTOCOL_FIELD_INVALID);
            }
            
            logger.info("[serialize] the transMode is {}", property.getTransMode());
            if (version == QrCodeVersion.V2) {
                // 下载模式
                //save CodeData
                saveTransData(codeData.getId(), codeData);
            }
            logger.info("[serialize] QrCodeTransportation serialization finished.");
            return new ResponseData<String>(codeData.buildCodeString(), ErrorCode.SUCCESS); 
        } catch (WeIdBaseException e) {
            logger.error("[serialize] QrCodeTransportation serialization due to base error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, e.getErrorCode());
        } catch (Exception e) {
            logger.error("[serialize] QrCodeTransportation serialization due to unknown error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, ErrorCode.UNKNOW_ERROR);
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
        TransMode transMode = super.getTransMode(transString);
        if (transMode == TransMode.DATA_MODE) {
            return deserializeInner(weIdAuthentication, transString, clazz);
        }
        return deserializeInnerForDown(weIdAuthentication, transString, clazz);
    }
    
    private <T extends JsonSerializer> ResponseData<T> deserializeInner(
        WeIdAuthentication weIdAuthentication,
        String transString, 
        Class<T> clazz
    ) {
        try {
            logger.info("[deserialize] begin to execute QrCodeTransportation deserialize.");
            logger.info("[deserialize] the transString:{}.", transString);
            //解析协议版本
            int versionCode = TransCodeBaseData.getVersion(transString);
            QrCodeVersion version = QrCodeVersion.getVersion(versionCode);
            //根据协议版本生成协议实体对象
            TransCodeBaseData codeData = TransCodeBaseData.newInstance(version.getClz());
            //将协议字符串构建成协议对象
            codeData.buildCodeData(transString);
            EncodeData encodeData = 
                new EncodeData(
                    codeData.getId(),
                    codeData.getAmopId(),
                    String.valueOf(codeData.getData()),
                    weIdAuthentication
                );
            EncodeType enCodeType = EncodeType.getEncodeType(codeData.getEncodeType());
            logger.info("[deserialize] encode by {}.", enCodeType.name());
            //进行解码处理
            String jsonString = 
                EncodeProcessorFactory
                    .getEncodeProcessor(enCodeType)
                    .decode(encodeData);
            logger.info("[deserialize] QrCodeTransportation deserialize finished.");
            return super.buildObject(jsonString, clazz);
        } catch (WeIdBaseException e) {
            logger.error("[deserialize] QrCodeTransportation deserialize due to base error.", e);
            return new ResponseData<T>(null, e.getErrorCode());
        } catch (Exception e) {
            logger.error("[deserialize] QrCodeTransportation deserialize due to unknown error.", e);
            return new ResponseData<T>(null, ErrorCode.UNKNOW_ERROR);
        }
    }
    
    private <T extends JsonSerializer> ResponseData<T> deserializeInnerForDown(
        WeIdAuthentication weIdAuthentication,
        String transString, 
        Class<T> clazz
    ) {
        try {
            logger.info("[deserialize] begin to execute JsonTransportation deserialize.");
            logger.info("[deserialize] the transString:{}.", transString);
            // 解析协议版本
            int versionCode = TransCodeBaseData.getVersion(transString);
            QrCodeVersion version = QrCodeVersion.getVersion(versionCode);
            //根据协议版本生成协议实体对象
            TransCodeBaseData codeData = TransCodeBaseData.newInstance(version.getClz());
            //将协议字符串构建成协议对象
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
            logger.error("[deserialize] QrCodeTransportation deserialize due to base error.", e);
            return new ResponseData<T>(null, e.getErrorCode());
        } catch (Exception e) {
            logger.error(
                "[deserialize] QrCodeTransportation deserialize due to unknown error.", e);
            return new ResponseData<T>(null, ErrorCode.TRANSPORTATION_BASE_ERROR);
        }
    }
}
