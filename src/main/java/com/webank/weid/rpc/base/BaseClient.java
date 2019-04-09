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

package com.webank.weid.rpc.base;

import com.webank.weid.protocol.amop.CheckDirectRouteMsgHealthArgs;
import com.webank.weid.protocol.response.DirectRouteNotifyMsgResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.callback.DirectRouteCallback;

/**
 * Created by Junqi Zhang on 2019/4/10.
 */
public interface BaseClient {

    /**
     * 注册处理来自其他机构的通知消息的回调函数
     * 需要实现一个类，继承DirectRouteCallback，并实现对应的几个onPush函数
     */
    void registerCallback(DirectRouteCallback directRouteCallback);

    /**
     * 链上链下check heath接口
     *
     * @param toOrgId
     * @param arg
     * @return
     */
    ResponseData<DirectRouteNotifyMsgResult> checkDirectRouteMsgHealth(String toOrgId, CheckDirectRouteMsgHealthArgs arg);
}
