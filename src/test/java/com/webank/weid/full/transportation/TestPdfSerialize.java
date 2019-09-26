package com.webank.weid.full.transportation;

import java.io.OutputStream;

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

        mockMysqlDriver();
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
        ResponseData<OutputStream> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
                presentation,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication);
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }


    /**
     * 多级CPT测试.
     */
    @Test
    public void testSerializeCase2() {
        ResponseData<OutputStream> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
                presentation4MlCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication);
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }


    /**
     * 多CPT测试.
     */
    @Test
    public void testSerializeCase3() {
        ResponseData<OutputStream> response = TransportationFactory
            .newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication);
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * 使用密文方式构建协议数据.
     */
    @Test
    public void testSerializeCase4() {
        ResponseData<OutputStream> response = TransportationFactory
            .newPdfTransportation().specify(verifier)
            .serialize(
                presentation,
                new ProtocolProperty(EncodeType.CIPHER),
                weIdAuthentication);
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * 传入协议配置编解码方式为null.
     */
    @Test
    public void testSerializeCase5() {
        ResponseData<OutputStream> response = TransportationFactory
            .newPdfTransportation()
            .serialize(presentation,
                new ProtocolProperty(null),
                weIdAuthentication);
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
        ResponseData<OutputStream> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
                presentation,
                null,
                weIdAuthentication);
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
        ResponseData<OutputStream> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
                presentation,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication);
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
        ResponseData<OutputStream> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
                presentation,
                new ProtocolProperty(EncodeType.ORIGINAL),
                null);
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
        ResponseData<OutputStream> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
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
     * 指定复杂PDF模板测试.
     */
    @Test
    public void testSerializeCase10() {
        ResponseData<OutputStream> response = TransportationFactory
                .newPdfTransportation()
                .serialize(
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
     * 传入指定模板目录为空字符串.
     */
    @Test
    public void testSerializeCase11() {
        ResponseData<OutputStream> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
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
        ResponseData<OutputStream> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
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
        ResponseData<OutputStream> response = TransportationFactory
                .newPdfTransportation()
                .serialize(
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
}
