/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

import java.util.concurrent.Future;

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
import com.webank.weid.contract.AuthorityIssuerController;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;

/**
 * isAuthorityIssuer method for testing AuthorityIssuerService.
 * @author v_wbgyang
 *
 */
public class TestIsAuthorityIssuer extends TestBaseServcie {
    
    private static final Logger logger = LoggerFactory.getLogger(TestIsAuthorityIssuer.class);

    private static CreateWeIdDataResult createWeId;

    @Override
    public void testInit() {

        super.testInit();
        if (null == createWeId) {
            createWeId = super.registerAuthorityIssuer();
        }

    }

    /**
     * case: is authority issuer.
     *
     */
    @Test
    public void testIsAuthorityIssuerCase1() {

        ResponseData<Boolean> response =
            authorityIssuerService.isAuthorityIssuer(createWeId.getWeId());
        logger.info("isAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * case: WeIdentity DID is bad format.
     *
     */
    @Test
    public void testIsAuthorityIssuerCase2() {

        ResponseData<Boolean> response = authorityIssuerService.isAuthorityIssuer("xxxxxxxxxxx");
        logger.info("isAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: WeIdentity DID is blank.
     *
     */
    @Test
    public void testIsAuthorityIssuerCase3() {

        ResponseData<Boolean> response = authorityIssuerService.isAuthorityIssuer(null);
        logger.info("isAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the WeIdentity DID is registed by other.
     *
     */
    @Test
    public void testIsAuthorityIssuerCase4() {

        ResponseData<Boolean> response = authorityIssuerService
            .isAuthorityIssuer("did:weid:0x5f3d8234e93823fac7ebdf0cfaa03b6a43d8773b");
        logger.info("isAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the WeIdentity DID is not exists.
     *
     */
    @Test
    public void testIsAuthorityIssuerCase5() {

        ResponseData<Boolean> response = authorityIssuerService
            .isAuthorityIssuer("did:weid:0x5f3d8234e93823fac7ebdf0cfaa03b6a43d87733");
        logger.info("isAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: the WeIdentity DID is removed.
     *
     */
    @Test
    public void testIsAuthorityIssuerCase6() {

        CreateWeIdDataResult createWeId = super.registerAuthorityIssuer();

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs =
            TestBaseUtil.buildRemoveAuthorityIssuerArgs(createWeId, privateKey);

        ResponseData<Boolean> response =
            authorityIssuerService.removeAuthorityIssuer(removeAuthorityIssuerArgs);
        logger.info("removeAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        response = authorityIssuerService.isAuthorityIssuer(createWeId.getWeId());
        logger.info("isAuthorityIssuer result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the
     *       isAuthorityIssuer method.
     *
     */
    @Test
    public void testIsAuthorityIssuerCase7() {

        MockUp<Future<?>> mockFuture = mockInterruptedFuture();

        ResponseData<Boolean> response = isAuthorityIssuerForMock(mockFuture);

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    /**
     * case: Simulation throws an TimeoutException when calling the
     *       isAuthorityIssuer method.
     *
     */
    @Test
    public void testIsAuthorityIssuerCase8() {

        MockUp<Future<?>> mockFuture = mockTimeoutFuture();

        ResponseData<Boolean> response = isAuthorityIssuerForMock(mockFuture);

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }

    private ResponseData<Boolean> isAuthorityIssuerForMock(MockUp<Future<?>> mockFuture) {
        
        MockUp<AuthorityIssuerController> mockTest = new MockUp<AuthorityIssuerController>() {
            @Mock
            public Future<?> isAuthorityIssuer(Address addr) {
                return mockFuture.getMockInstance();
            }
        };

        ResponseData<Boolean> response =
            authorityIssuerService.isAuthorityIssuer(createWeId.getWeId());
        logger.info("isAuthorityIssuer result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();
        return response;
    }

    /**
     * case: Simulation returns null when invoking the isAuthorityIssuer method.
     *
     */
    @Test
    public void testIsAuthorityIssuerCase9() {

        MockUp<AuthorityIssuerController> mockTest = new MockUp<AuthorityIssuerController>() {
            @Mock
            public Future<Bool> isAuthorityIssuer(Address addr) {
                return null;
            }
        };

        ResponseData<Boolean> response =
            authorityIssuerService.isAuthorityIssuer(createWeId.getWeId());
        logger.info("isAuthorityIssuer result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.AUTHORITY_ISSUER_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(false, response.getResult());
    }
}
