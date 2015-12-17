package com.fujixerox.aus.asset.api.util.cache.impl;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.asset.api.util.cache.ICacheFactory;
import com.fujixerox.aus.asset.api.util.cache.impl.TimedCache;
import com.fujixerox.aus.asset.api.util.cache.impl.TimedCacheEntry;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class TimedCacheTest {

    private final class MapFactory implements
            ICacheFactory<String, String, Void, Throwable> {

        private final Map<String, String> map = new HashMap<String, String>();

        @Override
        public String getObject(Void session, String key) throws Exception {
            return map.get(key);
        }

    }

    private TimedCache<String, String, Void, Throwable> cache;

    private MapFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new MapFactory();
        factory.map.put("key1", "value1");
        cache = new TimedCache<String, String, Void, Throwable>(factory, 0);
    }

    @After
    public void tearDown() throws Exception {
        cache = null;
    }

    @Test
    public void testNullForNull() throws Throwable {
        Assert.assertNull(cache.getObject(null));
    }

    @Test
    public void nullForUnknownKey() throws Throwable {
        Assert.assertNull(cache.getObject("unknown"));
    }

    @Test
    public void cacheNotExpiredObject() throws Throwable {
        String key = "key1";
        String value = "value1";
        cache.putObject(key, value);
        TimedCacheEntry<String, String> entry = cache.getEntry(key);
        entry.setExpirationTime(Long.MAX_VALUE);
        Assert.assertFalse(cache.isExpired(entry));
        Assert.assertEquals(value, cache.getObject(key));
    }

    @Test
    public void fetchExpiredObject() throws Throwable {
        String key = "key1";
        cache.putObject(key, "value1");
        TimedCacheEntry<String, String> entry = cache.getEntry(key);
        entry.setExpirationTime(Long.MIN_VALUE);
        factory.map.put(key, "value2");
        Assert.assertTrue(cache.isExpired(entry));
        Assert.assertEquals(factory.map.get(key), cache.getObject(key));
    }

    @Test
    public void testEntryExpired() {
        String key = "key1";
        String value = "value1";
        cache.putObject(key, value);
        TimedCacheEntry<String, String> entry = cache.getEntry(key);
        long currentTimeMillis = System.currentTimeMillis();
        entry.setExpirationTime(currentTimeMillis - 1);
        Assert.assertTrue(cache.isExpired(entry, currentTimeMillis));
    }

    @Test
    public void testEntryNotExpired() {
        String key = "key1";
        String value = "value1";
        cache.putObject(key, value);
        TimedCacheEntry<String, String> entry = cache.getEntry(key);
        long currentTimeMillis = System.currentTimeMillis();
        entry.setExpirationTime(currentTimeMillis + 1);
        Assert.assertFalse(cache.isExpired(entry, currentTimeMillis));
    }

}
