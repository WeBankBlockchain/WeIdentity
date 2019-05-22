package com.webank.weid.protocol.amop;

import lombok.Data;

import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.protocol.inf.IArgs;

@Data
public class AmopRequestBody implements IArgs {

    protected AmopMsgType msgType;

    protected String msgBody;
}
