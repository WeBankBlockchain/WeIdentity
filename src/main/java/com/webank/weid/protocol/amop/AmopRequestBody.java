package com.webank.weid.protocol.amop;


import com.webank.weid.constant.AmopMsgType;

import com.webank.weid.protocol.inf.IArgs;
import lombok.Data;

@Data
public class AmopRequestBody implements IArgs {

    protected AmopMsgType msgType;

    protected String msgBody;
}
