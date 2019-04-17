package com.webank.weid.service.impl.callback;

import com.webank.weid.protocol.base.PolicyAndChellenge;

public interface PresentationPolicyService {

    /**
     * 获取PolicyAndChellenge.
     * @param policyId 策略编号
     * @return 返回PresentationPolicyE对象数据
     */
    public PolicyAndChellenge obtainPolicy(String policyId);
}
