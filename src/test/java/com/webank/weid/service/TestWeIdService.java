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
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.WeIdService;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;
import org.junit.Test;

/**
 * test service
 *
 * @author v_wbgyang
 */
public class TestWeIdService extends BaseTest<WeIdService> {

    @Override
    public Class<WeIdService> initService() {

        return WeIdService.class;
    }

    /**
     * you can get the publicKey and privateKey after testGenerateKeyPair
     */
    @Test
    public void testGenerateKeyPair() throws Exception {

        ECKeyPair keyPair = Keys.createEcKeyPair();
        String publicKey = String.valueOf(keyPair.getPublicKey());
        String privateKey = String.valueOf(keyPair.getPrivateKey());
        System.out.println("publicKey:" + publicKey);
        System.out.println("privateKey:" + privateKey);
    }

    /**
     * test without reference method createWeId can't assert the result, because it is random
     */
    @Test
    public void testCreateWeId_() throws Exception {

        ResponseData<CreateWeIdDataResult> response = service.createWeId();
        BeanUtil.print(response);
    }

    /**
     * test a parameter method createWeId
     */
    @Test
    public void testCreateWeId() throws Exception {

        int scene = 1;

        CreateWeIdArgs args = RequestUtil.createWeId(scene);

        ResponseData<String> response = service.createWeId(args);
        BeanUtil.print(response);
    }

    /**
     * test WeIdService.testGetWeIdDocumentJson
     */
    @Test
    public void testGetWeIdDocumentJson() throws Exception {

        int scene = 1;

        String args = RequestUtil.getWeIdDocument(scene);

        ResponseData<String> response = service.getWeIdDocumentJson(args);
        BeanUtil.print(response);
    }

    /**
     * test WeIdService.getWeIdDocument
     */
    @Test
    public void testGetWeIdDocument() throws Exception {

        int scene = 1;

        String args = RequestUtil.getWeIdDocument(scene);

        ResponseData<WeIdDocument> response = service.getWeIdDocument(args);
        BeanUtil.print(response);
    }

    /**
     * test WeIdService.setPublicKey
     */
    @Test
    public void testSetPublicKey() throws Exception {

        int scene = 1;

        SetPublicKeyArgs args = RequestUtil.setPublicKey(scene);

        ResponseData<Boolean> response = service.setPublicKey(args);
        BeanUtil.print(response);
    }

    /**
     * test WeIdService.setService
     */
    @Test
    public void testSetService() throws Exception {

        int scene = 1;

        SetServiceArgs args = RequestUtil.setService(scene);

        ResponseData<Boolean> response = service.setService(args);
        BeanUtil.print(response);
    }

    /**
     * test WeIdService.setAuthenticate
     */
    @Test
    public void testSetAuthentication() throws Exception {

        int scene = 1;

        SetAuthenticationArgs args = RequestUtil.setAuthentication(scene);

        ResponseData<Boolean> response = service.setAuthentication(args);
        BeanUtil.print(response);
    }
}
