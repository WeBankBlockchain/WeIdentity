

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
         // todo 支持amop callback, 怎么返回result
        String content = new String(data);
        logger.info("received ChannelPush v2 from:{},seq:{},msg:{} ", endpoint, seq, content);
        if (0 == amopCallBackMap.size()) {
            logger.warn("directRouteCallback is null on server side!");
            return;
//            return "directRouteCallback is null on server side!".getBytes();
        }

        AmopRequestBody amopRequestBody = DataToolUtils.deserialize(content, AmopRequestBody.class);
        AmopMsgType msgType = amopRequestBody.getMsgType();
        AmopCallback amopCallBack = amopCallBackMap.get(msgType.getValue());
        if (amopCallBack == null) {
            amopCallBack = defaultAmopCallBack;
        }
        String messageBody = amopRequestBody.getMsgBody();
        String result = null;
        try {
            result = msgType.callOnPush(amopCallBack, null, messageBody);
        } catch (Exception e) {
            logger.error("callOnPush error, please check the log.", e);
        }


        //接收到以后需要给发送端回包 todo
//        return result.getBytes();
    }

}
