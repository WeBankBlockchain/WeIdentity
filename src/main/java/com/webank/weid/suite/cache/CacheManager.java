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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;

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

    //默认缓存个数
    private static final  Integer MAX_SIZE = 1000;
    private static final String CACHE_MAXSIZE_KEY = "caffeineCache.maximumSize.";

    /**
     * 根据缓存名获取缓存节点最大缓存个数，如果没有配置则使用默认大小配置.
     * @param cacheKey 缓存名
     * @return
     */
    private static Integer getMaxSize(String cacheName) {
        String maximumSize = PropertyUtils.getProperty(CACHE_MAXSIZE_KEY + cacheName);
        if (StringUtils.isNotBlank(maximumSize)) {
            return Integer.parseInt(maximumSize);
        }
        return MAX_SIZE;
    }
    
    /**
     * 注册缓存节点,如果存在则直接返回,不存在则注册.
     * @param <T> 需要存放的数据类型
     * @param cacheName 缓存名
     * @param timeout 超时时间
     * @return 返回缓存节点
     */
    public static <T> CacheNode<T> registerCacheNode(String cacheName, Long timeout) {
        return registerCacheNode(cacheName, timeout, getMaxSize(cacheName));
    }
    
    /**
     * 注册缓存节点,如果存在则直接返回,不存在则注册.
     * @param <T> 需要存放的数据类型
     * @param cacheName 缓存名
     * @param timeout 超时时间
     * @param maximumSize 最大缓存大小
     * @return 返回缓存节点
     */
    public static <T> CacheNode<T> registerCacheNode(
        String cacheName, 
        Long timeout, 
        Integer maximumSize) {
        
        CacheNode<Object> cacheNode = context.get(cacheName);
        if (cacheNode != null) {
            throw new WeIdBaseException("the cacheName is registed, cacheName= " + cacheName);
        }
        cacheNode = initCache(cacheName, timeout, maximumSize);
        @SuppressWarnings("unchecked")
        CacheNode<T> node = (CacheNode<T>)cacheNode;
        return node;
    }
    
    /**
     * 根据缓存名和超时时间初始化缓存模块.
     * @param cacheName 缓存名
     * @param timeout 超时时间
     * @param maximumSize 缓存项大小
     * @return 返回缓存节点对象
     */
    private static synchronized CacheNode<Object> initCache(
        String cacheName, 
        Long timeout, 
        Integer maximumSize) {
        
        Cache<String, Object> cache = Caffeine.newBuilder()
                .expireAfterWrite(timeout, TimeUnit.MILLISECONDS)
                .maximumSize(maximumSize)
                .build();
        CacheNode<Object> node = new CacheNode<>(cacheName, cache);
        context.put(cacheName, node);
        return node;
    }
}
