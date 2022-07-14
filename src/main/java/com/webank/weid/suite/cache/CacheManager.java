

package com.webank.weid.suite.cache;

import java.util.Map.Entry;
import java.util.Set;
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
    private static final Integer MAX_SIZE = 1000;
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
        CacheNode<T> node = (CacheNode<T>)cacheNode;
        return node;
    }
    
    /**
     * 失效缓存数据.
     */
    public static void clearAll() {
        Set<Entry<String, CacheNode<Object>>> entrySet = context.entrySet();
        for (Entry<String, CacheNode<Object>> entry : entrySet) {
            entry.getValue().removeAll();
        }
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
