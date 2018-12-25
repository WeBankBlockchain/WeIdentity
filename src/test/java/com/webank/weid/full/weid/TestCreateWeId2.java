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

package com.webank.weid.full.weid;

import java.util.List;
import java.util.concurrent.Future;

import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.bcos.web3j.tx.Contract;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.common.PasswordKey;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.WeIdContract;
import com.webank.weid.contract.WeIdContract.WeIdAttributeChangedEventResponse;
import com.webank.weid.exception.PrivateKeyIllegalException;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.BaseService;

/**
 * a parametric createWeId method for testing WeIdService.
 * 
 * @author v_wbgyang
 *
 */
public class TestCreateWeId2 extends TestBaseServcie {
    
    private static final Logger logger = LoggerFactory.getLogger(TestCreateWeId2.class);

    /**
     * case: create success.
     *
     */
    @Test
    public void testCreateWeIdCase1() {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        logger.info("createWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());
    }

    /**
     * case: createWeIdArgs is null.
     *
     */
    @Test
    public void testCreateWeIdCase2() {

        ResponseData<String> response = weIdService.createWeId(null);
        logger.info("createWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: publicKey is null.
     *
     */
    @Test
    public void testCreateWeIdCase3() {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs.setPublicKey(null);

        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        logger.info("createWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PUBLICKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: publicKey is Non integer string.
     *
     */
    @Test
    public void testCreateWeIdCase4() {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs.setPublicKey("abc");

        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        logger.info("createWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PUBLICKEY_AND_PRIVATEKEY_NOT_MATCHED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: weIdPrivateKey is null.
     *
     */
    @Test
    public void testCreateWeIdCase5() {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs.setWeIdPrivateKey(null);

        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        logger.info("createWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: privateKey is null.
     *
     */
    @Test
    public void testCreateWeIdCase6() {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs.getWeIdPrivateKey().setPrivateKey(null);

        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        logger.info("createWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: privateKey is invalid.
     *
     */
    @Test
    public void testCreateWeIdCase7() {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs.getWeIdPrivateKey().setPrivateKey("xxxxxxxxxxxx");

        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        logger.info("createWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: privateKey and publicKey misMatch.
     *
     */
    @Test
    public void testCreateWeIdCase8() {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        PasswordKey passwordKey = TestBaseUtil.createEcKeyPair();
        createWeIdArgs.getWeIdPrivateKey().setPrivateKey(passwordKey.getPrivateKey());

        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        logger.info("createWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.WEID_PUBLICKEY_AND_PRIVATEKEY_NOT_MATCHED.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: Simulation returns null when invoking the getWeIdAttributeChangedEvents
     *       method.
     *
     */
    @Test
    public void testCreateWeIdCase9() {

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public List<WeIdAttributeChangedEventResponse> getWeIdAttributeChangedEvents(
                TransactionReceipt transactionReceipt) {
                return null;
            }
        };

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        logger.info("createWeId result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the
     *       getWeIdAttributeChangedEvents method.
     *
     */
    @Test
    public void testCreateWeIdCase10() {

        MockUp<Future<?>> mockFuture = mockInterruptedFuture();
        
        ResponseData<String> response = createWeIdForMock(mockFuture);

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    private ResponseData<String> createWeIdForMock(MockUp<Future<?>> mockFuture) {
        
        MockUp<WeIdContract> mockTest = mockSetAttribute(mockFuture);

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        logger.info("createWeId result:");
        BeanUtil.print(response);

        mockTest.tearDown();
        mockFuture.tearDown();
        return response;
    }

    /**
     * case: Simulation throws an TimeoutException when calling the
     *       getWeIdAttributeChangedEvents method.
     *
     */
    @Test
    public void testCreateWeIdCase11() {

        MockUp<Future<?>> mockFuture = mockTimeoutFuture();

        ResponseData<String> response = createWeIdForMock(mockFuture);

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: Simulation throws an NullPointerException when calling the
     *       getWeIdAttributeChangedEvents method.
     *
     */
    @Test
    public void testCreateWeIdCase12() {

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public List<WeIdAttributeChangedEventResponse> getWeIdAttributeChangedEvents(
                TransactionReceipt transactionReceipt)
                throws NullPointerException {
                
                throw new NullPointerException();
            }
        };

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        logger.info("createWeId result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.UNKNOW_ERROR.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: Simulation throws an PrivateKeyIllegalException when calling the
     *       reloadContract method.
     *  
     */
    @Test
    public void testCreateWeIdCase13() {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();

        MockUp<BaseService> mockTest = new MockUp<BaseService>() {
            @Mock
            public Contract reloadContract(String contractAddress, String privateKey, Class<?> cls)
                throws PrivateKeyIllegalException {
                
                throw new PrivateKeyIllegalException();
            }
        };

        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        logger.info("createWeId result:");
        BeanUtil.print(response);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.WEID_PRIVATEKEY_INVALID.getCode(),
            response.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, response.getResult());
    }

    /**
     * case: create again.
     *
     */
    @Test
    public void testCreateWeIdCase14() {

        CreateWeIdArgs createWeIdArgs = TestBaseUtil.buildCreateWeIdArgs();
        ResponseData<String> response = weIdService.createWeId(createWeIdArgs);
        logger.info("createWeId result:");
        BeanUtil.print(response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        ResponseData<String> response1 = weIdService.createWeId(createWeIdArgs);
        logger.info("createWeId result:");
        BeanUtil.print(response1);

        Assert.assertEquals(ErrorCode.WEID_ALREADY_EXIST.getCode(),
            response1.getErrorCode().intValue());
        Assert.assertNotNull(response1.getResult());
    }
}
