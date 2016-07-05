package com.ljzh.gamex.cache;

public class CacheManager {
    private static final CacheManager I = new CacheManager();
    private CacheManager() {
        cacheManager = net.sf.ehcache.CacheManager.newInstance();
    }
    public static CacheManager getInstance() {
        return I;
    }

    private net.sf.ehcache.CacheManager cacheManager;

    public void addCache(Cache cache) {
        cacheManager.addCache(cache.getCache());
    }

    public void shutdown() {
        cacheManager.shutdown();
    }
}
