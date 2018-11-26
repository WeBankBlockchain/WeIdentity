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
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.response.ResponseData;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.junit.Assert;
import org.junit.Test;

/**
 * getWeIdDocument method for testing WeIdService.
 * 
 * @author v_wbgyang
 *
 */
public class TestGetWeIdDocument extends TestBaseServcie {

    /**
     * case: set and get weIdDom.
     *
     * @throws Exception may be throw Exception
     */
    @Test
    public void testGetWeIdDocumentCase1() throws Exception {

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdForGetDoc.getWeId());
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNotNull(weIdDoc.getResult());
    }

    /**
     * case: set many times and get the weIdDom.
     *
     * @throws Exception   may be throw Exception
     */
    @Test
    public void testGetWeIdDocumentCase2() throws Exception {

        super.setPublicKey(createWeIdForGetDoc,
            TestBaseUtil.createEcKeyPair()[0],
            createWeIdNew.getWeId());
        super.setAuthentication(createWeIdForGetDoc,
            TestBaseUtil.createEcKeyPair()[0],
            createWeIdForGetDoc.getWeId());
        super.setService(createWeIdForGetDoc,
            "drivingCardServic1",
            "https://weidentity.webank.com/endpoint/8377465");

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdForGetDoc.getWeId());
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNotNull(weIdDoc.getResult());
    }

    /**
     * case: weIdentity DID is invalid.
     * 
     * @throws Exception   may be throw Exception
     */
    @Test
    public void testGetWeIdDocumentCase3() throws Exception {

        ResponseData<WeIdDocument> weIdDoc = weIdService.getWeIdDocument("xxxxxxxxxx");
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNull(weIdDoc.getResult());
    }

    /**
     * case: weIdentity DID is null.
     *
     * @throws Exception   may be throw Exception
     */
    @Test
    public void testGetWeIdDocumentCase4() throws Exception {

        ResponseData<WeIdDocument> weIdDoc = weIdService.getWeIdDocument(null);
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNull(weIdDoc.getResult());
    }

    /**
     * case: weIdentity DID is not exists.
     * 
     * @throws Exception   may be throw Exception
     */
    @Test
    public void testGetWeIdDocumentCase5() throws Exception {

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument("did:weid:0xa1c93e93622c6a0b2f52c90741e0b98ab77385a9");
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        Assert.assertEquals(ErrorCode.WEID_DOES_NOT_EXIST.getCode(),
            weIdDoc.getErrorCode().intValue());
        Assert.assertNull(weIdDoc.getResult());
    }

    /**
     * case: Simulation throws an InterruptedException when calling the getLatestRelatedBlock 
     *       method.
     * 
     * @throws Exception   may be throw Exception
     */
    @Test
    public void testGetWeIdDocumentCase6() throws Exception {

        final MockUp<Future<Uint256>> mockFuture = new MockUp<Future<Uint256>>() {
            @Mock
            public Future<Uint256> get(long timeout, TimeUnit unit) throws Exception {
                throw new InterruptedException();
            }
        };

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public Future<Uint256> getLatestRelatedBlock(Address identity) throws Exception {
                return mockFuture.getMockInstance();
            }
        };

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdForGetDoc.getWeId());
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            weIdDoc.getErrorCode().intValue());
        Assert.assertNull(weIdDoc.getResult());
    }

    /**
     * case: Simulation throws an TimeoutException when calling the getLatestRelatedBlock method.
     * 
     * @throws Exception   may be throw Exception
     */
    @Test
    public void testGetWeIdDocumentCase7() throws Exception {

        final MockUp<Future<Uint256>> mockFuture = new MockUp<Future<Uint256>>() {
            @Mock
            public Future<Uint256> get(long timeout, TimeUnit unit) throws Exception {
                throw new TimeoutException();
            }
        };

        MockUp<WeIdContract> mockTest = new MockUp<WeIdContract>() {
            @Mock
            public Future<Uint256> getLatestRelatedBlock(Address identity) throws Exception {
                return mockFuture.getMockInstance();
            }
        };

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdForGetDoc.getWeId());
        System.out.println("\ngetWeIdDocument result:");
        BeanUtil.print(weIdDoc);

        mockTest.tearDown();
        mockFuture.tearDown();

        Assert.assertEquals(ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            weIdDoc.getErrorCode().intValue());
        Assert.assertNull(weIdDoc.getResult());
    }
}
