

package com.webank.weid.full.weid;

import java.security.NoSuchProviderException;

import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import mockit.Mock;
import mockit.MockUp;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.blockchain.protocol.response.ResponseData;

/**
 * non parametric createWeId method for testing WeIdService.
 *
 * @author v_wbgyang
 */
public class TestCreateWeId1 extends TestBaseService {

    private static final Logger logger = LoggerFactory.getLogger(TestCreateWeId1.class);

    /**
     * case: create WeId success.
     */
    @Test
    public void testCreateWeId_createSuccess() {

        ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();
        LogUtil.info(logger, "createWeId", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case:run CreateWeId twice Will create two different WeId.
     */
    @Test
    public void testCreateWeId_doubleCreateSuccess() {

        ResponseData<CreateWeIdDataResult> response = weIdService.createWeId();
        LogUtil.info(logger, "createWeId", response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
        ResponseData<CreateWeIdDataResult> response1 = weIdService.createWeId();
        LogUtil.info(logger, "createWeId", response1);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertNotNull(response1.getResult());
        Assert.assertNotEquals(response.getResult().getWeId(), response1.getResult().getWeId());
    }

    /**
     * case: create WeId success.
     */
    @Test
    public void testCreateWeIdByPublicKey_createSuccess() {
        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        WeIdPublicKey weIdPublicKey = new WeIdPublicKey();
        weIdPublicKey.setPublicKey(createWeIdArgs.getPublicKey());
        ResponseData<String> response = weIdService.createWeIdByPublicKey(weIdPublicKey, createWeIdArgs.getWeIdPrivateKey());
        LogUtil.info(logger, "createWeId", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case: create WeId success, public key not match with private key.
     */
    @Test
    public void testCreateWeIdByPublicKey_createSuccess2() {
        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        WeIdPublicKey weIdPublicKey = new WeIdPublicKey();
        weIdPublicKey.setPublicKey(createWeIdArgs.getPublicKey());
        ResponseData<String> response = weIdService.createWeIdByPublicKey(weIdPublicKey, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createWeId", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

}
