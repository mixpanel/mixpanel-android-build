package com.mixpanel.android.build;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Tweak {
    String defaultString() default "";
    double defaultNumber() default 0.0;
}
