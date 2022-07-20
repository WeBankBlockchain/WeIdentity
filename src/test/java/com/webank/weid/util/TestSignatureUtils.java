

package com.webank.weid.util;

import java.math.BigInteger;

import com.webank.weid.service.BaseService;
import org.apache.commons.codec.binary.Base64;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.ECDSASignatureResult;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.utils.Numeric;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.full.TestBaseUtil;

/**
 * Test SignatureUtils.
 *
 * @author v_wbjnzhang and chaoxinhu
 */
public class TestSignatureUtils {

    private static final Logger logger = LoggerFactory.getLogger(TestSignatureUtils.class);

    private static final Client client = ((Client)BaseService.getClient());

    @Test
    public void testSignatureUtils()
        throws Exception {

        String privateKey =
            "58317564669857453586637110679746575832914889677346283755719850144028639639651";
        CryptoKeyPair keyPair = DataToolUtils.cryptoSuite.createKeyPair(privateKey);
        //logger.info("publicKey:{} ", keyPair.getHexPublicKey());
        //BigInteger publicKey = DataToolUtils.publicKeyFromPrivate(new BigInteger(privateKey));
        String publicKey = keyPair.getHexPublicKey();
        logger.info("publicKey:{} ", publicKey);

        /*ECKeyPair keyPair = TestBaseUtil.createKeyPair();
        keyPair = ECKeyPair.create(new BigInteger(privateKey));
        logger.info("publicKey:{} ", keyPair.getPublicKey());
        logger.info("privateKey:{}", keyPair.getPrivateKey());*/
        //CryptoKeyPair keyPair = DataToolUtils.createKeyPairFromPrivate(new BigInteger(privateKey));
        logger.info("privateKey:{}", keyPair.getHexPrivateKey());

        String str = "hello world...........................yes";
        //Sign.SignatureData sigData = DataToolUtils.secp256k1SignToSignature(str, keyPair);
        //ECDSASignatureResult sigData = DataToolUtils.secp256k1SignToSignature(str, keyPair);
        SignatureResult signatureResult = DataToolUtils.signToSignature(str, privateKey);
        //byte[] serialized = DataToolUtils.simpleSignatureSerialization(sigData);
        //Sign.SignatureData newSigData = DataToolUtils.simpleSignatureDeserialization(serialized);
        //ECDSASignatureResult newSigData = DataToolUtils.simpleSignatureDeserialization(serialized);
        logger.info(signatureResult.convertToString());

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
        //ECKeyPair keyPair = DataToolUtils.createKeyPairFromPrivate(new BigInteger(hexPrivKey));
        //CryptoKeyPair keyPair = DataToolUtils.createKeyPairFromPrivate(new BigInteger(hexPrivKey));
        CryptoKeyPair keyPair = DataToolUtils.cryptoSuite.getKeyPairFactory().createKeyPair(new BigInteger(privKey));
        //String sig = DataToolUtils.secp256k1Sign(msg, new BigInteger(hexPrivKey));
        SignatureResult signatureResult = DataToolUtils.signToSignature(msg, privKey);
        String sig = signatureResult.convertToString();
        //Boolean result = DataToolUtils.verifySecp256k1Signature(msg, sig, keyPair.getPublicKey());
        BigInteger bigPublicKey =
                new BigInteger(1, Numeric.hexStringToByteArray(keyPair.getHexPublicKey()));
        Boolean result = DataToolUtils.verifySignature(msg, sig, bigPublicKey);
        Assert.assertTrue(result);
    }
}
