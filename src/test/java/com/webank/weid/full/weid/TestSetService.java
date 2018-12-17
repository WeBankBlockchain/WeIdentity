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
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * setService method for testing WeIdService.
 * 
 * @author v_wbgyang
 *
 */
public class TestSetService extends TestBaseServcie {
    
    private static final Logger logger = LoggerFactory.getLogger(TestSetService.class);

    /**
     * case: set success.
     *
     */
    @Test
    public void testSetServiceCase1() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: WeIdentity DID is blank.
     *
     */
    @Test
    public void testSetServiceCase2() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setWeId(null);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is bad format.
     *
     */
    @Test
    public void testSetServiceCase3() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setWeId("di:weid:0xbbd97a63365b6c9fb6b011a8d294307a3b7dac73");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is not exists.
     *
     */
    @Test
    public void testSetServiceCase4() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setWeId("did:weid:0xbbd97a63365b6c9fb6b011a8d294307a3b7dac7a");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: type is null.
     *
     */
    @Test
    public void testSetServiceCase5() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setType(null);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: type too long.
     *
     */
    @Test
    public void testSetServiceCase6() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs
            .setType("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: serviceEndpoint is null (or " ").
     *
     */
    @Test
    public void testSetServiceCase7() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setServiceEndpoint(null);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: userWeIdPrivateKey is null.
     *
     */
    @Test
    public void testSetServiceCase8() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setUserWeIdPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is null.
     *
     */
    @Test
    public void testSetServiceCase9() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the private key belongs to other WeIdentity DID.
     *
     */
    @Test
    public void testSetServiceCase10() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);

        setServiceArgs.setUserWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the private key does not match the current weId.
     *
     */
    @Test
    public void testSetServiceCase11() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey("11111111111111");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey and privateKey of WeIdentity DID do not match.
     *
     */
    @Test
    public void testSetServiceCase12() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);

        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey(passwordKey.getPrivateKey());

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
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
    public void testSetServiceCase13() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.setWeId(createWeIdNew.getWeId());

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the
     *       setService method.
     *
     */
    @Test
    public void testSetServiceCase14() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);

        MockUp<Future<?>> mockFuture = mockInterruptedFuture();

        ResponseData<Boolean> response = setAttributeForMock(setServiceArgs, mockFuture);

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    private ResponseData<Boolean> setAttributeForMock(
        SetServiceArgs setServiceArgs,
        MockUp<Future<?>> mockFuture) {
        
        MockUp<WeIdContract> mockTest = mockSetAttribute(mockFuture);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();
        return response;
    }

    /**
     * case: Simulation throws an TimeoutException when calling the
     *       setService method.
     *
     */
    @Test
    public void testSetServiceCase15() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);

        MockUp<Future<?>> mockFuture = mockTimeoutFuture();

        ResponseData<Boolean> response = setAttributeForMock(setServiceArgs, mockFuture);

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an NullPointerException when calling the
     *       setService method.
     *
     */
    @Test
    public void testSetServiceCase16() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public List<WeIdAttributeChangedEventResponse> getWeIdAttributeChangedEvents(
                TransactionReceipt transactionReceipt)
                throws NullPointerException {
                
                throw new NullPointerException();
            }
        };

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: setServiceArgs is null.
     *
     */
    @Test
    public void testSetServiceCase17() {

        ResponseData<Boolean> response = weIdService.setService(null);
        logger.info("setService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: private key is invalid.
     *
     */
    @Test
    public void testSetServiceCase18() {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey("xxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        logger.info("setService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
