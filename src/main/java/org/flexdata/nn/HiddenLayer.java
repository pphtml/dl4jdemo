package org.flexdata.nn;

import org.flexdata.nn.activation.ActivationFunction;

import java.util.List;

public class HiddenLayer extends AbstractDenseLayer implements Layer {
    public HiddenLayer(int neuronCount, List<Double> initialParams, ActivationFunction activationFunction) {
        this.neuronCount = neuronCount;
        this.initialParams = initialParams;
        this.activationFunction = activationFunction;
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

            return new HiddenLayer(neuronCount, initialParams, instantiateActivationFunction(activationFunction));
        }

        public <T extends Number> Builder setInitialParams(List<T> initialParams) {
            super.setInitialParams(initialParams);
            return this;
        }

        public <T extends Number> Builder setInitialParamsList(T... list) {
            super.setInitialParamsList(list);
            return this;
        }

        public Builder withActivationFunction(Class<? extends ActivationFunction> activationFunction) {
            this.activationFunction = activationFunction;
            return this;
        }
    }
}
