package com.webank.weid.full.transportation;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.CredentialPojoList;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.transportation.TransportationFactory;
import com.webank.weid.suite.api.transportation.params.EncodeType;
import com.webank.weid.suite.api.transportation.params.ProtocolProperty;

/**
 * test serialize class.
 */
public class TestPdfTransportation extends TestBaseTransportation {

    private static final String PRESENTATION_PDF = "presentationFromPDF";
    private static final Logger logger = LoggerFactory.getLogger(TestPdfTransportation.class);
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
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serialize(
                presentation,
                new ProtocolProperty(EncodeType.ORIGINAL)
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
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation4MlCpt, new ProtocolProperty(EncodeType.ORIGINAL));
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }


    /**
     * 多CPT测试.
     */
    @Test
    public void testSerializeCase3() {
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation4MultiCpt, new ProtocolProperty(EncodeType.ORIGINAL));
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * 多CPT，指定已存在目录，不指定文件名，生成文件测试.
     */
    public void testSerializeCase31() {
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation4MultiCpt, new ProtocolProperty(EncodeType.ORIGINAL), "./");
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }


    /**
     * 多CPT，指定已存在目录,指定文件名，生成文件测试.
     */
    public void testSerializeCase32() {
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serialize(
                presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "./out.pdf");
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT测试，指定不存在目录,不指定文件名，生成文件测试.
     */
    public void testSerializeCase33() {
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "./out");
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT，指定已存在多层目录,不指定文件名，生成文件测试.
     */
    public void testSerializeCase34() {
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "./test");
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT测试，指定已存在多层目录,指定文件名，生成文件测试.
     */
    public void testSerializeCase35() {
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "./test/out.pdf");
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT测试，指定不存在多层目录,不指定文件名，生成文件测试.
     */
    public void testSerializeCase36() {
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "./test/test/test/test/out");
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT测试，指定已存在包含多个"."的路径，生成文件测试.
     */
    public void testSerializeCase37() {
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "./test/test.test.test");
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT测试，指定已存在包含多个"."的路径和文件名，生成文件测试.
     */
    public void testSerializeCase38() {
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "./test/te.te.te/a.b.c.pdf");
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT测试，指定不存在包含多个"."的路径和文件名，生成文件测试.
     */
    public void testSerializeCase39() {
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "./test/test.test.test/a.b.c.pdf");
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * 多CPT测试，指定输出目录为空.
     */
    public void testSerializeCase310() {
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "");
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(!response.getResult());
    }


    /**
     * 使用密文方式构建协议数据.
     */
    @Test
    public void testSerializeCase4() {
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .specify(verifier)
            .serialize(
                presentation,
                new ProtocolProperty(EncodeType.CIPHER));
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * 传入协议配置编解码方式为null.
     */
    @Test
    public void testSerializeCase5() {
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation,
                new ProtocolProperty(null));
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
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation, null);
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
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation, new ProtocolProperty(EncodeType.ORIGINAL));
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PROTOCOL_DATA_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * 指定PDF模板测试.
     */
    @Test
    public void testSerializeCase9() {
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serializeWithTemplate(
                presentation4SpecTpl,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "src/test/resources/test-template.pdf");
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
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serializeWithTemplate(
                presentation4SpecTpl,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "src/test/resources/test-template.pdf",
                "./");
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
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serializeWithTemplate(
                presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "src/test/resources/test-template-complex.pdf");
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
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serializeWithTemplate(
                presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "src/test/resources/test-template-complex.pdf",
                "./test-template-complex-out.pdf");
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
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serializeWithTemplate(
                presentation4SpecTpl,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "");
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
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serializeWithTemplate(
                presentation4SpecTpl,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "illegal");
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
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serializeWithTemplate(
                presentation4SpecTpl,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "src/test/resources/test-template-complex.pdf");
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
    @Ignore
    public void testSerializeWithTemplate_credentialList() {
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serializeWithTemplate(
                getCredentialPojoList(presentation4MultiCpt),
                new ProtocolProperty(EncodeType.ORIGINAL),
                "src/test/resources/test-template-complex.pdf",
                "./");
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
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serialize(
                getCredentialPojoList(presentation4MultiCpt),
                new ProtocolProperty(EncodeType.ORIGINAL));
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    //deserialize测试用例

    /**
     * 使用原文方式构建协议数据并解析.
     */
    @Test
    public void testDeserializeCase1() {
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation, new ProtocolProperty((EncodeType.ORIGINAL)));

        ResponseData<PresentationE> resDeserialize = TransportationFactory.newPdfTransportation()
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
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .specify(verifier)
            .serialize(presentation4MlCpt, new ProtocolProperty((EncodeType.CIPHER)));

        ResponseData<PresentationE> resDeserialize = TransportationFactory.newPdfTransportation()
            .specify(verifier)
            .deserialize(response.getResult(), PresentationE.class, weIdAuthentication);
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), resDeserialize.getErrorCode().intValue());
        Assert.assertEquals(presentation4MlCpt.toJson(), resDeserialize.getResult().toJson());
    }

    /**
     * 未设置verifier导致的无权限获取密钥数据.
     */
    @Test
    @Ignore
    public void testDeserializeCase3() {
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation4MlCpt, new ProtocolProperty((EncodeType.CIPHER)));

        ResponseData<PresentationE> resDeserialize = TransportationFactory.newPdfTransportation()
            .deserialize(response.getResult(), PresentationE.class, weIdAuthentication);
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_NO_SPECIFYER_TO_SET.getCode(),
            resDeserialize.getErrorCode().intValue());
    }

    /**
     * 对指定PDF模板序列化并解析.
     */
    @Test
    public void testDeserializeCase4() {
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serializeWithTemplate(
                presentation4SpecTpl,
                new ProtocolProperty((EncodeType.ORIGINAL)),
                "src/test/resources/test-template.pdf");

        ResponseData<PresentationE> resDeserialize = TransportationFactory.newPdfTransportation()
            .deserialize(response.getResult(), PresentationE.class, weIdAuthentication);

        PresentationE presentationE = new PresentationE();

        if (resDeserialize.getErrorCode() == ErrorCode.SUCCESS.getCode()) {
            presentationE = resDeserialize.getResult();
        }
        if (presentationE.getType().contains(PRESENTATION_PDF)) {
            List<String> typeList = presentationE.getType();
            typeList.remove(PRESENTATION_PDF);
            presentationE.setType(typeList);
        }

        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), resDeserialize.getErrorCode().intValue());
        Assert.assertEquals(presentation4SpecTpl.toJson(), presentationE.toJson());
    }


    /**
     * 输入流数据为空.
     */
    @Test
    public void testDeserializeCase5() {
        byte[] out = null;
        ResponseData<PresentationE> resDeserialize = TransportationFactory.newPdfTransportation()
            .deserialize(out, PresentationE.class, weIdAuthentication);
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR.getCode(),
            resDeserialize.getErrorCode().intValue());
        Assert.assertNull(resDeserialize.getResult());
    }

    /**
     * 输入流数据非法.
     */
    @Test
    public void testDeserializeCase6() {
        byte[] out = new byte[0];
        ResponseData<PresentationE> resDeserialize = TransportationFactory.newPdfTransportation()
            .deserialize(out, PresentationE.class, weIdAuthentication);
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(
            ErrorCode.TRANSPORTATION_PDF_TRANSFER_ERROR.getCode(),
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
        List<String> verifier = new ArrayList<String>();
        verifier.add(createWeIdNew.getWeId());
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .specify(verifier)
            .serialize(credentialPojo, new ProtocolProperty(EncodeType.CIPHER));
        ResponseData<PresentationE> resDeserialize = TransportationFactory.newPdfTransportation()
            .deserialize(
                response.getResult(),
                PresentationE.class,
                weIdAuthentication);
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(
            ErrorCode.ENCRYPT_KEY_NO_PERMISSION.getCode(),
            resDeserialize.getErrorCode().intValue());
    }

    /**
     * 读入默认模板PDF文件反序列化.
     */
    public void testDeserializeCase8() {

        //1. 序列化presentation4MultiCpt为pdf文件
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serialize(
                presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "./out.pdf");
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());

        byte[] bytesArray = getFileByte("./out.pdf");

        //3. 对读取到byte[]做反序列化
        ResponseData<PresentationE> resDeserialize = TransportationFactory.newPdfTransportation()
            .deserialize(bytesArray, PresentationE.class, weIdAuthentication);
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), resDeserialize.getErrorCode().intValue());
        Assert.assertEquals(presentation4MultiCpt.toJson(), resDeserialize.getResult().toJson());
    }

    private byte[] getFileByte(String filePath) {
        //2. 从pdf文件读取到byte[]
        File file = new File(filePath);
        byte[] bytesArray = new byte[(int) file.length()];
        try {

            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytesArray;
    }

    /**
     * 读入指定模板PDF文件反序列化.
     */
    public void testDeserializeCase9() {

        //1. 序列化presentation4MultiCpt为pdf文件
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "./test-template-complex-out.pdf");
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());

        //2. 从pdf文件读取到byte[]
        byte[] bytesArray = getFileByte("./test-template-complex-out.pdf");

        //3. 对读取到byte[]做反序列化
        ResponseData<PresentationE> resDeserialize = TransportationFactory.newPdfTransportation()
            .deserialize(bytesArray, PresentationE.class, weIdAuthentication);
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), resDeserialize.getErrorCode().intValue());
        Assert.assertEquals(presentation4MultiCpt.toJson(), resDeserialize.getResult().toJson());
    }

    /**
     * 使用原文方式构建协议数据并解析.(无模板)
     */
    @Test
    public void testDeserialize_credentialList() {
        CredentialPojoList credentialPojoList = getCredentialPojoList(presentation4MultiCpt);
        ResponseData<byte[]> response = TransportationFactory.newPdfTransportation()
            .serialize(credentialPojoList, new ProtocolProperty(EncodeType.ORIGINAL));

        ResponseData<CredentialPojoList> resDeserialize = TransportationFactory
            .newPdfTransportation()
            .deserialize(response.getResult(), CredentialPojoList.class, weIdAuthentication);
        System.out.println(resDeserialize.getResult().toJson());
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), resDeserialize.getErrorCode().intValue());
        Assert.assertEquals(credentialPojoList.toJson(), resDeserialize.getResult().toJson());
    }

    /**
     * 使用原文方式构建协议数据并解析.(有模板)
     */
    @Test
    public void testDeserializeWithTemplet_credentialList() {
        CredentialPojoList credentialPojoList = getCredentialPojoList(presentation4MultiCpt);
        ResponseData<Boolean> response = TransportationFactory.newPdfTransportation()
            .serializeWithTemplate(
                credentialPojoList,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "src/test/resources/test-template-complex.pdf",
                "./out1.pdf");
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());

        byte[] bytesArray = getFileByte("./out1.pdf");
        ResponseData<CredentialPojoList> resDeserialize = TransportationFactory
            .newPdfTransportation()
            .deserialize(bytesArray, CredentialPojoList.class, weIdAuthentication);

        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), resDeserialize.getErrorCode().intValue());
        Assert.assertEquals(credentialPojoList.toJson(), resDeserialize.getResult().toJson());
    }

}
