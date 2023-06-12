package com.webank.weid.full.console;

import com.webank.weid.MockIssuerClient;
import com.webank.weid.blockchain.protocol.response.ResponseData;
import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.JsonSchemaConstant;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.full.TestData;
import com.webank.weid.protocol.base.*;
import com.webank.weid.protocol.request.AuthenticationArgs;
import com.webank.weid.protocol.request.CptMapArgs;
import com.webank.weid.protocol.request.ServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.service.console.WeIdServiceConsole;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.WeIdUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestWeIdServiceConsole {

    private static final Logger logger = LoggerFactory.getLogger(TestWeIdServiceConsole.class);

    private static final WeIdServiceConsole weIdServiceConsole = new WeIdServiceConsole();
    private String privateKey = TestBaseUtil.readPrivateKeyFromFile("private_key");

    static { MockIssuerClient.mockMakeCredentialTemplate();}

    /**
     * case: create WeIdDocument success.
     */
    @Test
    public void testCreateWeIdDocument_createSuccess() {

        ResponseData<WeIdDocument> response = weIdServiceConsole.createWeIdDocument(DataToolUtils.publicKeyStrFromPrivate(new BigInteger(privateKey)));
        LogUtil.info(logger, "createWeIdDocument", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case: create WeIdDocument Json success.
     */
    @Test
    public void testCreateWeIdDocumentJson_createSuccess() {

        ResponseData<String> response = weIdServiceConsole.createWeIdDocumentJson(DataToolUtils.publicKeyStrFromPrivate(new BigInteger(privateKey)));
        LogUtil.info(logger, "createWeIdDocumentJson", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case: SetAuthentication success.
     */
    @Test
    public void testSetAuthentication_setAuthenticationSuccess() {

        WeIdDocument weIdDocument = weIdServiceConsole.createWeIdDocument(DataToolUtils.publicKeyStrFromPrivate(new BigInteger(privateKey))).getResult();
        AuthenticationArgs setAuthenticationArgs = new AuthenticationArgs();
        setAuthenticationArgs.setPublicKey(DataToolUtils.publicKeyStrFromPrivate(new BigInteger(DataToolUtils.generatePrivateKey())));
        ResponseData<WeIdDocument> response = weIdServiceConsole.setAuthentication(weIdDocument, setAuthenticationArgs);
        LogUtil.info(logger, "createWeIdDocumentJson", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case: set success.
     */
    @Test
    public void testSetService_sucess() {

        ServiceArgs serviceArgs = new ServiceArgs();
        serviceArgs.setType(TestData.SERVICE_TYPE);
        serviceArgs.setServiceEndpoint(TestData.SERVICE_ENDPOINT);

        WeIdDocument weIdDocument = weIdServiceConsole.createWeIdDocument(DataToolUtils.publicKeyStrFromPrivate(new BigInteger(privateKey))).getResult();
        ResponseData<WeIdDocument> response = weIdServiceConsole.setService(weIdDocument, serviceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(com.webank.weid.blockchain.constant.ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
    }

    /**
     * build cpt json schema.
     *
     * @return HashMap
     */
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

    /**
     * case: when ctpId bigger>200 000 0,register ordinary cpt success.
     */
    @Test
    public void testRegisterCptArgsWithId_ordinaryCptSuccess() throws Exception {

        WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
        weIdAuthentication.setWeId(WeIdUtils.getWeIdFromPrivateKey(privateKey));
        weIdAuthentication.setWeIdPrivateKey(new WeIdPrivateKey());
        weIdAuthentication.getWeIdPrivateKey()
                .setPrivateKey(privateKey);
        CptMapArgs cptMapArgs = new CptMapArgs();
        cptMapArgs.setCptJsonSchema(buildCptJsonSchema());
        cptMapArgs.setWeIdAuthentication(weIdAuthentication);

        Integer issuerCptId = 2000000;
        while (weIdServiceConsole.queryCpt(issuerCptId).getResult() != null) {
            issuerCptId += (int) (Math.random() * 10 + 1);
        }

        ResponseData<CptBaseInfo> response = weIdServiceConsole.registerCpt(cptMapArgs, issuerCptId);
        LogUtil.info(logger, "testRegisterCptArgs with cptId", response);

        Assert.assertEquals(com.webank.weid.blockchain.constant.ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case： cpt updateCpt success, used no auth issuer to update no auth cpt.
     */
    @Test
    public void testUpdateCpt_success() {

        WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
        weIdAuthentication.setWeId(WeIdUtils.getWeIdFromPrivateKey(privateKey));
        weIdAuthentication.setWeIdPrivateKey(new WeIdPrivateKey());
        weIdAuthentication.getWeIdPrivateKey()
                .setPrivateKey(privateKey);
        CptMapArgs cptMapArgs = new CptMapArgs();
        cptMapArgs.setCptJsonSchema(buildCptJsonSchema());
        cptMapArgs.setWeIdAuthentication(weIdAuthentication);

        Integer issuerCptId = 10086;
        while (weIdServiceConsole.queryCpt(issuerCptId).getResult() != null) {
            issuerCptId += (int) (Math.random() * 10 + 1);
        }

        weIdServiceConsole.registerCpt(cptMapArgs, issuerCptId);

        ResponseData<CptBaseInfo> response = weIdServiceConsole.updateCpt(
                cptMapArgs,
                issuerCptId);
        LogUtil.info(logger, "updateCpt", response);

        Assert.assertEquals(com.webank.weid.blockchain.constant.ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    @Test
    public void happyPathPolicyAll() {
        WeIdAuthentication auth = new WeIdAuthentication();
        auth.setWeId(WeIdUtils.getWeIdFromPrivateKey(privateKey));
        auth.setAuthenticationMethodId(DataToolUtils.publicKeyStrFromPrivate(new BigInteger(privateKey)) + "#keys-0");
        auth.setWeIdPrivateKey(new WeIdPrivateKey(privateKey));
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":0,\"gender\":0,\"age\":0,\"id\":0}");
        List<Integer> claimPolicyIdList = new ArrayList<>();
        ResponseData<Integer> registerResp = weIdServiceConsole
                .registerPolicy(claimPolicy.getFieldsToBeDisclosed(), auth);
        Assert.assertTrue(registerResp.getResult() > 0);
        claimPolicyIdList.add(registerResp.getResult());
        registerResp = weIdServiceConsole
                .registerPolicy(claimPolicy.getFieldsToBeDisclosed(), auth);
        Assert.assertTrue(registerResp.getResult() > 0);
        claimPolicyIdList.add(registerResp.getResult());
        registerResp = weIdServiceConsole
                .registerPolicy(claimPolicy.getFieldsToBeDisclosed(), auth);
        Assert.assertTrue(registerResp.getResult() > 0);
        claimPolicyIdList.add(registerResp.getResult());
        ClaimPolicy claimPolicyFromChain = weIdServiceConsole.getClaimPolicy(registerResp.getResult())
                .getResult();
        Assert.assertFalse(StringUtils.isEmpty(claimPolicyFromChain.getFieldsToBeDisclosed()));
        System.out.println(claimPolicyFromChain.getFieldsToBeDisclosed());
        ResponseData<Integer> presentationResp = weIdServiceConsole
                .registerPresentationPolicy(claimPolicyIdList, auth);
        Assert.assertTrue(presentationResp.getResult() >= 0);
        ResponseData<PresentationPolicyE> getClaimFromPresResp = weIdServiceConsole
                .getPresentationPolicy(presentationResp.getResult());
        Assert.assertNotNull(getClaimFromPresResp.getResult());
        System.out.println(DataToolUtils.serialize(getClaimFromPresResp.getResult()));
    }
}
