


package com.webank.weid.suite.auth.protocol;

import lombok.Getter;
import lombok.Setter;

/**
 * Client and Server can communicate each other over an end-to-end encryption channel.
 * Created by Junqi Zhang on 2020/3/8.
 */
@Setter
@Getter
public class WeIdAuthObj {

    /**
     * 建立weIdAuth的通道ID.
     */
    private String channelId;

    /**
     * 对称秘钥，由服务端生成.
     */
    private String symmetricKey;

    /**
     * 您自己的weId.
     */
    private String selfWeId;

    /**
     * 对手方的weId.
     */
    private String counterpartyWeId;
}
