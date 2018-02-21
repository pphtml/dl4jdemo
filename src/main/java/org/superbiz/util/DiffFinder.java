package org.superbiz.util;

import java.lang.reflect.Field;
import java.util.Optional;

public class DiffFinder {
    public static <T> Optional<T> computeDiff(T oldObject, T newObject) {
        try {
            boolean significantSet = false;
            T result = (T) oldObject.getClass().newInstance();

            for (Field field : oldObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                final DiffAttribute annotation = field.getAnnotation(DiffAttribute.class);
                Object oldValue = field.get(oldObject);
                Object newValue = field.get(newObject);
                if (oldValue != null && !oldValue.equals(newValue) && (annotation == null || (!annotation.ignoreForComparison() && !annotation.setOnlyIfChanging()))) {
                    field.set(result, oldValue);
                    significantSet = true;
                }
            }

            if (significantSet) {
                for (Field field : oldObject.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    final DiffAttribute annotation = field.getAnnotation(DiffAttribute.class);
                    if (annotation != null && annotation.setOnlyIfChanging()) {
                        Object oldValue = field.get(oldObject);
                        field.set(result, oldValue);
                    }
                }

            }

            return significantSet ? Optional.of(result) : Optional.empty();
        } catch (Exception e) {
            throw new DiffFinderException(e);
        }
    }
}
