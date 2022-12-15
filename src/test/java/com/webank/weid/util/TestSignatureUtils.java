

package com.webank.weid.util;

import com.webank.weid.protocol.response.RsvSignature;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * Test SignatureUtils.
 *
 * @author v_wbjnzhang and chaoxinhu
 */
public class TestSignatureUtils {

    private static final Logger logger = LoggerFactory.getLogger(TestSignatureUtils.class);


    @Test
    public void testSignatureUtils()
        throws Exception {

        String privateKey =
            "58317564669857453586637110679746575832914889677346283755719850144028639639651";
        //CryptoKeyPair keyPair = com.webank.weid.blockchain.service.fisco.CryptoFisco.cryptoSuite.getKeyPairFactory().createKeyPair(new BigInteger(privateKey));
        //logger.info("publicKey:{} ", keyPair.getHexPublicKey());
        //BigInteger publicKey = DataToolUtils.publicKeyFromPrivate(new BigInteger(privateKey));
        String publicKey = DataToolUtils.publicKeyStrFromPrivate(new BigInteger(privateKey, 10));
        logger.info("publicKey:{} ", publicKey);

        /*ECKeyPair keyPair = TestBaseUtil.createKeyPair();
        keyPair = ECKeyPair.create(new BigInteger(privateKey));
        logger.info("publicKey:{} ", keyPair.getPublicKey());
        logger.info("privateKey:{}", keyPair.getPrivateKey());*/
        //CryptoKeyPair keyPair = DataToolUtils.createKeyPairFromPrivate(new BigInteger(privateKey));
        //logger.info("privateKey:{}", keyPair.getHexPrivateKey());

        String str = "hello world...........................yes";
        //Sign.SignatureData sigData = DataToolUtils.secp256k1SignToSignature(str, keyPair);
        //ECDSASignatureResult sigData = DataToolUtils.secp256k1SignToSignature(str, keyPair);
        RsvSignature signatureResult = DataToolUtils.signToRsvSignature(str, privateKey);
        //byte[] serialized = DataToolUtils.simpleSignatureSerialization(sigData);
        //Sign.SignatureData newSigData = DataToolUtils.simpleSignatureDeserialization(serialized);
        //ECDSASignatureResult newSigData = DataToolUtils.simpleSignatureDeserialization(serialized);
        logger.info(DataToolUtils.SigBase64Serialization(signatureResult));

        /*Sign.SignatureData signatureData = DataToolUtils
            .convertBase64StringToSignatureData(new String(Base64.encode(serialized)));*/
        /*ECDSASignatureResult signatureData = DataToolUtils
                .convertBase64StringToSignatureData(new String(Base64.encodeBase64(serialized)));*/
        //logger.info(signatureData.toString());
    }

    @Test
    public void testSecp256k1Signatures() {
        String privKey =
            "58317564669857453586637110679746575832914889677346283755719850144028639639651";
        String msg = "12345";
        String publicKey = DataToolUtils.publicKeyStrFromPrivate(new BigInteger(privKey, 10));
        /*SignatureResult signatureResult = com.webank.weid.blockchain.service.fisco.CryptoFisco.cryptoSuite.sign(com.webank.weid.blockchain.service.fisco.CryptoFisco.cryptoSuite.hash(msg),
                com.webank.weid.blockchain.service.fisco.CryptoFisco.cryptoSuite.getKeyPairFactory().createKeyPair(new BigInteger(privKey)));
                //注意cryptoSuite.verify只能接收hex形式的publicKey
        Boolean a = com.webank.weid.blockchain.service.fisco.CryptoFisco.cryptoSuite.verify((new BigInteger(publicKey, 10)).toString(16), com.webank.weid.blockchain.service.fisco.CryptoFisco.cryptoSuite.hash(msg), signatureResult.convertToString());*/

        String sig = DataToolUtils.SigBase64Serialization(DataToolUtils.signToRsvSignature(msg, privKey));
        BigInteger bigPublicKey = new BigInteger(publicKey, 10);
        boolean result = DataToolUtils.verifySignature(msg, sig, bigPublicKey);
        Assert.assertTrue(result);
    }
}
