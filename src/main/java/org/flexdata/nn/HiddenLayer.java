package org.flexdata.nn;

import java.util.List;

public class HiddenLayer extends AbstractDenseLayer implements Layer {
    public HiddenLayer(int neuronCount, List<Double> initialParams) {
        this.neuronCount = neuronCount;
        this.initialParams = initialParams;
    }

    public static class Builder extends AbstractDenseLayer.Builder {
        public static Builder create() {
            return new Builder();
        }

        public Builder withNeuronCount(int neuronCount) {
            this.neuronCount = neuronCount;
            return this;
        }

        public HiddenLayer build() {
            return new HiddenLayer(neuronCount, initialParams);
        }

        public <T extends Number> Builder setInitialParams(List<T> initialParams) {
            super.setInitialParams(initialParams);
            return this;
        }

        public <T extends Number> Builder setInitialParamsList(T... list) {
            super.setInitialParamsList(list);
            return this;
        }
    }
}
