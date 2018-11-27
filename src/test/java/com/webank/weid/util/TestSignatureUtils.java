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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

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
 *
 */
public class TestSignatureUtils {

    private static final Logger logger = LoggerFactory.getLogger(TestSignatureUtils.class);

    @Test
    public void testSignatureUtils()
        throws InvalidAlgorithmParameterException, 
        NoSuchAlgorithmException,
        NoSuchProviderException, 
        SignatureException,
        UnsupportedEncodingException {

        ECKeyPair keyPair = SignatureUtils.createKeyPair();
        String str = "hello world...........................yes";
        Sign.SignatureData sigData = SignatureUtils.signMessage(str, keyPair);
        BigInteger publicKey = SignatureUtils.signatureToPublicKey(str, sigData);
        logger.info("publicKey " + publicKey);

        String privateKey =
            "58317564669857453586637110679746575832914889677346283755719850144028639639651";
        Sign.SignatureData sigData2 = SignatureUtils.signMessage(str, privateKey);
        publicKey = SignatureUtils.signatureToPublicKey(str, sigData2);
        logger.info("publicKey " + publicKey);

        boolean result = SignatureUtils.verifySignature(str, sigData2, publicKey);
        Assert.assertTrue(result);

        publicKey = SignatureUtils.publicKeyFromPrivate(new BigInteger(privateKey));
        logger.info("publicKey " + publicKey);

        keyPair = SignatureUtils.createKeyPairFromPrivate(new BigInteger(privateKey));

        byte[] serialized = SignatureUtils.simpleSignatureSerialization(sigData);
        Sign.SignatureData newSigData = SignatureUtils.simpleSignatureDeserialization(serialized);
        logger.info(newSigData.toString());
    }
}
