package org.flexdata.data;

import com.google.common.primitives.Floats;

public class DataRow <T extends Number, U extends Number> {
    private final Features<T> features;
    private final Labels<U> labels;

    public  DataRow(Features<T> features, Labels<U> labels) {
        this.features = features;
        this.labels = labels;
    }

    public Features<T> getFeatures() {
        return features;
    }

    public float[] getFeaturesAsFloats() {
        return features.asFloats();
    }

    public Labels<U> getLabels() {
        return labels;
    }

    public static <T extends Number, U extends Number> DataRow of(Features<T> features, Labels<U> labels) {
        return new DataRow(features, labels);
    }

    public float[] getLabelsAsFloats() {
        return labels.asFloats();
    }
}
