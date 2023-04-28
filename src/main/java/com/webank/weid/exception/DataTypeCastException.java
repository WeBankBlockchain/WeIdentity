

package com.webank.weid.exception;

import com.webank.weid.blockchain.constant.ErrorCode;

/**
 * WeIdBase Exception. Base Exception for WeIdentity Project.
 *
 * @author tonychen
 */
@SuppressWarnings("serial")
public class DataTypeCastException extends WeIdBaseException {


    /**
     * constructor.
     *
     * @param cause Throwable
     */
    public DataTypeCastException(Throwable cause) {
        super(ErrorCode.DATA_TYPE_CASE_ERROR.getCodeDesc(), cause);
    }

    /**
     * constructor.
     *
     * @param message message
     */
    public DataTypeCastException(String message) {
        super(message);
    }
    
    /**
     * get associated error code.
     */
    public ErrorCode getErrorCode() {
        return ErrorCode.DATA_TYPE_CASE_ERROR;
    }
}
