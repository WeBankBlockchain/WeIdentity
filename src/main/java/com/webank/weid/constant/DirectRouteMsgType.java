/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
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

package com.webank.weid.constant;

import com.webank.weid.protocol.amop.CheckDirectRouteMsgHealthArgs;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.DirectRouteNotifyMsgResult;
import com.webank.weid.rpc.callback.DirectRouteCallback;
import com.webank.weid.util.DataToolUtils;

/**
 * Created by junqizhang on 12/06/2017.
 */
public enum DirectRouteMsgType {

    TYPE_ERROR(0),

    /*
     * 链上链下check health
     */
    TYPE_CHECK_DIRECT_ROUTE_MSG_HEALTH(1),
	
	/*
	 * 
	 */
	TYPE_TRANSPORTATION(2);

	
    private Integer value;

    private DirectRouteMsgType(Integer index) {
        this.value = index;
    }

    public Integer getValue() {
        return this.value;
    }

    public Class getMsgBodyArgsClass() {

        switch (this) {
        case TYPE_CHECK_DIRECT_ROUTE_MSG_HEALTH:
            return CheckDirectRouteMsgHealthArgs.class;
            default:
                break;
        }

        return null;
    }

    public Class getMsgBodyResultClass() {

        switch (this) {
        case TYPE_CHECK_DIRECT_ROUTE_MSG_HEALTH:
            return DirectRouteNotifyMsgResult.class;
            default:
                return DirectRouteNotifyMsgResult.class;
        }
    }

    public String callOnPush(DirectRouteCallback directRouteCallback, String messageId, String msgBodyStr) {

        String resultBodyStr = null;

        switch (this) {
        case TYPE_CHECK_DIRECT_ROUTE_MSG_HEALTH: {
            CheckDirectRouteMsgHealthArgs args = DataToolUtils.deserialize(msgBodyStr, CheckDirectRouteMsgHealthArgs.class);
            args.setMessageId(messageId);
            DirectRouteNotifyMsgResult result = directRouteCallback.onPush(args);
            resultBodyStr = DataToolUtils.serialize(result);
        }
        break;
        case TYPE_TRANSPORTATION: {
        	//1.GET key
        	
            CheckDirectRouteMsgHealthArgs args = DataToolUtils.deserialize(msgBodyStr, CheckDirectRouteMsgHealthArgs.class);
            args.setMessageId(messageId);
            DirectRouteNotifyMsgResult result = directRouteCallback.onPush(args);
            resultBodyStr = DataToolUtils.serialize(result);
        }
        break;
            default:
//                throw
//                logger.error("no handle msg type : " + push.getContent());
                break;
        }

        return resultBodyStr;
    }
}
