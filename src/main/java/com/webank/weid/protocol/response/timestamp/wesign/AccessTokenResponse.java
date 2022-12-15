

package com.webank.weid.protocol.response.timestamp.wesign;

import lombok.Data;

/**
 * Access Token response.
 *
 * @author darwindu, imported by chaoxinhu, 2019/5/8
 **/
@Data
public class AccessTokenResponse {

    private int code;
    private String msg;
    private String transactionTime;
    private String access_token;
    private String expire_time;
    private int expire_in;
}