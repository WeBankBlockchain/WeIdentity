

package com.webank.weid.protocol.response;

import lombok.Data;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.inf.IResult;

/**
 * the AMOP response.
 * @author tonychen 2019.04.16
 */
@Data
public class AmopResponse implements IResult {

    /**
     * 返回的消息.
     */
    private String result;

    /**
     * 业务类型.
     */
    protected String serviceType;

    /**
     * 错误码.
     */
    private Integer errorCode;

    /**
     * 错误信息.
     */
    protected String errorMessage;

    /**
     * 消息编号.
     */
    protected String messageId;

    /**
     * 无参构造器.
     */
    public AmopResponse() {
        super();
    }

    /**
     * ErrorCode造器.
     * @param errorCode 错误码
     */
    public AmopResponse(ErrorCode errorCode) {
        this();
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getCodeDesc();
    }
}
