

package com.webank.weid.full.credential;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.webank.weid.constant.CredentialConstant.CredentialProofType;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.blockchain.protocol.response.ResponseData;

public class TestGetCredentialHash extends TestBaseService {

    @Test
    public void testGetCredentialHash_success() {
        Credential credential = buildCredential();
        ResponseData<String> response1 = credentialService.getCredentialHash(credential);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertNotNull(response1.getResult());

        credential = buildCredential();
        credential.setIssuer("xxxxxxxxx");
        response1 = credentialService.getCredentialHash(credential);
        Assert.assertNotEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response1.getResult());
    }

    private Credential buildCredential() {
        Credential credential = new Credential();
        HashMap<String, Object> claim = new HashMap<>();
        claim.put("id", "xxxxxxxxxxxxxx");
        credential.setClaim(claim);
        credential.setContext("v1");
        credential.setCptId(Integer.valueOf(1001));
        credential.setExpirationDate(System.currentTimeMillis() + 1000L);
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
