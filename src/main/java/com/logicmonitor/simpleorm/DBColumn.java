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
public @interface DBColumn {
	String name() default "";

	String dataType() default "";
	int length() default 0;

	Constraint constraint() default @Constraint();
}
