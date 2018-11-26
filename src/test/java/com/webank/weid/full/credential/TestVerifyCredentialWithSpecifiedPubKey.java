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

import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.VerifyCredentialArgs;
import com.webank.weid.protocol.response.ResponseData;
import org.junit.Assert;
import org.junit.Test;

/**
 * verifyCredentialWithSpecifiedPubKey method for testing CredentialService.
 * 
 * @author v_wbgyang
 *
 */
public class TestVerifyCredentialWithSpecifiedPubKey extends TestBaseServcie {

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
     * case: verifyCredentialWithSpecifiedPubKey success.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase1() throws Exception {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(pk[1]);

        Credential credential = super.createCredential(createCredentialArgs);

        VerifyCredentialArgs verifyCredentialArgs =
            TestBaseUtil.buildVerifyCredentialArgs(credential, pk[0]);

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(verifyCredentialArgs);
        System.out.println("\nverifyCredentialWithSpecifiedPubKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /** 
     * case: verifyCredentialWithSpecifiedPubKey fail.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase2() throws Exception {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(pk[1]);

        Credential credential = super.createCredential(createCredentialArgs);

        String[] pkNew = TestBaseUtil.createEcKeyPair();

        VerifyCredentialArgs verifyCredentialArgs =
            TestBaseUtil.buildVerifyCredentialArgs(credential, pkNew[0]);

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(verifyCredentialArgs);
        System.out.println("\nverifyCredentialWithSpecifiedPubKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: verifyCredentialWithSpecifiedPubKey publicKey is null.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase3() throws Exception {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(pk[1]);

        Credential credential = super.createCredential(createCredentialArgs);

        VerifyCredentialArgs verifyCredentialArgs =
            TestBaseUtil.buildVerifyCredentialArgs(credential, null);

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(verifyCredentialArgs);
        System.out.println("\nverifyCredentialWithSpecifiedPubKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: verifyCredentialArgs is null.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase4() throws Exception {

        VerifyCredentialArgs verifyCredentialArgs = null;

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(verifyCredentialArgs);
        System.out.println("\nverifyCredentialWithSpecifiedPubKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: credential is null.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase5() throws Exception {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(pk[1]);

        Credential credential = super.createCredential(createCredentialArgs);

        VerifyCredentialArgs verifyCredentialArgs =
            TestBaseUtil.buildVerifyCredentialArgs(credential, pk[0]);
        verifyCredentialArgs.setCredential(null);

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(verifyCredentialArgs);
        System.out.println("\nverifyCredentialWithSpecifiedPubKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: weIdPublicKey is null.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase6() throws Exception {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(pk[1]);

        Credential credential = super.createCredential(createCredentialArgs);

        VerifyCredentialArgs verifyCredentialArgs =
            TestBaseUtil.buildVerifyCredentialArgs(credential, pk[0]);
        verifyCredentialArgs.setWeIdPublicKey(null);

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(verifyCredentialArgs);
        System.out.println("\nverifyCredentialWithSpecifiedPubKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /** 
     * case: verifyCredentialWithSpecifiedPubKey publicKey is "xxxxxxxxxxxxx".
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase7() throws Exception {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(pk[1]);

        Credential credential = super.createCredential(createCredentialArgs);

        VerifyCredentialArgs verifyCredentialArgs =
            TestBaseUtil.buildVerifyCredentialArgs(credential, "xxxxxxxxxxxxx");

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(verifyCredentialArgs);
        System.out.println("\nverifyCredentialWithSpecifiedPubKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
