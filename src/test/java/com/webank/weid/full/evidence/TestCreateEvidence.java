/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.full.evidence;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import static com.webank.weid.full.TestBaseUtil.createEcKeyPair;
import static com.webank.weid.util.CredentialUtils.copyCredential;

import com.webank.weid.common.LogUtil;
import com.webank.weid.common.PasswordKey;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.CredentialWrapper;
import com.webank.weid.protocol.base.EvidenceInfo;
import com.webank.weid.protocol.base.HashString;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.CredentialPojoUtils;

/**
 * Test CreateEvidence.
 *
 * @author v_wbgyang
 */
public class TestCreateEvidence extends TestBaseServcie {

    private static final Logger logger = LoggerFactory.getLogger(TestCreateEvidence.class);

    private static volatile Credential credential = null;

    @Override
    public synchronized void testInit() {
        super.testInit();
        if (credential == null) {
            credential = super.createCredential(createCredentialArgs).getCredential();
        }
    }

    /**
     * case1: credential issuer and private key belong the same weId.
     */
    @Test
    public void testCreateEvidence_success() {
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr =
            super.copyCreateWeId(createWeIdResultWithSetAttr);
        ResponseData<String> response = evidenceService
            .createEvidence(credential, tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(!response.getResult().isEmpty());
    }

    /**
     * case3: weIdPrivateKey is null.
     */
    @Test
    public void testCreateEvidence_priKeyNull() {
        ResponseData<String> response = evidenceService
            .createEvidence(credential, null);
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertEquals(
            ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case5: privateKey is null.
     */
    @Test
    public void testCreateEvidence_privateKeyNull() {
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr =
            super.copyCreateWeId(createWeIdResultWithSetAttr);
        tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey().setPrivateKey(null);

        ResponseData<String> response = evidenceService
            .createEvidence(credential, tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);
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
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr =
            super.copyCreateWeId(createWeIdResultWithSetAttr);
        tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey().setPrivateKey("xxxxx");

        ResponseData<String> response = evidenceService
            .createEvidence(credential, tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case6: privateKey is blank.
     */
    @Test
    public void testCreateEvidence_priKeyBlank() {
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr =
            super.copyCreateWeId(createWeIdResultWithSetAttr);
        tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey().setPrivateKey("");

        ResponseData<String> response = evidenceService
            .createEvidence(credential, tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case8: createEvidence immutability - multiple invocations lead to different addresses.
     */
    @Test
    public void testCreateEvidence_theSameRequestHasDifferentAddress() {
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr = super.copyCreateWeId(createWeIdNew);
        ResponseData<String> response = evidenceService
            .createEvidence(credential, tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertEquals(
            ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertTrue(!response.getResult().isEmpty());

        ResponseData<String> response1 = evidenceService
            .createEvidence(credential, tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertEquals(
            ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(response.getResult().equalsIgnoreCase(response1.getResult()));
    }

    /**
     * case9: privateKey is not exist.
     */
    @Test
    public void testCreateEvidence_privateKeyNotExist() {
        PasswordKey passwordKey = createEcKeyPair();
        WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
        weIdPrivateKey.setPrivateKey(passwordKey.getPrivateKey());
        ResponseData<String> response = evidenceService
            .createEvidence(credential, weIdPrivateKey);
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(!response.getResult().isEmpty());
    }

    /**
     * case: credential id is null.
     */
    @Test
    public void testCreateEvidence_idNull() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setId(null);
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ID_NOT_EXISTS.getCode(),
            innerResp.getErrorCode().intValue());
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case: credential id is null.
     */
    @Test
    public void testCreateEvidence_idBlank() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setId("");
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ID_NOT_EXISTS.getCode(),
            innerResp.getErrorCode().intValue());
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case12: the cptId is null of credential.
     */
    @Test
    public void testCreateEvidence_cptIdNull() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setCptId(null);
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.CPT_ID_ILLEGAL.getCode(),
            innerResp.getErrorCode().intValue());
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case12: the cptId is not exist.
     */
    @Test
    public void testCreateEvidence_cptIdNotExist() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setCptId(999999999);
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertEquals(
            ErrorCode.SUCCESS.getCode(),
            response.getErrorCode().intValue());
        Assert.assertFalse(response.getResult().isEmpty());
    }

    /**
     * case12: the cptId is minus.
     */
    // CI hold: @Test
    public void testCreateEvidence_cptIdIsMinus() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setCptId(-1);
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.CPT_ID_ILLEGAL.getCode(),
            innerResp.getErrorCode().intValue());
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case12: the issuer is null.
     */
    @Test
    public void testCreateEvidence_issuerIsNull() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setIssuer(null);
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            innerResp.getErrorCode().intValue());
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case12: the issuer is blank.
     */
    @Test
    public void testCreateEvidence_issuerIsBlank() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setIssuer("");
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            innerResp.getErrorCode().intValue());
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case12: the issuer is invalid.
     */
    @Test
    public void testCreateEvidence_invalidIssuer() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setIssuer("did:weid:101:0x39e5e6f663ef77409144014ceb063713b656");
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUER_INVALID.getCode(),
            innerResp.getErrorCode().intValue());
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case12: the issuer is not exist.
     */
    @Test
    public void testCreateEvidence_issuerNotExist() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setIssuer("did:weid:0x39e5e6f663ef77409144014ceb063713b65600e7");
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.SUCCESS.getCode(),
            innerResp.getErrorCode().intValue());
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(response.getResult().isEmpty());
    }

    /**
     * case12: the claim is null.
     */
    @Test
    public void testCreateEvidence_claimNull() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setClaim(null);
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS.getCode(),
            innerResp.getErrorCode().intValue());
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case12: the claim is blank.
     */
    @Test
    public void testCreateEvidence_claimBlank() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setClaim(new HashMap<>());
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_CLAIM_NOT_EXISTS.getCode(),
            innerResp.getErrorCode().intValue());
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case12: the issuanceDate is null.
     */
    @Test
    public void testCreateEvidence_issuanceDateNull() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setIssuanceDate(null);
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL.getCode(),
            innerResp.getErrorCode().intValue());
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case12: the issuanceDate is minus.
     */
    @Test
    public void testCreateEvidence_issuanceDateIsMinus() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setIssuanceDate(-1L);
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_ISSUANCE_DATE_ILLEGAL.getCode(),
            innerResp.getErrorCode().intValue());
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case12: the expireDate is minus.
     */
    @Test
    public void testCreateEvidence_expireDateIsMinus() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setExpirationDate(-1L);
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            innerResp.getErrorCode().intValue());

        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case12: the expireDate is null.
     */
    @Test
    public void testCreateEvidence_expireDateNull() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setExpirationDate(null);
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_EXPIRE_DATE_ILLEGAL.getCode(),
            innerResp.getErrorCode().intValue());
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case12: the expireDate is passed.
     */
    @Test
    public void testCreateEvidence_expireDatePassed() {
        Credential tempCredential = copyCredential(credential);
        long expireDate = System.currentTimeMillis() - 10000;
        tempCredential.setExpirationDate(expireDate);
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_EXPIRED.getCode(),
            innerResp.getErrorCode().intValue());
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case12: the pfooof is null.
     */
    @Test
    public void testCreateEvidence_proofNull() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setProof(null);
        ResponseData<String> hashResp = credentialService.getCredentialHash(tempCredential);
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertEquals(
            ErrorCode.ILLEGAL_INPUT.getCode(),
            hashResp.getErrorCode().intValue());
        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case12: the proof is blank.
     */
    @Test
    public void testCreateEvidence_proofBlank() {
        Credential tempCredential = copyCredential(credential);
        tempCredential.setProof(new HashMap<>());
        ResponseData<String> innerResp = credentialService.getCredentialHash(tempCredential);
        Assert.assertEquals(
            ErrorCode.CREDENTIAL_SIGNATURE_TYPE_ILLEGAL.getCode(),
            innerResp.getErrorCode().intValue());
        ResponseData<String> response = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey());
        LogUtil.info(logger, "createEvidence", response);

        Assert.assertFalse(!response.getResult().isEmpty());
    }

    /**
     * case13: selectively disclosure: credentialPojo.
     */
    @Test
    public void testEvidenceFull_SelectivelyDisclosurePojo() {
        if (credentialPojo == null) {
            credentialPojo = super.createCredentialPojo(createCredentialPojoArgs);
        }
        CredentialPojo originalCredential = copyCredentialPojo(credentialPojo);
        CredentialPojo sdCredential = copyCredentialPojo(selectiveCredentialPojo);
        Assert.assertTrue(CredentialPojoUtils.isSelectivelyDisclosed(sdCredential.getSalt()));
        Assert.assertTrue(originalCredential.getSignature().equals(sdCredential.getSignature()));
        String originalHashValue = credentialPojoService.getCredentialPojoHash(originalCredential)
            .getResult();
        String sdHashValue = credentialPojoService.getCredentialPojoHash(sdCredential).getResult();
        Assert.assertTrue(originalHashValue.equalsIgnoreCase(sdHashValue));
        String originalAddr = evidenceService
            .createEvidence(originalCredential, createWeIdNew.getUserWeIdPrivateKey()).getResult();
        String sdAddr = evidenceService
            .createEvidence(sdCredential, createWeIdNew.getUserWeIdPrivateKey()).getResult();
        Assert.assertTrue(!StringUtils.isEmpty(originalAddr));
        Assert.assertTrue(!StringUtils.isEmpty(sdAddr));
        EvidenceInfo originalEvi = evidenceService.getEvidence(originalAddr).getResult();
        EvidenceInfo sdEvi = evidenceService.getEvidence(sdAddr).getResult();
        Assert.assertTrue(
            originalEvi.getCredentialHash().equalsIgnoreCase(sdEvi.getCredentialHash()));
        Assert.assertTrue(evidenceService.verify(originalCredential, originalAddr).getResult());
        Assert.assertTrue(evidenceService.verify(sdCredential, sdAddr).getResult());
    }


    /**
     * case14: selectively disclosure: credential.
     */
    @Test
    public void testEvidenceFull_SelectivelyDisclosure() {
        Credential tempCredential = copyCredential(credential);
        Map<String, Object> claim = tempCredential.getClaim();
        Map<String, Object> disclosure = new HashMap<>(claim);
        // Set a nothing-to-disclose CredentialWrapper flag
        for (Map.Entry<String, Object> entry : claim.entrySet()) {
            disclosure.put(entry.getKey(), "0");
        }
        CredentialWrapper credentialWrapper = new CredentialWrapper();
        credentialWrapper.setCredential(tempCredential);
        credentialWrapper.setDisclosure(disclosure);
        String originalAddr = evidenceService
            .createEvidence(tempCredential, createWeIdNew.getUserWeIdPrivateKey()).getResult();
        String sdAddr = evidenceService
            .createEvidence(credentialWrapper, createWeIdNew.getUserWeIdPrivateKey()).getResult();
        Assert.assertTrue(!StringUtils.isEmpty(originalAddr));
        Assert.assertTrue(!StringUtils.isEmpty(sdAddr));
        EvidenceInfo originalEvi = evidenceService.getEvidence(originalAddr).getResult();
        EvidenceInfo sdEvi = evidenceService.getEvidence(sdAddr).getResult();
        Assert.assertTrue(
            originalEvi.getCredentialHash().equalsIgnoreCase(sdEvi.getCredentialHash()));
        Assert.assertTrue(evidenceService.verify(tempCredential, originalAddr).getResult());
        Assert.assertTrue(evidenceService.verify(credentialWrapper, sdAddr).getResult());
    }

    /**
     * Case: multiple signers happy path.
     */
    @Test
    public void testEvidenceMultipleSignersAll() {
        List<String> signersList = new ArrayList<>();
        signersList.add(createWeIdNew.getWeId());
        CreateWeIdDataResult weId2Result = createWeId();
        signersList.add(weId2Result.getWeId());
        CreateWeIdDataResult weId3Result = createWeId();
        signersList.add(weId3Result.getWeId());
        CreateWeIdDataResult weId4Result = createWeId();
        ResponseData<String> resp = evidenceService
            .createEvidence(credential, signersList, weId4Result.getUserWeIdPrivateKey());
        Assert.assertTrue(StringUtils.isEmpty(resp.getResult()));
        Assert.assertEquals(resp.getErrorCode().intValue(),
            ErrorCode.CREDENTIAL_PRIVATE_KEY_NOT_EXISTS.getCode());
        String eviAddr = evidenceService
            .createEvidence(credential, signersList, createWeIdNew.getUserWeIdPrivateKey())
            .getResult();
        Assert.assertFalse(StringUtils.isEmpty(eviAddr));
        EvidenceInfo eviInfo = evidenceService.getEvidence(eviAddr).getResult();
        Assert.assertNotNull(eviInfo);
        Assert.assertEquals(eviInfo.getSignatures().size(), 1);
        Assert.assertEquals(eviInfo.getSigners().size(), 3);
        List<String> signersAddrList = new ArrayList<>();
        for (String weId : signersList) {
            signersAddrList.add(weId);
        }
        List<String> onChainSigners = eviInfo.getSigners();
        for (String weId : signersAddrList) {
            Assert.assertTrue(onChainSigners.contains(weId));
        }
        for (String weId : onChainSigners) {
            Assert.assertTrue(signersAddrList.contains(weId));
        }
        Assert.assertTrue(evidenceService.verify(credential, eviAddr).getResult());
        Assert.assertFalse(
            evidenceService.addSignature(credential, eviAddr, weId4Result.getUserWeIdPrivateKey())
                .getResult());
        Assert.assertTrue(
            evidenceService.addSignature(credential, eviAddr, weId2Result.getUserWeIdPrivateKey())
                .getResult());
        Assert.assertFalse(
            evidenceService
                .addSignature(credentialPojo, eviAddr, weId3Result.getUserWeIdPrivateKey())
                .getResult());
        eviInfo = evidenceService.getEvidence(eviAddr).getResult();
        Assert.assertEquals(eviInfo.getSignatures().size(), 2);
        Assert.assertTrue(evidenceService.verify(credential, eviAddr).getResult());
    }

    /**
     * Case: empty evidence.
     */
    @Test
    public void testEmptyEvidenceAll() {
        WeIdPrivateKey privKey = createWeIdNew.getUserWeIdPrivateKey();
        String eviAddr = evidenceService.createEvidence(null, privKey).getResult();
        Assert.assertFalse(StringUtils.isEmpty(eviAddr));
        EvidenceInfo evidenceInfo = evidenceService.getEvidence(eviAddr).getResult();
        Assert.assertTrue(StringUtils.isEmpty(evidenceInfo.getCredentialHash()));
        for (String sig : evidenceInfo.getSignatures()) {
            Assert.assertTrue(StringUtils.isEmpty(sig));
        }
        String hashValue = credential.getHash();
        ResponseData<Boolean> resp = evidenceService.setHashValue(hashValue, eviAddr, privKey);
        Assert.assertTrue(resp.getResult());
        Assert.assertTrue(evidenceService.verify(credential, eviAddr).getResult());
        Assert.assertTrue(evidenceService.verify(new HashString(hashValue), eviAddr).getResult());
        // Any second attempt will fail
        Assert.assertFalse(evidenceService.setHashValue(hashValue, eviAddr, privKey).getResult());
    }

    /**
     * Case: generate hash value.
     */
    @Test
    public void testGenerateHash() throws Exception {
        // Credential and CredentialPojo
        Assert.assertTrue(evidenceService.generateHash(credential).getResult().getHash()
            .equalsIgnoreCase(credential.getHash()));
        Assert.assertTrue(evidenceService.generateHash(credentialPojo).getResult().getHash()
            .equalsIgnoreCase(credentialPojo.getHash()));
        Assert.assertTrue(evidenceService.generateHash(selectiveCredentialPojo).getResult()
            .getHash().equalsIgnoreCase(selectiveCredentialPojo.getHash()));
        // Test file
        File file = new ClassPathResource("test-template.pdf").getFile();
        String fileHash = evidenceService.generateHash(file).getResult().getHash();
        Assert.assertFalse(StringUtils.isEmpty(fileHash));
        // Support GBK and UTF-8 encoding here - they will yield different hash values, though
        file = new ClassPathResource("org1.txt").getFile();
        fileHash = evidenceService.generateHash(file).getResult().getHash();
        Assert.assertFalse(StringUtils.isEmpty(fileHash));
        file = new ClassPathResource("test-hash-pic.png").getFile();
        fileHash = evidenceService.generateHash(file).getResult().getHash();
        Assert.assertFalse(StringUtils.isEmpty(fileHash));
        // Non-existent file - uncreated with createNewFile()
        file = new File("non-existent.tmp");
        Assert.assertNull(evidenceService.generateHash(file).getResult());
        Assert.assertNull(evidenceService.generateHash(StringUtils.EMPTY).getResult());
        Assert.assertFalse(StringUtils.isEmpty(
            evidenceService.generateHash("10000").getResult().getHash()));
        Assert.assertNull(evidenceService.generateHash(createCredentialArgs).getResult());
    }
}
