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

package com.webank.weid.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.request.CreateCredentialArgs;

/**
 * test crentialUtils.
 *
 * @author v_wbjnzhang
 */
public class TestCredentialUtils {

    @Test
    public void getCredentialFieldsTest() {

        // test arg is null
        Credential arg = null;
        Map<String, Object> map = new HashMap<>();
        String result = CredentialUtils.getCredentialFields(arg, map);
        Assert.assertEquals(result, "");
    }

    @Test
    public void extractCredentialMetadataTest() {
        Credential arg = null;
        CreateCredentialArgs result = CredentialUtils.extractCredentialMetadata(arg);
        Assert.assertNull(result);

        arg = new Credential();
        result = CredentialUtils.extractCredentialMetadata(arg);
        Assert.assertNotNull(result);

        arg.setContext(CredentialUtils.getDefaultCredentialContext());
        arg.setId(UUID.randomUUID().toString());
        arg.setCptId(14356);
        arg.setIssuer("gdsgshher");
        arg.setIssuranceDate(new Long(System.currentTimeMillis()));
        arg.setExpirationDate(new Long(System.currentTimeMillis()));
        LinkedHashMap<String, Object> claim = new LinkedHashMap<>();
        claim.put("sfsfs", "sfsfs");
        result = CredentialUtils.extractCredentialMetadata(arg);
        Assert.assertNotNull(result);
    }
}
