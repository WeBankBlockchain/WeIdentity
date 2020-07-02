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

package com.webank.weid.suite.transmission.amop;

import com.webank.weid.constant.AmopMsgType;
import com.webank.weid.protocol.response.AmopResponse;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.AmopService;
import com.webank.weid.service.BaseService;
import com.webank.weid.service.impl.AmopServiceImpl;
import com.webank.weid.service.impl.base.AmopCommonArgs;
import com.webank.weid.service.impl.callback.CommonCallback;
import com.webank.weid.suite.transmission.TransmissionServiceCenter;

/**
 * AMOP处理器代理类.
 * 
 * @author yanggang
 *
 */
public class AmopTransmissionProxy extends BaseService {

    private static AmopService amopService;
    
    /**
     * 获取AMOP服务.
     * 
     * @return 返回AMOP服务
     */
    public AmopService getAmopService() {
        if (amopService == null) {
            amopService = new AmopServiceImpl();
        }
        return amopService;
    }

    /**
     * 发送AMOP远程服务.
     * 
     * @param amopCommonArgs AMOP公共请求参数
     * @return 返回AMOP处理结果
     */
    public ResponseData<AmopResponse> send(AmopCommonArgs amopCommonArgs) {
        return getAmopService().send(amopCommonArgs.getToAmopId(), amopCommonArgs);
    }
    
    /**
     * AMOP本地服务.
     * 
     * @param amopCommonArgs AMOP公共请求参数
     * @return 返回处理结果
     */
    public AmopResponse sendLocal(AmopCommonArgs amopCommonArgs) {
        ResponseData<?> response = TransmissionServiceCenter.getService(
            amopCommonArgs.getServiceType()).service(amopCommonArgs.getMessage());
        return ((CommonCallback)super.getPushCallback().getAmopCallback(
            AmopMsgType.COMMON_REQUEST.getValue())).buildAmopResponse(response, amopCommonArgs);
    }
    
    /**
     * 获取当前机构.
     * 
     * @return 返回当前机构名称
     */
    public String getCurrentAmopId() {
        return fiscoConfig.getAmopId();
    }
}
