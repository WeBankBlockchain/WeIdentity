package com.webank.weid.full.credentialpojo;

import com.webank.weid.common.LogUtil;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.full.persistence.TestBaseTransportation;
import com.webank.weid.protocol.base.*;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestVerifyCredentialWithPresentation extends TestBaseTransportation {

    private static final Logger logger =
        LoggerFactory.getLogger(TestCreatePresentation.class);

    private static CredentialPojo credentialPojoNew = null;

    private static List<CredentialPojo> credentialList = new ArrayList<>();

    private static PresentationPolicyE presentationPolicyE
        = PresentationPolicyE.create("policy.json");

    private static Challenge challenge = null;

    private static PresentationE presentationE = null;

    //test for pdf presentation verify
    private static CredentialPojo credentialPojoNew1 = null;

    private static List<CredentialPojo> credentialList1 = new ArrayList<>();

    private static PresentationPolicyE presentationPolicyE1
            = PresentationPolicyE.create("test-spectpl-policy.json");

    private static Challenge challenge1 = null;

    private static PresentationE presentationE1 = null;

    @Override
    public synchronized void testInit() {

        super.testInit();

        if (credentialPojoNew == null) {
            credentialPojoNew = super.createCredentialPojo(createCredentialPojoArgsNew);
        }
        if (presentationPolicyE != null) {
            presentationPolicyE = PresentationPolicyE.create("policy.json");
            presentationPolicyE.setPolicyPublisherWeId(createWeIdResultWithSetAttr.getWeId());
            Map<Integer, ClaimPolicy> policyMap = presentationPolicyE.getPolicy();
            ClaimPolicy cliamPolicy = policyMap.get(1000);
            policyMap.remove(1000);
            policyMap.put(createCredentialPojoArgs.getCptId(), cliamPolicy);
        }
        if (challenge == null) {
            challenge = Challenge.create(
                createWeIdResultWithSetAttr.getWeId(),
                String.valueOf(System.currentTimeMillis()));
        }

        if (credentialList == null || credentialList.size() == 0) {
            credentialList.add(credentialPojo);
        }

        if (presentationE == null) {
            ResponseData<PresentationE> response = credentialPojoService.createPresentation(
                credentialList,
                presentationPolicyE,
                challenge,
                TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
            );
            Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
            presentationE = response.getResult();
        }

        //init for pdf presentation verify
        super.testInitSpecTplCpt();

        if (credentialPojoNew1 == null) {
            credentialPojoNew1 = super.createCredentialPojo(createCredentialPojoArgs4);
        }
        if (presentationPolicyE1 != null) {
            presentationPolicyE1 = PresentationPolicyE.create("test-spectpl-policy.json");
            presentationPolicyE1.setPolicyPublisherWeId(createWeIdResultWithSetAttr.getWeId());
            Map<Integer, ClaimPolicy> policyMap1 = presentationPolicyE1.getPolicy();
            ClaimPolicy cliamPolicy1 = policyMap1.get(1005);
            policyMap1.remove(1005);
            policyMap1.put(createCredentialPojoArgs4.getCptId(), cliamPolicy1);
        }
        if (challenge1 == null) {
            challenge1 = Challenge.create(
                createWeIdResultWithSetAttr.getWeId(),
                String.valueOf(System.currentTimeMillis()));
        }

        if (credentialList1 == null || credentialList1.size() == 0) {
            credentialList1.add(credentialPojoNew1);
        }
        if (presentationE1 == null) {
            ResponseData<PresentationE> response1 = credentialPojoService.createPresentation(
                credentialList1,
                presentationPolicyE1,
                challenge1,
                TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
            );

            Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
            presentationE1 = response1.getResult();
        }


    }

    /**
     * verify credential pojo with presention successs.
     */
    @Test
    public void testVerfiyCredential_suceess() {

        ResponseData<Boolean> response = credentialPojoService.verify(
            credentialPojo.getIssuer(),
            presentationPolicyE,
            challenge,
            presentationE);
        LogUtil.info(logger, "testVerfiyCredentialWithPresention", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
    }

    /**
     * verify presentation from pdf transportation.
     */
    /*@Test
    @Ignore
    public void testVerifyPdfPresentation_fail() {

        //序列化PDF，生成包含PDF信息的byte[]
        ResponseData<byte[]> retSerialize = TransportationFactory.newPdfTransportation()
            .serializeWithTemplate(
                presentationE1,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "src/test/resources/test-template.pdf");
        LogUtil.info(logger, "serialize", retSerialize);

        //反序列化PDF数组为PresentationE
        ResponseData<PresentationE> retDeserialize = TransportationFactory.newPdfTransportation()
            .deserialize(retSerialize.getResult(), PresentationE.class, weIdAuthentication);
        LogUtil.info(logger, "deserialize", retDeserialize);

        ResponseData<Boolean> response = credentialPojoService.verify(
            credentialPojoNew1.getIssuer(),
            presentationPolicyE1,
            challenge1,
            retDeserialize.getResult());
        LogUtil.info(logger, "testVerfiyCredentialWithPresention", response);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_USE_VERIFY_FUNCTION_ERROR.getCode(),
            response.getErrorCode().intValue());
    }*/

    /**
     * verify presentation from pdf transportation.
     */
    /*@Test
    @Ignore
    public void testVerifyPdfPresentation_success() {
        PdfTransportationImpl pdfTransportation = new PdfTransportationImpl();

        //序列化PDF，生成包含PDF信息的byte[]
        ResponseData<byte[]> retSerialize = TransportationFactory.newPdfTransportation()
            .serializeWithTemplate(
                presentationE1,
                new ProtocolProperty(EncodeType.ORIGINAL),
                "src/test/resources/test-template.pdf");
        LogUtil.info(logger, "serialize", retSerialize);

        //反序列化PDF数组为PresentationE
        ResponseData<PresentationE> retDeserialize = TransportationFactory.newPdfTransportation()
            .deserialize(
                retSerialize.getResult(),
                PresentationE.class,
                weIdAuthentication);
        LogUtil.info(logger, "deserialize", retDeserialize);

        ResponseData<Boolean> response = credentialPojoService.verifyPresentationFromPdf(
            "src/test/resources/test-template.pdf",
            retSerialize.getResult(),
            credentialPojoNew1.getIssuer(),
            presentationPolicyE1,
            challenge1,
            retDeserialize.getResult());
        LogUtil.info(logger, "testVerfiyCredentialWithPresention", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
    }*/
}
