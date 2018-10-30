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
import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import org.junit.Assert;
import org.junit.Test;

public class TestQueryAuthorityIssuerInfo extends TestBaseServcie {

    @Test
    /**
     * case: query
     *
     * @throws Exception
     */
    public void testQueryAuthorityIssuerInfoCase1() throws Exception {

        CreateWeIdDataResult createWeId = super.registerAuthorityIssuer();

        ResponseData<AuthorityIssuer> response =
            authorityIssuerService.queryAuthorityIssuerInfo(createWeId.getWeId());
        System.out.println("\nqueryAuthorityIssuerInfo result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    @Test
    /**
     * case: weIdentity DId is blank
     *
     * @throws Exception
     */
    public void testQueryAuthorityIssuerInfoCase2() throws Exception {

        ResponseData<AuthorityIssuer> response = authorityIssuerService
            .queryAuthorityIssuerInfo(null);
        System.out.println("\nqueryAuthorityIssuerInfo result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /**
     * case: weIdentity DId is bad format
     *
     * @throws Exception
     */
    public void testQueryAuthorityIssuerInfoCase3() throws Exception {

        ResponseData<AuthorityIssuer> response =
            authorityIssuerService.queryAuthorityIssuerInfo("xx:xx:xxxxxxx");
        System.out.println("\nqueryAuthorityIssuerInfo result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /**
     * case: weIdentity DId is not exists
     *
     * @throws Exception
     */
    public void testQueryAuthorityIssuerInfoCase4() throws Exception {

        ResponseData<AuthorityIssuer> response =
            authorityIssuerService.queryAuthorityIssuerInfo(
                "did:weid:0xc7e399b8d2da337f4e92eb33ca88b60b899b5022");
        System.out.println("\nqueryAuthorityIssuerInfo result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /**
     * case: weIdentity DId is registed by other
     *
     * @throws Exception
     */
    public void testQueryAuthorityIssuerInfoCase5() throws Exception {

        ResponseData<AuthorityIssuer> response =
            authorityIssuerService.queryAuthorityIssuerInfo(
                "did:weid:0x5f3d8234e93823fac7ebdf0cfaa03b6a43d8773b");
        System.out.println("\nqueryAuthorityIssuerInfo result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /**
     * case: weIdentity DId is removed
     *
     * @throws Exception
     */
    public void testQueryAuthorityIssuerInfoCase6() throws Exception {

        CreateWeIdDataResult createWeId = super.registerAuthorityIssuer();

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId);

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("\nremoveAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        ResponseData<AuthorityIssuer> response1 =
            authorityIssuerService.queryAuthorityIssuerInfo(createWeId.getWeId());
        System.out.println("\nqueryAuthorityIssuerInfo result:");
        BeanUtil.print(response1);

        Assert.assertEquals(
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode(),
            response1.getErrorCode().intValue());
        Assert.assertNull(response1.getResult());
    }
}
