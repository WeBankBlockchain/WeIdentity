/*
 *       Copyright© (2018) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.service.impl.callback;

//import cn.webank.blockchain.api.accounting.callback.ClearingBankNotifyCallback;
//import cn.webank.blockchain.constants.ErrorCode;
//import cn.webank.blockchain.impl.payment.PushNotifyAllCallback;
//import cn.webank.blockchain.protocol.*;
//import cn.webank.blockchain.result.DirectRouteNotifyMsgResult;
//import cn.webank.blockchain.result.RecordAccountsPayableResult;
//import cn.webank.common.conf.Config;

import com.webank.weid.protocol.amop.CheckDirectRouteMsgHealthArgs;
import com.webank.weid.protocol.response.DirectRouteNotifyMsgResult;
import com.webank.weid.rpc.callback.PushNotifyAllCallback;

/**
 * Created by junqizhang on 08/07/2017.
 * 业务方需要继承DirectRouteCallback，并实现需要实现的方法。
 */
public class DirectRouteCallback implements PushNotifyAllCallback {

    static private final String MSG_HEALTH = "I am alive!";
    static private final String ERROR_MSG_NO_OVERRIDE = "server side have not handle this type of message!";

    public DirectRouteNotifyMsgResult onPush(CheckDirectRouteMsgHealthArgs arg) {
        return null;
    }
}
