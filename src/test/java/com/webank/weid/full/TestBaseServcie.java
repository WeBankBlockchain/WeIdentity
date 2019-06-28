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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.DynamicBytes;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.BaseTest;
import com.webank.weid.common.LogUtil;
import com.webank.weid.common.PasswordKey;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.contract.v1.WeIdContract;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.Credential;
import com.webank.weid.protocol.base.CredentialWrapper;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.base.WeIdPublicKey;
import com.webank.weid.protocol.request.CptMapArgs;
import com.webank.weid.protocol.request.CreateCredentialArgs;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.request.RegisterAuthorityIssuerArgs;
import com.webank.weid.protocol.request.SetAuthenticationArgs;
import com.webank.weid.protocol.request.SetPublicKeyArgs;
import com.webank.weid.protocol.request.SetServiceArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.CredentialUtils;
import com.webank.weid.util.WeIdUtils;

/**
 * testing basic method classes.
 *
 * @author v_wbgyang
 */
public abstract class TestBaseServcie extends BaseTest implements MockMysqlDriver {

    /**
     * log4j.
     */
    private static final Logger logger = LoggerFactory.getLogger(TestBaseServcie.class);

    /**
     * private key of authority membership list.
     */
    public static volatile List<String> issuerPrivateList = null;

    /**
     * whether to initialize authority list.
     */
    protected static volatile boolean isInitIssuer = false;

    /**
     * weId information required for use cases.
     */
    protected static volatile CreateWeIdDataResult createWeIdResult = null;

    /**
     * new weId information required for use cases.
     */
    protected static volatile CreateWeIdDataResult createWeIdNew = null;

    /**
     * weId information and set related attribute.
     */
    protected static volatile CreateWeIdDataResult createWeIdResultWithSetAttr = null;

    /**
     * parameters needed to create credentials.
     */
    protected static volatile CreateCredentialArgs createCredentialArgs = null;

    /**
     * parameters needed to register CPT.
     */
    protected static volatile CptMapArgs registerCptArgs = null;

    /**
     * CPT registration information.
     */
    protected static volatile CptBaseInfo cptBaseInfo = null;

    /**
     * initializing related services.
     */
    @Override
    public synchronized void testInit() {

        if (!isInitIssuer) {
            try {
                issuerPrivateList = new ArrayList<String>();
                issuerPrivateList.add(privateKey);
                initIssuer("org1.txt");
                isInitIssuer = true;
            } catch (Exception e) {
                logger.error("initIssuer error", e);
                Assert.assertTrue(false);
            }
        }

        if (createWeIdResult == null) {
            createWeIdResult = this.createWeId();
        }
        if (createWeIdResultWithSetAttr == null) {
            createWeIdResultWithSetAttr = this.createWeIdWithSetAttr();
        }
        if (createWeIdNew == null) {
            createWeIdNew = this.createWeId();
        }
        if (createCredentialArgs == null) {
            registerCptArgs = TestBaseUtil.buildCptArgs(createWeIdResultWithSetAttr);
            createCredentialArgs =
                TestBaseUtil.buildCreateCredentialArgs(createWeIdResultWithSetAttr);
            cptBaseInfo = this.registerCpt(createWeIdResultWithSetAttr, registerCptArgs);
            createCredentialArgs.setCptId(cptBaseInfo.getCptId());
        }
    }

    /**
     * according to the analysis of the private key to create WeIdentity DID,and registered as an
     * authority, and its private key is recorded.
     *
     * @param fileName fileName
     */
    private void initIssuer(String fileName) {

        PasswordKey passwordKey = TestBaseUtil.resolvePk(fileName);
        String publicKey = passwordKey.getPublicKey();
        String privateKey = passwordKey.getPrivateKey();

        CreateWeIdArgs createWeIdArgs1 = TestBaseUtil.buildCreateWeIdArgs();
        createWeIdArgs1.setPublicKey(publicKey);
        createWeIdArgs1.getWeIdPrivateKey().setPrivateKey(privateKey);
        ResponseData<String> response1 = weIdService.createWeId(createWeIdArgs1);
        if (response1.getErrorCode().intValue() != ErrorCode.WEID_ALREADY_EXIST.getCode()
            && response1.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            Assert.assertTrue(false);
        }

        String weId = WeIdUtils.convertPublicKeyToWeId(publicKey);

        CreateWeIdDataResult createResult = new CreateWeIdDataResult();
        createResult.setWeId(weId);
        createResult.setUserWeIdPrivateKey(new WeIdPrivateKey());
        createResult.setUserWeIdPublicKey(new WeIdPublicKey());
        createResult.getUserWeIdPrivateKey().setPrivateKey(privateKey);
        createResult.getUserWeIdPublicKey().setPublicKey(publicKey);

        this.setPublicKey(createResult, publicKey, createResult.getWeId());
        this.setAuthentication(createResult, publicKey, createResult.getWeId());

        CreateWeIdDataResult createWeId = new CreateWeIdDataResult();
        createWeId.setWeId(weId);

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId, this.privateKey);

        ResponseData<Boolean> response =
            authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        LogUtil.info(logger, "registerAuthorityIssuer", response);

        if (response.getErrorCode()
            .intValue() != ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_ALREADY_EXIST.getCode()
            && response.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            Assert.assertTrue(false);
        }

        issuerPrivateList.add(privateKey);
        logger.info("initIssuer success");
    }

    /**
     * verifyCredential.
     *
     * @param credential credential
     */
    protected ResponseData<Boolean> verifyCredential(Credential credential) {

        ResponseData<Boolean> response = credentialService.verify(credential);
        return response;
    }

    /**
     * createCredential.
     *
     * @param createCredentialArgs createCredentialArgs
     */
    protected CredentialWrapper createCredential(CreateCredentialArgs createCredentialArgs) {

        ResponseData<CredentialWrapper> response =
            credentialService.createCredential(createCredentialArgs);
        LogUtil.info(logger, "createCredential", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        return response.getResult();
    }

    /**
     * cpt register.
     *
     * @param createWeId createWeId
     * @param registerCptArgs registerCptArgs
     */
    protected CptBaseInfo registerCpt(
        CreateWeIdDataResult createWeId,
        CptMapArgs registerCptArgs) {

        ResponseData<CptBaseInfo> response = cptService.registerCpt(registerCptArgs);
        LogUtil.info(logger, "registerCpt", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertNotNull(response.getResult());

        return response.getResult();
    }

    /**
     * cpt register.
     *
     * @param createWeId createWeId
     */
    protected CptBaseInfo registerCpt(CreateWeIdDataResult createWeId) {

        CptMapArgs registerCptArgs = TestBaseUtil.buildCptArgs(createWeId);

        CptBaseInfo cptBaseInfo = registerCpt(createWeId, registerCptArgs);

        return cptBaseInfo;
    }

    /**
     * create WeIdentity DID and registerAuthorityIssuer.
     *
     * @return CreateWeIdDataResult
     */
    protected CreateWeIdDataResult registerAuthorityIssuer() {

        CreateWeIdDataResult createWeId = this.createWeId();

        registerAuthorityIssuer(createWeId);

        return createWeId;
    }

    /**
     * registerAuthorityIssuer default.
     */
    protected void registerAuthorityIssuer(CreateWeIdDataResult createWeId) {

        RegisterAuthorityIssuerArgs registerAuthorityIssuerArgs =
            TestBaseUtil.buildRegisterAuthorityIssuerArgs(createWeId, privateKey);

        ResponseData<Boolean> response = new ResponseData<>(false,
            ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NAME_ALREADY_EXISTS);

        while (response.getErrorCode()
            == ErrorCode.AUTHORITY_ISSUER_CONTRACT_ERROR_NAME_ALREADY_EXISTS.getCode()) {
            String name = registerAuthorityIssuerArgs.getAuthorityIssuer().getName();
            registerAuthorityIssuerArgs.getAuthorityIssuer().setName(name + Math.random());
            response = authorityIssuerService.registerAuthorityIssuer(registerAuthorityIssuerArgs);
        }
        logger.info("registerAuthorityIssuer result:");
        LogUtil.info(logger, "registerAuthorityIssuer", response);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), response.getErrorCode().intValue());
        Assert.assertEquals(true, response.getResult());
    }

    /**
     * create WeIdentity DID and set Attribute default.
     *
     * @return CreateWeIdDataResult
     */
    protected CreateWeIdDataResult createWeIdWithSetAttr() {

        CreateWeIdDataResult createWeId = this.createWeId();

        this.setPublicKey(createWeId, createWeId.getUserWeIdPublicKey().getPublicKey(),
            createWeId.getWeId());
        this.setAuthentication(createWeId, createWeId.getUserWeIdPublicKey().getPublicKey(),
            createWeId.getWeId());
        this.setService(createWeId, TestData.SERVICE_TYPE, TestData.SERVICE_ENDPOINT);
        return createWeId;
    }

    /**
     * create WeIdentity DID without set Attribute default.
     *
     * @return CreateWeIdDataResult
     */
    protected CreateWeIdDataResult createWeId() {

        ResponseData<CreateWeIdDataResult> createWeIdDataResult = weIdService.createWeId();
        LogUtil.info(logger, "createWeId", createWeIdDataResult);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(),
            createWeIdDataResult.getErrorCode().intValue());
        Assert.assertNotNull(createWeIdDataResult.getResult());

        return createWeIdDataResult.getResult();
    }

    /**
     * setPublicKey default.
     *
     * @param createResult createResult
     * @param publicKey publicKey
     * @param owner owner
     */
    protected void setPublicKey(
        CreateWeIdDataResult createResult,
        String publicKey,
        String owner) {

        // setPublicKey for this WeId
        SetPublicKeyArgs setPublicKeyArgs = TestBaseUtil.buildSetPublicKeyArgs(createResult);
        setPublicKeyArgs.setPublicKey(publicKey);
        setPublicKeyArgs.setOwner(owner);

        ResponseData<Boolean> responseSetPub = weIdService.setPublicKey(setPublicKeyArgs);
        LogUtil.info(logger, "setPublicKey", responseSetPub);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseSetPub.getErrorCode().intValue());
        Assert.assertEquals(true, responseSetPub.getResult());
    }

    /**
     * setService default.
     *
     * @param createResult createResult
     * @param serviceType serviceType
     * @param serviceEnpoint serviceEnpoint
     */
    protected void setService(
        CreateWeIdDataResult createResult,
        String serviceType,
        String serviceEnpoint) {

        // setService for this WeIdentity DID
        SetServiceArgs setServiceArgs = TestBaseUtil.buildSetServiceArgs(createResult);
        setServiceArgs.setType(serviceType);
        setServiceArgs.setServiceEndpoint(serviceEnpoint);

        ResponseData<Boolean> responseSetSer = weIdService.setService(setServiceArgs);
        LogUtil.info(logger, "setService", responseSetSer);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseSetSer.getErrorCode().intValue());
        Assert.assertEquals(true, responseSetSer.getResult());
    }

    /**
     * setAuthenticate default.
     *
     * @param createResult createResult
     * @param publicKey publicKey
     * @param owner owner
     */
    protected void setAuthentication(
        CreateWeIdDataResult createResult,
        String publicKey,
        String owner) {

        // setAuthenticate for this WeIdentity DID
        SetAuthenticationArgs setAuthenticationArgs =
            TestBaseUtil.buildSetAuthenticationArgs(createResult);
        setAuthenticationArgs.setOwner(owner);
        setAuthenticationArgs.setPublicKey(publicKey);
        ResponseData<Boolean> responseSetAuth =
            weIdService.setAuthentication(setAuthenticationArgs);
        LogUtil.info(logger, "setAuthentication", responseSetAuth);

        Assert.assertEquals(ErrorCode.SUCCESS.getCode(), responseSetAuth.getErrorCode().intValue());
        Assert.assertEquals(true, responseSetAuth.getResult());
    }

    protected MockUp<Future<?>> mockTimeoutFuture() {
        return new MockUp<Future<?>>() {
            @Mock
            public Future<?> get(long timeout, TimeUnit unit)
                throws TimeoutException {

                throw new TimeoutException();
            }
        };
    }

    protected MockUp<Future<?>> mockInterruptedFuture() {
        return new MockUp<Future<?>>() {
            @Mock
            public Future<?> get(long timeout, TimeUnit unit)
                throws InterruptedException {

                throw new InterruptedException();
            }

            @Mock
            public Future<?> get()
                throws InterruptedException {

                throw new InterruptedException();
            }
        };
    }

    protected MockUp<Future<?>> mockReturnNullFuture() {
        return new MockUp<Future<?>>() {
            @Mock
            public Future<?> get(long timeout, TimeUnit unit) {
                return null;
            }
        };
    }

    protected MockUp<WeIdContract> mockSetAttribute(MockUp<Future<?>> mockFuture) {
        return new MockUp<WeIdContract>() {
            @Mock
            public Future<?> createWeId(
                Address identity, 
                DynamicBytes auth,
                DynamicBytes created, 
                Int256 updated) {
                return mockFuture.getMockInstance();
            }
            
            @Mock
            public Future<?> setAttribute(
                Address identity,
                Bytes32 key,
                DynamicBytes value,
                Int256 updated) {
                return mockFuture.getMockInstance();
            }
        };
    }

    protected Credential copyCredential(Credential credential) {
        return CredentialUtils.copyCredential(credential);
    }

    protected CreateWeIdDataResult copyCreateWeId(CreateWeIdDataResult createWeId) {
        CreateWeIdDataResult copyWeId = new CreateWeIdDataResult();
        copyWeId.setWeId(createWeId.getWeId());
        copyWeId.setUserWeIdPrivateKey(new WeIdPrivateKey());
        copyWeId.getUserWeIdPrivateKey()
            .setPrivateKey(createWeId.getUserWeIdPrivateKey().getPrivateKey());
        copyWeId.setUserWeIdPublicKey(new WeIdPublicKey());
        copyWeId.getUserWeIdPublicKey()
            .setPublicKey(createWeId.getUserWeIdPublicKey().getPublicKey());
        return copyWeId;
    }
}
