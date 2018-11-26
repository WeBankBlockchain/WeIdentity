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
import com.webank.weid.exception.PrivateKeyIllegalException;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.BaseService;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.DynamicBytes;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.bcos.web3j.tx.Contract;
import org.junit.Assert;
import org.junit.Test;

/**
 * a parametric createWeId method for testing WeIdService.
 * 
 * @author v_wbgyang
 *
 */
public class TestCreateWeId2 extends TestBaseServcie {

    /**
     * case: create success.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testCreateWeIdCase1() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case: createWeIdArgs is null.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testCreateWeIdCase2() throws Exception {

        ResponseData<String> response = weIdService.createWeId(null);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: publicKey is null.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testCreateWeIdCase3() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs.setPublicKey(null);

        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PUBLICKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: publicKey is Non integer string.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testCreateWeIdCase4() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs.setPublicKey("abc");

        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PUBLICKEY_AND_PRIVATEKEY_NOT_MATCHED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: weIdPrivateKey is null.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testCreateWeIdCase5() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs.setWeIdPrivateKey(null);

        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: privateKey is null.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testCreateWeIdCase6() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs.getWeIdPrivateKey().setPrivateKey(null);

        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: privateKey is invalid.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testCreateWeIdCase7() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs.getWeIdPrivateKey().setPrivateKey("xxxxxxxxxxxx");

        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PUBLICKEY_AND_PRIVATEKEY_NOT_MATCHED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: privateKey and publicKey misMatch.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testCreateWeIdCase8() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        String[] pk = TestBaseUtil.createEcKeyPair();
        createWeIdArgs.getWeIdPrivateKey().setPrivateKey(pk[1]);

        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PUBLICKEY_AND_PRIVATEKEY_NOT_MATCHED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: Simulation returns null when invoking the getWeIdAttributeChangedEvents
     *       method.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testCreateWeIdCase9() throws Exception {

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public List<WeIdAttributeChangedEventResponse> getWeIdAttributeChangedEvents(
                TransactionReceipt transactionReceipt)
                throws Exception {
                return null;
            }
        };

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the
     *       getWeIdAttributeChangedEvents method.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testCreateWeIdCase10() throws Exception {

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

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: Simulation throws an TimeoutException when calling the
     *       getWeIdAttributeChangedEvents method.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testCreateWeIdCase11() throws Exception {

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

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: Simulation throws an NullPointerException when calling the
     *       getWeIdAttributeChangedEvents method.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testCreateWeIdCase12() throws Exception {

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public List<WeIdAttributeChangedEventResponse> getWeIdAttributeChangedEvents(
                TransactionReceipt transactionReceipt)
                throws Exception {
                throw new NullPointerException();
            }
        };

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: Simulation throws an PrivateKeyIllegalException when calling the
     *       reloadContract method.
     *  
     * @throws Exception   may be throw Exception
     */
    @Test
    public void testCreateWeIdCase13() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();

        MockUp<BaseService> mockTest = new MockUp<BaseService>() {
            @Mock
            public Contract reloadContract(String contractAddress, String privateKey, Class<?> cls)
                throws PrivateKeyIllegalException {
                throw new PrivateKeyIllegalException();
            }
        };

        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: create again.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testCreateWeIdCase14() throws Exception {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        ResponseData<String> response1 = weIdService.createWeId(createWeIdArgs);
        System.out.println("\ncreateWeId result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.WEID_ALREADY_EXIST.getCode(),
            response1.getErrorCode().intValue());
        Assert.assertNotNull(response1.getResult());
    }
}
