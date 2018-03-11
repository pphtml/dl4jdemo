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

    public double[] toParamsArrayOfDouble() {
        return Doubles.toArray(this.toParamsList());
    }

    public float[] asFloats() {
        float[] result = new float[features.length];
        for (int index = 0; index < result.length; index++) {
            result[index] = features[index].floatValue();
        }
        return result;
    }
}