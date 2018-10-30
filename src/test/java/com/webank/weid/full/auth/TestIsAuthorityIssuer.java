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
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import org.junit.Assert;
import org.junit.Test;

public class TestIsAuthorityIssuer extends TestBaseServcie {

    @Test
    /**
     * case: is authority issuer
     *
     * @throws Exception
     */
    public void testIsAuthorityIssuerCase1() throws Exception {

        CreateWeIdDataResult createWeId = super.registerAuthorityIssuer();
        ResponseData<Boolean> response = authorityIssuerService
            .isAuthorityIssuer(createWeId.getWeId());
        System.out.println("\nisAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    @Test
    /**
     * case: weIdentity DId is bad format
     *
     * @throws Exception
     */
    public void testIsAuthorityIssuerCase2() throws Exception {

        ResponseData<Boolean> response = authorityIssuerService.isAuthorityIssuer("xxxxxxxxxxx");
        System.out.println("\nisAuthorityIssuer result:");
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
    public void testIsAuthorityIssuerCase3() throws Exception {

        ResponseData<Boolean> response = authorityIssuerService.isAuthorityIssuer(null);
        System.out.println("\nisAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: the weIdentity DId is registed by other
     *
     * @throws Exception
     */
    public void testIsAuthorityIssuerCase4() throws Exception {

        ResponseData<Boolean> response =
            authorityIssuerService.isAuthorityIssuer(
                "did:weid:0x5f3d8234e93823fac7ebdf0cfaa03b6a43d8773b");
        System.out.println("\nisAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: the weIdentity DId is not exists
     *
     * @throws Exception
     */
    public void testIsAuthorityIssuerCase5() throws Exception {

        ResponseData<Boolean> response =
            authorityIssuerService.isAuthorityIssuer(
                "did:weid:0x5f3d8234e93823fac7ebdf0cfaa03b6a43d87733");
        System.out.println("\nisAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    /**
     * case: the weIdentity DId is removed
     *
     * @throws Exception
     */
    public void testIsAuthorityIssuerCase6() throws Exception {

        CreateWeIdDataResult createWeId = super.registerAuthorityIssuer();

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId);

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("\nremoveAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        response = authorityIssuerService.isAuthorityIssuer(createWeId.getWeId());
        System.out.println("\nisAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
