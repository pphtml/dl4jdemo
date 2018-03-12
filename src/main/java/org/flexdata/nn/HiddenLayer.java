package org.flexdata.nn;

import org.flexdata.nn.activation.ActivationFunction;
import org.flexdata.nn.initialization.Distribution;
import org.flexdata.nn.initialization.WeightInit;

import java.util.List;

public class HiddenLayer extends AbstractDenseLayer implements Layer {
//    public HiddenLayer(int neuronCount, List<Double> initialParams, ActivationFunction activationFunction) {
//        this.neuronCount = neuronCount;
//        this.initialParams = initialParams;
//        this.activationFunction = activationFunction;
//    }

    public static class Builder extends AbstractDenseLayer.Builder {
        public static Builder create() {
            return new Builder();
        }

        public Builder withNeuronCount(int neuronCount) {
            this.neuronCount = neuronCount;
            return this;
        }

        public HiddenLayer build() {
            //return new HiddenLayer(neuronCount, initialParams, instantiateActivationFunction(activationFunction));
            HiddenLayer hiddenLayer = new HiddenLayer();
            hiddenLayer.neuronCount = neuronCount;
            hiddenLayer.initialParams = initialParams;
            hiddenLayer.activationFunction = instantiateActivationFunction(activationFunction);
            hiddenLayer.weightInitialization = weightInitialization;
            hiddenLayer.distribution = distribution;
            return hiddenLayer;
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
