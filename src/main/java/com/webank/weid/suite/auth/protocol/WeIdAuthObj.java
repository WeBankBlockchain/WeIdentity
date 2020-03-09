package com.webank.weid.suite.auth.protocol;

/**
 * Created by Junqi Zhang on 2020/3/8.
 *
 * Client and Server can communicate each other over an end-to-end encryption channel.
 */
public class WeIdAuthObj {
    /**
     * 'channelId' is the ID for that channel.
     */
    Integer channelId;

    /**
     * 'symmetricKey' is the symmetric key of that channel.
     */
    String symmetricKey;

    /**
     * WeID of the youself.
     */
    String selfWeId;

    /**
     * WeID of the counterparty.
     */
    String counterpartyWeId;
}
