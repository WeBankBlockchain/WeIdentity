package com.webank.weid.rpc.callback;

import org.bcos.channel.client.ChannelPushCallback;
import org.bcos.channel.dto.ChannelPush;
import org.bcos.channel.dto.ChannelResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DirectRouteMsgType;
import com.webank.weid.protocol.amop.DirectPathRequestBody;
import com.webank.weid.service.impl.callback.DirectRouteCallback;
import com.webank.weid.util.SerializationUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by junqizhang on 08/07/2017.
 */
public class OnNotifyCallback extends ChannelPushCallback {

    private static final Logger logger = LoggerFactory.getLogger(OnNotifyCallback.class);
    @Setter
    @Getter
    protected DirectRouteCallback directRouteCallback = new DirectRouteCallback();

    @Override
    public void onPush(ChannelPush push) {

        if (null == directRouteCallback) {
            ChannelResponse response = new ChannelResponse();
            response.setContent("directRouteCallback is null on server side!");
            response.setErrorCode(0);
            push.sendResponse(response);
            return;
        }
        logger.info("received ChannelPush msg : " + push.getContent());
        DirectPathRequestBody directPathRequestBody = SerializationUtils.deserialize(push.getContent(), DirectPathRequestBody.class);
        DirectRouteMsgType responseMsgType = directPathRequestBody.getMsgType();
//        String errorMsg = "";
//        Class msgBodyClass = directPathRequestBody.getMsgType().getMsgBodyArgsClass();
//        Class msgResultClass = directPathRequestBody.getMsgType().getMsgBodyResultClass();
        DirectRouteMsgType msgType = directPathRequestBody.getMsgType();
        String resultBodyStr = msgType.callOnPush(directRouteCallback, push.getMessageID(), directPathRequestBody.getMsgBody());

        DirectPathRequestBody responseRequestBody = new DirectPathRequestBody();
        responseRequestBody.setMsgBody(resultBodyStr);
        responseRequestBody.setMsgType(responseMsgType);
        String responseRequestBodyStr = SerializationUtils.serialize(responseRequestBody);

        /*
         * 接收到以后需要给发送端回包
         */
        ChannelResponse response = new ChannelResponse();
        response.setContent(responseRequestBodyStr);
        response.setErrorCode(0);
        push.sendResponse(response);
    }
}
