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

package com.webank.weid.full.auth;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.AuthorityIssuerController;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.Bool;
import org.junit.Assert;
import org.junit.Test;

/**
 * isAuthorityIssuer method for testing AuthorityIssuerService.
 * @author v_wbgyang
 *
 */
public class TestIsAuthorityIssuer extends TestBaseServcie {

    private static CreateWeIdDataResult createWeId;

    @Override
    public void testInit() throws Exception {

        if (createWeId == null) {
            createWeId = super.registerAuthorityIssuer();
            super.testInit();
        }

    }

    /**
     * case: is authority issuer.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testIsAuthorityIssuerCase1() throws Exception {

        ResponseData<Boolean> response =
            authorityIssuerService.isAuthorityIssuer(createWeId.getWeId());
        System.out.println("\nisAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: weIdentity DId is bad format.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testIsAuthorityIssuerCase2() throws Exception {

        ResponseData<Boolean> response = authorityIssuerService.isAuthorityIssuer("xxxxxxxxxxx");
        System.out.println("\nisAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: weIdentity DId is blank.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testIsAuthorityIssuerCase3() throws Exception {

        ResponseData<Boolean> response = authorityIssuerService.isAuthorityIssuer(null);
        System.out.println("\nisAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the weIdentity DId is registed by other.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testIsAuthorityIssuerCase4() throws Exception {

        ResponseData<Boolean> response = authorityIssuerService
            .isAuthorityIssuer("did:weid:0x5f3d8234e93823fac7ebdf0cfaa03b6a43d8773b");
        System.out.println("\nisAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the weIdentity DId is not exists.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testIsAuthorityIssuerCase5() throws Exception {

        ResponseData<Boolean> response = authorityIssuerService
            .isAuthorityIssuer("did:weid:0x5f3d8234e93823fac7ebdf0cfaa03b6a43d87733");
        System.out.println("\nisAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the weIdentity DId is removed.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testIsAuthorityIssuerCase6() throws Exception {

        CreateWeIdDataResult createWeId = super.registerAuthorityIssuer();

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId);

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        System.out.println("\nremoveAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        response = authorityIssuerService.isAuthorityIssuer(createWeId.getWeId());
        System.out.println("\nisAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the
     *       isAuthorityIssuer method.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testIsAuthorityIssuerCase7() throws Exception {

        final MockUp<Future<Bool>> mockFuture = new MockUp<Future<Bool>>() {
            @Mock
            public Future<Bool> get(long timeout, TimeUnit unit) throws Exception {
                throw new InterruptedException();
            }
        };

        MockUp<AuthorityIssuerController> mockTest = new MockUp<AuthorityIssuerController>() {
            @Mock
            public Future<Bool> isAuthorityIssuer(Address addr) throws Exception {
                return mockFuture.getMockInstance();
            }
        };

        ResponseData<Boolean> response =
            authorityIssuerService.isAuthorityIssuer(createWeId.getWeId());
        System.out.println("\nisAuthorityIssuer result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an TimeoutException when calling the
     *       isAuthorityIssuer method.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testIsAuthorityIssuerCase8() throws Exception {

        final MockUp<Future<Bool>> mockFuture = new MockUp<Future<Bool>>() {
            @Mock
            public Future<Bool> get(long timeout, TimeUnit unit) throws Exception {
                throw new TimeoutException();
            }
        };

        MockUp<AuthorityIssuerController> mockTest = new MockUp<AuthorityIssuerController>() {
            @Mock
            public Future<Bool> isAuthorityIssuer(Address addr) throws Exception {
                return mockFuture.getMockInstance();
            }
        };

        ResponseData<Boolean> response =
            authorityIssuerService.isAuthorityIssuer(createWeId.getWeId());
        System.out.println("\nisAuthorityIssuer result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation returns null when invoking the isAuthorityIssuer method.
     *
     * @throws Exception  may be throw Exception
     */
    @Test
    public void testIsAuthorityIssuerCase9() throws Exception {

        MockUp<AuthorityIssuerController> mockTest = new MockUp<AuthorityIssuerController>() {
            @Mock
            public Future<Bool> isAuthorityIssuer(Address addr) throws Exception {
                return null;
            }
        };

        ResponseData<Boolean> response =
            authorityIssuerService.isAuthorityIssuer(createWeId.getWeId());
        System.out.println("\nisAuthorityIssuer result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
