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

import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.WeIdContract;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.protocol.response.ResponseData;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.Bool;
import org.junit.Assert;
import org.junit.Test;

/**
 * isWeIdExist method for testing WeIdService.
 * 
 * @author v_wbgyang
 *
 */
public class TestIsWeIdExist extends TestBaseServcie {

    /**
     * case: weIdentity DID is Exist.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testIsWeIdExistCase1() {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist(createWeId.getWeId());
        System.out.println("\nisWeIdExist result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(true, response1.getResult());
    }

    /**
     * case: weIdentity DID is empty.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testIsWeIdExistCase2() {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist(null);
        System.out.println("\nisWeIdExist result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    /**
     * case: weIdentity DID is invalid.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testIsWeIdExistCase3() {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist("xxxxxx");
        System.out.println("\nisWeIdExist result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    /**
     * case: weIdentity DID is not exist.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testIsWeIdExistCase4() {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist("did:weid:xxxxxx");
        System.out.println("\nisWeIdExist result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }

    /**
     * case: weIdentity DID is Exist.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testIsWeIdExistCase5() throws Exception {

        ResponseData<Boolean> response1 = weIdService.isWeIdExist(createWeId.getWeId());
        System.out.println("\nisWeIdExist result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(true, response1.getResult());
    }

    /**
     * case: Simulation throws an TimeoutException when calling the isIdentityExist
     *       method.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testIsWeIdExistCase6() {

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public Future<Bool> isIdentityExist(Address identity) throws Exception {
                throw new TimeoutException();
            }
        };

        ResponseData<Boolean> response1 = weIdService.isWeIdExist(createWeId.getWeId());
        System.out.println("\nisWeIdExist result:");
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
     * @throws Exception may be throw Exception
     */
    @Test
    public void testIsWeIdExistCase7() {

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public Future<Bool> isIdentityExist(Address identity) throws Exception {
                throw new InterruptedException();
            }
        };

        ResponseData<Boolean> response1 = weIdService.isWeIdExist(createWeId.getWeId());
        System.out.println("\nisWeIdExist result:");
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
     * @throws Exception may be throw Exception
     */
    @Test
    public void testIsWeIdExistCase8() {

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public Future<Bool> isIdentityExist(Address identity) throws Exception {
                throw new NullPointerException();
            }
        };

        ResponseData<Boolean> response1 = weIdService.isWeIdExist(createWeId.getWeId());
        System.out.println("\nisWeIdExist result:");
        BeanUtil.print(response1);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(false, response1.getResult());
    }
}
