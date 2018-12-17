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

package com.webank.weid.full.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.AuthorityIssuerController;
import com.webank.weid.contract.AuthorityIssuerController.AuthorityIssuerRetLogEventResponse;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;

/**
 * registerAuthorityIssuer method for testing AuthorityIssuerService.
 * 
 * @author v_wbgyang
 *
 */
public class TestRegisterAuthorityIssuer extends TestBaseServcie {

    private static final Logger logger = 
        LoggerFactory.getLogger(TestRegisterAuthorityIssuer.class);

    /**
     * case: WeIdentity DID is invalid.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase1() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);
        registerAuthorityIssuerArgs.getAuthorityIssuer().setWeId("xxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is bad format.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase2() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);
        registerAuthorityIssuerArgs.getAuthorityIssuer()
            .setWeId("di:weid:0x29f3fe8d4536966af41392f7ddc756b6f452df02");

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the WeIdentity DID address is not exists.
     * 
     */
    @Test
    public void testRegisterAuthorityIssuerCase3() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);
        registerAuthorityIssuerArgs.getAuthorityIssuer()
            .setWeId("did:weid:0x29f3" + System.currentTimeMillis());

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the WeIdentity DID is null or "" or " ".
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase4() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);
        registerAuthorityIssuerArgs.getAuthorityIssuer().setWeId(null);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the name is blank.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase5() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);
        registerAuthorityIssuerArgs.getAuthorityIssuer().setName(null);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_NAME_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the created before now ,now or after now.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase6() {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId, privateKey);
        registerAuthorityIssuerArgs.getAuthorityIssuer()
            .setCreated(System.currentTimeMillis() + 4000);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: the accValue is null.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase7() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);
        registerAuthorityIssuerArgs.getAuthorityIssuer().setAccValue(null);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_ACCVALUE_ILLEAGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: registerAuthorityIssuer success.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase8() {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId, privateKey);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: the WeIdentity DID is registed.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase9() {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId, privateKey);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        ResponseData<Boolean> response1 =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_ALREADY_EXIST.getCode(),
            response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    /**
     * case: registerAuthorityIssuerArgs is null.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase10() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs = null;
        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: authorityIssuer is null.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase11() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);
        registerAuthorityIssuerArgs.setAuthorityIssuer(null);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: weIdPrivateKey is null.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase12() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);
        registerAuthorityIssuerArgs.setWeIdPrivateKey(null);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is null.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase13() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);
        registerAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey(null);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_PRIVATE_KEY_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey is invalid.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase14() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);
        registerAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey("xxxxxxx");

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey and private key of WeIdentity DID do not match.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase15() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);
        registerAuthorityIssuerArgs.getWeIdPrivateKey()
            .setPrivateKey(TestBaseUtil.createEcKeyPair().getPrivateKey());

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NO_PERMISSION.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: privateKey belongs to the private key of other WeIdentity DID.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase16() {

        CreateWeIdDataResult createWeId = super.createWeId();

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId, privateKey);
        registerAuthorityIssuerArgs.setWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NO_PERMISSION.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the private key is the private key of the members of the committee.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase17() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);
        registerAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey(issuerPrivateList.get(1));

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NO_PERMISSION.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: mock an InterruptedException.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase18() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);

        MockUp<Future<?>> mockFuture = mockInterruptedFuture();
                
        ResponseData<Boolean> response =
            registerAuthorityIssuerForMock(registerAuthorityIssuerArgs, mockFuture);

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: mock an TimeoutException.
     * 
     */
    @Test
    public void testRegisterAuthorityIssuerCase19() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);

        MockUp<Future<?>> mockFuture = mockTimeoutFuture();

        ResponseData<Boolean> response =
            registerAuthorityIssuerForMock(registerAuthorityIssuerArgs, mockFuture);

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    private ResponseData<Boolean> registerAuthorityIssuerForMock(
        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs,
        MockUp<Future<?>> mockFuture) {
        
        MockUp<AuthorityIssuerController> mockTest = new MockUp<AuthorityIssuerController>() {
            @Mock
            public Future<?> addAuthorityIssuer(
                Address addr,
                StaticArray<Bytes32> attribBytes32, 
                StaticArray<Int256> attribInt,
                DynamicBytes accValue) {

                return mockFuture.getMockInstance();
            }
        };

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();
        return response;
    }

    /**
     * case: mock returns null when invoking the getAuthorityIssuerRetLogEvents.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase20() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);

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
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: authorityIssuer.name is too long.
     *
     */
    @Test
    public void testRegisterAuthorityIssuerCase21() {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeIdResult, privateKey);
        registerAuthorityIssuerArgs.getAuthorityIssuer()
            .setName("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        logger.info("registerAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_NAME_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
