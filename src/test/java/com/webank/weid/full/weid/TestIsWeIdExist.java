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

package com.webank.weid.full.weid;

import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.Bool;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.WeIdContract;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.protocol.response.ResponseData;

/**
 * isWeIdExist method for testing WeIdService.
 * 
 * @author v_wbgyang
 *
 */
public class TestIsWeIdExist extends TestBaseServcie {
    
    private static final Logger logger = LoggerFactory.getLogger(TestIsWeIdExist.class);

    /**
     * case: WeIdentity DID is Exist.
     *
     */
    @Test
    public void testIsWeIdExistCase1() {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist(createWeIdResult.getWeId());
        logger.info("isWeIdExist result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(true, response1.getResult());
    }

    /**
     * case: WeIdentity DID is empty.
     *
     */
    @Test
    public void testIsWeIdExistCase2() {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist(null);
        logger.info("isWeIdExist result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    /**
     * case: WeIdentity DID is invalid.
     *
     */
    @Test
    public void testIsWeIdExistCase3() {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist("xxxxxx");
        logger.info("isWeIdExist result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    /**
     * case: WeIdentity DID is not exist.
     *
     */
    @Test
    public void testIsWeIdExistCase4() {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist("did:weid:xxxxxx");
        logger.info("isWeIdExist result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    /**
     * case: WeIdentity DID is Exist.
     *
     */
    @Test
    public void testIsWeIdExistCase5() {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist(createWeIdResult.getWeId());
        logger.info("isWeIdExist result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(true, response1.getResult());
    }

    /**
     * case: Simulation throws an TimeoutException when calling the isIdentityExist
     *       method.
     *
     */
    @Test
    public void testIsWeIdExistCase6() {

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public Future<Bool> isIdentityExist(Address identity) throws TimeoutException {
                throw new TimeoutException();
            }
        };

        ResponseData<Boolean> response1 = weIdService.isWeIdExist(createWeIdResult.getWeId());
        logger.info("isWeIdExist result:");
        BeanUtil.print(response1);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the isIdentityExist
     *       method.
     *
     */
    @Test
    public void testIsWeIdExistCase7() {

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public Future<Bool> isIdentityExist(Address identity) throws InterruptedException {
                throw new InterruptedException();
            }
        };

        ResponseData<Boolean> response1 = weIdService.isWeIdExist(createWeIdResult.getWeId());
        logger.info("isWeIdExist result:");
        BeanUtil.print(response1);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    /**
     * case: Simulation throws an NullPointerException when calling the isIdentityExist
     *       method.
     *
     */
    @Test
    public void testIsWeIdExistCase8() {

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public Future<Bool> isIdentityExist(Address identity) throws NullPointerException {
                throw new NullPointerException();
            }
        };

        ResponseData<Boolean> response1 = weIdService.isWeIdExist(createWeIdResult.getWeId());
        logger.info("isWeIdExist result:");
        BeanUtil.print(response1);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }
}
