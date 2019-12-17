package com.webank.weid.full.transportation;

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

/**
 * test serialize class.
 */
public class TestPdfSerialize extends TestBaseTransportation {

    private static final Logger logger = LoggerFactory.getLogger(TestPdfSerialize.class);
    private static PresentationE presentation;
    private static PresentationE presentation4MlCpt;
    private static PresentationE presentation4MultiCpt;
    private static PresentationE presentation4SpecTpl;

    @Override
    public synchronized void testInit() {
        super.testInit();
        super.testInit4MlCpt();
        super.testInit4MultiCpt();
        super.testInitSpecTplCpt();

        presentation = getPresentationE();
        presentation4MlCpt = getPresentationE4MlCpt();
        presentation4MultiCpt = getPresentationE4MultiCpt();
        presentation4SpecTpl = getPresentationE4SpecTplCpt();
    }


    /**
     * 单级CPT测试.
     */
    @Test
    public void testSerializeCase1() {
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
                presentation,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }


    /**
     * 多级CPT测试.
     */
    @Test
    public void testSerializeCase2() {
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
                presentation4MlCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }


    /**
     * 多CPT测试.
     */
    @Test
    public void testSerializeCase3() {
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * 多CPT，指定已存在目录，不指定文件名，生成文件测试.
     */
    public void testSerializeCase31() {
        ResponseData<Boolean> response = TransportationFactory
            .newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "./"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }


    /**
     * 多CPT，指定已存在目录,指定文件名，生成文件测试.
     */
    public void testSerializeCase32() {
        ResponseData<Boolean> response = TransportationFactory
            .newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "./out.pdf"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT测试，指定不存在目录,不指定文件名，生成文件测试.
     */
    public void testSerializeCase33() {
        ResponseData<Boolean> response = TransportationFactory
            .newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "./out"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT，指定已存在多层目录,不指定文件名，生成文件测试.
     */
    public void testSerializeCase34() {
        ResponseData<Boolean> response = TransportationFactory
            .newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "./test"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT测试，指定已存在多层目录,指定文件名，生成文件测试.
     */
    public void testSerializeCase35() {
        ResponseData<Boolean> response = TransportationFactory
            .newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "./test/out.pdf"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT测试，指定不存在多层目录,不指定文件名，生成文件测试.
     */
    public void testSerializeCase36() {
        ResponseData<Boolean> response = TransportationFactory
            .newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "./test/test/test/test/out"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT测试，指定已存在包含多个"."的路径，生成文件测试.
     */
    public void testSerializeCase37() {
        ResponseData<Boolean> response = TransportationFactory
            .newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "./test/test.test.test"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT测试，指定已存在包含多个"."的路径和文件名，生成文件测试.
     */
    public void testSerializeCase38() {
        ResponseData<Boolean> response = TransportationFactory
            .newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "./test/te.te.te/a.b.c.pdf"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT测试，指定不存在包含多个"."的路径和文件名，生成文件测试.
     */
    public void testSerializeCase39() {
        ResponseData<Boolean> response = TransportationFactory
            .newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "./test/test.test.test/a.b.c.pdf"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT测试，指定输出目录为空.
     */
    public void testSerializeCase310() {
        ResponseData<Boolean> response = TransportationFactory
            .newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                ""
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(!response.getResult());
    }


    /**
     * 使用密文方式构建协议数据.
     */
    @Test
    public void testSerializeCase4() {
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation().specify(verifier)
            .serialize(
                presentation,
                new ProtocolProperty(EncodeType.CIPHER),
                weIdAuthentication
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * 传入协议配置编解码方式为null.
     */
    @Test
    public void testSerializeCase5() {
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation()
            .serialize(presentation,
                new ProtocolProperty(null),
                weIdAuthentication
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_ENCODE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }


    /**
     * 传入的协议配置为null.
     */
    @Test
    public void testSerializeCase6() {
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
                presentation,
                null,
                weIdAuthentication
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_PROPERTY_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * 传入presentation为null.
     */
    @Test
    public void testSerializeCase7() {
        PresentationE presentation = null;
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
                presentation,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_DATA_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * 传入weIdAuthentication为null.
     */
    @Test
    public void testSerializeCase8() {
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
                presentation,
                new ProtocolProperty(EncodeType.ORIGINAL),
                null
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.WEID_AUTHORITY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * 指定PDF模板测试.
     */
    @Test
    public void testSerializeCase9() {
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation()
            .serializeWithTemplate(
                presentation4SpecTpl,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "src/test/resources/test-template.pdf"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * 指定PDF模板测试.
     */
    public void testSerializeCase91() {
        ResponseData<Boolean> response = TransportationFactory
            .newPdfTransportation()
            .serializeWithTemplate(
                presentation4SpecTpl,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "src/test/resources/test-template.pdf",
                "./"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
                ErrorCode.SUCCESS.getCode(),
                response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 指定复杂PDF模板测试.
     */
    @Test
    public void testSerializeCase10() {
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation()
            .serializeWithTemplate(
                presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "src/test/resources/test-template-complex.pdf"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
                ErrorCode.SUCCESS.getCode(),
                response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * 指定复杂PDF模板测试.
     */
    public void testSerializeCase101() {
        ResponseData<Boolean> response = TransportationFactory
            .newPdfTransportation()
            .serializeWithTemplate(
                presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "src/test/resources/test-template-complex.pdf",
                "./test-template-complex-out.pdf"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
                ErrorCode.SUCCESS.getCode(),
                response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * 传入指定模板目录为空字符串.
     */
    @Test
    public void testSerializeCase11() {
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation()
            .serializeWithTemplate(
                presentation4SpecTpl,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                ""
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.ILLEGAL_INPUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * 传入指定模板目录为非法字符串.
     */
    @Test
    public void testSerializeCase12() {
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation()
            .serializeWithTemplate(
                presentation4SpecTpl,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "illegal"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * 传入模板与披露信息不匹配.
     */
    @Test
    public void testSerializeCase13() {
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation()
            .serializeWithTemplate(
                presentation4SpecTpl,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "src/test/resources/test-template-complex.pdf"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
                ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR.getCode(),
                response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }
    
    /**
     *将credentialList序列化成pdf(指定模板).
     */
    @Test
    public void testSerializeWithTemplate_credentialList() {
        ResponseData<Boolean> response = TransportationFactory
            .newPdfTransportation()
            .serializeWithTemplate(
                getCredentialPojoList(presentation4MultiCpt),
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "src/test/resources/test-template-complex.pdf",
                "./"
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }
    
    /**
     *将credentialList序列化成pdf(不指定模板).
     */
    @Test
    public void testSerialize_credentialList() {
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
                getCredentialPojoList(presentation4MultiCpt),
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication
            );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }
}
