package com.webank.weid.suite.auth.inf;

import com.webank.weid.protocol.base.Challenge;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.callback.AmopCallback;
import com.webank.weid.suite.auth.protocol.WeIdAuthObj;
import java.util.List;

/**
 * Created by Junqi Zhang on 2020/3/8.
 */
public class WeIdAuth {

    /**
     * 每次调用都是覆盖rule
     *
     * @param whitelistWeId
     * @param blacklistWeId
     * @return
     */
    public Integer setRule(List<String> whitelistWeId, List<String> blacklistWeId);

    /**
     * 如果使用amop需要传入orgId; 如果使用https，则传入url
     *
     * @param counterpartyWeId
     * @return
     */
    public ResponseData<WeIdAuthObj> createAuthenticatedChannel(String counterpartyWeId, WeIdAuthentication weIdAuthentication);

    public ResponseData<WeIdAuthObj> createMutualAuthenticatedChannel(String counterpartyWeId, WeIdAuthentication weIdAuthentication);

    public Integer onChannelConnecting(String counterpartyWeId);

    public Integer onChannelConnected(WeIdAuthObj arg);

//    void registerCallback(Integer directRouteMsgType, AmopCallback directRouteCallback);
}
