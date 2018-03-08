package org.flexdata.data;

public class DataRow <T extends Number, U> {
    private final Features<T> features;
    private final Labels<U> labels;

    public  DataRow(Features<T> features, Labels<U> labels) {
        this.features = features;
        this.labels = labels;
    }

    public Features<T> getFeatures() {
        return features;
    }

    public Labels<U> getLabels() {
        return labels;
    }

    public static <T extends Number, U> DataRow of(Features<T> features, Labels<U> labels) {
        return new DataRow(features, labels);
    }
}
