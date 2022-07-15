

package com.webank.weid.service.impl.callback;

import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.protocol.base.PolicyAndChallenge;
import com.webank.weid.rpc.AmopService;
import com.webank.weid.service.BaseService;
import com.webank.weid.service.impl.AmopServiceImpl;

public abstract class PresentationPolicyService extends BaseService {
    
    protected AmopService amopService = new AmopServiceImpl();
    
    private static PresentationCallback presentationCallback = new PresentationCallback();
    
    /**
     * 无参构造器,自动注册callback.
     */
    public PresentationPolicyService() {
        presentationCallback.registPolicyService(this);
        amopService.registerCallback(
            AmopMsgType.GET_POLICY_AND_CHALLENGE.getValue(), 
            presentationCallback
        );
    }

    /**
     * 获取PolicyAndChallenge.
     * @param policyId 策略编号
     * @param targetUserWeId user WeId
     * @return 返回PresentationPolicyE对象数据
     */
    public abstract PolicyAndChallenge policyAndChallengeOnPush(
        String policyId, 
        String targetUserWeId
    );
}
