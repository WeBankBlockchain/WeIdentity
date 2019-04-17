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

import java.math.BigInteger;

import com.lambdaworks.codec.Base64;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Sign;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test SignatureUtils.
 *
 * @author v_wbjnzhang
 */
public class TestSignatureUtils {

    private static final Logger logger = LoggerFactory.getLogger(TestSignatureUtils.class);

    @Test
    public void testSignatureUtils()
        throws Exception {

        ECKeyPair keyPair = DataToolUtils.createKeyPair();
        String str = "hello world...........................yes";
        Sign.SignatureData sigData = DataToolUtils.signMessage(str, keyPair);
        BigInteger publicKey = DataToolUtils.signatureToPublicKey(str, sigData);
        logger.info("publicKey:{} ", publicKey);

        String privateKey =
            "58317564669857453586637110679746575832914889677346283755719850144028639639651";
        Sign.SignatureData sigData2 = DataToolUtils.signMessage(str, privateKey);
        publicKey = DataToolUtils.signatureToPublicKey(str, sigData2);
        logger.info("publicKey:{} ", publicKey);

        boolean result = DataToolUtils.verifySignature(str, sigData2, publicKey);
        Assert.assertTrue(result);

        publicKey = DataToolUtils.publicKeyFromPrivate(new BigInteger(privateKey));
        logger.info("publicKey:{} ", publicKey);

        keyPair = DataToolUtils.createKeyPairFromPrivate(new BigInteger(privateKey));
        logger.info("publicKey:{} ", keyPair.getPublicKey());
        logger.info("privateKey:{}", keyPair.getPrivateKey());

        byte[] serialized = DataToolUtils.simpleSignatureSerialization(sigData);
        Sign.SignatureData newSigData = DataToolUtils.simpleSignatureDeserialization(serialized);
        logger.info(newSigData.toString());

        Sign.SignatureData signatureData = DataToolUtils
            .convertBase64StringToSignatureData(new String(Base64.encode(serialized)));
        logger.info(signatureData.toString());
    }
}
