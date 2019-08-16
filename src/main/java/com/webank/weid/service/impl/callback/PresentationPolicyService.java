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
