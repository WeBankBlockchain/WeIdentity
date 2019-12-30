/*
 *       Copyright© (2019) WeBank Co., Ltd.
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
