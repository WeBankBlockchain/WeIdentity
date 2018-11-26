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
import com.webank.weid.protocol.request.SetAuthenticationArgs;
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
 * setAuthentication method for testing WeIdService.
 * 
 * @author v_wbgyang
 *
 */
public class TestSetAuthentication extends TestBaseServcie {

    /**
     * case: set success.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetAuthenticationCase1() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase2() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setWeId(null);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase3() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setWeId("di:weid:0xbbd97a63365b6c9fb6b011a8d294307a3b7dac73");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase4() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setWeId("did:weid:0xbb");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase5() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setType(null);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase6() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        String[] pk = TestBaseUtil.createEcKeyPair();

        setAuthenticationArgs.setPublicKey(pk[0]);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase7() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setPublicKey(null);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase8() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setPublicKey("xxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase9() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setUserWeIdPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase10() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.getUserWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase11() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.getUserWeIdPrivateKey().setPrivateKey("xxxxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase12() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        String[] pk = TestBaseUtil.createEcKeyPair();
        setAuthenticationArgs.getUserWeIdPrivateKey().setPrivateKey(pk[1]);

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey belongs to the private key of other weIdentity DID.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetAuthenticationCase13() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setUserWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase14() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setWeId(createWeIdNew.getWeId());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase15() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setOwner(setAuthenticationArgs.getWeId());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase16() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setOwner(createWeIdNew.getWeId());

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase17() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);
        setAuthenticationArgs.setOwner("xxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the
     *       setAttribute method.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetAuthenticationCase18() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);

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

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase19() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);

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

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an NullPointerException when calling the
     *       getWeIdAttributeChangedEvents method.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testSetAuthenticationCase20() throws Exception {

        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createWeId);

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public List<WeIdAttributeChangedEventResponse> getWeIdAttributeChangedEvents(
                TransactionReceipt transactionReceipt)
                throws Exception {
                throw new NullPointerException();
            }
        };

        ResponseData<Boolean> response = weIdService.setAuthentication(setAuthenticationArgs);
        System.out.println("\nsetAuthentication result:");
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
    public void testSetAuthenticationCase21() throws Exception {

        ResponseData<Boolean> response = weIdService.setAuthentication(null);
        System.out.println("\nsetAuthentication result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
