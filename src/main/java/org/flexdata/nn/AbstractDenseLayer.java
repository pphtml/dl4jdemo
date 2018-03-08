package org.flexdata.nn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractDenseLayer implements Layer {
    int neuronCount;
    List<Double> initialParams;
    List<Double> params;
    Layer previousLayer;
    int nIn;
//    private int inputNeuronCount;

    @Override
    public void setPreviousLayer(Layer previousLayer) {
        this.previousLayer = previousLayer;
    }

    @Override
    public int getNeuronCount() {
        return neuronCount;
    }

    @Override
    public List<Double> evaluate(List<Double> values) {
        List<Double> result = new ArrayList<>(neuronCount);
        for (int index = 0; index < neuronCount; index++) {
            double bias = this.params.get(nIn * neuronCount + index);
            double value = bias;
            for (int indexPrevious = 0; indexPrevious < nIn; indexPrevious++) {
                int valueArrayIndex = index * nIn + indexPrevious;
                value += values.get(indexPrevious) * this.params.get(valueArrayIndex);
            }
            result.add(value);
        }
        return result;
    }

    void buildNeurons() {
        nIn = this.previousLayer.getNeuronCount();
        if (this.initialParams != null) {
            // build from provided parameters
            int nOut = this.getNeuronCount();
            int parameterCount = nOut * (nIn + 1);
            if (this.initialParams.size() != parameterCount) {
                throw new IllegalStateException("Layers don't match");
            } else {
                this.params = new ArrayList<>(this.initialParams);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static class Builder {
        int neuronCount;
        List<Double> initialParams;

        public <T extends Number> Builder setInitialParams(List<T> initialParams) {
            this.initialParams = convertToDoubles(initialParams);
            return this;
        }

        public <T extends Number> Builder setInitialParamsList(T... list) {
            this.setInitialParams(Arrays.asList(list));
            return this;
        }

        private <T extends Number> List<Double> convertToDoubles(List<T> initialParams) {
            List<Double> result = initialParams.stream()
                    .map(value -> value.doubleValue())
                    .collect(Collectors.toList());
            return result;
        }

    }
}
