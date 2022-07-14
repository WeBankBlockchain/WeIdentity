

package com.webank.weid.exception;

import com.webank.weid.constant.ErrorCode;

/**
 * 编解码处理异常.
 * @author v_wbgyang
 *
 */
public class EncodeSuiteException extends WeIdBaseException {

    private static final long serialVersionUID = 1L;

    private ErrorCode errorCode = ErrorCode.TRANSPORTATION_ENCODE_BASE_ERROR;
    
    public EncodeSuiteException(Throwable e) {
        super(ErrorCode.TRANSPORTATION_ENCODE_BASE_ERROR.getCodeDesc(), e);
    }
    
    public EncodeSuiteException() {
        super(ErrorCode.TRANSPORTATION_ENCODE_BASE_ERROR.getCodeDesc()); 
    }
    
    public EncodeSuiteException(ErrorCode errorCode) {
        super(errorCode.getCodeDesc());
        this.errorCode = errorCode;
    }
    
    public EncodeSuiteException(String message) {
        super(message);
    }
    
    public EncodeSuiteException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
