

package com.webank.weid.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.webank.weid.protocol.response.timestamp.wesign.GetTimestampResponse;
import com.webank.weid.protocol.response.timestamp.wesign.VerifyTimestampResponse;

public class TestTimestampUtils {

    @Test
    public void integrationTestWeSign()
        throws Exception {
        if (StringUtils.isBlank(PropertyUtils.getProperty("wesign.accessTokenUrl"))) {
            return;
        }
        String accessToken = TimestampUtils.getWeSignAccessToken();
        Assert.assertTrue(!StringUtils.isBlank(accessToken));
        String signTicket = TimestampUtils.getWeSignTicketString(accessToken);
        Assert.assertTrue(!StringUtils.isBlank(signTicket));
        String nonce = TimestampUtils.generateNonce(32, 32).get(0);
        Assert.assertTrue(!StringUtils.isBlank(nonce));
        String originalText = "Value to be signed.";
        String hashValue = TimestampUtils.getWeSignHash(originalText);
        Assert.assertEquals(hashValue.length(), 40);
        String signParam = TimestampUtils.getWeSignParam(nonce, signTicket);
        GetTimestampResponse getResp = TimestampUtils.getTimestamp(signTicket, nonce, hashValue);
        Assert.assertEquals(getResp.getCode(), 0);
        VerifyTimestampResponse verifyResp = TimestampUtils
            .verifyTimestamp(signParam, nonce, hashValue,
                getResp.getResult().getData().getB64TimeStamp());
        Assert.assertEquals(verifyResp.getCode(), 0);
    }
}
