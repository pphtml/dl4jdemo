package org.flexdata.nn;

import java.util.List;

public class OutputLayer extends AbstractDenseLayer implements Layer {
    public static class Builder extends AbstractDenseLayer.Builder {
        public static Builder create() {
            return new Builder();
        }

        public OutputLayer build() {
            OutputLayer outputLayer = new OutputLayer();
            outputLayer.neuronCount = neuronCount;
            outputLayer.initialParams = initialParams;
            return outputLayer;
        }

        public Builder withNeuronCount(int neuronCount) {
            this.neuronCount = neuronCount;
            return this;
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
