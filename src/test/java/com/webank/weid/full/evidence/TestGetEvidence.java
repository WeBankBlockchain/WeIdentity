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

package com.webank.weid.full.evidence;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import mockit.Mock;
import mockit.MockUp;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.Evidence;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.EvidenceInfo;
import com.webank.weid.protocol.response.ResponseData;

/**
 * TestGetEvidence v_wbpenghu.
 */
public class TestGetEvidence extends TestBaseServcie {

    private static final Logger logger = LoggerFactory.getLogger(TestCreateEvidence.class);
    private static Credential evidenceCredential = null;
    private static String evidenceAddress;

    @Override
    public void testInit() {
        super.testInit();
        if (evidenceCredential == null) {
            Credential credential = super.createCredential(createCredentialArgs).getCredential();
            ResponseData<String> evidence = evidenceService
                .createEvidence(credential, createWeIdResultWithSetAttr.getUserWeIdPrivateKey());
            Assert.assertTrue(!evidence.getResult().isEmpty());
            evidenceCredential = credential;
            evidenceAddress = evidence.getResult();
        }
    }

    /**
     * case1: success.
     */
    @Test
    public void testGetEvidenceCase1() {
        ResponseData<EvidenceInfo> responseData = evidenceService
            .getEvidence(evidenceAddress);
        logger.info("testGetEvidenceCase1 result :" + responseData);
        Assert.assertEquals(0, responseData.getErrorCode().intValue());
        Assert.assertEquals("success", responseData.getErrorMessage());
        EvidenceInfo evidenceInfo = responseData.getResult();
        Assert.assertTrue(
            evidenceCredential.getIssuer().contains(evidenceInfo.getSigners().get(0)));
        for (int i = 1; i < evidenceInfo.getSignatures().size(); i++) {
            Assert.assertEquals("0x0000000000000000000000000000000000000000",
                evidenceInfo.getSigners().get(i));
        }
    }

    /**
     * case2: address is null.
     */
    @Test
    public void testGetEvidenceCase2() {
        String evidenceAddress = null;
        ResponseData<EvidenceInfo> responseData = evidenceService.getEvidence(evidenceAddress);
        logger.info("testGetEvidenceCase2 result :" + responseData);
        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(),
            responseData.getErrorCode().intValue());
        Assert.assertEquals("input parameter is illegal.", responseData.getErrorMessage());
        Assert.assertNull(responseData.getResult());
    }

    /**
     * case3: credentialId is "".
     */
    @Test
    public void testGetEvidenceCase3() {
        String evidenceAddress = "";
        ResponseData<EvidenceInfo> responseData = evidenceService.getEvidence(evidenceAddress);
        logger.info("testGetEvidenceCase3 result :" + responseData);
        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(),
            responseData.getErrorCode().intValue());
        Assert.assertNull(responseData.getResult());
    }

    /**
     * case5: mock exception.
     */
    @Test
    public void testGetEvidenceCase5() {
        MockUp<Future<?>> mockFuture = mockTimeoutFuture();
        MockUp<Evidence> mockTest = getEvidence(mockFuture);
        ResponseData<EvidenceInfo> responseData = evidenceService
            .getEvidence(evidenceAddress);
        logger.info("testGetEvidenceCase5 result :" + responseData);
        mockFuture.tearDown();
        mockTest.tearDown();
        Assert.assertEquals(responseData.getErrorCode().intValue(),
            ErrorCode.TRANSACTION_TIMEOUT.getCode());
    }

    /**
     * case6: mock exception.
     */
    @Test
    public void testGetEvidenceCase6() {
        MockUp<Future<?>> mockFuture = mockInterruptedFuture();
        MockUp<Evidence> mockTest = getEvidence(mockFuture);

        ResponseData<EvidenceInfo> responseData = evidenceService
            .getEvidence(evidenceAddress);
        logger.info("testGetEvidenceCase6 result :" + responseData);
        mockFuture.tearDown();
        mockTest.tearDown();
        Assert.assertEquals(responseData.getErrorCode().intValue(),
            ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode());
    }

    /**
     * case7: mock exception.
     */
    @Test
    public void testGetEvidenceCase7() {
        MockUp<Future<?>> mockFuture = new MockUp<Future<?>>() {
            @Mock
            public Future<?> get() {
                return null;
            }

            @Mock
            public Future<?> get(long timeout, TimeUnit unit) {
                return null;
            }
        };
        MockUp<Evidence> mockTest = getEvidence(mockFuture);
        ResponseData<EvidenceInfo> responseData = evidenceService
            .getEvidence(evidenceAddress);
        logger.info("testGetEvidenceCase7 result :" + responseData);
        mockFuture.tearDown();
        mockTest.tearDown();
        Assert.assertNull(responseData.getResult());
        Assert.assertEquals(responseData.getErrorCode().intValue(),
            ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR.getCode());
    }

    /**
     * case8: mock exception.
     */
    @Test
    public void testGetEvidenceCase8() {

        MockUp<Evidence> mockTest
            = new MockUp<Evidence>() {
                @Mock
                public Future<?> getInfo() {
                    return null;
                }
            };
        ResponseData<EvidenceInfo> responseData = evidenceService
            .getEvidence(evidenceAddress);
        logger.info("testGetEvidenceCase8 result :" + responseData);
        mockTest.tearDown();
        Assert.assertNull(responseData.getResult());
        Assert.assertEquals(responseData.getErrorCode().intValue(),
            ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR.getCode());
    }


    private MockUp<Evidence> getEvidence(
        MockUp<Future<?>> mockFuture) {
        MockUp<Evidence> mockTest
            = new MockUp<Evidence>() {
                @Mock
                public Future<?> getInfo() {
                    return mockFuture.getMockInstance();
                }
            };
        return mockTest;
    }

}
