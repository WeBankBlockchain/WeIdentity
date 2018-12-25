/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.full.credential;

import java.security.SignatureException;
import java.util.Map;

import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.crypto.Sign;
import org.junit.Assert;
import org.junit.Test;

import com.webank.weid.common.PasswordKey;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.CredentialWrapper;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.WeIdServiceImpl;
import com.webank.weid.util.SignatureUtils;

/**
 * verifyCredential method for testing CredentialService.
 * 
 * @author v_wbgyang
 */
public class TestVerifyCredential extends TestBaseServcie {

    protected PasswordKey passwordKey = null;

    private static CredentialWrapper credentialWrapper = null;

    private static Credential credential = null;

    @Override
    public void testInit() {
        super.testInit();
        passwordKey = TestBaseUtil.createEcKeyPair();

        credentialWrapper = super.createCredential(createCredentialArgs);
        credential = credentialWrapper.getCredential();
    }

    /**
     * case: verifyCredential success.
     */
    @Test
    public void testVerifyCredentialCase1() {

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: context is null.
     */
    @Test
    public void testVerifyCredentialCase2() {

        String context = credential.getContext();
        credential.setContext(null);

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setContext(context);
        Assert.assertEquals(ErrorCode.CREDENTIAL_CONTEXT_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: context is other string.
     */
    @Test
    public void testVerifyCredentialCase3() {

        String context = credential.getContext();
        credential.setContext("xxx");

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setContext(context);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: cptId is another.
     */
    @Test
    public void testVerifyCredentialCase7() {

        Integer cptId = credential.getCptId();
        CptBaseInfo cpt = super.registerCpt(createWeIdResultWithSetAttr, registerCptArgs);
        credential.setCptId(cpt.getCptId());

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setCptId(cptId);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: id is null.
     */
    @Test
    public void testVerifyCredentialCase8() {

        String id = credential.getId();
        credential.setId(null);

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setId(id);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ID_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: id is another.
     */
    @Test
    public void testVerifyCredentialCase9() {

        String id = credential.getId();
        credential.setId("xxxxxxxxx");
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setId(id);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ID_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuer is null.
     */
    @Test
    public void testVerifyCredentialCase10() {

        String issuer = credential.getIssuer();
        credential.setIssuer(null);

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setIssuer(issuer);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuer is xxxxx.
     */
    @Test
    public void testVerifyCredentialCase11() {

        String issuer = credential.getIssuer();
        credential.setIssuer("xxxxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setIssuer(issuer);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuer is another.
     */
    @Test
    public void testVerifyCredentialCase12() {

        String issuer = credential.getIssuer();
        credential.setIssuer(createWeIdNew.getWeId());

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setIssuer(issuer);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuranceDate < = 0.
     */
    @Test
    public void testVerifyCredentialCase13() {

        Long issuranceDate = credential.getIssuranceDate();
        credential.setIssuranceDate(-1L);

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setIssuranceDate(issuranceDate);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuranceDate > now.
     */
    @Test
    public void testVerifyCredentialCase14() {

        Long issuranceDate = credential.getIssuranceDate();
        credential.setIssuranceDate(System.currentTimeMillis() + 100000);

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setIssuranceDate(issuranceDate);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: expirationDate <= 0.
     */
    @Test
    public void testVerifyCredentialCase15() {

        Long expirationDate = credential.getExpirationDate();
        credential.setExpirationDate(-1L);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setExpirationDate(expirationDate);
        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: expirationDate <= now.
     */
    @Test
    public void testVerifyCredentialCase16() {

        Long expirationDate = credential.getExpirationDate();
        credential.setExpirationDate(System.currentTimeMillis() - 10000);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setExpirationDate(expirationDate);
        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: expirationDate is null.
     */
    @Test
    public void testVerifyCredentialCase17() {

        Long expirationDate = credential.getExpirationDate();
        credential.setExpirationDate(null);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setExpirationDate(expirationDate);
        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: issuranceDate is null.
     */
    @Test
    public void testVerifyCredentialCase18() {

        Long issuranceDate = credential.getIssuranceDate();
        credential.setIssuranceDate(null);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setIssuranceDate(issuranceDate);
        Assert.assertEquals(ErrorCode.CREDENTIAL_CREATE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: claim is null.
     */
    @Test
    public void testVerifyCredentialCase19() {

        Map<String, Object> claim = credential.getClaim();
        credential.setClaim(null);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setClaim(claim);
        Assert.assertEquals(ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: claim is xxxxxxxxxx.
     */
    @Test
    public void testVerifyCredentialCase20() {

        MockUp<SignatureUtils> mockTest = new MockUp<SignatureUtils>() {
            @Mock
            public Sign.SignatureData simpleSignatureDeserialization(
                byte[] serializedSignatureData) throws SignatureException {
                throw new SignatureException();
            }
        };
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXCEPTION_VERIFYSIGNATURE.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }


    /**
     * case: signature is null.
     */
    @Test
    public void testVerifyCredentialCase22() {

        String signature = credential.getSignature();
        credential.setSignature(null);

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setSignature(signature);
        Assert.assertEquals(ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: signature is xxxxxxxxxxxxxxxxxxx.
     */
    @Test
    public void testVerifyCredentialCase23() {

        String signature = credential.getSignature();
        credential.setSignature("xxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setSignature(signature);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: signature by 122324324324.
     */
    @Test
    public void testVerifyCredentialCase24() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey("122324324324");

        CredentialWrapper credentialWrapper = super.createCredential(createCredentialArgs);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }


    /**
     * case: Sing through another private key in publickeys of WeIdentity DID.
     */
    @Test
    public void testVerifyCredentialCase26() {

        super.setPublicKey(
            createWeIdResultWithSetAttr,
            passwordKey.getPublicKey(),
            createWeIdResultWithSetAttr.getWeId());
        super.setAuthentication(
            createWeIdResultWithSetAttr,
            passwordKey.getPublicKey(),
            createWeIdResultWithSetAttr.getWeId());

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(passwordKey.getPrivateKey());
        CredentialWrapper credentialWrapper = super.createCredential(createCredentialArgs);
        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: issuranceDate < now && expirationDate < now  && issuranceDate < expirationDate.
     */
    @Test
    public void testVerifyCredentialCase27() {

        Long issuranceDate = credential.getIssuranceDate();
        Long expirationDate = credential.getExpirationDate();
        credential.setIssuranceDate(System.currentTimeMillis() - 12000);
        credential.setExpirationDate(System.currentTimeMillis() - 10000);

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setIssuranceDate(issuranceDate);
        credential.setExpirationDate(expirationDate);
        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: mock CREDENTIAL_WEID_DOCUMENT_ILLEGAL.
     */
    @Test
    public void testVerifyCredentialCase28() {

        MockUp<WeIdServiceImpl> mockTest = new MockUp<WeIdServiceImpl>() {
            @Mock
            public ResponseData<WeIdDocument> getWeIdDocument(String weId) {
                ResponseData<WeIdDocument> response = new ResponseData<WeIdDocument>();
                response.setErrorCode(ErrorCode.CREDENTIAL_WEID_DOCUMENT_ILLEGAL.getCode());
                return response;
            }
        };

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.CREDENTIAL_WEID_DOCUMENT_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: issuer is not exists.
     */
    @Test
    public void testVerifyCredentialCase30() {

        String issuer = credential.getIssuer();
        credential.setIssuer("did:weid:0x111111111111111");

        ResponseData<Boolean> response = super.verifyCredential(credentialWrapper);
        credential.setIssuer(issuer);
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
