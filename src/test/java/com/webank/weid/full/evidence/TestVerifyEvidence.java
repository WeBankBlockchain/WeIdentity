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

import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.crypto.Sign.SignatureData;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.EvidenceServiceImpl;

/**
 * TestVerifyEvidence v_wbpenghu.
 */
public class TestVerifyEvidence extends TestBaseServcie {

    private static final Logger logger = LoggerFactory.getLogger(TestCreateEvidence.class);
    private static Credential evidenceCredential = null;
    private static String evidenceAddress;

    @Override
    public void testInit() {
        if (!isInitIssuer) {
            super.testInit();
        }
        if (evidenceCredential == null) {
            evidenceCredential = super.createCredential(createCredentialArgs).getCredential();
            ResponseData<String> evidence = evidenceService.createEvidence(evidenceCredential,
                createWeIdResultWithSetAttr.getUserWeIdPrivateKey());
            Assert.assertTrue(!evidence.getResult().isEmpty());
            evidenceAddress = evidence.getResult();
        }
    }

    /**
     * case1: succeed.
     */
    @Test
    public void testVerifyEvidenceCase1() {
        ResponseData<Boolean> responseData = evidenceService
            .verify(evidenceCredential, evidenceAddress);
        logger.info("testVerifyEvidenceCase1 result :" + responseData);
        Assert.assertTrue(responseData.getResult());
        Assert.assertEquals(responseData.getErrorCode().intValue(), ErrorCode.SUCCESS.getCode());
    }

    /**
     * case2: id is "".
     */
    @Test
    public void testVerifyEvidenceCase2() {
        Credential credential = copyCredential(evidenceCredential);
        credential.setId("");
        ResponseData<Boolean> responseData = evidenceService
            .verify(credential, evidenceAddress);
        logger.info("testVerifyEvidenceCase2 result :" + responseData);
        Assert.assertFalse(responseData.getResult());
        Assert.assertEquals(responseData.getErrorCode().intValue(),
            ErrorCode.CREDENTIAL_ID_NOT_EXISTS.getCode());
    }

    /**
     * case3: id is null.
     */
    @Test
    public void testVerifyEvidenceCase3() {
        Credential credential = copyCredential(evidenceCredential);
        credential.setId(null);
        ResponseData<Boolean> responseData = evidenceService
            .verify(credential, evidenceAddress);
        logger.info("testVerifyEvidenceCase3 result :" + responseData);
        Assert.assertFalse(responseData.getResult());
        Assert.assertEquals(responseData.getErrorCode().intValue(),
            ErrorCode.CREDENTIAL_ID_NOT_EXISTS.getCode());
    }


    /**
     * case5: Issuer is "".
     */
    @Test
    public void testVerifyEvidenceCase5() {
        Credential credential = copyCredential(evidenceCredential);
        credential.setId(credential.getId());
        credential.setIssuer("");
        ResponseData<Boolean> responseData = evidenceService
            .verify(credential, evidenceAddress);
        logger.info("testVerifyEvidenceCase5 result :" + responseData);
        Assert.assertFalse(responseData.getResult());
        Assert.assertEquals(responseData.getErrorCode().intValue(),
            ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode());
    }

    /**
     * case6: cptId is not exit.
     */
    @Test
    public void testVerifyEvidenceCase6() {
        Credential credential = copyCredential(evidenceCredential);
        credential.setId(credential.getId());
        credential.setCptId(-1);
        ResponseData<Boolean> responseData = evidenceService
            .verify(credential, evidenceAddress);
        logger.info("testVerifyEvidenceCase6 result :" + responseData);
        Assert.assertFalse(responseData.getResult());
        Assert.assertEquals(responseData.getErrorCode().intValue(),
            ErrorCode.CREDENTIAL_CPT_NOT_EXISTS.getCode());
    }

    /**
     * case7: ExpirationDate is not match.
     */
    @Test
    public void testVerifyEvidenceCase7() {
        Credential credential = copyCredential(evidenceCredential);
        credential.setExpirationDate(System.currentTimeMillis());
        ResponseData<Boolean> responseData = evidenceService
            .verify(credential, evidenceAddress);
        logger.info("testVerifyEvidenceCase7 result :" + responseData);
        Assert.assertFalse(responseData.getResult());
        Assert.assertEquals(responseData.getErrorCode().intValue(),
            ErrorCode.CREDENTIAL_EVIDENCE_HASH_MISMATCH.getCode());
    }

    /**
     * case8: IssuranceDate is not match.
     */
    @Test
    public void testVerifyEvidenceCase8() {
        Credential credential = copyCredential(evidenceCredential);
        credential.setIssuranceDate(System.currentTimeMillis());
        ResponseData<Boolean> responseData = evidenceService
            .verify(credential, evidenceAddress);
        logger.info("testVerifyEvidenceCase8 result :" + responseData);
        Assert.assertFalse(responseData.getResult());
        Assert.assertEquals(responseData.getErrorCode().intValue(),
            ErrorCode.CREDENTIAL_EVIDENCE_HASH_MISMATCH.getCode());
    }

    /**
     * case10: args is null.
     */
    @Test
    public void testVerifyEvidenceCase10() {
        ResponseData<Boolean> responseData = evidenceService
            .verify(evidenceCredential, null);
        logger.info("testVerifyEvidenceCase12 result :" + responseData);
        Assert.assertFalse(responseData.getResult());
        Assert.assertEquals(responseData.getErrorCode().intValue(),
            ErrorCode.ILLEGAL_INPUT.getCode());
    }

    /**
     * case11: Signature is not match.
     */
    @Test
    public void testVerifyEvidenceCase11() {
        Credential credential = copyCredential(evidenceCredential);
        credential.setSignature(credential.getSignature() + "x");
        ResponseData<Boolean> responseData = evidenceService
            .verify(credential, evidenceAddress);
        logger.info("testVerifyEvidenceCase11 result :" + responseData);
        Assert.assertFalse(responseData.getResult());
        Assert.assertEquals(responseData.getErrorCode().intValue(),
            ErrorCode.CREDENTIAL_EVIDENCE_HASH_MISMATCH.getCode());
    }

    /**
     * case12: args is null.
     */
    @Test
    public void testVerifyEvidenceCase12() {
        ResponseData<Boolean> responseData = evidenceService.verify(null, evidenceAddress);
        logger.info("testVerifyEvidenceCase12 result :" + responseData);
        Assert.assertFalse(responseData.getResult());
        Assert.assertEquals(responseData.getErrorCode().intValue(),
            ErrorCode.ILLEGAL_INPUT.getCode());
    }

    /**
     * case13: privateKey is not match.
     */
    @Test
    public void testVerifyEvidenceCase13() {
        CreateWeIdDataResult weIdWithSetAttr = createWeIdResultWithSetAttr;
        weIdWithSetAttr.getUserWeIdPrivateKey().setPrivateKey("11111111");
        ResponseData<String> responseData = evidenceService
            .createEvidence(evidenceCredential, weIdWithSetAttr.getUserWeIdPrivateKey());
        Assert.assertTrue(!responseData.getResult().isEmpty());
        Assert.assertEquals(responseData.getErrorCode().intValue(), ErrorCode.SUCCESS.getCode());
        ResponseData<Boolean> responseData1 = evidenceService
            .verify(evidenceCredential, responseData.getResult());
        logger.info("testVerifyEvidenceCase13 result :" + responseData1);
        Assert.assertEquals(responseData1.getErrorCode().intValue(),
            ErrorCode.CREDENTIAL_WEID_DOCUMENT_ILLEGAL.getCode());
        Assert.assertFalse(responseData1.getResult());
    }

    /**
     * case15: privateKey is correct ,  private is on chain(weid exist) ,but this weid not set
     * Permission.
     */
    @Test
    public void testVerifyEvidenceCase15() {
        CreateWeIdDataResult weIdWithSetAttr = weIdService.createWeId().getResult();
        ResponseData<String> responseData1 = evidenceService
            .createEvidence(evidenceCredential, weIdWithSetAttr.getUserWeIdPrivateKey());
        logger.info("testVerifyEvidenceCase15 result :" + responseData1);
        Assert.assertEquals(responseData1.getErrorCode().intValue(), ErrorCode.SUCCESS.getCode());
        ResponseData<Boolean> responseData2 = evidenceService
            .verify(evidenceCredential, responseData1.getResult());
        Assert.assertEquals(responseData2.getErrorCode().intValue(),
            ErrorCode.CREDENTIAL_ISSUER_MISMATCH.getCode());
        Assert.assertFalse(responseData2.getResult());
    }

    /**
     * case16: mock exception.
     */
    @Test
    public void testVerifyEvidenceCase16() {
        MockUp<EvidenceServiceImpl> mockException = new MockUp<EvidenceServiceImpl>() {
            @Mock
            public ResponseData<Boolean> verifySignatureToSigner(String rawData,
                String signerWeId, SignatureData signatureData) throws Exception {
                return null;
            }
        };

        Credential credential = copyCredential(evidenceCredential);
        ResponseData<Boolean> responseData = evidenceService
            .verify(credential, evidenceAddress);
        logger.info("testVerifyEvidenceCase16 result :" + responseData);
        Assert.assertEquals(ErrorCode.CREDENTIAL_EVIDENCE_BASE_ERROR.getCode(),
            responseData.getErrorCode().intValue());
        mockException.tearDown();
    }
}
