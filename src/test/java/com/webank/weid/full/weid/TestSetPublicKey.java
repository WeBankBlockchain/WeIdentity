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

package com.webank.weid.full.weid;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.common.PasswordKey;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;

/**
 * setPublicKey method for testing WeIdService.
 *
 * @author v_wbgyang
 */
public class TestSetPublicKey extends TestBaseServcie {

    private static final Logger logger = LoggerFactory.getLogger(TestSetPublicKey.class);

    /**
     * case: set one public key for authentication success.
     */
    @Test
    public void testSetPublicKey_oneAuthPubKeySuccess() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: WeIdentity DID is blank.
     */
    @Test
    public void testSetPublicKey_weIdIsNull() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setWeId(null);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is bad format.
     */
    @Test
    public void testSetPublicKey_badFormat() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        System.out.println(setPublicKeyArgs);
        setPublicKeyArgs.setWeId("di:weid:0xbbd97a63365b6c9fb6b011a8d294307a3b7dac73");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is not exists.
     */
    @Test
    public void testSetPublicKeyCase4() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setWeId("did:weid:0aaaaaaaaaaaa");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: publicKey is a new key.
     *
     */
    @Test
    public void testSetPublicKey_newKey() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();

        setPublicKeyArgs.setPublicKey(passwordKey.getPublicKey());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: publicKey is null.
     */
    @Test
    public void testSetPublicKey_pubKeyIsNull() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setPublicKey(null);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: publicKey is blank.
     */
    @Test
    public void testSetPublicKey_pubKeyIsBlank() {

        SetPublicKeyArgs setPublicKeyArgs =
            TestBaseUtil.buildSetPublicKeyArgs(createWeIdResultWithSetAttr);
        setPublicKeyArgs.setPublicKey(" ");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());

    }

    /**
     * case: publicKey contain specialChar.
     */
    @Test
    public void testSetPublicKey_pubKeyIsAnyString() {

        final CreateWeIdDataResult weId = super.createWeId();
        SetPublicKeyArgs setPublicKeyArgs =
            TestBaseUtil.buildSetPublicKeyArgs(weId);
        setPublicKeyArgs.setPublicKey(" a!~@#$%^&*(123456789asfs ");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        System.out.println(response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: userWeIdPrivateKey is null.
     */
    @Test
    public void testSetPublicKey_privateKeyIsNull() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setUserWeIdPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is null.
     */
    @Test
    public void testSetPublicKeyCase10() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.getUserWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is invalid.
     */
    @Test
    public void testSetPublicKey_priKeyIsInvalid() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.getUserWeIdPrivateKey().setPrivateKey("xxxxxxxxxxxxxxxxxxx");
        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey and privateKey of WeIdentity DID does not match.
     */
    @Test
    public void testSetPublicKey_priKeyNotMatch() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();
        setPublicKeyArgs.getUserWeIdPrivateKey().setPrivateKey(passwordKey.getPrivateKey());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the private key belongs to the private key of other WeIdentity DID.
     */
    @Test
    public void testSetPublicKeyCase13() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setUserWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: other WeIdentity DID.
     */
    @Test
    public void testSetPublicKeyCase14() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setWeId(createWeIdNew.getWeId());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: owner is the WeIdentity DID.
     */
    @Test
    public void testSetPublicKey_ownerIsWeId() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setOwner(setPublicKeyArgs.getWeId());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: owner is other WeIdentity DID.
     */
    @Test
    public void testSetPublicKey_ownerIsOtherWeId() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setOwner(createWeIdNew.getWeId());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: owner is invalid.
     */
    @Test
    public void testSetPublicKey_ownerIsInvalid() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setOwner("xxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: owner is invalid.
     */
    @Test
    public void testSetPublicKey_ownerIsNull() {

        SetPublicKeyArgs setPublicKeyArgs =
            TestBaseUtil.buildSetPublicKeyArgs(super.createWeId());
        setPublicKeyArgs.setOwner(null);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: owner is invalid.
     */
    @Test
    public void testSetPublicKey_ownerIsTooLong() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        char[] chars = new char[1000];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (i % 127);
        }
        String owner = String.valueOf(chars);
        System.out.println(owner);
        setPublicKeyArgs.setOwner(owner);
        System.out.println(setPublicKeyArgs);
        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: setAuthenticationArgs is null.
     */
    @Test
    public void testSetPublicKeyCase21() {

        ResponseData<Boolean> response = weIdService.setPublicKey(null);
        LogUtil.info(logger, "setPublicKey", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
