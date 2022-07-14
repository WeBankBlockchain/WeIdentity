

package com.webank.weid.suite.auth.inf;

import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.suite.auth.protocol.WeIdAuthObj;

/**
 * weIdAuth的回调接口，需要服务端实现，并调用WeIdAuth里的registerCallBack进行注册.
 * @author tonychen 2020年3月13日
 */
public interface WeIdAuthCallback {

    /**
     * 您需要实现该方法，根据传入的weId，返回自己的WeIdAuthentication信息，后续将用于对challenge进行签名.
     *
     * @param counterpartyWeId 对手方的weId
     * @return WeIdAuthentication 对象，包含用户的私钥
     */
    public WeIdAuthentication onChannelConnecting(String counterpartyWeId);

    /**
     * 您需要实现该方法，您可以在该方法里决定如何处理生成的WeIdAuthObj对象.
     *
     * @param arg WeIdAuthObj对象
     * @return flag 执行成功与否的标识
     */
    public Integer onChannelConnected(WeIdAuthObj arg);
}
