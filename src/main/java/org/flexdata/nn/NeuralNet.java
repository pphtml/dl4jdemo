package org.flexdata.nn;

import com.google.common.primitives.Doubles;
import org.flexdata.data.DataRow;
import org.flexdata.data.DataSet;
import org.flexdata.data.Features;
import org.flexdata.nn.activation.ActivationFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class NeuralNet {
    private InputLayer inputLayer;
    private List<AbstractDenseLayer> denseLayers;
    //private OutputLayer outputLayer;

//    public <T extends Number> Labels evaluate(Features<T> features) {
    public List<Double> evaluate(Features features) {
        //this.inputLayer.setParams(features.toParamsList());
        double[] values = features.toParamsArrayOfDouble();
        for (AbstractDenseLayer denseLayer : denseLayers) {
            values = denseLayer.evaluate(values);
        }
        return Doubles.asList(values);
    }

    //     public void train(float[] input, float[] targetOutput, float learningRate, float momentum){
    //        float[] calculatedOutput = run(input);
    //        float[] error = new float[calculatedOutput.length];
    //
    //        for(int i = 0; i < error.length; i++){
    //            error[i] = targetOutput[i] - calculatedOutput[i];
    //        }
    //        for(int i = layers.length-1; i >= 0; i--){
    //            error = layers[i].train(error, learningRate, momentum);
    //        }
    //    }

    public void fit() {
        DataSet dataSet = inputLayer.getDataSet();
        //DataRow record = dataSet.getHeadRecord();


        for (int iteration = 0; iteration < 10000; iteration++) {
            for (Object d : dataSet) {
                DataRow record = (DataRow) d;
                double[] calculatedOutput = Doubles.toArray(evaluate(record.getFeatures()));
                double[] error = new double[calculatedOutput.length];

                double[] targetOutput = record.getLabels().toArrayOfDouble();
                for (int index = 0; index < error.length; index++) {
                    error[index] = targetOutput[index] - calculatedOutput[index];
                }
                for (int index = denseLayers.size() - 1; index >= 0; index--) {
                    final AbstractDenseLayer denseLayer = denseLayers.get(index);
                    final float learningRate = 0.3f;
                    final float momentum = 0.6f;
                    error = denseLayer.train(error, learningRate, momentum);
                    //error = layers[index].train(error, learningRate, momentum);
                }
            }
        }
    }

    public static class Builder {
        private InputLayer inputLayer;
        private List<HiddenLayer> hiddenLayers = new ArrayList<>();
        private OutputLayer outputLayer;
        private Long seed;
        private ActivationFunction activationFunction;

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
            Random random = this.seed != null ? new Random(this.seed) : new Random();
            net.inputLayer = inputLayer;
//            for (HiddenLayer hiddenLayer : hiddenLayers) {
//            }
            net.denseLayers = hiddenLayers.stream().map(layer -> (AbstractDenseLayer)layer).collect(Collectors.toList());
//            outputLayer.setDefaultActivationFunction(activationFunction);
//            outputLayer.setPreviousLayer(previousLayer);
//            outputLayer.setRandom(random);
//            outputLayer.buildNeurons();
            net.denseLayers.add(outputLayer);

            Layer previousLayer = inputLayer;
            for (AbstractDenseLayer denseLayer : net.denseLayers) {
                denseLayer.setDefaultActivationFunction(activationFunction);
                denseLayer.setPreviousLayer(previousLayer);
                previousLayer = denseLayer;
                //hiddenLayer.setInputNeuronCount(previousLayer.getNeuronCount());
                denseLayer.setRandom(random);
                denseLayer.buildNeurons();
            }
            //net.outputLayer = outputLayer;

            return net;
        }

        public Builder withRandomSeed(long seed) {
            this.seed = seed;
            return this;
        }

        public Builder withActivationFunction(Class<? extends ActivationFunction> activationFunction) {
            try {
                this.activationFunction = activationFunction.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new NeuralNetBuildException(e);
            }
            return this;
        }
    }
}
