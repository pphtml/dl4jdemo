package org.superbiz.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DiffAttribute {
    boolean ignoreForComparison() default false;
    boolean setOnlyIfChanging() default false;
}
