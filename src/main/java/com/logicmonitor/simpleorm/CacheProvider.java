package com.logicmonitor.simpleorm;

/**
 * Created by rbtq on 7/11/16.
 */
public interface CacheProvider<K, V> {
    void pushCache(K key, V value);
    V    pullCache(K key);
    void expire(K key);
}
