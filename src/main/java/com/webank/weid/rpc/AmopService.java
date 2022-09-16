

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
