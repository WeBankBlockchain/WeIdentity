package com.webank.weid.protocol.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tonychen 2020年4月4日
 */
@Getter
@Setter
public class TransactionArgs {


    private String requestId;

    private String method;

    private String[] args;

    private String extra;

    private Long timeStamp;
}
