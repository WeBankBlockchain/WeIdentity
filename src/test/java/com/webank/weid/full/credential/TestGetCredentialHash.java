/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

package com.webank.weid.full.credential;

import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.response.ResponseData;

public class TestGetCredentialHash extends TestBaseServcie {

    @Override
    public void testInit() {
        System.out.println();
    }

    @Test
    public void testGetCredentialHashCase1() {
        Credential credential = buildCredential();
        ResponseData<String> response1 = credentialService.getCredentialHash(credential);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertNotNull(response1.getResult());

        credential = buildCredential();
        credential.setIssuer("xxxxxxxxx");
        response1 = credentialService.getCredentialHash(credential);
        Assert.assertNotEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response1.getResult());
    }

    private Credential buildCredential() {
        Credential credential = new Credential();
        HashMap<String, Object> claim = new HashMap<>();
        claim.put("xxxxxxxxxxxxxx", "xxxxxxxxxxxxxx");
        credential.setClaim(claim);
        credential.setContext("v1");
        credential.setCptId(new Integer(1001));
        credential.setExpirationDate(System.currentTimeMillis() + 1000L);
        credential.setId(UUID.randomUUID().toString());
        credential.setIssuer("did:weid:0x0000000000000000");
        credential.setIssuranceDate(System.currentTimeMillis());
        credential.setSignature("xxxxxxxxxxx");
        return credential;
    }
}
