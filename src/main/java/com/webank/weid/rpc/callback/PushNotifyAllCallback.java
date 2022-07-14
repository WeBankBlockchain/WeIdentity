

package com.webank.weid.rpc.callback;

import com.webank.weid.protocol.amop.CheckAmopMsgHealthArgs;
import com.webank.weid.protocol.response.AmopNotifyMsgResult;

/**
 * Created by junqizhang on 17/5/24.
 */
public interface PushNotifyAllCallback {


    /**
     * 链上链下health check, 不需要覆盖实现.
     *
     * @param arg echo arg
     * @return amopNotifyMsgResult
     */
    AmopNotifyMsgResult onPush(CheckAmopMsgHealthArgs arg);
}
