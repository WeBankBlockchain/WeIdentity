/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.common.PasswordKey;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.AuthenticationArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;

/**
 * setAuthentication method for testing WeIdService.
 *
 * @author v_wbgyang
 */
public class TestSetAuthentication extends TestBaseService {

    private static final Logger logger = LoggerFactory.getLogger(TestSetAuthentication.class);

    @Override
    public synchronized void testInit() {
        super.testInit();
    }

    /**
     * case: set success.
     */
    @Test
    public void testSetAuthentication_setAuthenticationSuccess() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

    }

    /**
     * case: set two Authentication success.
     */
    @Test
    public void testSetAuthentication_setTwoAuthenticationSuccess() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdNew);
        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        SetAuthenticationArgs setAuthenticationArgs1 =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdNew);
        setAuthenticationArgs1.setOwner(createWeIdResult.getWeId());
        ResponseData<Boolean> response1 = weIdService.setAuthentication(setAuthenticationArgs1);
        LogUtil.info(logger, "setAuthentication", response1);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(true, response1.getResult());

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdNew.getWeId());
        LogUtil.info(logger, "setAuthentication", weIdDoc);
        Assert.assertEquals(1, weIdDoc.getResult().getAuthentication().size());
    }

    /**
     * case: WeIdentity DID is blank.
     */
    @Test
    public void testSetAuthentication_weIdBlank() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setWeId("");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is blank.
     */
    @Test
    public void testSetAuthentication_weIdNull() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setWeId(null);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is bad format.
     */
    @Test
    public void testSetAuthentication_weIdFormat() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setWeId("di:weid:0xbbd97a63365b6c9fb6b011a8d294307a3b7dac73");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is not exists.
     */
    @Test
    public void testSetAuthentication_weIdNotExist() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setWeId("did:weid:0xbb1670306aedfaeb75cff9581c99e56ba4797431");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID contain zh.
     */
    @Test
    public void testSetAuthentication_weIdContainZh() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setWeId("did:weid:你好");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: and * at the end of WeIdentity DID .
     */
    @Test
    public void testSetAuthentication_weIdEndwithX() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        String weId = createWeIdResult.getWeId();
        List<String> arrayList = Arrays.asList("*", "+", "=");
        for (String c : arrayList) {
            weId = weId + c;
            setAuthenticationArgs.setWeId(weId);

            ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
            LogUtil.info(logger, "setAuthentication", response);

            Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(),
                response.getErrorCode().intValue());
            Assert.assertEquals(false, response.getResult());
        }
    }

    /**
     * case: WeIdentity DID is too long.
     */
    @Test
    public void testSetAuthentication_weIdEndTooLong() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        String weId = createWeIdResult.getWeId();
        char[] chars = new char[100];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) i;
        }
        setAuthenticationArgs.setWeId(weId + String.valueOf(chars));

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: publicKey is a new key.
     */
    @Test
    public void testSetAuthentication_newPubKey() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();

        setAuthenticationArgs.setPublicKey(passwordKey.getPublicKey());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: publicKey is null.
     */
    @Test
    public void testSetAuthentication_PubKeyNull() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setPublicKey(null);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: publicKey is blank.
     */
    @Test
    public void testSetAuthentication_PubKeyBlank() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setPublicKey("");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: publicKey is belong other weId.
     */
    @Test
    public void testSetAuthentication_PubKeyOther() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        String newPubkey = createWeIdNew.getUserWeIdPublicKey().getPublicKey();
        setAuthenticationArgs.setPublicKey(newPubkey);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: publicKey is invalid ("xxxxxxxxxx" or "1111111111111").
     */
    @Test
    public void testSetAuthentication_pubKeyxxx() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setPublicKey("xxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: publicKey contain special character.
     */
    @Test
    public void testSetAuthentication_pubKeySpecialChar() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setPublicKey("~！@#￥%……&*？》《;az09");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: userWeIdPrivateKey is null.
     */
    @Test
    public void testSetAuthentication_priKeySucess() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setUserWeIdPrivateKey(createWeIdResult.getUserWeIdPrivateKey());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: userWeIdPrivateKey is null.
     */
    @Test
    public void testSetAuthentication_priKeyNull() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setUserWeIdPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is null.
     */
    @Test
    public void testSetAuthentication_setPriKeyNull() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.getUserWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is invalid.
     */
    @Test
    public void testSetAuthentication_invalidPriKey() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.getUserWeIdPrivateKey().setPrivateKey("xxxxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey and privateKey of WeIdentity DID does not match.
     */
    @Test
    public void testSetAuthentication_newPriKey() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();
        setAuthenticationArgs.getUserWeIdPrivateKey().setPrivateKey(passwordKey.getPrivateKey());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey belongs to the private key of other WeIdentity DID.
     */
    @Test
    public void testSetAuthentication_otherPriKey() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setUserWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: other WeIdentity DID.
     */
    @Test
    public void testSetAuthentication_otherWeid() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setWeId(createWeIdNew.getWeId());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: owner is the WeIdentity DID.
     */
    @Test
    public void testSetAuthentication_owerIsWeId() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setOwner(setAuthenticationArgs.getWeId());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: owner is other WeIdentity DID.
     */
    @Test
    public void testSetAuthentication_ownerIsOtherWeId() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setOwner(createWeIdNew.getWeId());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: owner is the WeIdentity DID.
     */
    @Test
    public void testSetAuthentication_NoOwer() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: owner is the WeIdentity DID.
     */
    @Test
    public void testSetAuthentication_owerNotExist() {

        final String weid = "did:weid:1:0x39e5e6f663ef77409144014ceb063713b656aaf7";
        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setOwner(weid);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: owner is the WeIdentity DID.
     */
    @Test
    public void testSetAuthentication_twoAuthentication() {

        final String weid1 = "did:weid:101:0x39e5e6f663ef77409144014ceb063713b656aaf7";
        final String weid2 = "did:weid:101:0x39e5e6f663ef77409144014ceb063713b656aaf8";
        CreateWeIdDataResult createWeIdResultWithSetAttr = super.createWeId();
        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResultWithSetAttr);
        setAuthenticationArgs.setOwner(weid1);
        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        SetAuthenticationArgs setAuthenticationArgs1 =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResultWithSetAttr);
        setAuthenticationArgs1.setOwner(weid2);
        setAuthenticationArgs1.setPublicKey("12345678");
        ResponseData<Boolean> res = weIdService.setAuthentication(setAuthenticationArgs1);
        LogUtil.info(logger, "setAuthentication", res);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), res.getErrorCode().intValue());

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdResultWithSetAttr.getWeId());
        LogUtil.info(logger, "setAuthentication", weIdDoc);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(2, weIdDoc.getResult().getPublicKey().size());
        Assert.assertEquals(2, weIdDoc.getResult().getAuthentication().size());
    }

    /**
     * case: owner weId is not belong chain id .
     */
    @Test
    public void testSetAuthentication_otherChainId() {

        final String owner = "did:weid:101:0x39e5e6f663ef77409144014ceb063713b656aaf7";
        final CreateWeIdDataResult weId = super.createWeId();
        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(weId);
        setAuthenticationArgs.setOwner(owner);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(weId.getWeId());
        LogUtil.info(logger, "setAuthentication", weIdDoc);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(owner, weIdDoc.getResult().getPublicKey().get(0).getOwner());
    }

    /**
     * case: owner is invalid.
     */
    @Test
    public void testSetAuthentication_invalidOwner() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setOwner("xxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: owner is null.
     */
    @Test
    public void testSetAuthentication_ownerNull() {

        final CreateWeIdDataResult weId = super.createWeId();
        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(weId);
        setAuthenticationArgs.setOwner(null);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        ResponseData<WeIdDocument> weIdDoc
            = weIdService.getWeIdDocument(weId.getWeId());
        LogUtil.info(logger, "setAuthentication", weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(1, weIdDoc.getResult().getAuthentication().size());
    }

    /**
     * case: owner is blank.
     */
    @Test
    public void testSetAuthentication_ownerBlank() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setOwner("");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: owner contain special char.
     */
    @Test
    public void testSetAuthentication_ownerContainSpecialChar() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setOwner("~!@#$%^&*()——+=？》《，<>aq10");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: owner contain zh.
     */
    @Test
    public void testSetAuthentication_ownerContainZh() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setOwner("你好");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: setAuthenticationArgs is null.
     */
    @Test
    public void testSetAuthenticationCase21() {

        ResponseData<Boolean> response = weIdService.setAuthentication(null);
        LogUtil.info(logger, "setAuthentication", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    @Test
    public void testDelegateSetAuth_weIdIsNotExist() {

        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();
        String pubKey = passwordKey.getPublicKey();
        String weId = "did:weid:101:0x52560bcb2aea030347fe1891f09" + System.currentTimeMillis();
        LogUtil.info(logger, "weid", weId);

        AuthenticationArgs authenticationArgs = new AuthenticationArgs();
        authenticationArgs.setWeId(weId);
        authenticationArgs.setPublicKey(pubKey);

        String delegateWeId = createWeIdNew.getWeId();
        String delegatePrivateKey = this.privateKey;
        WeIdAuthentication weIdAuthentication = new WeIdAuthentication(delegateWeId,
            delegatePrivateKey);

        ResponseData<Boolean> response = weIdService
            .delegateSetAuthentication(authenticationArgs, weIdAuthentication);
        LogUtil.info(logger, "response", response);
        Assert.assertTrue(ErrorCode.WEID_DOES_NOT_EXIST.getCode() == response.getErrorCode()
            || ErrorCode.WEID_INVALID.getCode() == response.getErrorCode());
        Assert.assertEquals(Boolean.FALSE, response.getResult());
    }
}
