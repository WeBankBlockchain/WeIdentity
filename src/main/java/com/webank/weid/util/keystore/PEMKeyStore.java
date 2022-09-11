/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.weid.util.keystore;

import com.webank.weid.exception.LoadKeyStoreException;
import com.webank.weid.exception.SaveKeyStoreException;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class PEMKeyStore extends KeyTool {
    public static final String PRIVATE_KEY = "PRIVATE KEY";
    private PemObject pem;

    public PEMKeyStore(final String keyStoreFile) {
        super(keyStoreFile);
    }

    public PEMKeyStore(InputStream keyStoreFileInputStream) {
        super(keyStoreFileInputStream);
    }

    @Override
    protected PublicKey getPublicKey() {
        try {
            X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(pem.getContent());
            KeyFactory keyFactory =
                    KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            return keyFactory.generatePublic(encodedKeySpec);
        } catch (InvalidKeySpecException | NoSuchProviderException | NoSuchAlgorithmException e) {
            throw new LoadKeyStoreException(
                    "getPublicKey from pem file "
                            + keyStoreFile
                            + " failed, error message: "
                            + e.getMessage(),
                    e);
        }
    }

    public static void storeKeyPairWithPemFormat(
            String hexedPrivateKey, String privateKeyFilePath, String curveName)
            throws SaveKeyStoreException {
        try {
            hexedPrivateKey = hexedPrivateKey.startsWith("0x") ? hexedPrivateKey.substring(2) : hexedPrivateKey;
            KeyPair keyPair = convertHexedStringToKeyPair(hexedPrivateKey, curveName);
            // save the private key
            PemWriter writer = new PemWriter(new FileWriter(privateKeyFilePath));
            BCECPrivateKey bcecPrivateKey = (BCECPrivateKey) (keyPair.getPrivate());
            writer.writeObject(new PemObject(PRIVATE_KEY, bcecPrivateKey.getEncoded()));
            writer.flush();
            writer.close();
            // write the public key
            storePublicKeyWithPem(keyPair.getPublic(), privateKeyFilePath);
        } catch (IOException | LoadKeyStoreException e) {
            throw new SaveKeyStoreException(
                    "save keys into "
                            + privateKeyFilePath
                            + " failed, error information: "
                            + e.getMessage(),
                    e);
        }
    }

    @Override
    protected void load(InputStream in) {
        try {
            PemReader pemReader = new PemReader(new InputStreamReader(in));
            pem = pemReader.readPemObject();
            pemReader.close();
        } catch (IOException e) {
            String errorMessage =
                    "load key info from the pem file "
                            + keyStoreFile
                            + " failed, error message:"
                            + e.getMessage();
            logger.error(errorMessage);
            throw new LoadKeyStoreException(errorMessage, e);
        }
        if (pem == null) {
            logger.error("The file " + keyStoreFile + " does not represent a pem account.");
            throw new LoadKeyStoreException("The file does not represent a pem account.");
        }
    }

    @Override
    protected PrivateKey getPrivateKey() {
        try {
            PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(pem.getContent());
            KeyFactory keyFacotry =
                    KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            return keyFacotry.generatePrivate(encodedKeySpec);
        } catch (InvalidKeySpecException | NoSuchProviderException | NoSuchAlgorithmException e) {
            String errorMessage =
                    "getPrivateKey from pem file "
                            + keyStoreFile
                            + " failed, error message:"
                            + e.getMessage();
            logger.error(errorMessage);
            throw new LoadKeyStoreException(errorMessage, e);
        }
    }

}
