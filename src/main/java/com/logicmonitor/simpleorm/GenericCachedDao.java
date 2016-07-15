package com.logicmonitor.simpleorm;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by rbtq on 7/8/16.
 */
public class GenericCachedDao<T extends Cacheable<T> > extends GenericDao<T> {
    private final LoadingCache<Integer, T> cache;
    public GenericCachedDao(int cacheSize) {
        cache = CacheBuilder.newBuilder().maximumSize(cacheSize).build(new CacheLoader<Integer, T>() {
            @Override
            public T load(Integer integer) throws Exception {
                System.out.println("From db");
                return GenericCachedDao.super.load(integer);
            }
        });
    }

    private Integer getId(T obj){
        Field field = getIdField();
        try {
            return (Integer) field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public List<T> loadAll() throws InstantiationException, IllegalAccessException {
        List<T> all = super.loadAll();
        all.stream().forEach(n -> cache.put(getId(n), n.cloneObj()));
        return all;
    }

    @Override
    public T load(Integer id) throws IllegalAccessException, InstantiationException, ExecutionException {
        T obj = cache.get(id);
        return obj.cloneObj();
    }
}
