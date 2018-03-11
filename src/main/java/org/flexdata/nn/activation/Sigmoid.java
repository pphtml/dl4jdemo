package org.flexdata.nn.activation;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

public class Sigmoid implements ActivationFunction {
    @Override
    public INDArray call(INDArray values) {
        return Transforms.sigmoid(values);
    }

    @Override
    public INDArray derivation(INDArray values) {
        return Transforms.sigmoidDerivative(values);
    }
//    @Override
//    public double call(double value) {
//        return 1 / (1 + Math.exp(-value));
//    }
//
//    @Override
//    public double derivation(double value) {
//        return value * (1 - value);
//    }
}
