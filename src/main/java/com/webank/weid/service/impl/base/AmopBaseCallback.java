package com.webank.weid.service.impl.base;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.BaseService;
import com.webank.weid.util.DataToolUtils;
import org.fisco.bcos.sdk.amop.AmopResponse;
import org.fisco.bcos.sdk.amop.AmopResponseCallback;
import org.fisco.bcos.sdk.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmopBaseCallback<T> extends AmopResponseCallback {

    private static final Logger logger = LoggerFactory.getLogger(AmopBaseCallback.class);

    public ResponseData<T> responseStruct;


    /**
     * Constructor.
     */
    public AmopBaseCallback() { }

    @Override
    public void onResponse(AmopResponse response) {
        logger.info("direct route response, seq : {}, errorCode : {}, errorMsg : {}, messageIn: {}",
                response.getMessageID(),
                response.getErrorCode(),
                response.getErrorMessage(),
                response.getAmopMsgIn()
        );
        responseStruct = new ResponseData<>();
        if (102 == response.getErrorCode()) {
            responseStruct.setErrorCode(ErrorCode.DIRECT_ROUTE_REQUEST_TIMEOUT);
        } else if (0 != response.getErrorCode()) {
            responseStruct.setErrorCode(ErrorCode.DIRECT_ROUTE_MSG_BASE_ERROR);
        } else {
            responseStruct.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        }
    }
}
