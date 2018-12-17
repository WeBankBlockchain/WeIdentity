/*
 *       Copyright© (2018) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.full.weid;

import java.util.List;
import java.util.concurrent.Future;

import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.common.PasswordKey;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.WeIdContract;
import com.webank.weid.contract.WeIdContract.WeIdAttributeChangedEventResponse;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * setAuthentication method for testing WeIdService.
 * 
 * @author v_wbgyang
 *
 */
public class TestSetAuthentication extends TestBaseServcie {
    
    private static final Logger logger = LoggerFactory.getLogger(TestSetAuthentication.class);

    /**
     * case: set success.
     *
     */
    @Test
    public void testSetAuthenticationCase1() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: WeIdentity DID is blank.
     *
     */
    @Test
    public void testSetAuthenticationCase2() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setWeId(null);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is bad format.
     *
     */
    @Test
    public void testSetAuthenticationCase3() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setWeId("di:weid:0xbbd97a63365b6c9fb6b011a8d294307a3b7dac73");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is not exists.
     *
     */
    @Test
    public void testSetAuthenticationCase4() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setWeId("did:weid:0xbb");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: type is null or other string.
     *
     */
    @Test
    public void testSetAuthenticationCase5() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setType(null);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: publicKey is a new key.
     *
     */
    @Test
    public void testSetAuthenticationCase6() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();

        setAuthenticationArgs.setPublicKey(passwordKey.getPublicKey());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: publicKey is null.
     *
     */
    @Test
    public void testSetAuthenticationCase7() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setPublicKey(null);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: publicKey is invalid ("xxxxxxxxxx" or "1111111111111").
     * 
     */
    @Test
    public void testSetAuthenticationCase8() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setPublicKey("xxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: userWeIdPrivateKey is null.
     *
     */
    @Test
    public void testSetAuthenticationCase9() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setUserWeIdPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is null.
     *
     */
    @Test
    public void testSetAuthenticationCase10() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.getUserWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is invalid.
     *
     */
    @Test
    public void testSetAuthenticationCase11() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.getUserWeIdPrivateKey().setPrivateKey("xxxxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey and privateKey of WeIdentity DID does not match.
     *
     */
    @Test
    public void testSetAuthenticationCase12() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();
        setAuthenticationArgs.getUserWeIdPrivateKey().setPrivateKey(passwordKey.getPrivateKey());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey belongs to the private key of other WeIdentity DID.
     *
     */
    @Test
    public void testSetAuthenticationCase13() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setUserWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: other WeIdentity DID.
     *
     */
    @Test
    public void testSetAuthenticationCase14() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setWeId(createWeIdNew.getWeId());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: owner is the WeIdentity DID.
     *
     */
    @Test
    public void testSetAuthenticationCase15() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setOwner(setAuthenticationArgs.getWeId());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: owner is other WeIdentity DID.
     *
     */
    @Test
    public void testSetAuthenticationCase16() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setOwner(createWeIdNew.getWeId());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: owner is invalid.
     *
     */
    @Test
    public void testSetAuthenticationCase17() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);
        setAuthenticationArgs.setOwner("xxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the
     *       setAttribute method.
     *
     */
    @Test
    public void testSetAuthenticationCase18() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);

        MockUp<Future<?>> mockFuture = mockInterruptedFuture();

        ResponseData<Boolean> response =
            setAuthenticationForMock(setAuthenticationArgs, mockFuture);

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    private ResponseData<Boolean> setAuthenticationForMock(
        SetAuthenticationArgs setAuthenticationArgs,
        MockUp<Future<?>> mockFuture) {
        
        MockUp<WeIdContract> mockTest = mockSetAttribute(mockFuture);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();
        return response;
    }

    /**
     * case: Simulation throws an TimeoutException when calling the
     *       setAttribute method.
     *
     */
    @Test
    public void testSetAuthenticationCase19() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);

        MockUp<Future<?>> mockFuture = mockTimeoutFuture();

        ResponseData<Boolean> response =
            setAuthenticationForMock(setAuthenticationArgs, mockFuture);

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an NullPointerException when calling the
     *       getWeIdAttributeChangedEvents method.
     *
     */
    @Test
    public void testSetAuthenticationCase20() {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeIdResult);

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public List<WeIdAttributeChangedEventResponse> getWeIdAttributeChangedEvents(
                TransactionReceipt transactionReceipt)
                throws NullPointerException {
                
                throw new NullPointerException();
            }
        };

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: setAuthenticationArgs is null.
     *
     */
    @Test
    public void testSetAuthenticationCase21() {

        ResponseData<Boolean> response = weIdService.setAuthentication(null);
        logger.info("setAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
