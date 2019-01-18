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

package com.webank.weid.full.cpt;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Future;

import com.fasterxml.jackson.core.JsonProcessingException;
import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.bcos.web3j.abi.datatypes.generated.Uint8;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.common.PasswordKey;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.JsonSchemaConstant;
import com.webank.weid.contract.CptController;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.request.CptMapArgs;
import com.webank.weid.protocol.request.CptStringArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.WeIdUtils;

/**
 * updateCpt method for testing CptService.
 * 
 * @author v_wbgyang
 *
 */
public class TestUpdateCpt extends TestBaseServcie {

    private static final Logger logger = LoggerFactory.getLogger(TestUpdateCpt.class);

    @Override
    public void testInit() {

        super.testInit();
        if (null == cptBaseInfo) {
            cptBaseInfo = super.registerCpt(createWeIdResultWithSetAttr);
        }
    }

    /**
     * case： cpt updateCpt success.
     */
    @Test
    public void testUpdateCptCase1() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId);

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeId);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case： updateCptArgs is null.
     */
    @Test
    public void testUpdateCptCase2() {

        CptMapArgs cptMapArgs = null;
        ResponseData<CptBaseInfo> response = cptService.updateCpt(cptMapArgs, null);
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptId is null.
     */
    @Test
    public void testUpdateCptCase3() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(cptMapArgs, null);
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CPT_ID_NULL.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptId is minus number.
     */
    @Test
    public void testUpdateCptCase4() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(cptMapArgs, -1);
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptId is not exists.
     */
    @Test
    public void testUpdateCptCase5() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(cptMapArgs, 10000);
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CPT_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptJsonSchema is null.
     */
    @Test
    public void testUpdateCptCase6() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);
        cptMapArgs.setCptJsonSchema(null);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CPT_JSON_SCHEMA_NULL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptJsonSchema is invalid.
     */
    @Test
    public void testUpdateCptCase7() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);

        MockUp<CptController> mockTest = new MockUp<CptController>() {
            @Mock
            public Future<TransactionReceipt> updateCpt(
                Uint256 cptId,
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

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptJsonSchema too long.
     */
    @Test
    public void testUpdateCptCase8() throws JsonProcessingException, IOException {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);

        StringBuffer value = new StringBuffer("");
        for (int i = 0; i < 5000; i++) {
            value.append("x");
        }

        HashMap<String, Object> cptJsonSchema = TestBaseUtil.buildCptJsonSchema();
        cptJsonSchema.put(JsonSchemaConstant.TITLE_KEY, value.toString());
        cptMapArgs.setCptJsonSchema(cptJsonSchema);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CPT_JSON_SCHEMA_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptPublisher is blank.
     */
    @Test
    public void testUpdateCptCase9() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);
        cptMapArgs.getWeIdAuthentication().setWeId(null);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptPublisher is invalid.
     */
    @Test
    public void testUpdateCptCase10() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);
        cptMapArgs.getWeIdAuthentication().setWeId("di:weid:0xaaaaaaaaaaaaaaaa");

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptPublisher is not exists and the private key does not match.
     */
    @Test
    public void testUpdateCptCase11() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);
        cptMapArgs.getWeIdAuthentication().setWeId("did:weid:0xaaaaaaaaaaaaaa");

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());

        ResponseData<Cpt> responseCpt = cptService.queryCpt(cptBaseInfo.getCptId());
        logger.info("queryCpt result:");
        BeanUtil.print(responseCpt);
    }

    /**
     * case： cptPublisherPrivateKey is null.
     */
    @Test
    public void testUpdateCptCase12() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);
        cptMapArgs.getWeIdAuthentication().setWeIdPrivateKey(null);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： private Key is null.
     */
    @Test
    public void testUpdateCptCase13() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);
        cptMapArgs.getWeIdAuthentication().getWeIdPrivateKey().setPrivateKey(null);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： privateKey is invalid.
     */
    @Test
    public void testUpdateCptCase14() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);
        cptMapArgs.getWeIdAuthentication().getWeIdPrivateKey().setPrivateKey("123132545646878901");

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： privateKey is new privateKey.
     */
    @Test
    public void testUpdateCptCase15() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);
        cptMapArgs.getWeIdAuthentication().getWeIdPrivateKey()
            .setPrivateKey(TestBaseUtil.createEcKeyPair().getPrivateKey());

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： privateKey belongs to SDK.
     */
    @Test
    public void testUpdateCptCase16() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);
        cptMapArgs.getWeIdAuthentication().getWeIdPrivateKey().setPrivateKey(privateKey);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： privateKey belongs to new WeIdentity DID.
     */
    @Test
    public void testUpdateCptCase17() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);
        cptMapArgs.getWeIdAuthentication().setWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： privateKey belongs to new WeIdentity DID , cptPublisher is a new WeId.
     * [TIP] update success,we will deal with the two issue.
     *
     */
    @Test
    public void testUpdateCptCase18() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResult);
        cptMapArgs.getWeIdAuthentication().setWeId(createWeIdNew.getWeId());
        cptMapArgs.getWeIdAuthentication().setWeIdPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case： privateKey is xxxxxxx.
     */
    @Test
    public void testUpdateCptCase19() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);
        cptMapArgs.getWeIdAuthentication().getWeIdPrivateKey().setPrivateKey("xxxxxxxxxx");

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptPublisher is not exists , but private key matching.
     */
    @Test
    public void testUpdateCptCase20() {

        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();
        String weId = WeIdUtils.convertPublicKeyToWeId(passwordKey.getPublicKey());

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);
        cptMapArgs.getWeIdAuthentication().setWeId(weId);
        cptMapArgs.getWeIdAuthentication()
            .getWeIdPrivateKey()
            .setPrivateKey(passwordKey.getPrivateKey());

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CPT_PUBLISHER_NOT_EXIST.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());

        ResponseData<Cpt> responseCpt = cptService.queryCpt(cptBaseInfo.getCptId());
        logger.info("queryCpt result:");
        BeanUtil.print(responseCpt);
    }

    /**
     * case： mock an InterruptedException.
     */
    @Test
    public void testUpdateCptCase21() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);

        MockUp<Future<?>> mockFuture = mockInterruptedFuture();

        ResponseData<CptBaseInfo> response = updateCptForMock(cptMapArgs, mockFuture);

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    private ResponseData<CptBaseInfo> updateCptForMock(
        CptMapArgs cptMapArgs,
        MockUp<Future<?>> mockFuture) {

        MockUp<CptController> mockTest = new MockUp<CptController>() {
            @Mock
            public Future<?> updateCpt(
                Uint256 cptId,
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

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();
        return response;
    }

    /**
     * case： mock an TimeoutException.
     */
    @Test
    public void testUpdateCptCase22() {

        CptMapArgs cptMapArgs =
            TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);

        MockUp<Future<?>> mockFuture = mockTimeoutFuture();

        ResponseData<CptBaseInfo> response = updateCptForMock(cptMapArgs, mockFuture);

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： build cpt string.
     */
    @Test
    public void testUpdateCptCase23() throws IOException {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId);

        CptStringArgs cptStringArgs =
            TestBaseUtil.buildCptStringArgs(createWeId, false);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptStringArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case： build cpt string, args is null.
     */
    @Test
    public void testUpdateCptCase24() {

        CptStringArgs cptStringArgs = null;

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptStringArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： build cpt string for unkonw_error.
     */
    @Test
    public void testUpdateCptCase25() throws IOException {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId);

        CptStringArgs cptStringArgs =
            TestBaseUtil.buildCptStringArgs(createWeId, false);
        cptStringArgs.setCptJsonSchema("xxxx");

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptStringArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： build cpt string by file.
     */
    @Test
    public void testUpdateCptCase26() throws IOException {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId);

        CptStringArgs cptStringArgs =
            TestBaseUtil.buildCptStringArgs(createWeId, true);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(
            cptStringArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

}
