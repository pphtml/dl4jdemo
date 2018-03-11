package org.flexdata.nn.activation;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

public class Softmax implements ActivationFunction {
    @Override
    public INDArray call(INDArray values) {
        return Transforms.softmax(values);
    }

    @Override
    public INDArray derivation(INDArray values) {
        throw new UnsupportedOperationException();
        //return Transforms.softsignDerivative(values);
    }
}
