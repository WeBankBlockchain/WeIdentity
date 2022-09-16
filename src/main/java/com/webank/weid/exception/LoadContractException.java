

package com.webank.weid.exception;

import com.webank.weid.constant.ErrorCode;

/**
 * Load Contract Exception.
 *
 * @author tonychen
 */
@SuppressWarnings("serial")
public class LoadContractException extends WeIdBaseException {

    public LoadContractException(Throwable cause) {
        super(ErrorCode.LOAD_CONTRACT_FAILED.getCodeDesc(), cause);
    }

    public LoadContractException() {
        super(ErrorCode.LOAD_CONTRACT_FAILED.getCodeDesc());
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.LOAD_CONTRACT_FAILED;
    }
}
