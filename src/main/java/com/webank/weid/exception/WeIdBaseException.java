

package com.webank.weid.exception;

import com.webank.weid.blockchain.constant.ErrorCode;

/**
 * WeIdBase Exception. Base Exception for WeIdentity Project.
 *
 * @author tonychen
 */
@SuppressWarnings("serial")
public class WeIdBaseException extends RuntimeException {
    
    private ErrorCode errorCode = ErrorCode.BASE_ERROR;

    /**
     * constructor.
     *
     * @param msg exception message
     * @param cause exception object
     */
    public WeIdBaseException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * constructor.
     *
     * @param msg exception message
     */
    public WeIdBaseException(String msg) {
        super(msg);
    }
    
    /**
     * constructor.
     *
     * @param errorCode the errorCode
     */
    public WeIdBaseException(ErrorCode errorCode) {
        this(errorCode.getCode() + " - " + errorCode.getCodeDesc());
        this.errorCode = errorCode;
    }

    /**
     * get associated error code.
     *
     * @return ErrorCode
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        StringBuilder builder = new StringBuilder();
        builder
            .append(s)
            .append(". Error code = ")
            .append(getErrorCode().getCode())
            .append(", Error message : ")
            .append(getMessage());
        return builder.toString();
    }
}
