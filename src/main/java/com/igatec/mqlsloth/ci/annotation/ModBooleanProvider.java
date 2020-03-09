package com.igatec.mqlsloth.ci.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModBooleanProvider {

    String value();
    boolean useTrueFalse() default false;
    String setFalse() default "";
    int setTruePriority() default Integer.MIN_VALUE;
    int setFalsePriority() default Integer.MIN_VALUE;

}
