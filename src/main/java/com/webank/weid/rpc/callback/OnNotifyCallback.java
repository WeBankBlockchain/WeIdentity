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

import java.util.HashMap;
import java.util.Map;

import org.bcos.channel.client.ChannelPushCallback;
import org.bcos.channel.dto.ChannelPush;
import org.bcos.channel.dto.ChannelResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.protocol.amop.AmopRequestBody;
import com.webank.weid.util.DataToolUtils;

/**
 * Created by junqizhang on 08/07/2017.
 */
public class OnNotifyCallback extends ChannelPushCallback implements RegistCallBack {

    private static final Logger logger = LoggerFactory.getLogger(OnNotifyCallback.class);
    
    private Map<Integer, AmopCallback> amopCallBackMap = new HashMap<Integer, AmopCallback>();

    public void registAmopCallback(Integer msgType, AmopCallback routeCallBack) {

        amopCallBackMap.put(msgType, routeCallBack);
    }
    
    @Override
    public void onPush(ChannelPush push) {

        if (0 == amopCallBackMap.size()) {
            ChannelResponse response = new ChannelResponse();
            response.setContent("directRouteCallback is null on server side!");
            response.setErrorCode(0);
            push.sendResponse(response);
            return;
        }
        
        logger.info("received ChannelPush msg : " + push.getContent());
        AmopRequestBody amopRequestBody = 
            DataToolUtils.deserialize(push.getContent(), AmopRequestBody.class);
        AmopMsgType msgType = amopRequestBody.getMsgType();
        AmopCallback amopCallBack = amopCallBackMap.get(msgType.getValue());
        String messageBody = amopRequestBody.getMsgBody();
        String result = msgType.callOnPush(amopCallBack, push.getMessageID(), messageBody);
        /*
         * 接收到以后需要给发送端回包
         */
        ChannelResponse response = new ChannelResponse();
        response.setContent(result);
        response.setErrorCode(0);
        push.sendResponse(response);
    }
}
