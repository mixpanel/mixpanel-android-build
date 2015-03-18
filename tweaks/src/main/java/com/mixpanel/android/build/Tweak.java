package com.mixpanel.android.build;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Tweak {
    // TODO - make name optional IF AND ONLY IF annotated method matches /^set([A-Z].*)/
    String name();
    String defaultString() default "";
    double defaultDouble() default 0.0;
    long defaultLong() default 0;
    boolean defaultBoolean() default false;
}
