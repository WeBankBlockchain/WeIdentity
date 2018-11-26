/*
 *       Copyright© (2018) WeBank Co., Ltd.
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

import com.webank.weid.protocol.base.WeIdPrivateKey;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test WeIdUtils.
 * 
 * @author v_wbjnzhang
 *
 */
public class TestWeIdUtils {

    @Test
    public void testConvertWeIdToAddress() {
        String weid = null;
        String address = WeIdUtils.convertWeIdToAddress(weid);
        assertEquals(address, "");

        weid = "gdhgdrhrhjrersgfds";
        address = WeIdUtils.convertWeIdToAddress(weid);
        assertEquals(address, "");

        address = "0xce84646754976464646";
        weid = WeIdUtils.convertAddressToWeId(address);
        System.out.println(weid);
        address = WeIdUtils.convertWeIdToAddress(weid);
        System.out.println(address);
    }

    @Test
    public void testIsWeIdValid() {
        String weid = null;
        boolean result = WeIdUtils.isWeIdValid(weid);
        assertFalse(result);

        weid = "gdhgdrhrhjrersgfds";
        result = WeIdUtils.isWeIdValid(weid);
        assertFalse(result);

        weid = "gdhgdrhrhjrersgfds:ffsf";
        result = WeIdUtils.isWeIdValid(weid);
        assertFalse(result);

        weid = "did:weid:0xce84646754976464646";
        result = WeIdUtils.isWeIdValid(weid);
        assertTrue(result);
    }

    @Test
    public void testConvertPublicKeyToWeId() {

        String publicKey =
            "4152630134607745313775653941373712642376482837388159007706431164834407808290456176569372213582021586381145279900416808342958821437075568109344613716670953";
        String weid = WeIdUtils.convertPublicKeyToWeId(publicKey);
        System.out.println(weid);
    }

    @Test
    public void testIsKeypairMatch() {

        String publicKey =
            "4152630134607745313775653941373712642376482837388159007706431164834407808290456176569372213582021586381145279900416808342958821437075568109344613716670953";
        String privateKey =
            "58317564669857453586637110679746575832914889677346283755719850144028639639651";
        boolean result = WeIdUtils.isKeypairMatch(privateKey, publicKey);
        assertTrue(result);

        privateKey = "646467494548664";
        result = WeIdUtils.isKeypairMatch(privateKey, publicKey);
        assertFalse(result);
    }

    @Test
    public void testIsPrivateKeyValid() {

        String privateKey =
            "58317564669857453586637110679746575832914889677346283755719850144028639639651";
        WeIdPrivateKey key = new WeIdPrivateKey();
        boolean result = WeIdUtils.isPrivateKeyValid(key);
        assertFalse(result);

        key.setPrivateKey(privateKey);
        result = WeIdUtils.isPrivateKeyValid(key);
        assertTrue(result);

        result = WeIdUtils.isPrivateKeyValid(null);
        assertFalse(result);
    }
}
