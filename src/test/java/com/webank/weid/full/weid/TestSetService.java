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

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.common.PasswordKey;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * setService method for testing WeIdService.
 *
 * @author v_wbgyang
 */
public class TestSetService extends TestBaseService {

    private static final Logger logger = LoggerFactory.getLogger(TestSetService.class);

    @Override
    public synchronized void testInit() {
        super.testInit();
    }

    /**
     * case: set success.
     */
    @Test
    public void testSetService_sucess() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: two services(type different) set success.
     */
    @Test
    public void testSetService_twoTypeDifferentServices() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        SetServiceArgs setServiceArgs1 = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs1.setType("1234");
        ResponseData<Boolean> response1 = weIdService.setService(setServiceArgs1);
        LogUtil.info(logger, "setService", response1);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(true, response1.getResult());

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdResult.getWeId());
        LogUtil.info(logger, "setService", weIdDoc);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(2, weIdDoc.getResult().getService().size());


    }

    /**
     * case: two services(endpoint different) set success.
     */
    @Test
    public void testSetService_twoEndpointDifferentServices() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        SetServiceArgs setServiceArgs1 = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs1.setServiceEndpoint("http:test.com");
        ResponseData<Boolean> response1 = weIdService.setService(setServiceArgs1);
        LogUtil.info(logger, "setService", response1);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(true, response1.getResult());

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdResult.getWeId());
        LogUtil.info(logger, "setService", weIdDoc);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(2, weIdDoc.getResult().getService().size());
    }

    /**
     * case: WeIdentity DID is blank.
     */
    @Test
    public void testSetService_weIdIsNull() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setWeId(null);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is bad format.
     */
    @Test
    public void testSetService_invalidWeId() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setWeId("di:weid:0xsdg!@#$%^&《》");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is not exists.
     */
    @Test
    public void testSetService_weIdNotExist() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setWeId("did:weid:0xbbd97a63365b6c9fb6b011a8d294307a3b7dac7a");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is too long.
     */
    @Test
    public void testSetService_weIdTooLong() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        char[] chars = new char[1000];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (i % 127);
        }
        String weId = String.valueOf(chars);
        weId = "did:weid:0xbbd97a63365b6c9fb6b011a8d294307a3b7dac7" + weId;
        setServiceArgs.setWeId(weId);
        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: other WeIdentity DID.
     */
    @Test
    public void testSetService_otherWeId() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setWeId(createWeIdNew.getWeId());

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: type is null.
     */
    @Test
    public void testSetService_typeNull() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setType(null);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: type is blank.
     */
    @Test
    public void testSetService_typeBlank() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setType(" ");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: type contain interger special string.
     */
    @Test
    public void testSetService_typeIsAnyString() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setType("x123!@#$%^&*()_+=-?><:;sdf");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(
            ErrorCode.WEID_SERVICE_TYPE_OVERLIMIT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: type too long.
     */
    @Test
    public void testSetService_typeTooLong() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs
            .setType("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.WEID_SERVICE_TYPE_OVERLIMIT.getCode(), 
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: serviceEndpoint is null.
     */
    @Test
    public void testSetService_endpointNull() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setServiceEndpoint(null);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: serviceEndpoint is blank.
     */
    @Test
    public void testSetService_endpointBlank() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setServiceEndpoint("");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: serviceEndpoint is blank.
     */
    @Test
    public void testSetService_endpointZh() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setServiceEndpoint("你好");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: userWeIdPrivateKey is null.
     */
    @Test
    public void testSetService_priKeyNull() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setUserWeIdPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is null.
     */
    @Test
    public void testSetService_setPriKeyNull() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is blank.
     */
    @Test
    public void testSetService_setPriKeyBlank() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey(" ");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the private key belongs to other WeIdentity DID.
     */
    @Test
    public void testSetService_priKeyIsOtherWeid() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);

        setServiceArgs.setUserWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the private key does not match the current weId.
     */
    @Test
    public void testSetService_prikeyNotMatch() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey("11111111111111");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey and privateKey of WeIdentity DID do not match.
     */
    @Test
    public void testSetServiceCase12() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);

        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey(passwordKey.getPrivateKey());

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: setServiceArgs is null.
     */
    @Test
    public void testSetServiceCase17() {

        ResponseData<Boolean> response = weIdService.setService(null);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: private key is invalid.
     */
    @Test
    public void testSetServiceCase18() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey("xxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
