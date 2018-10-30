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
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import org.junit.Assert;
import org.junit.Test;

public class TestRemoveAuthorityIssuer extends TestBaseServcie {

    @Test
    /**
     * case: weIdentity DId is invalid
     *
     * @throws Exception
     */
    public void testRemoveAuthorityIssuerCase1() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId);
        removeAuthorityIssuerArgs.setWeId("xxxxxxxx");

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("removeAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: weIdentity DId is blank
     *
     * @throws Exception
     */
    public void testRemoveAuthorityIssuerCase2() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId);
        removeAuthorityIssuerArgs.setWeId(null);

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("removeAuthorityIssuer result:");
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
    public void testRemoveAuthorityIssuerCase3() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId);
        removeAuthorityIssuerArgs.setWeId("di:weid:0x29f3fe8d4536966af41392f7ddc756b6f452df09");

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("removeAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: weIdentity DId is not exists
     *
     * @throws Exception
     */
    public void testRemoveAuthorityIssuerCase4() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId);
        removeAuthorityIssuerArgs.setWeId("did:weid:0x21f3fe8d4536966af41392f7ddc756b6f452df09");

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("removeAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: remove success
     *
     * @throws Exception
     */
    public void testRemoveAuthorityIssuerCase5() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId);

        ResponseData<Boolean> response1 =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("\nremoveAuthorityIssuer result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(true, response1.getResult());
    }

    @Test
    /**
     * case: the weIdentity DId is removed
     *
     * @throws Exception
     */
    public void testRemoveAuthorityIssuerCase6() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId);

        ResponseData<Boolean> response1 =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("\nremoveAuthorityIssuer result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(true, response1.getResult());

        response1 = authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("\nremoveAuthorityIssuer result:");
        BeanUtil.print(response1);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode(),
            response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    @Test
    /**
     * case: the weIdentity DId is register by other
     *
     * @throws Exception
     */
    public void testRemoveAuthorityIssuerCase7() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId);
        removeAuthorityIssuerArgs.setWeId("did:weid:0x5f3d8234e93823fac7ebdf0cfaa03b6a43d8773b");

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("removeAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: removeAuthorityIssuerArgs is null
     *
     * @throws Exception
     */
    public void testRemoveAuthorityIssuerCase8() throws Exception {

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs = null;

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("removeAuthorityIssuer result:");
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
    public void testRemoveAuthorityIssuerCase9() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId);
        removeAuthorityIssuerArgs.setWeIdPrivateKey(null);

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("removeAuthorityIssuer result:");
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
    public void testRemoveAuthorityIssuerCase10() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId);
        removeAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("removeAuthorityIssuer result:");
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
    public void testRemoveAuthorityIssuerCase11() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId);
        removeAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey("xxxxxxx");

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("removeAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: privateKey belongs to the private key of other weIdentity DId
     *
     * @throws Exception
     */
    public void testRemoveAuthorityIssuerCase12() throws Exception {

        CreateWeIdDataResult createWeIdNew = super.createWeId();

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);

        ResponseData<Boolean> response_ =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response_);

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId);
        removeAuthorityIssuerArgs.setWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("removeAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NO_PERMISSION.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: privateKey and private key of weIdentity DId do not match
     *
     * @throws Exception
     */
    public void testRemoveAuthorityIssuerCase13() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);

        ResponseData<Boolean> response_ =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response_);

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId);
        removeAuthorityIssuerArgs.getWeIdPrivateKey()
            .setPrivateKey(TestBaseUtil.createEcKeyPair()[1]);

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("removeAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NO_PERMISSION.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
