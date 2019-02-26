package com.webank.weid.util;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.JsonSchemaConstant;
import com.webank.weid.protocol.base.Credential;

public class TestJsonUtil {

    @Test
    public void testObjToJsonStr() {

        LinkedHashMap<String, Object> propertitesMap = new LinkedHashMap<String, Object>();
        propertitesMap.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATE_TYPE_STRING);
        propertitesMap.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is name");

        String propertites = JsonUtil.objToJsonStr(propertitesMap);
        Assert.assertNotNull(propertites);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testJsonStrToObj() {

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        String s = "{\"name\":\"zhang san\", \"age\":21}";
        LinkedHashMap<String, Object> propertitesMap =
            (LinkedHashMap<String, Object>)JsonUtil.jsonStrToObj(map, s);
        Assert.assertNotNull(propertitesMap);
    }

    @Test
    public void testMapConversion() throws Exception {
        String uuid = UUID.randomUUID().toString();
        Credential cred = new Credential();
        cred.setIssuer("did:weid:0x00000011111111111");
        cred.setIssuranceDate(System.currentTimeMillis());
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
        String json = JsonUtil.mapToCompactJson(JsonUtil.objToMap(cred));
        Assert.assertFalse(StringUtils.isEmpty(json));
        Credential newcred = (Credential) JsonUtil.jsonStrToObj(new Credential(), json);
        Assert.assertNotNull(newcred);
    }
}
