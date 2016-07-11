package com.logicmonitor.simpleorm;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Created by rbtq on 7/8/16.
 */
public class GenericCachedDao<T extends Cacheable<T> > extends GenericDao<T> {
    private CacheProvider<Integer, ? super T> cacheProvider;
    public GenericCachedDao(CacheProvider<Integer, ? super T> cp) {
        cacheProvider = Objects.requireNonNull(cp);
    }

    private Integer getObjID(T obj) throws IllegalAccessException {
        Field field = getIdField();
        return (Integer) field.get(obj);
    }

    @Override
    public T load(Integer id) throws IllegalAccessException, InstantiationException {
        T obj = super.load(id);
        cacheProvider.pushCache(getObjID(obj), obj);

        return obj.cloneObj();
    }

    public void dumpCache() {
        System.out.println(cacheProvider.toString());
    }
}
