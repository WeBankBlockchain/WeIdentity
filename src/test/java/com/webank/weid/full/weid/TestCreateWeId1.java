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

import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.WeIdContract;
import com.webank.weid.contract.WeIdContract.WeIdAttributeChangedEventResponse;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.DynamicBytes;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.junit.Assert;
import org.junit.Test;

/**
 * non parametric createWeId method for testing WeIdService.
 * 
 * @author v_wbgyang
 *
 */
public class TestCreateWeId1 extends TestBaseServcie {

    /**
     * case: create WeId success.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testCreateWeIdCase1() throws Exception {

        ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case: Simulation throws an TimeoutException when calling the
     *       getWeIdAttributeChangedEvents method.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testCreateWeIdCase2() throws Exception {

        final MockUp<Future<TransactionReceipt>> mockFuture =
            new MockUp<Future<TransactionReceipt>>() {
                @Mock
                public Future<TransactionReceipt> get(long timeout, TimeUnit unit)
                    throws Exception {
                    throw new TimeoutException();
                }
            };

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public Future<TransactionReceipt> setAttribute(
                Address identity,
                Bytes32 key,
                DynamicBytes value,
                Int256 updated)
                throws Exception {
                return mockFuture.getMockInstance();
            }
        };

        ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the
     *       getWeIdAttributeChangedEvents method.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testCreateWeIdCase3() throws Exception {

        final MockUp<Future<TransactionReceipt>> mockFuture =
            new MockUp<Future<TransactionReceipt>>() {
                @Mock
                public Future<TransactionReceipt> get(long timeout, TimeUnit unit)
                    throws Exception {
                    throw new InterruptedException();
                }
            };

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public Future<TransactionReceipt> setAttribute(
                Address identity,
                Bytes32 key,
                DynamicBytes value,
                Int256 updated)
                throws Exception {
                return mockFuture.getMockInstance();
            }
        };

        ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case: Simulation returns null when invoking the getWeIdAttributeChangedEvents
     *       method.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testCreateWeIdCase4() throws Exception {

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public List<WeIdAttributeChangedEventResponse> getWeIdAttributeChangedEvents(
                TransactionReceipt transactionReceipt)
                throws Exception {
                return null;
            }
        };

        ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case: Simulation throws an NullPointerException when calling the
     *       getWeIdAttributeChangedEvents method.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testCreateWeIdCase5() throws Exception {

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public List<WeIdAttributeChangedEventResponse> getWeIdAttributeChangedEvents(
                TransactionReceipt transactionReceipt)
                throws Exception {
                throw new NullPointerException();
            }
        };

        ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case: Simulation throws an exception when calling the createEcKeyPair
     *       method.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testCreateWeIdCase6() throws Exception {

        MockUp<Keys> mockTest = new MockUp<Keys>() {
            @Mock
            public ECKeyPair createEcKeyPair()
                throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
                NoSuchProviderException {
                throw new NoSuchProviderException();
            }
        };

        ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.WEID_KEYPAIR_CREATE_FAILED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }
}
