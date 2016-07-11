package com.logicmonitor.simpleorm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by rbtq on 7/11/16.
 */
public class ReflectUtils {
    public static Field[] getAllField(Class<?> clazz) {
        ArrayList<Field> fieldList = new ArrayList<>();
        Field[] dFields = clazz.getDeclaredFields();
        if (null != dFields && dFields.length > 0) {
            fieldList.addAll(Arrays.asList(dFields));
        }

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class) {
            Field[] superFields = getAllField(superClass);
            if (null != superFields && superFields.length > 0) {
                for(Field field:superFields){
                    if(!isContain(fieldList, field)){
                        fieldList.add(field);
                    }
                }
            }
        }
        Field[] result=new Field[fieldList.size()];
        fieldList.toArray(result);
        return result;
    }

    public static boolean isContain(ArrayList<Field> fieldList,Field field){
        for(Field temp:fieldList){
            if(temp.getName().equals(field.getName())){
                return true;
            }
        }
        return false;
    }
}
