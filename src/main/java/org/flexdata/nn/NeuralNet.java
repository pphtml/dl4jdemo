package org.flexdata.nn;

import org.flexdata.data.DataSet;
import org.flexdata.nn.activation.ActivationFunction;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class NeuralNet {
    private InputLayer inputLayer;
    private List<AbstractDenseLayer> denseLayers;
    //private OutputLayer outputLayer;

//    public <T extends Number> Labels evaluate(Features<T> features) {
    public INDArray evaluate(INDArray features) {
        //this.inputLayer.setParams(features.toParamsList());
        INDArray values = features;
        for (AbstractDenseLayer denseLayer : denseLayers) {
            values = denseLayer.evaluate(values);
        }
        return values;
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
        INDArray features = dataSet.getFeatures();
        INDArray labels = dataSet.getLabels();

        for (int iteration = 0; iteration < 1; iteration++) {
            INDArray result = evaluate(features);
            INDArray outputGradients = result.subi(labels);

//            //Nd4j.gemm(input, delta, weightGradView, true, false, 1.0, 0.0); //Equivalent to:  weightGradView.assign(input.transpose().mmul(delta));
//            delta.sum(biasGradView, 0); //biasGradView is initialized/zeroed first in sum op
//            weightGradView = [[0.02,  -0.02],
// [0.19,  -0.19],
// [0.36,  -0.36],
// [0.42,  -0.42]]
//            biasGradView = [0.24,  -0.24]

            AbstractDenseLayer previousLayer = denseLayers.get(denseLayers.size() - 2);
            AbstractDenseLayer currentLayer = denseLayers.get(denseLayers.size() - 1);

            INDArray input = previousLayer.getOutput();
            INDArray weightGradView = input.transpose().mmul(outputGradients);
            INDArray biasGradView = outputGradients.sum(new int[]{0});

            INDArray epsilonNext = currentLayer.getW().mmul(outputGradients.transpose()).transpose();


//            INDArray epsilonNext = params.get(DefaultParamInitializer.WEIGHT_KEY).mmul(delta.transpose()).transpose();
//            epsilonNext = [[-0.09,  -0.36,  0.30,  -0.01],
// [0.10,  0.40,  -0.33,  0.01],
// [0.09,  0.38,  -0.31,  0.01],
// [-0.06,  -0.24,  0.20,  -0.00]]


            System.out.println(result);


//            for (Object d : dataSet) {
//                DataRow record = (DataRow) d;
//                double[] calculatedOutput = Doubles.toArray(evaluate(record.getFeatures()));
//                double[] error = new double[calculatedOutput.length];
//
//                double[] targetOutput = record.getLabels().toArrayOfDouble();
//                for (int index = 0; index < error.length; index++) {
//                    error[index] = targetOutput[index] - calculatedOutput[index];
//                }
//                for (int index = denseLayers.size() - 1; index >= 0; index--) {
//                    final AbstractDenseLayer denseLayer = denseLayers.get(index);
//                    final float learningRate = 0.3f;
//                    final float momentum = 0.6f;
//                    error = denseLayer.train(error, learningRate, momentum);
//                }
//            }
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
            if (seed != null) {
                Nd4j.getRandom().setSeed(seed);
            }
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
