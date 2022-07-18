

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
    
    public void remove(String key) {
        cache.invalidate(key);
    }
    
    public void removeAll() {
        cache.invalidateAll();
    }
    
    public String getCacheName() {
        return cacheName;
    }
}
