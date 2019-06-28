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

import java.security.NoSuchProviderException;
import java.util.List;
import java.util.concurrent.Future;

import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.v1.WeIdContract;
import com.webank.weid.contract.v1.WeIdContract.WeIdAttributeChangedEventResponse;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.RawTransactionService;
import com.webank.weid.service.impl.RawTransactionServiceImpl;

/**
 * non parametric createWeId method for testing WeIdService.
 *
 * @author v_wbgyang
 */
public class TestCreateWeId1 extends TestBaseServcie {

    private static final Logger logger = LoggerFactory.getLogger(TestCreateWeId1.class);

    /**
     * case: create WeId success.
     */
    @Test
    public void testCreateWeIdCase1() {

        ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();
        LogUtil.info(logger, "createWeId", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case: Simulation throws an TimeoutException when calling the getWeIdAttributeChangedEvents
     * method.
     */
    @Test
    public void testCreateWeIdCase2() {

        MockUp<Future<?>> mockFuture = mockTimeoutFuture();

        ResponseData<CreateWeIdDataResult> response = createWeIdForMock(mockFuture);
        LogUtil.info(logger, "createWeId", response);

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the
     * getWeIdAttributeChangedEvents method.
     */
    @Test
    public void testCreateWeIdCase3() {

        MockUp<Future<?>> mockFuture = mockInterruptedFuture();

        ResponseData<CreateWeIdDataResult> response = createWeIdForMock(mockFuture);
        LogUtil.info(logger, "createWeId", response);

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    private ResponseData<CreateWeIdDataResult> createWeIdForMock(MockUp<Future<?>> mockFuture) {

        MockUp<WeIdContract> mockTest = mockSetAttribute(mockFuture);

        ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();
        mockTest.tearDown();
        mockFuture.tearDown();
        return response;
    }

    /**
     * case: Simulation returns null when invoking the getWeIdAttributeChangedEvents method.
     */
    @Test
    public void testCreateWeIdCase4() {

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public List<WeIdAttributeChangedEventResponse> getWeIdAttributeChangedEvents(
                TransactionReceipt transactionReceipt) {
                return null;
            }
        };

        ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();
        LogUtil.info(logger, "createWeId", response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case: Simulation throws an NullPointerException when calling the
     * getWeIdAttributeChangedEvents method.
     */
    @Test
    public void testCreateWeIdCase5() {

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public List<WeIdAttributeChangedEventResponse> getWeIdAttributeChangedEvents(
                TransactionReceipt transactionReceipt) {
                throw new WeIdBaseException("mock exception");
            }
        };

        ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();
        LogUtil.info(logger, "createWeId", response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case: Simulation throws an exception when calling the createEcKeyPair method.
     */
    @Test
    public void testCreateWeIdCase6() {

        MockUp<Keys> mockTest = new MockUp<Keys>() {
            @Mock
            public ECKeyPair createEcKeyPair()
                throws NoSuchProviderException {

                throw new NoSuchProviderException();
            }
        };

        ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();
        LogUtil.info(logger, "createWeId", response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.WEID_KEYPAIR_CREATE_FAILED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case: call transactionhex null - arbitrary.
     */
    @Test
    public void testCreateWeIdCase7() {
        String hex = StringUtils.EMPTY;
        RawTransactionService rawTransactionService = new RawTransactionServiceImpl();
        ResponseData<String> response = rawTransactionService.createWeId(hex);
        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertTrue(StringUtils.isEmpty(response.getResult()));
    }

    /**
     * case: call transactionhex method - arbitrary.
     */
    @Test
    public void testCreateWeIdCase8() {
        String hex = "11111";
        RawTransactionService rawTransactionService = new RawTransactionServiceImpl();
        ResponseData<String> response = rawTransactionService.createWeId(hex);
        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertTrue(StringUtils.isEmpty(response.getResult()));
    }
}
