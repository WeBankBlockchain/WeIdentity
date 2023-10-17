

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
            "58317564669857453586637110679736175832914889667346286865928840144018638639411";
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
        String encodedTransaction = "+QO0hAi2YVuFF0h25/+FF0h25/+Cr7qUBpkSD2WtUaFvOjCsTBmdGldzoWuAuQOEY171pQAAAAAAAAAAAAAAAFsCVdizVm+rfGFI2R29uQi6Z7wQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA0xNjk2MDc0NDI0NjQ1AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABcmRpZDp3ZWlkOjEwMToweDViMDI1NWQ4YjM1NjZmYWI3YzYxNDhkOTFkYmRiOTA4YmE2N2JjMTAja2V5cy0xZjBlYzc1NyxFZDI1NTE5VmVyaWZpY2F0aW9uS2V5MjAyMCxkaWQ6d2VpZDoxMDE6MHg1YjAyNTVkOGIzNTY2ZmFiN2M2MTQ4ZDkxZGJkYjkwOGJhNjdiYzEwLHpOVDRBd1N4c3pDWldZOVJLakVqcFE0dFdMektEd2NDWndkRG9IaHNwSlZjRXNBenVLaHBkekhpWnNBYkd3YjNaaXFBSk5peVRVaVMyakExRlZnRU5Zb0xRalNkWDVQOG55Y2FyWHc2OWRRd2NxazNDN0V4NlFjOW8zOG5zWnZIbm1kc3J0WXdKN1FlM2p0TkRFUVZ4TndaSjN1cHhmR3E5eXBlZXU4ZDJoZm5VcXV3R3lUUmhMaEpmNTdQcU5XU2czMWRwYVBxNjVqeE1xdmVzTXpENVVobmgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAemRpZDp3ZWlkOjEwMToweDViMDI1NWQ4YjM1NjZmYWI3YzYxNDhkOTFkYmRiOTA4YmE2N2JjMTAjMDRiOGQ4YWUsV2VJZGVudGl0eSxodHRwczovL2dpdGh1Yi5jb20vV2VCYW5rQmxvY2tjaGFpbi9XZUlkZW50aXR5AAAAAAAAAQGA";
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
