package com.webank.weid.rpc.callback;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.response.GetPolicyAndChallengeResponse;
import com.webank.weid.protocol.response.GetWeIdAuthResponse;
import com.webank.weid.service.impl.base.AmopBaseCallback;
import com.webank.weid.util.DataToolUtils;
import org.fisco.bcos.sdk.amop.AmopResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetWeIdAuthCallback extends AmopBaseCallback {
    private static final Logger logger = LoggerFactory.getLogger(GetWeIdAuthCallback.class);
    public GetWeIdAuthResponse getWeIdAuthResponse;

    public GetWeIdAuthCallback() {

    }

    @Override
    public void onResponse(AmopResponse response) {
        super.onResponse(response);
        getWeIdAuthResponse = DataToolUtils.deserialize(response.getAmopMsgIn().getContent().toString(),
                GetWeIdAuthResponse.class);
        if (null == getWeIdAuthResponse) {
            super.responseStruct.setErrorCode(ErrorCode.UNKNOW_ERROR);
        }
    }
}
