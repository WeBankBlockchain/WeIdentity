

package com.webank.weid.protocol.response.timestamp.wesign;

import java.util.List;

import lombok.Data;

/**
 * Sign ticket response.
 *
 * @author darwindu, imported by chaoxinhu, 2019/5/8
 **/
@Data
public class SignTicketResponse {

    private int code;
    private String msg;
    private String transactionTime;
    private List<Ticket> tickets;

    @Data
    public class Ticket {
        private String value;
        private int expire_id;
        private String expire_time;
    }
}