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
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;
import org.junit.Test;

/**
 * test service.
 *
 * @author v_wbgyang
 */
public class TestWeIdService extends BaseTest {

    /**
     * you can get the publicKey and privateKey after testGenerateKeyPair.
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
     * test without reference method createWeId can't assert the result, because it is random.
     */
    @Test
    public void testCreateWeId_() throws Exception {

        ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();
        BeanUtil.print(response);
    }

    /**
     * test a parameter method createWeId.
     */
    @Test
    public void testCreateWeId() throws Exception {

        CreateWeIdArgs args = new CreateWeIdArgs();
        args.setWeIdPrivateKey(new WeIdPrivateKey());
        args.getWeIdPrivateKey().setPrivateKey(
            "38847998560426504802666437193681088212587743543930619195304160132018773764799");
        args.setPublicKey(
            "13018259646160420136476747261062739427107399118741098594421740627408250832097563679915569899249860162658726497802275511046279230970892819141376414047446393");

        ResponseData<String> response = weIdService.createWeId(args);
        BeanUtil.print(response);
    }

    /**
     * test WeIdService.testGetWeIdDocumentJson.
     */
    @Test
    public void testGetWeIdDocumentJson() throws Exception {

        String weId = "did:weid:0x0518f2b92fad9da7807a78b58af64db8997357db1";

        ResponseData<String> response = weIdService.getWeIdDocumentJson(weId);
        BeanUtil.print(response);
    }

    /**
     * test WeIdService.getWeIdDocument.
     */
    @Test
    public void testGetWeIdDocument() throws Exception {

        System.out.println("currentBlockNumber:" + this.getBlockNumber());
        String weId = "did:weid:0x0518f2b92fad9da7807a78b58af64db8997357d1";
        ResponseData<Boolean> isExists = weIdService.isWeIdExist(weId);
        System.out.println("is exists:" + isExists.getResult());

        long startTime = System.currentTimeMillis();
        ResponseData<WeIdDocument> response = weIdService.getWeIdDocument(weId);
        System.out.println("gas time:" + (System.currentTimeMillis() - startTime));
        BeanUtil.print(response);
    }

    /**
     * test WeIdService.setPublicKey.
     */
    @Test
    public void testSetPublicKey() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = new SetPublicKeyArgs();
        setPublicKeyArgs.setUserWeIdPrivateKey(new WeIdPrivateKey());
        setPublicKeyArgs.getUserWeIdPrivateKey().setPrivateKey(
            "38847998560426504802666437193681088212587743543930619195304160132018773764799");
        
        setPublicKeyArgs.setWeId("did:weid:0x0518f2b92fad9da7807a78b58af64db8997357db");
        setPublicKeyArgs.setOwner("did:weid:0x0518f2b92fad9da7807a78b58af64db8997357db");
        setPublicKeyArgs.setType("Secp256k1");
        setPublicKeyArgs.setPublicKey(
            "13018259646160420136476747261062739427107399118741098594421740627408250832097563679915569899249860162658726497802275511046279230970892819141376414047446393");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        BeanUtil.print(response);
    }

    /**
     * test WeIdService.setService.
     */
    @Test
    public void testSetService() throws Exception {

        SetServiceArgs setServiceArgs = new SetServiceArgs();
        setServiceArgs.setUserWeIdPrivateKey(new WeIdPrivateKey());
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey(
            "38847998560426504802666437193681088212587743543930619195304160132018773764799");

        setServiceArgs.setWeId("did:weid:0x0518f2b92fad9da7807a78b58af64db8997357db");
        setServiceArgs.setType("drivingCardService");
        setServiceArgs.setServiceEndpoint("https://weidentity.webank.com/endpoint/8377464");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        BeanUtil.print(response);
    }

    /**
     * test WeIdService.setAuthenticate.
     */
    @Test
    public void testSetAuthentication() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs = new SetAuthenticationArgs();
        setAuthenticationArgs.setUserWeIdPrivateKey(new WeIdPrivateKey());
        setAuthenticationArgs.getUserWeIdPrivateKey().setPrivateKey(
            "38847998560426504802666437193681088212587743543930619195304160132018773764799");

        setAuthenticationArgs.setWeId("did:weid:0x0518f2b92fad9da7807a78b58af64db8997357db");
        setAuthenticationArgs.setOwner("did:weid:0x0518f2b92fad9da7807a78b58af64db8997357db");
        setAuthenticationArgs.setPublicKey(
            "13018259646160420136476747261062739427107399118741098594421740627408250832097563679915569899249860162658726497802275511046279230970892819141376414047446393");
        setAuthenticationArgs.setType("RsaSignatureAuthentication2018");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        BeanUtil.print(response);
    }

    /**
     * test WeIdService.isWeIdExist.
     */
    @Test
    public void testIsWeIdExist() throws Exception {

        String weId = "did:weid:0x0518f2b92fad9da7807a78b58af64db8997357db";

        ResponseData<Boolean> response = weIdService.isWeIdExist(weId);
        BeanUtil.print(response);
    }
}
