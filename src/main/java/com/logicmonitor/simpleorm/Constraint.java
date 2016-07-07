package com.logicmonitor.simpleorm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rbtq on 7/7/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Constraint {
    boolean allowNull() default true;
    boolean isPrimary() default false;
    boolean hasDefault() default false;
    String defaultValue() default "";
}
