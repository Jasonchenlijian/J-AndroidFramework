package com.clj.jaf.activity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JInjectView {
    int id() default -1;

    String click() default "";

    String longClick() default "";

    String focuschange() default "";

    String key() default "";

    String Touch() default "";
}
