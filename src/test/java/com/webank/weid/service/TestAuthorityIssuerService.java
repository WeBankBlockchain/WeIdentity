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

package com.webank.weid.service;

import com.webank.weid.BaseTest;
import com.webank.weid.common.BeanUtil;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.AuthorityIssuerService;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

/**
 * test AuthorityIssuerService.
 *
 * @author v_wbgyang
 */
@SuppressWarnings("all")
public class TestAuthorityIssuerService extends BaseTest {

    /**
     * test AuthorityIssuerService.registerAuthorityIssuer.
     */
    @Test
    public void testRegisterAuthorityIssuer() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs = new RegisterAuthorityIssuerArgs();
        registerAuthorityIssuerArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        registerAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey(TestBaseUtil.privKey);

        AuthorityIssuer authorityIssuer = new AuthorityIssuer();
        registerAuthorityIssuerArgs.setAuthorityIssuer(authorityIssuer);

        authorityIssuer.setWeId("did:weid:0x0518f2b92fad9da7807a78b58af64db8997357db");
        authorityIssuer.setCreated(new Date().getTime());
        authorityIssuer.setName("webank1");
        authorityIssuer.setAccValue("0");

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        BeanUtil.print(response);
    }

    /**
     * test AuthorityIssuerService.removeAuthorityIssuer.
     */
    @Test
    public void testRemoveAuthorityIssuer() throws Exception {

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs = new RemoveAuthorityIssuerArgs();
        removeAuthorityIssuerArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        removeAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey(TestBaseUtil.privKey);

        removeAuthorityIssuerArgs.setWeId("did:weid:0x0518f2b92fad9da7807a78b58af64db8997357db");

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        BeanUtil.print(response);
    }

    /**
     * test AuthorityIssuerService.isAuthorityIssuer.
     */
    @Test
    public void testIsAuthorityIssuer() throws Exception {

        String weId = "did:weid:0x0518f2b92fad9da7807a78b58af64db8997357db";

        ResponseData<Boolean> response = authorityIssuerService.isAuthorityIssuer(weId);
        BeanUtil.print(response);
    }

    /**
     * test AuthorityIssuerService.queryAuthorityIssuerInfo.
     */
    @Test
    public void testQueryAuthorityIssuerInfo() throws Exception {

        String weId = "did:weid:0x0518f2b92fad9da7807a78b58af64db8997357db";

        ResponseData<AuthorityIssuer> response =
            authorityIssuerService.queryAuthorityIssuerInfo(weId);
        BeanUtil.print(response);
    }
}
