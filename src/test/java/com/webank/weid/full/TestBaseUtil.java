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

package com.webank.weid.full;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RegisterCptArgs;
import com.webank.weid.protocol.request.RemoveAuthorityIssuerArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.request.UpdateCptArgs;
import com.webank.weid.protocol.request.VerifyCredentialArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;

public class TestBaseUtil {

   /**
    * the private key of sdk is a BigInteger,which needs to be used
    * when registering authority
    * 
    */
    public static String privKey;

    /**
     * build VerifyCredentialArgs
     */
    public static VerifyCredentialArgs buildVerifyCredentialArgs(
        Credential credential, String publicKey) {
        
        VerifyCredentialArgs verifyCredentialArgs = new VerifyCredentialArgs();
        verifyCredentialArgs.setCredential(credential);
        verifyCredentialArgs.setWeIdPublicKey(new WeIdPublicKey());
        verifyCredentialArgs.getWeIdPublicKey().setPublicKey(publicKey);
        return verifyCredentialArgs;
    }

    /** build CreateCredentialArgs no cptId */
    public static CreateCredentialArgs buildCreateCredentialArgs(CreateWeIdDataResult createWeId) {

        CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
        createCredentialArgs.setIssuer(createWeId.getWeId());
        createCredentialArgs.setExpirationDate(System.currentTimeMillis() + (1000 * 60 * 60 * 24));
        createCredentialArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        createCredentialArgs
            .getWeIdPrivateKey()
            .setPrivateKey(createWeId.getUserWeIdPrivateKey().getPrivateKey());
        createCredentialArgs.setClaim(TestData.schemaData);

        return createCredentialArgs;
    }

    /** build default CreateCredentialArgs */
    public static CreateCredentialArgs buildCreateCredentialArgs(
        CreateWeIdDataResult createWeId, CptBaseInfo cptBaseInfo) {

        CreateCredentialArgs createCredentialArgs = buildCreateCredentialArgs(createWeId);
        createCredentialArgs.setCptId(cptBaseInfo.getCptId());
        return createCredentialArgs;
    }

    /** build default UpdateCptArgs */
    public static UpdateCptArgs buildUpdateCptArgs(
        CreateWeIdDataResult createWeId, CptBaseInfo cptBaseInfo) {

        UpdateCptArgs updateCptArgs = new UpdateCptArgs();
        updateCptArgs.setCptJsonSchema(TestData.schema);
        updateCptArgs.setCptPublisher(createWeId.getWeId());
        updateCptArgs.setCptPublisherPrivateKey(new WeIdPrivateKey());
        updateCptArgs
            .getCptPublisherPrivateKey()
            .setPrivateKey(createWeId.getUserWeIdPrivateKey().getPrivateKey());
        updateCptArgs.setCptId(cptBaseInfo.getCptId());

        return updateCptArgs;
    }

    /** build default RegisterCptArgs */
    public static RegisterCptArgs buildRegisterCptArgs(CreateWeIdDataResult createWeId) {

        RegisterCptArgs registerCptArgs = new RegisterCptArgs();
        registerCptArgs.setCptJsonSchema(TestData.schema);
        registerCptArgs.setCptPublisher(createWeId.getWeId());
        registerCptArgs.setCptPublisherPrivateKey(new WeIdPrivateKey());
        registerCptArgs
            .getCptPublisherPrivateKey()
            .setPrivateKey(createWeId.getUserWeIdPrivateKey().getPrivateKey());

        return registerCptArgs;
    }

    /** build default RegisterAuthorityIssuerArgs */
    public static RegisterAuthorityIssuerArgs buildRegisterAuthorityIssuerArgs(
        CreateWeIdDataResult createWeId) {

        AuthorityIssuer authorityIssuer = new AuthorityIssuer();
        authorityIssuer.setWeId(createWeId.getWeId());
        authorityIssuer.setCreated(new Date().getTime());
        authorityIssuer.setName(TestData.authorityIssuerName);
        authorityIssuer.setAccValue(TestData.authorityIssuerAccValue);

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs = new RegisterAuthorityIssuerArgs();
        registerAuthorityIssuerArgs.setAuthorityIssuer(authorityIssuer);
        registerAuthorityIssuerArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        registerAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey(privKey);

        return registerAuthorityIssuerArgs;
    }

    /** build default CreateWeIdArgs */
    public static CreateWeIdArgs buildCreateWeIdArgs() throws Exception {
        CreateWeIdArgs args = new CreateWeIdArgs();
        String[] pk = createEcKeyPair();
        args.setPublicKey(pk[0]);

        WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
        weIdPrivateKey.setPrivateKey(pk[1]);

        args.setWeIdPrivateKey(weIdPrivateKey);

        return args;
    }

    /** buildSetPublicKeyArgs */
    public static SetAuthenticationArgs buildSetAuthenticationArgs(CreateWeIdDataResult createWeId)
        throws Exception {

        SetAuthenticationArgs setAuthenticationArgs = new SetAuthenticationArgs();
        setAuthenticationArgs.setWeId(createWeId.getWeId());
        setAuthenticationArgs.setPublicKey(createWeId.getUserWeIdPublicKey().getPublicKey());
        setAuthenticationArgs.setType(TestData.authenticationType);
        setAuthenticationArgs.setUserWeIdPrivateKey(new WeIdPrivateKey());
        setAuthenticationArgs
            .getUserWeIdPrivateKey()
            .setPrivateKey(createWeId.getUserWeIdPrivateKey().getPrivateKey());

        return setAuthenticationArgs;
    }

    /** buildSetPublicKeyArgs */
    public static SetPublicKeyArgs buildSetPublicKeyArgs(CreateWeIdDataResult createWeId)
        throws Exception {

        SetPublicKeyArgs setPublicKeyArgs = new SetPublicKeyArgs();
        setPublicKeyArgs.setWeId(createWeId.getWeId());
        setPublicKeyArgs.setPublicKey(createWeId.getUserWeIdPublicKey().getPublicKey());
        setPublicKeyArgs.setType(TestData.publicKeyType);
        setPublicKeyArgs.setUserWeIdPrivateKey(new WeIdPrivateKey());
        setPublicKeyArgs
            .getUserWeIdPrivateKey()
            .setPrivateKey(createWeId.getUserWeIdPrivateKey().getPrivateKey());

        return setPublicKeyArgs;
    }

    /** buildSetPublicKeyArgs */
    public static SetServiceArgs buildSetServiceArgs(CreateWeIdDataResult createWeId)
        throws Exception {

        SetServiceArgs setServiceArgs = new SetServiceArgs();
        setServiceArgs.setWeId(createWeId.getWeId());
        setServiceArgs.setType(TestData.serviceType);
        setServiceArgs.setServiceEndpoint(TestData.serviceEndpoint);
        setServiceArgs.setUserWeIdPrivateKey(new WeIdPrivateKey());
        setServiceArgs
            .getUserWeIdPrivateKey()
            .setPrivateKey(createWeId.getUserWeIdPrivateKey().getPrivateKey());

        return setServiceArgs;
    }

    /** buildRemoveAuthorityIssuerArgs */
    public static RemoveAuthorityIssuerArgs buildRemoveAuthorityIssuerArgs(
        CreateWeIdDataResult createWeId) {

        RemoveAuthorityIssuerArgs removeAuthorityIssuerArgs = new RemoveAuthorityIssuerArgs();
        removeAuthorityIssuerArgs.setWeId(createWeId.getWeId());
        removeAuthorityIssuerArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        removeAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey(privKey);

        return removeAuthorityIssuerArgs;
    }

    /** create a new public key - private key */
    public static String[] createEcKeyPair() throws Exception {
        ECKeyPair keyPair = Keys.createEcKeyPair();
        String publicKey = String.valueOf(keyPair.getPublicKey());
        String privateKey = String.valueOf(keyPair.getPrivateKey());
        String[] pk = new String[]{publicKey, privateKey};
        System.out.println();
        BeanUtil.print(pk);
        return pk;
    }

    /**
     *  to test the public and private key from the file
     *  
     * @param fileName
     * @return
     */
    public static String[] resolvePk(String fileName) {
        BufferedReader br = null;
        try {

            String filePath = TestBaseUtil.class.getClassLoader().getResource(fileName).getFile();
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String publicKey = br.readLine().split(":")[1]; // read publicKey
            String privateKey = br.readLine().split(":")[1]; // read privateKey
            System.out.println("publicKey:" + publicKey);
            System.out.println("privateKey:" + privateKey);
            return new String[]{publicKey, privateKey};
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
