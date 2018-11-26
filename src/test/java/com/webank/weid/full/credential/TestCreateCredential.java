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
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.response.ResponseData;
import org.junit.Assert;
import org.junit.Test;

/**
 * createCredential method for testing CredentialService.
 * 
 * @author v_wbgyang
 *
 */
public class TestCreateCredential extends TestBaseServcie {

    @Override
    public void testInit() throws Exception {

        super.testInit();
        if (cptBaseInfo == null) {
            cptBaseInfo = super.registerCpt(createWeIdWithSetAttr);
        }
    }

    /** 
     * case：createCredential success. 
     */
    @Test
    public void testCreateCredentialCase1() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /** 
     * case: createCredentialArgs is null.
     */
    @Test
    public void testCreateCredentialCase2() {

        ResponseData<Credential> response = credentialService.createCredential(null);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case：cptId is null.
     */
    @Test
    public void testCreateCredentialCase3() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);
        createCredentialArgs.setCptId(null);

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： cptId is minus number.
     */
    @Test
    public void testCreateCredentialCase4() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);
        createCredentialArgs.setCptId(-1);

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /** 
     * case： cptId is not exists.
     */
    @Test
    public void testCreateCredentialCase5() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);
        createCredentialArgs.setCptId(100000);

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /** 
     * case： cptId is belongs to others weIdentity dId.
     */
    @Test
    public void testCreateCredentialCase6() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);

        CptBaseInfo cptBaseInfoNew = super.registerCpt(createWeIdNew);
        createCredentialArgs.setCptId(cptBaseInfoNew.getCptId());

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /** 
     * case： issuer is null.
     */
    @Test
    public void testCreateCredentialCase7() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);
        createCredentialArgs.setIssuer(null);

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： issuer is invalid.
     */
    @Test
    public void testCreateCredentialCase8() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);
        createCredentialArgs.setIssuer("di:weid:0x1111111111");

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： issuer is not exists.
     */
    @Test
    public void testCreateCredentialCase9() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);
        createCredentialArgs.setIssuer("did:weid:0x1111111111");

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /** 
     * case： expirationDate <= 0.
     */
    @Test
    public void testCreateCredentialCase10() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);
        createCredentialArgs.setExpirationDate(0L);

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： expirationDate <= now.
     */
    @Test
    public void testCreateCredentialCase11() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);
        createCredentialArgs.setExpirationDate(System.currentTimeMillis() - 1000000);

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /** 
     * case： claim is null.
     */
    @Test
    public void testCreateCredentialCase12() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);
        createCredentialArgs.setClaim(null);

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： claim is xxxxxxx.
     */
    @Test
    public void testCreateCredentialCase13() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);
        createCredentialArgs.setClaim("xxxxxxxxxxxxxx");

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /** 
     * case： weIdPrivateKey is null.
     */
    @Test
    public void testCreateCredentialCase14() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);
        createCredentialArgs.setWeIdPrivateKey(null);

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： privateKey is null.
     */
    @Test
    public void testCreateCredentialCase15() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);
        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： privateKey is xxxxxxxxxxx.
     */
    @Test
    public void testCreateCredentialCase16() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);
        createCredentialArgs.getWeIdPrivateKey().setPrivateKey("xxxxxxxxxx");

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： privateKey is 11111111111111.
     */
    @Test
    public void testCreateCredentialCase17() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdWithSetAttr, cptBaseInfo);
        createCredentialArgs.getWeIdPrivateKey().setPrivateKey("11111111111111");

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }
}
