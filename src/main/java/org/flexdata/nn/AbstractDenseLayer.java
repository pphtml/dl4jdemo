package org.flexdata.nn;

import com.google.common.primitives.Doubles;
import org.flexdata.nn.activation.ActivationFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class AbstractDenseLayer implements Layer {
    int neuronCount;
    List<Double> initialParams;
    double[] params;
    double[] input;
    double[] output;
    Layer previousLayer;
    int nIn;
    private Random random;
    private ActivationFunction defaultActivationFunction;
    private ActivationFunction activationFunction;
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
    public double[] evaluate(double[] values) {
        output = new double[this.neuronCount];
        input = new double[values.length + 1];
        System.arraycopy(values, 0, input, 0, values.length);
        input[input.length - 1] = 1; // bias

        int offset = 0;
        for(int i = 0; i < output.length; i++){
            for(int j = 0; j < input.length; j++){
                output[i] += params[offset + j] * input[j];
            }
            output[i] = activationFunction.call(output[i]); // ActivationFunction.sigmoid(output[i]);
            offset += input.length;
        }
        return output; //Arrays.copyOf(output, output.length);


//        List<Double> result = new ArrayList<>(neuronCount);
//        for (int index = 0; index < neuronCount; index++) {
//            double bias = this.params.get(nIn * neuronCount + index);
//            double value = bias;
//            for (int indexPrevious = 0; indexPrevious < nIn; indexPrevious++) {
//                int valueArrayIndex = index * nIn + indexPrevious;
//                value += values.get(indexPrevious) * this.params.get(valueArrayIndex);
//            }
//            result.add(value);
//        }
//        return result;
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
        final int parameterCount = nOut * (nIn + 1);

        this.params = new double[parameterCount];
        for (int index = 0; index < this.params.length; index++) {
            //this.params[index] = (random.nextFloat() - 0.5f) * 2f; // [-2, 2]
            this.params[index] = random.nextFloat();
        }
    }

    void buildNeurons() {
        this.activationFunction = this.defaultActivationFunction;

        nIn = this.previousLayer.getNeuronCount();
        if (this.initialParams != null) {
            // build from provided parameters
            final int nOut = this.getNeuronCount();
            final int parameterCount = nOut * (nIn + 1);
            if (this.initialParams.size() != parameterCount) {
                throw new IllegalStateException("Layers don't match");
            } else {
                this.params = Doubles.toArray(this.initialParams);
            }
        } else {
            initWeights();
        }
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setDefaultActivationFunction(ActivationFunction defaultActivationFunction) {
        this.defaultActivationFunction = defaultActivationFunction;
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
        int offset = 0;
        double[] nextError = new double[input.length];

        for(int i = 0; i < output.length; i++){
            double delta = error[i] * activationFunction.derivation(output[i]);
            for(int j = 0; j < input.length; j++){
                int weightIndex = offset + j;
                nextError[j] += this.params[weightIndex] * delta;
                double dw = input[j] * delta * learningRate;
                //weights[weightIndex] += dw * momentum;
                this.params[weightIndex] += dw;
//                weights[weightIndex] += /*dWeights[weightIndex]*/ dw * momentum + dw;
                //dWeights[weightIndex] = dw;
            }
            offset += input.length;
        }
        return nextError;
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
