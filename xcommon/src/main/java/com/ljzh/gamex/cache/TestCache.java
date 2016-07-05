package com.ljzh.gamex.cache;

import java.util.concurrent.TimeUnit;

public class TestCache {
    public static void main(String[] args) throws InterruptedException {
        testIdleTime();
        //testLiveTime();
        //testRemoval();
        //testEvict();
        CacheManager.getInstance().shutdown();
    }

    private static void testIdleTime() throws InterruptedException {
        int timeToIdleSeconds = 5;
        Cache1 c = new Cache1("c1", 10, timeToIdleSeconds, Integer.MAX_VALUE);
        for (int i = 0; i < 10; ++i) {
            c.put("k" + i, new CachedObj("v" + i));
            TimeUnit.MICROSECONDS.sleep(10); // Add element at different time.
        }

        c.dumpKeys();

        TimeUnit.SECONDS.sleep(timeToIdleSeconds / 2 + 1);

        // Access some keys to keep them in the cache.
        for (int i = 0; i < 3; ++i) {
            c.get("k" + i);
        }

        c.dumpKeys();

        TimeUnit.SECONDS.sleep(timeToIdleSeconds / 2 + 1);
        c.dumpKeys();
    }

    private static void testLiveTime() throws InterruptedException {
        int timeToLiveSeconds = 10;
        Cache1 c = new Cache1("c2", 10, Integer.MAX_VALUE, timeToLiveSeconds);
        for (int i = 0; i < 10; ++i) {
            c.put("k" + i, new CachedObj("v" + i));
            TimeUnit.MICROSECONDS.sleep(10); // Add element at different time.
        }

        TimeUnit.SECONDS.sleep(timeToLiveSeconds / 2 + 1);
        c.dumpKeys();

        // Access some keys
        for (int i = 0; i < 3; ++i) {
            c.get("k" + i);
        }

        TimeUnit.SECONDS.sleep(timeToLiveSeconds / 2 + 1);
        c.dumpKeys();
    }

    private static void testRemoval() throws InterruptedException {
        Cache1 c = new Cache1("c3", 10, Integer.MAX_VALUE, 5);
        for (int i = 0; i < 10; ++i) {
            c.put("k" + i, new CachedObj("v" + i));
            TimeUnit.MICROSECONDS.sleep(10); // Add element at different time.
        }

        TimeUnit.SECONDS.sleep(5 + 1);

        // notify keys removed only if called remove function,
        // and it will NOT trigger the expired notification when called remove function on a key which has expired.
        c.remove("k0");
        c.remove("k-NotExists");

        CachedObj v0 = c.get("k0");
        System.out.println(v0.value);
        v0.value = "V0";
        System.out.println(c.get("k0").value);

        c.dumpKeys();
    }

    private static void testEvict() throws InterruptedException {
        Cache1 c = new Cache1("c3", 10, Integer.MAX_VALUE, 5);
        for (int i = 0; i < 10; ++i) {
            c.put("k" + i, new CachedObj("v" + i));
            TimeUnit.MICROSECONDS.sleep(1); // Add element at different time.
        }

        // notify keys evicted only if it is not expired.
        for (int i = 10; i < 15; ++i) {
            c.put("k" + i, new CachedObj("v" + i));
            TimeUnit.MICROSECONDS.sleep(1);
        }

        TimeUnit.SECONDS.sleep(5 + 1);

        // notify keys expired but no evicted notification !!!
        for (int i = 15; i < 20; ++i) {
            c.put("k" + i, new CachedObj("v" + i));
            TimeUnit.MICROSECONDS.sleep(1);
        }

        TimeUnit.SECONDS.sleep(1);
        c.dumpKeys();
    }
}

class CachedObj {
    public String value;

    public CachedObj(String value) {
        this.value = value;
    }
}

class Cache1 extends Cache<String, CachedObj> {
    public Cache1(String name,
                  int maxEntriesLocalHeap,
                  int timeToIdleSeconds,
                  int timeToLiveSeconds) {
        super(name, maxEntriesLocalHeap, timeToIdleSeconds, timeToLiveSeconds);
    }

    @Override
    public CachedObj load(String key) {
        System.out.println("load " + key);
        return new CachedObj("loaded " + key);
    }

    @Override
    public void save(String key, CachedObj value) {
        System.out.println("save " + key + ", value:" + value);
    }
}
