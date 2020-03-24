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
