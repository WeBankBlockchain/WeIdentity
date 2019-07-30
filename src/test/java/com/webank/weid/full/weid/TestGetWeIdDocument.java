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
 * getWeIdDocument method for testing WeIdService.
 *
 * @author v_wbgyang
 */
public class TestGetWeIdDocument extends TestBaseServcie {

    private static final Logger logger = LoggerFactory.getLogger(TestGetWeIdDocument.class);


    private static CreateWeIdDataResult createWeIdForGetDoc = null;

    @Override
    public synchronized void testInit() {
        super.testInit();
        if (createWeIdForGetDoc == null) {
            createWeIdForGetDoc = super.createWeIdWithSetAttr();
        }
    }

    /**
     * case: get weIdDom that setService and setAuthentication .
     */
    @Test
    public void testGetWeIdDocument_hasServiceAndAuthentication() {

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdForGetDoc.getWeId());
        LogUtil.info(logger, "getWeIdDocument", weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(1, weIdDoc.getResult().getService().size());
        Assert.assertEquals(1, weIdDoc.getResult().getAuthentication().size());
        Assert.assertEquals(1, weIdDoc.getResult().getPublicKey().size());
    }

    /**
     * case: get weIdDom that setService and setAuthentication .
     */
    @Test
    public void testGetWeIdDocument_noServiceAndAuthentication() {

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdNew.getWeId());
        LogUtil.info(logger, "getWeIdDocument", weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(0, weIdDoc.getResult().getService().size());
        Assert.assertEquals(1, weIdDoc.getResult().getAuthentication().size());
        Assert.assertEquals(1, weIdDoc.getResult().getPublicKey().size());
    }

    /**
     * case: get weIdDom that setService and setAuthentication .
     */
    @Test
    public void testGetWeIdDocument_twoServiceAndAuthentication() {
        CreateWeIdDataResult createWeIdResult = super.createWeId();
        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);

        ResponseData<Boolean> response = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        SetServiceArgs setServiceArgs1 = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs1.setType("1234");
        setServiceArgs1.setServiceEndpoint("http:test.com");

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
     * case: WeIdentity DID is not exists.
     */
    @Test
    public void testGetWeIdDocument_weIdUpper() {

        String weid = createWeIdForGetDoc.getWeId();
        weid = weid.toUpperCase();
        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(weid);
        LogUtil.info(logger, "getWeIdDocument", weIdDoc);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(),
            weIdDoc.getErrorCode().intValue());
        Assert.assertNull(weIdDoc.getResult());
    }

    /**
     * case: WeIdentity DID is not exists.
     */
    @Test
    public void testGetWeIdDocument_weIdExist() {

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument("did:weid:0xa1c93e93622c6a0b2f52c90741e0b98ab77385a9");
        LogUtil.info(logger, "getWeIdDocument", weIdDoc);

        Assert.assertEquals(ErrorCode.WEID_DOES_NOT_EXIST.getCode(),
            weIdDoc.getErrorCode().intValue());
        Assert.assertNull(weIdDoc.getResult());
    }

    /**
     * case: set many times and get the weIdDom.
     */
    @Test
    public void testGetWeIdDocument_setManyTimes() {

        super.setPublicKey(createWeIdForGetDoc,
            TestBaseUtil.createEcKeyPair().getPublicKey(),
            createWeIdNew.getWeId());
        super.setAuthentication(createWeIdForGetDoc,
            TestBaseUtil.createEcKeyPair().getPublicKey(),
            createWeIdForGetDoc.getWeId());
        super.setService(createWeIdForGetDoc,
            "drivingCardServic1",
            "https://weidentity.webank.com/endpoint/8377465");

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdForGetDoc.getWeId());
        LogUtil.info(logger, "getWeIdDocument", weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(2, weIdDoc.getResult().getService().size());
        Assert.assertEquals(2, weIdDoc.getResult().getAuthentication().size());
        Assert.assertEquals(3, weIdDoc.getResult().getPublicKey().size());
    }

    /**
     * case: WeIdentity DID is invalid.
     */
    @Test
    public void testGetWeIdDocument_weIdInvalid() {

        ResponseData<WeIdDocument> weIdDoc = weIdService.getWeIdDocument("xxxxxxxxxx");
        LogUtil.info(logger, "getWeIdDocument", weIdDoc);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNull(weIdDoc.getResult());
    }

    /**
     * case: WeIdentity DID is null.
     */
    @Test
    public void testGetWeIdDocument_weIdIsNull() {

        ResponseData<WeIdDocument> weIdDoc = weIdService.getWeIdDocument(null);
        LogUtil.info(logger, "getWeIdDocument", weIdDoc);

        Assert.assertEquals(ErrorCode.WEID_INVALID.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertNull(weIdDoc.getResult());
    }
}
