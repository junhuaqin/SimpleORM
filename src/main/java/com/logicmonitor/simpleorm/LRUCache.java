package com.logicmonitor.simpleorm;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> implements CacheProvider<K, V>{
    private final int MAX_CACHE_SIZE;

    public LRUCache(int cacheSize) {
        super((int) Math.ceil(cacheSize / 0.75) + 1, 0.75f, true);
        MAX_CACHE_SIZE = cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > MAX_CACHE_SIZE;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.entrySet().stream().forEach(n->sb.append(String.format("%s:%s ", n.getKey(), n.getValue())));
        return sb.toString();
    }

    @Override
    public void pushCache(K key, V value) {
        put(key, value);
    }

    @Override
    public V pullCache(K key) {
        return get(key);
    }

    @Override
    public void expire(K key) {
        remove(key);
    }
}
