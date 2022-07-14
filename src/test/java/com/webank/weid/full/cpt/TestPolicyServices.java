

package com.webank.weid.full.cpt;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.full.TestBaseService;
import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.DataToolUtils;

/**
 * Testing policy services related methods.
 *
 * @author chaoxinhu
 */

public class TestPolicyServices extends TestBaseService {

    private static final Logger logger = LoggerFactory.getLogger(TestQueryCpt.class);

    @Override
    public synchronized void testInit() {

        super.testInit();
        if (cptBaseInfo == null) {
            cptBaseInfo = super.registerCpt(createWeIdResultWithSetAttr);
        }
    }

    @Test
    public void happyPathPolicyAll() {
        WeIdAuthentication auth = new WeIdAuthentication();
        CreateWeIdDataResult cwdr = createWeIdWithSetAttr();
        auth.setWeId(cwdr.getWeId());
        auth.setWeIdPublicKeyId(cwdr.getUserWeIdPublicKey().getPublicKey() + "#keys-0");
        auth.setWeIdPrivateKey(cwdr.getUserWeIdPrivateKey());
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":0,\"gender\":0,\"age\":0,\"id\":0}");
        Integer cptId = cptBaseInfo.getCptId();
        ResponseData<Integer> registerResp = policyService
            .registerClaimPolicy(cptId, claimPolicy.getFieldsToBeDisclosed(), auth);
        Assert.assertTrue(registerResp.getResult() > 0);
        registerResp = policyService
            .registerClaimPolicy(cptId, claimPolicy.getFieldsToBeDisclosed(), auth);
        Assert.assertTrue(registerResp.getResult() > 0);
        registerResp = policyService
            .registerClaimPolicy(cptId, claimPolicy.getFieldsToBeDisclosed(), auth);
        Assert.assertTrue(registerResp.getResult() > 0);
        ResponseData<List<Integer>> allPoliciesList = policyService
            .getAllClaimPolicies(0, 3);
        Assert.assertEquals(allPoliciesList.getResult().size(), 3);
        System.out.println(DataToolUtils.serialize(allPoliciesList.getResult()));
        ClaimPolicy claimPolicyFromChain = policyService.getClaimPolicy(registerResp.getResult())
            .getResult();
        Assert.assertFalse(StringUtils.isEmpty(claimPolicyFromChain.getFieldsToBeDisclosed()));
        System.out.println(claimPolicyFromChain.getFieldsToBeDisclosed());
        ResponseData<List<Integer>> getClaimFromCptResp = policyService
            .getClaimPoliciesFromCpt(cptId);
        Assert.assertTrue(getClaimFromCptResp.getResult().size() > 0);
        ResponseData<Integer> presentationResp = policyService
            .registerPresentationPolicy(getClaimFromCptResp.getResult(), auth);
        Assert.assertTrue(presentationResp.getResult() >= 0);
        ResponseData<PresentationPolicyE> getClaimFromPresResp = policyService
            .getPresentationPolicy(presentationResp.getResult());
        Assert.assertNotNull(getClaimFromPresResp.getResult());
        System.out.println(DataToolUtils.serialize(getClaimFromPresResp.getResult()));
    }
}
