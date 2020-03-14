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

package com.webank.weid.suite.auth.inf;

import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.suite.auth.protocol.WeIdAuthObj;

/**
 * @author tonychen 2020年3月13日
 */
public interface WeIdAuthCallback {

    /**
     * you need to implements this method to return your WeIdAuthentication object.
     * @param counterpartyWeId weId of the counterparty
     * @return WeIdAuthentication object
     */
    public WeIdAuthentication onChannelConnecting(String counterpartyWeId);

    /**
     * you need to implements this method, this method will be called after the connection
     * is established..
     * @param arg WeIdAuthObj
     * @return flag
     */
    public Integer onChannelConnected(WeIdAuthObj arg);
}
