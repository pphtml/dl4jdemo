package org.flexdata.nn;

public class OutputLayer implements Layer {
    public static class Builder {
        public static Builder create() {
            return new Builder();
        }

        public OutputLayer build() {
            return new OutputLayer();
        }
    }
}
