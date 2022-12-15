

package com.webank.weid.full.credential;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.CredentialConstant.CredentialProofType;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.response.ResponseData;

/**
 * Testing getCredentialJson method.
 *
 * @author chaoxinhu
 */
public class TestGetCredentialJson extends TestBaseService {

    @Test
    public void testGetCredentialJsonCase1() {
        Credential credential = buildCredential();
        ResponseData<String> response1 = credentialService.getCredentialJson(credential);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertNotNull(response1.getResult());
        Assert.assertTrue(
            response1
                .getResult()
                .contains(CredentialConstant.CREDENTIAL_CONTEXT_PORTABLE_JSON_FIELD));
        Assert.assertTrue(
            response1
                .getResult()
                .contains(CredentialConstant.DEFAULT_CREDENTIAL_CONTEXT));

        credential = buildCredential();
        credential.setIssuer("xxxxxxxxx");
        response1 = credentialService.getCredentialHash(credential);
        Assert.assertNotEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response1.getResult());
    }

    @Test
    public void testGetCredentialJsonCase2() {
        ResponseData<String> response1 = credentialService.getCredentialJson(null);
        Assert.assertNotEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response1.getResult());
    }

    private Credential buildCredential() {
        Credential credential = new Credential();
        HashMap<String, Object> claim = new HashMap<>();
        claim.put("id", "xxxxxxxxxxxxx");
        credential.setClaim(claim);
        credential.setContext(CredentialConstant.DEFAULT_CREDENTIAL_CONTEXT);
        credential.setCptId(Integer.valueOf(1002));
        credential.setExpirationDate(System.currentTimeMillis() + 10000L);
        credential.setId(UUID.randomUUID().toString());
        credential.setIssuer("did:weid:0xbb1670306aedfaeb75cff9581c99e56ba4797431");
        credential.setIssuanceDate(System.currentTimeMillis());
        Map<String, String> proof = new HashMap<>();
        proof.put(ParamKeyConstant.PROOF_CREATOR, credential.getIssuer());
        proof.put(ParamKeyConstant.PROOF_CREATED, credential.getIssuanceDate().toString());
        proof.put(ParamKeyConstant.PROOF_TYPE, CredentialProofType.ECDSA.getTypeName());
        proof.put(ParamKeyConstant.CREDENTIAL_SIGNATURE, "xxxxxxxxxxx");
        credential.setProof(proof);
        return credential;
    }
}
