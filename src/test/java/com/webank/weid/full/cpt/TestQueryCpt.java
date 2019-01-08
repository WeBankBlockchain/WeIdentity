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

import java.util.List;
import java.util.concurrent.Future;

import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.abi.datatypes.DynamicArray;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.CptController;
import com.webank.weid.exception.DataTypeCastException;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.Cpt;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.request.CptMapArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.DataTypetUtils;

/**
 * queryCpt method for testing CptService.
 * 
 * @author v_wbgyang
 *
 */
public class TestQueryCpt extends TestBaseServcie {
    
    private static final Logger logger = LoggerFactory.getLogger(TestQueryCpt.class);

    @Override
    public void testInit() {

        super.testInit();
        if (null == cptBaseInfo) {
            cptBaseInfo = super.registerCpt(createWeIdResultWithSetAttr);
        }
    }

    /** 
     * case： cpt query success .
     */
    @Test
    public void testQueryCptCase1() {

        ResponseData<Cpt> response = cptService.queryCpt(cptBaseInfo.getCptId());
        logger.info("queryCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /** 
     * case： cptId is null.
     */
    @Test
    public void testQueryCptCase2() {

        ResponseData<Cpt> response = cptService.queryCpt(null);
        logger.info("queryCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： cptId is minus number.
     */
    @Test
    public void testQueryCptCase3() {

        ResponseData<Cpt> response = cptService.queryCpt(-1);
        logger.info("queryCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： cptId is not exists.
     */
    @Test
    public void testQueryCptCase4() {

        ResponseData<Cpt> response = cptService.queryCpt(100000);
        logger.info("queryCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.CPT_NOT_EXISTS.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /** 
     * case： query after updateCpt.
     */
    @Test
    public void testQueryCptCase5() {

        ResponseData<Cpt> response = cptService.queryCpt(cptBaseInfo.getCptId());
        logger.info("queryCpt result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        CptMapArgs cptMapArgs = TestBaseUtil.buildCptArgs(createWeIdNew);

        ResponseData<CptBaseInfo> responseUp = cptService.updateCpt(
            cptMapArgs,
            cptBaseInfo.getCptId());
        logger.info("updateCpt result:");
        BeanUtil.print(responseUp);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseUp.getErrorCode().intValue());
        Assert.assertNotNull(responseUp.getResult());

        ResponseData<Cpt> responseQ = cptService.queryCpt(cptBaseInfo.getCptId());
        logger.info("queryCpt result:");
        BeanUtil.print(responseQ);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseQ.getErrorCode().intValue());
        Assert.assertNotNull(responseQ.getResult());
    }

    /**
     * case: mock an InterruptedException.
     */
    @Test
    public void testQueryCptCase6() {

        MockUp<Future<?>> mockFuture = mockInterruptedFuture();

        MockUp<CptController> mockTest = new MockUp<CptController>() {
            @Mock
            public Future<?> queryCpt(Uint256 cptId) {
                return mockFuture.getMockInstance();
            }
        };

        ResponseData<Cpt> response = cptService.queryCpt(cptBaseInfo.getCptId());
        logger.info("queryCpt result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case: mock returns null.
     */
    @Test
    public void testQueryCptCase7() {

        MockUp<CptController> mockTest = new MockUp<CptController>() {
            @Mock
            public Future<List<Type<?>>> queryCpt(Uint256 cptId) {
                return null;
            }
        };

        ResponseData<Cpt> response = cptService.queryCpt(cptBaseInfo.getCptId());
        logger.info("queryCpt result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }

    /**
     * case: mock DataTypetUtils.bytes32DynamicArrayToStringArrayWithoutTrim()
     *      for DataTypeCastException.DataTypeCastException()
     */
    @Test
    public void testQueryCptCase8() {

        MockUp<DataTypetUtils> mockTest = new MockUp<DataTypetUtils>() {
            @Mock
            public String[] bytes32DynamicArrayToStringArrayWithoutTrim(
                DynamicArray<Bytes32> bytes32DynamicArray)
                throws DataTypeCastException {
                WeIdBaseException e = new WeIdBaseException(
                    "mock DataTypeCastException for coverage.");
                logger.error("testQueryCptCase8:{}", e.toString(), e);
                throw new DataTypeCastException(e);
            }
        };

        ResponseData<Cpt> response = cptService.queryCpt(cptBaseInfo.getCptId());
        logger.info("queryCpt result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertNull(response.getResult());
    }


}
