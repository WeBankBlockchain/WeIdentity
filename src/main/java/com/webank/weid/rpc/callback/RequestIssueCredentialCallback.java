package com.webank.weid.rpc.callback;

import com.webank.wedpr.selectivedisclosure.CredentialTemplateEntity;
import com.webank.wedpr.selectivedisclosure.UserClient;
import com.webank.wedpr.selectivedisclosure.UserResult;
import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.DatabaseException;
import com.webank.weid.protocol.amop.IssueCredentialArgs;
import com.webank.weid.protocol.amop.RequestIssueCredentialArgs;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.response.AmopNotifyMsgResult;
import com.webank.weid.protocol.response.RequestIssueCredentialResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.CptService;
import com.webank.weid.service.impl.CptServiceImpl;
import com.webank.weid.service.impl.base.AmopBaseCallback;
import com.webank.weid.suite.api.persistence.PersistenceFactory;
import com.webank.weid.suite.api.persistence.inf.Persistence;
import com.webank.weid.suite.api.persistence.params.PersistenceType;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.JsonUtil;
import com.webank.weid.util.PropertyUtils;
import org.fisco.bcos.sdk.amop.AmopResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestIssueCredentialCallback extends AmopBaseCallback {

    private static final Logger logger = LoggerFactory.getLogger(RequestIssueCredentialCallback.class);
    public RequestIssueCredentialResponse requestIssueCredentialResponse;
    private static PersistenceType persistenceType;
    private static Persistence dataDriver;
    private static CptService cptService = new CptServiceImpl();
    public static RequestIssueCredentialArgs args;

    public RequestIssueCredentialCallback(RequestIssueCredentialArgs args) {
        this.args = args;
    }

    @Override
    public void onResponse(AmopResponse response) {
        super.onResponse(response);
        requestIssueCredentialResponse = DataToolUtils.deserialize(response.getAmopMsgIn().getContent().toString(),
                RequestIssueCredentialResponse.class);
        if (null == requestIssueCredentialResponse) {
            super.responseStruct.setErrorCode(ErrorCode.UNKNOW_ERROR);
        }
        blindCredentialSignature(requestIssueCredentialResponse, args.getAuth().getWeId());
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
}
