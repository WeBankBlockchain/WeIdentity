/*
 * Copyright© (2018) WeBank Co., Ltd.
 *
 * This file is part of weidentity-java-sdk.
 *
 * weidentity-java-sdk is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * weidentity-java-sdk is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * weidentity-java-sdk. If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.demo;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.RegisterCptArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.AuthorityIssuerService;
import com.webank.weid.rpc.CptService;
import com.webank.weid.rpc.CredentialService;
import com.webank.weid.rpc.WeIdService;
import java.math.BigInteger;
import org.bcos.contract.tools.ToolConf;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <p>
 * This demo is designed to guide users to use SDK.
 * 
 * The streamlining process is as follows:
 * 
 * 1. Create the only full network identity and set up your public key and authenticator. If you do
 * not have your own public and private keys, we provide you with a method that will automatically
 * create public and private key pairs for you. Please keep your private key and identity
 * information, because you need to use your private key account to verify your identity in
 * subsequent operations.
 * 
 * 2. If you want to become an authority, you can call the corresponding method to register the
 * authority, but this step is not necessary.
 * 
 * 3. You need to register an authentication template (CPT). We will return a template number.
 * Please keep your template number. You need to use the template number to create credentials.
 * 
 * 4.This step will create your credentials, using the ID, private key, CPT number and JSON string
 * data corresponding to the CPT template you generated earlier. We will return you a credential
 * object. You can serialize and save your credential information by yourself, such as using JSON to
 * serialize and save it.
 * 
 * 5.Any person or institution with weId status can use your credentials to verify the validity of
 * your information.
 * </p>
 * 
 * @author v_wbgyang
 *
 */
public class DemoTest1 {

    /** jsonSchema. */
    private static String schema =
            "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"title\":\"/etc/fstab\",\"description\":\"JSON representation of /etc/fstab\",\"type\":\"object\",\"properties\":{\"swap\":{\"$ref\":\"#/definitions/mntent\"}},\"patternProperties\":{\"^/([^/]+(/[^/]+)*)?$\":{\"$ref\":\"#/definitions/mntent\"}},\"required\":[\"/\",\"swap\"],\"additionalProperties\":false,\"definitions\":{\"mntent\":{\"title\":\"mntent\",\"description\":\"An fstab entry\",\"type\":\"object\",\"properties\":{\"device\":{\"type\":\"string\"},\"fstype\":{\"type\":\"string\"},\"options\":{\"type\":\"array\",\"minItems\":1,\"items\":{\"type\":\"string\"}},\"dump\":{\"type\":\"integer\",\"minimum\":0},\"fsck\":{\"type\":\"integer\",\"minimum\":0}},\"required\":[\"device\",\"fstype\"],\"additionalItems\":false}}}";

    /** claim. */
    private static String schemaData =
            "{\"/\":{\"device\":\"/dev/sda2\",\"fstype\":\"btrfs\",\"options\":[\"ssd\"]},\"swap\":{\"device\":\"/dev/sda2\",\"fstype\":\"swap\"},\"/tmp\":{\"device\":\"tmpfs\",\"fstype\":\"tmpfs\",\"options\":[\"size=64M\"]},\"/var/lib/mysql\":{\"device\":\"/dev/data/mysql\",\"fstype\":\"btrfs\"}}";

    /**
     * main for DemoTest1.
     * 
     */
    public static void main(String[] args) throws Exception {

        /*
         * Because weidentity-java-sdk is implemented based on spring framework, it is necessary to
         * use spring container to manage core objects.
         * 
         * 1. The purpose of loading SpringApplicationContext-test.xml is to let spring containers
         * manage core objects. In their own spring project, annotation-driven scanning of
         * com.webank.weid package is required.
         * 
         * 2. The purpose of loading applicationContext. XML is to obtain the contract deployment
         * private key. If you need to register an authority, you need to use the private key.
         * Otherwise, you do not have the right to register as an authority.
         * 
         */
        System.out.println("init context...");
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
            "classpath:SpringApplicationContext-test.xml", "classpath:applicationContext.xml"});

        // The purpose of this step is to get relevant service objects from the spring container.
        WeIdService weIdService = context.getBean(WeIdService.class);
        AuthorityIssuerService authorityIssuerService =
                context.getBean(AuthorityIssuerService.class);
        CptService cptService = context.getBean(CptService.class);
        CredentialService credentialService = context.getBean(CredentialService.class);
        ToolConf toolConf = context.getBean(ToolConf.class);
        String sdkPrivKey = new BigInteger(toolConf.getPrivKey(), 16).toString();

        // Step one: create weId and set the public key and authenticator status.
        System.out.println("begin create weId...");

        // 1. Create weId on the chain
        ResponseData<CreateWeIdDataResult> createWeIdResult = weIdService.createWeId();
        if (createWeIdResult.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()
                || createWeIdResult.getResult() == null) {
            throw new Exception(createWeIdResult.getErrorMessage());
        }

        String weId = createWeIdResult.getResult().getWeId();
        String privateKey = createWeIdResult.getResult().getUserWeIdPrivateKey().getPrivateKey();
        String publicKey = createWeIdResult.getResult().getUserWeIdPublicKey().getPublicKey();
        System.out.println("----------createWeIdResult--------------");
        System.out.println("weId:" + weId);
        System.out.println("privateKey:" + privateKey);
        System.out.println("publicKey:" + publicKey);
        System.out.println("------------------------");

        // 2. Set the public key on the chain
        System.out.println("begin set publicKey...");
        SetPublicKeyArgs setPublicKeyArgs = new SetPublicKeyArgs();
        setPublicKeyArgs.setWeId(weId);
        setPublicKeyArgs.setPublicKey(publicKey);

        setPublicKeyArgs.setUserWeIdPrivateKey(new WeIdPrivateKey());
        setPublicKeyArgs.getUserWeIdPrivateKey().setPrivateKey(privateKey);

        ResponseData<Boolean> setPubResult = weIdService.setPublicKey(setPublicKeyArgs);
        if (setPubResult.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()
                || !setPubResult.getResult()) {
            throw new Exception(setPubResult.getErrorMessage());
        }

        // 3. Set the Authenticator on chain.
        System.out.println("begin set authentication...");
        SetAuthenticationArgs setAuthenticationArgs = new SetAuthenticationArgs();
        setAuthenticationArgs.setWeId(weId);
        setAuthenticationArgs.setPublicKey(publicKey);
        setAuthenticationArgs.setType("RsaSignatureAuthentication2018");

        setAuthenticationArgs.setUserWeIdPrivateKey(new WeIdPrivateKey());
        setAuthenticationArgs.getUserWeIdPrivateKey().setPrivateKey(privateKey);

        ResponseData<Boolean> setAuthResult = weIdService.setAuthentication(setAuthenticationArgs);
        if (setAuthResult.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()
                || !setAuthResult.getResult()) {
            throw new Exception(setAuthResult.getErrorMessage());
        }

        // The second step: register as an authority, this step is not necessary.
        System.out.println("begin regist authority issuer...");
        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs = new RegisterAuthorityIssuerArgs();
        registerAuthorityIssuerArgs.setAuthorityIssuer(new AuthorityIssuer());
        registerAuthorityIssuerArgs.getAuthorityIssuer().setWeId(weId);
        registerAuthorityIssuerArgs.getAuthorityIssuer().setName("webank");
        registerAuthorityIssuerArgs.getAuthorityIssuer().setAccValue("0");

        registerAuthorityIssuerArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        registerAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey(sdkPrivKey);

        ResponseData<Boolean> registAuthResult =
                authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        if (registAuthResult.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()
                || !registAuthResult.getResult()) {
            throw new Exception(registAuthResult.getErrorMessage());
        }

        // The third step: register CPT template
        System.out.println("begin regist cpt...");
        RegisterCptArgs registerCptArgs = new RegisterCptArgs();
        registerCptArgs.setCptJsonSchema(schema); // Set up a template
        registerCptArgs.setCptPublisher(weId); // Set template publisher

        registerCptArgs.setCptPublisherPrivateKey(new WeIdPrivateKey());
        registerCptArgs.getCptPublisherPrivateKey().setPrivateKey(privateKey);

        ResponseData<CptBaseInfo> cptBaseResult = cptService.registerCpt(registerCptArgs);
        if (cptBaseResult.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()
                || cptBaseResult.getResult() == null) {
            throw new Exception(cptBaseResult.getErrorMessage());
        }

        // The fourth step: create credential information.
        System.out.println("begin create credential...");
        CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
        createCredentialArgs.setClaim(schemaData); // Set data required for template
        createCredentialArgs.setCptId(cptBaseResult.getResult().getCptId()); // Set cptId
        createCredentialArgs.setIssuer(weId); // Set Creator of voucher
        createCredentialArgs.setExpirationDate(System.currentTimeMillis() + 1000000);// Set
                                                                                     // expiration
                                                                                     // date

        createCredentialArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(privateKey);

        ResponseData<Credential> credentialResult =
                credentialService.createCredential(createCredentialArgs);
        if (credentialResult.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()
                || credentialResult.getResult() == null) {
            throw new Exception(credentialResult.getErrorMessage());
        }
        Credential credential = credentialResult.getResult();

        // The fifth step: verify the voucher.
        System.out.println("begin verify credential...");
        ResponseData<Boolean> verifyResult = credentialService.verifyCredential(credential);
        if (verifyResult.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            throw new Exception(verifyResult.getErrorMessage());
        }

        if (verifyResult.getResult()) {
            System.out.println("credential verify success");
        } else {
            System.out.println("credential verify fail");
        }
    }
}
