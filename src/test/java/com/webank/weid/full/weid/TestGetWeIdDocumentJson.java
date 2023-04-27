

package com.webank.weid.full.weid;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.webank.weid.protocol.request.AuthenticationArgs;
import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.LogUtil;
import com.webank.weid.blockchain.constant.ErrorCode;
import com.webank.weid.full.TestBaseService;
import com.webank.weid.full.TestBaseUtil;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.blockchain.protocol.response.ResponseData;

/**
 * getWeIdDocumentJson method for testing WeIdService.
 *
 * @author v_wbgyang
 */
public class TestGetWeIdDocumentJson extends TestBaseService {

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
        Assert.assertEquals(2, weIdDocument.getService().size());
        Assert.assertEquals(1, weIdDocument.getAuthentication().size());
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
        Assert.assertEquals(1, weIdDocument.getService().size());
        Assert.assertEquals(1, weIdDocument.getAuthentication().size());
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
        AuthenticationArgs setAuthenticationArgs = new AuthenticationArgs();
        setAuthenticationArgs.setController(createWeIdForGetJson.getWeId());
        setAuthenticationArgs.setPublicKey(TestBaseUtil.createEcKeyPair().getPublicKey());
        weIdService.setAuthentication(createWeIdForGetJson.getWeId(),
                setAuthenticationArgs,
            createWeIdForGetJson.getUserWeIdPrivateKey());
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

        new MockUp<ObjectMapper>() {
            @Mock
            public ObjectWriter writerWithDefaultPrettyPrinter() {
                return null;
            }
        };

        ResponseData<String> weIdDoc =
            weIdService.getWeIdDocumentJson(createWeIdForGetJson.getWeId());
        LogUtil.info(logger, "getWeIdDocumentJson", weIdDoc);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), weIdDoc.getErrorCode().intValue());
        Assert.assertEquals(StringUtils.EMPTY, weIdDoc.getResult());
    }
}
