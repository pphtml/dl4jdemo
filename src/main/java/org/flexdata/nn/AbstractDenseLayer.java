package org.flexdata.nn;

import org.flexdata.nn.activation.ActivationFunction;
import org.flexdata.nn.initialization.Distribution;
import org.flexdata.nn.initialization.WeightInit;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class AbstractDenseLayer implements Layer {
    int neuronCount;
    List<Double> initialParams;
    INDArray W;
    INDArray b;
//    double[] params;
//    double[] input;
//    double[] output;
    Layer previousLayer;
    int nIn;
    Random random;
    ActivationFunction activationFunction;
    WeightInit weightInitialization;
    Distribution distribution;
    INDArray output;
//    private int inputNeuronCount;

    @Override
    public void setPreviousLayer(Layer previousLayer) {
        this.previousLayer = previousLayer;
    }

    @Override
    public int getNeuronCount() {
        return neuronCount;
    }

    public INDArray evaluate(INDArray input) {
//            input = [[0.00,  0.00],
// [1.00,  0.00],
// [0.00,  1.00],
// [1.00,  1.00]]
//
//            W = [[0.09,  0.94,  0.46,  0.44],
// [0.29,  0.40,  0.35,  0.17]]
//
//            b = [0.00,  0.00,  0.00,  0.00]
//
//
//            ret = [[0.00,  0.00,  0.00,  0.00],
// [0.09,  0.94,  0.46,  0.44],
// [0.29,  0.40,  0.35,  0.17],
// [0.39,  1.33,  0.81,  0.61]]
//
//            Nd4j.getExecutioner().execAndReturn(new Sigmoid(in));

        INDArray result = input.mmul(W).addiRowVector(b);
        result = activationFunction.call(result);
        this.output = result;

//        output = new double[this.neuronCount];
//        input = new double[values.length + 1];
//        System.arraycopy(values, 0, input, 0, values.length);
//        input[input.length - 1] = 1; // bias
//
//        int offset = 0;
//        for(int i = 0; i < output.length; i++){
//            for(int j = 0; j < input.length; j++){
//                output[i] += params[offset + j] * input[j];
//            }
//            output[i] = activationFunction.call(output[i]); // ActivationFunction.sigmoid(output[i]);
//            offset += input.length;
//        }
//        return output;
        return result;
    }


    //     public float[] train(float[] error, float learningRate, float momentum) {
    //        int offset = 0;
    //        float[] nextError = new float[input.length];
    //
    //        for(int i = 0; i < output.length; i++){
    //            float delta = error[i] * ActivationFunction.dSigmoid(output[i]);
    //            for(int j = 0; j < input.length; j++){
    //                int weightIndex = offset + j;
    //                nextError[j] += weights[weightIndex] * delta;
    //                float dw = input[j] * delta * learningRate;
    //                //weights[weightIndex] += dw * momentum;
    //                weights[weightIndex] += dw;
    ////                weights[weightIndex] += /*dWeights[weightIndex]*/ dw * momentum + dw;
    //                //dWeights[weightIndex] = dw;
    //            }
    //            offset += input.length;
    //        }
    //        return nextError;
    //    }

    private void initWeights() {
        final int nOut = this.getNeuronCount();
        //final int parameterCount = nOut * (nIn + 1);
        //W = Nd4j.randn(new int[]{nIn, nOut});


        W = weightInitialization.execute(new int[]{nIn, nOut}, nIn, distribution, random);
        //W = Nd4j.randn('f', new int[]{nIn, nOut}).muli(FastMath.sqrt(2.0 / nIn));
        b = Nd4j.zeros(new int[]{nOut});
//
//        this.params = new double[parameterCount];
//        for (int index = 0; index < this.params.length; index++) {
//            //this.params[index] = (random.nextFloat() - 0.5f) * 2f; // [-2, 2]
//            this.params[index] = random.nextFloat();
//        }
    }

    void buildNeurons() {
        nIn = this.previousLayer.getNeuronCount();
        if (this.initialParams != null) {
//            // build from provided parameters
//            final int nOut = this.getNeuronCount();
//            final int parameterCount = nOut * (nIn + 1);
//            if (this.initialParams.size() != parameterCount) {
//                throw new IllegalStateException("Layers don't match");
//            } else {
//                this.params = Doubles.toArray(this.initialParams);
//            }
            throw new UnsupportedOperationException("is not implemented yet");
        } else {
            initWeights();
        }
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setDefaultActivationFunction(ActivationFunction defaultActivationFunction) {
        if (this.activationFunction == null) {
            this.activationFunction = defaultActivationFunction;
        }
    }

    //     public float[] train(float[] error, float learningRate, float momentum) {
    //        int offset = 0;
    //        float[] nextError = new float[input.length];
    //
    //        for(int i = 0; i < output.length; i++){
    //            float delta = error[i] * ActivationFunction.dSigmoid(output[i]);
    //            for(int j = 0; j < input.length; j++){
    //                int weightIndex = offset + j;
    //                nextError[j] += weights[weightIndex] * delta;
    //                float dw = input[j] * delta * learningRate;
    //                //weights[weightIndex] += dw * momentum;
    //                weights[weightIndex] += dw;
    ////                weights[weightIndex] += /*dWeights[weightIndex]*/ dw * momentum + dw;
    //                //dWeights[weightIndex] = dw;
    //            }
    //            offset += input.length;
    //        }
    //        return nextError;
    //    }
    public double[] train(double[] error, float learningRate, float momentum) {
//        int offset = 0;
//        double[] nextError = new double[input.length];
//
//        for(int i = 0; i < output.length; i++){
//            double delta = error[i] * activationFunction.derivation(output[i]);
//            for(int j = 0; j < input.length; j++){
//                int weightIndex = offset + j;
//                nextError[j] += this.params[weightIndex] * delta;
//                double dw = input[j] * delta * learningRate;
//                //weights[weightIndex] += dw * momentum;
//                this.params[weightIndex] += dw;
////                weights[weightIndex] += /*dWeights[weightIndex]*/ dw * momentum + dw;
//                //dWeights[weightIndex] = dw;
//            }
//            offset += input.length;
//        }
//        return nextError;
        return null;
    }

    public static class Builder {
        int neuronCount;
        List<Double> initialParams;
        Class<? extends ActivationFunction> activationFunction;
        WeightInit weightInitialization;
        Distribution distribution;

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

    static ActivationFunction instantiateActivationFunction(Class<? extends ActivationFunction> activationFunction) {
        if (activationFunction == null) {
            return null;
        }

        try {
            return activationFunction.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new NeuralNetBuildException(e);
        }

    }

    @Override
    public INDArray getOutput() {
        return output;
    }

    public INDArray getW() {
        return W;
    }

    public void setW(INDArray w) {
        W = w;
    }

    public INDArray getB() {
        return b;
    }

    public void setB(INDArray b) {
        this.b = b;
    }
}
