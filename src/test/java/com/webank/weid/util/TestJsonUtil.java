

package com.webank.weid.util;

import com.networknt.schema.ValidationMessage;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.JsonSchemaConstant;
import com.webank.weid.protocol.base.Challenge;
import com.webank.weid.protocol.base.Credential;
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

        cptSchema = DataToolUtils.generateDefaultCptJsonSchema(103);
        Assert.assertTrue(DataToolUtils.isCptJsonSchemaValid(cptSchema));
        Cpt103 cpt103 = new Cpt103();
        cpt103.setChallenge(Challenge.create("did:weid:0x11111", "abcd"));
        cpt103.setProof("aaa");
        cpt103.setId("did:weid:101:0x12221");
        Set<ValidationMessage> checkRes = DataToolUtils.checkJsonVersusSchema(
            DataToolUtils.objToJsonStrWithNoPretty(cpt103), cptSchema);
        Assert.assertTrue(checkRes.size() == 0);

        cptSchema = DataToolUtils.generateDefaultCptJsonSchema(105);
        Assert.assertTrue(DataToolUtils.isCptJsonSchemaValid(cptSchema));
        cptSchema = DataToolUtils.generateUnformattedCptJsonSchema();
        Assert.assertTrue(DataToolUtils.isCptJsonSchemaValid(cptSchema));
    }
}
