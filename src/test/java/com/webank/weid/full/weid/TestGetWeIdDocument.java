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
import com.webank.weid.common.PasswordKey;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.WeIdConstant.PublicKeyType;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.PublicKeyArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.DataToolUtils;

/**
 * getWeIdDocument method for testing WeIdService.
 *
 * @author v_wbgyang
 */
public class TestGetWeIdDocument extends TestBaseService {

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

    @Test
    public void testDifferentPublicKeyType() {
        CreateWeIdDataResult createWeIdResult = super.createWeId();
        SetPublicKeyArgs setPublicKeyArgs = new SetPublicKeyArgs();
        setPublicKeyArgs.setWeId(createWeIdResult.getWeId());
        setPublicKeyArgs.setPublicKey("bcabu298t876Buc");
        setPublicKeyArgs.setUserWeIdPrivateKey(new WeIdPrivateKey());
        setPublicKeyArgs.getUserWeIdPrivateKey()
            .setPrivateKey(createWeIdResult.getUserWeIdPrivateKey().getPrivateKey());
        setPublicKeyArgs.setType(PublicKeyType.RSA);
        setPublicKeyArgs.setOwner(createWeIdResult.getWeId());
        weIdService.setPublicKey(setPublicKeyArgs);
        ResponseData<WeIdDocument> weIdDoc = weIdService
            .getWeIdDocument(createWeIdResult.getWeId());
        Assert.assertEquals(weIdDoc.getResult().getPublicKey().size(), 2);
        Assert.assertTrue(weIdDoc.getResult().getPublicKey().get(0).getType().equals("RSA"));
        // test delegate
        PublicKeyArgs publicKeyArgs = new PublicKeyArgs();
        publicKeyArgs.setOwner(setPublicKeyArgs.getOwner());
        publicKeyArgs.setPublicKey("abcabac123123");
        publicKeyArgs.setType(PublicKeyType.RSA);
        publicKeyArgs.setWeId(setPublicKeyArgs.getWeId());
        WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
        weIdAuthentication.setWeId(DataToolUtils.convertPrivateKeyToDefaultWeId(privateKey));
        weIdAuthentication.setWeIdPrivateKey(new WeIdPrivateKey());
        weIdAuthentication.getWeIdPrivateKey().setPrivateKey(privateKey);
        ResponseData<Boolean> resp = weIdService
            .delegateSetPublicKey(publicKeyArgs, weIdAuthentication);
        weIdDoc = weIdService.getWeIdDocument(createWeIdResult.getWeId());
        Assert.assertEquals(weIdDoc.getResult().getPublicKey().size(), 3);
        weIdAuthentication = new WeIdAuthentication();
        weIdAuthentication.setWeId(createWeIdResult.getWeId());
        weIdAuthentication.setWeIdPrivateKey(createWeIdResult.getUserWeIdPrivateKey());
        ResponseData<Boolean> resp2 = weIdService
            .delegateSetPublicKey(publicKeyArgs, weIdAuthentication);
        weIdDoc = weIdService.getWeIdDocument(createWeIdResult.getWeId());
        Assert.assertEquals(weIdDoc.getResult().getPublicKey().size(), 3);
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
    public void testGetWeIdDocument_setRemoveManyTimes() {

        super.setPublicKey(createWeIdForGetDoc,
            TestBaseUtil.createEcKeyPair().getPublicKey(),
            createWeIdNew.getWeId());
        PasswordKey tempPwKey = TestBaseUtil.createEcKeyPair();
        super.setAuthentication(createWeIdForGetDoc,
            tempPwKey.getPublicKey(),
            createWeIdForGetDoc.getWeId());
        super.setService(createWeIdForGetDoc,
            "drivingCardServic1",
            "https://weidentity.webank.com/endpoint/8377465");
        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createWeIdForGetDoc);
        PasswordKey pwKey = TestBaseUtil.createEcKeyPair();
        setPublicKeyArgs.setPublicKey(pwKey.getPublicKey());
        // Set the owner to be the original guy
        setPublicKeyArgs.setOwner(createWeIdForGetDoc.getWeId());
        setPublicKeyArgs.setWeId(createWeIdForGetDoc.getWeId());

        // test cycle 1: remove + add + remove
        ResponseData<Boolean> res1 = weIdService
            .removePublicKeyWithAuthentication(setPublicKeyArgs);
        ResponseData<Boolean> res2 = weIdService.setPublicKey(setPublicKeyArgs);
        ResponseData<Boolean> res3 = weIdService
            .removePublicKeyWithAuthentication(setPublicKeyArgs);

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdForGetDoc.getWeId());
        LogUtil.info(logger, "getWeIdDocument", weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(2, weIdDoc.getResult().getService().size());
        Assert.assertEquals(2, weIdDoc.getResult().getAuthentication().size());
        Assert.assertEquals(3, weIdDoc.getResult().getPublicKey().size());

        // test cycle 2: remove another pubkey and authentication
        // remove the pre-created authentication
        setPublicKeyArgs.setPublicKey(tempPwKey.getPublicKey());
        ResponseData<Boolean> res4 = weIdService
            .removePublicKeyWithAuthentication(setPublicKeyArgs);

        ResponseData<WeIdDocument> weIdDoc2 = weIdService
            .getWeIdDocument(createWeIdForGetDoc.getWeId());
        LogUtil.info(logger, "getWeIdDocument-2", weIdDoc2);
        Assert.assertEquals(1, weIdDoc2.getResult().getAuthentication().size());
        Assert.assertEquals(2, weIdDoc2.getResult().getPublicKey().size());

        // test cycle 3: create a new WeID and try to remove its only existing pubkey and auth
        CreateWeIdDataResult tempWdr = super.createWeIdWithSetAttr();
        setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(tempWdr);
        ResponseData<Boolean> result = weIdService
            .removePublicKeyWithAuthentication(setPublicKeyArgs);
        Assert.assertFalse(result.getResult());
        Assert.assertEquals(result.getErrorCode().intValue(),
            ErrorCode.WEID_CANNOT_REMOVE_ITS_OWN_PUB_KEY_WITHOUT_BACKUP.getCode());
        weIdDoc2 = weIdService
            .getWeIdDocument(createWeIdForGetDoc.getWeId());
        LogUtil.info(logger, "getWeIdDocument-3", weIdDoc2);

        // test cycle 4: remove the authentication of a WeID while preserves its public key
        SetAuthenticationArgs setAuthenticationArgs = new SetAuthenticationArgs();
        setAuthenticationArgs.setOwner(createWeIdForGetDoc.getWeId());
        setAuthenticationArgs
            .setPublicKey(createWeIdForGetDoc.getUserWeIdPublicKey().getPublicKey());
        setAuthenticationArgs.setUserWeIdPrivateKey(createWeIdForGetDoc.getUserWeIdPrivateKey());
        setAuthenticationArgs.setWeId(createWeIdForGetDoc.getWeId());
        ResponseData<Boolean> res5 = weIdService.removeAuthentication(setAuthenticationArgs);
        weIdDoc2 = weIdService
            .getWeIdDocument(createWeIdForGetDoc.getWeId());
        LogUtil.info(logger, "getWeIdDocument-4", weIdDoc2);
        Assert.assertEquals(0, weIdDoc2.getResult().getAuthentication().size());
        Assert.assertEquals(2, weIdDoc2.getResult().getPublicKey().size());

        // test cycle 5: add the pre-removed authentication again
        ResponseData<Boolean> res6 = weIdService.setAuthentication(setAuthenticationArgs);
        weIdDoc2 = weIdService
            .getWeIdDocument(createWeIdForGetDoc.getWeId());
        LogUtil.info(logger, "getWeIdDocument5", weIdDoc2);
        Assert.assertEquals(1, weIdDoc2.getResult().getAuthentication().size());
        Assert.assertEquals(2, weIdDoc2.getResult().getPublicKey().size());
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
