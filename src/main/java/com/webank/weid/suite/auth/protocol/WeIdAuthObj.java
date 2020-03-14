/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */


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
     * 'channelId' is the ID for that channel.
     */
    private String channelId;

    /**
     * 'symmetricKey' is the symmetric key of that channel.
     */
    private String symmetricKey;

    /**
     * WeID of the youself.
     */
    private String selfWeId;

    /**
     * WeID of the counterparty.
     */
    private String counterpartyWeId;
}
