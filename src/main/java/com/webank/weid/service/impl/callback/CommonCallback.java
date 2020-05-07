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

package com.webank.weid.service.impl.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ServiceType;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.callback.AmopCallback;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.service.impl.inner.DownTransDataService;
import com.webank.weid.suite.transmission.TransmissionService;
import com.webank.weid.suite.transmission.TransmissionServiceCenter;
import com.webank.weid.util.DataToolUtils;

/**
 * AMOP公共回调处理.
 * @author yanggang
 *
 */
public class CommonCallback extends AmopCallback {

    private static final Logger logger =  LoggerFactory.getLogger(CommonCallback.class);

    //private static WeIdAuth weIdAuthService;

    static {
        // 初始化默认的服务处理
        initDefaultService();
    }

    /*
    private WeIdAuth getweIdAuthService() {
        if (weIdAuthService == null) {
            weIdAuthService = new WeIdAuthImpl();
        }
        return weIdAuthService;
    }
    */
    private static void initDefaultService() {
        register(ServiceType.SYS_GET_TRANS_DATA.name(), new DownTransDataService());
    }

    private static void register(String serviceType, TransmissionService<?> service) {
        TransmissionServiceCenter.registerService(serviceType, service); 
    }

    @Override
    public AmopResponse onPush(AmopCommonArgs arg) {
        logger.info("[CommonCallBack] request param: {}.", arg);
        String serviceType = arg.getServiceType();
        String messageId = arg.getMessageId();
        try {
            TransmissionService<?> service = TransmissionServiceCenter.getService(serviceType);
            if (service == null) {
                logger.error(
                    "[CommonCallBack] no found the service for {}, messageId:{}.", 
                    serviceType,
                    messageId
                ); 
                return super.onPush(arg);
            }
            // 此处暂时不走认证模式
            /*
            String channelId = arg.getChannelId();
            if (StringUtils.isBlank(channelId)) {
                logger.error("[CommonCallBack] the channelId is null, messageId:{}.", messageId);
                return new AmopResponse(ErrorCode.WEID_AUTH_CHANNELID_IS_NULL);
            }
            WeIdAuthObj weIdAuth = getweIdAuthService().getWeIdAuthObjByChannelId(channelId);
            if (weIdAuth == null) {
                logger.error("[CommonCallBack] the channelId is invalid, messageId:{}.", messageId);
                return new AmopResponse(ErrorCode.WEID_AUTH_CHANNELID_INVALID);
            }
            //解密数据
            String message = 
                CryptServiceFactory
                    .getCryptService(CryptType.AES)
                    .decrypt(arg.getMessage(), weIdAuth.getSymmetricKey());
            */
            logger.info("[CommonCallBack] begin request the service, messageId : {}", messageId);
            AmopResponse amopResponse = buildAmopResponse(service.service(arg.getMessage()), arg);
            logger.info("[CommonCallBack] begin encrypt the data, messageId : {}", messageId);
            /*
            // 数据暂时不走认证模式
            //加密响应数据
            String originalData = 
                CryptServiceFactory
                    .getCryptService(CryptType.AES)
                    .encrypt(amopResponse.getResult(), weIdAuth.getSymmetricKey());
            amopResponse.setResult(originalData);
            */
            logger.info("[CommonCallBack] onPush finish and the reponse : {}", amopResponse);
            return amopResponse;
        } catch (WeIdBaseException e) {
            logger.error("[CommonCallBack] callback fail, messageId:{}", messageId, e);
            return new AmopResponse(e.getErrorCode());
        } catch (Exception e) {
            logger.error("[CommonCallBack] has unknow error, messageId:{}.", messageId, e);
            return new AmopResponse(ErrorCode.UNKNOW_ERROR);
        }
    }

    /**
     * 根据业务响应和请求构建AMOP响应.
     * 
     * @param <T> 业务响应实体数据类型
     * @param response 业务响应
     * @param arg AMOP请求
     * @return 返回AMOP响应实体
     */
    public <T> AmopResponse buildAmopResponse(ResponseData<T> response, AmopCommonArgs arg) {
        AmopResponse result = new AmopResponse();
        if (response.getResult() instanceof String) {
            result.setResult((String)response.getResult());
        } else {
            result.setResult(DataToolUtils.serialize(response.getResult()));
        }
        result.setErrorCode(response.getErrorCode());
        result.setMessageId(arg.getMessageId());
        result.setErrorMessage(response.getErrorMessage());
        result.setServiceType(arg.getServiceType());
        return result;
    }
}
