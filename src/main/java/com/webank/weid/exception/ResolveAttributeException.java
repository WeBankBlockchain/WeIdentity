package com.webank.weid.exception;

/**
 * get weId document, resolve attribute Exception.
 * @author darwindu
 */
@SuppressWarnings("serial")
public class ResolveAttributeException extends RuntimeException {

    private Integer errorCode;
    private String errorMessage;

    /**
     * constructor.
     * @param errorCode exception error code.
     * @param errorMessage exception error messave.
     */
    public ResolveAttributeException(Integer errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
