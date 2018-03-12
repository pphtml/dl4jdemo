package org.flexdata.nn;

import org.flexdata.nn.activation.ActivationFunction;
import org.flexdata.nn.initialization.Distribution;
import org.flexdata.nn.initialization.UniformDistribution;
import org.flexdata.nn.initialization.WeightInit;

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
            outputLayer.activationFunction = instantiateActivationFunction(activationFunction);
            outputLayer.weightInitialization = weightInitialization;
            outputLayer.distribution = distribution;
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


        public Builder withActivationFunction(Class<? extends ActivationFunction> activationFunction) {
            this.activationFunction = activationFunction;
            return this;
        }

        public Builder withWeightInitialization(WeightInit weightInitialization) {
            this.weightInitialization = weightInitialization;
            return this;
        }

        public Builder withWeightInitialization(WeightInit weightInitialization, Distribution distribution) {
            this.weightInitialization = weightInitialization;
            this.distribution = distribution;
            return this;
        }
    }
}
