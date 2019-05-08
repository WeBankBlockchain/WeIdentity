package com.webank.weid.service.impl.callback;

import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.protocol.base.PolicyAndChallenge;
import com.webank.weid.service.BaseService;

public abstract class PresentationPolicyService extends BaseService {
    
    public PresentationPolicyService() {
        PresentationCallback presentationCallback = new PresentationCallback();
        presentationCallback.registPolicyService(this);
        registerCallback(AmopMsgType.GET_POLICY_AND_CHALLENGE.getValue(), presentationCallback); 
    }

    /**
     * 获取PolicyAndChellenge.
     * @param policyId 策略编号
     * @return 返回PresentationPolicyE对象数据
     */
    public abstract PolicyAndChallenge obtainPolicy(String policyId);
}
