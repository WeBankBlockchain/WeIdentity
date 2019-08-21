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

package com.webank.weid.full.credential;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.CredentialWrapper;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * createCredential method for testing CredentialService.
 *
 * @author v_wbgyang
 */
public class TestCreateCredential extends TestBaseServcie {

    private static final Logger logger = LoggerFactory.getLogger(TestCreateCredential.class);

    @Override
    public synchronized void testInit() {
        super.testInit();
        if (cptBaseInfo == null) {
            cptBaseInfo = super.registerCpt(createWeIdResultWithSetAttr);
        }
    }

    /**
     * case：cptjsonshema and claim is same,createCredential success.
     */
    @Test
    public void testCreateCredential_success() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        System.out.println(createCredentialArgs);

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case：when cptjsonschema is different ,createCredential success but verify fail.
     */
    @Test
    public void testCreateCredential_sampleSuccess() {

        HashMap<String, Object> claim = new HashMap<>();
        claim.put("student", new CptBaseInfo());
        claim.put("name", "李白");
        claim.put("age", 1300);
        claim.put("poiet", "桃花潭水深千尺，不及汪伦送我情");

        CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
        createCredentialArgs.setClaim(claim);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialArgs.setExpirationDate(System.currentTimeMillis() + 6000);
        createCredentialArgs.setIssuer(createWeIdResultWithSetAttr.getWeId());
        createCredentialArgs.setWeIdPrivateKey(createWeIdResultWithSetAttr.getUserWeIdPrivateKey());

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialService.verify(response.getResult());
        Assert.assertEquals(ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL.getCode(),
            verify.getErrorCode().intValue());
    }

    /**
     * case：when cptJsonSchema and claim is different ,createCredential success but verify fail.
     */
    @Test
    public void testCreateCredential_claimIsSubsetOfCptJsonSchema() {

        HashMap<String, Object> claim = new HashMap<>();
        claim.put("id", createWeIdResultWithSetAttr.getWeId());
        claim.put("name", "李白");
        claim.put("age", 1300);

        CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
        createCredentialArgs.setClaim(claim);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialArgs.setExpirationDate(System.currentTimeMillis() + 6000);
        createCredentialArgs.setIssuer(createWeIdResultWithSetAttr.getWeId());
        createCredentialArgs.setWeIdPrivateKey(createWeIdResultWithSetAttr.getUserWeIdPrivateKey());

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialService.verify(response.getResult());
        Assert.assertEquals(ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL.getCode(),
            verify.getErrorCode().intValue());
    }

    /**
     * case：when cptJsonSchema and claim is different ,createCredential success but verify fail.
     */
    @Test
    public void testCreateCredential_cptJsonIsSubsetOfClaim() {

        HashMap<String, Object> claim = new HashMap<>();
        claim.put("id", createWeIdResultWithSetAttr.getWeId());
        claim.put("name", "李白");
        claim.put("age", 1300);
        claim.put("gender", "F");
        claim.put("city", "changan");

        CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
        createCredentialArgs.setClaim(claim);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialArgs.setExpirationDate(System.currentTimeMillis() + 1000 * 60);
        createCredentialArgs.setIssuer(createWeIdResultWithSetAttr.getWeId());
        createCredentialArgs.setWeIdPrivateKey(createWeIdResultWithSetAttr.getUserWeIdPrivateKey());

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialService.verify(response.getResult());
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), verify.getErrorCode().intValue());
    }

    /**
     * case：when claim and cptJsonSchema key is same but type of value is different,
     * createCredential success but verify fail.
     */
    @Test
    public void testCreateCredential_claimKeyMatchButTypeDifferent() {

        HashMap<String, Object> claim = new HashMap<>();
        claim.put("id", createWeIdResultWithSetAttr.getWeId());
        claim.put("name", "李白");
        claim.put("age", 1300);
        claim.put("gender", "FM");

        CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
        createCredentialArgs.setClaim(claim);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());
        createCredentialArgs.setExpirationDate(System.currentTimeMillis() + 6000);
        createCredentialArgs.setIssuer(createWeIdResultWithSetAttr.getWeId());
        createCredentialArgs.setWeIdPrivateKey(createWeIdResultWithSetAttr.getUserWeIdPrivateKey());

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialService.verify(response.getResult());
        Assert.assertEquals(ErrorCode.CREDENTIAL_CLAIM_DATA_ILLEGAL.getCode(),
            verify.getErrorCode().intValue());
    }

    /**
     * case: createCredentialArgs is null.
     */
    @Test
    public void testCreateCredential_credentialArgsNull() {
        CreateCredentialArgs createCredentialArgs = null;
        ResponseData<CredentialWrapper> response = credentialService
            .createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case：cptId is null.
     */
    @Test
    public void testCreateCredential_cptIdNull() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        createCredentialArgs.setCptId(null);

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.CPT_ID_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptId is minus number.
     */
    @Test
    public void testCreateCredential_cptIdMinus() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        createCredentialArgs.setCptId(-1);

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.CPT_ID_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
    }

    /**
     * case： cptId is not exists.
     */
    @Test
    public void testCreateCredential_cptIdNotExist() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        createCredentialArgs.setCptId(999999999);

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialService.verify(response.getResult());
        Assert.assertEquals(ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode(),
            verify.getErrorCode().intValue());
    }

    /**
     * case： cptId is belongs to others weIdentity dId.
     */
    @Test
    public void testCreateCredential_otherCptIdSuccess() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);

        CptBaseInfo cptBaseInfoNew = super.registerCpt(createWeIdNew);
        createCredentialArgs.setCptId(cptBaseInfoNew.getCptId());

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialService.verify(response.getResult());
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(),
            verify.getErrorCode().intValue());
    }

    /**
     * case： issuer is null.
     */
    @Test
    public void testCreateCredential_issuerNull() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        createCredentialArgs.setIssuer(null);

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： issuer is invalid.
     */
    @Test
    public void testCreateCredential_invalidIssuer() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        createCredentialArgs.setIssuer("di:weid:0x11!@#$%^&*()_+zhon 中国》《？12qwe");

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： issuer and private key not match.
     */
    @Test
    public void testCreateCredential_priKeyNotMatch() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        createCredentialArgs.setWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        ResponseData<Boolean> verify = credentialService.verify(response.getResult());
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            verify.getErrorCode().intValue());
    }

    /**
     * case： private key is sdk private key.
     */
    @Test
    public void testCreateCredential_sdkPriKey() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
        weIdPrivateKey.setPrivateKey(privateKey);
        createCredentialArgs.setWeIdPrivateKey(weIdPrivateKey);

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());

        ResponseData<Boolean> verify = credentialService.verify(response.getResult());
        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            verify.getErrorCode().intValue());
    }


    /**
     * case: test issuance date changes.
     */
    @Test
    public void createCredentialWithIssuanceDateTest() {
        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        createCredentialArgs.setIssuanceDate(0L);
        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： expirationDate <= 0.
     */
    @Test
    public void testCreateCredential_expirationDateIsZero() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        createCredentialArgs.setExpirationDate(0L);

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： expirationDate <= now.
     */
    @Test
    public void testCreateCredential_expirationDatePassed() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        createCredentialArgs.setExpirationDate(System.currentTimeMillis() - 1000000);

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_EXPIRED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： claim is null. 
     */
    @Test
    public void testCreateCredential_claimNull() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        createCredentialArgs.setClaim(null);

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： weIdPrivateKey is null.
     */
    @Test
    public void testCreateCredential_priKeyNull() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        createCredentialArgs.setWeIdPrivateKey(null);

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： privateKey is null.
     */
    @Test
    public void testCreateCredential_priKeyNull2() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(null);

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： privateKey is xxxxxxxxxxx.
     */
    @Test
    public void testCreateCredential_invalidPriKey() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        createCredentialArgs.getWeIdPrivateKey().setPrivateKey("xxxxxxxxxx");

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： privateKey is 11111111111111.
     */
    @Test
    public void testCreateCredential_priKeyIsInt() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr, cptBaseInfo);
        createCredentialArgs.getWeIdPrivateKey().setPrivateKey("11111111111111");

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }
}
