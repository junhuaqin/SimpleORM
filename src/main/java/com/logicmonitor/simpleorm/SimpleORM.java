package com.logicmonitor.simpleorm;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by rbtq on 7/7/16.
 */
public class SimpleORM<T> {
    private Class<T> bean = null;

    public SimpleORM() {
        this.bean = (Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass())
                              .getActualTypeArguments()[0];
    }

    public void createTable() {
        
    }

    public void dropTable() {

    }

    public void deleteAll() {

    }

    public void trunct() {

    }

    public List<T> loadAll() {
        return null;
    }

    public T load(Integer id) throws IllegalAccessException, InstantiationException {
        return bean.newInstance();
    }
}
