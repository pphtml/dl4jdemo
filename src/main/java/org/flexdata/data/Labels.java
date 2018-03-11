package org.flexdata.data;

import com.google.common.primitives.Doubles;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Labels<S extends Number> {
    private final S[] labels;

    Labels(S[] labels) {
        this.labels = labels;
    }

    public static <T extends Number> Labels<T> of(T... list) {
        Labels labels = new Labels<>(list);
        return labels;
    }

    public List<Double> toList() {
        return Arrays.asList(labels).stream()
                .map(value -> value.doubleValue())
                .collect(Collectors.toList());
    }

    public double[] toArrayOfDouble() {
        return Doubles.toArray(this.toList());
    }

    public float[] asFloats() {
        float[] result = new float[labels.length];
        for (int index = 0; index < result.length; index++) {
            result[index] = labels[index].floatValue();
        }
        return result;
    }
}
