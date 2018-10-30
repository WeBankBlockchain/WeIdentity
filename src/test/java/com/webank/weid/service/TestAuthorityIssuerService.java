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
import com.webank.weid.common.RequestUtil;
import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.AuthorityIssuerService;
import org.junit.Assert;
import org.junit.Test;

/**
 * test AuthorityIssuerService
 *
 * @author v_wbgyang
 */
@SuppressWarnings("all")
public class TestAuthorityIssuerService extends BaseTest<AuthorityIssuerService> {

    @Override
    public Class<AuthorityIssuerService> initService() {

        return AuthorityIssuerService.class;
    }

    /**
     * test AuthorityIssuerService.registerAuthorityIssuer
     */
    @Test
    public void testRegisterAuthorityIssuer() throws Exception {

        int scene = 1;

        RegisterAuthorityIssuerArgs args = RequestUtil.registerAuthorityIssuer(scene);

        ResponseData<Boolean> response = service.registerAuthorityIssuer(args);
        BeanUtil.print(response);
    }

    /**
     * test AuthorityIssuerService.removeAuthorityIssuer
     */
    @Test
    public void testRemoveAuthorityIssuer() throws Exception {

        int scene = 1;

        RemoveAuthorityIssuerArgs args = RequestUtil.removeAuthorityIssuer(scene);

        ResponseData<Boolean> response = service.removeAuthorityIssuer(args);
        BeanUtil.print(response);
    }

    /**
     * test AuthorityIssuerService.isAuthorityIssuer
     */
    @Test
    public void testIsAuthorityIssuer() throws Exception {

        int scene = 1;

        String args = RequestUtil.isAuthorityIssuer(scene);

        ResponseData<Boolean> response = service.isAuthorityIssuer(args);
        BeanUtil.print(response);
    }

    /**
     * test AuthorityIssuerService.queryAuthorityIssuerInfo
     */
    @Test
    public void testQueryAuthorityIssuerInfo() throws Exception {

        int scene = 1;

        String args = RequestUtil.queryAuthorityIssuerInfo(scene);

        ResponseData<AuthorityIssuer> response = service.queryAuthorityIssuerInfo(args);
        BeanUtil.print(response);
    }
}
