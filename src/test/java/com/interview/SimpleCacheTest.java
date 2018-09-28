package com.interview;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class to test simple mem cache
 */
public class SimpleCacheTest {

    @Test
    public void addRetrieveTest() {
        SimpleCache<String, String> cache = new SimpleCache<>();

        cache.add("a", "this is first string");
        cache.add("b", "this is second string");
        cache.add("c", "this is third string");
        cache.add("d", "this is fourth string");

        Assert.assertEquals(4, cache.size());
        Assert.assertEquals("this is first string", cache.retrieve("a"));
        Assert.assertEquals("this is second string", cache.retrieve("b"));
        Assert.assertEquals("this is third string", cache.retrieve("c"));
        Assert.assertEquals("this is fourth string", cache.retrieve("d"));
    }

    @Test
    public void sizeTest() {
        SimpleCache<String, String> cache = new SimpleCache<>();

        cache.add("a", "this is first string");
        cache.add("b", "this is second string");
        cache.add("c", "this is third string");
        cache.add("d", "this is fourth string");

        Assert.assertEquals(4, cache.size());

        cache.add("a", "this is fifth string");
        Assert.assertEquals(4, cache.size()); //samekey
    }

    @Test
    public void removeTest() {
        SimpleCache<String, String> cache = new SimpleCache<>();

        cache.add("a", "this is first string");
        cache.add("b", "this is second string");
        cache.add("c", "this is third string");
        cache.add("d", "this is fourth string");

        Assert.assertEquals(4, cache.size());
        cache.remove("b");
        cache.remove("c");
        cache.remove("d");

        Assert.assertEquals(1, cache.size());
        Assert.assertEquals("this is first string", cache.retrieve("a"));
    }

    /**
     * Using time sleep to simulate the time diff, as interfact does not support input of timestamp
     */
    @Test
    public void evictOnTimeTest() throws InterruptedException {
        SimpleCache<String, String> cache = new SimpleCache<>();

        cache.add("a", "this is first string");
        Thread.sleep(1000);

        cache.add("b", "this is second string");
        Thread.sleep(1000);

        cache.add("c", "this is third string");
        Thread.sleep(1000);
        long cutOffTimestamp = System.currentTimeMillis();

        cache.add("d", "this is fourth string");
        Thread.sleep(1000);

        cache.retrieve("a");


        cache.evictOnTime(cutOffTimestamp);
        Assert.assertEquals(2, cache.size());

        Assert.assertEquals("this is fourth string", cache.retrieve("d"));
        Assert.assertEquals("this is first string", cache.retrieve("a"));
    }

    @Test
    public void evictOnSizeTest() throws InterruptedException {
        SimpleCache<String, String> cache = new SimpleCache<>();

        cache.add("a", "this is first string");
        Thread.sleep(1000);

        cache.add("b", "this is second string");
        Thread.sleep(1000);

        cache.add("c", "this is third string");
        Thread.sleep(1000);

        cache.add("d", "this is fourth string");
        Thread.sleep(1000);

        // based on CREATION time
        cache.evictOnSize(3);

        Assert.assertEquals(3, cache.size());
        Assert.assertEquals("this is second string", cache.retrieve("b"));
        Assert.assertEquals("this is third string", cache.retrieve("c"));
        Assert.assertEquals("this is fourth string", cache.retrieve("d"));
    }
}