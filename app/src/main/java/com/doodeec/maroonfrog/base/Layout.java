package com.doodeec.maroonfrog.base;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Dusan Bartos
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Layout {
    int value() default -1;
}
