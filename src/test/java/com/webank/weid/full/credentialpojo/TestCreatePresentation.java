/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.full.credentialpojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.Challenge;
import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.DateUtils;

public class TestCreatePresentation extends TestBaseService {

    private static final Logger logger =
        LoggerFactory.getLogger(TestCreatePresentation.class);

    private static CredentialPojo credentialPojo = null;

    private static CredentialPojo credentialPojoNew = null;

    private static List<CredentialPojo> credentialList = new ArrayList<>();

    private static PresentationPolicyE presentationPolicyE =
        PresentationPolicyE.create("policy.json");

    private static Challenge challenge = null;


    @Override
    public synchronized void testInit() {
        super.testInit();

        if (credentialPojo == null) {
            credentialPojo = super.createCredentialPojo(createCredentialPojoArgs);
        }
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

    }

    /**
     * case:create Presentation success.
     */
    @Test
    public void testCreatePresentation_success() {

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case: credential list have two credentialPojo.
     */
    @Test
    public void testCreatePresentation_credentialListsSuccess() {

        credentialList.add(credentialPojoNew);

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        credentialList.remove(credentialPojoNew);
    }

    /**
     * case: credential list have two credentialPojo.
     */
    @Test
    public void testCreatePresentation_credentialListRepeatSuccess() {

        CredentialPojo credentialPojoCopy = copyCredentialPojo(credentialPojo);
        credentialList.add(credentialPojoCopy);

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        credentialList.remove(credentialPojoCopy);
    }

    /**
     * case: credentiallist.size==0 .
     */
    @Test
    public void testCreatePresentation_credentialListEmpty() {

        credentialList.clear();

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case: credentiallist is null.
     */
    @Test
    public void testCreatePresentation_credentialListNull() {

        credentialList.clear();

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            null,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case:presentationPolicyE is null.
     */
    @Test
    public void testCreatePresentation_presentationPolicyENull() {

        ResponseData<PresentationE> response
            = credentialPojoService.createPresentation(
            credentialList,
            null,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.PRESENTATION_POLICY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case:presentationPolicyE policy is blank.
     */
    @Test
    public void testCreatePresentation_policyNull() {

        credentialList.clear();
        credentialList.add(credentialPojoNew);
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        ClaimPolicy claimPolicy
            = presentationPolicyE.getPolicy().get(copyCredentialPojo.getCptId());
        claimPolicy.setFieldsToBeDisclosed("");
        credentialList.add(copyCredentialPojo);

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);
        credentialList.remove(credentialPojoNew);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_DISCLOSURE_DATA_TYPE_ILLEGAL.getCode(),
            response.getErrorCode().intValue()
        );
        Assert.assertNull(response.getResult());

    }

    /**
     * case:presentationPolicyE policy is different with credentialPojo.claim
     */

    @Test
    public void testCreatePresentation_policyDifferentWithClaim() {

        credentialList.clear();
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        ClaimPolicy claimPolicy
            = presentationPolicyE.getPolicy().get(copyCredentialPojo.getCptId());
        claimPolicy.setFieldsToBeDisclosed(
            "{\"name\":1,\"gender\":0,\"age\":1,\"id\":1,\"yy\":1}");
        credentialList.add(copyCredentialPojo);
        credentialList.add(credentialPojoNew);

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);
        credentialList.remove(credentialPojoNew);

        Assert.assertEquals(ErrorCode.CREDENTIAL_POLICY_FORMAT_DOSE_NOT_MATCH_CLAIM.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());

    }

    /**
     * case:presentationPolicyE policy is different with credentialPojo.claim
     */

    @Test
    public void testCreatePresentation_policyDisclosedId() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> claimMap = copyCredentialPojo.getClaim();
        String weId = (String) claimMap.get("id");
        claimMap.replace("id", weId,
            "did:weid:101:0x39e5e6f663ef77409144014ceb063713b6123456");
        ClaimPolicy claimPolicy
            = presentationPolicyE.getPolicy().get(copyCredentialPojo.getCptId());
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");
        credentialList.clear();
        credentialList.add(copyCredentialPojo);
        credentialList.add(credentialPojoNew);

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);
        credentialList.remove(credentialPojoNew);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());


    }

    /**
     * case:presentationPolicyE policy is different with credentialPojo.claim
     */

    @Test
    public void testCreatePresentation_policyClosedId() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> claimMap = copyCredentialPojo.getClaim();
        String weId = (String) claimMap.get("id");
        claimMap.replace("id", weId,
            "did:weid:101:0x39e5e6f663ef77409144014ceb063713b6123456");

        ClaimPolicy claimPolicy
            = presentationPolicyE.getPolicy().get(copyCredentialPojo.getCptId());
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":0}");
        credentialList.clear();
        credentialList.add(copyCredentialPojo);
        credentialList.add(credentialPojoNew);

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);
        credentialList.remove(credentialPojoNew);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case:presentationPolicyE policy is different with credentialPojo.claim.
     */

    @Test
    public void testCreatePresentation_credetialListHasDupilcate() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> claimMap = copyCredentialPojo.getClaim();
        String weId = (String) claimMap.get("id");
        claimMap.replace("id", weId,
            "did:weid:101:0x39e5e6f663ef77409144014ceb063713b6123456");

        ClaimPolicy claimPolicy
            = presentationPolicyE.getPolicy().get(copyCredentialPojo.getCptId());
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":0}");
        credentialList.clear();
        credentialList.add(copyCredentialPojo);
        credentialList.add(credentialPojo);

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);
        credentialList.remove(credentialPojo);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case:credentialPojo.cptid is different with presentationPolicyE.cptId.
     */
    @Test
    public void testCreatePresentation_OtherCptId() {

        Map<Integer, ClaimPolicy> policyMap = presentationPolicyE.getPolicy();
        ClaimPolicy cliamPolicy = policyMap.get(credentialPojo.getCptId());
        policyMap.remove(credentialPojo.getCptId());
        policyMap.put(credentialPojoNew.getCptId(), cliamPolicy);

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.PRESENTATION_CREDENTIALLIST_MISMATCH_CLAIM_POLICY.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());

        policyMap.remove(credentialPojoNew.getCptId());
        policyMap.put(credentialPojo.getCptId(), cliamPolicy);
    }

    /**
     * case:credentialPojo.issuer is different with PresentationPolicyE.policyPublisherWeId.
     */
    @Test
    public void testCreatePresentation_issuerAndPolicyPublisherDifferent() {

        final String policyPublisherWeId = presentationPolicyE.getPolicyPublisherWeId();
        presentationPolicyE.setPolicyPublisherWeId(credentialPojoNew.getIssuer());

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        presentationPolicyE.setPolicyPublisherWeId(policyPublisherWeId);
    }

    /**
     * case:credentialPojo.issuer is different with PresentationPolicyE.policyPublisherWeId.
     */
    @Test
    public void testCreatePresentation_issuerNotExist() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        String weId = createWeId().getWeId();
        weId = weId.replace(weId.substring(weId.length() - 4, weId.length()),
            DateUtils.getNoMillisecondTimeStampString());
        copyCredentialPojo.setIssuer(weId);
        credentialList.clear();
        credentialList.add(copyCredentialPojo);
        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case:credentialPojo.issuer is different with PresentationPolicyE.policyPublisherWeId.
     */
    @Test
    public void testCreatePresentation_issuerNull() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setIssuer(null);
        credentialList.clear();
        credentialList.add(copyCredentialPojo);

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case:PresentationPolicyE.policyPublisherWeId is not exist.
     */
    @Test
    public void testCreatePresentation_publisherIssuerNotExist() {

        final String policyPublisherWeId = presentationPolicyE.getPolicyPublisherWeId();
        String weId = createWeId().getWeId();
        weId = weId.replace(weId.substring(weId.length() - 4, weId.length()), "ffff");
        presentationPolicyE.setPolicyPublisherWeId(weId);
        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);
        presentationPolicyE.setPolicyPublisherWeId(policyPublisherWeId);

        Assert.assertEquals(ErrorCode.PRESENTATION_POLICY_PUBLISHER_WEID_NOT_EXIST.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());

    }

    /**
     * case:PresentationPolicyE.policyPublisherWeId is null.
     */
    @Test
    public void testCreatePresentation_publisherIssuerNull() {

        final String policyPublisherWeId = presentationPolicyE.getPolicyPublisherWeId();
        presentationPolicyE.setPolicyPublisherWeId(null);

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.PRESENTATION_POLICY_PUBLISHER_WEID_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());

        presentationPolicyE.setPolicyPublisherWeId(policyPublisherWeId);
    }

    /**
     * case:challenge.weid is not exist.
     */
    @Test
    public void testCreatePresentation_challengeWeIdNotExist() {

        final String challengeWeId = challenge.getWeId();
        challenge.setWeId("did:weid:101:0x39e5e6f663ef77409144014ceb063713b6123456");

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.PRESENTATION_CHALLENGE_WEID_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());

        challenge.setWeId(challengeWeId);
    }

    /**
     * case:issuer and challenge.weid are different.
     */
    @Test
    public void testCreatePresentation_challengeWeIdDifferentIssuer() {

        final String challengeWeId = challenge.getWeId();
        challenge.setWeId(credentialPojoNew.getIssuer());

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.PRESENTATION_CHALLENGE_WEID_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());

        challenge.setWeId(challengeWeId);
    }

    /**
     * case:challenge.weid is null.
     */
    @Test
    public void testCreatePresentation_challengeWeIdNull() {

        final String challengeWeId = challenge.getWeId();
        challenge.setWeId(null);

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        challenge.setWeId(challengeWeId);
    }

    /**
     * case:challenge.weid is null.
     */
    @Test
    public void testCreatePresentation_WeIdAuthenticationPrivateKeyNotMatch() {

        WeIdAuthentication weIdAuthentication = TestBaseUtil
            .buildWeIdAuthentication(createWeIdResultWithSetAttr);
        weIdAuthentication.setWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            weIdAuthentication
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case:credentialPojo.claim put("claim",claim) should success.
     */
    @Test
    public void testCreatePresentation_ClaimInClaim() {

        credentialList.clear();
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> originalClaim = copyCredentialPojo.getClaim();
        if (originalClaim != null) {
            Map<String, Object> claim = DataToolUtils
                .deserialize(DataToolUtils.serialize(originalClaim), HashMap.class);
            originalClaim.put("claim", claim);
        }
        credentialList.add(copyCredentialPojo);

        ClaimPolicy claimPolicy = presentationPolicyE
            .getPolicy().get(copyCredentialPojo.getCptId());
        final String policy = claimPolicy.getFieldsToBeDisclosed();
        claimPolicy
            .setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1,\"claim\":1}");

        ResponseData<PresentationE> response = credentialPojoService.createPresentation(
            credentialList,
            presentationPolicyE,
            challenge,
            TestBaseUtil.buildWeIdAuthentication(createWeIdResultWithSetAttr)
        );
        LogUtil.info(logger, "TestCreatePresentation", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_POLICY_FORMAT_DOSE_NOT_MATCH_CLAIM.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());

        claimPolicy.setFieldsToBeDisclosed(policy);
    }


}
