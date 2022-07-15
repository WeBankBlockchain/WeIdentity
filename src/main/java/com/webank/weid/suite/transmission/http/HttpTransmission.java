

package com.webank.weid.suite.transmission.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.transportation.params.TransType;
import com.webank.weid.suite.transmission.AbstractTransmission;
import com.webank.weid.suite.transmission.Transmission;
import com.webank.weid.suite.transmission.TransmissionRequest;
import com.webank.weid.util.HttpClient;

/**
 * HTTP传输处理器.
 * 
 * @author yanggang
 *
 */
public class HttpTransmission extends AbstractTransmission implements Transmission {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpTransmission.class);
    
    private static final String REQUEST_DATA = "data";
    private static final String REQUEST_CHANNEL_ID = "channelId";

    @Override
    public ResponseData<String> send(TransmissionRequest<?> request) {
        try {
            logger.info(
                "[HttpTransmission.send] this is http transmission and the service type is: {}", 
                request.getServiceType());
            if (request.getTransType() != TransType.HTTP
                && request.getTransType() != TransType.HTTPS) {
                throw new WeIdBaseException("the transmission type error.");
            }
            TransmissionlRequestWarp<?> requestWarp = super.authTransmission(request);
            Map<String, String> params = new HashMap<String, String>();
            params.put(REQUEST_DATA, requestWarp.getEncodeData());
            params.put(REQUEST_CHANNEL_ID, requestWarp.getWeIdAuthObj().getChannelId());
            String result = null;
            if (request.getTransType() == TransType.HTTP) {
                result = HttpClient.doPost("", params, false);
            } else {
                result = HttpClient.doPost("", params, true);
            }
            return new ResponseData<String>(result, ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            logger.error("[HttpTransmission.send] send http fail.", e);
            return new ResponseData<String>(StringUtils.EMPTY, e.getErrorCode());
        } catch (Exception e) {
            logger.error("[HttpTransmission.send] send http due to unknown error.", e);
            return new ResponseData<String>(StringUtils.EMPTY, ErrorCode.UNKNOW_ERROR);
        }
    }
}
