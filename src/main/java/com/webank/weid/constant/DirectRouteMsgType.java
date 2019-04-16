package com.webank.weid.constant;

import com.webank.weid.protocol.amop.CheckDirectRouteMsgHealthArgs;
import com.webank.weid.protocol.response.DirectRouteNotifyMsgResult;
import com.webank.weid.service.impl.callback.DirectRouteCallback;
import com.webank.weid.util.SerializationUtils;

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
            CheckDirectRouteMsgHealthArgs args = SerializationUtils.deserialize(msgBodyStr, CheckDirectRouteMsgHealthArgs.class);
            args.setMessageId(messageId);
            DirectRouteNotifyMsgResult result = directRouteCallback.onPush(args);
            resultBodyStr = SerializationUtils.serialize(result);
        }
        break;
        case TYPE_TRANSPORTATION: {
            CheckDirectRouteMsgHealthArgs args = SerializationUtils.deserialize(msgBodyStr, CheckDirectRouteMsgHealthArgs.class);
            args.setMessageId(messageId);
            DirectRouteNotifyMsgResult result = directRouteCallback.onPush(args);
            resultBodyStr = SerializationUtils.serialize(result);
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
