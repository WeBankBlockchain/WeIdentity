package com.webank.weid.full.transportation;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
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
import com.webank.weid.suite.api.transportation.params.EncodeType;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;
import com.webank.weid.suite.crypto.CryptService;
import com.webank.weid.suite.crypto.CryptServiceFactory;
import com.webank.weid.suite.entity.CryptType;

/**
 * test base class.
 */
public class TestPdfDeserialize extends TestBaseTransportation {

    private static final Logger logger = LoggerFactory.getLogger(TestPdfDeserialize.class);

    private static PresentationE presentation;
    private static PresentationE presentation4MlCpt;
    private static PresentationE presentation4MultiCpt;
    private static PresentationE presentation4SpecTpl;

    @Override
    public synchronized void testInit() {
        if (presentation == null) {
            super.testInit();
            super.testInit4MlCpt();
            super.testInit4MultiCpt();
            super.testInitSpecTplCpt();
            mockMysqlDriver();
            presentation = this.getPresentationE();
            presentation4MlCpt = getPresentationE4MlCpt();
            presentation4MultiCpt = getPresentationE4MultiCpt();
            presentation4SpecTpl = getPresentationE4SpecTplCpt();
        }
    }

    /**
     * 使用原文方式构建协议数据并解析.
     */
    @Test
    public void testDeserializeCase1() {
        ResponseData<OutputStream> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
                presentation,
                new ProtocolProperty((EncodeType.ORIGINAL)),
                weIdAuthentication);

        ResponseData<PresentationE> resDeserialize = TransportationFactory
            .newPdfTransportation()
            .deserialize(
                response.getResult(),
                PresentationE.class,
                weIdAuthentication);
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), resDeserialize.getErrorCode().intValue());
        Assert.assertEquals(presentation.toJson(), resDeserialize.getResult().toJson());
    }

    /**
     * 使用密文方式构建协议数据并解析.
     */
    @Test
    public void testDeserializeCase2() {
        ResponseData<OutputStream> response = TransportationFactory
            .newPdfTransportation()
            .specify(verifier)
            .serialize(
                presentation4MlCpt,
                new ProtocolProperty((EncodeType.CIPHER)),
                weIdAuthentication);

        ResponseData<PresentationE> resDeserialize = TransportationFactory
            .newPdfTransportation()
            .specify(verifier)
            .deserialize(
                response.getResult(),
                PresentationE.class,
                weIdAuthentication);
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), resDeserialize.getErrorCode().intValue());
        Assert.assertEquals(presentation4MlCpt.toJson(), resDeserialize.getResult().toJson());
    }

    /**
     * 未设置verifier导致的无权限获取密钥数据.
     */
    @Test
    public void testDeserializeCase3() {
        ResponseData<OutputStream> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
                presentation4MlCpt,
                new ProtocolProperty((EncodeType.CIPHER)),
                weIdAuthentication);

        ResponseData<PresentationE> resDeserialize = TransportationFactory
            .newPdfTransportation()
            .deserialize(
                response.getResult(),
                PresentationE.class,
                weIdAuthentication);
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(ErrorCode.ENCRYPT_KEY_NO_PERMISSION.getCode(),
            resDeserialize.getErrorCode().intValue());
    }

    /**
     * 对指定PDF模板序列化并解析.
     */
    @Test
    public void testDeserializeCase4() {
        ResponseData<OutputStream> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
                presentation4SpecTpl,
                new ProtocolProperty((EncodeType.ORIGINAL)),
                weIdAuthentication,
                "src/test/resources/test-template.pdf");

        ResponseData<PresentationE> resDeserialize = TransportationFactory
            .newPdfTransportation()
            .deserialize(
                response.getResult(),
                PresentationE.class,
                weIdAuthentication);
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), resDeserialize.getErrorCode().intValue());
        Assert.assertEquals(presentation4SpecTpl.toJson(), resDeserialize.getResult().toJson());
    }


    /**
     * 输入流数据为空.
     */
    @Test
    public void testDeserializeCase5() {
        OutputStream out = null;
        ResponseData<PresentationE> resDeserialize = TransportationFactory
            .newPdfTransportation()
            .deserialize(out, PresentationE.class, weIdAuthentication);
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR.getCode(),
            resDeserialize.getErrorCode().intValue());
        Assert.assertNull(resDeserialize.getResult());
    }

    /**
     * 输入流数据非法.
     */
    @Test
    public void testDeserializeCase6() {
        OutputStream out = new ByteArrayOutputStream();
        ResponseData<PresentationE> resDeserialize = TransportationFactory
            .newPdfTransportation()
            .deserialize(out, PresentationE.class, weIdAuthentication);
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR.getCode(),
            resDeserialize.getErrorCode().intValue());
        Assert.assertEquals(null, resDeserialize.getResult());
    }


    /**
     * credentialPojo测试.
     */
    @Test
    public void testDeserializeCase7() {
        List<CredentialPojo> credentialPojoList = presentation.getVerifiableCredential();
        CredentialPojo credentialPojo = new CredentialPojo();
        if (credentialPojoList.size() > 0) {
            credentialPojo = credentialPojoList.get(0);
        }

        ResponseData<OutputStream> response =
                TransportationFactory.newPdfTransportation().serialize(
                        credentialPojo,
                        new ProtocolProperty(EncodeType.CIPHER),
                        weIdAuthentication
                );
        ResponseData<PresentationE> resDeserialize = TransportationFactory.newPdfTransportation()
                .deserialize(
                        response.getResult(),
                        PresentationE.class,
                        weIdAuthentication);
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(ErrorCode.ENCRYPT_KEY_NO_PERMISSION.getCode(),
                resDeserialize.getErrorCode().intValue());
    }
}
