/*
 * Copyright© (2019) WeBank Co., Ltd.
 *
 * This file is part of weid-java-sdk.
 *
 * weid-java-sdk is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * weid-java-sdk is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * weid-java-sdk. If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.constant;

/**
 * 系统缓存枚举.
 * @author v_wbgyang
 *
 */
public enum WeIdCacheName {

    CPT("CPT", 0L);
    
    private long timeout;
    private String cacheName;
    
    WeIdCacheName(String cacheName, long timeout) {
        this.cacheName = cacheName;
        this.timeout = timeout;
    }
    
    public long getTimeout() {
        return timeout;
    }
    
    public String getCacheName() {
        return cacheName;
    }
    
    /**
     * 根据缓存名获取缓存名枚举对象.
     * @param cacheName 缓存名
     * @return 返回系统缓存枚举对象
     */
    public static WeIdCacheName getWeIdCacheName(String cacheName) {
        for (WeIdCacheName value : WeIdCacheName.values()) {
            if (value.getCacheName().equals(cacheName)) {
                return value;
            }
        }
        return null;
    }
}
