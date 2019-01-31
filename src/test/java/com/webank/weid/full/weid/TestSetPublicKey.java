/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * setPublicKey method for testing WeIdService.
 * 
 * @author v_wbgyang
 *
 */
public class TestSetPublicKey extends TestBaseServcie {
    
    private static final Logger logger = LoggerFactory.getLogger(TestSetPublicKey.class);

    /**
     * case: create success.
     *
     */
    @Test
    public void testSetPublicKeyCase1() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        BeanUtil.print(setPublicKeyArgs);
        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: WeIdentity DID is blank.
     *
     */
    @Test
    public void testSetPublicKeyCase2() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setWeId(null);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is bad format.
     *
     */
    @Test
    public void testSetPublicKeyCase3() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setWeId("di:weid:0xbbd97a63365b6c9fb6b011a8d294307a3b7dac73");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is not exists.
     *
     */
    @Test
    public void testSetPublicKeyCase4() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setWeId("did:weid:0aaaaaaaaaaaa");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
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
    public void testSetPublicKeyCase5() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setType(null);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: publicKey is a new key.
     *
     */
    @Test
    public void testSetPublicKeyCase6() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();

        setPublicKeyArgs.setPublicKey(passwordKey.getPublicKey());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: publicKey is null.
     *
     */
    @Test
    public void testSetPublicKeyCase7() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setPublicKey(null);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: publicKey is invalid ("xxxxxxxxxx" or "1111111111111").
     *
     */
    @Test
    public void testSetPublicKeyCase8() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setPublicKey("xxxxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: userWeIdPrivateKey is null.
     *
     */
    @Test
    public void testSetPublicKeyCase9() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setUserWeIdPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is null.
     *
     */
    @Test
    public void testSetPublicKeyCase10() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.getUserWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
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
    public void testSetPublicKeyCase11() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.getUserWeIdPrivateKey().setPrivateKey("xxxxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
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
    public void testSetPublicKeyCase12() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();
        setPublicKeyArgs.getUserWeIdPrivateKey().setPrivateKey(passwordKey.getPrivateKey());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the private key belongs to the private key of other WeIdentity DID. 
     *
     */
    @Test
    public void testSetPublicKeyCase13() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setUserWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
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
    public void testSetPublicKeyCase14() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setWeId(createWeIdNew.getWeId());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
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
    public void testSetPublicKeyCase15() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setOwner(setPublicKeyArgs.getWeId());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: owner is other WeIdentity DID.
     *
     */
    @Test
    public void testSetPublicKeyCase16() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setOwner(createWeIdNew.getWeId());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: owner is invalid. 
     *
     */
    @Test
    public void testSetPublicKeyCase17() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);
        setPublicKeyArgs.setOwner("xxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the
     *       setPublicKey method.
     *
     */
    @Test
    public void testSetPublicKeyCase18() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);

        MockUp<Future<?>> mockFuture = mockInterruptedFuture();

        ResponseData<Boolean> response = setPublicKeyForMock(setPublicKeyArgs, mockFuture);

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    private ResponseData<Boolean> setPublicKeyForMock(
        SetPublicKeyArgs setPublicKeyArgs,
        MockUp<Future<?>> mockFuture) {
        
        MockUp<WeIdContract> mockTest = mockSetAttribute(mockFuture);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
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
    public void testSetPublicKeyCase19() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);

        MockUp<Future<?>> mockFuture = mockTimeoutFuture();

        ResponseData<Boolean> response = setPublicKeyForMock(setPublicKeyArgs, mockFuture);

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the
     *       setAttribute method.
     *
     */
    @Test
    public void testSetPublicKeyCase20() {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdResult);

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public List<WeIdAttributeChangedEventResponse> getWeIdAttributeChangedEvents(
                TransactionReceipt transactionReceipt)
                throws NullPointerException {
                
                throw new NullPointerException();
            }
        };

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        logger.info("setPublicKey result:");
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
    public void testSetPublicKeyCase21() {

        ResponseData<Boolean> response = weIdService.setPublicKey(null);
        logger.info("setPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
