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

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.webank.weid.common.BeanUtil;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.CptMapArgs;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.AuthorityIssuerService;
import com.webank.weid.rpc.CptService;
import com.webank.weid.rpc.CredentialService;
import com.webank.weid.rpc.WeIdService;

@Component
public class DemoService {

    @Autowired
    private AuthorityIssuerService authorityIssuerService;

    @Autowired
    private CptService cptService;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private WeIdService weIdService;

    /**
     * create WeIdentity DID.
     */
    public CreateWeIdDataResult createWeId() throws RuntimeException {

        // create WeIdentity DID,publicKey,privateKey
        ResponseData<CreateWeIdDataResult> responseCreate = weIdService.createWeId();
        // check result is success
        if (responseCreate.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            throw new RuntimeException(responseCreate.getErrorMessage());
        }
        return responseCreate.getResult();
    }

    /**
     * setPublicKey.
     */
    public void setPublicKey(CreateWeIdDataResult createResult, String keyType)
        throws RuntimeException {

        // setPublicKey for this WeId
        SetPublicKeyArgs setPublicKeyArgs = new SetPublicKeyArgs();
        setPublicKeyArgs.setWeId(createResult.getWeId());
        setPublicKeyArgs.setPublicKey(createResult.getUserWeIdPublicKey().getPublicKey());
        setPublicKeyArgs.setType(keyType);
        setPublicKeyArgs.setUserWeIdPrivateKey(createResult.getUserWeIdPrivateKey());
        ResponseData<Boolean> responseSetPub = weIdService.setPublicKey(setPublicKeyArgs);
        // check is success
        if (responseSetPub.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || !responseSetPub.getResult()) {
            throw new RuntimeException(responseSetPub.getErrorMessage());
        }
    }

    /**
     * setService.
     */
    public void setService(
        CreateWeIdDataResult createResult,
        String serviceType,
        String serviceEnpoint)
        throws RuntimeException {

        // setService for this WeIdentity DID
        SetServiceArgs setServiceArgs = new SetServiceArgs();
        setServiceArgs.setWeId(createResult.getWeId());
        setServiceArgs.setType(serviceType);
        setServiceArgs.setServiceEndpoint(serviceEnpoint);
        setServiceArgs.setUserWeIdPrivateKey(createResult.getUserWeIdPrivateKey());
        ResponseData<Boolean> responseSetSer = weIdService.setService(setServiceArgs);
        // check is success
        if (responseSetSer.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || !responseSetSer.getResult()) {
            throw new RuntimeException(responseSetSer.getErrorMessage());
        }
    }

    /**
     * setAuthenticate.
     */
    public void setAuthenticate(CreateWeIdDataResult createResult, String authType)
        throws RuntimeException {

        // setAuthenticate for this WeIdentity DID
        SetAuthenticationArgs setAuthenticationArgs = new SetAuthenticationArgs();
        setAuthenticationArgs.setWeId(createResult.getWeId());
        setAuthenticationArgs.setType(authType);
        setAuthenticationArgs.setPublicKey(createResult.getUserWeIdPublicKey().getPublicKey());
        setAuthenticationArgs.setUserWeIdPrivateKey(createResult.getUserWeIdPrivateKey());
        ResponseData<Boolean> responseSetAuth =
            weIdService.setAuthentication(setAuthenticationArgs);
        // check is success
        if (responseSetAuth.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || !responseSetAuth.getResult()) {
            throw new RuntimeException(responseSetAuth.getErrorMessage());
        }
    }

    /**
     * getWeIdDom.
     */
    public WeIdDocument getWeIdDom(String weId) throws RuntimeException {

        // get weIdDom
        ResponseData<WeIdDocument> responseResult = weIdService.getWeIdDocument(weId);
        // check result
        if (responseResult.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || responseResult.getResult() == null) {
            throw new RuntimeException(responseResult.getErrorMessage());
        }
        return responseResult.getResult();
    }

    /**
     * regist cpt.
     */
    public CptBaseInfo registCpt(CreateWeIdDataResult weIdResult, Map<String, Object> cptJsonSchema)
        throws RuntimeException {

        CptMapArgs registerCptArgs = new CptMapArgs();
        WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
        weIdPrivateKey.setPrivateKey(weIdResult.getUserWeIdPrivateKey().getPrivateKey());
        registerCptArgs.getWeIdAuthentication().setWeIdPrivateKey(weIdPrivateKey);

        registerCptArgs.setWeIdAuthentication(new WeIdAuthentication());
        registerCptArgs.getWeIdAuthentication().setWeId(weIdResult.getWeId());
        registerCptArgs.setCptJsonSchema(cptJsonSchema);
        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        // check result
        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || response.getResult() == null) {
            throw new RuntimeException(response.getErrorMessage());
        }
        return response.getResult();
    }

    /**
     * regist authority issuer.
     */
    public void registerAuthorityIssuer(
        CreateWeIdDataResult weIdResult,
        String name,
        String accValue)
        throws RuntimeException {

        AuthorityIssuer authorityIssuerResult = new AuthorityIssuer();
        authorityIssuerResult.setWeId(weIdResult.getWeId());
        authorityIssuerResult.setName(name);
        authorityIssuerResult.setCreated(new Date().getTime());
        authorityIssuerResult.setAccValue(accValue);

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs = new RegisterAuthorityIssuerArgs();
        registerAuthorityIssuerArgs.setAuthorityIssuer(authorityIssuerResult);
        registerAuthorityIssuerArgs.setWeIdPrivateKey(new WeIdPrivateKey());
        registerAuthorityIssuerArgs.getWeIdPrivateKey().setPrivateKey(DemoBase.PRIVKEY);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        BeanUtil.print(response);

        // check is success
        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode() || !response.getResult()) {
            throw new RuntimeException(response.getErrorMessage());
        }
    }

    /**
     * create Credential.
     */
    public Credential createCredential(
        CreateWeIdDataResult weIdResult,
        Integer cptId,
        Map<String, Object> claim,
        long expirationDate)
        throws RuntimeException {

        CreateCredentialArgs args = new CreateCredentialArgs();
        args.setClaim(claim);
        args.setCptId(cptId);
        args.setExpirationDate(expirationDate);
        args.setIssuer(weIdResult.getWeId());
        WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
        weIdPrivateKey.setPrivateKey(weIdResult.getUserWeIdPrivateKey().getPrivateKey());
        args.setWeIdPrivateKey(weIdPrivateKey);
        ResponseData<Credential> response = credentialService.createCredential(args);
        // check result
        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode()
            || response.getResult() == null) {
            throw new RuntimeException(response.getErrorMessage());
        }
        return response.getResult();
    }

    /**
     * verifyCredential.
     */
    public boolean verifyCredential(Credential credential) throws RuntimeException {
        ResponseData<Boolean> response = credentialService.verifyCredential(credential);
        // check is success
        if (response.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            throw new RuntimeException(response.getErrorMessage());
        }
        return response.getResult();
    }
}
