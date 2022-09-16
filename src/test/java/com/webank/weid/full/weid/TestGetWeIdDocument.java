

package com.webank.weid.full.weid;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.AuthenticationArgs;
import com.webank.weid.protocol.request.PublicKeyArgs;
import com.webank.weid.protocol.request.ServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.protocol.response.WeIdListResult;

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
    }

    /**
     * case: get weIdDom that setService and setAuthentication .
     */
    @Test
    public void testGetWeIdDocument_twoServiceAndAuthentication() {
        CreateWeIdDataResult createWeIdResult = super.createWeId();
        ServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createWeIdResult);

        ResponseData<Boolean> response = weIdService.setService(createWeIdResult.getWeId(),
            setServiceArgs, createWeIdResult.getUserWeIdPrivateKey());
        LogUtil.info(logger, "setService", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());

        ServiceArgs setServiceArgs1 = TestBaseUtil.buildSetServiceArgs(createWeIdResult);
        setServiceArgs1.setType("1234");
        setServiceArgs1.setServiceEndpoint("http:test.com");

        ResponseData<Boolean> response1 = weIdService.setService(createWeIdResult.getWeId(),
            setServiceArgs1, createWeIdResult.getUserWeIdPrivateKey());
        LogUtil.info(logger, "setService", response1);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response1.getErrorCode().intValue());
        Assert.assertEquals(true, response1.getResult());

        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdResult.getWeId());
        LogUtil.info(logger, "getWeIdDocument", weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(2, weIdDoc.getResult().getService().size());
        Assert.assertEquals(1, weIdDoc.getResult().getAuthentication().size());
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
    public void testGetWeIdDocument_setRemoveManyTimes() throws Exception {
        PasswordKey pwKey2 = TestBaseUtil.createEcKeyPair();
        AuthenticationArgs setAuthenticationArgs2 = new AuthenticationArgs();
        setAuthenticationArgs2.setController(createWeIdForGetDoc.getWeId());
        setAuthenticationArgs2.setPublicKey(pwKey2.getPublicKey());
        ResponseData<Boolean> key2Resp = weIdService.setAuthentication(
            createWeIdForGetDoc.getWeId(),
            setAuthenticationArgs2,
            createWeIdForGetDoc.getUserWeIdPrivateKey());
        super.setService(createWeIdForGetDoc,
            "drivingCardServic1",
            "https://weidentity.webank.com/endpoint/8377465");

        // test cycle 1: remove + add + remove
        ResponseData<WeIdDocument> weIdDoc =
            weIdService.getWeIdDocument(createWeIdForGetDoc.getWeId());
        LogUtil.info(logger, "getWeIdDocument", weIdDoc);
        System.out.println("1" + new ObjectMapper()
            .writerWithDefaultPrettyPrinter().writeValueAsString(weIdDoc.getResult()));

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(2, weIdDoc.getResult().getService().size());
        Assert.assertEquals(2, weIdDoc.getResult().getAuthentication().size());

        // test cycle 2: remove another pubkey and authentication
        // remove the pre-created authentication

        ResponseData<WeIdDocument> weIdDoc2 = weIdService
            .getWeIdDocument(createWeIdForGetDoc.getWeId());
        LogUtil.info(logger, "getWeIdDocument-2", weIdDoc2);
        System.out.println("2" + new ObjectMapper()
            .writerWithDefaultPrettyPrinter().writeValueAsString(weIdDoc2.getResult()));
        Assert.assertEquals(2, weIdDoc2.getResult().getAuthentication().size());

        // test cycle 4: remove the authentication of a WeID while preserves its public key
        AuthenticationArgs setAuthenticationArgs = new AuthenticationArgs();
        setAuthenticationArgs.setController(createWeIdForGetDoc.getWeId());
        setAuthenticationArgs
            .setPublicKey(createWeIdForGetDoc.getUserWeIdPublicKey().getPublicKey());
        ResponseData<Boolean> res5 = weIdService.revokeAuthentication(
            createWeIdForGetDoc.getWeId(),
            setAuthenticationArgs,
            createWeIdForGetDoc.getUserWeIdPrivateKey());
        weIdDoc2 = weIdService
            .getWeIdDocument(createWeIdForGetDoc.getWeId());
        System.out.println("4" + new ObjectMapper()
            .writerWithDefaultPrettyPrinter().writeValueAsString(weIdDoc2.getResult()));
        LogUtil.info(logger, "getWeIdDocument-4", weIdDoc2);
        Assert.assertEquals(1, weIdDoc2.getResult().getAuthentication().size());

        // test cycle 5: add the pre-removed authentication again
        ResponseData<Boolean> res6 = weIdService.setAuthentication(
            createWeIdForGetDoc.getWeId(),
            setAuthenticationArgs,
            createWeIdForGetDoc.getUserWeIdPrivateKey());
        weIdDoc2 = weIdService
            .getWeIdDocument(createWeIdForGetDoc.getWeId());
        System.out.println("5" + new ObjectMapper()
            .writerWithDefaultPrettyPrinter().writeValueAsString(weIdDoc2.getResult()));
        LogUtil.info(logger, "getWeIdDocument5", weIdDoc2);
        Assert.assertEquals(2, weIdDoc2.getResult().getAuthentication().size());
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

    /**
     * case: getWeIdList By publicKeyList.
     */
    @Test
    public void testGetWeIdListByPubkeyList() {
        List<WeIdPublicKey> pubKeyList = new ArrayList<>();
        int num = 5;
        for (int i = 0; i < num; i++) {
            WeIdPublicKey publicKey = new WeIdPublicKey();
            publicKey.setPublicKey(TestBaseUtil.createEcKeyPair().getPublicKey());
            pubKeyList.add(publicKey);
        }
        ResponseData<WeIdListResult> weIdListRes = weIdService.getWeIdListByPubKeyList(pubKeyList);
        LogUtil.info(logger, "getWeIdListByPubKeyList", weIdListRes);
        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdListRes.getErrorCode().intValue());
        Assert.assertEquals(num, weIdListRes.getResult().getWeIdList().size());
    }
}
