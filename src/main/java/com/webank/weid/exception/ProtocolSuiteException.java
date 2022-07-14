

package com.webank.weid.exception;

import com.webank.weid.constant.ErrorCode;

/**
 * 协议处理异常.
 * @author v_wbgyang
 *
 */
public class ProtocolSuiteException extends WeIdBaseException {

    private static final long serialVersionUID = 1L;
    
    private ErrorCode errorCode;

    public ProtocolSuiteException(ErrorCode errorCode) {
        super(errorCode.getCodeDesc());
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
