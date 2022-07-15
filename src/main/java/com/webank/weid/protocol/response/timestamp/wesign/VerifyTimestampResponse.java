

package com.webank.weid.protocol.response.timestamp.wesign;

import java.util.Date;

import lombok.Data;

/**
 * Verify timestamp response.
 *
 * @author chaoxinhu
 **/
@Data
public class VerifyTimestampResponse {

    private int code;
    private String msg;
    private String transactionTime;
    private String bizSeqNo;
    private Result result;

    @Data
    public class Result {

        private String bizSeqNo;
        private String transactionTime;
        private TimestampData data;

        @Data
        public class TimestampData {

            private Date signTime;
            private Boolean verifyResult;
        }
    }

}
