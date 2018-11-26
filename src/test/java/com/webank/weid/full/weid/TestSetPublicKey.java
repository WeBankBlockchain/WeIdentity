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
import com.webank.weid.protocol.request.SetPublicKeyArgs;
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
 * setPublicKey method for testing WeIdService.
 * 
 * @author v_wbgyang
 *
 */
public class TestSetPublicKey extends TestBaseServcie {

    /**
     * case: create success.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase1() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
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
    public void testSetPublicKeyCase2() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        setPublicKeyArgs.setWeId(null);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
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
    public void testSetPublicKeyCase3() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        setPublicKeyArgs.setWeId("di:weid:0xbbd97a63365b6c9fb6b011a8d294307a3b7dac73");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
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
    public void testSetPublicKeyCase4() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        setPublicKeyArgs.setWeId("did:weid:0aaaaaaaaaaaa");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: type is null or other string.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase5() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        setPublicKeyArgs.setType(null);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: publicKey is a new key.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase6() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        String[] pk = TestBaseUtil.createEcKeyPair();

        setPublicKeyArgs.setPublicKey(pk[0]);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: publicKey is null.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase7() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        setPublicKeyArgs.setPublicKey(null);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: publicKey is invalid ("xxxxxxxxxx" or "1111111111111").
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase8() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        setPublicKeyArgs.setPublicKey("xxxxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: userWeIdPrivateKey is null.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase9() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        setPublicKeyArgs.setUserWeIdPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
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
    public void testSetPublicKeyCase10() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        setPublicKeyArgs.getUserWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is invalid.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase11() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        setPublicKeyArgs.getUserWeIdPrivateKey().setPrivateKey("xxxxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey and privateKey of weIdentity DID does not match.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase12() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        String[] pk = TestBaseUtil.createEcKeyPair();
        setPublicKeyArgs.getUserWeIdPrivateKey().setPrivateKey(pk[1]);

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the private key belongs to the private key of other weIdentity DID. 
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase13() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        setPublicKeyArgs.setUserWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
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
    public void testSetPublicKeyCase14() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        setPublicKeyArgs.setWeId(createWeIdNew.getWeId());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: owner is the weIdentity DID.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase15() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        setPublicKeyArgs.setOwner(setPublicKeyArgs.getWeId());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: owner is other weIdentity DID.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase16() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        setPublicKeyArgs.setOwner(createWeIdNew.getWeId());

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: owner is invalid. 
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase17() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);
        setPublicKeyArgs.setOwner("xxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the
     *       setPublicKey method.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase18() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);

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

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an TimeoutException when calling the
     *       setAttribute method.
     *       
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase19() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);

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

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the
     *       setAttribute method.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase20() throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeId);

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public List<WeIdAttributeChangedEventResponse> getWeIdAttributeChangedEvents(
                TransactionReceipt transactionReceipt)
                throws Exception {
                throw new NullPointerException();
            }
        };

        ResponseData<Boolean> response = weIdService.setPublicKey(setPublicKeyArgs);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: setAuthenticationArgs is null.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetPublicKeyCase21() throws Exception {

        ResponseData<Boolean> response = weIdService.setPublicKey(null);
        System.out.println("\nsetPublicKey result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
