


package com.webank.weid.protocol.amop;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.webank.weid.protocol.amop.base.AmopBaseMsgArgs;

/**
 * Created by junqizhang on 08/07/2017.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CheckAmopMsgHealthArgs extends AmopBaseMsgArgs {

    /*
     * 任意包体
     */
    private String message;
}
