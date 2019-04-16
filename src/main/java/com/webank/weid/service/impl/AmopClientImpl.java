package com.webank.weid.service.impl;

import org.bcos.channel.dto.ChannelRequest;
import org.bcos.channel.dto.ChannelResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DirectRouteMsgType;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.amop.CheckDirectRouteMsgHealthArgs;
import com.webank.weid.protocol.amop.DirectPathRequestBody;
import com.webank.weid.protocol.amop.DirectRouteBaseMsgArgs;
import com.webank.weid.protocol.response.DirectRouteNotifyMsgResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.base.AmopClient;
import com.webank.weid.service.BaseService;
import com.webank.weid.util.PropertyUtils;
import com.webank.weid.util.SerializationUtils;

/**
 * @author tonychen 2019年4月15日
 *
 */
public class AmopClientImpl extends BaseService implements AmopClient  {
	
	
	private static final Logger logger = LoggerFactory.getLogger(AmopClientImpl.class);
	 /*
     * 链上链下最大超时时间
     * unit : millisecond
     */
    public static final int MAX_DIRECT_ROUTE_REQUEST_TIMEOUT = 50000;
    
    public static final int DEFAULT_DIRECT_ROUTE_REQUEST_TIMEOUT = 5000;
    

	/* (non-Javadoc)
	 * @see com.webank.weid.rpc.base.BaseClient#registerCallback(com.webank.weid.service.impl.callback.DirectRouteCallback)
	 */

	/* (non-Javadoc)
	 * @see com.webank.weid.rpc.base.BaseClient#checkDirectRouteMsgHealth(java.lang.String, com.webank.weid.protocol.amop.CheckDirectRouteMsgHealthArgs)
	 */
	@Override
	public ResponseData<DirectRouteNotifyMsgResult> checkDirectRouteMsgHealth(String toOrgId,
			CheckDirectRouteMsgHealthArgs arg) {
		
		String fromOrgId = PropertyUtils.getProperty("blockchain.orgId");
		
        return this.getImpl(
                fromOrgId,
                toOrgId,
                arg,
                CheckDirectRouteMsgHealthArgs.class,
                DirectRouteNotifyMsgResult.class,
                DirectRouteMsgType.TYPE_CHECK_DIRECT_ROUTE_MSG_HEALTH,
                DEFAULT_DIRECT_ROUTE_REQUEST_TIMEOUT
        );
	}

	protected <T, F extends DirectRouteBaseMsgArgs> ResponseData<T> getImpl(
            String fromOrgId,
            String toOrgId,
            F arg,
            Class<F> argsClass,
            Class<T> resultClass,
            DirectRouteMsgType msgType,
            int timeOut
    ) {
        
        arg.setFromOrgId(fromOrgId);
        arg.setToOrgId(toOrgId);

        ChannelRequest request = new ChannelRequest();
        if (timeOut > MAX_DIRECT_ROUTE_REQUEST_TIMEOUT || timeOut < 0) {
        	request.setTimeout(timeOut);
//            timeOut = MAX_DIRECT_ROUTE_REQUEST_TIMEOUT;
            logger.error("invalid timeOut : {}", timeOut);
        }
        request.setToTopic(toOrgId);
        request.setMessageID(getSeq());

//        String msgBody = jsonMapper.toJson(arg);
        String msgBody = SerializationUtils.serialize(arg);
        DirectPathRequestBody directPathRequestBody = new DirectPathRequestBody();
        directPathRequestBody.setMsgType(msgType);
        directPathRequestBody.setMsgBody(msgBody);
        String wePopDirectPathRequestBodyStr = SerializationUtils.serialize(arg);
        logger.info("direct route request, seq : {}, body ：{}", request.getMessageID(), wePopDirectPathRequestBodyStr);
        request.setContent(wePopDirectPathRequestBodyStr);

        ChannelResponse response = getService().sendChannelMessage2(request);
        logger.info("direct route response, seq : {}, errorCode : {}, body : {}",
                response.getMessageID(),
                response.getErrorCode(),
                response.getContent()
        );
        ResponseData<T> responseStruct = new ResponseData<>();

        responseStruct.setErrorCode(ErrorCode.getTypeByErrorCode(response.getErrorCode()));
        if (102 == response.getErrorCode()) {
        	responseStruct.setErrorCode(ErrorCode.DIRECT_ROUTE_REQUEST_TIMEOUT);
//            return responseStruct;
        } else if (0 != response.getErrorCode()) {
            responseStruct.setErrorCode(ErrorCode.DIRECT_ROUTE_MSG_BASE_ERROR);
            return responseStruct;
        }
//        T msgBodyObj = DirectRouteBodyParser.deserialize(response.getContent(), resultClass);
        T msgBodyObj = SerializationUtils.deserialize(response.getContent(), resultClass);
        if (null == msgBodyObj) {
        	responseStruct.setErrorCode(ErrorCode.UNKNOW_ERROR);
		}
		responseStruct.setResult(msgBodyObj);
		return responseStruct;
    }
	

	
}
