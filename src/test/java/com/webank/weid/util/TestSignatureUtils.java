

package com.webank.weid.util;

import com.webank.weid.protocol.response.RsvSignature;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

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
            "58317564669857453586637110679746175832914889677346286855728850144028639639411";
        //CryptoKeyPair keyPair = com.webank.weid.blockchain.service.fisco.CryptoFisco.cryptoSuite.getKeyPairFactory().createKeyPair(new BigInteger(privateKey));
        //logger.info("publicKey:{} ", keyPair.getHexPublicKey());
        //BigInteger publicKey = DataToolUtils.publicKeyFromPrivate(new BigInteger(privateKey));
        String publicKey = DataToolUtils.publicKeyStrFromPrivate(new BigInteger(privateKey, 10));
        String address = DataToolUtils.addressFromPublic(new BigInteger(publicKey, 10));
        logger.info("publicKey:{} ", publicKey);

        /*ECKeyPair keyPair = TestBaseUtil.createKeyPair();
        keyPair = ECKeyPair.create(new BigInteger(privateKey));
        logger.info("publicKey:{} ", keyPair.getPublicKey());
        logger.info("privateKey:{}", keyPair.getPrivateKey());*/
        //CryptoKeyPair keyPair = DataToolUtils.createKeyPairFromPrivate(new BigInteger(privateKey));
        //logger.info("privateKey:{}", keyPair.getHexPrivateKey());

        String str = "hello world...........................yes";
        String encodedTransaction = "+QO0hAi2YVSFF0h25/+FF0h25/+Cr7KUBpkSD2WtUaFvOjCsTBmdGldzoWuAuQOEY171pQAAAAAAAAAAAAAAACjm35JDYfxrsw1HaGOliu0ub1toAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA0xNjk1MzcxNzQ0NTA0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABcWRpZDp3ZWlkOjEwMToweDI4ZTZkZjkyNDM2MWZjNmJiMzBkNDc2ODYzYTU4YWVkMmU2ZjViNjgja2V5cy0zYTg2MDMzZCxFZDI1NTE5VmVyaWZpY2F0aW9uS2V5MjAyMCxkaWQ6d2VpZDoxMDE6MHgyOGU2ZGY5MjQzNjFmYzZiYjMwZDQ3Njg2M2E1OGFlZDJlNmY1YjY4LHo1cnJmM3VQQ2d3ek1CWjRqMnpuMWhZVzF2QWc2OFdUVTZTZFpFQnl3aUdGTnpUZWVZNHR4cFRZTDJHQnJRWmlIdVRjcXg5RjJ5dzlKUndKSjF0NVI2YzY1dG5KTDh2ZUN5VDg1cms2dXlrRGM2aDk0ZVZOSnh3QUg1NlI0OHRoRDd5RlR5VUVTR2RNenVId3oydThHdWRIWXVlZkJzdG1MRktFSjQ2V0tEdnZ4QjlDRlg0ZkZWaG85ZDFYRW9ranpaeVRSRTFFMktiOFg1NkZrU3NibjZCUgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAemRpZDp3ZWlkOjEwMToweDI4ZTZkZjkyNDM2MWZjNmJiMzBkNDc2ODYzYTU4YWVkMmU2ZjViNjgjMDRiOGQ4YWUsV2VJZGVudGl0eSxodHRwczovL2dpdGh1Yi5jb20vV2VCYW5rQmxvY2tjaGFpbi9XZUlkZW50aXR5AAAAAAAAAQGA";
        byte[] decodeTransaction = DataToolUtils.base64Decode(encodedTransaction.getBytes());
        String decodeTransactionStr = new String(decodeTransaction);
        //address = DataToolUtils.addressFromPublic(new BigInteger(decodeTransaction));
        //Sign.SignatureData sigData = DataToolUtils.secp256k1SignToSignature(str, keyPair);
        //ECDSASignatureResult sigData = DataToolUtils.secp256k1SignToSignature(str, keyPair);
        RsvSignature signatureResult = DataToolUtils.signToRsvSignature(decodeTransactionStr, privateKey);
        //byte[] serialized = DataToolUtils.simpleSignatureSerialization(sigData);
        //Sign.SignatureData newSigData = DataToolUtils.simpleSignatureDeserialization(serialized);
        //ECDSASignatureResult newSigData = DataToolUtils.simpleSignatureDeserialization(serialized);
        String result = DataToolUtils.SigBase64Serialization(signatureResult);
        logger.info(result);

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
