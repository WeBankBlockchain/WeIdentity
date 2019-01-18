package com.webank.weid.util;

import java.util.LinkedHashMap;

import org.junit.Assert;
import org.junit.Test;

import com.webank.weid.constant.JsonSchemaConstant;

public class TestJsonUtil {

    @Test
    public void testObjToJsonStr() {

        LinkedHashMap<String, Object> propertitesMap = new LinkedHashMap<String, Object>();
        propertitesMap.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATE_TYPE_STRING);
        propertitesMap.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is name");

        String propertites = JsonUtil.objToJsonStr(propertitesMap);
        Assert.assertNotNull(propertites);
    }

    @Test
    public void testJsonStrToObj() {

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        String s = "{\"name\":\"zhang san\", \"age\":21}";
        LinkedHashMap<String, Object> propertitesMap =
            (LinkedHashMap<String, Object>)JsonUtil.jsonStrToObj(map, s);
        Assert.assertNotNull(propertitesMap);
    }
}
