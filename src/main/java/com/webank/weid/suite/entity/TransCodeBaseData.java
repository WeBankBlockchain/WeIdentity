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

package com.webank.weid.suite.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.ProtocolSuiteException;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;

@Getter
@Setter
public abstract class TransCodeBaseData extends TransBaseData {
    
    private static final Logger logger = LoggerFactory.getLogger(TransCodeBaseData.class);
    
    /**
     * 协议通讯类型.
     */
    private int transTypeCode;
    
    /**
     * 控制URI类型:(机构/长URI/短URI).
     */
    private int uriTypeCode;
    
    /**
     * 根据配置对象构建协议对象.
     * 
     * @return 返回协议字符串
     */
    public abstract String buildCodeString();

    /**
     * 根据协议字符串构建协议对象基础数据.
     * 
     * @param codeString 协议字符串
     */
    public abstract void buildCodeData(String codeString);

    /**
     *  根据协议配置, 机构编码和资源编号构建协议对象.
     *  
     * @param property 协议配置
     * @param amopId 机构编码
     * @param resourceId 资源编号
     */
    public void buildCodeData(ProtocolProperty property, String amopId, String resourceId) { 
        this.setEncodeType(property.getEncodeType().getCode());
        this.setId(resourceId);
        this.setAmopId(amopId);
        this.setTransTypeCode(property.getTransType().getCode());
        this.setUriTypeCode(property.getUriType().getCode());
    }
    
    /**
     * 检查协议对象是否正确.
     * 
     * @return true表示正确，false表示错误
     */
    public boolean check() {
        if (super.getAmopId().indexOf(PROTOCOL_PARTITION) != -1) {
            logger.error("[check] the value of amopId error, amopId: {}", super.getAmopId());
            return false;
        }
        return true;
    }
    
    /**
     * 根据协议版本类实例化协议对象.
     * .
     * @param cls 协议类对象
     * @return Object 返回协议对象
     * @throws ReflectiveOperationException 实例创建失败
     */
    public static TransCodeBaseData newInstance(Class<?> cls) throws ReflectiveOperationException {
        return (TransCodeBaseData)cls.newInstance();
    }
    
    /**
     * 根据协议获取版本编号.
     * 
     * @param transString this is transString
     * @return Object of version
     */
    public static int getVersion(String transString) {
        if (StringUtils.isBlank(transString) || transString.indexOf(PROTOCOL_PARTITION) == -1) {
            throw new ProtocolSuiteException(ErrorCode.TRANSPORTATION_PROTOCOL_STRING_INVALID);
        }
        try {
            String version = transString.substring(0, transString.indexOf(PROTOCOL_PARTITION));
            return Integer.parseInt(version);
        } catch (Exception e) {
            logger.error("[getVersion] get the version has error.", e);
            throw new ProtocolSuiteException(ErrorCode.TRANSPORTATION_PROTOCOL_VERSION_ERROR);
        }
    }
}
