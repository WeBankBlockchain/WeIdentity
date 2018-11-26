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
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.VerifyCredentialArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.DateUtils;
import java.text.ParseException;
import org.junit.Test;

/**
 * test CredentialService.
 *
 * @author v_wbgyang
 */
public class TestCredentialService extends BaseTest {

    /**claimData for register.*/
    private static String schemaData =
         "{\"/\":{\"device\":\"/dev/sda1\",\"fstype\":\"btrfs\",\"options\":[\"ssd\"]},\"swap\":{\"device\":\"/dev/sda2\",\"fstype\":\"swap\"},\"/tmp\":{\"device\":\"tmpfs\",\"fstype\":\"tmpfs\",\"options\":[\"size=64M\"]},\"/var/lib/mysql\":{\"device\":\"/dev/data/mysql\",\"fstype\":\"btrfs\"}}";

    /**
     * test CredentialService.createCredential.
     */
    @Test
    public void testcreateCredential() throws Exception {

        CreateCredentialArgs createCredentialArgs = buildCreateCredentialArgs();

        ResponseData<Credential> response =
            credentialService.createCredential(createCredentialArgs);
        BeanUtil.print(response);
    }

    /**
     * test CredentialService.verifyCredential.
     */
    @Test
    public void testVerifyCredential() throws Exception {

        CreateCredentialArgs createCredentialArgs = buildCreateCredentialArgs();
        ResponseData<Credential> ctresponse =
            credentialService.createCredential(createCredentialArgs);

        ResponseData<Boolean> response = credentialService.verifyCredential(ctresponse.getResult());
        BeanUtil.print(response);
    }

    /**
     * test CredentialService.verifyCredentialWithSpecifiedPubKey.
     */
    @Test
    public void testVerifyCredentialWithPublicKey() throws Exception {

        CreateCredentialArgs createCredentialArgs = buildCreateCredentialArgs();
        ResponseData<Credential> ctresponse =
            credentialService.createCredential(createCredentialArgs);

        VerifyCredentialArgs verifyCredentialArgs = new VerifyCredentialArgs();
        verifyCredentialArgs.setWeIdPublicKey(new WeIdPublicKey());
        verifyCredentialArgs.getWeIdPublicKey().setPublicKey(
            "13018259646160420136476747261062739427107399118741098594421740627408250832097563679915569899249860162658726497802275511046279230970892819141376414047446393");

        verifyCredentialArgs.setCredential(ctresponse.getResult());

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(verifyCredentialArgs);
        BeanUtil.print(response);
    }

    /**
     * buildCreateCredentialArgs.
     * 
     * @return CreateCredentialArgs CreateCredentialArgs Object
     * @throws ParseException may be throw ParseException
     */
    private CreateCredentialArgs buildCreateCredentialArgs() throws ParseException {

        CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
        createCredentialArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(
            "38847998560426504802666437193681088212587743543930619195304160132018773764799");

        createCredentialArgs.setClaim(schemaData);
        createCredentialArgs.setCptId(2000682);
        createCredentialArgs
            .setExpirationDate(DateUtils.convertStringToDate("2019-10-11T18:09:42Z").getTime());
        createCredentialArgs.setIssuer("did:weid:0x0518f2b92fad9da7807a78b58af64db8997357db1");

        return createCredentialArgs;
    }
}
