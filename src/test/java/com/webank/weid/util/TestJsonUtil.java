/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.JsonSchemaConstant;
import com.webank.weid.protocol.base.Challenge;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.cpt.Cpt101;
import com.webank.weid.protocol.cpt.Cpt103;

public class TestJsonUtil {

    @Test
    public void testObjToJsonStr() {

        LinkedHashMap<String, Object> propertitesMap = new LinkedHashMap<String, Object>();
        propertitesMap.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_STRING);
        propertitesMap.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is name");

        // String propertites = JsonUtil.objToJsonStr(propertitesMap);
        String propertites = DataToolUtils.serialize(propertitesMap);
        Assert.assertNotNull(propertites);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testJsonStrToObj() {

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        String s = "{\"name\":\"zhang san\", \"age\":21}";
        // LinkedHashMap<String, Object> propertitesMap =
        //     (LinkedHashMap<String, Object>)JsonUtil.jsonStrToObj(map, s);
        LinkedHashMap<String, Object> propertitesMap =
            DataToolUtils.deserialize(s, LinkedHashMap.class);
        Assert.assertNotNull(propertitesMap);
    }

    @Test
    public void testMapConversion() throws Exception {
        String uuid = UUID.randomUUID().toString();
        Credential cred = new Credential();
        cred.setIssuer("did:weid:0x00000011111111111");
        cred.setIssuanceDate(System.currentTimeMillis());
        cred.setExpirationDate(System.currentTimeMillis() + new Long(10000));
        cred.setContext(CredentialConstant.DEFAULT_CREDENTIAL_CONTEXT);
        cred.setId(uuid);
        cred.setCptId(55);
        HashMap<String, Object> claim = new HashMap<>();
        claim.put("xxxxxxxxxxxxxx", "xxxxxxxxxxxxxx");
        claim.put("xxy", "yyx");
        claim.put("age", 12);
        claim.put("acc", new BigInteger("111111111111111"));
        claim.put("date", new Long(1000000000));
        cred.setClaim(claim);
        String json = DataToolUtils.mapToCompactJson(DataToolUtils.objToMap(cred));
        Assert.assertFalse(StringUtils.isEmpty(json));
        Credential newcred = DataToolUtils.deserialize(json, Credential.class);
        Assert.assertNotNull(newcred);
    }

    @Test
    public void testCptGenerator() throws Exception {
        String cptSchema = DataToolUtils.generateDefaultCptJsonSchema(11);
        Assert.assertTrue(DataToolUtils.isCptJsonSchemaValid(cptSchema));
        cptSchema = DataToolUtils.generateDefaultCptJsonSchema(101);
        Assert.assertTrue(DataToolUtils.isCptJsonSchemaValid(cptSchema));
        Cpt101 cpt101 = new Cpt101();
        cpt101.setId("did:weid:101:0x11111");
        cpt101.setReceiver("bbb");
        List<String> stringList = new ArrayList<>();
        stringList.add("ccc");
        cpt101.setSubjects(stringList);
        Assert.assertTrue(DataToolUtils
            .isValidateJsonVersusSchema(DataToolUtils.objToJsonStrWithNoPretty(cpt101), cptSchema));

        cptSchema = DataToolUtils.generateDefaultCptJsonSchema(103);
        Assert.assertTrue(DataToolUtils.isCptJsonSchemaValid(cptSchema));
        Cpt103 cpt103 = new Cpt103();
        cpt103.setChallenge(Challenge.create("did:weid:0x11111", "abcd"));
        cpt103.setProof("aaa");
        cpt103.setId("did:weid:101:0x12221");
        Assert.assertTrue(DataToolUtils
            .isValidateJsonVersusSchema(DataToolUtils.objToJsonStrWithNoPretty(cpt103), cptSchema));

        cptSchema = DataToolUtils.generateDefaultCptJsonSchema(105);
        Assert.assertTrue(DataToolUtils.isCptJsonSchemaValid(cptSchema));
        cptSchema = DataToolUtils.generateUnformattedCptJsonSchema();
        Assert.assertTrue(DataToolUtils.isCptJsonSchemaValid(cptSchema));
    }
}
