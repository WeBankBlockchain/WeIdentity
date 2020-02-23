/*
 *       Copyright© (2018) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.full.weid;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.protocol.response.ResponseData;

/**
 * isWeIdExist method for testing WeIdService.
 *
 * @author v_wbgyang
 */
public class TestIsWeIdExist extends TestBaseService {

    private static final Logger logger = LoggerFactory.getLogger(TestIsWeIdExist.class);

    /**
     * case: WeIdentity DID is Exist.
     */
    @Test
    public void testIsWeIdExist_sucess() {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist(createWeIdResult.getWeId());
        LogUtil.info(logger, "isWeIdExist", response1);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(true, response1.getResult());
    }

    /**
     * case: WeIdentity DID is not Exist.
     */
    @Test
    public void testIsWeIdExist_weIdNotExist() {

        String weId = createWeIdResult.getWeId();
        weId = weId.replace(weId.substring(weId.length() - 4, weId.length()), "fefe");
        ResponseData<Boolean> response1 = weIdService.isWeIdExist(weId);
        LogUtil.info(logger, "isWeIdExist", response1);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    /**
     * case: WeIdentity DID is empty.
     */
    @Test
    public void testIsWeIdExist_weIdNull() {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist(null);
        LogUtil.info(logger, "isWeIdExist", response1);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    /**
     * case: WeIdentity DID is blank.
     */
    @Test
    public void testIsWeIdExist_weIdBlank() {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist("");
        LogUtil.info(logger, "isWeIdExist", response1);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    /**
     * case: WeIdentity DID is invalid.
     */
    @Test
    public void testIsWeIdExist_invalidWeId() {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist("xxxxxx");
        LogUtil.info(logger, "isWeIdExist", response1);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    /**
     * case: WeIdentity DID contain zh.
     */
    @Test
    public void testIsWeIdExist_weIdZh() {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist("你好");
        LogUtil.info(logger, "isWeIdExist", response1);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    /**
     * case: WeIdentity DID is Exist.
     */
    @Test
    public void testIsWeIdExistCase5() {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist(createWeIdResult.getWeId());
        LogUtil.info(logger, "isWeIdExist", response1);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(true, response1.getResult());
    }
}
