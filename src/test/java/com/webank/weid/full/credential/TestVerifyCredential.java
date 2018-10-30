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
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.RegisterCptArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import org.junit.Assert;
import org.junit.Test;

public class TestVerifyCredential extends TestBaseServcie {

    CreateWeIdDataResult createWeId;
    RegisterCptArgs registerCptArgs;
    CreateCredentialArgs createCredentialArgs;
    /**
     * is register issuer
     */
    private boolean isRegisterAuthorityIssuer = true;

    @Test
    /** case: verifyCredential success */
    public void testVerifyCredentialCase1() throws Exception {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    @Test
    /** case: context is null */
    public void testVerifyCredentialCase2() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setContext(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CONTEXT_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: context is other string */
    public void testVerifyCredentialCase3() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setContext("xxx");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: cptId is null */
    public void testVerifyCredentialCase4() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setCptId(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: cptId is minus number */
    public void testVerifyCredentialCase5() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setCptId(-1);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: cptId is not exists */
    public void testVerifyCredentialCase6() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setCptId(10000);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: cptId is another */
    public void testVerifyCredentialCase7() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);

        CptBaseInfo cpt = super.registerCpt(createWeId, registerCptArgs, false);
        credential.setCptId(cpt.getCptId());

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: id is null */
    public void testVerifyCredentialCase8() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setId(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ID_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: id is another */
    public void testVerifyCredentialCase9() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setId("xxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: issuer is null */
    public void testVerifyCredentialCase10() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setIssuer(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: issuer is xxxxx */
    public void testVerifyCredentialCase11() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setIssuer("xxxxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: issuer is another */
    public void testVerifyCredentialCase12() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);

        CreateWeIdDataResult createWeIdNew = super.createWeId();
        credential.setIssuer(createWeIdNew.getWeId());

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: issuranceDate < = 0 */
    public void testVerifyCredentialCase13() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setIssuranceDate(-1l);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: issuranceDate > now */
    public void testVerifyCredentialCase14() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setIssuranceDate(System.currentTimeMillis() + 100000);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: expirationDate <= 0 */
    public void testVerifyCredentialCase15() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setExpirationDate(-1l);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: expirationDate <= now */
    public void testVerifyCredentialCase16() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setExpirationDate(System.currentTimeMillis() - 10000);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: expirationDate is null */
    public void testVerifyCredentialCase17() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setExpirationDate(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: issuranceDate is null */
    public void testVerifyCredentialCase18() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setIssuranceDate(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CREATE_DATE_ILLEGAL.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: claim is null */
    public void testVerifyCredentialCase19() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setClaim(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: claim is xxxxxxxxxx */
    public void testVerifyCredentialCase20() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setClaim("xxxxxxxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert
            .assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: claim does not match jsonSchema */
    public void testVerifyCredentialCase21() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setClaim(TestData.schemaDataInvalid);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: signature is null */
    public void testVerifyCredentialCase22() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setSignature(null);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: signature is xxxxxxxxxxxxxxxxxxx */
    public void testVerifyCredentialCase23() {

        preVerify();

        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);
        credential.setSignature("xxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert
            .assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: signature by 122324324324 */
    public void testVerifyCredentialCase24() throws Exception {

        preVerify();

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey("122324324324");
        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: signature by non weIdentity DId publickeys */
    public void testVerifyCredentialCase25() throws Exception {

        preVerify();

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(TestBaseUtil.createEcKeyPair()[1]);
        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /** case: Sing through another private key in publickeys of weIdentity DId */
    public void testVerifyCredentialCase26() throws Exception {

        preVerify();

        String[] pk = TestBaseUtil.createEcKeyPair();
        super.setPublicKey(createWeId, pk[0], createWeId.getWeId());
        super.setAuthentication(createWeId, pk[0], createWeId.getWeId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(pk[1]);
        Credential credential =
            super.createCredential(
                createWeId, registerCptArgs, createCredentialArgs, isRegisterAuthorityIssuer);

        ResponseData<Boolean> response = super.verifyCredential(credential);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    private void preVerify() {

        createWeId = super.createWeIdWithSetAttr();

        registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);

        createCredentialArgs = TestBaseUtil.buildCreateCredentialArgs(createWeId);
    }
}
