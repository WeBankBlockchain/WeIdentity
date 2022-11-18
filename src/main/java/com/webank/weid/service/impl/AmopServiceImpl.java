

package com.webank.weid.service.impl;

import com.webank.wedpr.selectivedisclosure.CredentialTemplateEntity;
import com.webank.wedpr.selectivedisclosure.UserClient;
import com.webank.wedpr.selectivedisclosure.UserResult;
import com.webank.weid.blockchain.constant.WeIdConstant;
import com.webank.weid.blockchain.service.fisco.BaseServiceFisco;
import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.protocol.amop.*;
import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;
import com.webank.weid.protocol.base.*;
import com.webank.weid.protocol.response.*;
import com.webank.weid.rpc.AmopService;
import com.webank.weid.rpc.CptService;
import com.webank.weid.rpc.CredentialPojoService;
import com.webank.weid.rpc.callback.OnNotifyCallbackV2;
import com.webank.weid.rpc.callback.OnNotifyCallbackV3;
import com.webank.weid.rpc.callback.RegistCallBack;
import com.webank.weid.rpc.callback.WeIdAmopCallback;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.service.impl.callback.CommonCallbackWeId;
import com.webank.weid.service.impl.callback.KeyManagerCallbackWeId;
import com.webank.weid.suite.api.persistence.PersistenceFactory;
import com.webank.weid.suite.api.persistence.inf.Persistence;
import com.webank.weid.suite.api.persistence.params.PersistenceType;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.JsonUtil;
import com.webank.weid.util.PropertyUtils;
import com.webank.weid.util.WeIdUtils;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.AmopCallback;
import org.fisco.bcos.sdk.jni.amop.AmopRequestCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Junqi Zhang on 2019/4/10.
 */
public class AmopServiceImpl implements AmopService {

    private static final Logger logger = LoggerFactory.getLogger(AmopServiceImpl.class);

    public static final int MAX_AMOP_REQUEST_TIMEOUT = 50000;
    public static final int AMOP_REQUEST_TIMEOUT = Integer
            .valueOf(PropertyUtils.getProperty("amop.request.timeout", "5000"));


    private static CptService cptService = new CptServiceImpl();

    /**
     * AMOP回调处理注册器.
     */
    private RegistCallBack pushCallBack;

    private String amopId;

    /**
     * persistence service.
     */
    private static Persistence dataDriver;

    private static PersistenceType persistenceType;

    public AmopServiceImpl() {
        this.amopId = com.webank.weid.blockchain.service.fisco.BaseServiceFisco.fiscoConfig.getAmopId();
        initAmopCallBack();
    }

    /**
     * credentialpojo service.
     */
    private static CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();

    /**
     * 获取AMOP监听的topic.
     *
     * @return 返回topic集合，目前sdk只支持单topic监听
     */
    private String getTopic() {
        if (StringUtils.isNotBlank(com.webank.weid.blockchain.config.FiscoConfig.topic)) {
            return this.amopId + "_" + com.webank.weid.blockchain.config.FiscoConfig.topic;
        } else {
            return this.amopId;
        }
    }

    private void initAmopCallBack() {
        if (com.webank.weid.blockchain.service.fisco.BaseServiceFisco.fiscoConfig.getVersion().startsWith(WeIdConstant.FISCO_BCOS_2_X_VERSION_PREFIX)) {
            pushCallBack = new OnNotifyCallbackV2();
            ((BcosSDK) com.webank.weid.blockchain.service.fisco.BaseServiceFisco.getBcosSDK()).getAmop().setCallback((AmopCallback) pushCallBack);
            ((BcosSDK) com.webank.weid.blockchain.service.fisco.BaseServiceFisco.getBcosSDK()).getAmop().subscribeTopic(getTopic(), (AmopCallback) pushCallBack);
        } else {
            pushCallBack = new OnNotifyCallbackV3();
            ((org.fisco.bcos.sdk.v3.BcosSDK) com.webank.weid.blockchain.service.fisco.BaseServiceFisco.getBcosSDK()).getAmop().setCallback((AmopRequestCallback) pushCallBack);
        }
        pushCallBack.registAmopCallback(
                AmopMsgType.GET_ENCRYPT_KEY.getValue(),
                new KeyManagerCallbackWeId()
        );
        pushCallBack.registAmopCallback(
                AmopMsgType.COMMON_REQUEST.getValue(),
                new CommonCallbackWeId()
        );
    }

    private static Persistence getDataDriver() {
        String type = PropertyUtils.getProperty("persistence_type");
        if (type.equals("mysql")) {
            persistenceType = PersistenceType.Mysql;
        } else if (type.equals("redis")) {
            persistenceType = PersistenceType.Redis;
        }
        if (dataDriver == null) {
            dataDriver = PersistenceFactory.build(persistenceType);
        }
        return dataDriver;
    }

    /**
     * 获取PushCallback对象，用于给使用者注册callback处理器.
     *
     * @return 返回RegistCallBack
     */
    public RegistCallBack getPushCallback() {
        return pushCallBack;
    }

    @Override
    public ResponseData<PolicyAndChallenge> getPolicyAndChallenge(
            String toAmopId,
            Integer policyId,
            String targetUserWeId
    ) {
        try {
            if (StringUtils.isBlank(this.amopId)) {
                logger.error("the amopId is null, policyId = {}", policyId);
                return new ResponseData<PolicyAndChallenge>(null, ErrorCode.ILLEGAL_INPUT);
            }
            GetPolicyAndChallengeArgs args = new GetPolicyAndChallengeArgs();
            args.setFromAmopId(this.amopId);
            args.setTopic(toAmopId);
            args.setPolicyId(String.valueOf(policyId));
            args.setMessageId(DataToolUtils.getUuId32());
            args.setTargetUserWeId(targetUserWeId);
            ResponseData<GetPolicyAndChallengeResponse> retResponse =
                    this.getPolicyAndChallenge(toAmopId, args);
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
            String toAmopId,
            GetPolicyAndChallengeArgs args) {
        return this.getImpl(
                this.amopId,
                toAmopId,
                args,
                GetPolicyAndChallengeArgs.class,
                GetPolicyAndChallengeResponse.class,
                AmopMsgType.GET_POLICY_AND_CHALLENGE,
                AMOP_REQUEST_TIMEOUT
        );
    }

    /**
     * 发送普通消息的AMOP请求接口.
     */
    public ResponseData<AmopResponse> request(String toAmopId, AmopCommonArgs args) {
        return this.getImpl(
                this.amopId,
                toAmopId,
                args,
                AmopCommonArgs.class,
                AmopResponse.class,
                AmopMsgType.TYPE_TRANSPORTATION,
                AMOP_REQUEST_TIMEOUT
        );
    }

    /**
     * 通过AMOP获取秘钥请求接口.
     */
    public ResponseData<GetEncryptKeyResponse> getEncryptKey(String toAmopId,
                                                             GetEncryptKeyArgs args) {
        return this.getImpl(
                this.amopId,
                toAmopId,
                args,
                GetEncryptKeyArgs.class,
                GetEncryptKeyResponse.class,
                AmopMsgType.GET_ENCRYPT_KEY,
                AMOP_REQUEST_TIMEOUT
        );
    }

    /**
     * 注册回调函数接口.
     */
    public void registerCallback(Integer directRouteMsgType, WeIdAmopCallback directRouteCallback) {
        pushCallBack.registAmopCallback(directRouteMsgType, directRouteCallback);
    }

    /**
     * request PolicyAndPreCredential.
     *
     * @param toAmopId toAmopId
     * @param args args
     * @return PolicyAndPreCredential
     */
    public ResponseData<PolicyAndPreCredentialResponse> requestPolicyAndPreCredential(
            String toAmopId,
            GetPolicyAndPreCredentialArgs args) {

        return this.getImpl(
                this.amopId,
                toAmopId,
                args,
                GetPolicyAndPreCredentialArgs.class,
                PolicyAndPreCredentialResponse.class,
                AmopMsgType.GET_POLICY_AND_PRE_CREDENTIAL,
                AMOP_REQUEST_TIMEOUT
        );
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.AmopService#requestIssueCredential(java.lang.String,
     * com.webank.weid.protocol.amop.RequestIssueCredentialArgs)
     */
    @Override
    public ResponseData<RequestIssueCredentialResponse> requestIssueCredential(
            String toAmopId,
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
                        toAmopId,
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

        return ErrorCode.SUCCESS;
    }

    private ResponseData<RequestIssueCredentialResponse> requestIssueCredentialInner(
            String toAmopId,
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
                this.amopId,
                toAmopId,
                issueCredentialArgs,
                IssueCredentialArgs.class,
                RequestIssueCredentialResponse.class,
                AmopMsgType.REQUEST_SIGN_CREDENTIAL,
                AMOP_REQUEST_TIMEOUT
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
        ResponseData<String> dbResp = getDataDriver()
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
                getDataDriver().addOrUpdate(
                        DataDriverConstant.DOMAIN_USER_CREDENTIAL_SIGNATURE,
                        dbKey,
                        newCredentialSignature);
        if (dbResponse.getErrorCode().intValue() != ErrorCode.SUCCESS.getCode()) {
            throw new DatabaseException("database error!");
        }
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.AmopService#getWeIdAuth(java.lang.String, java.lang.String,
     * com.webank.weid.protocol.base.Challenge)
     */
    @Override
    public ResponseData<GetWeIdAuthResponse> getWeIdAuth(
            String toAmopId,
            GetWeIdAuthArgs args) {

        ResponseData<GetWeIdAuthResponse> resp = this.getImpl(
                this.amopId,
                toAmopId,
                args,
                GetWeIdAuthArgs.class,
                GetWeIdAuthResponse.class,
                AmopMsgType.GET_WEID_AUTH,
                AMOP_REQUEST_TIMEOUT
        );

        return resp;
    }

    /* (non-Javadoc)
     * @see com.webank.weid.rpc.AmopService#requestVerifyChallenge(java.lang.String,
     * com.webank.weid.protocol.amop.RequestVerifyChallengeArgs)
     */
    @Override
    public ResponseData<RequestVerifyChallengeResponse> requestVerifyChallenge(String toAmopId,
                                                                               RequestVerifyChallengeArgs args) {

        ResponseData<RequestVerifyChallengeResponse> resp = this.getImpl(
                this.amopId,
                toAmopId,
                args,
                RequestVerifyChallengeArgs.class,
                RequestVerifyChallengeResponse.class,
                AmopMsgType.GET_WEID_AUTH,
                AMOP_REQUEST_TIMEOUT
        );

        return resp;
    }

    /**
     * 发送通用消息的AMOP请求接口.
     */
    public ResponseData<AmopResponse> send(String toAmopId, AmopCommonArgs args) {
        return this.getImpl(
                this.amopId,
                toAmopId,
                args,
                AmopCommonArgs.class,
                AmopResponse.class,
                AmopMsgType.COMMON_REQUEST,
                AMOP_REQUEST_TIMEOUT
        );
    }

    /**
     * the checkDirectRouteMsgHealth。.
     *
     * @param toAmopId target amopId.
     * @param arg the message
     * @return return the health result
     */
    public ResponseData<AmopNotifyMsgResult> checkDirectRouteMsgHealth(
            String toAmopId,
            CheckAmopMsgHealthArgs arg) {

        return this.getImpl(
                com.webank.weid.blockchain.service.fisco.BaseServiceFisco.fiscoConfig.getAmopId(),
                toAmopId,
                arg,
                CheckAmopMsgHealthArgs.class,
                AmopNotifyMsgResult.class,
                AmopMsgType.TYPE_CHECK_DIRECT_ROUTE_MSG_HEALTH,
                AMOP_REQUEST_TIMEOUT
        );
    }

    protected <T, F extends AmopBaseMsgArgs> ResponseData<T> getImpl(
            String fromAmopId,
            String toAmopId,
            F arg,
            Class<F> argsClass,
            Class<T> resultClass,
            AmopMsgType msgType,
            int timeOut
    ) {
        arg.setFromAmopId(fromAmopId);
        arg.setTopic(toAmopId);

        String msgBody = DataToolUtils.serialize(arg);
        AmopRequestBody amopRequestBody = new AmopRequestBody();
        amopRequestBody.setMsgType(msgType);
        amopRequestBody.setMsgBody(msgBody);
        String requestBodyStr = DataToolUtils.serialize(amopRequestBody);

        com.webank.weid.blockchain.protocol.amop.base.AmopCommonArgs amopCommonArgs = new com.webank.weid.blockchain.protocol.amop.base.AmopCommonArgs();
        amopCommonArgs.setTopic(toAmopId);
        amopCommonArgs.setMessage(requestBodyStr);
        amopCommonArgs.setMessageId(DataToolUtils.getUuId32());
        logger.info("direct route request, seq : {}, body ：{}", amopCommonArgs.getMessageId(),
                requestBodyStr);
        com.webank.weid.blockchain.protocol.response.AmopResponse response = com.webank.weid.blockchain.service.fisco.BaseServiceFisco.getWeServer(BaseServiceFisco.masterGroupId).sendChannelMessage(amopCommonArgs, timeOut);
        logger.info("direct route response, seq : {}, errorCode : {}, errorMsg : {}, body : {}",
                response.getMessageId(),
                response.getErrorCode(),
                response.getErrorMessage(),
                response.getResult()
        );
        ResponseData<T> responseStruct = new ResponseData<>();
        if (102 == response.getErrorCode()) {
            responseStruct.setErrorCode(ErrorCode.DIRECT_ROUTE_REQUEST_TIMEOUT);
        } else if (0 != response.getErrorCode()) {
            responseStruct.setErrorCode(ErrorCode.DIRECT_ROUTE_MSG_BASE_ERROR);
            //return responseStruct;
        } else {
            responseStruct.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        }
        T msgBodyObj = DataToolUtils.deserialize(response.getResult(), resultClass);
        if (null == msgBodyObj) {
            responseStruct.setErrorCode(ErrorCode.UNKNOW_ERROR);
        }
        responseStruct.setResult(msgBodyObj);
        return responseStruct;
    }
}
