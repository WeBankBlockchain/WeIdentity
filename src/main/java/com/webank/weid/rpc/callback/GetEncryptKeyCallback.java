package com.webank.weid.rpc.callback;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.response.GetEncryptKeyResponse;
import com.webank.weid.protocol.response.GetPolicyAndChallengeResponse;
import com.webank.weid.service.impl.base.AmopBaseCallback;
import com.webank.weid.util.DataToolUtils;
import org.fisco.bcos.sdk.amop.AmopResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetEncryptKeyCallback extends AmopBaseCallback {

    private static final Logger logger = LoggerFactory.getLogger(GetEncryptKeyCallback.class);
    public GetEncryptKeyResponse getEncryptKeyResponse;

    public GetEncryptKeyCallback() {

    }

    @Override
    public void onResponse(AmopResponse response) {
        super.onResponse(response);
        getEncryptKeyResponse = DataToolUtils.deserialize(response.getAmopMsgIn().getContent().toString(),
                GetEncryptKeyResponse.class);
        if (null == getEncryptKeyResponse) {
            super.responseStruct.setErrorCode(ErrorCode.UNKNOW_ERROR);
        }
    }
}
