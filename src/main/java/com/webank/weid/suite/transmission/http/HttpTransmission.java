/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.suite.transmission.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.suite.api.transportation.params.TransmissionType;
import com.webank.weid.suite.transmission.AbstractTransmission;
import com.webank.weid.suite.transmission.Transmission;
import com.webank.weid.suite.transmission.TransmissionlRequest;
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
    public ResponseData<String> send(TransmissionlRequest<?> request) {
        try {
            logger.info(
                "[HttpTransmission.send] this is http transmission and the service type is: {}", 
                request.getServiceType());
            if (request.getTransmissionType() != TransmissionType.HTTP
                &&request.getTransmissionType() != TransmissionType.HTTPS) {
                throw new WeIdBaseException("the transmission type error.");
            }
            super.auth(request);
            Map<String, String> params = new HashMap<String, String>();
            params.put(REQUEST_DATA, super.getData(request));
            params.put(REQUEST_CHANNEL_ID, "");
            String result = null;
            if (request.getTransmissionType() == TransmissionType.HTTP) {
                result = HttpClient.doPost("", params, false);
            } else {
                result = HttpClient.doPost("", params, true);
            }
            return new ResponseData<String>(result, ErrorCode.SUCCESS);
        } catch (WeIdBaseException e) {
            return new ResponseData<String>(StringUtils.EMPTY, e.getErrorCode());
        } catch (Exception e) {
            return new ResponseData<String>(StringUtils.EMPTY, ErrorCode.UNKNOW_ERROR);
        }
    }
}
