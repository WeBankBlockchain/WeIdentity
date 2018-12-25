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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.DynamicArray;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Uint8;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.EvidenceFactory;
import com.webank.weid.contract.EvidenceFactory.CreateEvidenceLogEventResponse;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;

/**
 * Test CreateEvidence.
 *
 * @author v_wbgyang
 */
public class TestCreateEvidence extends TestBaseServcie {

    private static final Logger logger = LoggerFactory.getLogger(TestCreateEvidence.class);

    private static Credential credential;

    @Override
    public void testInit() {
        super.testInit();
        if (credential == null) {
            credential = super.createCredential(createCredentialArgs).getCredential();
        }
    }

    /**
     * case1: create evidence success.
     */
    @Test
    public void testCreateEvidenceCase01() {
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr = createWeIdResultWithSetAttr;
        ResponseData<String> response = evidenceService
            .createEvidence(credential, tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        logger.info("testCreateEvidenceCase1 createEvidence result:");
        BeanUtil.print(response);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(!response.getResult().isEmpty());
    }

    /**
     * case2: credential is null.
     */
    @Test
    public void testCreateEvidenceCase02() {
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr = createWeIdResultWithSetAttr;
        ResponseData<String> response = evidenceService
            .createEvidence(null, tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        logger.info("testCreateEvidenceCase2 createEvidence result:");
        BeanUtil.print(response);
        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(), response.getErrorCode().intValue());
        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case3: weIdPrivateKey is null.
     */
    @Test
    public void testCreateEvidenceCase03() {
        ResponseData<String> response = evidenceService
            .createEvidence(credential, null);
        logger.info("testCreateEvidenceCase3 createEvidence result:");
        BeanUtil.print(response);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case5: privateKey is null.
     */
    @Test
    public void testCreateEvidenceCase04() {
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr = createWeIdResultWithSetAttr;
        WeIdPrivateKey weIdPrivateKey = createWeIdResultWithSetAttr.getUserWeIdPrivateKey();
        weIdPrivateKey.setPrivateKey(null);
        tempCreateWeIdResultWithSetAttr.setUserWeIdPrivateKey(weIdPrivateKey);

        ResponseData<String> response = evidenceService
            .createEvidence(credential, tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        logger.info("testCreateEvidenceCase5 createEvidence result:");
        BeanUtil.print(response);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case6: privateKey is xxxxx.
     */
    @Test
    public void testCreateEvidenceCase05() {
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr = createWeIdResultWithSetAttr;
        WeIdPrivateKey weIdPrivateKey = createWeIdResultWithSetAttr.getUserWeIdPrivateKey();
        weIdPrivateKey.setPrivateKey("xxxxx");
        tempCreateWeIdResultWithSetAttr.setUserWeIdPrivateKey(weIdPrivateKey);

        ResponseData<String> response = evidenceService
            .createEvidence(credential, tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        logger.info("testCreateEvidenceCase6 createEvidence result:");
        BeanUtil.print(response);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case8: createEvidence immutability - multiple invocations lead to different addresses.
     */
    @Test
    public void testCreateEvidenceCase06() {
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr = createWeIdNew;
        ResponseData<String> response = evidenceService
            .createEvidence(credential, tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        logger.info("testCreateEvidenceCase8 createEvidence result:");
        BeanUtil.print(response);
        Assert.assertEquals(
            ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertTrue(!response.getResult().isEmpty());

        ResponseData<String> response1 = evidenceService
            .createEvidence(credential, tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        logger.info("testCreateEvidenceCase8 createEvidence again result:");
        BeanUtil.print(response1);
        Assert.assertEquals(
            ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(response.getResult().equalsIgnoreCase(response1.getResult()));
    }

    /**
     * case9: privateKey is belong to others weId.
     */
    @Test
    public void testCreateEvidenceCase07() {
        ResponseData<String> response = evidenceService
            .createEvidence(credential, createWeIdNew.getUserWeIdPrivateKey());
        logger.info("testCreateEvidenceCase9 createEvidence result:");
        BeanUtil.print(response);
        Assert.assertEquals(
            ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertTrue(!response.getResult().isEmpty());
    }

    /**
     * case10: credential id is null.
     */
    @Test
    public void testCreateEvidenceCase08() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setId(null);

        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        logger.info("testCreateEvidenceCase10 createEvidence result:");
        BeanUtil.print(response);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ID_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case12: the cptId is null of credential.
     */
    @Test
    public void testCreateEvidenceCase09() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setCptId(null);

        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        logger.info("testCreateEvidenceCase12 createEvidence result:");
        BeanUtil.print(response);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case13: mock TimeOutException.
     */
    @Test
    public void testCreateEvidenceCase10() {
        MockUp<Future<?>> mockFuture = mockTimeoutFuture();
        ResponseData<String> response = this.createEvidence(mockFuture);
        logger.info("testCreateEvidenceCase13 createEvidence result:");
        BeanUtil.print(response);
        Assert.assertEquals(
            ErrorCode.TRANSACTION_TIMEOUT.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case14: mock InterruptedException.
     */
    @Test
    public void testCreateEvidenceCase11() {
        MockUp<Future<?>> mockFuture = mockInterruptedFuture();
        ResponseData<String> response = this.createEvidence(mockFuture);
        logger.info("testCreateEvidenceCase14 createEvidence result:");
        BeanUtil.print(response);
        Assert.assertEquals(
            ErrorCode.TRANSACTION_EXECUTE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case15: mock InterruptedException.
     */
    @Test
    public void testCreateEvidenceCase12() {
        MockUp<EvidenceFactory> mockTest = new MockUp<EvidenceFactory>() {
            @Mock
            public List<CreateEvidenceLogEventResponse> getCreateEvidenceLogEvents(
                TransactionReceipt transactionReceipt) {

                List<CreateEvidenceLogEventResponse> eventResponseList =
                    new ArrayList<CreateEvidenceLogEventResponse>();
                eventResponseList.add(null);
                return eventResponseList;
            }
        };

        ResponseData<String> response = evidenceService
            .createEvidence(credential, createWeIdNew.getUserWeIdPrivateKey());
        mockTest.tearDown();
        logger.info("testCreateEvidenceCase15 createEvidence result:");
        BeanUtil.print(response);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(!response.getResult().isEmpty());
    }

    private ResponseData<String> createEvidence(MockUp<Future<?>> mockFuture) {
        MockUp<EvidenceFactory> mockTest = new MockUp<EvidenceFactory>() {
            @Mock
            public Future<?> createEvidence(
                DynamicArray<Bytes32> credentialHash,
                DynamicArray<Address> signer,
                Bytes32 r,
                Bytes32 s,
                Uint8 v,
                DynamicArray<Bytes32> extra
            ) {

                return mockFuture.getMockInstance();
            }
        };
        ResponseData<String> response = evidenceService
            .createEvidence(credential, createWeIdNew.getUserWeIdPrivateKey());
        mockFuture.tearDown();
        mockTest.tearDown();
        return response;
    }

}
