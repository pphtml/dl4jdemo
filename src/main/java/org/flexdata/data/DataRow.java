package org.flexdata.data;

public class DataRow <T, U> {
    private final Features<T> features;
    private final Labels<U> labels;

    public  DataRow(Features<T> features, Labels<U> labels) {
        this.features = features;
        this.labels = labels;
    }

    public static <T, U> DataRow of(Features<T> features, Labels<U> labels) {
        return new DataRow(features, labels);
    }
}
