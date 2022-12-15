

package com.webank.weid.util;

import java.nio.charset.StandardCharsets;

import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * test HashUtils.
 *
 * @author v_wbjnzhang
 */
public class TestHashUtils {

    private static final Logger logger = LoggerFactory.getLogger(TestHashUtils.class);

    @Test
    public void testHashUtils() {

        String rawString = "hello world.";
        byte[] rawBytes = rawString.getBytes(StandardCharsets.UTF_8);
        logger.info("Befor hash, String: {}, Bytes: {} ",
            rawString,
            rawBytes,
            new String(rawBytes, StandardCharsets.UTF_8)
        );
        CryptoSuite cryptoSuite = new CryptoSuite(0);
        String hashedString = cryptoSuite.hash(rawString);
        byte[] hashedBytes = cryptoSuite.hash(rawBytes);
        // use assert here to verify the String to be 64 bit and Bytes[] to be
        // 32 bit
        logger.info(
            "After hash, String: {}, Bytes: {}",
            hashedString,
            new String(hashedBytes, StandardCharsets.UTF_8)
        );
        Assert.assertNotNull(hashedBytes);
    }
}
