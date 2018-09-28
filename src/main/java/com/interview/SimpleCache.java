package com.interview;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SimpleCache<K, T> {

    private LinkedMap<K, CacheObject> cacheMap = new LinkedMap<>();

    class CacheObject {
        public long lastAccessed;
        public long createdTime;
        public T value;

        public CacheObject(T value, long lastAccessed, long createdTime) {
            this.lastAccessed = lastAccessed;
            this.createdTime = createdTime;
            this.value = value;
        }
    }

    /**
     * Adding object to a cache
     * if object added multiple time, the latest time will be considered as adding time
     * @param key
     * @param value
     */
    public long add(K key, T value) {
        synchronized (cacheMap) {
            long now = System.currentTimeMillis();
            cacheMap.put(key, new CacheObject(value,now, now));
            return now;
        }
    }

    /**
     * get value using a key
     * @param key
     * @return
     */
    public T retrieve(K key) {
        synchronized(cacheMap) {
            CacheObject co = cacheMap.get(key);

            if (co == null) {
                return null;
            }

            co.lastAccessed = System.currentTimeMillis();
            return co.value;
        }
    }

    /**
     * Get size of memcache
     * @return
     */
    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }

    /**
     * remove an cache object with a Key
     * @param key
     */
    public void remove(K key) {
        synchronized (cacheMap) {
            cacheMap.remove(key);
        }
    }

    /**
     * Delete all cache object which is older than the specified timestamp
     * @param timestamp
     * @return
     */
    public void evictOnTime(long timestamp) {
        List<K> deletionList = new ArrayList<>();

        synchronized (cacheMap) {
            Set keys = cacheMap.keySet();
            Iterator itr = keys.iterator();


            while (itr.hasNext()) {
                K key = (K) itr.next();
                CacheObject cacheObject = cacheMap.get(key);

                if (cacheObject != null && cacheObject.lastAccessed < timestamp) {
                    deletionList.add(key);
                }
            }
        }

        for (K key : deletionList) {
            synchronized (cacheMap) {
                cacheMap.remove(key);
            }
        }
    }

    /**
     * If memcache size is bigger than input size, delete the object with OLDEST creation time
     * @param size
     * @return
     */
    public void evictOnSize(int size) {
        synchronized (cacheMap) {
            while (!cacheMap.isEmpty()&& cacheMap.size() > size ) {
                cacheMap.remove(0); // 0 is always the oldest
            }
        }
    }
}
