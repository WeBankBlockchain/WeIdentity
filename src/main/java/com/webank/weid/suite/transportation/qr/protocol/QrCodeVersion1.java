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

package com.webank.weid.suite.transportation.qr.protocol;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.ProtocolSuiteException;
import com.webank.weid.suite.entity.QrCodeVersion;
import com.webank.weid.suite.entity.TransCodeBaseData;

/**
 * 协议版本V1.
 * @author v_wbgyang
 *
 */
public class QrCodeVersion1 extends TransCodeBaseData {
    
    private static final Logger logger = LoggerFactory.getLogger(QrCodeVersion1.class);
    
    private static final QrCodeVersion version = QrCodeVersion.V1;
    
    // 协议数据位置定义
    private static final int VERSION_INDEX = 0;
    private static final int ENCODE_INDEX = 1;
    private static final int AMOPID_INDEX = 2;
    private static final int RESOURCEID_INDEX = 3;
    private static final int DATA_INDEX = 4;
    private static final int EXTENDDATA_INDEX = 5;
    
    public QrCodeVersion1() {
        this.setVersion(version.getCode());
    }
    
    /**
     * 获取协议数据.
     * 协议模板: version|encodeType|orgId|id|data|extendData
     * @return 返回协议字符串
     */
    @Override
    public String buildCodeString() {
        StringBuffer buffer = new StringBuffer();
        // 第一段 控制协议版本
        buffer.append(version.getCode()).append(PROTOCOL_PARTITION);
        // 第二段 控制协议编解码相关
        buffer.append(super.getEncodeType()).append(PROTOCOL_PARTITION);
        // 第三段 控制AmopId
        buffer.append(super.getAmopId()).append(PROTOCOL_PARTITION);
        // 第四段 控制资源Id
        buffer.append(super.getId()).append(PROTOCOL_PARTITION);
        // 第五段 控制资源数据
        buffer.append(super.getData()).append(PROTOCOL_PARTITION);
        // 第六段 扩展字符串 结尾如果为空进行占位
        buffer.append(StringUtils.isBlank(super.getExtendData()) ? "?" : super.getExtendData());
        return buffer.toString();
    }

    @Override
    public void buildCodeData(String codeString) {
        String[] codeStrings = codeString.split(PARTITION_FOR_SPLIT);
        if (codeStrings.length != 6) {
            logger.error("[buildCodeData] the field of protocol invalid.");
            throw new ProtocolSuiteException(ErrorCode.TRANSPORTATION_PROTOCOL_FIELD_INVALID);
        }
        try {
            // 第一段 控制协议版本
            String versionString = codeStrings[VERSION_INDEX];
            this.setVersion(Integer.parseInt(versionString));
            // 第二段 控制协议编解码相关
            String enCodeString = codeStrings[ENCODE_INDEX];
            this.setEncodeType(Integer.parseInt(enCodeString));
            // 第三段 控制机构id
            String amopId = codeStrings[AMOPID_INDEX];
            this.setAmopId(amopId);
            // 第四段 控制资源Id
            String resourceIdString = codeStrings[RESOURCEID_INDEX];
            this.setId(resourceIdString);
            // 第五段 控制资源数据
            String dataString = codeStrings[DATA_INDEX];
            this.setData(dataString);
            // 第六段 扩展字符串
            String extendDataString = codeStrings[EXTENDDATA_INDEX];
            this.setExtendData(extendDataString);
        } catch (Exception e) {
            logger.error("[buildCodeData] the protocol string invalid.", e);
            throw new ProtocolSuiteException(ErrorCode.TRANSPORTATION_PROTOCOL_STRING_INVALID);
        }
    }
    
    @Override
    public boolean check() {
        if (!super.check()) {
            return false;
        }
        boolean checkData = String.valueOf(super.getData()).indexOf(PROTOCOL_PARTITION) == -1;
        if (!checkData) {
            logger.error("[check] the value of data error, please check the data.");
            return false;
        }
        return true;
    }
}
