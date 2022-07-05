package com.webank.weid.rpc.callback;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.amop.GetPolicyAndChallengeArgs;
import com.webank.weid.protocol.base.PolicyAndChallenge;
import com.webank.weid.protocol.response.AmopNotifyMsgResult;
import com.webank.weid.protocol.response.GetPolicyAndChallengeResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.base.AmopBaseCallback;
import com.webank.weid.util.DataToolUtils;
import org.fisco.bcos.sdk.amop.AmopResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetPolicyAndChallengeCallback extends AmopBaseCallback {

    private static final Logger logger = LoggerFactory.getLogger(GetPolicyAndChallengeCallback.class);
    public GetPolicyAndChallengeResponse getPolicyAndChallengeResponse;
    public Integer policyId;

    public GetPolicyAndChallengeCallback(Integer policyId) {
        this.policyId = policyId;
    }

    @Override
    public void onResponse(AmopResponse response) {
        if (super.responseStruct.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            logger.error("AMOP response fail, policyId={}, errorCode={}, errorMessage={}",
                    policyId,
                    super.responseStruct.getErrorCode(),
                    super.responseStruct.getErrorMessage()
            );
            return;
        }
        getPolicyAndChallengeResponse = DataToolUtils.deserialize(response.getAmopMsgIn().getContent().toString(),
                GetPolicyAndChallengeResponse.class);
        if (null == getPolicyAndChallengeResponse) {
            super.responseStruct.setErrorCode(ErrorCode.UNKNOW_ERROR);
        }
    }
}
