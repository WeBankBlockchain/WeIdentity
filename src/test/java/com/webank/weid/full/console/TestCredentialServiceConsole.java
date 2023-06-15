package com.webank.weid.full.console;

import com.fasterxml.jackson.databind.JsonNode;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.JsonSchemaConstant;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.*;
import com.webank.weid.protocol.request.CptMapArgs;
import com.webank.weid.protocol.request.CreateCredentialPojoArgs;
import com.webank.weid.service.console.CredentialServiceConsole;
import com.webank.weid.service.console.WeIdServiceConsole;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.WeIdUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class TestCredentialServiceConsole {

    private static final Logger logger = LoggerFactory.getLogger(TestCredentialServiceConsole.class);

    private String privateKey = TestBaseUtil.readPrivateKeyFromFile("private_key");
    private static final WeIdServiceConsole weIdServiceConsole = new WeIdServiceConsole();
    private static final CredentialServiceConsole credentialServiceConsole = new CredentialServiceConsole();
    private static Integer cptId = 100000;

    /**
     * case：when issuer and cpt publisher is same,createCredentialPojo success.
     */
    @Test
    public void testCreateCredentialPojo_success() {

        WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
        weIdAuthentication.setWeId(WeIdUtils.getWeIdFromPrivateKey(privateKey));
        weIdAuthentication.setAuthenticationMethodId(WeIdUtils.getWeIdFromPrivateKey(privateKey) + "#keys-0");
        weIdAuthentication.setWeIdPrivateKey(new WeIdPrivateKey());
        weIdAuthentication.getWeIdPrivateKey()
                .setPrivateKey(privateKey);
        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
                new CreateCredentialPojoArgs<Map<String, Object>>();

        createCredentialPojoArgs.setIssuer(WeIdUtils.getWeIdFromPrivateKey(privateKey));
        createCredentialPojoArgs.setExpirationDate(
                System.currentTimeMillis() + (1000 * 60 * 60 * 24));

        createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);
        try {
            HashMap<String, Object> cptJsonSchemaData = new HashMap<String, Object>();
            JsonNode jsonNode = DataToolUtils.loadJsonObjectFromResource("claim.json");
            cptJsonSchemaData = DataToolUtils.deserialize(jsonNode.toString(), HashMap.class);
            cptJsonSchemaData.put("id", WeIdUtils.getWeIdFromPrivateKey(privateKey));
            createCredentialPojoArgs.setClaim(cptJsonSchemaData);
        } catch (IOException e) {
            logger.error("buildCreateCredentialPojoArgs failed. ", e);
        }
        createCredentialPojoArgs.setCptId(cptId);

        ResponseData<CredentialPojo> response =
                credentialServiceConsole.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        CptMapArgs cptMapArgs = new CptMapArgs();
        cptMapArgs.setCptJsonSchema(buildCptJsonSchema());
        cptMapArgs.setWeIdAuthentication(weIdAuthentication);
        if (weIdServiceConsole.queryCpt(cptId).getResult() == null) {
            weIdServiceConsole.registerCpt(cptMapArgs, cptId);
        }

        ResponseData<Boolean> verify = credentialServiceConsole.verify(
                DataToolUtils.publicKeyFromPrivate(new BigInteger(privateKey)).toString(10), response.getResult());
        Assert.assertTrue(verify.getResult());

        WeIdDocument weIdDocument = weIdServiceConsole.createWeIdDocument(DataToolUtils.publicKeyStrFromPrivate(new BigInteger(privateKey))).getResult();
        verify = credentialServiceConsole.verify(weIdDocument, response.getResult());
        Assert.assertTrue(verify.getResult());
    }

    public static HashMap<String, Object> buildCptJsonSchema() {

        HashMap<String, Object> cptJsonSchemaNew = new HashMap<String, Object>(3);
        cptJsonSchemaNew.put(JsonSchemaConstant.TITLE_KEY, "Digital Identity");
        cptJsonSchemaNew.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is a cpt template");

        HashMap<String, Object> propertitesMap1 = new HashMap<String, Object>(2);
        propertitesMap1.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_STRING);
        propertitesMap1.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is name");

        String[] genderEnum = {"F", "M"};
        HashMap<String, Object> propertitesMap2 = new HashMap<String, Object>(2);
        propertitesMap2.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_STRING);
        propertitesMap2.put(JsonSchemaConstant.DATA_TYPE_ENUM, genderEnum);

        HashMap<String, Object> propertitesMap3 = new HashMap<String, Object>(2);
        propertitesMap3.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_NUMBER);
        propertitesMap3.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is age");

        HashMap<String, Object> propertitesMap4 = new HashMap<String, Object>(2);
        propertitesMap4.put(JsonSchemaConstant.TYPE_KEY, JsonSchemaConstant.DATA_TYPE_STRING);
        propertitesMap4.put(JsonSchemaConstant.DESCRIPTION_KEY, "this is weid");

        HashMap<String, Object> cptJsonSchema = new HashMap<String, Object>(3);
        cptJsonSchema.put("name", propertitesMap1);
        cptJsonSchema.put("gender", propertitesMap2);
        cptJsonSchema.put("age", propertitesMap3);
        cptJsonSchema.put("id", propertitesMap4);
        cptJsonSchemaNew.put(JsonSchemaConstant.PROPERTIES_KEY, cptJsonSchema);

        String[] genderRequired = {"id", "name", "gender"};
        cptJsonSchemaNew.put(JsonSchemaConstant.REQUIRED_KEY, genderRequired);

        return cptJsonSchemaNew;
    }

    @Test
    public void testCreateSelectiveCredential_success() {

        WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
        weIdAuthentication.setWeId(WeIdUtils.getWeIdFromPrivateKey(privateKey));
        weIdAuthentication.setAuthenticationMethodId(WeIdUtils.getWeIdFromPrivateKey(privateKey) + "#keys-0");
        weIdAuthentication.setWeIdPrivateKey(new WeIdPrivateKey());
        weIdAuthentication.getWeIdPrivateKey()
                .setPrivateKey(privateKey);
        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
                new CreateCredentialPojoArgs<Map<String, Object>>();

        createCredentialPojoArgs.setIssuer(WeIdUtils.getWeIdFromPrivateKey(privateKey));
        createCredentialPojoArgs.setExpirationDate(
                System.currentTimeMillis() + (1000 * 60 * 60 * 24));

        CptMapArgs cptMapArgs = new CptMapArgs();
        cptMapArgs.setCptJsonSchema(buildCptJsonSchema());
        cptMapArgs.setWeIdAuthentication(weIdAuthentication);
        if (weIdServiceConsole.queryCpt(cptId).getResult() == null) {
            weIdServiceConsole.registerCpt(cptMapArgs, cptId);
        }

        createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);
        createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);
        try {
            HashMap<String, Object> cptJsonSchemaData = new HashMap<String, Object>();
            JsonNode jsonNode = DataToolUtils.loadJsonObjectFromResource("claim.json");
            cptJsonSchemaData = DataToolUtils.deserialize(jsonNode.toString(), HashMap.class);
            cptJsonSchemaData.put("id", WeIdUtils.getWeIdFromPrivateKey(privateKey));
            createCredentialPojoArgs.setClaim(cptJsonSchemaData);
        } catch (IOException e) {
            logger.error("buildCreateCredentialPojoArgs failed. ", e);
        }
        createCredentialPojoArgs.setCptId(cptId);

        CredentialPojo credentialPojo =
                credentialServiceConsole.createCredential(createCredentialPojoArgs).getResult();
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        CredentialPojo  selectiveCredentialPojo =
                credentialServiceConsole.createSelectiveCredential(credentialPojo, claimPolicy).getResult();

        Assert.assertNotNull(selectiveCredentialPojo);

        ResponseData<Boolean> verify = credentialServiceConsole.verify(
                DataToolUtils.publicKeyFromPrivate(new BigInteger(privateKey)).toString(10), selectiveCredentialPojo);
        LogUtil.info(logger, "selectiveCredentialPojo", selectiveCredentialPojo);
        LogUtil.info(logger, "verifyCredentialPojo", verify);
        Assert.assertTrue(verify.getResult());
    }
}
