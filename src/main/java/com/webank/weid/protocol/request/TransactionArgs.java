package com.webank.weid.protocol.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 离线交易的参数.
 *
 * @author tonychen 2020年4月4日
 */
@Getter
@Setter
public class TransactionArgs {

    /**
     * 每条请求对应的ID，唯一标识.
     */
    private String requestId;

    /**
     * 交易上链需要调用的方法名.
     */
    private String method;

    /**
     * 交易参数.
     */
    private String args;

    /**
     * 交易额外的信息.
     */
    private String extra;

    /**
     * 交易发生时的时间戳.
     */
    private Long timeStamp;

    /**
     * 批次，目前是按天.
     */
    private String batch;
}
