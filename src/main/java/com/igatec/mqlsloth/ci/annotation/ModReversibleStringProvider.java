package com.igatec.mqlsloth.ci.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModReversibleStringProvider {

    String value();
    int setPriority() default Integer.MIN_VALUE;
    int removePriority() default Integer.MIN_VALUE;

}
