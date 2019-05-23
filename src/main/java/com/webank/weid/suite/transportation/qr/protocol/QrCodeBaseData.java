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

package com.webank.weid.suite.transportation.qr.protocol;

import java.lang.reflect.Method;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.ProtocolSuiteException;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.suite.api.transportation.params.EncodeType;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;
import com.webank.weid.suite.entity.QrCodeVersion;

/**
 * 协议基本配置类.
 * @author v_wbgyang
 *
 */
@Getter
@Setter
public abstract class QrCodeBaseData {
    
    /**
     * 协议分隔符.
     */
    protected static final String PROTOCOL_PARTITION = "|";
    
    /**
     * 协议分隔符(split需要转义).
     */
    protected static final String PROTOCOL_PARTITION_DIVISION = "\\" + PROTOCOL_PARTITION;
    
    /**
     * 协议字符串第一段必须是协议版本.
     */
    protected static final String PROTOCOL_VERSION = "version|";
    
    /**
     * 协议编解码方法.
     */
    private EncodeType encodeType;
     
    /**
     * 协议版本.
     */
    private QrCodeVersion version;

    /**
     * 协议数据签发机构.
     */
    private String orgId;

    /**
     * 协议负载数据编码.
     */
    private String id;

    /**
     * 协议负载.
     */
    private String data;
    
    /**
     * 协议扩展字段.
     */
    private String extendData;

    /**
     * 用于拼接协议字符串.
     */
    protected StringBuffer buffer = new StringBuffer();
   
    public String getExtendData() {
        return extendData != null ? extendData : "?";
    }
    
    /**
     * get TransString of Meta.
     * @return
     */
    public String getTransString() {
        return this.buffer.toString();
    }
    
    public abstract void buildQrCodeData(
        ProtocolProperty protocol, 
        String orgId
    );
    
    /**
     * 配置协议头.
     * @param encodeType 编解码类型
     * @param version 协议版本
     */
    protected void buildHead(EncodeType encodeType, QrCodeVersion version) {
        this.encodeType = encodeType;
        this.version = version;
    }
    
    /**
     * 根据协议对象构建协议字符串buffer.
     * @return 返回协议对象
     */
    public abstract QrCodeBaseData buildBuffer();
    
    /**
     * 序列化协议对象.
     * @param protocol 协议模板字符串
     */
    protected void buildBuffer(String[] protocols) {
        buffer.setLength(0);
        try {
            //遍历协议字段
            for (String  protocolField: protocols) {
                //获取字段对应的get方法
                Method method = getGetterMethod(this.getClass(), protocolField);
                //调用方法获取字段对应的协议值
                String value = method.invoke(this, new Object[0]).toString();
                //如果协议值中包含分割符则抛异常
                if (value.indexOf(PROTOCOL_PARTITION) >= 0) {
                    throw new ProtocolSuiteException(
                        ErrorCode.TRANSPORTATION_PROTOCOL_FIELD_INVALID
                    );
                }
                //将协议字段拼接成协议字符串
                if (buffer.length() == 0) {
                    buffer.append(value);
                } else {
                    buffer.append(PROTOCOL_PARTITION).append(value); 
                }
            }
        } catch (ProtocolSuiteException  e) {
            throw e;
        } catch (ReflectiveOperationException
            | SecurityException e) {
            throw new WeIdBaseException("buildBuffer error.", e);
        }
    }
    
    /**
     * 根据协议字符串数据构建协议对象数据.
     * @param transString 协议字符串
     * @return  返回协议实体对象
     */
    public abstract QrCodeBaseData buildData(String transString);
    
    /**
     * 根据协议字符串数据构建协议对象数据.
     * @param protocol  协议模板字符串
     * @param transString 协议字符串数据
     */
    protected void buildData(String[] protocols, String transString) {
        buffer.setLength(0);
        buffer.append(transString);
        //解析得到协议字段数据数组
        String[] values = buffer.toString().split(PROTOCOL_PARTITION_DIVISION);
        //如果解析的协议字段数据数组和协议字段数组不匹配，则抛出异常协议数组错误
        if (values.length != protocols.length) {
            throw new ProtocolSuiteException(ErrorCode.TRANSPORTATION_PROTOCOL_STRING_INVALID);
        }
        try {
            //遍历协议字段数组
            for (int i = 0; i < protocols.length; i++) {
                //获取响应的set方法
                Method setMethod = getSetterMethod(this.getClass(), protocols[i]);
                String value = values[i];
                //获取第一个参数的类型
                Class<?> theFirstParamType = setMethod.getParameterTypes()[0];
                //如果是枚举,则调用特定方法获取枚举对象
                if (theFirstParamType.isEnum()) {
                    Method method = theFirstParamType.getMethod("getObject", String.class);
                    Object obj = method.invoke(theFirstParamType, value);
                    if (obj == null) {
                        throw new ProtocolSuiteException(
                            ErrorCode.TRANSPORTATION_PROTOCOL_STRING_INVALID
                        );
                    }
                    setMethod.invoke(this, obj);
                } else {
                    //否则直接反射赋值
                    setMethod.invoke(this, value);
                }
            }
        } catch (ReflectiveOperationException
            | SecurityException e) {
            throw new WeIdBaseException("buildData error.", e);
        } 
    }
    
    /**
     * 获取对应字段的get方法.
     * @param cls 类型名
     * @param fieldName 字段名
     * @return
     */
    private Method getGetterMethod(Class<?> cls, String fieldName) throws NoSuchMethodException {
        return cls.getMethod("get"
            + fieldName.substring(0, 1).toUpperCase(Locale.getDefault())
            + fieldName.substring(1), new Class[0]);
    }
    
    /**
     * 获取对应的set方法.
     * @param cls 类型名
     * @param fieldName 字段名
     * @return 返回方法对象
     * @throws SecurityException 有可能产生的异常
     * @throws NoSuchMethodException 有可能产生无方法的异常
     */
    private Method getSetterMethod(
        Class<?> cls, 
        String fieldName
    ) throws NoSuchMethodException, SecurityException {
        String methodName = "set"
            + fieldName.substring(0, 1).toUpperCase(Locale.getDefault())
            + fieldName.substring(1);
        return cls.getMethod(methodName, getGetterMethod(cls, fieldName).getReturnType());
    }
    
    /**
     * build Meta instance by Class.
     * @param cls class Type
     * @return
     */
    public static QrCodeBaseData newInstance(Class<?> cls) throws ReflectiveOperationException {
        return (QrCodeBaseData)cls.newInstance();
    }
    
    /**
     * get the MetaVersion by transString.
     * @param transString this is transString
     * @return
     */
    public static QrCodeVersion getQrCodeVersion(String transString) {
        if (StringUtils.isBlank(transString) || transString.indexOf(PROTOCOL_PARTITION) == -1) {
            throw new ProtocolSuiteException(ErrorCode.TRANSPORTATION_PROTOCOL_STRING_INVALID);
        }
        String version = transString.substring(0, transString.indexOf(PROTOCOL_PARTITION));
        QrCodeVersion metaVersion = QrCodeVersion.getObject(version);
        if (metaVersion == null) {
            throw new ProtocolSuiteException(ErrorCode.TRANSPORTATION_PROTOCOL_VERSION_ERROR);
        }
        return metaVersion;
    }
}
