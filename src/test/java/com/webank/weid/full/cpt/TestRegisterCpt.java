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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.CptController;
import com.webank.weid.contract.CptController.RegisterCptRetLogEventResponse;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.full.TestData;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.request.RegisterCptArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.WeIdUtils;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.StaticArray;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.bcos.web3j.abi.datatypes.generated.Uint8;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.junit.Assert;
import org.junit.Test;

/**
 * registerCpt method for testing CptService.
 * 
 * @author v_wbgyang
 *
 */
public class TestRegisterCpt extends TestBaseServcie {

    private static CreateWeIdDataResult createWeId = null;

    @Override
    public void testInit() throws Exception {

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

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /** 
     * case： registerCptArgs is null.
     */
    @Test
    public void testRegisterCptCase2() {

        ResponseData<CptBaseInfo> response = cptService.registerCpt(null);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： cptJsonSchema is null.
     */
    @Test
    public void testRegisterCptCase3() {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.setCptJsonSchema(null);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CPT_JSON_SCHEMA_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： cptJsonSchema is invalid.
     */
    @Test
    public void testRegisterCptCase4() {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.setCptJsonSchema("xxxxxxxxx");

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： cptJsonSchema too long.
     */
    @Test
    public void testRegisterCptCase5() throws JsonProcessingException, IOException {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);

        StringBuffer value = new StringBuffer("");
        for (int i = 0; i < 5000; i++) {
            value.append("x");
        }

        JsonNode jsonNode = new ObjectMapper().readTree(TestData.schema);
        ObjectNode objNode = (ObjectNode) jsonNode;
        objNode.put("title", value.toString());
        String afterStr =
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(objNode);

        registerCptArgs.setCptJsonSchema(afterStr);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CPT_JSON_SCHEMA_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： cptPublisher is blank.
     */
    @Test
    public void testRegisterCptCase6() {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.setCptPublisher(null);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： cptPublisher is invalid.
     */
    @Test
    public void testRegisterCptCase7() {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.setCptPublisher("di:weid:0xaaaaaaaaaaaaaaaa");

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： cptPublisher is not exists and the private key does not match.
     */
    @Test
    public void testRegisterCptCase8() {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.setCptPublisher("did:weid:0xaaaaaaaaaaaaaaaa");

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： cpt register again.
     */
    @Test
    public void testRegisterCptCase9() {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /** 
     * case： cptPublisherPrivateKey is null.
     */
    @Test
    public void testRegisterCptCase10() {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.setCptPublisherPrivateKey(null);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： privateKey is null.
     */
    @Test
    public void testRegisterCptCase11() {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.getCptPublisherPrivateKey().setPrivateKey(null);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： privateKey is invalid.
     */
    @Test
    public void testRegisterCptCase12() {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.getCptPublisherPrivateKey().setPrivateKey("1231325456468789");

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： privateKey is new privateKey.
     * 
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterCptCase13() throws Exception {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.getCptPublisherPrivateKey()
            .setPrivateKey(TestBaseUtil.createEcKeyPair()[1]);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： privateKey is SDK privateKey.
     */
    @Test
    public void testRegisterCptCase14() {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.getCptPublisherPrivateKey().setPrivateKey(TestBaseUtil.privKey);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： privateKey is xxxxxxxxx.
     */
    @Test
    public void testRegisterCptCase15() {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.getCptPublisherPrivateKey().setPrivateKey("xxxxxxxxxx");

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： cptPublisher is not exists and the private key does match.
     * 
     * @throws Exception may be throw Exception
     */
    @Test
    public void testRegisterCptCase16() throws Exception {

        String[] pk = TestBaseUtil.createEcKeyPair();
        String weId = WeIdUtils.convertPublicKeyToWeId(pk[0]);

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.setCptPublisher(weId);
        registerCptArgs.getCptPublisherPrivateKey().setPrivateKey(pk[1]);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CPT_PUBLISHER_NOT_EXIST.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： mock an InterruptedException.
     */
    @Test
    public void testRegisterCptCase17() {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);

        final MockUp<Future<TransactionReceipt>> mockFuture =
            new MockUp<Future<TransactionReceipt>>() {
                @Mock
                public Future<TransactionReceipt> get(long timeout, TimeUnit unit)
                    throws Exception {
                    throw new InterruptedException();
                }
            };

        MockUp<CptController> mockTest = new MockUp<CptController>() {
            @Mock
            public Future<TransactionReceipt> registerCpt(
                Address publisher,
                StaticArray<Int256> intArray,
                StaticArray<Bytes32> bytes32Array,
                StaticArray<Bytes32> jsonSchemaArray,
                Uint8 v,
                Bytes32 r,
                Bytes32 s)
                throws Exception {
                return mockFuture.getMockInstance();
            }
        };

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： mock an TimeoutException.
     */
    @Test
    public void testRegisterCptCase18() {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);

        final MockUp<Future<TransactionReceipt>> mockFuture =
            new MockUp<Future<TransactionReceipt>>() {
                @Mock
                public Future<TransactionReceipt> get(long timeout, TimeUnit unit)
                    throws Exception {
                    throw new TimeoutException();
                }
            };

        MockUp<CptController> mockTest = new MockUp<CptController>() {
            @Mock
            public Future<TransactionReceipt> registerCpt(
                Address publisher,
                StaticArray<Int256> intArray,
                StaticArray<Bytes32> bytes32Array,
                StaticArray<Bytes32> jsonSchemaArray,
                Uint8 v,
                Bytes32 r,
                Bytes32 s)
                throws Exception {
                return mockFuture.getMockInstance();
            }
        };

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： mock returns null.
     */
    @Test
    public void testRegisterCptCase19() {

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);

        MockUp<CptController> mockTest = new MockUp<CptController>() {
            @Mock
            public List<RegisterCptRetLogEventResponse> getRegisterCptRetLogEvents(
                TransactionReceipt transactionReceipt) {
                return null;
            }
        };

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }
}
