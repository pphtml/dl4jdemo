package org.flexdata.data;

public class Labels<S> {
    Labels(S[] array) {
    }

    public static <T> Labels<T> of(T... list) {
        Labels labels = new Labels<>(list);
        return labels;
    }
}
