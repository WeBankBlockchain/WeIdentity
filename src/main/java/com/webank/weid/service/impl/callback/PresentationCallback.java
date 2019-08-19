/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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
