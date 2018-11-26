/*
 * Copyright© (2018) WeBank Co., Ltd.
 *
 * This file is part of weidentity-java-sdk.
 *
 * weidentity-java-sdk is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * weidentity-java-sdk is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * weidentity-java-sdk. If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.full.auth;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.AuthorityIssuerController;
import com.webank.weid.contract.AuthorityIssuerController.AuthorityIssuerRetLogEventResponse;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.DynamicBytes;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.junit.Assert;
import org.junit.Test;

/**
 * registerAuthorityIssuer method for testing AuthorityIssuerService.
 * 
 * @author v_wbgyang
 *
 */
public class TestRegisterAuthorityIssuer extends TestBaseServcie {

    /**
     * case: weIdentity DId is invalid.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase1() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getAuthorityIssuer().setWeId("xxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: weIdentity DId is bad format.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase2() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getAuthorityIssuer()
                .setWeId("di:weid:0x29f3fe8d4536966af41392f7ddc756b6f452df02");

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the weIdentity DId address is not exists.
     * 
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase3() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getAuthorityIssuer()
                .setWeId("did:weid:0x29f3" + System.currentTimeMillis());

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the weIdentity DId is null or "" or " ".
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase4() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getAuthorityIssuer().setWeId(null);

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the name is blank.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase5() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getAuthorityIssuer().setName(null);

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_NAME_ILLEGAL.getCode(),
                response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the created before now ,now or after now.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase6() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getAuthorityIssuer()
                .setCreated(System.currentTimeMillis() + 4000);

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: the accValue is null.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase7() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getAuthorityIssuer().setAccValue(null);

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_ACCVALUE_ILLEAGAL.getCode(),
                response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: registerAuthorityIssuer success.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase8() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: the weIdentity DId is registed.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase9() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        ResponseData<Boolean> response1 =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_ALREADY_EXIST.getCode(),
                response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    /**
     * case: registerAuthorityIssuerArgs is null.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase10() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs = null;
        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: authorityIssuer is null.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase11() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.setAuthorityIssuer(null);

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: weIdPrivateKey is null.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase12() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.setWeIdPrivateKey(null);

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL.getCode(),
                response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is null.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase13() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL.getCode(),
                response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is invalid.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase14() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey("xxxxxxx");

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_ERROR.getCode(),
                response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey and private key of weIdentity DId do not match.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase15() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getWeIdPrivateKey()
                .setPrivateKey(TestBaseUtil.createEcKeyPair()[1]);

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NO_PERMISSION.getCode(),
                response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey belongs to the private key of other weIdentity DId.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase16() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.setWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NO_PERMISSION.getCode(),
                response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the private key is the private key of the members of the committee.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase17() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey(issuerPrivateList.get(1));

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NO_PERMISSION.getCode(),
                response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: mock an InterruptedException.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase18() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);

        final MockUp<Future<TransactionReceipt>> mockFuture =
                new MockUp<Future<TransactionReceipt>>() {

                @Mock
                public Future<TransactionReceipt> get(long timeout, TimeUnit unit)
                        throws Exception {
                    throw new InterruptedException();
                }
            };

        MockUp<AuthorityIssuerController> mockTest = new MockUp<AuthorityIssuerController>() {
            @Mock
            public Future<TransactionReceipt> addAuthorityIssuer(Address addr,
                    StaticArray<Bytes32> attribBytes32, StaticArray<Int256> attribInt,
                    DynamicBytes accValue) throws Exception {

                return mockFuture.getMockInstance();
            }
        };

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
                response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: mock an TimeoutException.
     * 
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase19() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);

        final MockUp<Future<TransactionReceipt>> mockFuture =
                new MockUp<Future<TransactionReceipt>>() {
                @Mock
                public Future<TransactionReceipt> get(long timeout, TimeUnit unit)
                        throws Exception {
                    throw new TimeoutException();
                }
            };

        MockUp<AuthorityIssuerController> mockTest = new MockUp<AuthorityIssuerController>() {
            @Mock
            public Future<TransactionReceipt> addAuthorityIssuer(Address addr,
                    StaticArray<Bytes32> attribBytes32, StaticArray<Int256> attribInt,
                    DynamicBytes accValue) throws Exception {

                return mockFuture.getMockInstance();
            }
        };

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
                response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: mock returns null when invoking the getAuthorityIssuerRetLogEvents.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase20() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);

        MockUp<AuthorityIssuerController> mockTest = new MockUp<AuthorityIssuerController>() {
            @Mock
            public List<AuthorityIssuerRetLogEventResponse> getAuthorityIssuerRetLogEvents(
                    TransactionReceipt transactionReceipt) {
                List<AuthorityIssuerRetLogEventResponse> list =
                        new ArrayList<AuthorityIssuerRetLogEventResponse>();
                list.add(null);
                return list;
            }
        };

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_ERROR.getCode(),
                response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: authorityIssuer.name is too long.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterAuthorityIssuerCase21() throws Exception {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
                TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId);
        registerAuthorityIssuerArgs.getAuthorityIssuer()
                .setName("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        System.out.println("\nregisterAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_NAME_ILLEGAL.getCode(),
                response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
