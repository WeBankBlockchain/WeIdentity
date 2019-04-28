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

package com.webank.weid.suite.transportation.qr;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.base.JsonSerialize;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.encode.EncodeProcessorFactory;
import com.webank.weid.suite.entity.EncodeData;
import com.webank.weid.suite.entity.QrCodeVersion;
import com.webank.weid.suite.transportation.AbstractTransportation;
import com.webank.weid.suite.transportation.qr.protocol.QrCodeBaseData;
import com.webank.weid.suite.transportation.qr.protocol.QrCodeProtocolProperty;
import com.webank.weid.suite.transportation.qr.protocol.QrCodeVersion1;
import com.webank.weid.util.JsonUtil;

/**
 * 二维码传输协议业务处理类.
 * @author v_wbgyang
 *
 */
public class QrCodeTransportationService
    extends AbstractTransportation
    implements QrCodeTransportation {

    private static final Logger logger = 
        LoggerFactory.getLogger(QrCodeTransportationService.class);
    
    /**
     * 支持的协议版本配置.
     */
    private static final Map<String, Class<?>> protocol_version_map;
    
    static {
        protocol_version_map = new HashMap<String, Class<?>>();
        protocol_version_map.put(QrCodeVersion.V1.name(), QrCodeVersion1.class);
    }
    
    @Override
    public <T extends JsonSerialize> ResponseData<String> serialize(
        T object, 
        QrCodeProtocolProperty property) {
        
        logger.info(
            "begin to execute QrCodeTransportationService serialization, property:{}",
            property
        );
        // 验证协议配置
        ErrorCode errorCode = checkEncodeProperty(property);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("checkEncodeProperty fail, errorCode:{}.", errorCode);
            return new ResponseData<String>(StringUtils.EMPTY, errorCode);
        }
        // 验证presentation数据
        errorCode = checkProtocolData(object);
        if (errorCode != ErrorCode.SUCCESS) {
            logger.error("checkProtocolData fail, errorCode:{}.", errorCode);
            return new ResponseData<String>(StringUtils.EMPTY, errorCode);
        }
        try {
            // 根据协议版本生成协议实体对象
            QrCodeBaseData qrCodeData = 
                QrCodeBaseData.newInstance(
                    protocol_version_map.get(property.getVersion().name())
                );
            // 构建协议header
            qrCodeData.buildQrCodeData(
                property,
                fromOrgId
            );
            // 创建编解码实体对象，对此实体中的data编码操作
            EncodeData encodeData = 
                new EncodeData(
                    qrCodeData.getId(),
                    qrCodeData.getOrgId(),
                    object.toJson()
                );
            logger.info("encode by {}.", property.getEncodeType().name());
            // 进行编码处理
            String data = 
                EncodeProcessorFactory
                    .getEncodeProcessor(qrCodeData.getEncodeType())
                    .encode(encodeData);
            qrCodeData.setData(data);
            // 将协议实体转换成协议字符串数据
            String transString = qrCodeData.buildBuffer().getTransString();
            logger.info("QrCodeTransportationService serialization finished.");
            return new ResponseData<String>(transString, ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("QrCodeTransportation serialization due to base error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, e.getErrorCode());
        } catch (Exception e) {
            logger.error("QrCodeTransportation serialization due to unknown error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, ErrorCode.UNKNOW_ERROR);
        }
    }  

    @Override
    public <T extends JsonSerialize> ResponseData<T> deserialize(
        String transString,
        Class<T> clazz) {
        
        logger.info("begin to execute QrCodeTransportationService deserialization.");
        try {
            //解析协议版本
            QrCodeVersion version = QrCodeBaseData.getQrCodeVersion(transString);
            //根据协议版本生成协议实体对象
            QrCodeBaseData qrCodeData = 
                QrCodeBaseData.newInstance(protocol_version_map.get(version.name()));
            //将协议字符串构建成协议对象
            qrCodeData.buildData(transString);
            EncodeData encodeData = 
                new EncodeData(qrCodeData.getId(), qrCodeData.getOrgId(), qrCodeData.getData());
            logger.info("encode by {}.", qrCodeData.getEncodeType().name());
            //进行解码处理
            String data = 
                EncodeProcessorFactory
                    .getEncodeProcessor(qrCodeData.getEncodeType())
                    .decode(encodeData);
            //将解压出来的数据进行反序列化成原数据对象
            T presentation = JsonUtil.jsonStrToObj(clazz, data);
            logger.info("QrCodeTransportationService deserialization finished.");
            return new ResponseData<T>(presentation, ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("QrCodeTransportation deserialization due to base error.", e);
            return new ResponseData<T>(null, e.getErrorCode());
        } catch (Exception e) {
            logger.error("QrCodeTransportation deserialization due to unknown error.", e);
            return new ResponseData<T>(null, ErrorCode.UNKNOW_ERROR);
        }
    }
}
