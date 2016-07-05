package com.ljzh.gamex.cache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.NotificationScope;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import java.util.List;

public abstract class Cache<K, V> {
    private net.sf.ehcache.Cache cache;

    public Cache(String name) {
        this(name, 20000, 1800, Integer.MAX_VALUE);
    }

    Cache(String name,
                   int maxEntriesLocalHeap,
                   int timeToIdleSeconds,
                   int timeToLiveSeconds) {
        cache = new net.sf.ehcache.Cache(new CacheConfiguration()
                .name(name)
                .eternal(false)
                .maxEntriesLocalHeap(maxEntriesLocalHeap)
                .timeToIdleSeconds(timeToIdleSeconds)
                .timeToLiveSeconds(timeToLiveSeconds)
                .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
                .diskExpiryThreadIntervalSeconds(120));
        cache.getCacheEventNotificationService()
                .registerListener(new Removal(), NotificationScope.LOCAL);
        CacheManager.getInstance().addCache(this);
    }

    public void put(K key, V value) {
        cache.put(new Element(key, value));
    }

    @SuppressWarnings("unchecked")
    public V get(K key) {
        Element element = cache.get(key);
        if (element == null) {
            V value = load(key);
            if (value != null) {
                put(key, value);
            }
            return value;
        }
        return (V) element.getObjectValue();
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public abstract V load(K key);
    public abstract void save(K key, V value);

    public void dumpKeys() {
        System.out.println("<<" + cache.getName() + ">>");
        List keys = cache.getKeysWithExpiryCheck(); //cache.getKeys();
        for (int i = 0, n = keys.size(); i < n; ++i) {
            if (i != 0) {
                System.out.print(" ");
            }

            Object k = keys.get(i);
            System.out.print(k);
        }

        System.out.println();
        System.out.println("size:" + cache.getSize());
        System.out.println("== done ==");
    }

    net.sf.ehcache.Cache getCache() {
        return cache;
    }

    class Removal implements CacheEventListener {
        @Override
        public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
            System.out.println("removed " + element.getObjectKey());
            _save(element);
        }

        @Override
        public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
            //System.out.println("putted " + element.getObjectKey());
        }

        @Override
        public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
            //System.out.println("updated " + element.getObjectKey());
        }

        @Override
        public void notifyElementExpired(Ehcache cache, Element element) {
            System.out.println("expired " + element.getObjectKey());
            _save(element);
        }

        @Override
        public void notifyElementEvicted(Ehcache cache, Element element) {
            System.out.println("evicted " + element.getObjectKey());
            _save(element);
        }

        @Override
        public void notifyRemoveAll(Ehcache cache) {
            //System.out.println("removeAll");
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public void dispose() {
            //System.out.println("dispose");
        }

        @SuppressWarnings("unchecked")
        private void _save(Element element) {
            K key = (K) element.getObjectKey();
            V value = (V) element.getObjectValue();
            save(key, value);
        }
    }
}
