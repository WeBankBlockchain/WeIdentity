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
import org.junit.Assert;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.full.TestData;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.request.RegisterCptArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.WeIdUtils;

public class TestRegisterCpt extends TestBaseServcie {

    /**
     * is register issuer
     */
    private boolean isRegisterAuthorityIssuer = false;

    @Test
    /** case： cpt register success */
    public void testRegisterCptCase1() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        if (isRegisterAuthorityIssuer) {
            super.registerAuthorityIssuer(createWeId);
        }

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    @Test
    /** case： registerCptArgs is null */
    public void testRegisterCptCase2() {

        ResponseData<CptBaseInfo> response = cptService.registerCpt(null);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.ILLEGAL_INPUT.getCode(), 
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptJsonSchema is null */
    public void testRegisterCptCase3() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        if (isRegisterAuthorityIssuer) {
            super.registerAuthorityIssuer(createWeId);
        }

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.setCptJsonSchema(null);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.CPT_JSON_SCHEMA_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptJsonSchema is invalid */
    public void testRegisterCptCase4() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        if (isRegisterAuthorityIssuer) {
            super.registerAuthorityIssuer(createWeId);
        }

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.setCptJsonSchema("xxxxxxxxx");

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptJsonSchema too long */
    public void testRegisterCptCase5() throws JsonProcessingException, IOException {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        if (isRegisterAuthorityIssuer) {
            super.registerAuthorityIssuer(createWeId);
        }

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

        Assert.assertEquals(
            ErrorCode.CPT_JSON_SCHEMA_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptPublisher is blank */
    public void testRegisterCptCase6() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        if (isRegisterAuthorityIssuer) {
            super.registerAuthorityIssuer(createWeId);
        }

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.setCptPublisher(null);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptPublisher is invalid */
    public void testRegisterCptCase7() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        if (isRegisterAuthorityIssuer) {
            super.registerAuthorityIssuer(createWeId);
        }

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.setCptPublisher("di:weid:0xaaaaaaaaaaaaaaaa");

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptPublisher is not exists and the private key does not match */
    public void testRegisterCptCase8() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        if (isRegisterAuthorityIssuer) {
            super.registerAuthorityIssuer(createWeId);
        }

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.setCptPublisher("did:weid:0xaaaaaaaaaaaaaaaa");

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cpt register again */
    public void testRegisterCptCase9() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        if (isRegisterAuthorityIssuer) {
            super.registerAuthorityIssuer(createWeId);
        }

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

    @Test
    /** case： cptPublisherPrivateKey is null */
    public void testRegisterCptCase10() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        if (isRegisterAuthorityIssuer) {
            super.registerAuthorityIssuer(createWeId);
        }

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.setCptPublisherPrivateKey(null);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： privateKey is null */
    public void testRegisterCptCase11() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        if (isRegisterAuthorityIssuer) {
            super.registerAuthorityIssuer(createWeId);
        }

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.getCptPublisherPrivateKey().setPrivateKey(null);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： privateKey is invalid */
    public void testRegisterCptCase12() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        if (isRegisterAuthorityIssuer) {
            super.registerAuthorityIssuer(createWeId);
        }

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.getCptPublisherPrivateKey().setPrivateKey("1231325456468789");

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： privateKey is new privateKey */
    public void testRegisterCptCase13() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        if (isRegisterAuthorityIssuer) {
            super.registerAuthorityIssuer(createWeId);
        }

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.getCptPublisherPrivateKey()
            .setPrivateKey(TestBaseUtil.createEcKeyPair()[1]);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： privateKey is sdk privateKey */
    public void testRegisterCptCase14() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        if (isRegisterAuthorityIssuer) {
            super.registerAuthorityIssuer(createWeId);
        }

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.getCptPublisherPrivateKey().setPrivateKey(TestBaseUtil.privKey);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： privateKey is xxxxxxxxx */
    public void testRegisterCptCase15() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        if (isRegisterAuthorityIssuer) {
            super.registerAuthorityIssuer(createWeId);
        }

        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.getCptPublisherPrivateKey().setPrivateKey("xxxxxxxxxx");

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }
    
    @Test
    /** case： cptPublisher is not exists and the private key does match */
    public void testRegisterCptCase16() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        if (isRegisterAuthorityIssuer) {
            super.registerAuthorityIssuer(createWeId);
        }

        String[] pk = TestBaseUtil.createEcKeyPair();
        String weId = WeIdUtils.convertPublicKeyToWeId(pk[0]);
        
        RegisterCptArgs registerCptArgs = TestBaseUtil.buildRegisterCptArgs(createWeId);
        registerCptArgs.setCptPublisher(weId);
        registerCptArgs.getCptPublisherPrivateKey().setPrivateKey(pk[1]);

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        System.out.println("\nregisterCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.CPT_PUBLISHER_NOT_EXIST.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }
}
