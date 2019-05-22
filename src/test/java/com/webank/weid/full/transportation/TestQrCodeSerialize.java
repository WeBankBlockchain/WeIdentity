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

package com.webank.weid.full.transportation;

import java.lang.reflect.Method;
import java.util.HashMap;

import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.transportation.TransportationFactory;
import com.webank.weid.suite.api.transportation.params.EncodeType;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;
import com.webank.weid.suite.crypto.CryptService;
import com.webank.weid.suite.crypto.CryptServiceFactory;
import com.webank.weid.suite.entity.CryptType;
import com.webank.weid.suite.transportation.qr.protocol.QrCodeBaseData;

/**
 * 二维码协议序列化测试.
 * @author v_wbgyang
 *
 */
public class TestQrCodeSerialize extends TestBaseTransportation {
    
    private static final Logger logger = LoggerFactory.getLogger(TestQrCodeSerialize.class);
    
    private PresentationE presentation;
    
    @Override
    public synchronized void testInit() {
        mockMysqlDriver();
        presentation = getPresentationE();
    }
    
    /**
     * 使用原文方式构建协议数据.
     */
    @Test
    public void testSerializeCase1() {
        ResponseData<String> response =
            TransportationFactory.newQrCodeTransportation().serialize(
                presentation,
                new ProtocolProperty(EncodeType.ORIGINAL)
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }
    
    /**
     * 使用密文方式构建协议数据.
     */
    @Test
    public void testSerializeCase2() {
        ResponseData<String> response =
            TransportationFactory.newQrCodeTransportation().serialize(
                presentation,
                new ProtocolProperty(EncodeType.CIPHER)
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }
    
    /**
     * 传入的协议配置为null.
     */
    @Test
    public void testSerializeCase3() {
        long startTime = System.currentTimeMillis();
        ResponseData<String> response =
            TransportationFactory.newQrCodeTransportation().serialize(presentation, null);
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_PROPERTY_ERROR.getCode(),
            response.getErrorCode().intValue()
        );
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
        System.out.println("time:" + (System.currentTimeMillis() - startTime) );
    }
    
    /**
     * 传入协议配置的编解码为null.
     */
    @Test
    public void testSerializeCase4() {
        ResponseData<String> response =
            TransportationFactory.newQrCodeTransportation().serialize(presentation, new ProtocolProperty(null));
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_ENCODE_ERROR.getCode(),
            response.getErrorCode().intValue()
        );
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }
    
    /**
     * 传入实体数据为null.
     */
    @Test
    public void testSerializeCase5() {
        PresentationE presentation = null;
        ResponseData<String> response =
            TransportationFactory.newQrCodeTransportation().serialize(
                presentation,
                new ProtocolProperty(EncodeType.ORIGINAL)
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_DATA_INVALID.getCode(),
            response.getErrorCode().intValue()
        );
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }
    
    /**
     * 传入实体数据的Credential中的凭证ID存在分隔符.
     */
    @Test
    public void testSerializeCase6() {
        PresentationE presentation = this.getPresentationE();
        presentation.getContext().add("value|v");
        ResponseData<String> response =
            TransportationFactory.newQrCodeTransportation().serialize(
                presentation,
                new ProtocolProperty(EncodeType.ORIGINAL)
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_FIELD_INVALID.getCode(),
            response.getErrorCode().intValue()
        );
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }
    
    /**
     * mock异常情况.
     */
    @Test
    public void testSerializeCase7() {
        
        MockUp<CryptServiceFactory> mockTest = new MockUp<CryptServiceFactory>() {
            @Mock
            public CryptService getCryptService(CryptType cryptType) {
                return new HashMap<String, CryptService>().get("key");
            }
        };
        
        ResponseData<String> response =
            TransportationFactory.newQrCodeTransportation().serialize(
                presentation,
                new ProtocolProperty(EncodeType.CIPHER)
            );
        mockTest.tearDown();
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_ENCODE_BASE_ERROR.getCode(),
            response.getErrorCode().intValue()
        );
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }
    
    /**
     * mock异常情况.
     */
    @Test
    public void testSerializeCase8() {
        
        MockUp<QrCodeBaseData> mockTest = new MockUp<QrCodeBaseData>() {
            @Mock
            public QrCodeBaseData newInstance(Class<?> cls) throws ReflectiveOperationException {
                return new HashMap<String, QrCodeBaseData>().get("key");
            }
        };
        
        ResponseData<String> response =
            TransportationFactory.newQrCodeTransportation().serialize(
                presentation, 
                new ProtocolProperty(EncodeType.CIPHER)
            );
        mockTest.tearDown();
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }   
 
    /**
     * mock异常情况.
     */
    @Test
    public void testSerializeCase9() {
        
        MockUp<QrCodeBaseData> mockTest = new MockUp<QrCodeBaseData>() {
            @Mock
            public Method getGetterMethod(
                Class<?> cls,
                String fieldName
            ) throws NoSuchMethodException {
                return cls.getMethod("get" + fieldName, new Class[0]);
            }
        };
        
        ResponseData<String> response =
            TransportationFactory.newQrCodeTransportation().serialize(
                presentation,
                new ProtocolProperty(EncodeType.CIPHER)
            );
        mockTest.tearDown();
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.BASE_ERROR.getCode(),
            response.getErrorCode().intValue()
        );
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }
}
