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

package com.webank.weid.demo;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.bcos.contract.tools.ToolConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.CptMapArgs;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.AuthorityIssuerService;
import com.webank.weid.rpc.CptService;
import com.webank.weid.rpc.CredentialService;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.util.JsonUtil;

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
    
    /**
     * log4j.
     */
    private static final Logger logger = LoggerFactory.getLogger(DemoTest1.class);
    
    /**
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
    public static void main(String[] args) {

        BeanUtil.print("init context...");
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
            "classpath:SpringApplicationContext-test.xml", "classpath:applicationContext.xml"});

        // The purpose of this step is to get relevant service objects from the spring container.
        WeIdService weIdService = context.getBean(WeIdService.class);
        
        // Step one: create weId and set the public key and authenticator status.
        BeanUtil.print("begin create weId...");

        // 1. Create weId on the chain
        ResponseData<CreateWeIdDataResult> createWeIdResult = weIdService.createWeId();
        if (createWeIdResult.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()
            || createWeIdResult.getResult() == null) {
            logger.info(createWeIdResult.getErrorMessage());
            return;
        }

        String weId = createWeIdResult.getResult().getWeId();
        String privateKey = createWeIdResult.getResult().getUserWeIdPrivateKey().getPrivateKey();
        String publicKey = createWeIdResult.getResult().getUserWeIdPublicKey().getPublicKey();
        BeanUtil.print("----------createWeIdResult--------------");
        BeanUtil.print("weId:" + weId);
        BeanUtil.print("privateKey:" + privateKey);
        BeanUtil.print("publicKey:" + publicKey);
        BeanUtil.print("------------------------");

        // 2. Set the public key on the chain
        BeanUtil.print("begin set publicKey...");
        SetPublicKeyArgs setPublicKeyArgs = buildSetPublicKeyArgs(weId, privateKey, publicKey);

        ResponseData<Boolean> setPubResult = weIdService.setPublicKey(setPublicKeyArgs);
        if (setPubResult.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()
            || !setPubResult.getResult()) {
            logger.info(setPubResult.getErrorMessage());
            return;
        }

        // 3. Set the Authenticator on chain.
        BeanUtil.print("begin set authentication...");
        SetAuthenticationArgs setAuthenticationArgs =
            buildSetAuthenticationArgs(weId, privateKey, publicKey);

        ResponseData<Boolean> setAuthResult = weIdService.setAuthentication(setAuthenticationArgs);
        if (setAuthResult.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()
            || !setAuthResult.getResult()) {
            logger.info(setAuthResult.getErrorMessage());
            return;
        }

        // The second step: register as an authority, this step is not necessary.
        BeanUtil.print("begin regist authority issuer...");
        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            buildRegisterAuthorityIssuerArgs(context, weId);

        AuthorityIssuerService authorityIssuerService =
            context.getBean(AuthorityIssuerService.class);
        ResponseData<Boolean> registAuthResult =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        if (registAuthResult.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()
            || !registAuthResult.getResult()) {
            logger.info(registAuthResult.getErrorMessage());
            return;
        }

        // The third step: register CPT template
        BeanUtil.print("begin regist cpt...");
        CptMapArgs registerCptArgs = buildRegisterCptArgs(weId, privateKey);

        CptService cptService = context.getBean(CptService.class);
        ResponseData<CptBaseInfo> cptBaseResult = cptService.registerCpt(registerCptArgs);
        if (cptBaseResult.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()
            || cptBaseResult.getResult() == null) {
            logger.info(cptBaseResult.getErrorMessage());
            return;
        }

        // The fourth step: create credential information.
        BeanUtil.print("begin create credential...");
        CreateCredentialArgs createCredentialArgs =
            buildCreateCredentialArgs(weId, privateKey, cptBaseResult);

        CredentialService credentialService = context.getBean(CredentialService.class);
        ResponseData<Credential> credentialResult =
            credentialService.createCredential(createCredentialArgs);
        if (credentialResult.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()
            || credentialResult.getResult() == null) {
            logger.info(credentialResult.getErrorMessage());
            return;
        }
        Credential credential = credentialResult.getResult();

        // The fifth step: verify the voucher.
        BeanUtil.print("begin verify credential...");
        ResponseData<Boolean> verifyResult = credentialService.verifyCredential(credential);
        if (verifyResult.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            logger.info(verifyResult.getErrorMessage());
            return;
        }

        if (verifyResult.getResult()) {
            BeanUtil.print("credential verify success");
        } else {
            BeanUtil.print("credential verify fail");
        }
    }

    private static SetPublicKeyArgs buildSetPublicKeyArgs(
        String weId, 
        String privateKey,
        String publicKey) {
        
        SetPublicKeyArgs setPublicKeyArgs = new SetPublicKeyArgs();
        setPublicKeyArgs.setWeId(weId);
        setPublicKeyArgs.setPublicKey(publicKey);

        setPublicKeyArgs.setUserWeIdPrivateKey(new WeIdPrivateKey());
        setPublicKeyArgs.getUserWeIdPrivateKey().setPrivateKey(privateKey);
        return setPublicKeyArgs;
    }

    private static SetAuthenticationArgs buildSetAuthenticationArgs(
        String weId, 
        String privateKey,
        String publicKey) {
        
        SetAuthenticationArgs setAuthenticationArgs = new SetAuthenticationArgs();
        setAuthenticationArgs.setWeId(weId);
        setAuthenticationArgs.setPublicKey(publicKey);
        setAuthenticationArgs.setType("RsaSignatureAuthentication2018");

        setAuthenticationArgs.setUserWeIdPrivateKey(new WeIdPrivateKey());
        setAuthenticationArgs.getUserWeIdPrivateKey().setPrivateKey(privateKey);
        return setAuthenticationArgs;
    }

    private static RegisterAuthorityIssuerArgs buildRegisterAuthorityIssuerArgs(
        ApplicationContext context, 
        String weId) {
        
        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs = 
            new RegisterAuthorityIssuerArgs();
        registerAuthorityIssuerArgs.setAuthorityIssuer(new AuthorityIssuer());
        registerAuthorityIssuerArgs.getAuthorityIssuer().setWeId(weId);
        registerAuthorityIssuerArgs.getAuthorityIssuer().setName("webank");
        registerAuthorityIssuerArgs.getAuthorityIssuer().setAccValue("0");

        ToolConf toolConf = context.getBean(ToolConf.class);
        String sdkPrivKey = new BigInteger(toolConf.getPrivKey(), 16).toString();
        registerAuthorityIssuerArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        registerAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey(sdkPrivKey);
        return registerAuthorityIssuerArgs;
    }

    private static CreateCredentialArgs buildCreateCredentialArgs(
        String weId, 
        String privateKey,
        ResponseData<CptBaseInfo> cptBaseResult) {
        
        CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
        createCredentialArgs.setClaim(
            (Map<String, Object>) JsonUtil.jsonStrToObj(
                new HashMap<String, Object>(),
                DemoTest.SCHEMADATA)
        ); // Set data required for template
        createCredentialArgs.setCptId(cptBaseResult.getResult().getCptId()); // Set cptId
        createCredentialArgs.setIssuer(weId); // Set Creator of voucher
        // Set expiration date
        createCredentialArgs.setExpirationDate(System.currentTimeMillis() + 1000000);

        createCredentialArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        createCredentialArgs.getWeIdPrivateKey().setPrivateKey(privateKey);
        return createCredentialArgs;
    }

    private static CptMapArgs buildRegisterCptArgs(
        String weId, 
        String privateKey) {

        CptMapArgs registerCptArgs = new CptMapArgs();
        registerCptArgs.setCptJsonSchema(
            (Map<String, Object>) JsonUtil.jsonStrToObj(
                new HashMap<String, Object>(),
                DemoTest.SCHEMA)
        ); // Set up a template

        registerCptArgs.setWeIdAuthentication(new WeIdAuthentication());
        registerCptArgs.getWeIdAuthentication().setWeId(weId); // Set template publisher
        registerCptArgs.getWeIdAuthentication().setWeIdPrivateKey(new WeIdPrivateKey());
        registerCptArgs.getWeIdAuthentication().getWeIdPrivateKey().setPrivateKey(privateKey);
        return registerCptArgs;
    }
}
