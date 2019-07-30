package com.webank.weid.full.credentialpojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.Challenge;
import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.response.ResponseData;

public class TestVerifyCredentialWithPresentation extends TestBaseServcie {

    private static final Logger logger =
        LoggerFactory.getLogger(TestCreatePresentation.class);

    private static CredentialPojo credentialPojoNew = null;

    private static List<CredentialPojo> credentialList = new ArrayList<>();

    private static PresentationPolicyE presentationPolicyE
        = PresentationPolicyE.create("policy.json");

    private static Challenge challenge = null;

    private static PresentationE presentationE = null;

    private static final String claimDisclosedPolicy
        = "{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}";

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


}
