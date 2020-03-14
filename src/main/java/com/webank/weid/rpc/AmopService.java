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

package com.webank.weid.rpc;

import com.webank.weid.protocol.amop.GetEncryptKeyArgs;
import com.webank.weid.protocol.amop.GetPolicyAndPreCredentialArgs;
import com.webank.weid.protocol.amop.GetWeIdAuthArgs;
import com.webank.weid.protocol.amop.RequestIssueCredentialArgs;
import com.webank.weid.protocol.amop.RequestVerifyChallengeArgs;
import com.webank.weid.protocol.base.PolicyAndChallenge;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.GetEncryptKeyResponse;
import com.webank.weid.protocol.response.GetWeIdAuthResponse;
import com.webank.weid.protocol.response.PolicyAndPreCredentialResponse;
import com.webank.weid.protocol.response.RequestIssueCredentialResponse;
import com.webank.weid.protocol.response.RequestVerifyChallengeResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.callback.AmopCallback;
import com.webank.weid.service.impl.base.AmopCommonArgs;

/**
 * Created by Junqi Zhang on 2019/4/10.
 */
public interface AmopService {

    void registerCallback(Integer directRouteMsgType, AmopCallback directRouteCallback);

    ResponseData<AmopResponse> request(String toOrgId, AmopCommonArgs args);

    ResponseData<PolicyAndChallenge> getPolicyAndChallenge(
        String orgId,
        Integer policyId,
        String targetUserWeId
    );

    ResponseData<GetEncryptKeyResponse> getEncryptKey(
        String toOrgId,
        GetEncryptKeyArgs args
    );

    ResponseData<PolicyAndPreCredentialResponse> requestPolicyAndPreCredential(
        String toOrgId,
        GetPolicyAndPreCredentialArgs args
    );

    ResponseData<RequestIssueCredentialResponse> requestIssueCredential(
        String toOrgId,
        RequestIssueCredentialArgs args
    );

    /**
     * @param toOrgId target organization id
     * @param weId self weId
     * @param challenge random number
     */
    ResponseData<GetWeIdAuthResponse> getWeIdAuth(
        String toOrgId,
        GetWeIdAuthArgs args
    );


    /**
     * @param toOrgId target organization id
     * @param weId self weId
     * @param challenge random number
     */
    ResponseData<RequestVerifyChallengeResponse> requestVerifyChallenge(
        String toOrgId,
        RequestVerifyChallengeArgs args
    );

}
