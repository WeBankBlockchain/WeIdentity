package com.webank.weid.rpc.callback;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.response.AmopNotifyMsgResult;
import com.webank.weid.service.impl.base.AmopBaseCallback;
import com.webank.weid.util.DataToolUtils;
import org.fisco.bcos.sdk.amop.AmopResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmopNotifyMsgCallback extends AmopBaseCallback {

    private static final Logger logger = LoggerFactory.getLogger(AmopNotifyMsgCallback.class);
    public AmopNotifyMsgResult amopNotifyMsgResult;

    public AmopNotifyMsgCallback() {

    }

    @Override
    public void onResponse(AmopResponse response) {
        super.onResponse(response);
        amopNotifyMsgResult = DataToolUtils.deserialize(response.getAmopMsgIn().getContent().toString(),
                AmopNotifyMsgResult.class);
        if (null == amopNotifyMsgResult) {
            super.responseStruct.setErrorCode(ErrorCode.UNKNOW_ERROR);
        }
    }
}
