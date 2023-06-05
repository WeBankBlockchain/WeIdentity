

package com.webank.weid.full.auth;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.IssuerType;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.blockchain.protocol.response.ResponseData;

/**
 * testing basic method classes.
 *
 * @author rocky
 */
public class TestRegisterIssuerType extends TestBaseService {

    private static final Logger logger = LoggerFactory.getLogger(TestIsAuthorityIssuer.class);

    private static CreateWeIdDataResult createWeId;

    private static final String issuerType = "police office";

    @Override
    public synchronized void testInit() {

        super.testInit();
        if (createWeId == null) {
            createWeId = super.createWeId();
        }

    }

    /**
     * case:Register issuer type success.
     */

    public void testRegisterIssuerType_success() {

        WeIdAuthentication weIdAuthentication = TestBaseUtil
            .buildWeIdAuthentication(createWeId);

        ResponseData<Boolean> response = new ResponseData<>(false,
            ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_EXISTS);

        while (response.getErrorCode()
            == ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_EXISTS.getCode()) {
            response = authorityIssuerService.registerIssuerType(weIdAuthentication, issuerType);
        }
        LogUtil.info(logger, "registerIssuerType", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * case:Register issuer type success.
     */
    @Test
    public void testRegisterIssuerType_successZh() {

        WeIdAuthentication weIdAuthentication = TestBaseUtil
            .buildWeIdAuthentication(createWeId);

        ResponseData<Boolean> response = new ResponseData<>(false,
            ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_EXISTS);

        while (response.getErrorCode()
            == ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_EXISTS.getCode()) {
            String name = "中国" + Math.random();
            response = authorityIssuerService.registerIssuerType(weIdAuthentication, name);
        }
        LogUtil.info(logger, "registerIssuerType", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * case:when issuerType is accii,Register issuer type success.
     */
    @Test
    public void testRegisterIssuerType_successAscii() {

        WeIdAuthentication weIdAuthentication = TestBaseUtil
            .buildWeIdAuthentication(createWeId);

        ResponseData<Boolean> response = new ResponseData<>(false,
            ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_EXISTS);
        char[] chars = new char[10];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (Math.random() % 127);
        }
        while (response.getErrorCode()
            == ErrorCode.SPECIFIC_ISSUER_CONTRACT_ERROR_ALREADY_EXISTS.getCode()) {
            String name = String.valueOf(chars) + Math.random();
            response = authorityIssuerService.registerIssuerType(weIdAuthentication, name);
        }
        LogUtil.info(logger, "registerIssuerType", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult());
    }

    /**
     * case:Register issuer type is blank.
     */
    @Test
    public void testRegisterIssuerType_issuerTypeNull() {

        WeIdAuthentication weIdAuthentication = TestBaseUtil
            .buildWeIdAuthentication(createWeId);

        ResponseData<Boolean> response =
            authorityIssuerService.registerIssuerType(weIdAuthentication, null);
        LogUtil.info(logger, "registerIssuerType", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertFalse(response.getResult());
    }

    /**
     * case:Register issuer type is blank.
     */
    @Test
    public void testRegisterIssuerType_issuerTypeBlank() {

        WeIdAuthentication weIdAuthentication = TestBaseUtil
            .buildWeIdAuthentication(createWeId);

        ResponseData<Boolean> response =
            authorityIssuerService.registerIssuerType(weIdAuthentication, "");
        LogUtil.info(logger, "registerIssuerType", response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertFalse(response.getResult());
    }

    @Test
    public void testRegisterIssuerType_issuerTypeTooLong() {

        WeIdAuthentication weIdAuthentication = TestBaseUtil
            .buildWeIdAuthentication(createWeId);
        char[] chars = new char[33];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (Math.random() % 127);
        }
        String name = String.valueOf(chars);
        ResponseData<Boolean> response =
            authorityIssuerService.registerIssuerType(weIdAuthentication, name);
        LogUtil.info(logger, "registerIssuerType", response);

        Assert.assertEquals(ErrorCode.SPECIFIC_ISSUER_TYPE_ILLEGAL.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(response.getResult());
    }

    /**
     * case: weid not exist.
     */
    @Test
    public void testRegisterIssuerType_weidNotExist() {

        WeIdAuthentication weIdAuthentication = TestBaseUtil
            .buildWeIdAuthentication(createWeId);
        weIdAuthentication.setWeId("did:weid:0xc7e399b8d2da337f4e92eb33ca88b60b899ff022");
        ResponseData<Boolean> response =
            authorityIssuerService.registerIssuerType(weIdAuthentication, "ttrr");
        LogUtil.info(logger, "registerIssuerType", response);

        Assert.assertEquals(ErrorCode.WEID_DOES_NOT_EXIST.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(response.getResult());
    }

    /**
     * case: weid is null.
     */
    @Test
    public void testRegisterIssuerType_weidNull() {

        WeIdAuthentication weIdAuthentication = TestBaseUtil
            .buildWeIdAuthentication(createWeId);
        weIdAuthentication.setWeId(null);
        ResponseData<Boolean> response =
            authorityIssuerService.registerIssuerType(weIdAuthentication, "xx");
        LogUtil.info(logger, "registerIssuerType", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(response.getResult());
    }

    /**
     * case: weid is blank.
     */
    @Test
    public void testRegisterIssuerType_weIdBlank() {

        WeIdAuthentication weIdAuthentication = TestBaseUtil
            .buildWeIdAuthentication(createWeId);
        weIdAuthentication.setWeId("");
        ResponseData<Boolean> response =
            authorityIssuerService.registerIssuerType(weIdAuthentication, "xxx");
        LogUtil.info(logger, "registerIssuerType", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(response.getResult());
    }

    /**
     * case: weid is invalid.
     */
    @Test
    public void testRegisterIssuerType_invalidWeId() {

        WeIdAuthentication weIdAuthentication = TestBaseUtil
            .buildWeIdAuthentication(createWeId);
        weIdAuthentication.setWeId("123sdf中国@#￥%……&*90（）-+、《》·nm");
        ResponseData<Boolean> response =
            authorityIssuerService.registerIssuerType(weIdAuthentication, "xxxxx");
        LogUtil.info(logger, "registerIssuerType", response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(response.getResult());
    }

    /**
     * case: Get RetIssuerType Count.
     */
    @Test
    public void testGetRetIssuerTypeCount() {
        ResponseData<Integer> responseBefore =  authorityIssuerService.getIssuerTypeCount();
        LogUtil.info(logger, "getIssuerTypeCount", responseBefore);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseBefore.getErrorCode().intValue());
        
        String typeName = String.valueOf(System.currentTimeMillis());
        super.registerIssuerType(typeName);

        ResponseData<Integer> responseAfter =  authorityIssuerService.getIssuerTypeCount();
        LogUtil.info(logger, "getIssuerTypeCount", responseAfter);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseAfter.getErrorCode().intValue());
        Assert.assertTrue((responseAfter.getResult() - responseBefore.getResult()) == 1);

        WeIdAuthentication weIdAuthentication = TestBaseUtil.buildWeIdAuthentication(createWeIdNew);
        ResponseData<Boolean> removeRes = authorityIssuerService.removeIssuerType(
            weIdAuthentication, typeName);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), removeRes.getErrorCode().intValue());
        Assert.assertTrue(removeRes.getResult());

        responseAfter =  authorityIssuerService.getIssuerTypeCount();
        LogUtil.info(logger, "getIssuerTypeCount", responseAfter);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseAfter.getErrorCode().intValue());
        Assert.assertTrue(responseAfter.getResult() == responseBefore.getResult());
    }

    @Test
    public void getIssuerTypeList() {
        super.registerIssuerType(String.valueOf(System.currentTimeMillis()));
        ResponseData<List<IssuerType>> response = 
            authorityIssuerService.getIssuerTypeList(0, 1);
        LogUtil.info(logger, "getIssuerTypeList", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(response.getResult().size() == 1);
        Assert.assertNotNull(response.getResult().get(0).getOwner());
        Assert.assertNotNull(response.getResult().get(0).getTypeName());
        Assert.assertNotNull(response.getResult().get(0).getCreated());
    }
}
