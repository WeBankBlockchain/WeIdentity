package com.webank.weid.service.impl.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.amop.GetPolicyAndChallengeArgs;
import com.webank.weid.protocol.base.PolicyAndChallenge;
import com.webank.weid.protocol.response.GetPolicyAndChallengeResponse;
import com.webank.weid.rpc.callback.AmopCallback;

/**
 * 用于处理机构根据policyId获取policy的回调.
 * 
 * @author v_wbgyang
 *
 */
public class PresentationCallback extends AmopCallback {
    
    private static final Logger logger = 
            LoggerFactory.getLogger(PresentationCallback.class);

    private PresentationPolicyService policyService;
    
    @Override
    public GetPolicyAndChallengeResponse onPush(GetPolicyAndChallengeArgs arg) {
        logger.info("PresentationCallback param:{}", arg);
        GetPolicyAndChallengeResponse response = new GetPolicyAndChallengeResponse();
        if (policyService == null) {
            logger.error("PresentationCallback policyService is null");
            response.setErrorCode(ErrorCode.POLICY_SERVICE_NOT_EXISTS.getCode());
            response.setErrorMessage(ErrorCode.POLICY_SERVICE_NOT_EXISTS.getCodeDesc());
            return response;
        }
        PolicyAndChallenge policyAndChallenge;
        try {
            policyAndChallenge = 
                policyService.policyAndChallengeOnPush(arg.getPolicyId(), arg.getTargetUserWeId());
        } catch (Exception e) {
            logger.error("the policy service call fail, please check the error log.", e);
            response.setErrorCode(ErrorCode.POLICY_SERVICE_CALL_FAIL.getCode());
            response.setErrorMessage(ErrorCode.POLICY_SERVICE_CALL_FAIL.getCodeDesc());
            return response;
        }
        response.setErrorCode(ErrorCode.SUCCESS.getCode());
        response.setErrorMessage(ErrorCode.SUCCESS.getCodeDesc());
        response.setPolicyAndChallenge(policyAndChallenge);
        return response;
    }

    public void registPolicyService(PresentationPolicyService service) {
        policyService = service;
    }
}
