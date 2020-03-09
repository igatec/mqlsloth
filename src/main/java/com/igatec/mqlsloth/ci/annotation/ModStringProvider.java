package com.igatec.mqlsloth.ci.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModStringProvider {

    String value();
    boolean useRemove() default false;
    int setPriority() default Integer.MIN_VALUE;
    int removePriority() default Integer.MIN_VALUE;

}
