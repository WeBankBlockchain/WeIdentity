/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

package com.webank.weid.full.weid;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.full.TestBaseServcie;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;

/**
 * getWeIdDocumentJson method for testing WeIdService.
 *
 * @author v_wbgyang
 */
public class TestGetWeIdDocumentJson extends TestBaseServcie {

    private static final Logger logger = LoggerFactory.getLogger(TestGetWeIdDocumentJson.class);

    private static CreateWeIdDataResult createWeIdForGetJson = null;

    @Override
    public synchronized void testInit() {
        super.testInit();
        if (createWeIdForGetJson == null) {
            createWeIdForGetJson = super.createWeIdWithSetAttr();
        }
    }

    /**
     * case: get and fromJson success.
     */
    @Test
    public void testGetWeIdDocumentJson_withAttrSuccess() {

        ResponseData<String> weIdDoc =
            weIdService.getWeIdDocumentJson(createWeIdForGetJson.getWeId());
        LogUtil.info(logger, "getWeIdDocumentJson", weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        WeIdDocument weIdDocument = WeIdDocument.fromJson(weIdDoc.getResult());
        Assert.assertEquals(1, weIdDocument.getService().size());
        Assert.assertEquals(1, weIdDocument.getAuthentication().size());
        Assert.assertEquals(1, weIdDocument.getPublicKey().size());
    }

    /**
     * case: get and fromJson success.
     */
    @Test
    public void testGetWeIdDocumentJson_noAttrSuccess() {

        ResponseData<String> weIdDoc =
            weIdService.getWeIdDocumentJson(super.createWeId().getWeId());
        LogUtil.info(logger, "getWeIdDocumentJson", weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        WeIdDocument weIdDocument = WeIdDocument.fromJson(weIdDoc.getResult());
        Assert.assertEquals(0, weIdDocument.getService().size());
        Assert.assertEquals(1, weIdDocument.getAuthentication().size());
        Assert.assertEquals(1, weIdDocument.getPublicKey().size());
    }

    /**
     * case: get weIdDomJson that setService and setAuthentication .
     */
    @Test
    public void testGetWeIdDocument_twoServiceAndAuthentication() {
        CreateWeIdDataResult createWeIdResult = this.createWeId();
        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        SetServiceArgs setServiceArgs1 = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs1.setType("123");
        setServiceArgs1.setServiceEndpoint("http://test.com");

        ResponseData<Boolean> response1 = weIdService.setService(setServiceArgs1);
        LogUtil.info(logger, "setService", response1);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(true, response1.getResult());


        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdResult.getWeId());
        LogUtil.info(logger, "getWeIdDocument", weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(2, weIdDoc.getResult().getService().size());
        Assert.assertEquals(1, weIdDoc.getResult().getAuthentication().size());
        Assert.assertEquals(1, weIdDoc.getResult().getPublicKey().size());
    }


    /**
     * case: weid is invalid.
     */
    @Test
    public void testGetWeIdDocumentJson_weIdInvalid() {

        ResponseData<String> weIdDoc =
            weIdService.getWeIdDocumentJson("weid:did:123");
        LogUtil.info(logger, "getWeIdDocumentJson", weIdDoc);
        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, weIdDoc.getResult());
    }

    /**
     * case: weid format is right but not exist.
     */
    @Test
    public void testGetWeIdDocumentJson_weIdNotExist() {
        String weid = createWeIdForGetJson.getWeId();
        weid = weid.replace(weid.substring(weid.length() - 4, weid.length()), "ffff");
        ResponseData<String> weIdDoc =
            weIdService.getWeIdDocumentJson(weid);
        LogUtil.info(logger, "getWeIdDocumentJson", weIdDoc);
        Assert.assertEquals(ErrorCode.WEID_DOES_NOT_EXIST.getCode(),
            weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, weIdDoc.getResult());
    }

    /**
     * case: weid UPPER.
     */
    @Test
    public void testGetWeIdDocumentJson_weIdIsUpper() {
        String weid = createWeIdForGetJson.getWeId();
        weid = weid.toUpperCase();
        ResponseData<String> weIdDoc =
            weIdService.getWeIdDocumentJson(weid);
        LogUtil.info(logger, "getWeIdDocumentJson", weIdDoc);
        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, weIdDoc.getResult());
    }

    /**
     * case: weid is null.
     */
    @Test
    public void testGetWeIdDocumentJson_weIdIsNull() {

        ResponseData<String> weIdDoc =
            weIdService.getWeIdDocumentJson(null);
        LogUtil.info(logger, "getWeIdDocumentJson", weIdDoc);
        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, weIdDoc.getResult());
    }

    /**
     * case: weid is too long .
     */
    @Test
    public void testGetWeIdDocumentJson_weIdIsTooLong() {
        char[] chars = new char[1000];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (i % 127);
        }
        ResponseData<String> weIdDoc =
            weIdService.getWeIdDocumentJson(Arrays.toString(chars));
        LogUtil.info(logger, "getWeIdDocumentJson", weIdDoc);
        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, weIdDoc.getResult());
    }

    /**
     * case: set many times.
     */
    @Test
    public void testGetWeIdDocumentJsonCase2() {

        super.setPublicKey(createWeIdForGetJson,
            TestBaseUtil.createEcKeyPair().getPublicKey(),
            createWeIdNew.getWeId());
        super.setAuthentication(createWeIdForGetJson,
            TestBaseUtil.createEcKeyPair().getPublicKey(),
            createWeIdForGetJson.getWeId());
        super.setService(createWeIdForGetJson,
            "drivingCardServic1",
            "https://weidentity.webank.com/endpoint/8377465");

        ResponseData<String> weIdDoc =
            weIdService.getWeIdDocumentJson(createWeIdForGetJson.getWeId());
        LogUtil.info(logger, "getWeIdDocumentJson", weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNotNull(weIdDoc.getResult());
    }

    /**
     * case: WeIdentity DID is invalid.
     */
    @Test
    public void testGetWeIdDocumentJsonCase3() {

        ResponseData<String> weIdDoc = weIdService.getWeIdDocumentJson("xxxxxxxxxx");
        LogUtil.info(logger, "getWeIdDocumentJson", weIdDoc);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, weIdDoc.getResult());
    }

    /**
     * case: Simulation throws an Exception when calling the writerWithDefaultPrettyPrinter method.
     */
    @Test
    public void testGetWeIdDocumentJsonCase6() {

        MockUp<ObjectMapper> mockTest = new MockUp<ObjectMapper>() {
            @Mock
            public ObjectWriter writerWithDefaultPrettyPrinter() {
                return null;
            }
        };

        ResponseData<String> weIdDoc =
            weIdService.getWeIdDocumentJson(createWeIdForGetJson.getWeId());
        LogUtil.info(logger, "getWeIdDocumentJson", weIdDoc);

        mockTest.tearDown();

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, weIdDoc.getResult());
    }
}
