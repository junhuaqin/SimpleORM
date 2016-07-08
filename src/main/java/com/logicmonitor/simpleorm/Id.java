package com.logicmonitor.simpleorm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rbtq on 7/7/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Id {
    enum GenerationType {
        AUTO_INCREMENT,
        IDENTITY,
        NONE
    }

    GenerationType strategy() default GenerationType.AUTO_INCREMENT;
}
