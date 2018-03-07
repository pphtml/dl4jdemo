package org.flexdata.nn;

import org.flexdata.data.Features;
import org.flexdata.data.Labels;

import java.util.ArrayList;
import java.util.List;

public class NeuralNet {
    public <T> Labels evaluate(Features<T> features) {
        throw new UnsupportedOperationException();
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
            return net;
        }
    }
}
