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

package com.webank.weid.suite.transmission;

import com.webank.weid.util.DataToolUtils;

/**
 * 传输公共处理类.
 * 
 * @author yanggang
 *
 */
public abstract class AbstractTransmission implements Transmission {

    /**
     * 认证处理.
     * 
     * @param <T> 请求实例中具体数据类型
     * @param request 用于做weAuth验证的用户身份信息
     */
    protected <T> void auth(TransmissionlRequest<T> request) {
       
    }
    
    /**
     * 获取传递数据.
     * 
     * @param <T> 请求实例类型
     * @param request 请求实例
     * @return 返回处理后的数据
     */
    protected <T> String getData(TransmissionlRequest<T> request) {
        return DataToolUtils.serialize(request.getArgs());
    }
}
