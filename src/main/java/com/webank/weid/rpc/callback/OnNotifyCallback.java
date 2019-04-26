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

package com.webank.weid.rpc.callback;

import org.bcos.channel.client.ChannelPushCallback;
import org.bcos.channel.dto.ChannelPush;
import org.bcos.channel.dto.ChannelResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DirectRouteMsgType;
import com.webank.weid.protocol.amop.DirectPathRequestBody;
import com.webank.weid.util.DataToolUtils;

/**
 * Created by junqizhang on 08/07/2017.
 */
public class OnNotifyCallback extends ChannelPushCallback {

    private static final Logger logger = LoggerFactory.getLogger(OnNotifyCallback.class);
    
    private DirectRouteCallback directRouteCallBack = new DirectRouteCallback();

    public void RegistRouteCallBackMap(DirectRouteMsgType msgType, DirectRouteCallback routeCallBack) {
    	directRouteCallBack =  routeCallBack;
    }
    @Override
    public void onPush(ChannelPush push) {
    	
        if (null == directRouteCallBack) {
            ChannelResponse response = new ChannelResponse();
            response.setContent("directRouteCallback is null on server side!");
            response.setErrorCode(0);
            push.sendResponse(response);
            return;
        }
        logger.info("received ChannelPush msg : " + push.getContent());
        DirectPathRequestBody directPathRequestBody = DataToolUtils.deserialize(push.getContent(), DirectPathRequestBody.class);
        DirectRouteMsgType responseMsgType = directPathRequestBody.getMsgType();
        DirectRouteMsgType msgType = directPathRequestBody.getMsgType();
        String resultBodyStr = msgType.callOnPush(directRouteCallBack, push.getMessageID(), directPathRequestBody.getMsgBody());

        DirectPathRequestBody responseRequestBody = new DirectPathRequestBody();
        responseRequestBody.setMsgBody(resultBodyStr);
        responseRequestBody.setMsgType(responseMsgType);
        String responseRequestBodyStr = DataToolUtils.serialize(responseRequestBody);

        /*
         * 接收到以后需要给发送端回包
         */
        ChannelResponse response = new ChannelResponse();
        response.setContent(responseRequestBodyStr);
        response.setErrorCode(0);
        push.sendResponse(response);
    }
}
