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
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.CreateCredentialPojoArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.DateUtils;

/**
 * createCredential method for testing CredentialService.
 *
 * @author v_wbgyang
 */
public class TestCreateCredentialPojo extends TestBaseServcie {

    private static final Logger logger = LoggerFactory.getLogger(TestCreateCredentialPojo.class);

    @Override
    public synchronized void testInit() {

        super.testInit();
        if (cptBaseInfo == null) {
            cptBaseInfo = super.registerCpt(createWeIdResultWithSetAttr);
            Assert.assertTrue(cptBaseInfo.getCptId() > 2000000);
        }
    }

    /**
     * case：when issuer and cpt publisher is same,createCredentialPojo success.
     */
    @Test
    public void testCreateCredentialPojo_success() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialPojoService.verify(
            createCredentialPojoArgs.getIssuer(), response.getResult());
        Assert.assertTrue(verify.getResult());
    }

    /**
     * case：when issuer and cpt publisher is same,createCredentialPojo success.
     */
    @Test
    public void testCreateMultiSignCredentialPojo_success() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        ResponseData<CredentialPojo> credResp1 =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", credResp1);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), credResp1.getErrorCode().intValue());
        ResponseData<Boolean> verify = credentialPojoService.verify(
            createCredentialPojoArgs.getIssuer(), credResp1.getResult());
        Assert.assertTrue(verify.getResult());

        CreateWeIdDataResult weIdResult2 = createWeIdWithSetAttr();
        createCredentialPojoArgs = TestBaseUtil.buildCreateCredentialPojoArgs(weIdResult2);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        Long expirationDate = DateUtils.convertToNoMillisecondTimeStamp(
            createCredentialPojoArgs.getExpirationDate() + 24 * 60 * 60);
        createCredentialPojoArgs.setExpirationDate(expirationDate);
        ResponseData<CredentialPojo> credResp2 =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", credResp2);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), credResp2.getErrorCode().intValue());
        verify = credentialPojoService.verify(
            createCredentialPojoArgs.getIssuer(), credResp2.getResult());
        Assert.assertTrue(verify.getResult());

        List<CredentialPojo> credPojoList = new ArrayList<>();
        credPojoList.add(credResp1.getResult());
        credPojoList.add(credResp2.getResult());
        CreateWeIdDataResult weIdResult3 = createWeIdWithSetAttr();
        WeIdAuthentication callerAuth = TestBaseUtil.buildWeIdAuthentication(weIdResult3);
        CredentialPojo doubleSigned = credentialPojoService.addSignature(credPojoList, callerAuth)
            .getResult();
        Assert.assertEquals(doubleSigned.getCptId(),
            CredentialConstant.CREDENTIALPOJO_EMBEDDED_SIGNATURE_CPT);
        Assert.assertEquals(doubleSigned.getExpirationDate(), expirationDate);
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

    /**
     * case：when issuer is register authentication issuer， cpt publisher is not auth issuer,
     * createCredentialPojo success.
     */
    @Test
    public void testCreateCredentialPojo_authenticationIssuerSuccess() {

        CreateWeIdDataResult createWeIdDataResult = super.createWeId();
        super.registerAuthorityIssuer(createWeIdDataResult);
        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdDataResult);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialPojoService.verify(
            createCredentialPojoArgs.getIssuer(), response.getResult());
        Assert.assertTrue(verify.getResult());
    }

    /**
     * case：cpt publisher is auth issuer, createCredentialPojo success.
     */
    @Test
    public void testCreateCredentialPojo_authenticationCptSuccess() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);

        CreateWeIdDataResult createWeIdDataResult = super.createWeId();
        super.registerAuthorityIssuer(createWeIdDataResult);
        CptBaseInfo cptBaseInfo = super.registerCpt(createWeIdDataResult);
        Assert.assertTrue(cptBaseInfo.getCptId() < 2000000);

        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialPojoService.verify(
            createCredentialPojoArgs.getIssuer(), response.getResult());
        Assert.assertTrue(verify.getResult());
    }

    /**
     * case：repeat createCredentialPojo success.
     */
    @Test
    public void testCreateCredentialPojo_repeatSuccess() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        ResponseData<CredentialPojo> response1 =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response1);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertNotNull(response1.getResult());
    }

    /**
     * case：createCredential success refer to doc.
     */
    @Test
    public void testCreateCredentialPojo_sampleFail() {

        HashMap<String, Object> claim = new HashMap<>();
        claim.put("student", new CptBaseInfo());
        claim.put("name", "李白");
        claim.put("age", 1300);
        claim.put("poiet", "桃花潭水深千尺，不及汪伦送我情");
        CreateCredentialPojoArgs createCredentialArgs = new CreateCredentialPojoArgs();
        createCredentialArgs.setClaim(claim);

        createCredentialArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialArgs.setExpirationDate(System.currentTimeMillis() + 6000);
        createCredentialArgs.setIssuer(createWeIdResultWithSetAttr.getWeId());

        WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
        weIdAuthentication.setWeId(createWeIdResultWithSetAttr.getWeId());
        weIdAuthentication.setWeIdPublicKeyId(
            createWeIdResultWithSetAttr.getUserWeIdPublicKey().getPublicKey());
        weIdAuthentication.setWeIdPrivateKey(
            createWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        createCredentialArgs.setWeIdAuthentication(weIdAuthentication);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialPojoService.verify(
            createCredentialArgs.getIssuer(), response.getResult());
        LogUtil.info(logger, "verify", verify);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL.getCode(), verify.getErrorCode().intValue());
        Assert.assertFalse(verify.getResult());
    }

    /**
     * case：when weIdAuthentication is null,createCredentialPojo return ILLEGAL_INPUT.
     */
    @Test
    public void testCreateCredentialPojo_weIdAuthenticationNull() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialPojoArgs.setWeIdAuthentication(new WeIdAuthentication());

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case: createCredentialPojoArgs is null,return ILLEGAL_INPUT.
     */
    @Test
    public void testCreateCredentialPojo_credentialArgsNull() {

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(null);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case：cptId is null,return ILLEGAL_INPUT .
     */
    @Test
    public void testCreateCredential_cptIdNull() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.CPT_ID_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptId is minus number,return CREDENTIAL_CPT_NOT_EXISTS.
     */
    @Test
    public void testCreateCredential_cptIdMinus() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(-1);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.CPT_ID_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
    }

    /**
     * case： cptId is not exists,createCredentialPojo success but verify fail.
     */
    @Test
    public void testCreateCredential_cptIdNotExist() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(999999999);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialPojoService.verify(
            createCredentialPojoArgs.getIssuer(), response.getResult());
        LogUtil.info(logger, "verify createCredentialPojo", verify);
        Assert.assertEquals(ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode(),
            verify.getErrorCode().intValue());
        Assert.assertFalse(verify.getResult());
    }

    /**
     * case： cptId is belongs to others weIdentity dId.
     */
    @Test
    public void testCreateCredential_otherCptIdSuccess() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        CptBaseInfo cptBaseInfoNew = super.registerCpt(createWeIdNew);
        createCredentialPojoArgs.setCptId(cptBaseInfoNew.getCptId());

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialPojoService.verify(
            createCredentialPojoArgs.getIssuer(), response.getResult());
        Assert.assertTrue(verify.getResult());
    }

    /**
     * case： issuer is null.
     */
    @Test
    public void testCreateCredential_issuerNull() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialPojoArgs.setIssuer(null);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： issuer is invalid.
     */
    @Test
    public void testCreateCredential_invalidIssuer() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialPojoArgs.setIssuer("di:weid:0x11!@#$%^&*()_+zhon 中国》《？12qwe");

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： issuer and private key not match.
     */
    @Test
    public void testCreateCredential_priKeyNotMatch() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        WeIdAuthentication weIdAuthentication = createCredentialPojoArgs.getWeIdAuthentication();
        weIdAuthentication.setWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： private key is sdk private key.
     */
    @Test
    public void testCreateCredential_sdkPriKey() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        WeIdAuthentication weIdAuthentication = createCredentialPojoArgs.getWeIdAuthentication();
        WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
        weIdPrivateKey.setPrivateKey(privateKey);
        weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }


    /**
     * case: issuance date = 0,create success and verify success.
     */
    @Test
    public void createCredential_withIssuanceZero() {
        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialPojoArgs.setIssuanceDate(0L);

        ResponseData<CredentialPojo> response = credentialPojoService
            .createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case:issuance date data is minus number.
     */
    @Test
    public void testCreateCredential_issuanceDateMinus() {
        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialPojoArgs.setIssuanceDate(-1L);

        ResponseData<CredentialPojo> response = credentialPojoService
            .createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
    }

    /**
     * case: issuance date is a big integer.
     */
    @Test
    public void testCreateCredential_bigIssuanceDate() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialPojoArgs.setIssuanceDate(Long.MAX_VALUE);

        ResponseData<CredentialPojo> response = credentialPojoService
            .createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
    }

    /**
     * case:issuance date is Long.MIN_VALUE
     */
    @Test
    public void testCreateCredential_minIsuanceDate() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs
            = TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialPojoArgs.setIssuanceDate(Long.MIN_VALUE);

        ResponseData<CredentialPojo> response = credentialPojoService
            .createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： expirationDate <= 0.
     */
    @Test
    public void testCreateCredential_minusExpirationDate() {

        CreateCredentialPojoArgs createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setExpirationDate(0L);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： expirationDate <= now.
     */
    @Test
    public void testCreateCredential_passedExpirationDate() {

        CreateCredentialPojoArgs createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setExpirationDate(System.currentTimeMillis() - 1000000);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case:expiration date is null.
     */
    @Test
    public void testCreateCredential_expirationDateNull() {
        CreateCredentialPojoArgs createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialPojoArgs.setExpirationDate(null);

        ResponseData<CredentialPojo> response = credentialPojoService
            .createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： claim is null.
     */
    @Test
    public void testCreateCredential_claimNull() {

        CreateCredentialPojoArgs createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialPojoArgs.setClaim(null);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： claim is xxxxxxx.
     */
    @Test
    public void testCreateCredential_invalidClaim() {

        CreateCredentialPojoArgs createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        HashMap<String, Object> claim = new HashMap<>();
        claim.put("xxxxxxxxxxxxxx", "xxxxxxxxxxxxxx");
        createCredentialPojoArgs.setClaim(claim);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialPojoService
            .verify(createCredentialPojoArgs.getIssuer(), response.getResult());
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL.getCode(), verify.getErrorCode().intValue());
        Assert.assertFalse(verify.getResult());
    }

    /**
     * case:claim is a json.
     */
    @Test
    public void testCreateCredential_jsonClaim() {

        CreateCredentialPojoArgs<Map<String, Object>> createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        Map<String, Object> claim = new HashMap<>();
        claim.put("name", 20);
        createCredentialPojoArgs.setClaim(claim);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialPojoService
            .verify(createCredentialPojoArgs.getIssuer(), response.getResult());
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL.getCode(), verify.getErrorCode().intValue());
        Assert.assertFalse(verify.getResult());
    }

    /**
     * case： weIdPrivateKey is null.
     */
    @Test
    public void testCreateCredential_priKeyNull() {

        CreateCredentialPojoArgs createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
        weIdAuthentication.setWeId(createWeIdResultWithSetAttr.getWeId());
        weIdAuthentication.setWeIdPublicKeyId(
            createWeIdResultWithSetAttr.getUserWeIdPublicKey().getPublicKey());
        createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： publicKey is null.
     */
    @Test
    public void testCreateCredential_pubKeyNull() {

        CreateCredentialPojoArgs createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
        weIdAuthentication.setWeId(createWeIdResultWithSetAttr.getWeId());
        weIdAuthentication.setWeIdPrivateKey(createWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        weIdAuthentication.setWeIdPublicKeyId(
            createWeIdResultWithSetAttr.getUserWeIdPublicKey().getPublicKey() + "56");
        createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialPojoService
            .verify(createCredentialPojoArgs.getIssuer(), response.getResult());
        Assert.assertTrue(verify.getResult());

    }

    /**
     * case： issuer and weIdAuthentication.weId is different,createCredentialPojo fail.
     */
    @Test
    public void testCreateCredential_otherWeId() {

        CreateCredentialPojoArgs createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
        weIdAuthentication.setWeId(createWeIdNew.getWeId());
        weIdAuthentication.setWeIdPublicKeyId(createWeIdNew.getUserWeIdPublicKey().getPublicKey());
        weIdAuthentication.setWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());
        createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
    }

    /**
     * case： privateKey is issuer private key.
     */
    @Test
    public void testCreateCredential_issuerPriKey() {

        CreateCredentialPojoArgs createCredentialPojoArgs =
            TestBaseUtil.buildCreateCredentialPojoArgs(createWeIdResultWithSetAttr);
        createCredentialPojoArgs.setCptId(cptBaseInfo.getCptId());
        WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
        weIdAuthentication.setWeId(createWeIdNew.getWeId());
        weIdAuthentication.setWeIdPrivateKey(createWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        createCredentialPojoArgs.setWeIdAuthentication(weIdAuthentication);

        ResponseData<CredentialPojo> response =
            credentialPojoService.createCredential(createCredentialPojoArgs);
        LogUtil.info(logger, "createCredentialPojo", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(),
            response.getErrorCode().intValue());
    }
}
