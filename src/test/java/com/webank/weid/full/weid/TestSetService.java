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
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.ResponseData;
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
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.junit.Assert;
import org.junit.Test;

/**
 * setService method for testing WeIdService.
 * 
 * @author v_wbgyang
 *
 */
public class TestSetService extends TestBaseServcie {

    /**
     * case: set success.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase1() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: weIdentity DID is blank.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase2() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.setWeId(null);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: weIdentity DID is bad format.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase3() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.setWeId("di:weid:0xbbd97a63365b6c9fb6b011a8d294307a3b7dac73");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: weIdentity DID is not exists.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase4() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.setWeId("did:weid:0xbbd97a63365b6c9fb6b011a8d294307a3b7dac7a");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: type is null.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase5() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.setType(null);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: type too long.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase6() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs
            .setType("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: serviceEndpoint is null (or " ").
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase7() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.setServiceEndpoint(null);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: userWeIdPrivateKey is null.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase8() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.setUserWeIdPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is null.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase9() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the private key belongs to other weIdentity DID.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase10() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);

        setServiceArgs.setUserWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the private key does not match the current weId.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase11() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey("11111111111111");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey and privateKey of weIdentity DID do not match.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase12() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);

        String[] pk = TestBaseUtil.createEcKeyPair();
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey(pk[1]);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: other weIdentity DID.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase13() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.setWeId(createWeIdNew.getWeId());

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the
     *       setService method.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase14() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);

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

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an TimeoutException when calling the
     *       setService method.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase15() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);

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

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an NullPointerException when calling the
     *       setService method.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase16() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public List<WeIdAttributeChangedEventResponse> getWeIdAttributeChangedEvents(
                TransactionReceipt transactionReceipt)
                throws Exception {
                throw new NullPointerException();
            }
        };

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: setServiceArgs is null.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase17() throws Exception {

        ResponseData<Boolean> response = weIdService.setService(null);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: private key is invalid.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetServiceCase18() throws Exception {

        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeId);
        setServiceArgs.getUserWeIdPrivateKey().setPrivateKey("xxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        System.out.println("\nsetService result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
