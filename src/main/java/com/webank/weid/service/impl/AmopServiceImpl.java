/*
 *       Copyright© (2018) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webank.wedpr.selectivedisclosure.CredentialTemplateEntity;
import com.webank.wedpr.selectivedisclosure.UserClient;
import com.webank.wedpr.selectivedisclosure.UserResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.protocol.amop.GetEncryptKeyArgs;
import com.webank.weid.protocol.amop.GetPolicyAndChallengeArgs;
import com.webank.weid.protocol.amop.GetPolicyAndPreCredentialArgs;
import com.webank.weid.protocol.amop.IssueCredentialArgs;
import com.webank.weid.protocol.amop.RequestIssueCredentialArgs;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PolicyAndChallenge;
import com.webank.weid.protocol.base.PolicyAndPreCredential;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.GetEncryptKeyResponse;
import com.webank.weid.protocol.response.GetPolicyAndChallengeResponse;
import com.webank.weid.protocol.response.PolicyAndPreCredentialResponse;
import com.webank.weid.protocol.response.RequestIssueCredentialResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.AmopService;
import com.webank.weid.rpc.CptService;
import com.webank.weid.rpc.CredentialPojoService;
import com.webank.weid.rpc.callback.AmopCallback;
import com.webank.weid.service.BaseService;
import com.webank.weid.service.fisco.WeServer;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.suite.api.persistence.Persistence;
import com.webank.weid.suite.persistence.sql.driver.MysqlDriver;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.JsonUtil;
import com.webank.weid.util.WeIdUtils;


/**
 * Created by Junqi Zhang on 2019/4/10.
 */
public class AmopServiceImpl extends BaseService implements AmopService {

    private static final Logger logger = LoggerFactory.getLogger(AmopServiceImpl.class);

    private static CptService cptService = new CptServiceImpl();

    /**
     * persistence service.
     */
    private static Persistence dataDriver = new MysqlDriver();

    /**
     * credentialpojo service.
     */
    private static CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();

    @Override
    public ResponseData<PolicyAndChallenge> getPolicyAndChallenge(
        String targetOrgId,
        Integer policyId,
        String targetUserWeId
    ) {
        try {
            if (StringUtils.isBlank(fiscoConfig.getCurrentOrgId())) {
                logger.error("the orgId is null, policyId = {}", policyId);
                return new ResponseData<PolicyAndChallenge>(null, ErrorCode.ILLEGAL_INPUT);
            }
            GetPolicyAndChallengeArgs args = new GetPolicyAndChallengeArgs();
            args.setFromOrgId(fiscoConfig.getCurrentOrgId());
            args.setToOrgId(targetOrgId);
            args.setPolicyId(String.valueOf(policyId));
            args.setMessageId(super.getSeq());
            args.setTargetUserWeId(targetUserWeId);
            ResponseData<GetPolicyAndChallengeResponse> retResponse =
                this.getPolicyAndChallenge(targetOrgId, args);
            if (retResponse.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
                logger.error("AMOP response fail, policyId={}, errorCode={}, errorMessage={}",
                    policyId,
                    retResponse.getErrorCode(),
                    retResponse.getErrorMessage()
                );
                return new ResponseData<PolicyAndChallenge>(
                    null,
                    ErrorCode.getTypeByErrorCode(retResponse.getErrorCode().intValue())
                );
            }
            GetPolicyAndChallengeResponse result = retResponse.getResult();
            ErrorCode errorCode =
                ErrorCode.getTypeByErrorCode(result.getErrorCode().intValue());
            return new ResponseData<PolicyAndChallenge>(result.getPolicyAndChallenge(), errorCode);
        } catch (Exception e) {
            logger.error("getPresentationPolicy failed due to system error. ", e);
            return new ResponseData<PolicyAndChallenge>(null, ErrorCode.UNKNOW_ERROR);
        }
    }

    private ResponseData<GetPolicyAndChallengeResponse> getPolicyAndChallenge(
        String toOrgId,
        GetPolicyAndChallengeArgs args) {
        return this.getImpl(
            fiscoConfig.getCurrentOrgId(),
            toOrgId,
            args,
            GetPolicyAndChallengeArgs.class,
            GetPolicyAndChallengeResponse.class,
            AmopMsgType.GET_POLICY_AND_CHALLENGE,
            WeServer.AMOP_REQUEST_TIMEOUT
        );
    }

    /**
     * 发送普通消息的AMOP请求接口.
     */
    public ResponseData<AmopResponse> request(String toOrgId, AmopCommonArgs args) {
        return this.getImpl(
            fiscoConfig.getCurrentOrgId(),
            toOrgId,
            args,
            AmopCommonArgs.class,
            AmopResponse.class,
            AmopMsgType.TYPE_TRANSPORTATION,
            WeServer.AMOP_REQUEST_TIMEOUT
        );
    }

    /**
     * 通过AMOP获取秘钥请求接口.
     */
    public ResponseData<GetEncryptKeyResponse> getEncryptKey(String toOrgId,
        GetEncryptKeyArgs args) {
        return this.getImpl(
            fiscoConfig.getCurrentOrgId(),
            toOrgId,
            args,
            GetEncryptKeyArgs.class,
            GetEncryptKeyResponse.class,
            AmopMsgType.GET_ENCRYPT_KEY,
            WeServer.AMOP_REQUEST_TIMEOUT
        );
    }

    /**
     * 注册回调函数接口.
     */
    public void registerCallback(Integer directRouteMsgType, AmopCallback directRouteCallback) {
        super.getPushCallback().registAmopCallback(directRouteMsgType, directRouteCallback);
    }

    /**
     * request PolicyAndPreCredential.
     *
     * @param toOrgId toOrgId
     * @param args args
     * @return PolicyAndPreCredential
     */
    public ResponseData<PolicyAndPreCredentialResponse> requestPolicyAndPreCredential(
        String toOrgId,
        GetPolicyAndPreCredentialArgs args) {

        return this.getImpl(
            fiscoConfig.getCurrentOrgId(),
            toOrgId,
            args,
            GetPolicyAndPreCredentialArgs.class,
            PolicyAndPreCredentialResponse.class,
            AmopMsgType.GET_POLICY_AND_PRE_CREDENTIAL,
            WeServer.AMOP_REQUEST_TIMEOUT
        );
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.AmopService#requestIssueCredential(java.lang.String,
     * com.webank.weid.protocol.amop.RequestIssueCredentialArgs)
     */
    @Override
    public ResponseData<RequestIssueCredentialResponse> requestIssueCredential(
        String toOrgId,
        RequestIssueCredentialArgs args) {

        int checkErrorCode = checkIssueCredentialArgs(args).getCode();
        if (checkErrorCode != ErrorCode.SUCCESS.getCode()) {
            logger.error(
                "[requestIssueCredential] prepareZkpCredential failed. error code :{}",
                checkErrorCode);
            return new ResponseData<RequestIssueCredentialResponse>(null,
                ErrorCode.getTypeByErrorCode(checkErrorCode));
        }

        //1. user genenerate credential based on CPT111
        PolicyAndPreCredential policyAndPreCredential = args.getPolicyAndPreCredential();
        String claimJson = args.getClaim();
        CredentialPojo preCredential = policyAndPreCredential.getPreCredential();
        ResponseData<CredentialPojo> userCredentialResp =
            credentialPojoService.prepareZkpCredential(
                preCredential,
                claimJson,
                args.getAuth());
        int errCode = userCredentialResp.getErrorCode();
        if (errCode != ErrorCode.SUCCESS.getCode()) {
            logger.error(
                "[requestIssueCredential] prepareZkpCredential failed. error code :{}",
                errCode);
            return new ResponseData<RequestIssueCredentialResponse>(null,
                ErrorCode.getTypeByErrorCode(errCode));
        }

        //2. prepare presentation and send amop request to verify presentation and issue credential
        CredentialPojo userCredential = userCredentialResp.getResult();
        ResponseData<PresentationE> presentationResp = preparePresentation(args, userCredential);
        int errorCode = presentationResp.getErrorCode();
        if (errorCode != ErrorCode.SUCCESS.getCode()) {
            logger.error(
                "[requestIssueCredential] create presentation failed. error code :{}",
                errorCode);
            return new ResponseData<RequestIssueCredentialResponse>(null,
                ErrorCode.getTypeByErrorCode(errorCode));
        }

        //3. send presentataion to issuer and request issue credential.
        PresentationE presentation = presentationResp.getResult();
        ResponseData<RequestIssueCredentialResponse> resp =
            requestIssueCredentialInner(
                toOrgId,
                args,
                userCredential,
                presentation);

        //ResponseData<RequestIssueCredentialResponse> resp =
        //Test.test( issueCredentialArgs,  policyAndChallenge);

        //4. get credential response and blind signature.
        RequestIssueCredentialResponse response = resp.getResult();
        blindCredentialSignature(response, args.getAuth().getWeId());
        return resp;
    }

    private ErrorCode checkIssueCredentialArgs(RequestIssueCredentialArgs args) {

        if (args == null
            || args.getAuth() == null
            || args.getPolicyAndPreCredential() == null
            || args.getCredentialList() == null) {

            return ErrorCode.ILLEGAL_INPUT;

        }
        PolicyAndPreCredential policyAndPreCredential = args.getPolicyAndPreCredential();
        PolicyAndChallenge policyAndChallenge = policyAndPreCredential.getPolicyAndChallenge();
        if (policyAndChallenge == null
            || policyAndChallenge.getChallenge() == null
            || policyAndChallenge.getPresentationPolicyE() == null) {
            return ErrorCode.ILLEGAL_INPUT;
        }
        WeIdAuthentication auth = args.getAuth();
        if (!WeIdUtils.isWeIdValid(auth.getWeId())) {
            return ErrorCode.WEID_INVALID;
        }
        if (!WeIdUtils
            .isKeypairMatch(auth.getWeIdPrivateKey().getPrivateKey(), auth.getWeIdPublicKeyId())) {
            return ErrorCode.WEID_PRIVATEKEY_DOES_NOT_MATCH;
        }

        return ErrorCode.SUCCESS;
    }

    private ResponseData<RequestIssueCredentialResponse> requestIssueCredentialInner(
        String toOrgId,
        RequestIssueCredentialArgs args,
        CredentialPojo userCredential,
        PresentationE presentation) {

        //prepare request args
        String claimJson = args.getClaim();
        IssueCredentialArgs issueCredentialArgs = new IssueCredentialArgs();
        issueCredentialArgs.setClaim(claimJson);
        String policyId = String.valueOf(
            args.getPolicyAndPreCredential()
                .getPolicyAndChallenge()
                .getPresentationPolicyE()
                .getId());
        issueCredentialArgs.setPolicyId(policyId);
        issueCredentialArgs.setPresentation(presentation);

        // AMOP request (issuer to issue credential)
        ResponseData<RequestIssueCredentialResponse> resp = this.getImpl(
            fiscoConfig.getCurrentOrgId(),
            toOrgId,
            issueCredentialArgs,
            IssueCredentialArgs.class,
            RequestIssueCredentialResponse.class,
            AmopMsgType.REQUEST_SIGN_CREDENTIAL,
            WeServer.AMOP_REQUEST_TIMEOUT
        );
        return resp;
    }

    private ResponseData<PresentationE> preparePresentation(
        RequestIssueCredentialArgs args,
        CredentialPojo userCredential) {

        List<CredentialPojo> credentialList = args.getCredentialList();
        PolicyAndPreCredential policyAndPreCredential = args.getPolicyAndPreCredential();
        PolicyAndChallenge policyAndChallenge = policyAndPreCredential.getPolicyAndChallenge();

        credentialList.add(userCredential);

        //put pre-credential and user-credential(based on CPT 111)
        ResponseData<PresentationE> presentationResp =
            credentialPojoService.createPresentation(
                credentialList,
                policyAndChallenge.getPresentationPolicyE(),
                policyAndChallenge.getChallenge(),
                args.getAuth());

        return presentationResp;
    }

    /**
     * blind credential signature.
     */
    private void blindCredentialSignature(RequestIssueCredentialResponse response, String userId) {

        CredentialPojo credentialPojo = response.getCredentialPojo();
        Map<String, String> credentialInfoMap = new HashMap<>();
        Map<String, String> newCredentialInfo = new HashMap<>();
        try {
            credentialInfoMap = JsonUtil.credentialToMonolayer(credentialPojo);
            for (Map.Entry<String, String> entry : credentialInfoMap.entrySet()) {
                newCredentialInfo.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        } catch (IOException e) {
            logger.error("[requestIssueCredential] generate credentialInfoMap failed.", e);
            //return new ResponseData<RequestIssueCredentialResponse>(null, ErrorCode.UNKNOW_ERROR);
        }

        Integer cptId = credentialPojo.getCptId();
        ResponseData<CredentialTemplateEntity> resp1 = cptService.queryCredentialTemplate(cptId);
        CredentialTemplateEntity template = resp1.getResult();
        String id = new StringBuffer().append(userId).append("_").append(cptId)
            .toString();
        ResponseData<String> dbResp = dataDriver
            .get(DataDriverConstant.DOMAIN_USER_MASTER_SECRET, id);
        if (dbResp.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            throw new DatabaseException("database error!");
        }
        String userInfo = dbResp.getResult();
        Map<String, String> userInfoMap = DataToolUtils.deserialize(userInfo, HashMap.class);
        String masterSecret = userInfoMap.get("masterSecret");
        String credentialSecretsBlindingFactors = userInfoMap
            .get("credentialSecretsBlindingFactors");
        //有问题
        UserResult userResult = UserClient.blindCredentialSignature(
            response.getCredentialSignature(),
            newCredentialInfo,
            template,
            masterSecret,
            credentialSecretsBlindingFactors,
            response.getIssuerNonce());
        String newCredentialSignature = userResult.credentialSignature;

        //String dbKey = (String) preCredential.getClaim()
        //   .get(CredentialConstant.CREDENTIAL_META_KEY_ID);
        String dbKey = credentialPojo.getId();
        ResponseData<Integer> dbResponse =
            dataDriver.saveOrUpdate(
                DataDriverConstant.DOMAIN_USER_CREDENTIAL_SIGNATURE,
                dbKey,
                newCredentialSignature);
        if (dbResponse.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            throw new DatabaseException("database error!");
        }
    }
}
