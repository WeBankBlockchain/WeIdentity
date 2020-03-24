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

package com.webank.weid.full.cpt;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.request.CptMapArgs;
import com.webank.weid.protocol.response.ResponseData;

/**
 * queryCpt method for testing CptService.
 *
 * @author v_wbgyang/rockyxia
 */
public class TestQueryCpt extends TestBaseService {

    private static final Logger logger = LoggerFactory.getLogger(TestQueryCpt.class);

    @Override
    public synchronized void testInit() {

        super.testInit();
        if (cptBaseInfo == null) {
            cptBaseInfo = super.registerCpt(createWeIdResultWithSetAttr);
        }
    }

    /**
     * case： cpt query success .
     */
    @Test
    public void testQueryCpt_success() {

        ResponseData<Cpt> response = cptService.queryCpt(cptBaseInfo.getCptId());
        LogUtil.info(logger, "queryCpt", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case： cptId is null.
     */
    @Test
    public void testQueryCpt_cptIdNull() {

        ResponseData<Cpt> response = cptService.queryCpt(null);
        LogUtil.info(logger, "queryCpt", response);

        Assert.assertEquals(ErrorCode.CPT_ID_ILLEGAL.getCode(), 
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptId is minus number.
     */
    @Test
    public void testQueryCpt_minusCptId() {

        ResponseData<Cpt> response = cptService.queryCpt(-1);
        LogUtil.info(logger, "queryCpt", response);

        Assert.assertEquals(ErrorCode.CPT_ID_ILLEGAL.getCode(), 
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptId is not exists.
     */
    @Test
    public void testQueryCpt_cptIdNotExist() {

        ResponseData<Cpt> response = cptService.queryCpt(999999999);
        LogUtil.info(logger, "queryCpt", response);

        Assert.assertEquals(ErrorCode.CPT_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： cptId is too big.
     */
    @Test
    public void testQueryCpt_cptIdBigger() {

        ResponseData<Cpt> response = cptService.queryCpt(-999999999);
        LogUtil.info(logger, "queryCpt", response);

        Assert.assertEquals(ErrorCode.CPT_ID_ILLEGAL.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case： query after updateCpt.
     */
    @Test
    public void testQueryCpt_afterUpdate() {

        ResponseData<Cpt> response = cptService.queryCpt(cptBaseInfo.getCptId());
        LogUtil.info(logger, "queryCpt", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);

        ResponseData<CptBaseInfo> responseUp = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        LogUtil.info(logger, "updateCpt", responseUp);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseUp.getErrorCode().intValue());
        Assert.assertNotNull(responseUp.getResult());

        ResponseData<Cpt> responseQ = cptService.queryCpt(cptBaseInfo.getCptId());
        LogUtil.info(logger, "queryCpt", responseQ);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseQ.getErrorCode().intValue());
        Assert.assertNotNull(responseQ.getResult());
    }

}
