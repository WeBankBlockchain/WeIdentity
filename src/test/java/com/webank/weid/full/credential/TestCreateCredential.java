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
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import org.junit.Assert;
import org.junit.Test;

public class TestCreateCredential extends TestBaseServcie {

    /**
     * is register issuer
     */
    private boolean isRegisterAuthorityIssuer = false;

    @Test
    /** case：createCredential success */
    public void testCreateCredentialCase1() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    @Test
    /** case: createCredentialArgs is null */
    public void testCreateCredentialCase2() {

        ResponseData<Credential> response = credentialService.createCredential(null);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert
            .assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case：cptId is null */
    public void testCreateCredentialCase3() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);
        createCredentialArgs.setCptId(null);

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptId is minus number */
    public void testCreateCredentialCase4() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);
        createCredentialArgs.setCptId(-1);

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    @Test
    /** case： cptId is not exists */
    public void testCreateCredentialCase5() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);
        createCredentialArgs.setCptId(100000);

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    @Test
    /** case： cptId is belongs to others weIdentity DId */
    public void testCreateCredentialCase6() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);

        CreateWeIdDataResult createWeIdNew = super.createWeIdWithSetAttr();
        CptBaseInfo cptBaseInfoNew = super.registerCpt(createWeIdNew, true);
        createCredentialArgs.setCptId(cptBaseInfoNew.getCptId());

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    @Test
    /** case： issuer is null */
    public void testCreateCredentialCase7() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);
        createCredentialArgs.setIssuer(null);

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： issuer is invalid */
    public void testCreateCredentialCase8() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);
        createCredentialArgs.setIssuer("di:weid:0x1111111111");

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： issuer is not exists */
    public void testCreateCredentialCase9() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);
        createCredentialArgs.setIssuer("did:weid:0x1111111111");

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    @Test
    /** case： expirationDate <= 0 */
    public void testCreateCredentialCase10() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);
        createCredentialArgs.setExpirationDate(0L);

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： expirationDate <= now */
    public void testCreateCredentialCase11() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);
        createCredentialArgs.setExpirationDate(System.currentTimeMillis() - 1000000);

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    @Test
    /** case： claim is null */
    public void testCreateCredentialCase12() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);
        createCredentialArgs.setClaim(null);

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： claim is xxxxxxx */
    public void testCreateCredentialCase13() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);
        createCredentialArgs.setClaim("xxxxxxxxxxxxxx");

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    @Test
    /** case： weIdPrivateKey is null */
    public void testCreateCredentialCase14() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);
        createCredentialArgs.setWeIdPrivateKey(null);

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert
            .assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： privateKey is null */
    public void testCreateCredentialCase15() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);
        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： privateKey is xxxxxxxxxxx */
    public void testCreateCredentialCase16() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);
        createCredentialArgs.getWeIdPrivateKey().setPrivateKey("xxxxxxxxxx");

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert
            .assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： privateKey is 11111111111111 */
    public void testCreateCredentialCase17() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeId, cptBaseInfo);
        createCredentialArgs.getWeIdPrivateKey().setPrivateKey("11111111111111");

        ResponseData<Credential> response = credentialService
            .createCredential(createCredentialArgs);
        System.out.println("\ncreateCredential result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }
}
