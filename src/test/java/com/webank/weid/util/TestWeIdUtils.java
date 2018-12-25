/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.util;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.protocol.base.WeIdPrivateKey;

/**
 * Test WeIdUtils.
 * 
 * @author v_wbjnzhang
 *
 */
public class TestWeIdUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(TestWeIdUtils.class);

    @Test
    public void testConvertWeIdToAddress() {
        String weid = null;
        String address = WeIdUtils.convertWeIdToAddress(weid);
        Assert.assertEquals(address, "");

        weid = "gdhgdrhrhjrersgfds";
        address = WeIdUtils.convertWeIdToAddress(weid);
        Assert.assertEquals(address, "");

        address = "0xce84646754976464646";
        weid = WeIdUtils.convertAddressToWeId(address);
        logger.info(weid);
        address = WeIdUtils.convertWeIdToAddress(weid);
        logger.info(address);
    }

    @Test
    public void testIsWeIdValid() {
        String weid = null;
        boolean result = WeIdUtils.isWeIdValid(weid);
        Assert.assertFalse(result);

        weid = "gdhgdrhrhjrersgfds";
        result = WeIdUtils.isWeIdValid(weid);
        Assert.assertFalse(result);

        weid = "gdhgdrhrhjrersgfds:ffsf";
        result = WeIdUtils.isWeIdValid(weid);
        Assert.assertFalse(result);

        weid = "did:weid:0xce84646754976464646";
        result = WeIdUtils.isWeIdValid(weid);
        Assert.assertTrue(result);
    }

    @Test
    public void testConvertPublicKeyToWeId() {

        String publicKey =
            "4152630134607745313775653941373712642376482837388159007706431164834407808290456176"
            + "569372213582021586381145279900416808342958821437075568109344613716670953";
        String weId = WeIdUtils.convertPublicKeyToWeId(publicKey);
        Assert.assertNotNull(weId);
        logger.info(weId);
    }

    @Test
    public void testIsKeypairMatch() {

        String publicKey =
            "41526301346077453137756539413737126423764828373881590077064311648344078082904561"
            + "76569372213582021586381145279900416808342958821437075568109344613716670953";
        String privateKey =
            "58317564669857453586637110679746575832914889677346283755719850144028639639651";
        boolean result = WeIdUtils.isKeypairMatch(privateKey, publicKey);
        Assert.assertTrue(result);

        privateKey = "646467494548664";
        result = WeIdUtils.isKeypairMatch(privateKey, publicKey);
        Assert.assertFalse(result);
    }

    @Test
    public void testIsPrivateKeyValid() {
        String privateKey =
            "58317564669857453586637110679746575832914889677346283755719850144028639639651";
        WeIdPrivateKey key = new WeIdPrivateKey();
        boolean result = WeIdUtils.isPrivateKeyValid(key);
        Assert.assertFalse(result);

        key.setPrivateKey(privateKey);
        result = WeIdUtils.isPrivateKeyValid(key);
        Assert.assertTrue(result);

        result = WeIdUtils.isPrivateKeyValid(null);
        Assert.assertFalse(result);
    }
}
