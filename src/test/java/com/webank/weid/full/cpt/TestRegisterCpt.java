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

package com.webank.weid.full.cpt;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import com.fasterxml.jackson.core.JsonProcessingException;
import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint8;
import org.bcos.web3j.protocol.core.methods.response.Transaction;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.common.PasswordKey;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.JsonSchemaConstant;
import com.webank.weid.contract.v1.CptController;
import com.webank.weid.contract.v1.CptController.RegisterCptRetLogEventResponse;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.request.CptMapArgs;
import com.webank.weid.protocol.request.CptStringArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.RawTransactionService;
import com.webank.weid.service.impl.RawTransactionServiceImpl;
import com.webank.weid.util.TransactionUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * registerCpt method for testing CptService.
 *
 * @author v_wbgyang
 */
public class TestRegisterCpt extends TestBaseServcie {

    private static final Logger logger = LoggerFactory.getLogger(TestRegisterCpt.class);

    private static CreateWeIdDataResult createWeId = null;

    @Override
    public synchronized void testInit() {

        super.testInit();
        if (createWeId == null) {
            createWeId = super.createWeId();
            super.registerAuthorityIssuer(createWeId);
        }
    }

    /**
     * case： cpt register success.
     */
    @Test
    public void testRegisterCptCase1() {

        CptMapArgs registerCptArgs = TestBaseUtil.buildCptArgs(createWeId);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
        Transaction transaction = TransactionUtils
            .getTransaction(response.getTransactionInfo());
        Assert.assertNotNull(transaction);
        Assert.assertFalse(WeIdUtils.isEmptyAddress(new Address(transaction.getFrom())));
        Assert.assertFalse(WeIdUtils.isEmptyAddress(new Address(transaction.getTo())));
    }

    /**
     * case： registerCptArgs is null.
     */
    @Test
    public void testRegisterCptCase2() {

        CptMapArgs cptMapArgs = null;
        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptJsonSchema is null.
     */
    @Test
    public void testRegisterCptCase3() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);
        cptMapArgs.setCptJsonSchema(null);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.CPT_JSON_SCHEMA_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： Mock for ErrorCode.UNKNOW_ERROR.
     */
    @Test
    public void testRegisterCptCase4() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);

        MockUp<CptController> mockTest = new MockUp<CptController>() {
            @Mock
            public Future<TransactionReceipt> registerCpt(
                Address publisher,
                StaticArray<Int256> intArray,
                StaticArray<Bytes32> bytes32Array,
                StaticArray<Bytes32> jsonSchemaArray,
                Uint8 v,
                Bytes32 r,
                Bytes32 s) {
                return null;
            }
        };

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptJsonSchema too long.
     */
    @Test
    public void testRegisterCptCase5() throws JsonProcessingException, IOException {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);

        StringBuffer value = new StringBuffer("");
        for (int i = 0; i < 5000; i++) {
            value.append("x");
        }

        HashMap<String, Object> cptJsonSchema = TestBaseUtil.buildCptJsonSchema();
        cptJsonSchema.put(JsonSchemaConstant.TITLE_KEY, value.toString());
        cptMapArgs.setCptJsonSchema(cptJsonSchema);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.CPT_JSON_SCHEMA_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptPublisher is blank.
     */
    @Test
    public void testRegisterCptCase6() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);
        cptMapArgs.getWeIdAuthentication().setWeId(null);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptPublisher is invalid.
     */
    @Test
    public void testRegisterCptCase7() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);
        cptMapArgs.getWeIdAuthentication().setWeId("di:weid:0xaaaaaaaaaaaaaaaa");

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptPublisher is not exists and the private key does not match.
     */
    @Test
    public void testRegisterCptCase8() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);
        cptMapArgs.getWeIdAuthentication()
            .setWeId("did:weid:0xbb1670306aedfaeb75cff9581c99e56ba4797431");

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cpt register again.
     */
    @Test
    public void testRegisterCptCase9() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt again", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case： cptPublisherPrivateKey is null.
     */
    @Test
    public void testRegisterCptCase10() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);
        cptMapArgs.getWeIdAuthentication().setWeIdPrivateKey(null);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： privateKey is null.
     */
    @Test
    public void testRegisterCptCase11() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);
        cptMapArgs.getWeIdAuthentication().getWeIdPrivateKey().setPrivateKey(null);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： privateKey is invalid.
     */
    @Test
    public void testRegisterCptCase12() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);
        cptMapArgs.getWeIdAuthentication().getWeIdPrivateKey().setPrivateKey("1231325456468789");

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： privateKey is new privateKey.
     */
    @Test
    public void testRegisterCptCase13() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);
        cptMapArgs.getWeIdAuthentication().getWeIdPrivateKey()
            .setPrivateKey(TestBaseUtil.createEcKeyPair().getPrivateKey());

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： privateKey is SDK privateKey.
     */
    @Test
    public void testRegisterCptCase14() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);
        cptMapArgs.getWeIdAuthentication().getWeIdPrivateKey().setPrivateKey(privateKey);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： privateKey is xxxxxxxxx.
     */
    @Test
    public void testRegisterCptCase15() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);
        cptMapArgs.getWeIdAuthentication().getWeIdPrivateKey().setPrivateKey("xxxxxxxxxx");

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptPublisher is not exists and the private key is match.
     */
    @Test
    public void testRegisterCptCase16() {

        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();
        String weId = WeIdUtils.convertPublicKeyToWeId(passwordKey.getPublicKey());

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);
        cptMapArgs.getWeIdAuthentication().setWeId(weId);
        cptMapArgs.getWeIdAuthentication()
            .getWeIdPrivateKey()
            .setPrivateKey(passwordKey.getPrivateKey());

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.CPT_PUBLISHER_NOT_EXIST.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： mock an InterruptedException.
     */
    @Test
    public void testRegisterCptCase17() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);

        MockUp<Future<?>> mockFuture = mockInterruptedFuture();

        ResponseData<CptBaseInfo> response = registerCptForMock(cptMapArgs, mockFuture);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： mock an TimeoutException.
     */
    @Test
    public void testRegisterCptCase18() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);

        MockUp<Future<?>> mockFuture = mockTimeoutFuture();

        ResponseData<CptBaseInfo> response = registerCptForMock(cptMapArgs, mockFuture);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    private ResponseData<CptBaseInfo> registerCptForMock(
        CptMapArgs cptMapArgs,
        MockUp<Future<?>> mockFuture) {

        MockUp<CptController> mockTest = new MockUp<CptController>() {
            @Mock
            public Future<?> registerCpt(
                Address publisher,
                StaticArray<Int256> intArray,
                StaticArray<Bytes32> bytes32Array,
                StaticArray<Bytes32> jsonSchemaArray,
                Uint8 v,
                Bytes32 r,
                Bytes32 s) {
                return mockFuture.getMockInstance();
            }
        };

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        mockTest.tearDown();
        mockFuture.tearDown();
        return response;
    }

    /**
     * case： mock returns null.
     */
    @Test
    public void testRegisterCptCase19() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);

        MockUp<CptController> mockTest = new MockUp<CptController>() {
            @Mock
            public List<RegisterCptRetLogEventResponse> getRegisterCptRetLogEvents(
                TransactionReceipt transactionReceipt) {
                return null;
            }
        };

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        mockTest.tearDown();

        Assert.assertEquals(
            ErrorCode.CPT_EVENT_LOG_NULL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： build cpt string.
     */
    @Test
    public void testRegisterCptCase20() throws IOException {

        CptStringArgs cptStringArgs =
            TestBaseUtil.buildCptStringArgs(createWeId, false);
        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptStringArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case： build cpt string, args is null.
     */
    @Test
    public void testRegisterCptCase21() {

        CptStringArgs cptStringArgs = null;
        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptStringArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： build cpt string for unkonw_error.
     */
    @Test
    public void testRegisterCptCase22() throws IOException {

        CptStringArgs cptStringArgs =
            TestBaseUtil.buildCptStringArgs(createWeId, false);
        cptStringArgs.setCptJsonSchema("xxxxx");
        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptStringArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(
            ErrorCode.UNKNOW_ERROR.getCode(),
            response.getErrorCode().intValue()
        );
        Assert.assertNull(response.getResult());
    }

    /**
     * case： build cpt string by file.
     */
    @Test
    public void testRegisterCptCase23() throws IOException {

        CptStringArgs cptStringArgs =
            TestBaseUtil.buildCptStringArgs(createWeId, true);
        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptStringArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
        Transaction transaction = TransactionUtils
            .getTransaction(response.getTransactionInfo());
        Assert.assertNotNull(transaction);
        Assert.assertFalse(WeIdUtils.isEmptyAddress(new Address(transaction.getFrom())));
        Assert.assertFalse(WeIdUtils.isEmptyAddress(new Address(transaction.getTo())));
    }

    /**
     * case： cptPublisher is blank.
     */
    @Test
    public void testRegisterCptCase27() {

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);
        cptMapArgs.setWeIdAuthentication(null);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptMapArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(
            ErrorCode.WEID_AUTHORITY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case: call transactionhex method - arbitrary.
     */
    @Test
    public void testRegisterCptCase28() {
        String hex = StringUtils.EMPTY;
        RawTransactionService rawTransactionService = new RawTransactionServiceImpl();
        ResponseData<String> response = rawTransactionService.registerCpt(hex);
        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertTrue(StringUtils.isEmpty(response.getResult()));
    }

    /**
     * case: call transactionhex method - arbitrary.
     */
    @Test
    public void testRegisterCptCase29() {
        String hex = "11111";
        RawTransactionService rawTransactionService = new RawTransactionServiceImpl();
        ResponseData<String> response = rawTransactionService.registerCpt(hex);
        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertTrue(StringUtils.isEmpty(response.getResult()));
    }

    /**
     * case: register cpt id w/ and w/o permission.
     */
    @Test
    public void testRegisterCptWithIdPermission() {
        // The "system" CPT ID
        Integer keyCptId = 50;
        CptMapArgs registerCptArgs = TestBaseUtil.buildCptArgs(createWeId);
        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs, keyCptId);
        LogUtil.info(logger, "registerCpt", response);
        Assert.assertEquals(ErrorCode.CPT_NO_PERMISSION.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());

        // The authority issuer related cpt ID
        Integer issuerCptId = 1200000;
        while (cptService.queryCpt(issuerCptId).getResult() != null) {
            issuerCptId += (int) (Math.random() * 10 + 1);
        }
        ResponseData<CptBaseInfo> responseData = cptService
            .registerCpt(registerCptArgs, issuerCptId);
        LogUtil.info(logger, "registerCpt", responseData);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseData.getErrorCode().intValue());
        Assert.assertNotNull(responseData.getResult());

        ResponseData<CptBaseInfo> errResponse = cptService.registerCpt(registerCptArgs, null);
        Assert
            .assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), errResponse.getErrorCode().intValue());
    }

    /**
     * case: register cpt id success. Query multiple times until find an available id, register
     * successfully, and retry with an expected failure.
     */
    @Test
    public void testRegisterCptWithIdSuccessAndDuplicate() {
        Integer cptId = 6000000;
        // Add randomness in the next available cpt number - also for faster test cycles
        while (cptService.queryCpt(cptId).getResult() != null) {
            cptId += (int) (Math.random() * 50 + 1);
        }
        CptMapArgs registerCptArgs = TestBaseUtil.buildCptArgs(createWeId);
        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs, cptId);
        LogUtil.info(logger, "registerCpt", response);
        Assert.assertEquals(response.getErrorCode().intValue(), ErrorCode.SUCCESS.getCode());
        Assert.assertNotNull(response.getResult());

        // do it twice
        ResponseData<CptBaseInfo> responseData = cptService.registerCpt(registerCptArgs, cptId);
        LogUtil.info(logger, "registerCpt", responseData);
        Assert.assertEquals(responseData.getErrorCode().intValue(),
            ErrorCode.CPT_ALREADY_EXIST.getCode());
        Assert.assertNull(responseData.getResult());
    }

    /**
     * case: register cpt id with string args.
     */
    @Test
    public void testRegisterCptStringWithId() throws Exception {
        Integer issuerCptId = 1000000;
        while (cptService.queryCpt(issuerCptId).getResult() != null) {
            issuerCptId += (int) (Math.random() * 10 + 1);
        }
        CptStringArgs cptStringArgs =
            TestBaseUtil.buildCptStringArgs(createWeId, false);
        ResponseData<CptBaseInfo> response = cptService.registerCpt(cptStringArgs, issuerCptId);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        ResponseData<CptBaseInfo> responseData = cptService.registerCpt(cptStringArgs, null);
        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(),
            responseData.getErrorCode().intValue());
    }
}
