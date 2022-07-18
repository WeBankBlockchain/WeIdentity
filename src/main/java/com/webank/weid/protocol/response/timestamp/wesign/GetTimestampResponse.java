

package com.webank.weid.protocol.response.timestamp.wesign;

import lombok.Data;

/**
 * Get timestamp response.
 *
 * @author chaoxinhu
 **/
@Data
public class GetTimestampResponse {

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

            private String b64TimeStamp;
        }
    }

}
