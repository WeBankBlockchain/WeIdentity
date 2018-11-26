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
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.RegisterCptArgs;
import com.webank.weid.protocol.request.UpdateCptArgs;
import com.webank.weid.protocol.response.ResponseData;
import org.junit.Test;

/**
 * test CptService.
 *
 * @author v_wbgyang
 */
public class TestCptService extends BaseTest {

    /**jsonSchema for register.*/
    private static String schema =
        "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"title\":\"/etc/fstab\",\"description\":\"JSON representation of /etc/fstab\",\"type\":\"object\",\"properties\":{\"swap\":{\"$ref\":\"#/definitions/mntent\"}},\"patternProperties\":{\"^/([^/]+(/[^/]+)*)?$\":{\"$ref\":\"#/definitions/mntent\"}},\"required\":[\"/\",\"swap\"],\"additionalProperties\":false,\"definitions\":{\"mntent\":{\"title\":\"mntent\",\"description\":\"An fstab entry\",\"type\":\"object\",\"properties\":{\"device\":{\"type\":\"string\"},\"fstype\":{\"type\":\"string\"},\"options\":{\"type\":\"array\",\"minItems\":1,\"items\":{\"type\":\"string\"}},\"dump\":{\"type\":\"integer\",\"minimum\":0},\"fsck\":{\"type\":\"integer\",\"minimum\":0}},\"required\":[\"device\",\"fstype\"],\"additionalItems\":false}}}";

    /**
     * test CptService.registerCpt.
     */
    @Test
    public void testRegisterCpt() throws Exception {

        RegisterCptArgs registerCptArgs = new RegisterCptArgs();
        registerCptArgs.setCptPublisherPrivateKey(new WeIdPrivateKey());
        registerCptArgs.getCptPublisherPrivateKey().setPrivateKey(
            "38847998560426504802666437193681088212587743543930619195304160132018773764799");

        registerCptArgs.setCptJsonSchema(schema);
        registerCptArgs.setCptPublisher("did:weid:0x0518f2b92fad9da7807a78b58af64db8997357db");

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        BeanUtil.print(response);
    }

    /**
     * test CptService.queryCpt.
     */
    @Test
    public void testQueryCpt() throws Exception {

        Integer cptId = new Integer(2000682);

        ResponseData<Cpt> response = cptService.queryCpt(cptId);
        BeanUtil.print(response);
    }

    /**
     * test CptService.updateCpt.
     */
    @Test
    public void testUpdateCpt() throws Exception {

        UpdateCptArgs updateCptArgs = new UpdateCptArgs();
        updateCptArgs.setCptPublisherPrivateKey(new WeIdPrivateKey());
        updateCptArgs.getCptPublisherPrivateKey().setPrivateKey(
            "38847998560426504802666437193681088212587743543930619195304160132018773764799");

        updateCptArgs.setCptJsonSchema(schema);
        updateCptArgs.setCptPublisher("did:weid:0x0518f2b92fad9da7807a78b58af64db8997357db");
        Integer cptId = new Integer(2000682);
        updateCptArgs.setCptId(cptId);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        BeanUtil.print(response);
    }
}
