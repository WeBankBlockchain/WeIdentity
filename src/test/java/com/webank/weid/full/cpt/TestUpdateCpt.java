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
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.request.UpdateCptArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.WeIdUtils;

public class TestUpdateCpt extends TestBaseServcie {

    /**
     * is register issuer
     */
    private boolean isRegisterAuthorityIssuer = false;

    @Test
    /** case： cpt updateCpt success */
    public void testUpdateCptCase1() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    @Test
    /** case： updateCptArgs is null */
    public void testUpdateCptCase2() {

        ResponseData<CptBaseInfo> response = cptService.updateCpt(null);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.ILLEGAL_INPUT.getCode(), 
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptId is null */
    public void testUpdateCptCase3() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.setCptId(null);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptId is minus number */
    public void testUpdateCptCase4() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.setCptId(-1);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptId is not exists */
    public void testUpdateCptCase5() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.setCptId(10000);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CPT_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptJsonSchema is null */
    public void testUpdateCptCase6() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.setCptJsonSchema(null);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.CPT_JSON_SCHEMA_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptJsonSchema is invalid */
    public void testUpdateCptCase7() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.setCptJsonSchema("xxxxxxxxx");

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptJsonSchema too long */
    public void testUpdateCptCase8() throws JsonProcessingException, IOException {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);

        StringBuffer value = new StringBuffer("");
        for (int i = 0; i < 5000; i++) {
            value.append("x");
        }

        JsonNode jsonNode = new ObjectMapper().readTree(TestData.schema);
        ObjectNode objNode = (ObjectNode) jsonNode;
        objNode.put("title", value.toString());
        String afterStr =
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(objNode);

        updateCptArgs.setCptJsonSchema(afterStr);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.CPT_JSON_SCHEMA_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptPublisher is blank */
    public void testUpdateCptCase9() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.setCptPublisher(null);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptPublisher is invalid */
    public void testUpdateCptCase10() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.setCptPublisher("di:weid:0xaaaaaaaaaaaaaaaa");

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： cptPublisher is not exists and the private key does not match */
    public void testUpdateCptCase11() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.setCptPublisher("did:weid:0xaaaaaaaaaaaaaa");

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());

        ResponseData<Cpt> responseCpt = cptService.queryCpt(cptBaseInfo.getCptId());
        System.out.println("\nqueryCpt result:");
        BeanUtil.print(responseCpt);
    }

    @Test
    /** case： cptPublisherPrivateKey is null */
    public void testUpdateCptCase12() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.setCptPublisherPrivateKey(null);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： private Key is null */
    public void testUpdateCptCase13() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.getCptPublisherPrivateKey().setPrivateKey(null);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： privateKey is invalid */
    public void testUpdateCptCase14() {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.getCptPublisherPrivateKey().setPrivateKey("123132545646878901");

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： privateKey is new privateKey */
    public void testUpdateCptCase15() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.getCptPublisherPrivateKey().setPrivateKey(TestBaseUtil.createEcKeyPair()[1]);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： privateKey belongs to sdk */
    public void testUpdateCptCase16() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.getCptPublisherPrivateKey().setPrivateKey(TestBaseUtil.privKey);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** case： privateKey belongs to new weIdentity DId */
    public void testUpdateCptCase17() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CreateWeIdDataResult createWeIdNew = super.createWeId();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.setCptPublisherPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    @Test
    /** 
     * case： privateKey belongs to new weIdentity DId , cptPublisher is a new WeId 
     * TODO update success,we will deal with the two issue
     *  
     */
    public void testUpdateCptCase18() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CreateWeIdDataResult createWeIdNew = super.createWeId();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.setCptPublisher(createWeIdNew.getWeId());
        updateCptArgs.setCptPublisherPrivateKey(createWeIdNew.getUserWeIdPrivateKey());

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    @Test
    /** case： privateKey is xxxxxxx  */
    public void testUpdateCptCase19() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.getCptPublisherPrivateKey().setPrivateKey("xxxxxxxxxx");

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }
    
    @Test
    /** case： cptPublisher is not exists , but private key matching */
    public void testUpdateCptCase20() throws Exception {

        CreateWeIdDataResult createWeId = super.createWeIdWithSetAttr();

        CptBaseInfo cptBaseInfo = super.registerCpt(createWeId, isRegisterAuthorityIssuer);

        String[] pk = TestBaseUtil.createEcKeyPair();
        String weId = WeIdUtils.convertPublicKeyToWeId(pk[0]);
        
        UpdateCptArgs updateCptArgs = TestBaseUtil.buildUpdateCptArgs(createWeId, cptBaseInfo);
        updateCptArgs.setCptPublisher(weId);
        updateCptArgs.getCptPublisherPrivateKey().setPrivateKey(pk[1]);

        ResponseData<CptBaseInfo> response = cptService.updateCpt(updateCptArgs);
        System.out.println("\nupdateCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(
            ErrorCode.CPT_PUBLISHER_NOT_EXIST.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());

        ResponseData<Cpt> responseCpt = cptService.queryCpt(cptBaseInfo.getCptId());
        System.out.println("\nqueryCpt result:");
        BeanUtil.print(responseCpt);
    }
}
