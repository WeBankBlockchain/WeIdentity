

package com.webank.weid.exception;

import com.webank.weid.blockchain.constant.ErrorCode;

/**
 * Database Exception. Base Exception for WeIdentity Project.
 *
 * @author tonychen
 */
@SuppressWarnings("serial")
public class DatabaseException extends WeIdBaseException {


    /**
     * constructor.
     *
     * @param cause Throwable
     */
    public DatabaseException(Throwable cause) {
        super(ErrorCode.DATA_TYPE_CASE_ERROR.getCodeDesc(), cause);
    }

    /**
     * constructor.
     *
     * @param message message
     */
    public DatabaseException(String message) {
        super(message);
    }
    
    /**
     * get associated error code.
     */
    public ErrorCode getErrorCode() {
        return ErrorCode.DATA_TYPE_CASE_ERROR;
    }
}
