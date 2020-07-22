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

package com.webank.weid.full.credentialpojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.CredentialConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.ClaimPolicy;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.request.CreateCredentialPojoArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.CredentialPojoUtils;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.DateUtils;

/**
 * createCredential method for testing CredentialService.
 *
 * @author v_wbgyang
 */
public class TestCreateSelectiveCredential extends TestBaseService {

    private static final Logger logger = LoggerFactory
        .getLogger(TestCreateSelectiveCredential.class);

    @Override
    public synchronized void testInit() {

        super.testInit();
        if (credentialPojo == null) {
            credentialPojo = super.createCredentialPojo(createCredentialPojoArgs);
        }
    }

    /**
     * case：createSelectiveCredential success.
     */
    @Test
    public void testCreateSelectiveCredential_success() {
        Assert.assertNotNull(selectiveCredentialPojo);

        ResponseData<Boolean> verify = credentialPojoService
            .verify(selectiveCredentialPojo.getIssuer(), selectiveCredentialPojo);
        LogUtil.info(logger, "selectiveCredentialPojo", selectiveCredentialPojo);
        LogUtil.info(logger, "verifyCredentialPojo", verify);
        Assert.assertTrue(verify.getResult());
    }

    @Test
    public void testTwoCredentialPojoEqual() {
        CredentialPojo tmpCred = copyCredentialPojo(selectiveCredentialPojo);
        Assert.assertTrue(CredentialPojoUtils.isEqual(selectiveCredentialPojo, tmpCred));
    }

    @Test
    public void testCreateFullyNonSdCredential() {
        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        CredentialPojo credential =
            credentialPojoService.createCredential(createCredentialPojoArgs).getResult();
        Assert.assertFalse(CredentialPojoUtils.isSelectivelyDisclosed(credential.getSalt()));
        logger.info(DataToolUtils.serialize(credential));
        CredentialPojo nonSdCredential =
            credentialPojoService.createSelectiveCredential(credential,
                CredentialPojoUtils.generateNonDisclosedPolicy(credential)).getResult();
        Assert.assertNotNull(nonSdCredential);
        Assert.assertTrue(CredentialPojoUtils.isSelectivelyDisclosed(nonSdCredential.getSalt()));
        logger.info(DataToolUtils.serialize(nonSdCredential));
        Assert.assertTrue(CredentialPojoUtils
            .getCredentialThumbprintWithoutSig(credential, credential.getSalt(), null)
            .equalsIgnoreCase(CredentialPojoUtils
                .getCredentialThumbprintWithoutSig(nonSdCredential, nonSdCredential.getSalt(),
                    null)));
    }

    /**
     * case：when issuer and cpt publisher is same,createCredentialPojo success.
     */
    @Test
    public void testCreateMultiSignSdCredentialPojo_success() {
        List<CredentialPojo> credPojoList = new ArrayList<>();
        credPojoList.add(selectiveCredentialPojo);
        CreateWeIdDataResult weIdResult = createWeIdWithSetAttr();
        WeIdAuthentication callerAuth = TestBaseUtil.buildWeIdAuthentication(weIdResult);
        CredentialPojo doubleSigned = credentialPojoService.addSignature(credPojoList, callerAuth)
            .getResult();
        Assert.assertEquals(doubleSigned.getCptId(),
            CredentialConstant.CREDENTIALPOJO_EMBEDDED_SIGNATURE_CPT);

        ResponseData<Boolean> verifyResp = credentialPojoService
            .verify(doubleSigned.getIssuer(), doubleSigned);
        Assert.assertTrue(verifyResp.getResult());
        credPojoList = new ArrayList<>();
        credPojoList.add(doubleSigned);
        CredentialPojo tripleSigned = credentialPojoService.addSignature(credPojoList, callerAuth)
            .getResult();
        verifyResp = credentialPojoService.verify(doubleSigned.getIssuer(), tripleSigned);
        Assert.assertTrue(verifyResp.getResult());
    }

    @Test
    public void testMultiSignPojo_fromToJson_ReplaceInnerCredential() throws Exception {
        List<CredentialPojo> credPojoList = new ArrayList<>();
        credPojoList.add(selectiveCredentialPojo);
        credPojoList.add(credentialPojo);
        WeIdAuthentication callerAuth = TestBaseUtil
            .buildWeIdAuthentication(createWeIdResultWithSetAttr);
        CredentialPojo doubleSigned =
            credentialPojoService.addSignature(credPojoList, callerAuth).getResult();
        System.out.println("A part: " + CredentialPojoUtils
            .getEmbeddedCredentialThumbprintWithoutSig(credPojoList));
        String serializedjson = doubleSigned.toJson();
        System.out.println("A is: " + serializedjson);
        CredentialPojo cpj = CredentialPojo.fromJson(serializedjson);
        Assert.assertTrue(CredentialPojoUtils.isEqual(cpj, doubleSigned));

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":0,\"gender\":0,\"age\":0,\"id\":0}");
        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(credentialPojo, claimPolicy);
        CredentialPojo tempPojo = response.getResult();
        credPojoList = new ArrayList<>();
        credPojoList.add(selectiveCredentialPojo);
        credPojoList.add(tempPojo);
        System.out.println("B part: " + CredentialPojoUtils
            .getEmbeddedCredentialThumbprintWithoutSig(credPojoList));
        CredentialPojo modifiedDoubleSigned = copyCredentialPojo(doubleSigned);
        Map<String, Object> modifiedClaim = new HashMap<>();
        modifiedClaim.put("credentialList", credPojoList);
        modifiedDoubleSigned.setClaim(modifiedClaim);
        serializedjson = modifiedDoubleSigned.toJson();
        System.out.println("B is: " + serializedjson);
        Assert.assertTrue(
            credentialPojoService.verify(modifiedDoubleSigned.getIssuer(), modifiedDoubleSigned)
                .getResult());
    }

    @Test
    public void testMultiSignPojo_sedeserialize() throws Exception {
        List<CredentialPojo> credPojoList = new ArrayList<>();
        credPojoList.add(selectiveCredentialPojo);
        credPojoList.add(credentialPojo);
        WeIdAuthentication callerAuth = TestBaseUtil
            .buildWeIdAuthentication(createWeIdResultWithSetAttr);
        CredentialPojo doubleSigned =
            credentialPojoService.addSignature(credPojoList, callerAuth).getResult();
        System.out.println(doubleSigned);
        String serializedjson = DataToolUtils.serialize(doubleSigned);
        System.out.println(serializedjson);
        CredentialPojo testcpj = DataToolUtils.deserialize(serializedjson, CredentialPojo.class);
        System.out.println(testcpj);
        Assert.assertTrue(CredentialPojoUtils.isEqual(doubleSigned, testcpj));
    }

    /**
     * case：createSelectiveCredential fail.
     */
    @Test
    public void testCreateSelectiveCredential_verifyClaimId() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> claimMap = copyCredentialPojo.getClaim();
        claimMap.remove("id");
        claimMap.put("id", "did:weid:101:0x39e5e6f663ef77409144014ceb063713b6112121");

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialPojoService
            .verify(copyCredentialPojo.getIssuer(), copyCredentialPojo);
        Assert.assertFalse(verify.getResult());
    }

    /**
     * case：createSelectiveCredential success.
     */
    @Test
    public void testCreateSelectiveCredential_notVerifyClaimId() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> claimMap = copyCredentialPojo.getClaim();
        claimMap.remove("id");
        claimMap.put("id", "did:weid:101:0x39e5e6f663ef77409144014ceb063713b6112121");

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":0}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case: createSelectiveCredential repeat success.
     */
    @Test
    public void testCreateSelectiveCredential_repeatSucess() {
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");
        Map<String, Object> claimMap = credentialPojo.getClaim();

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(credentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotEquals(
            claimMap.get("gender"), response.getResult().getClaim().get("gender"));

        ResponseData<CredentialPojo> response1 =
            credentialPojoService.createSelectiveCredential(credentialPojo, claimPolicy);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertNotEquals(
            claimMap.get("gender"), response1.getResult().getClaim().get("gender"));
    }

    /**
     * case：context contain any string.
     */
    @Test
    public void testCreateSelectiveCredential_contextContainsAnyStr() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");
        copyCredentialPojo.setContext("~!@#$%^&*()_中国123we");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

    }

    /**
     * case：context is null.
     */
    @Test
    public void testCreateSelectiveCredential_contextNull() {

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setContext(null);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CONTEXT_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case：context is blank.
     */
    @Test
    public void testCreateSelectiveCredential_contextBlank() {

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setContext("");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CONTEXT_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case：context is too long .
     */
    @Test
    public void testCreateSelectiveCredential_contextTooLong() {

        char[] chars = new char[1000];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (i % 127);
        }
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setContext(String.valueOf(chars));
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case：type contain any string.
     */
    @Test
    public void testCreateSelectiveCredential_typeContainsAnyStr() {

        List<String> stringList = new ArrayList<>();
        stringList.add("你好哈");
        stringList.add("!@#$%^&*(*)/.,><;lkjhg");
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setType(stringList);
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case：type is null.
     */
    @Test
    public void testCreateSelectiveCredential_typeNull() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setType(null);
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_TYPE_IS_NULL.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());


    }

    /**
     * case：type is blank.
     */
    @Test
    public void testCreateSelectiveCredential_typeBlank() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setType(new ArrayList<>());
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_TYPE_IS_NULL.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case：type is too long.
     */
    @Test
    public void testCreateSelectiveCredential_typeTooLong() {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String s = "ah" + Math.random();
            stringList.add(s);
        }
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setType(stringList);

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case：cptid not exist.
     */
    @Test
    public void testCreateSelectiveCredential_cptIdNotExist() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setCptId(Integer.MAX_VALUE);
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialPojoService
            .verify(copyCredentialPojo.getIssuer(), copyCredentialPojo);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode(), verify.getErrorCode().intValue());
        Assert.assertFalse(verify.getResult());
    }

    /**
     * case：credentialPojo.issuer is no exist.
     */
    @Test
    public void testCreateSelectiveCredential_issuerNotExist() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        String weId = createWeId().getWeId();
        weId = weId.replace(weId.substring(weId.length() - 4, weId.length()), "ffff");
        copyCredentialPojo.setIssuer(weId);
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialPojoService
            .verify(copyCredentialPojo.getIssuer(), response.getResult());
        Assert.assertEquals(ErrorCode.WEID_DOES_NOT_EXIST.getCode(),
            verify.getErrorCode().intValue());
        Assert.assertFalse(verify.getResult());
    }


    /**
     * case：credentialPojo.issuer is a invalid issuer.
     */
    @Test
    public void testCreateSelectiveCredential_invalidIssuer() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        String weId = createWeId().getWeId();
        weId = weId.replace(weId.substring(weId.length() - 4, weId.length()),
            DateUtils.getNoMillisecondTimeStampString());
        copyCredentialPojo.setIssuer(weId);
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case：issuer is null.
     */
    @Test
    public void testCreateSelectiveCredential_issuerNull() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setIssuer(null);
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case：issuer is blank.
     */
    @Test
    public void testCreateSelectiveCredential_issuerBlank() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setIssuer("");
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case：expirationDatePassed.
     */
    @Test
    public void testCreateSelectiveCredential_expirationDatePassed() {

        long currentDate = System.currentTimeMillis();
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setExpirationDate(currentDate - 1000);
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case：IssuanceDate has been passed.
     */
    @Test
    public void testCreateSelectiveCredential_IssuanceDatePassed() {
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        long currentDate = System.currentTimeMillis();
        copyCredentialPojo.setIssuanceDate(currentDate - 10000);
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case：IssuanceDate bigger than expirationDate.
     */
    @Test
    public void testCreateSelectiveCredential_expirationed() {
        long currentDate = System.currentTimeMillis();
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setIssuanceDate(currentDate + 20000);
        copyCredentialPojo.setExpirationDate(currentDate + 10000);
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case：claim is null.
     */
    @Test
    public void testCreateSelectiveCredential_claimNull() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setClaim(null);
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case：claim is blank.
     */
    @Test
    public void testCreateSelectiveCredential_claimBlank() {

        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setClaim(new HashMap<>());
        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_POLICY_FORMAT_DOSE_NOT_MATCH_CLAIM.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case：claim policy contain key that claim not have.
     */
    @Test
    public void testCreateSelectiveCredential_claimPolicyMore() {

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed(
            "{\"name\":1,\"gender\":0,\"age\":1,\"id\":1,\"birthday\":1986}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(credentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_POLICY_FORMAT_DOSE_NOT_MATCH_CLAIM.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case：claim contain keys but claim policy not.
     */
    @Test
    public void testCreateSelectiveCredential_claimPolicyLess() {

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(credentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case：bad claim policy .
     */
    @Test
    public void testCreateSelectiveCredential_badClaimPolicy() {

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(credentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_DISCLOSURE_DATA_TYPE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case：claim policy key contains twice .
     */
    @Test
    public void testCreateSelectiveCredential_claimPolicyRepeat() {

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(credentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case：claim policy value is null.
     */
    @Test
    public void testCreateSelectiveCredential_claimPolicyContainsNull() {

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":0,\"age\":1,\"id\":null}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(credentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_POLICY_DISCLOSUREVALUE_ILLEGAL.getCode(),
            response.getErrorCode().intValue()
        );
        Assert.assertNull(response.getResult());
    }

    /**
     * case：claim policy all open .
     */
    @Test
    public void testCreateSelectiveCredential_claimPolicyAllOpen() {

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":1,\"gender\":1,\"age\":1,\"id\":1}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(credentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case：claim policy all close .
     */
    @Test
    public void testCreateSelectiveCredential_claimPolicyAllClose() {

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":0,\"gender\":0,\"age\":0,\"id\":0}");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(credentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case:proof is null.
     */
    @Test
    public void testCreateSelectiveCredential_proofNull() {

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":0,\"gender\":0,\"age\":0,\"id\":0}");
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setProof(null);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case:proof is blank.
     */
    @Test
    public void testCreateSelectiveCredential_proofBlank() {

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":0,\"gender\":0,\"age\":0,\"id\":0}");
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        copyCredentialPojo.setProof(new HashMap<>());

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_SIGNATURE_TYPE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case:proof is blank.
     */
    @Test
    public void testCreateSelectiveCredential_proofRemoveCreatorSucess() {

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":0,\"gender\":0,\"age\":0,\"id\":0}");
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> proofMap = copyCredentialPojo.getProof();
        proofMap.remove("creator");
        copyCredentialPojo.setProof(proofMap);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case:proof is blank.
     */
    @Test
    public void testCreateSelectiveCredential_proofRemoveSalt() {

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":0,\"gender\":0,\"age\":0,\"id\":0}");
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> proofMap = copyCredentialPojo.getProof();
        proofMap.remove("salt");
        copyCredentialPojo.setProof(proofMap);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_POLICY_FORMAT_DOSE_NOT_MATCH_CLAIM.getCode(),
            response.getErrorCode().intValue()
        );
        Assert.assertNull(response.getResult());
    }

    /**
     * case:proof is blank.
     */
    @Test
    public void testCreateSelectiveCredential_proofRemoveSignature() {

        ClaimPolicy claimPolicy = new ClaimPolicy();
        claimPolicy.setFieldsToBeDisclosed("{\"name\":0,\"gender\":0,\"age\":0,\"id\":0}");
        CredentialPojo copyCredentialPojo = copyCredentialPojo(credentialPojo);
        Map<String, Object> proofMap = copyCredentialPojo.getProof();
        proofMap.remove("signatureValue");
        copyCredentialPojo.setProof(proofMap);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createSelectiveCredential(copyCredentialPojo, claimPolicy);
        LogUtil.info(logger, "TestCreateSelectiveCredential", response);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

}
