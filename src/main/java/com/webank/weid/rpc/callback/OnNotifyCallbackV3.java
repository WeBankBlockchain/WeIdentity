

package com.webank.weid.rpc.callback;

import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.protocol.amop.AmopRequestBody;
import com.webank.weid.util.DataToolUtils;
import java.util.HashMap;
import java.util.Map;
import org.fisco.bcos.sdk.amop.topic.AmopMsgIn;
import org.fisco.bcos.sdk.jni.amop.AmopRequestCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by junqizhang on 08/07/2017.
 */
/*public class OnNotifyCallbackV2 extends ChannelPushCallback implements RegistCallBack {*/
    // todo 支持amop callback
public class OnNotifyCallbackV3
        implements AmopRequestCallback, RegistCallBack {

    private static final Logger logger = LoggerFactory.getLogger(OnNotifyCallbackV3.class);

    private Map<Integer, AmopCallback> amopCallBackMap = new HashMap<Integer, AmopCallback>();

    private AmopCallback defaultAmopCallBack = new AmopCallback();

    @Override
    public void registAmopCallback(Integer msgType, AmopCallback routeCallBack) {
        amopCallBackMap.put(msgType, routeCallBack);
    }
    
    @Override
    public AmopCallback getAmopCallback(Integer msgType) {
        return amopCallBackMap.get(msgType);
    }


    /**
     * recieve amop request message
     *
     * @param endpoint
     * @param seq
     * @param data
     * @return void
     */
    @Override
    public void onRequest(String endpoint, String seq, byte[] data) {
         // todo amop.response
//        String content = new String(amopMsgIn.getContent());
//        logger.info("received ChannelPush v2 msg : " + content);
//        if (0 == amopCallBackMap.size()) {
//            /*ChannelResponse response = new ChannelResponse();
//            response.setContent("directRouteCallback is null on server side!");
//            response.setErrorCode(0);
//            push.sendResponse(response);
//            return;*/
//            return "directRouteCallback is null on server side!".getBytes();
//        }
//
//        AmopRequestBody amopRequestBody = DataToolUtils.deserialize(content, AmopRequestBody.class);
//            //DataToolUtils.deserialize(push.getContent(), AmopRequestBody.class);
//        AmopMsgType msgType = amopRequestBody.getMsgType();
//        AmopCallback amopCallBack = amopCallBackMap.get(msgType.getValue());
//        if (amopCallBack == null) {
//            amopCallBack = defaultAmopCallBack;
//        }
//        String messageBody = amopRequestBody.getMsgBody();
//        String result = null;
//        try {
//            //result = msgType.callOnPush(amopCallBack, push.getMessageID(), messageBody);
//            result = msgType.callOnPush(amopCallBack, amopMsgIn.getMessageID(), messageBody);
//        } catch (Exception e) {
//            logger.error("callOnPush error, please check the log.", e);
//        }
//
//
//         /* //接收到以后需要给发送端回包
//
//        ChannelResponse response = new ChannelResponse();
//        response.setContent(result);
//        response.setErrorCode(0);
//        push.sendResponse(response);*/
//        return result.getBytes();
    }

}
