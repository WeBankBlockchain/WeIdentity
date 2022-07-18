

package com.webank.weid.util;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.common.PasswordKey;
import com.webank.weid.full.TestBaseUtil;

/**
 * the role of this class is to handle the private key required in the testing process.
 *
 * @author v_wbgyang
 */
public class TestBuildPrivateKey {

    private static final Logger logger = LoggerFactory.getLogger(TestBuildPrivateKey.class);

    /**
     * extract the private key in org1.txt and transform it into 16 binary system
     */
    @Ignore
    public void testBuildPrivateKey() {
        PasswordKey passwordKey = TestBaseUtil.resolvePk("org1.txt");
        BigInteger bigInter1 = new BigInteger(passwordKey.getPrivateKey(), 10);
        String str1 = bigInter1.toString(16);
        logger.info("private key:{}", str1);
        Assert.assertNotNull(str1);
    }
}
