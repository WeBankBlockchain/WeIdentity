package com.webank.weid.protocol.amop;


import com.webank.weid.constant.DirectRouteMsgType;
import com.webank.weid.protocol.base.IArgs;

import lombok.Data;

@Data
public class DirectPathRequestBody implements IArgs {

    protected DirectRouteMsgType msgType;

    protected String msgBody;

    protected String message;
}