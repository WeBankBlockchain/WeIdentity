/*
 *       Copyright© (2019) WeBank Co., Ltd.
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

package com.webank.weid.suite.cache;

import com.github.benmanes.caffeine.cache.Cache;

/**
 * 缓存节点.
 * @author v_wbgyang
 *
 * @param <T> 节点存放的对象泛型
 */
public class CacheNode<T> {
    
    private Cache<String, T> cache;
    
    private String cacheName;
    
    CacheNode(String cacheName, Cache<String, T> cache) {
        this.cacheName = cacheName;
        this.cache = cache;
    }
    
    public void put(String key, T t) {
        cache.put(key, t);
    }
    
    public T get(String key) {
        return cache.getIfPresent(key);
    }
    
    public String getCacheName() {
        return cacheName;
    }
}
