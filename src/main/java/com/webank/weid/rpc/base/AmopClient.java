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

package com.webank.weid.rpc.base;

import com.webank.weid.protocol.amop.CheckDirectRouteMsgHealthArgs;
import com.webank.weid.protocol.amop.PresentationEncryptKeyArgs;
import com.webank.weid.protocol.amop.PresentationPolicyArgs;
import com.webank.weid.protocol.response.DirectRouteNotifyMsgResult;
import com.webank.weid.protocol.response.PresentationEncryptKeyResult;
import com.webank.weid.protocol.response.PresentationPolicyResult;
import com.webank.weid.protocol.response.ResponseData;

/**
 * @author tonychen 2019年4月16日
 */
public interface AmopClient {


    /**
     * 链上链下check heath接口
     *
     * @param toOrgId
     * @param arg
     * @return
     */
    ResponseData<DirectRouteNotifyMsgResult> checkDirectRouteMsgHealth(String toOrgId, CheckDirectRouteMsgHealthArgs arg);
    
    
    /**
     * 链上链下发消息的通用接口
     *
     * @param toOrgId
     * @param arg
     * @return
     */
    ResponseData<PresentationPolicyResult> requestPresentationPolicy(String toOrgId,  PresentationPolicyArgs presentationPolicyArg);
    
    /**
     * 链上链下发消息的通用接口
     *
     * @param toOrgId
     * @param arg
     * @return
     */
    ResponseData<PresentationEncryptKeyResult> requestPresentationEncryptKey(String toOrgId, PresentationEncryptKeyArgs arg);
}
