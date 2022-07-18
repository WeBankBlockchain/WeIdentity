

package com.webank.weid.protocol.response;

import lombok.Data;

import com.webank.weid.constant.ErrorCode;

/**
 * The internal base response result class.
 *
 * @param <T> the generic type
 * @author tonychen 2018.9.29
 */
@Data
public class EngineResultData<T> {

    /**
     * The generic type result object.
     */
    private T result;

    /**
     * The error code.
     */
    private Integer errorCode;

    /**
     * The error message.
     */
    private String errorMessage;

    /**
     * The blockchain transaction info. Note that this transaction only becomes valid (not null nor
     * blank) when a successful transaction is sent to chain with a block generated.
     */
    private TransactionInfo transactionInfo = null;

    /**
     * Instantiates a new response data.
     */
    public EngineResultData() {
        this.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * Instantiates a new response data. Transaction info is left null to avoid unnecessary boxing.
     *
     * @param result the result
     * @param errorCode the return code
     */
    public EngineResultData(T result, ErrorCode errorCode) {
        this.result = result;
        if (errorCode != null) {
            this.errorCode = errorCode.getCode();
            this.errorMessage = errorCode.getCodeDesc();
        }
    }

    /**
     * Instantiates a new response data with transaction info.
     *
     * @param result the result
     * @param errorCode the return code
     * @param transactionInfo transactionInfo
     */
    public EngineResultData(T result, ErrorCode errorCode, TransactionInfo transactionInfo) {
        this.result = result;
        if (errorCode != null) {
            this.errorCode = errorCode.getCode();
            this.errorMessage = errorCode.getCodeDesc();
        }
        if (transactionInfo != null) {
            this.transactionInfo = transactionInfo;
        }
    }

    /**
     * set a ErrorCode type errorCode.
     * 
     * @param errorCode the errorCode
     */
    public void setErrorCode(ErrorCode errorCode) {
        if (errorCode != null) {
            this.errorCode = errorCode.getCode();
            this.errorMessage = errorCode.getCodeDesc();
        }
    }

    /**
     * Instantiates a new Response data based on the error code and error message.
     * 
     * @param result the result
     * @param errorCode code number
     * @param errorMessage errorMessage
     */
    public EngineResultData(T result, Integer errorCode, String errorMessage) {
        this.result = result;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
