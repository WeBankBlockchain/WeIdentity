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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.webank.weid.full.TestBaseService;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.CredentialWrapper;
import com.webank.weid.protocol.base.EvidenceInfo;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.inf.Hashable;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.EvidenceService;
import com.webank.weid.service.impl.EvidenceServiceImpl;
import com.webank.weid.service.impl.engine.EngineFactory;
import com.webank.weid.service.impl.engine.EvidenceServiceEngine;
import com.webank.weid.util.CredentialPojoUtils;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.DateUtils;

/**
 * Test CreateEvidence.
 *
 * @author v_wbgyang
 */
public class TestCreateEvidence extends TestBaseService {

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
        CredentialPojo credential = createCredentialPojo(createCredentialPojoArgs);
        credential.setId(UUID.randomUUID().toString());
        String hash = evidenceService.generateHash(credential).getResult().getHash();
        // Direct call of add - should fail
        ResponseData<Boolean> addResp = evidenceService.addLogByHash(hash, "3.23",
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        Assert.assertFalse(addResp.getResult());
        // Must create evidence and sign first!
        ResponseData<String> response = evidenceService.createEvidence(credential,
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        evidenceService.addLogByHash(hash, "1.23",
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        evidenceService.addLogByHash(hash, "13.15",
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertTrue(!response.getResult().isEmpty());
        ResponseData<EvidenceInfo> eviInfo = evidenceService.getEvidence(hash);
        EvidenceInfo evidenceInfo = eviInfo.getResult();
        Assert.assertTrue(evidenceInfo.getCredentialHash().equalsIgnoreCase(hash));
        String signerWeId = tempCreateWeIdResultWithSetAttr.getWeId();
        Assert.assertTrue(evidenceInfo.getSigners().contains(signerWeId));
        Assert.assertEquals(evidenceInfo.getSignInfo().get(signerWeId).getLogs().size(), 2);
        Assert.assertTrue(
            evidenceInfo.getSignInfo().get(signerWeId).getLogs().get(0).equals("1.23"));
        Assert.assertTrue(
            evidenceInfo.getSignInfo().get(signerWeId).getLogs().get(1).equals("13.15"));
        ResponseData<Boolean> resp = evidenceService.verifySigner(evidenceInfo, signerWeId);
        Assert.assertTrue(resp.getResult());
    }

    @Test
    public void testCreateEvidence_MultipleSigners() {
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr =
            super.copyCreateWeId(createWeIdResultWithSetAttr);
        CredentialPojo credential = createCredentialPojo(createCredentialPojoArgs);
        credential.setId(UUID.randomUUID().toString());
        String hash = evidenceService.generateHash(credential).getResult().getHash();
        evidenceService.createEvidence(credential,
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        evidenceService.addLogByHash(hash, "1.23",
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        evidenceService.addLogByHash(hash, "13.15",
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        // Another guy signs
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr2 = createWeIdWithSetAttr();
        evidenceService.createEvidence(credential,
            tempCreateWeIdResultWithSetAttr2.getUserWeIdPrivateKey());
        evidenceService.addLogByHash(hash, "abc",
            tempCreateWeIdResultWithSetAttr2.getUserWeIdPrivateKey());
        evidenceService.addLogByHash(hash, "eef",
            tempCreateWeIdResultWithSetAttr2.getUserWeIdPrivateKey());
        // Now check
        ResponseData<EvidenceInfo> eviInfo = evidenceService.getEvidence(hash);
        EvidenceInfo evidenceInfo = eviInfo.getResult();
        String signer1 = tempCreateWeIdResultWithSetAttr.getWeId();
        String signer2 = tempCreateWeIdResultWithSetAttr2.getWeId();
        Assert.assertEquals(evidenceInfo.getSignInfo().get(signer1).getLogs().size(), 2);
        Assert.assertEquals(evidenceInfo.getSignInfo().get(signer2).getLogs().size(), 2);
        ResponseData<Boolean> resp = evidenceService.verifySigner(evidenceInfo, signer1);
        Assert.assertTrue(resp.getResult());
        resp = evidenceService.verifySigner(evidenceInfo, signer2);
        Assert.assertTrue(resp.getResult());
    }

    @Test
    public void testCreateEvidence_CustomKeyHappyPath() {
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr =
            super.copyCreateWeId(createWeIdResultWithSetAttr);
        CredentialPojo credential = createCredentialPojo(createCredentialPojoArgs);
        credential.setId(UUID.randomUUID().toString());
        String hash = evidenceService.generateHash(credential).getResult().getHash();
        String credId = credential.getId();
        ResponseData<String> cresp = evidenceService.createEvidenceWithLogAndCustomKey(
            credential,
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey(),
            "Ironman",
            hash
        );
        Assert.assertTrue(StringUtils.isEmpty(cresp.getResult()));
        evidenceService.createEvidenceWithLogAndCustomKey(
            credential,
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey(),
            "Ironman",
            credId
        );
        ResponseData<EvidenceInfo> eviHash = evidenceService.getEvidence(hash);
        EvidenceInfo evi1 = eviHash.getResult();
        ResponseData<EvidenceInfo> eviCustomKey = evidenceService.getEvidenceByCustomKey(credId);
        EvidenceInfo evi2 = eviCustomKey.getResult();
        Assert.assertEquals(evi1.getSigners(), evi2.getSigners());
        Assert.assertEquals(evi1.getSignatures(), evi2.getSignatures());
        String signer = tempCreateWeIdResultWithSetAttr.getWeId();
        Assert.assertEquals(evi1.getSignInfo().get(signer).getLogs(),
            evi2.getSignInfo().get(signer).getLogs());
        evidenceService.addLogByHash(hash, "Insane",
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        evidenceService.addLogByCustomKey(credId, "Difficult",
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        evi2 = evidenceService.getEvidenceByCustomKey(credId).getResult();
        Assert.assertEquals(evi2.getSignInfo().get(signer).getLogs().size(), 3);
        List<String> list = Arrays.asList("Ironman", "Insane", "Difficult");
        Assert.assertEquals(evi2.getSignInfo().get(signer).getLogs(), list);
    }

    @Test
    public void testCreateEvidence_CustomKeyMultiSignerMultiTimes() {
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr =
            super.copyCreateWeId(createWeIdResultWithSetAttr);
        CredentialPojo credential = createCredentialPojo(createCredentialPojoArgs);
        credential.setId(UUID.randomUUID().toString());
        String credId = credential.getId();
        List<String> list = new ArrayList<>();
        String log = "X:112.5,Y:97.6";
        list.add(log);
        evidenceService.createEvidenceWithLogAndCustomKey(
            credential,
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey(),
            log,
            credId
        );
        log = "X:122.5,Y:94.3";
        list.add(log);
        evidenceService.createEvidenceWithLogAndCustomKey(
            credential,
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey(),
            log,
            credId
        );
        log = "X:102.5,Y:99.1";
        list.add(log);
        evidenceService.createEvidenceWithLogAndCustomKey(
            credential,
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey(),
            log,
            credId
        );
        log = "X:0,Y:0";
        list.add(log);
        evidenceService.addLogByCustomKey(credId, log,
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr2 = createWeIdWithSetAttr();
        evidenceService.createEvidenceWithLogAndCustomKey(
            credential,
            tempCreateWeIdResultWithSetAttr2.getUserWeIdPrivateKey(),
            "Age:22",
            credId
        );
        ResponseData<EvidenceInfo> eviCustomKey = evidenceService.getEvidenceByCustomKey(credId);
        EvidenceInfo evi = eviCustomKey.getResult();
        String signer = tempCreateWeIdResultWithSetAttr.getWeId();
        Assert.assertEquals(evi.getSignInfo().get(signer).getLogs(), list);
        String signer2 = tempCreateWeIdResultWithSetAttr2.getWeId();
        Assert.assertTrue(evi.getSignInfo().get(signer2).getLogs().contains("Age:22")
            && evi.getSignInfo().get(signer2).getLogs().size() == 1);
    }

    @Test
    public void testAddLogsAll() {
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr =
            super.copyCreateWeId(createWeIdResultWithSetAttr);
        CredentialPojo credential = createCredentialPojo(createCredentialPojoArgs);
        credential.setId(UUID.randomUUID().toString());
        String credId = credential.getId();
        String log = DataToolUtils.serialize(credential);
        evidenceService.createEvidenceWithLogAndCustomKey(
            credential,
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey(),
            log,
            credId
        );
        ResponseData<EvidenceInfo> eviResp = evidenceService.getEvidenceByCustomKey(credId);
        EvidenceInfo evi = eviResp.getResult();
        String signer = tempCreateWeIdResultWithSetAttr.getWeId();
        Assert.assertTrue(evi.getSignInfo().get(signer).getLogs().contains(log));
        int length = 50; // Up to 2M can still work
        StringBuffer outputBuffer = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            outputBuffer.append("a");
        }
        log = outputBuffer.toString();
        evidenceService.addLogByCustomKey(credId, log,
            tempCreateWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        evi = evidenceService.getEvidenceByCustomKey(credId).getResult();
        Assert.assertNotNull(evi);
        Assert.assertTrue(evi.getSignInfo().get(signer).getLogs().contains(log));
    }

    @Test
    public void testBatchCreate() throws Exception {
        CreateWeIdDataResult tempCreateWeIdResultWithSetAttr =
            super.copyCreateWeId(createWeIdResultWithSetAttr);
        List<String> hashValues = new ArrayList<>();
        List<String> signatures = new ArrayList<>();
        List<Long> timestamps = new ArrayList<>();
        List<String> signers = new ArrayList<>();
        List<String> logs = new ArrayList<>();
        List<String> customKeys = new ArrayList<>();
        int batchSize = 100;
        for (int i = 0; i < batchSize; i++) {
            CredentialPojo credential = createCredentialPojo(createCredentialPojoArgs);
            credential.setId(UUID.randomUUID().toString());
            String hash = credential.getHash();
            hashValues.add(credential.getHash());
            signatures.add(new String(DataToolUtils.base64Encode(DataToolUtils
                .simpleSignatureSerialization(DataToolUtils.signMessage(hash, privateKey))),
                StandardCharsets.UTF_8));
            timestamps.add(System.currentTimeMillis());
            signers.add(DataToolUtils.convertPrivateKeyToDefaultWeId(privateKey));
            logs.add("test log" + i);
            if (i % 2 == 1) {
                customKeys.add(String.valueOf(System.currentTimeMillis()));
            } else {
                customKeys.add(StringUtils.EMPTY);
            }
        }
        EvidenceServiceEngine engine = EngineFactory.createEvidenceServiceEngine();

        // raw creation
        Long start = System.currentTimeMillis();
        ResponseData<List<Boolean>> resp = engine
            .batchCreateEvidence(hashValues, signatures, logs, timestamps, signers, privateKey);
        Long end = System.currentTimeMillis();
        System.out.println("Batch creation w/ size: " + batchSize + " takes time (ms): " + (String
            .valueOf(end - start)));
        List<Boolean> booleans = resp.getResult();
        Assert.assertEquals(booleans.size(), hashValues.size());
        Boolean result = true;
        for (int i = 0; i < booleans.size(); i++) {
            result = result && booleans.get(i).booleanValue();
        }
        Assert.assertTrue(result);

        // test illegal hashs
        List<String> faultyHashValues = new ArrayList<>();
        faultyHashValues.addAll(hashValues);
        faultyHashValues.set(1, null);
        ResponseData<List<Boolean>> faultyResp = engine
            .batchCreateEvidence(faultyHashValues, signatures, logs, timestamps, signers,
                privateKey);
        booleans = faultyResp.getResult();
        Assert.assertFalse(booleans.get(1));

        // custom keys (semi filled)
        start = System.currentTimeMillis();
        resp = engine
            .batchCreateEvidenceWithCustomKey(hashValues, signatures, logs, timestamps, signers,
                customKeys, privateKey);
        end = System.currentTimeMillis();
        System.out.println(
            "Batch creation w/ custom keys and size: " + batchSize + " takes time (ms): " + (String
                .valueOf(end - start)));
        booleans = resp.getResult();
        Assert.assertEquals(booleans.size(), hashValues.size());
        result = true;
        for (int i = 0; i < booleans.size(); i++) {
            result = result && booleans.get(i).booleanValue();
        }
        Assert.assertTrue(result);

        // Check get
        String hash0 = hashValues.get(0);
        String hash1 = hashValues.get(1);
        String key1 = customKeys.get(1);
        EvidenceInfo evidenceInfo0 = evidenceService.getEvidence(hash0).getResult();
        EvidenceInfo evidenceInfo1 = evidenceService.getEvidence(hash1).getResult();
        EvidenceInfo evidenceInfo1k = evidenceService.getEvidenceByCustomKey(key1).getResult();
        Assert.assertNotNull(evidenceInfo0);
        Assert.assertNotNull(evidenceInfo1);
        Assert.assertNotNull(evidenceInfo1k);
        // ran for 3 times
        Assert.assertEquals(evidenceInfo0.getSignInfo()
            .get(DataToolUtils.convertPrivateKeyToDefaultWeId(privateKey)).getLogs().size(), 3);
        // ran only twice (one set null in between)
        Assert.assertEquals(evidenceInfo1.getSignInfo()
            .get(DataToolUtils.convertPrivateKeyToDefaultWeId(privateKey)).getLogs().size(), 2);
        Assert.assertEquals(evidenceInfo1.getCredentialHash(), evidenceInfo1k.getCredentialHash());
    }

    @Test
    public void testRawCreation() {
        CredentialPojo credential = createCredentialPojo(createCredentialPojoArgs);
        credential.setId(UUID.randomUUID().toString());
        String hash = credential.getHash();
        String sig = "testSig";
        String log = "";
        String customKey = credential.getId();
        ResponseData<Boolean> resp = evidenceService
            .createRawEvidenceWithCustomKey(hash, sig, log, System.currentTimeMillis(), customKey,
                privateKey);
        Assert.assertTrue(resp.getResult());
        ResponseData<EvidenceInfo> eviResp = evidenceService.getEvidenceByCustomKey(customKey);
        System.out.println();
        Assert.assertTrue(eviResp.getResult().getSignatures().get(0).equalsIgnoreCase(sig));
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
