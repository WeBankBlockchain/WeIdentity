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
import com.webank.weid.suite.api.transportation.TransportationFactory;
import com.webank.weid.suite.api.transportation.params.EncodeType;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;
import com.webank.weid.suite.crypto.CryptService;
import com.webank.weid.suite.crypto.CryptServiceFactory;
import com.webank.weid.suite.entity.CryptType;

/**
 * 二维码协议反序列化测试.
 * @author v_wbgyang
 *
 */
public class TestJsonDeserialize extends TestBaseTransportation {
    
    private static final Logger logger = LoggerFactory.getLogger(TestJsonDeserialize.class);
    
    private static PresentationE presentation;
    
    private static String original_transString;
    
    @Override
    public synchronized void testInit() {
        if (presentation == null) {
            super.testInit();
            mockMysqlDriver();
            presentation = this.getPresentationE();
            original_transString = 
                TransportationFactory.newJsonTransportation().serialize(
                    presentation, 
                    new ProtocolProperty(EncodeType.ORIGINAL)
                ).getResult();
        } 
    }
    
    /**
     * 使用原文方式构建协议数据并解析.
     */
    @Test
    public void testDeserializeCase1() {
        ResponseData<String> response =
            TransportationFactory.newJsonTransportation().serialize(
                presentation,
                new ProtocolProperty(EncodeType.ORIGINAL)
            );
        ResponseData<PresentationE> wrapperRes = TransportationFactory.newJsonTransportation()
            .deserialize(response.getResult(), PresentationE.class);
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), wrapperRes.getErrorCode().intValue());
        Assert.assertEquals(presentation.toJson(), wrapperRes.getResult().toJson());
    }
    
    /**
     * 使用密文方式构建协议数据并解析.
     */
    @Test
    public void testDeserializeCase2() {
        ResponseData<String> response =
            TransportationFactory.newJsonTransportation().specify(verifier).serialize(
                presentation,
                new ProtocolProperty(EncodeType.CIPHER)
            );
        ResponseData<PresentationE> wrapperRes = TransportationFactory.newJsonTransportation()
            .deserialize(weIdAuthentication, response.getResult(), PresentationE.class);
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), wrapperRes.getErrorCode().intValue());
        Assert.assertEquals(presentation.toJson(), wrapperRes.getResult().toJson());
    }   
    
    /**
     * 协议字符串输入为空.
     */
    @Test
    public void testDeserializeCase3() {
        String transString = null;
        ResponseData<PresentationE> wrapperRes = TransportationFactory.newJsonTransportation()
            .deserialize(transString, PresentationE.class);
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_DATA_INVALID.getCode(),
            wrapperRes.getErrorCode().intValue()
        ); 
        Assert.assertNull(wrapperRes.getResult());
    }
    
    /**
     * 协议字符串输入非法.
     */
    @Test
    public void testDeserializeCase4() {
        String transString = "abcd";
        ResponseData<PresentationE> wrapperRes = TransportationFactory.newJsonTransportation()
            .deserialize(transString, PresentationE.class);
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(
            ErrorCode.DATA_TYPE_CASE_ERROR.getCode(),
            wrapperRes.getErrorCode().intValue()
        );
        Assert.assertNull(wrapperRes.getResult());
    }
    
    /**
     * mock异常情况.
     */
    @Test
    public void testDeserializeCase5() {       
        ResponseData<String> response =
            TransportationFactory.newJsonTransportation().specify(verifier).serialize(
                presentation,
                new ProtocolProperty(EncodeType.CIPHER)
            );
        
        MockUp<CryptServiceFactory> mockTest = new MockUp<CryptServiceFactory>() {
            @Mock
            public CryptService getCryptService(CryptType cryptType) {
                return new HashMap<String, CryptService>().get("key");
            }
        };

        ResponseData<PresentationE> wrapperRes = 
            TransportationFactory.newJsonTransportation()
                .deserialize(weIdAuthentication, response.getResult(), PresentationE.class);
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
    public void testDeserializeCase6() {
        MockUp<EncodeType> mockTest = new MockUp<EncodeType>() {
            @Mock
            public EncodeType getObject(String value) {
                return null;
            }
        };      
        ResponseData<PresentationE> wrapperRes = TransportationFactory.newJsonTransportation()
            .deserialize(original_transString, PresentationE.class);
        mockTest.tearDown();
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_ENCODE_ERROR.getCode(),
            wrapperRes.getErrorCode().intValue()
        );
        Assert.assertNull(wrapperRes.getResult());
    }  
    
    /**
     * mock异常情况.
     */
    @Test
    public void testDeserializeCase7() {     
        MockUp<EncodeType> mockTest = new MockUp<EncodeType>() {
            @Mock
            public EncodeType getObject(String value) {
                return null;
            }
        };
        
        ResponseData<PresentationE> response =
            TransportationFactory.newJsonTransportation().deserialize(
                original_transString, 
                PresentationE.class
            );
        mockTest.tearDown();
        LogUtil.info(logger, "deserialize", response);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_ENCODE_ERROR.getCode(), 
            response.getErrorCode().intValue()
        );
        Assert.assertEquals(null, response.getResult());
    }   
}
