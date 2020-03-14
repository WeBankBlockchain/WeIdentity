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

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ServiceType;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.amop.GetBarCodeDataArgs;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.inf.JsonSerializer;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.persistence.Persistence;
import com.webank.weid.suite.api.transportation.inf.BarCodeTransportation;
import com.webank.weid.suite.api.transportation.params.EncodeType;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;
import com.webank.weid.suite.api.transportation.params.TransmissionType;
import com.webank.weid.suite.encode.EncodeProcessorFactory;
import com.webank.weid.suite.entity.BarCodeVersion;
import com.webank.weid.suite.entity.EncodeData;
import com.webank.weid.suite.persistence.sql.driver.MysqlDriver;
import com.webank.weid.suite.transmission.TransmissionFactory;
import com.webank.weid.suite.transmission.TransmissionRequest;
import com.webank.weid.suite.transportation.AbstractBarCodeTransportation;
import com.webank.weid.suite.transportation.bar.protocol.BarCodeData;
import com.webank.weid.util.DataToolUtils;

/**
 * 条形码Transportation.
 * 
 * @author yanggang
 *
 */
public class BarCodeTransportationImpl 
    extends AbstractBarCodeTransportation 
    implements BarCodeTransportation {

    private static final Logger logger =
        LoggerFactory.getLogger(BarCodeTransportationImpl.class);
    
    private static final BarCodeVersion version = BarCodeVersion.V1;
    
    private Persistence dataDriver;
        
    private Persistence getDataDriver() {
        if (dataDriver == null) {
            dataDriver = new MysqlDriver();
        }
        return dataDriver;
    }
    
    @Override
    public <T extends JsonSerializer> ResponseData<String> serialize(
        T object, 
        ProtocolProperty property) {
        
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
            // 构建BarCode协议数据
            BarCodeData barBaseData = buildBarCodeData(property);
            logger.info("[serialize] encode by {}.", property.getEncodeType().name());
            // 如果是原文方式，则直接放对象,data为对象类型
            if (property.getEncodeType() == EncodeType.ORIGINAL) {
                barBaseData.setData(object.toJson());
            } else {
                // 非原文格式，根据data进行编解码，data为字符串类型
                // 创建编解码实体对象，对此实体中的data编码操作
                EncodeData encodeData =
                    new EncodeData(
                        barBaseData.getId(),
                        barBaseData.getOrgId(),
                        object.toJson(),
                        super.getVerifiers()
                    );

                String data =
                    EncodeProcessorFactory
                        .getEncodeProcessor(property.getEncodeType())
                        .encode(encodeData);
                barBaseData.setData(data);
            }
            
            //save BarCodeData
            ResponseData<Integer> save = getDataDriver().save(
                DataDriverConstant.DOMAIN_RESOURCE_INFO, 
                barBaseData.getId(), 
                DataToolUtils.serialize(barBaseData));
            if (save.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                throw new WeIdBaseException(ErrorCode.getTypeByErrorCode(save.getErrorCode()));
            }
            logger.info("[serialize] BarCodeTransportation serialization finished.");
            return new ResponseData<String>(barBaseData.getBarCodeString(), ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("[serialize] BarCodeTransportation serialization due to base error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, e.getErrorCode());
        } catch (Exception e) {
            logger.error(
                "[serialize] BarCodeTransportation serialization due to unknown error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, ErrorCode.TRANSPORTATION_BASE_ERROR);
        }
    }

    private BarCodeData buildBarCodeData(
        ProtocolProperty property) {
        
        BarCodeData barCodeData = new BarCodeData();
        barCodeData.setEncodeType(property.getEncodeType().getCode());
        barCodeData.setId(String.valueOf(nextId()));
        barCodeData.setOrgId(fiscoConfig.getCurrentOrgId());
        barCodeData.setVersion(version.getCode());
        barCodeData.setTransmissionTypeCode(property.getTransmissionType().getCode());
        barCodeData.setUriTypeCode(property.getUriType().getCode());
        return barCodeData;
    }

    @Override
    public <T extends JsonSerializer> ResponseData<T> deserialize(
        String transString, Class<T> clazz) {

        throw new WeIdBaseException(ErrorCode.THIS_IS_DEPRECATED);
    }

    @Override
    public <T extends JsonSerializer> ResponseData<T> deserialize(
        WeIdAuthentication weIdAuthentication,
        String transString, 
        Class<T> clazz) {
        
        try {
            logger.info("[deserialize] begin to execute JsonTransportation deserialize.");
            logger.info("[deserialize] the transString:{}.", transString);
            ErrorCode errorCode = checkInputForDeserialize(weIdAuthentication, transString);
            if (errorCode != ErrorCode.SUCCESS) {
                logger.error("[deserialize] checkEncodeProperty fail, errorCode:{}.", errorCode);
                return new ResponseData<T>(null, errorCode);
            }
            // 解析协议
            BarCodeData barCodeData = BarCodeData.buildByBarCodeString(transString);
            // 获取协议请求下载通过类型
            TransmissionType type = TransmissionType.getTransmissionByCode(
                barCodeData.getTransmissionTypeCode());
            // 获取原始数据，两种协议获取方式，目前只实现一种amop，支持https扩展
            ResponseData<String> result = TransmissionFactory.getTransmisson(type)
                .send(buildRequest(type, barCodeData, weIdAuthentication));
            if (result.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                logger.error(
                    "[deserialize] channel request fail:{}-{}.",
                    result.getErrorCode(),
                    result.getErrorMessage()
                );
                return new ResponseData<T>(null, 
                    ErrorCode.getTypeByErrorCode(result.getErrorCode().intValue()));
            }
            barCodeData = DataToolUtils.deserialize(result.getResult(), BarCodeData.class);
            String jsonData = DataToolUtils.convertUtcToTimestamp(
                String.valueOf(barCodeData.getData()));
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
            logger.info("[deserialize] BarCodeTransportation deserialize finished.");
            return new ResponseData<T>(object, ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("[deserialize] BarCodeTransportation deserialize due to base error.", e);
            return new ResponseData<T>(null, e.getErrorCode());
        } catch (Exception e) {
            logger.error(
                "[deserialize] BarCodeTransportation deserialize due to unknown error.", e);
            return new ResponseData<T>(null, ErrorCode.TRANSPORTATION_BASE_ERROR);
        }
    }

    private TransmissionRequest<GetBarCodeDataArgs> buildRequest(
        TransmissionType type, 
        BarCodeData barCodeData,
        WeIdAuthentication weIdAuthentication) {
        
        TransmissionRequest<GetBarCodeDataArgs> request = new TransmissionRequest<>();
        request.setOrgId(barCodeData.getOrgId());
        request.setServiceType(ServiceType.SYS_GET_BARCODE_DATA.name());
        request.setWeIdAuthentication(weIdAuthentication);
        request.setArgs(getBarCodeArgs(barCodeData, weIdAuthentication));
        request.setTransmissionType(type);
        return request;
    }
    
    private GetBarCodeDataArgs getBarCodeArgs(
        BarCodeData barCodeData, 
        WeIdAuthentication weIdAuthentication) {
        GetBarCodeDataArgs args = new GetBarCodeDataArgs();
        args.setResourceId(barCodeData.getId());
        args.setToOrgId(barCodeData.getOrgId());
        args.setFromOrgId(fiscoConfig.getCurrentOrgId());
        args.setWeId(weIdAuthentication.getWeId());
        String signValue = DataToolUtils.sign(
            barCodeData.getId(), 
            weIdAuthentication.getWeIdPrivateKey().getPrivateKey()
        );
        args.setSignValue(signValue);
        return args;
    }
    
    private ErrorCode checkInputForDeserialize(
        WeIdAuthentication weIdAuthentication,
        String transString) {
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
        Object object) {
        
        ErrorCode errorCode = checkEncodeProperty(property);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("[serialize] checkEncodeProperty fail, errorCode:{}.", errorCode);
            return errorCode;
        }
        // 检查data完整性
        errorCode = checkProtocolData(object);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("[serialize] checkProtocolData fail, errorCode:{}.", errorCode);
            return  errorCode;
        }
        return ErrorCode.SUCCESS;
    }

    @Override
    public Integer generateBarCode(
        String content, 
        BarcodeFormat format, 
        ErrorCorrectionLevel correctionLevel,
        String destPath) {
        try {
            FileOutputStream outputStream = new FileOutputStream(destPath);
            generateBarCode(content, format, correctionLevel, outputStream);
            outputStream.flush();
            outputStream.close();
            return ErrorCode.SUCCESS.getCode();
        } catch (IOException e) {
            logger.error("[generateBarCode] generate barCode error.", e);
            return ErrorCode.UNKNOW_ERROR.getCode();
        }
    }

    @Override
    public Integer generateBarCode(
        String content, 
        BarcodeFormat format, 
        ErrorCorrectionLevel correctionLevel,
        OutputStream stream) {
        try {
            BufferedImage generateBarCode = super.generateBarCode(content, format, correctionLevel);
            ImageIO.write(generateBarCode, IMG_FORMATE, stream);
            return ErrorCode.SUCCESS.getCode();
        } catch (IOException e) {
            logger.error("[generateBarCode] generate barCode error.", e);
            return ErrorCode.UNKNOW_ERROR.getCode();
        }
    }
}
