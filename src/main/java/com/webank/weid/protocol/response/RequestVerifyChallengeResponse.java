package com.webank.weid.protocol.response;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tonychen 2020年3月12日
 *
 */
@Setter
@Getter
public class RequestVerifyChallengeResponse {

    /**
     * error code.
     */
    private Integer errorCode;

    /**
     * error message.
     */
    protected String errorMessage;
}
