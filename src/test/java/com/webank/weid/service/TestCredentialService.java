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
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.VerifyCredentialArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.CredentialService;
import org.junit.Test;

/**
 * test CredentialService
 *
 * @author v_wbgyang
 */
public class TestCredentialService extends BaseTest<CredentialService> {

    @Override
    public Class<CredentialService> initService() {

        return CredentialService.class;
    }

    /**
     * test CredentialService.createCredential
     */
    @Test
    public void testcreateCredential() throws Exception {

        int scene = 1;

        CreateCredentialArgs args = RequestUtil.createCredential(scene);

        ResponseData<Credential> response = service.createCredential(args);
        BeanUtil.print(response);
    }

    /**
     * test CredentialService.verifyCredential
     */
    @Test
    public void testVerifyCredential() throws Exception {

        int scene = 1;

        ResponseData<Credential> ctresponse =
            service.createCredential(RequestUtil.createCredential(scene));

        VerifyCredentialArgs args = RequestUtil.verifyCredential(scene, ctresponse.getResult());

        ResponseData<Boolean> response = service.verifyCredential(args.getCredential());
        BeanUtil.print(response);
    }

    /**
     * test CredentialService.verifyCredentialWithSpecifiedPubKey
     */
    @Test
    public void testVerifyCredentialWithPublicKey() throws Exception {

        int scene = 1;

        ResponseData<Credential> ctresponse =
            service.createCredential(RequestUtil.createCredential(scene));

        VerifyCredentialArgs args =
            RequestUtil.verifyCredentialWithSpecifiedPubKey(scene, ctresponse.getResult());

        ResponseData<Boolean> response = service.verifyCredentialWithSpecifiedPubKey(args);
        BeanUtil.print(response);
    }
}
