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

import java.math.BigInteger;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Sign;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
/**
 * Test SignatureUtils.
 * 
 * @author v_wbjnzhang
 *
 */
public class TestSignatureUtils {

    @Test
    public void testSignatureUtils() throws Exception {

        ECKeyPair keyPair = SignatureUtils.createKeyPair();
        // public key
        // 6005884739482598907019672016029935954035758996027051146272921018865015941269698926222431345309233458526942087465818124661687956402067203118790805113144306
        // private key
        // 11695290896330592173013668505941497555094145434653626165899956696676058923570
        // serialize key
        String str = "hello world...........................yes";
        Sign.SignatureData sigData = SignatureUtils.signMessage(str, keyPair);
        BigInteger publicKey = SignatureUtils.signatureToPublicKey(str, sigData);
        System.out.println("publicKey " + publicKey);

        String privateKey =
            "58317564669857453586637110679746575832914889677346283755719850144028639639651";
        Sign.SignatureData sigData2 = SignatureUtils.signMessage(str, privateKey);
        publicKey = SignatureUtils.signatureToPublicKey(str, sigData2);
        System.out.println("publicKey " + publicKey);

        boolean result = SignatureUtils.verifySignature(str, sigData2, publicKey);
        assertTrue(result);

        publicKey = SignatureUtils.publicKeyFromPrivate(new BigInteger(privateKey));
        System.out.println("publicKey " + publicKey);

        keyPair = SignatureUtils.createKeyPairFromPrivate(new BigInteger(privateKey));

        byte[] serialized = SignatureUtils.simpleSignatureSerialization(sigData);
        Sign.SignatureData newSigData = SignatureUtils.simpleSignatureDeserialization(serialized);
        System.out.println(newSigData);
    }
}
