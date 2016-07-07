package com.logicmonitor.simpleorm;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Administrator on 2016/7/7.
 */
public abstract  class SimpleORMBase<T> {
    protected Class<T> bean = null;

    @SuppressWarnings("unchecked")
    public SimpleORMBase() {
        Type type = getClass().getGenericSuperclass();
        Type trueType = ((ParameterizedType) type).getActualTypeArguments()[0];
        this.bean = (Class<T>) trueType;
    }
}
