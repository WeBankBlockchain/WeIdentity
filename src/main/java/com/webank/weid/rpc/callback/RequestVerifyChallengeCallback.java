package com.webank.weid.rpc.callback;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.response.AmopNotifyMsgResult;
import com.webank.weid.protocol.response.RequestVerifyChallengeResponse;
import com.webank.weid.service.impl.base.AmopBaseCallback;
import com.webank.weid.util.DataToolUtils;
import org.fisco.bcos.sdk.amop.AmopResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestVerifyChallengeCallback extends AmopBaseCallback {

    private static final Logger logger = LoggerFactory.getLogger(RequestVerifyChallengeCallback.class);
    public RequestVerifyChallengeResponse requestVerifyChallengeResponse;

    public RequestVerifyChallengeCallback () {}

    @Override
    public void onResponse(AmopResponse response) {
        super.onResponse(response);
        requestVerifyChallengeResponse = DataToolUtils.deserialize(response.getAmopMsgIn().getContent().toString(),
                RequestVerifyChallengeResponse.class);
        if (null == requestVerifyChallengeResponse) {
            super.responseStruct.setErrorCode(ErrorCode.UNKNOW_ERROR);
        }
    }
}
