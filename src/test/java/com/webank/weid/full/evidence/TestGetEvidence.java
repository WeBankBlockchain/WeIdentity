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

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.protocol.base.EvidenceInfo;
import com.webank.weid.protocol.base.HashString;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.DateUtils;

/**
 * TestGetEvidence v_wbpenghu.
 */
public class TestGetEvidence extends TestBaseService {

    private static final Logger logger = LoggerFactory.getLogger(TestGetEvidence.class);

    @Override
    public synchronized void testInit() {
        super.testInit();
    }

    /**
     * case1: success.
     */
    @Test
    public void testGetEvidence_success() {
        HashString str = new HashString(
            DataToolUtils.sha3(DateUtils.getNoMillisecondTimeStampString()));
        ResponseData<String> evidence = evidenceService
            .createEvidence(str, createWeIdResultWithSetAttr.getUserWeIdPrivateKey());
        Assert.assertTrue(!evidence.getResult().isEmpty());
        String evidenceAddress = evidence.getResult();
        ResponseData<EvidenceInfo> responseData = evidenceService
            .getEvidence(evidenceAddress);
        logger.info("testGetEvidenceCase1 result :" + responseData);
        Assert.assertEquals(0, responseData.getErrorCode().intValue());
        Assert.assertEquals("success", responseData.getErrorMessage());
        EvidenceInfo evidenceInfo = responseData.getResult();
        for (int i = 1; i < evidenceInfo.getSignatures().size(); i++) {
            Assert.assertEquals("0x0000000000000000000000000000000000000000",
                evidenceInfo.getSigners().get(i));
        }
    }

    /**
     * case2: address is null.
     */
    @Test
    public void testGetEvidence_HashNull() {

        ResponseData<EvidenceInfo> responseData = evidenceService.getEvidence(null);
        logger.info("testGetEvidenceCase2 result :" + responseData);
        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(),
            responseData.getErrorCode().intValue());
        Assert.assertEquals("input parameter is illegal.", responseData.getErrorMessage());
        Assert.assertNull(responseData.getResult());
    }

    /**
     * case3: address is "".
     */
    @Test
    public void testGetEvidence_HashIllegal() {
        String evidenceAddress = "sdasfdcscwwewecas";
        ResponseData<EvidenceInfo> responseData = evidenceService.getEvidence(evidenceAddress);
        logger.info("testGetEvidenceCase3 result :" + responseData);
        Assert.assertEquals(ErrorCode.ILLEGAL_INPUT.getCode(),
            responseData.getErrorCode().intValue());
        Assert.assertNull(responseData.getResult());
    }
}
