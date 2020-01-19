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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;

import com.webank.weid.constant.WeIdCacheName;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.util.PropertyUtils;


/**
 * 缓存管理器.
 * @author v_wbgyang
 *
 */
public class CacheManager {

    //全局缓存上下文
    private static final ConcurrentHashMap<String, CacheNode<Object>> context = 
        new ConcurrentHashMap<String, CacheNode<Object>>();
    //默认超时时间 默认为 30分钟
    private static final long DEFAULT_TIMEOUT = 1000 * 60 * 30;
    
    private static final String KEY_SPLIT_CHAR = ",";
    private static final String CACHE_NAMES_KEY = "caffeineCache.names";
    private static final String CACHE_TIMEOUT_KEY = "caffeineCache.timeout.";
    
    static {
        init();
    }
    
    /**
     * 初始化缓存管理器.
     */
    private static void init() {
        Set<String> cacheNameSet = analyzeCacheName();
        initSystemCacheName(cacheNameSet);
        for (String cacheName : cacheNameSet) {
            initCache(cacheName, getTimeout(cacheName));
        }
    }
    
    /**
     * 根据缓存名获取超时时间，如果没有配置则检查是否为系统缓存模块，如果是并且配置了超时时间，
     * 则使用配置超时时间，否则使用默认超时时间.
     * @param cacheKey 缓存名
     * @return
     */
    private static long getTimeout(String cacheName) {
        String timeout = PropertyUtils.getProperty(CACHE_TIMEOUT_KEY + cacheName);
        if (StringUtils.isNotBlank(timeout)) {
            return Long.parseLong(timeout);
        }
        WeIdCacheName weIdCacheName = WeIdCacheName.getWeIdCacheName(cacheName);
        if (weIdCacheName != null && weIdCacheName.getTimeout() != 0L) {
            return weIdCacheName.getTimeout();
        }
        return DEFAULT_TIMEOUT;
    }
    
    /**
     * 获取缓存节点. 如果不存在改缓存节点，则抛异常.
     * @param <T> 缓存节点存放的数据类型泛型
     * @param cacheName 缓存名
     * @return 返回缓存节点
     */
    public static <T> CacheNode<T> getCache(String cacheName) {
        CacheNode<Object> cacheNode = context.get(cacheName);
        if (cacheNode == null) {
            throw new WeIdBaseException("can't find the cache, cacheName = " + cacheName);
        }
        @SuppressWarnings("unchecked")
        CacheNode<T> node = (CacheNode<T>)cacheNode;
        return node;
    }
    
    /**
     * 根据缓存名和超时时间初始化缓存模块.
     * @param cacheName 缓存名
     * @param timeout 超时时间
     * @return 返回缓存节点对象
     */
    private static synchronized CacheNode<Object> initCache(String cacheName, long timeout) {
        Cache<String, Object> cache = Caffeine.newBuilder()
                .expireAfterWrite(timeout, TimeUnit.MILLISECONDS)
                .build();
        CacheNode<Object> node = new CacheNode<>(cacheName, cache);
        context.put(cacheName, node);
        return node;
    }
    
    /**
     * 解析配置的cacheName集合.
     * @return 返回cacheName集合
     */
    private static Set<String> analyzeCacheName() {
        String keyStr = PropertyUtils.getProperty(CACHE_NAMES_KEY);
        Set<String> cacheNameSet = new HashSet<>();
        if (StringUtils.isNotBlank(keyStr)) {
            String[] keys = keyStr.split(KEY_SPLIT_CHAR);
            for (String key : keys) {
                cacheNameSet.add(key);
            }  
        }
        return cacheNameSet;
    }
    
    /**
     * 初始系统默认的缓存模块.
     * @param cacheNameSet 缓存模块名集合
     */
    private static void initSystemCacheName(Set<String> cacheNameSet) {
        for (WeIdCacheName value : WeIdCacheName.values()) {
            cacheNameSet.add(value.getCacheName());
        }
    }
}
