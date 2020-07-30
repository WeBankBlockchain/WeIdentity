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

import java.util.Map;

import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.common.PasswordKey;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.WeIdServiceImpl;

/**
 * verifyCredential method for testing CredentialService.
 *
 * @author v_wbgyang
 */
public class TestVerifyCredentialByPublicKey extends TestBaseService {

    private static final Logger logger =
        LoggerFactory.getLogger(TestVerifyCredentialByPublicKey.class);

    protected PasswordKey passwordKey = null;

    private static CredentialPojo credentialPojo = null;

    private static CredentialPojo credentialPojoNew = null;

    private static WeIdPublicKey weIdPublicKey = null;

    private static CredentialPojo selectiveCredentialPojo = null;


    @Override
    public synchronized void testInit() {
        super.testInit();
        passwordKey = TestBaseUtil.createEcKeyPair();
        if (credentialPojo == null) {
            credentialPojo = super.createCredentialPojo(createCredentialPojoArgs);
        }
        if (credentialPojoNew == null) {
            credentialPojoNew = super.createCredentialPojo(createCredentialPojoArgs);
        }
        if (selectiveCredentialPojo == null) {
            selectiveCredentialPojo = super.createSelectiveCredentialPojo(credentialPojo);
        }
        if (weIdPublicKey == null) {
            weIdPublicKey = createWeIdResultWithSetAttr.getUserWeIdPublicKey();
        }

    }


    /**
     * case: verify CredentialPojo success by public key .
     */
    @Test
    public void testVerifyCredentialByPubKey_success() {

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey, credentialPojo);
        LogUtil.info(logger, "testVerifyCredentialByPubKey", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: verify selective CredentialPojo success by public key .
     */
    @Test
    public void testVerifyCredentialByPubKey_selectiveCredentialSuccess() {

        ResponseData<Boolean> response = super
            .verifyCredentialPojo(weIdPublicKey, selectiveCredentialPojo);
        LogUtil.info(logger, "testVerifyCredentialByPubKey", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }


    /**
     * case: context is null.
     */
    @Test
    public void testVerifyCredentialByPubKey_contextNull() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setContext(null);
        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "testVerifyCredentialByPubKey", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CONTEXT_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: context is other string.
     */
    @Test
    public void testVerifyCredential_contentError() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(selectiveCredentialPojo);
        copyCredentialPojo.setContext("xxx");

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "testVerifyCredentialByPubKey", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_VERIFY_FAIL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: cptId is another with **same** schema.
     */
    @Test
    public void testVerifyCredential_otherCptId() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        CptBaseInfo cpt = super.registerCpt(createWeIdResultWithSetAttr, registerCptArgs);
        copyCredentialPojo.setCptId(cpt.getCptId());

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "testVerifyCredentialByPubKey", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_VERIFY_FAIL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: id is null.
     */
    @Test
    public void testVerifyCredential_idNull() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(selectiveCredentialPojo);
        copyCredentialPojo.setId(null);

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "testVerifyCredentialByPubKey", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ID_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: id is not exist.
     */
    @Test
    public void testVerifyCredential_idNotExist() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setId("xxxxx");
        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "testVerifyCredentialByPubKey", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ID_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: id is other.
     */
    @Test
    public void testVerifyCredential_OtherId() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setId(credentialPojoNew.getId());
        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "testVerifyCredentialByPubKey", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_VERIFY_FAIL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuer is null.
     */
    @Test
    public void testVerifyCredential_issuerNull() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setIssuer(null);

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuer is xxxxx.
     */
    @Test
    public void testVerifyCredential_invalidIsuuer() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(selectiveCredentialPojo);
        copyCredentialPojo.setIssuer("xxxxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: credentialPojo.issuer is another.
     */
    @Test
    public void testVerifyCredential_credentialPojoIssuerOther() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(selectiveCredentialPojo);
        copyCredentialPojo.setIssuer(createWeIdNew.getWeId());

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_VERIFY_FAIL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: publickey and credentialPojo.issuer.publickey is different .
     */
    @Test
    public void testVerifyCredential_otherIssuer() {

        WeIdPublicKey otherPublicKey = createWeIdNew.getUserWeIdPublicKey();

        ResponseData<Boolean> response = credentialPojoService.verify(otherPublicKey,
            credentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_VERIFY_FAIL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: public key is  not exist .
     */
    @Test
    public void testVerifyCredential_pubKeyNull() {

        ResponseData<Boolean> response = credentialPojoService.verify(new WeIdPublicKey(),
            credentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_PUBLIC_KEY_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: IssuanceDate < = 0.
     */
    @Test
    public void testVerifyCredential_issuanceDateMinus() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setIssuanceDate(-1L);

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: IssuanceDate > now.
     */
    @Test
    public void testVerifyCredential_issuanceDateFuture() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setIssuanceDate(System.currentTimeMillis() + 100000);

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: expirationDate <= 0.
     */
    @Test
    public void testVerifyCredential_minusExpirationDate() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setExpirationDate(-1L);
        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: expirationDate <= now.
     */
    @Test
    public void testVerifyCredential_expirationDatePassed() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setExpirationDate(credentialPojo.getIssuanceDate() - 100000000);
        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: expirationDate is null.
     */
    @Test
    public void testVerifyCredential_expirationDateNull() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setExpirationDate(null);
        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: IssuanceDate is null.
     */
    @Test
    public void testVerifyCredential_issuanceDateNull() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setIssuanceDate(null);
        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            credentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: claim is null. test error,Please add check.
     */
    @Test
    public void testVerifyCredential_claimNull() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setClaim(null);
        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: claim is null. test error,.
     */
    @Test
    public void testVerifyCredential_selectiveCredentialClaimNull() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(selectiveCredentialPojo);
        copyCredentialPojo.setClaim(null);
        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: signature is empty.
     */
    @Test
    public void testVerifyCredential_emptySignature() {
        CredentialPojo newCredential = copyCredentialPojo(credentialPojo);
        Map<String, Object> proof = newCredential.getProof();
        proof.put(ParamKeyConstant.PROOF_SIGNATURE, StringUtils.EMPTY);
        newCredential.setProof(proof);

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            newCredential);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: signature is null.
     */
    @Test
    public void testVerifyCredential_signatureNull() {
        CredentialPojo newCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> proof = newCredentialPojo.getProof();
        proof.put(ParamKeyConstant.PROOF_SIGNATURE, null);
        newCredentialPojo.setProof(proof);

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            newCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: signature is xxxxxxxxxxxxxxxxxxx.
     */
    @Test
    public void testVerifyCredential_invalidSignature() {

        CredentialPojo newCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> proof = newCredentialPojo.getProof();
        proof.put(ParamKeyConstant.PROOF_SIGNATURE, "xxxxxxxxxxx");
        newCredentialPojo.setProof(proof);

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            newCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: signature by 122324324324.
     */
    @Test
    public void testVerifyCredential_signatureRnadom() {

        CredentialPojo newCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> proof = newCredentialPojo.getProof();
        proof.put(ParamKeyConstant.PROOF_SIGNATURE, "12341234");
        newCredentialPojo.setProof(proof);

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            newCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: IssuanceDate < now && expirationDate < now  && IssuanceDate < expirationDate.
     */
    @Test
    public void testVerifyCredentialCase27() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setIssuanceDate(System.currentTimeMillis() - 12000);
        copyCredentialPojo.setExpirationDate(System.currentTimeMillis() - 10000);
        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);

        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: mock CREDENTIAL_WEID_DOCUMENT_ILLEGAL.
     */
    @Test
    public void testVerifyCredentialCase28() {

        new MockUp<WeIdServiceImpl>() {
            @Mock
            public ResponseData<WeIdDocument> getWeIdDocument(String weId) {
                ResponseData<WeIdDocument> response = new ResponseData<WeIdDocument>();
                response.setErrorCode(ErrorCode.CREDENTIAL_WEID_DOCUMENT_ILLEGAL);
                return response;
            }
        };

        ResponseData<Boolean> response = super.verifyCredentialPojo(credentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_WEID_DOCUMENT_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: proof creator and created are null - this is OK.
     */
    @Test
    public void testVerifyCredential_proofCreatorNull() {

        CredentialPojo newCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> proof = newCredentialPojo.getProof();
        proof.put(ParamKeyConstant.PROOF_CREATOR, null);
        newCredentialPojo.setProof(proof);

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            newCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuer is not exists.
     */
    @Test
    public void testVerifyCredential_invalidIssuer() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        String weId = createWeId().getWeId();
        weId = weId.replace(weId.substring(weId.length() - 4, weId.length()), "ffff");
        copyCredentialPojo.setIssuer(weId);

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            copyCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_VERIFY_FAIL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: proof type illegal.
     */
    @Test
    public void testVerifyCredentialCase31() {

        CredentialPojo newCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> proof = newCredentialPojo.getProof();
        proof.put(ParamKeyConstant.PROOF_TYPE, null);
        newCredentialPojo.setProof(proof);

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            newCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_SIGNATURE_TYPE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: proof is missing signature key at all - fail.
     */
    @Test
    public void testVerifyCredential_removeProofCredentialSignature() {

        CredentialPojo newCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> proof = newCredentialPojo.getProof();
        proof.remove(ParamKeyConstant.PROOF_SIGNATURE);
        newCredentialPojo.setProof(proof);

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            newCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: proof is missing creator key at all - should be fine.
     */
    @Test
    public void testVerifyCredentialCase33() {

        CredentialPojo newCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> proof = newCredentialPojo.getProof();
        proof.remove(ParamKeyConstant.PROOF_CREATOR);
        newCredentialPojo.setProof(proof);

        ResponseData<Boolean> response = super.verifyCredentialPojo(weIdPublicKey,
            newCredentialPojo);
        LogUtil.info(logger, "verifyCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
