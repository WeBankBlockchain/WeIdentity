

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
 */
public class TestWeIdUtils {

    private static final Logger logger = LoggerFactory.getLogger(TestWeIdUtils.class);

    @Test
    public void testConvertWeIdToAddress() {

        String address = WeIdUtils.convertWeIdToAddress(null);
        Assert.assertEquals(address, "");

        String weid = "gdhgdrhrhjrersgfds";
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

        boolean result = WeIdUtils.isWeIdValid(null);
        Assert.assertFalse(result);

        String weid = "gdhgdrhrhjrersgfds";
        result = WeIdUtils.isWeIdValid(weid);
        Assert.assertFalse(result);

        weid = "gdhgdrhrhjrersgfds:ffsf";
        result = WeIdUtils.isWeIdValid(weid);
        Assert.assertFalse(result);

        weid = "did:weid:0x123471623125358127679*";
        result = WeIdUtils.isWeIdValid(weid);
        Assert.assertFalse(result);

        weid = "did:weid:0xbb1670306aedfaeb75cff9581c99e56ba4797431";
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

//    @Test
//    public void testIsKeypairMatch() {
//
//        String publicKey =
//            /*"41526301346077453137756539413737126423764828373881590077064311648344078082904561"
//                + "76569372213582021586381145279900416808342958821437075568109344613716670953";*/
//            "616487157832629935695248334732183001470968861496251911400081764434326377050103015"
//            + "62844338994634470121084706056348088101890813246949904622670239758638712304";
//        String privateKey =
//            //"58317564669857453586637110679746575832914889677346283755719850144028639639651";
//            "95190837481536383166137149464958960030030752301426973662112256567139589090597";
//        boolean result = WeIdUtils.isEcdsaKeypairMatch(privateKey, publicKey);
//        Assert.assertTrue(result);
//
//        privateKey = "646467494548664";
//        result = WeIdUtils.isEcdsaKeypairMatch(privateKey, publicKey);
//        Assert.assertFalse(result);
//    }

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
