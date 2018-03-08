package org.flexdata.data;

import com.google.common.primitives.Doubles;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Features<S extends Number> {
    private final S[] features;

    Features(S[] features) {
        this.features = features;
    }

    public static <T extends Number> Features<T> from(T... list) {
        Features features = new Features<>(list);
        return features;
    }

    public int count() {
        return features.length;
    }

    public List<Double> toParamsList() {
        return Arrays.asList(features).stream()
                .map(value -> value.doubleValue())
                .collect(Collectors.toList());
    }
}
