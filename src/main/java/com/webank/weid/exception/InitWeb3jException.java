

package com.webank.weid.exception;

import com.webank.weid.constant.ErrorCode;

/**
 * InitWeb3sdk Exception.
 *
 * @author tonychen
 */
@SuppressWarnings("serial")
public class InitWeb3jException extends WeIdBaseException {

    public InitWeb3jException(Throwable cause) {
        super(ErrorCode.LOAD_WEB3J_FAILED.getCodeDesc(), cause);
    }
    
    public InitWeb3jException(String message) {
        super(message);
    }
    
    public InitWeb3jException() {
        super(ErrorCode.LOAD_WEB3J_FAILED.getCodeDesc());
    }
    
    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.LOAD_WEB3J_FAILED;
    }
}
