

package com.webank.weid.protocol.response;

import lombok.Data;

import com.webank.weid.protocol.inf.IResult;

/**
 * Created by junqizhang on 15/08/2017.
 */
@Data
public class AmopNotifyMsgResult implements IResult {

    /*
     * 错误信息
     */
    protected String message;
    /*
     * 错误码：返回0表明成功收到通知；其他表明异常情况.
     */
    private Integer errorCode;
}
