

package com.webank.weid.full.transportation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.crypto.CryptoServiceFactory;
import com.webank.weid.suite.api.crypto.inf.CryptoService;
import com.webank.weid.suite.api.crypto.params.CryptoType;
import com.webank.weid.suite.api.transportation.TransportationFactory;
import com.webank.weid.suite.api.transportation.params.EncodeType;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;
import com.webank.weid.util.CredentialPojoUtils;

/**
 * 二维码协议序列化测试.
 *
 * @author v_wbgyang
 */
public class TestJsonSerialize extends TestBaseTransportation {

    private static final Logger logger = LoggerFactory.getLogger(TestJsonSerialize.class);

    private PresentationE presentation;

    @Override
    public synchronized void testInit() {
        super.testInit();
        presentation = getPresentationE();
    }

    /**
     * 使用原文方式构建协议数据.
     */
    @Test
    public void testSerializeCase1() {
        ResponseData<String> response = TransportationFactory.newJsonTransportation()
            .serialize(presentation, new ProtocolProperty(EncodeType.ORIGINAL));
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * 使用密文方式构建协议数据.
     */
    @Test
    public void testSerializeCase2() {
        ResponseData<String> response = TransportationFactory.newJsonTransportation()
            .specify(verifier)
            .serialize(presentation, new ProtocolProperty(EncodeType.CIPHER));
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    @Test
    public void testSerializeCase7() {
        List<CredentialPojo> credPojoList = new ArrayList<>();
        credPojoList.add(selectiveCredentialPojo);
        CredentialPojo doubleSigned =
            credentialPojoService.addSignature(credPojoList, weIdAuthentication)
                .getResult();
        credPojoList = new ArrayList<>();
        credPojoList.add(doubleSigned);
        CredentialPojo triSigned =
            credentialPojoService.addSignature(credPojoList, weIdAuthentication)
                .getResult();

        ResponseData<String> response = TransportationFactory.newJsonTransportation()
            .serialize(triSigned, new ProtocolProperty(EncodeType.ORIGINAL));
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        ResponseData<CredentialPojo> wrapperRes = TransportationFactory.newJsonTransportation()
            .deserialize(weIdAuthentication, response.getResult(), CredentialPojo.class);
        LogUtil.info(logger, "deserialize", wrapperRes);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), wrapperRes.getErrorCode().intValue());
        Assert.assertTrue(CredentialPojoUtils.isEqual(triSigned, wrapperRes.getResult()));
    }

    /**
     * 传入的协议配置为null.
     */
    @Test
    public void testSerializeCase3() {
        ResponseData<String> response = TransportationFactory.newJsonTransportation()
            .serialize(presentation, null);
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_PROPERTY_ERROR.getCode(),
            response.getErrorCode().intValue()
        );
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * 传入协议配置的编解码为null.
     */
    @Test
    public void testSerializeCase4() {
        ResponseData<String> response = TransportationFactory.newJsonTransportation()
            .serialize(presentation, new ProtocolProperty(null));
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
        ResponseData<String> response = TransportationFactory.newJsonTransportation()
            .serialize(presentation, new ProtocolProperty(EncodeType.ORIGINAL));
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_DATA_INVALID.getCode(),
            response.getErrorCode().intValue()
        );
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * mock异常情况.
     */
    @Test
    public void testSerializeCase6() {

        new MockUp<CryptoServiceFactory>() {
            @Mock
            public CryptoService getCryptoService(CryptoType cryptoType) {
                return new HashMap<String, CryptoService>().get("key");
            }
        };

        ResponseData<String> response = TransportationFactory.newJsonTransportation()
            .specify(verifier)
            .serialize(presentation, new ProtocolProperty(EncodeType.CIPHER));

        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_ENCODE_BASE_ERROR.getCode(),
            response.getErrorCode().intValue()
        );
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }
}
