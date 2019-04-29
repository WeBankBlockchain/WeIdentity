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
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.crypto.CryptService;
import com.webank.weid.suite.crypto.CryptServiceFactory;
import com.webank.weid.suite.entity.CryptType;
import com.webank.weid.suite.entity.EncodeType;
import com.webank.weid.suite.transportation.qr.protocol.QrCodeBaseData;
import com.webank.weid.suite.transportation.qr.protocol.QrCodeProtocolProperty;

/**
 * 二维码协议反序列化测试.
 * @author v_wbgyang
 *
 */
public class TestDeserialize extends TestBaseTransportation {
    
    private static final Logger logger = LoggerFactory.getLogger(TestDeserialize.class);
    
    private static PresentationE presentation;
    
    private static String original_transString;
    
    @Override
    public synchronized void testInit() {
        if (presentation == null) {
            presentation = this.getPresentationE();
            original_transString = 
                qrCodeTransportation.serialize(
                    presentation, 
                    new QrCodeProtocolProperty(EncodeType.ORIGINAL)
                ).getResult();
        } 
    }
    
    /**
     * 使用原文方式构建协议数据并解析.
     */
    @Test
    public void testDeserializeCase1() {
        ResponseData<String> response =
            qrCodeTransportation.serialize(
                presentation,
                new QrCodeProtocolProperty(EncodeType.ORIGINAL)
            );
        ResponseData<PresentationE> wrapperRes =
            qrCodeTransportation.deserialize(response.getResult(), PresentationE.class);
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), wrapperRes.getErrorCode().intValue());
        Assert.assertEquals(presentation, wrapperRes.getResult());
    }
    
    /**
     * 使用密文方式构建协议数据并解析.
     */
    @Test
    public void testDeserializeCase2() {
        ResponseData<String> response =
            qrCodeTransportation.serialize(
                presentation,
                new QrCodeProtocolProperty(EncodeType.CIPHER)
            );
        ResponseData<PresentationE> wrapperRes = super.mockCipherDeserialize(response);
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), wrapperRes.getErrorCode().intValue());
        Assert.assertEquals(presentation, wrapperRes.getResult());
    }

    
    
    /**
     * 协议字符串输入为空.
     */
    @Test
    public void testDeserializeCase3() {
        String transString = null;
        ResponseData<PresentationE> wrapperRes =
            qrCodeTransportation.deserialize(transString, PresentationE.class);
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_STRING_INVALID.getCode(),
            wrapperRes.getErrorCode().intValue()
        ); 
        Assert.assertNull(wrapperRes.getResult());
    }
    
    /**
     * 协议字符串输入为空.
     */
    @Test
    public void testDeserializeCase4() {
        String transString = null;
        ResponseData<PresentationE> wrapperRes =
            qrCodeTransportation.deserialize(transString, PresentationE.class);
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_STRING_INVALID.getCode(),
            wrapperRes.getErrorCode().intValue()
        );
        Assert.assertNull(wrapperRes.getResult());
    }
    
    /**
     * 协议字符串输入非法.
     */
    @Test
    public void testDeserializeCase5() {
        String transString = "abcd";
        ResponseData<PresentationE> wrapperRes =
            qrCodeTransportation.deserialize(transString, PresentationE.class);
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_STRING_INVALID.getCode(),
            wrapperRes.getErrorCode().intValue()
        );
        Assert.assertNull(wrapperRes.getResult());
    }
    
    /**
     * 协议字符串输入第一段非法.
     */
    @Test
    public void testDeserializeCase6() {
        String trans = changeTransString(original_transString, 1, "ab");
        ResponseData<PresentationE> wrapperRes =
            qrCodeTransportation.deserialize(trans, PresentationE.class);
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_VERSION_ERROR.getCode(),
            wrapperRes.getErrorCode().intValue()
        );
        Assert.assertNull(wrapperRes.getResult());
    }
    
    /**
     * 协议字符串输入第二段非法.
     */
    @Test
    public void testDeserializeCase7() {
        String trans = changeTransString(original_transString, 2, "ab");
        ResponseData<PresentationE> wrapperRes =
            qrCodeTransportation.deserialize(trans, PresentationE.class);
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_STRING_INVALID.getCode(),
            wrapperRes.getErrorCode().intValue()
        );
        Assert.assertNull(wrapperRes.getResult());
    }
    
    /**
     * 协议字符串输入第五段非法.
     */
    @Test
    public void testDeserializeCase8() {
        String trans = changeTransString(original_transString, 5, "ab");
        ResponseData<PresentationE> wrapperRes =
            qrCodeTransportation.deserialize(trans, PresentationE.class);
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(
            ErrorCode.DATA_TYPE_CASE_ERROR.getCode(),
            wrapperRes.getErrorCode().intValue()
        );
        Assert.assertNull(wrapperRes.getResult());
    }
    
    /**
     * 协议字符串输入的字段数跟协议不匹配.
     */
    @Test
    public void testDeserializeCase9() {
        String trans = original_transString + "|" + "ab";
        ResponseData<PresentationE> wrapperRes =
            qrCodeTransportation.deserialize(trans, PresentationE.class);
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_STRING_INVALID.getCode(),
            wrapperRes.getErrorCode().intValue()
        ); 
        Assert.assertNull(wrapperRes.getResult());
    }
    
    /**
     * mock异常情况.
     */
    @Test
    public void testDeserializeCase10() {
        
        ResponseData<String> response =
            qrCodeTransportation.serialize(
                presentation,
                new QrCodeProtocolProperty(EncodeType.CIPHER)
            );
        
        MockUp<CryptServiceFactory> mockTest = new MockUp<CryptServiceFactory>() {
            @Mock
            public CryptService getCryptService(CryptType cryptType) {
                return new HashMap<String, CryptService>().get("key");
            }
        };

        ResponseData<PresentationE> wrapperRes = super.mockCipherDeserialize(response);
        mockTest.tearDown();
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_ENCODE_BASE_ERROR.getCode(),
            wrapperRes.getErrorCode().intValue()
        );
        Assert.assertNull(wrapperRes.getResult());
    }
    
    /**
     * mock异常情况.
     */
    @Test
    public void testDeserializeCase11() {
        MockUp<QrCodeBaseData> mockTest = new MockUp<QrCodeBaseData>() {
            @Mock
            public QrCodeBaseData newInstance(Class<?> cls) throws ReflectiveOperationException {
                return new HashMap<String, QrCodeBaseData>().get("key");
            }
        };
        
        ResponseData<PresentationE> wrapperRes =
            qrCodeTransportation.deserialize(original_transString, PresentationE.class);
        mockTest.tearDown();
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(
            ErrorCode.UNKNOW_ERROR.getCode(),
            wrapperRes.getErrorCode().intValue()
        );
        Assert.assertNull(wrapperRes.getResult());
    } 
    
    /**
     * mock异常情况.
     */
    @Test
    public void testSerializeCase11() {
        
        MockUp<QrCodeBaseData> mockTest = new MockUp<QrCodeBaseData>() {
            @Mock
            public Method getGetterMethod(
                Class<?> cls, 
                String fieldName
            ) throws NoSuchMethodException {
                return cls.getMethod("get" + fieldName, new Class[0]);
            }
        };
        
        ResponseData<PresentationE> wrapperRes =
            qrCodeTransportation.deserialize(original_transString, PresentationE.class);
        mockTest.tearDown();
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(
            ErrorCode.BASE_ERROR.getCode(),
            wrapperRes.getErrorCode().intValue()
        );
        Assert.assertNull(wrapperRes.getResult());
    } 
    
    /**
     * 改变协议指定段数据.
     * @param transString 原协议数据
     * @param index 段位
     * @param newString 新数据
     * @return
     */
    private String changeTransString(String transString, int index, String newString) {
        StringBuffer buffer = new StringBuffer();
        String[] trans = transString.split("\\|");
        for (int i = 0; i < trans.length; i++) {
            String value = trans[i];
            if (i == index - 1) {
                if (i == 0) {
                    buffer.append(newString);
                } else {
                    buffer.append("|").append(newString);
                }
            } else {
                if (i == 0) {
                    buffer.append(value);
                } else {
                    buffer.append("|").append(value);
                }
            }
        }
        return buffer.toString();
    }
}
