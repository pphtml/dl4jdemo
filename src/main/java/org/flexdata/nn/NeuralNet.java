package org.flexdata.nn;

import org.flexdata.data.Features;
import org.flexdata.data.Labels;

import java.util.ArrayList;
import java.util.List;

public class NeuralNet {
    private InputLayer inputLayer;
    private List<HiddenLayer> hiddenLayers;
    private OutputLayer outputLayer;

//    public <T extends Number> Labels evaluate(Features<T> features) {
    public List<Double> evaluate(Features features) {
        //this.inputLayer.setParams(features.toParamsList());
        List<Double> values = features.toParamsList();
        for (HiddenLayer hiddenLayer : hiddenLayers) {
            values = hiddenLayer.evaluate(values);
        }
        return outputLayer.evaluate(values);
    }

    public static class Builder {
        private InputLayer inputLayer;
        private List<HiddenLayer> hiddenLayers = new ArrayList<>();
        private OutputLayer outputLayer;

        public static Builder create() {
            return new Builder();
        }

        public Builder withInputLayer(InputLayer inputLayer) {
            this.inputLayer = inputLayer;
            return this;
        }

        public Builder addHiddenLayer(HiddenLayer hiddenLayer) {
            hiddenLayers.add(hiddenLayer);
            return this;
        }

        public Builder withOutputLayer(OutputLayer outputLayer) {
            this.outputLayer = outputLayer;
            return this;
        }

        public NeuralNet build() {
            NeuralNet net = new NeuralNet();
            net.inputLayer = inputLayer;
            Layer previousLayer = inputLayer;
            for (HiddenLayer hiddenLayer : hiddenLayers) {
                hiddenLayer.setPreviousLayer(previousLayer);
                previousLayer = hiddenLayer;
                //hiddenLayer.setInputNeuronCount(previousLayer.getNeuronCount());
                hiddenLayer.buildNeurons();
            }
            net.hiddenLayers = hiddenLayers;
            outputLayer.setPreviousLayer(previousLayer);
            outputLayer.buildNeurons();
            net.outputLayer = outputLayer;

            return net;
        }
    }
}
