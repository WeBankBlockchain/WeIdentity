

package com.webank.weid.full.credentialpojo;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.common.PasswordKey;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.CredentialWrapper;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.blockchain.protocol.response.ResponseData;

/**
 * verifyCredentialWithSpecifiedPubKey method for testing CredentialService.
 *
 * @author v_wbgyang
 */
public class TestVerifyCredentialWithSpecifiedPubKey extends TestBaseService {

    private static final Logger logger =
        LoggerFactory.getLogger(TestVerifyCredentialWithSpecifiedPubKey.class);

    protected PasswordKey newPasswordKey = null;

    @Override
    public synchronized void testInit() {
        super.testInit();
        newPasswordKey = TestBaseUtil.createEcKeyPair();
    }

    /**
     * case: verifyCredentialWithSpecifiedPubKey success.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase1() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(newPasswordKey.getPrivateKey());

        CredentialWrapper credentialWrapper = super.createCredential(createCredentialArgs);

        WeIdPublicKey weIdPublicKey = new WeIdPublicKey();
        weIdPublicKey.setPublicKey(newPasswordKey.getPublicKey());
        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(credentialWrapper, weIdPublicKey);
        LogUtil.info(logger, "verifyCredentialWithSpecifiedPubKey", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: verifyCredentialWithSpecifiedPubKey fail.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase2() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(newPasswordKey.getPrivateKey());

        CredentialWrapper credentialWrapper = super.createCredential(createCredentialArgs);

        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();
        WeIdPublicKey weIdPublicKey = new WeIdPublicKey();
        weIdPublicKey.setPublicKey(passwordKey.getPublicKey());

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(credentialWrapper, weIdPublicKey);
        LogUtil.info(logger, "verifyCredentialWithSpecifiedPubKey", response);

        Assert.assertTrue(ErrorCode.CREDENTIAL_SIGNATURE_BROKEN.getCode()
            == response.getErrorCode().intValue() || ErrorCode.CREDENTIAL_VERIFY_FAIL.getCode()
            == response.getErrorCode().intValue() || response.getErrorCode().intValue()
            == ErrorCode.CREDENTIAL_EXCEPTION_VERIFYSIGNATURE.getCode());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: verifyCredentialWithSpecifiedPubKey publicKey is null.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase3() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(newPasswordKey.getPrivateKey());

        CredentialWrapper credentialWrapper = super.createCredential(createCredentialArgs);

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(credentialWrapper, null);
        LogUtil.info(logger, "verifyCredentialWithSpecifiedPubKey", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: verifyCredentialArgs is null.
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase4() {

        WeIdPublicKey weIdPublicKey = new WeIdPublicKey();
        weIdPublicKey.setPublicKey(newPasswordKey.getPublicKey());
        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(null, weIdPublicKey);
        LogUtil.info(logger, "verifyCredentialWithSpecifiedPubKey", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }


    /**
     * case: verifyCredentialWithSpecifiedPubKey publicKey is "xxxxxxxxxxxxx".
     */
    @Test
    public void testVerifyCredentialWithSpecifiedPubKeyCase7() {

        CreateCredentialArgs createCredentialArgs =
            TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());

        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(newPasswordKey.getPrivateKey());

        CredentialWrapper credentialWrapper = super.createCredential(createCredentialArgs);

        WeIdPublicKey weIdPublicKey = new WeIdPublicKey();
        weIdPublicKey.setPublicKey("xxxxxxxxxxxxx");

        ResponseData<Boolean> response =
            credentialService.verifyCredentialWithSpecifiedPubKey(credentialWrapper, weIdPublicKey);
        LogUtil.info(logger, "verifyCredentialWithSpecifiedPubKey", response);

        Assert.assertEquals(ErrorCode.CREDENTIAL_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
