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

package com.webank.weid.full.auth;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import org.junit.Assert;
import org.junit.Test;

public class TestRegisterAuthorityIssuer extends TestBaseServcie {

    @Test
    /**
     * case: weIdentity DId is invalid
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase1() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getAuthorityIssuer().setWeId("xxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: weIdentity DId is bad format
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase2() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs
            .getAuthorityIssuer()
            .setWeId("di:weid:0x29f3fe8d4536966af41392f7ddc756b6f452df02");

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: the weIdentity DId address is not exists
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase3() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs
            .getAuthorityIssuer()
            .setWeId("did:weid:0x29f3" + System.currentTimeMillis());

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: the weIdentity DId is null or "" or " "
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase4() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getAuthorityIssuer().setWeId(null);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: the name is blank
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase5() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getAuthorityIssuer().setName(null);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_NAME_ILLEGAL.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: the created before now ,now or after now
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase6() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getAuthorityIssuer()
            .setCreated(System.currentTimeMillis() + 4000);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    @Test
    /**
     * case: the accValue is null ,
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase7() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getAuthorityIssuer().setAccValue(null);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_ACCVALUE_ILLEAGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: registerAuthorityIssuer success
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase8() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    @Test
    /**
     * case: the weIdentity DId is Registed
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase9() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        ResponseData<Boolean> response1 =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response1);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_ALREADY_EXIST.getCode(),
            response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    @Test
    /**
     * case: registerAuthorityIssuerArgs is null
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase10() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs = null;

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.ILLEGAL_INPUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: authorityIssuer is null
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase11() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.setAuthorityIssuer(null);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.ILLEGAL_INPUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: weIdPrivateKey is null
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase12() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.setWeIdPrivateKey(null);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: privateKey is null
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase13() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: privateKey is invalid
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase14() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey("xxxxxxx");

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: privateKey and private key of weIdentity DId do not match
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase15() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs
            .getWeIdPrivateKey()
            .setPrivateKey(TestBaseUtil.createEcKeyPair()[1]);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NO_PERMISSION.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: privateKey belongs to the private key of other weIdentity DId
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase16() throws Exception {

        CreateWeIdDataResult createWeIdNew = super.createWeId();

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.setWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NO_PERMISSION.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: the private key is the private key of the members of the committee
     *
     * @throws Exception
     */
    public void testRegisterAuthorityIssuerCase17() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getWeIdPrivateKey()
            .setPrivateKey(super.issuerPrivateList.get(1));

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NO_PERMISSION.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
