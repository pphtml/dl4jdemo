package org.flexdata.data;

public class Features<S> {
    //private T[] list;
    Features(S[] array) {
        //a = Objects.requireNonNull(array);
    }

    public static <T> Features<T> from(T... list) {
        Features features = new Features<>(list);
        return features;
    }
}
