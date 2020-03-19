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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.transportation.TransportationFactory;
import com.webank.weid.suite.api.transportation.inf.Transportation;
import com.webank.weid.suite.api.transportation.params.EncodeType;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;
import com.webank.weid.suite.api.transportation.params.TransportationType;
import com.webank.weid.suite.crypto.CryptService;
import com.webank.weid.suite.crypto.CryptServiceFactory;
import com.webank.weid.suite.entity.CryptType;

/**
 * 条码协议反序列化测试.
 *
 * @author v_wbgyang
 */
public class TestBarCodeDeserialize extends TestBaseTransportation {

    private static final Logger logger = LoggerFactory.getLogger(TestBarCodeDeserialize.class);

    private static PresentationE presentation;
    private static String original_transString;
    private static Transportation transportation;
            

    @Override
    public synchronized void testInit() {
        if (presentation == null) {
            super.testInit();
            presentation = this.getPresentationE();
            transportation = 
                TransportationFactory.build(TransportationType.BAR_CODE)
                    .specify(verifier);
            original_transString = transportation.serialize(
                presentation,
                new ProtocolProperty(EncodeType.ORIGINAL)
            ).getResult();
        }
    }

    /**
     * 使用原文方式构建协议数据并解析.
     */
    @Test
    public void testDeserialize_EncodeTypeOriginal() {
        ResponseData<String> response = transportation.serialize(
            presentation,
            new ProtocolProperty(EncodeType.ORIGINAL)
        );
        ResponseData<PresentationE> wrapperRes = transportation.deserialize(
            weIdAuthentication, 
            response.getResult(), 
            PresentationE.class
        );
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), wrapperRes.getErrorCode().intValue());
        Assert.assertEquals(presentation.toJson(), wrapperRes.getResult().toJson());
    }

    /**
     * 使用密文方式构建协议数据并解析.
     */
    @Test
    public void testDeserialize_EncodeTypeCipher() {
        ResponseData<String> response = transportation.serialize(
            presentation,
            new ProtocolProperty(EncodeType.CIPHER)
        );
        ResponseData<PresentationE> wrapperRes = transportation.deserialize(
            weIdAuthentication, 
            response.getResult(), 
            PresentationE.class
        );
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), wrapperRes.getErrorCode().intValue());
        Assert.assertEquals(presentation.toJson(), wrapperRes.getResult().toJson());
    }

    /**
     * 使用密文方式构建协议数据并解析.
     */
    @Test
    public void testDeserialize_credentialPojo() {
        List<CredentialPojo> credentialPojoList = presentation.getVerifiableCredential();
        CredentialPojo credentialPojo = new CredentialPojo();
        if (credentialPojoList.size() > 0) {
            credentialPojo = credentialPojoList.get(0);
        }

        List<String> verifier = new ArrayList<String>();
        verifier.add(createWeIdNew.getWeId());
        ResponseData<String> response =
            TransportationFactory.build(TransportationType.BAR_CODE)
                .specify(verifier)
                .serialize(
                    credentialPojo, 
                    new ProtocolProperty(EncodeType.CIPHER)
                );
        ResponseData<CredentialPojo> wrapperRes = transportation.deserialize(
            weIdAuthentication, 
            response.getResult(), 
            CredentialPojo.class
        );
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(ErrorCode.ENCRYPT_KEY_NO_PERMISSION.getCode(),
            wrapperRes.getErrorCode().intValue());  
    }

    /**
     * 解析的数据和解析类型不匹配.
     */
    @Test
    public void testDeserialize_transNotMath() {
        List<CredentialPojo> credentialPojoList = presentation.getVerifiableCredential();
        CredentialPojo credentialPojo = new CredentialPojo();
        if (credentialPojoList.size() > 0) {
            credentialPojo = credentialPojoList.get(0);
        }
        ResponseData<String> response = transportation.serialize(
            credentialPojo,
            new ProtocolProperty(EncodeType.CIPHER)
        );
        ResponseData<PresentationE> wrapperRes = transportation.deserialize(
            weIdAuthentication, 
            response.getResult(), 
            PresentationE.class
        );
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(ErrorCode.TRANSPORTATION_BASE_ERROR.getCode(),
                wrapperRes.getErrorCode().intValue());        
    }

    /**
     * 协议字符串输入为空.
     */
    @Test
    public void testDeserialize_dataNull() {
        String transString = null;
        ResponseData<PresentationE> wrapperRes = transportation.deserialize(
            weIdAuthentication, 
            transString, 
            PresentationE.class
        );
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
    public void testDeserialize_transStrig() {
        String transString = "abcd";
        ResponseData<PresentationE> wrapperRes = transportation.deserialize(
            weIdAuthentication, 
            transString, 
            PresentationE.class
        );
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_BASE_ERROR.getCode(),
            wrapperRes.getErrorCode().intValue()
        );
        Assert.assertNull(wrapperRes.getResult());
    }

    /**
     * mock异常情况.
     */
    @Test
    public void testDeserializeCase5() {
        ResponseData<String> response = transportation.serialize(
            presentation,
            new ProtocolProperty(EncodeType.CIPHER)
        );

        new MockUp<CryptServiceFactory>() {
            @Mock
            public CryptService getCryptService(CryptType cryptType) {
                return new HashMap<String, CryptService>().get("key");
            }
        };

        ResponseData<PresentationE> wrapperRes = transportation.deserialize(
            weIdAuthentication, 
            response.getResult(), 
            PresentationE.class
        );
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
    public void testDeserializeCase6() {
        new MockUp<EncodeType>() {
            @Mock
            public EncodeType getObject(String value) {
                return null;
            }
        };
        ResponseData<PresentationE> wrapperRes = transportation.deserialize(
            weIdAuthentication, 
            original_transString, 
            PresentationE.class
        );

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
    public void testDeserializeCase7() {
        new MockUp<EncodeType>() {
            @Mock
            public EncodeType getObject(String value) {
                return null;
            }
        };

        ResponseData<PresentationE> response = transportation.deserialize(
            weIdAuthentication, 
            original_transString,
            PresentationE.class
        );

        LogUtil.info(logger, "deserialize", response);
        Assert.assertEquals(
            ErrorCode.UNKNOW_ERROR.getCode(),
            response.getErrorCode().intValue()
        );
        Assert.assertEquals(null, response.getResult());
    }
}
