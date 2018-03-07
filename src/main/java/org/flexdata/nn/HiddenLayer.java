package org.flexdata.nn;

import java.util.List;

public class HiddenLayer implements Layer {
    private final int neuronCount;
    private final List<Double> params;

    public HiddenLayer(int neuronCount, List<Double> params) {
        this.neuronCount = neuronCount;
        this.params = params;
    }

    public static class Builder {
        private int neuronCount;
        private List<Double> params;

        public static Builder create() {
            return new Builder();
        }

        public Builder neuronCount(int neuronCount) {
            this.neuronCount = neuronCount;
            return this;
        }

        public Builder setParams(List<Double> params) {
            this.params = params;
            return this;
        }

        public HiddenLayer build() {
            return new HiddenLayer(neuronCount, params);
        }
    }
}
