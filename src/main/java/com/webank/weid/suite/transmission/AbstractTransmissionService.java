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

/**
 * 请求处理服务, 用于机构间的数据传输服务处理.
 * 
 * @author yanggang
 *
 */
public abstract class AbstractTransmissionService<T> implements TransmissionService<T> {
    
    /**
     * 自动注册服务构造器.
     * 
     * @param serviceType 服务名称
     */
    public AbstractTransmissionService(String serviceType) {
        TransmissionServcieCenter.registerService(serviceType, this);
    }
}
