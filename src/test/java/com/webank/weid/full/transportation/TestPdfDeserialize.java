package com.webank.weid.full.transportation;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.junit.Assert;
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
        mockMysqlDriver();
        if (presentation == null) {
            super.testInit();
            super.testInit4MlCpt();
            super.testInit4MultiCpt();
            super.testInitSpecTplCpt();
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
        ResponseData<byte[]> response = TransportationFactory
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
        ResponseData<byte[]> response = TransportationFactory
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
        ResponseData<byte[]> response = TransportationFactory
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
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation()
            .serializeWithTemplate(
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
        byte[] out = null;
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
        byte[] out = new byte[0];
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

        ResponseData<byte[]> response =
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

    /**
     * 读入默认模板PDF文件反序列化.
     */
    public void testDeserializeCase8() {

        //1. 序列化presentation4MultiCpt为pdf文件
        ResponseData<Boolean> response = TransportationFactory
            .newPdfTransportation()
            .serialize(presentation4MultiCpt,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "./out.pdf");
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());

        byte[] bytesArray = getFileByte("./out.pdf");

        //3. 对读取到byte[]做反序列化
        ResponseData<PresentationE> resDeserialize = TransportationFactory
            .newPdfTransportation()
            .deserialize(
                bytesArray,
                PresentationE.class,
                weIdAuthentication);
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
        ResponseData<Boolean> response = TransportationFactory
                .newPdfTransportation()
                .serialize(presentation4MultiCpt,
                        new ProtocolProperty(EncodeType.ORIGINAL),
                        weIdAuthentication,
                        "./test-template-complex-out.pdf");
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());

        //2. 从pdf文件读取到byte[]
        byte[] bytesArray = getFileByte("./test-template-complex-out.pdf");

        //3. 对读取到byte[]做反序列化
        ResponseData<PresentationE> resDeserialize = TransportationFactory
                .newPdfTransportation()
                .deserialize(
                        bytesArray,
                        PresentationE.class,
                        weIdAuthentication);
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
        ResponseData<byte[]> response = TransportationFactory
            .newPdfTransportation()
            .serialize(
                credentialPojoList,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication);

        ResponseData<CredentialPojoList> resDeserialize = TransportationFactory
            .newPdfTransportation()
            .deserialize(
                response.getResult(),
                CredentialPojoList.class,
                weIdAuthentication);
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
        ResponseData<Boolean> response = TransportationFactory
            .newPdfTransportation()
            .serializeWithTemplate(
                credentialPojoList,
                new ProtocolProperty(EncodeType.ORIGINAL),
                weIdAuthentication,
                "src/test/resources/test-template-complex.pdf",
                "./out1.pdf"
             );
        LogUtil.info(logger, "serialize", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
        
        byte[] bytesArray = getFileByte("./out1.pdf");
        ResponseData<CredentialPojoList> resDeserialize = TransportationFactory
            .newPdfTransportation()
            .deserialize(
                bytesArray,
                CredentialPojoList.class,
                weIdAuthentication);
        LogUtil.info(logger, "deserialize", resDeserialize);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), resDeserialize.getErrorCode().intValue());
        Assert.assertEquals(credentialPojoList.toJson(), resDeserialize.getResult().toJson());
    }
}
