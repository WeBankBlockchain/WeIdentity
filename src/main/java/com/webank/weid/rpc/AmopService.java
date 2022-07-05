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
import com.webank.weid.rpc.callback.*;
import com.webank.weid.service.impl.base.AmopBaseCallback;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import org.fisco.bcos.sdk.amop.AmopCallback;

/**
 * Created by Junqi Zhang on 2019/4/10.
 */
public interface AmopService {

    //void registerCallback(Integer directRouteMsgType, AmopCallback directRouteCallback);
    void registerCallback(String topicName, AmopCallback directRouteCallback);

    //ResponseData<AmopResponse> request(String toAmopId, AmopCommonArgs args);
    void request(String topic, AmopCommonArgs args, AmopBaseCallback cb);

    /*ResponseData<PolicyAndChallenge> getPolicyAndChallenge(
        String amopId,
        Integer policyId,
        String targetUserWeId
    );*/
    void getPolicyAndChallenge(
        //String amopId,
        Integer policyId,
        String targetUserWeId,
        GetPolicyAndChallengeCallback cb
    );

    /*ResponseData<GetEncryptKeyResponse> getEncryptKey(
        String toAmopId,
        GetEncryptKeyArgs args
    );*/
    void getEncryptKey(
        //String toAmopId,
        GetEncryptKeyArgs args,
        GetEncryptKeyCallback cb
    );

    /*ResponseData<PolicyAndPreCredentialResponse> requestPolicyAndPreCredential(
        String toAmopId,
        GetPolicyAndPreCredentialArgs args
    );*/
    void requestPolicyAndPreCredential(
        //String toAmopId,
        GetPolicyAndPreCredentialArgs args,
        GetPolicyAndChallengeCallback cb
    );

    /*ResponseData<RequestIssueCredentialResponse> requestIssueCredential(
        String toAmopId,
        RequestIssueCredentialArgs args
    );*/
    void requestIssueCredential(
        //String toAmopId,
        RequestIssueCredentialArgs args,
        RequestIssueCredentialCallback cb
    );
    
    //ResponseData<AmopResponse> send(String toAmopId, AmopCommonArgs args);
    void send(AmopCommonArgs args, AmopBaseCallback cb);

    /**
     * get weIdAuth object.
     * @param args random number
     * @return return the GetWeIdAuthResponse
     */
    /*ResponseData<GetWeIdAuthResponse> getWeIdAuth(
        String toAmopId,
        GetWeIdAuthArgs args
    );*/
    void getWeIdAuth(
        //String toAmopId,
        GetWeIdAuthArgs args,
        GetWeIdAuthCallback cb
    );

    /**
     * verify challenge signature.
     * @param args verify args
     * @return return the RequestVerifyChallengeResponse
     */
    /*ResponseData<RequestVerifyChallengeResponse> requestVerifyChallenge(
        String toAmopId,
        RequestVerifyChallengeArgs args
    );*/
    void requestVerifyChallenge(
            //String toAmopId,
            RequestVerifyChallengeArgs args,
            RequestVerifyChallengeCallback cb
    );

}
