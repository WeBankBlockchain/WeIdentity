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

    ResponseData<AmopResponse> request(String toAmopId, AmopCommonArgs args);

    ResponseData<PolicyAndChallenge> getPolicyAndChallenge(
        String amopId,
        Integer policyId,
        String targetUserWeId
    );

    ResponseData<GetEncryptKeyResponse> getEncryptKey(
        String toAmopId,
        GetEncryptKeyArgs args
    );

    ResponseData<PolicyAndPreCredentialResponse> requestPolicyAndPreCredential(
        String toAmopId,
        GetPolicyAndPreCredentialArgs args
    );

    ResponseData<RequestIssueCredentialResponse> requestIssueCredential(
        String toAmopId,
        RequestIssueCredentialArgs args
    );
    
    ResponseData<AmopResponse> send(String toAmopId, AmopCommonArgs args);

    /**
     * get weIdAuth object.
     * @param toAmopId target organization id
     * @param args random number
     * @return return the GetWeIdAuthResponse
     */
    ResponseData<GetWeIdAuthResponse> getWeIdAuth(
        String toAmopId,
        GetWeIdAuthArgs args
    );

    /**
     * verify challenge signature.
     * @param toAmopId target organization id
     * @param args verify args
     * @return return the RequestVerifyChallengeResponse
     */
    ResponseData<RequestVerifyChallengeResponse> requestVerifyChallenge(
        String toAmopId,
        RequestVerifyChallengeArgs args
    );

}
