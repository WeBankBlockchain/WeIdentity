/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.full.TestData;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.WeIdServiceImpl;
import com.webank.weid.util.CredentialUtils;
import mockit.Mock;
import mockit.MockUp;
import org.junit.Assert;
import org.junit.Test;

/**
 * verifyCredential method for testing CredentialService.
 * 
 * @author v_wbgyang
 *
 */
public class TestVerifyCredential extends TestBaseServcie {

    private String[] pk = null;

    @Override
    public void testInit() throws Exception {

        super.testInit();
        pk = TestBaseUtil.createEcKeyPair();
        if (createCredentialArgs == null) {
            registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeIdWithSetAttr);
            createCredentialArgs = TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr);
            cptBaseInfo = this.registerCpt(createWeIdWithSetAttr, registerCptArgs);
            createCredentialArgs.setCptId(cptBaseInfo.getCptId());
        }
    }

    /** 
     * case: verifyCredential success.
     */
    @Test
    public void testVerifyCredentialCase1() throws Exception {

        Credential credential = super.createCredential(createCredentialArgs);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /** 
     * case: context is null.
     */
    @Test
    public void testVerifyCredentialCase2() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setContext(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CONTEXT_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: context is other string.
     */
    @Test
    public void testVerifyCredentialCase3() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setContext("xxx");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: cptId is null.
     */
    @Test
    public void testVerifyCredentialCase4() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setCptId(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: cptId is minus number.
     */
    @Test
    public void testVerifyCredentialCase5() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setCptId(-1);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: cptId is not exists.
     */
    @Test
    public void testVerifyCredentialCase6() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setCptId(10000);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: cptId is another.
     */
    @Test
    public void testVerifyCredentialCase7() {

        Credential credential = super.createCredential(createCredentialArgs);

        CptBaseInfo cpt = super.registerCpt(createWeIdWithSetAttr, registerCptArgs);
        credential.setCptId(cpt.getCptId());

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: id is null.
     */
    @Test
    public void testVerifyCredentialCase8() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setId(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ID_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: id is another.
     */
    @Test
    public void testVerifyCredentialCase9() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setId("xxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: issuer is null.
     */
    @Test
    public void testVerifyCredentialCase10() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setIssuer(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: issuer is xxxxx.
     */
    @Test
    public void testVerifyCredentialCase11() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setIssuer("xxxxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: issuer is another.
     */
    @Test
    public void testVerifyCredentialCase12() {

        Credential credential = super.createCredential(createCredentialArgs);

        credential.setIssuer(createWeIdNew.getWeId());

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: issuranceDate < = 0.
     */
    @Test
    public void testVerifyCredentialCase13() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setIssuranceDate(-1L);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: issuranceDate > now.
     */
    @Test
    public void testVerifyCredentialCase14() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setIssuranceDate(System.currentTimeMillis() + 100000);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: expirationDate <= 0.
     */
    @Test
    public void testVerifyCredentialCase15() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setExpirationDate(-1L);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: expirationDate <= now.
     */
    @Test
    public void testVerifyCredentialCase16() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setExpirationDate(System.currentTimeMillis() - 10000);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: expirationDate is null.
     */
    @Test
    public void testVerifyCredentialCase17() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setExpirationDate(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: issuranceDate is null.
     */
    @Test
    public void testVerifyCredentialCase18() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setIssuranceDate(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CREATE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: claim is null.
     */
    @Test
    public void testVerifyCredentialCase19() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setClaim(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: claim is xxxxxxxxxx.
     */
    @Test
    public void testVerifyCredentialCase20() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setClaim("xxxxxxxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: claim does not match jsonSchema.
     */
    @Test
    public void testVerifyCredentialCase21() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setClaim(TestData.schemaDataInvalid);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: signature is null.
     */
    @Test
    public void testVerifyCredentialCase22() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setSignature(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: signature is xxxxxxxxxxxxxxxxxxx.
     */
    @Test
    public void testVerifyCredentialCase23() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setSignature("xxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: signature by 122324324324.
     */
    @Test
    public void testVerifyCredentialCase24() throws Exception {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey("122324324324");

        Credential credential = super.createCredential(createCredentialArgs);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: signature by non weIdentity DId publickeys.
     */
    @Test
    public void testVerifyCredentialCase25() throws Exception {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(pk[1]);
        Credential credential = super.createCredential(createCredentialArgs);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: Sing through another private key in publickeys of weIdentity dId.
     */
    @Test
    public void testVerifyCredentialCase26() throws Exception {

        super.setPublicKey(createWeIdWithSetAttr, pk[0], createWeIdWithSetAttr.getWeId());
        super.setAuthentication(createWeIdWithSetAttr, pk[0], createWeIdWithSetAttr.getWeId());

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(pk[1]);
        Credential credential = super.createCredential(createCredentialArgs);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /** 
     * case: issuranceDate < now && expirationDate < now  && issuranceDate < expirationDate.
     */
    @Test
    public void testVerifyCredentialCase27() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setIssuranceDate(System.currentTimeMillis() - 12000);
        credential.setExpirationDate(System.currentTimeMillis() - 10000);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: mock CREDENTIAL_WEID_DOCUMENT_ILLEGAL.
     */
    @Test
    public void testVerifyCredentialCase28() {

        Credential credential = super.createCredential(createCredentialArgs);

        MockUp<WeIdServiceImpl> mockTest = new MockUp<WeIdServiceImpl>() {
            @Mock
            public ResponseData<WeIdDocument> getWeIdDocument(String weId) throws Exception {
                ResponseData<WeIdDocument> response = new ResponseData<WeIdDocument>();
                response.setErrorCode(ErrorCode.CREDENTIAL_WEID_DOCUMENT_ILLEGAL.getCode());
                return response;
            }
        };

        ResponseData<Boolean> response = super.verifyCredential(credential);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.CREDENTIAL_WEID_DOCUMENT_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: mock NullPointerException.
     */
    @Test
    public void testVerifyCredentialCase29() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setIssuranceDate(System.currentTimeMillis() - 12000);
        credential.setExpirationDate(System.currentTimeMillis() - 10000);

        MockUp<CredentialUtils> mockTest = new MockUp<CredentialUtils>() {
            @Mock
            public CreateCredentialArgs extractCredentialMetadata(Credential arg) throws Exception {
                throw new NullPointerException();
            }
        };

        ResponseData<Boolean> response = super.verifyCredential(credential);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: issuer is not exists.
     */
    @Test
    public void testVerifyCredentialCase30() {

        Credential credential = super.createCredential(createCredentialArgs);
        credential.setIssuer("did:weid:0x111111111111111");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
